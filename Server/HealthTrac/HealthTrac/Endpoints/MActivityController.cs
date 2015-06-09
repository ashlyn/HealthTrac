using System;
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
    public class MActivityController : ApiController
    {
        private readonly IActivityService _activityService;

        public MActivityController(IActivityService activityService)
        {
            _activityService = activityService;
        }

        // GET api/Activity
        public IList<Activity> GetActivities()
        {
            return _activityService.GetActivities();
        }

        // GET api/Activity/5
        [ResponseType(typeof(Activity))]
        public async Task<IHttpActionResult> GetActivity(long id)
        {
            var activity = await _activityService.FindActivity(id);
            if (activity == null)
            {
                return NotFound();
            }

            return Ok(activity);
        }

        [HttpGet]
        [Route("api/mactivity/user/{userId}")]
        public IList<Activity> GetUserActivity(string userId)
        {
            return _activityService.GetUserActivities(userId);
        }

        [HttpGet]
        [Route("api/mactivity/{userId}/{date}")]
        public IList<Activity> GetDailyUserActivities(string userId, DateTime date)
        {
            return _activityService.GetUserActivitiesByDay(userId, date);
        } 

        // PUT api/Activity/5
        public async Task<IHttpActionResult> PutActivity(long id, Activity activity)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != activity.Id)
            {
                return BadRequest();
            }

            try
            {
                await _activityService.UpdateActivity(activity);
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ActivityExists(id))
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

        // POST api/Activity
        [ResponseType(typeof(Activity))]
        public async Task<IHttpActionResult> PostActivity(Activity activity)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            activity.Id = await _activityService.CreateActivity(activity);

            return CreatedAtRoute("DefaultApi", new { id = activity.Id }, activity);
        }

        [Route("api/mactivity/classify")]
        public ActivityType ClassifyActivity(Activity activity)
        {
            var type = _activityService.ClassifyActivity(activity);
            return type;
        }

        [HttpGet]
        [Route("api/mactivity/buildforest")]
        public IHttpActionResult BuildForest()
        {
            try
            {
                _activityService.BuildForest();
            }
            catch
            {
                return InternalServerError();
            }
            return Ok("reports generated");
        }

        // DELETE api/Activity/5
        [ResponseType(typeof(Activity))]
        public async Task<IHttpActionResult> DeleteActivity(long id)
        {
            Activity activity = await _activityService.FindActivity(id);
            if (activity == null)
            {
                return NotFound();
            }

            await _activityService.DeleteActivity(id);

            return Ok(activity);
        }

        private bool ActivityExists(long id)
        {
            return _activityService.GetActivities().Count(e => e.Id == id) > 0;
        }
    }
}