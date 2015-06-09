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
    public class MUserMoodController : ApiController
    {
        private readonly IUserMoodService _userMoodService;

        public MUserMoodController(IUserMoodService userMoodService)
        {
            _userMoodService = userMoodService;
        }

        // GET api/MUserMood
        public IList<UserMood> GetActivities()
        {
            return _userMoodService.GetUserMoods();
        }

        // GET api/MUserMood/5
        [ResponseType(typeof(UserMood))]
        public async Task<IHttpActionResult> GetUserMood(long id)
        {
            UserMood userMood = await _userMoodService.FindUserMood(id);
            if (userMood == null)
            {
                return NotFound();
            }

            return Ok(userMood);
        }

        [HttpGet]
        [Route("api/muserMood/user/{userId}")]
        public IList<UserMood> GetUserUserMood(string userId)
        {
            return _userMoodService.GetMoodsByUser(userId);
        }

        // PUT api/MUserMood/5
        public async Task<IHttpActionResult> PutUserMood(long id, UserMood userMood)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != userMood.Id)
            {
                return BadRequest();
            }

            try
            {
                await _userMoodService.UpdateUserMood(userMood);
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!UserMoodExists(id))
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

        // POST api/MUserMood
        [ResponseType(typeof(UserMood))]
        public async Task<IHttpActionResult> PostUserMood(UserMood userMood)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            userMood.Id = await _userMoodService.CreateUserMood(userMood);

            return CreatedAtRoute("DefaultApi", new { id = userMood.Id }, userMood);
        }

        // DELETE api/MUserMood/5
        [ResponseType(typeof(UserMood))]
        public async Task<IHttpActionResult> DeleteUserMood(long id)
        {
            UserMood userMood = await _userMoodService.FindUserMood(id);
            if (userMood == null)
            {
                return NotFound();
            }

            await _userMoodService.DeleteUserMood(id);

            return Ok(userMood);
        }

        private bool UserMoodExists(long id)
        {
            return _userMoodService.GetUserMoods().Count(e => e.Id == id) > 0;
        }
    }
}
