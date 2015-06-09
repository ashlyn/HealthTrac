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
    public class MUserBadgeController : ApiController
    {
        private readonly IUserBadgeService _userBadgeService;

        public MUserBadgeController(IUserBadgeService userBadgeService)
        {
            _userBadgeService = userBadgeService;
        }

        // GET api/UserBadge
        public IList<UserBadge> GetActivities()
        {
            return _userBadgeService.GetUserBadges();
        }

        // GET api/UserBadge/5
        [ResponseType(typeof(UserBadge))]
        public async Task<IHttpActionResult> GetUserBadge(long id)
        {
            UserBadge userBadge = await _userBadgeService.FindUserBadge(id);
            if (userBadge == null)
            {
                return NotFound();
            }

            return Ok(userBadge);
        }

        [HttpGet]
        [Route("api/mUserBadge/user/{userId}")]
        public IList<Badge> GetUserUserBadge(string userId)
        {
            return _userBadgeService.GetUserBadges(userId);
        }

        // PUT api/UserBadge/5
        public IHttpActionResult PutUserBadge(long id, UserBadge userBadge)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != userBadge.Id)
            {
                return BadRequest();
            }

            try
            {
                _userBadgeService.UpdateUserBadge(userBadge);
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!UserBadgeExists(id))
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

        // POST api/UserBadge
        [ResponseType(typeof(UserBadge))]
        public async Task<IHttpActionResult> PostUserBadge(UserBadge userBadge)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            userBadge.Id = await _userBadgeService.CreateUserBadge(userBadge);

            return CreatedAtRoute("DefaultApi", new { id = userBadge.Id }, userBadge);
        }

        // DELETE api/UserBadge/5
        [ResponseType(typeof(UserBadge))]
        public async Task<IHttpActionResult> DeleteUserBadge(long id)
        {
            UserBadge userBadge = await _userBadgeService.FindUserBadge(id);
            if (userBadge == null)
            {
                return NotFound();
            }

            await _userBadgeService.DeleteUserBadge(id);

            return Ok(userBadge);
        }

        private bool UserBadgeExists(long id)
        {
            return _userBadgeService.GetUserBadges().Count(e => e.Id == id) > 0;
        }
    }
}