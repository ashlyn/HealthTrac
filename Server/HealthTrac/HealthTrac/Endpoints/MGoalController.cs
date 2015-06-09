using System.Collections.Generic;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Net;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Description;
using HealthTrac.Application.Services;
using HealthTrac.Models;

namespace HealthTrac.Endpoints
{
    public class MGoalController : ApiController
    {
        private readonly IGoalService _goalService;

        public MGoalController(IGoalService goalService)
        {
            _goalService = goalService;
        }

        // GET api/MGoal
        public IList<Goal> GetActivities()
        {
            return _goalService.GetGoals();
        }

        // GET api/MGoal/5
        [ResponseType(typeof(Goal))]
        public async Task<IHttpActionResult> GetGoal(long id)
        {
            Goal goal = await _goalService.FindGoal(id);
            if (goal == null)
            {
                return NotFound();
            }

            return Ok(goal);
        }

        [HttpGet]
        [Route("api/mgoal/user/{userId}")]
        public IList<Goal> GetUserGoal(string userId)
        {
            return _goalService.GetUserGoals(userId);
        }

        // PUT api/MGoal/5
        public async Task<IHttpActionResult> PutGoal(long id, Goal goal)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != goal.Id)
            {
                return BadRequest();
            }

            try
            {
                await _goalService.UpdateGoal(goal);
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!GoalExists(id))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return StatusCode(HttpStatusCode.NoContent);
        }

        // POST api/MGoal
        [ResponseType(typeof(Goal))]
        public async Task<IHttpActionResult> PostGoal(Goal goal)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            goal.Id = await _goalService.CreateGoal(goal);

            return CreatedAtRoute("DefaultApi", new { id = goal.Id }, goal);
        }

        // DELETE api/MGoal/5
        [ResponseType(typeof(Goal))]
        public async Task<IHttpActionResult> DeleteGoal(long id)
        {
            Goal goal = await _goalService.FindGoal(id);
            if (goal == null)
            {
                return NotFound();
            }

            await _goalService.DeleteGoal(id);

            return Ok(goal);
        }

        private bool GoalExists(long id)
        {
            return _goalService.GetGoals().Count(e => e.Id == id) > 0;
        }
    }
}
