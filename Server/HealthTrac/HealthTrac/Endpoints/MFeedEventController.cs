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
    public class MFeedEventController : ApiController
    {
        private readonly IFeedEventService _feedEventService;

        public MFeedEventController(IFeedEventService feedEventService)
        {
            _feedEventService = feedEventService;
        }

        // GET: api/MFeedEvent
        public IList<FeedEvent> GetFeedEvents()
        {
            return _feedEventService.GetFeedEvents();
        }

        // GET api/MFeedEvent/5
        [ResponseType(typeof(FeedEvent))]
        public async Task<IHttpActionResult> GetFeedEvent(long id)
        {
            FeedEvent feedEvent = await _feedEventService.FindFeedEvent(id);
            if (feedEvent == null)
            {
                return NotFound();
            }

            return Ok(feedEvent);
        }

        [HttpGet]
        [Route("api/mfeedevent/user/{userId}")]
        public IList<FeedEvent> GetUserFeedEvents(string userId)
        {
            return _feedEventService.GetFeedEventsByUser(userId);
        }

        [HttpGet]
        [Route("api/mfeedevent/group/{groupId}")]
        public Tuple<IList<FeedEvent>, long> GetGroupFeedEvents(long groupId)
        {
            var events = new Tuple<IList<FeedEvent>, long>(_feedEventService.GetFeedEventsByGroup(groupId), groupId);
            return events;
        }

        // PUT api/mfeedevent/5
        public IHttpActionResult PutFeedEvent(long id, FeedEvent feedEvent)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != feedEvent.Id)
            {
                return BadRequest();
            }

            try
            {
                _feedEventService.UpdateFeedEvent(feedEvent);
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!FeedEventExists(id))
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

        // DELETE api/mfeedevent/5
        [ResponseType(typeof(FeedEvent))]
        public async Task<IHttpActionResult> DeleteFeedEvent(long id)
        {
            FeedEvent feedEvent = await _feedEventService.FindFeedEvent(id);
            if (feedEvent == null)
            {
                return NotFound();
            }

            await _feedEventService.DeleteFeedEvent(id);

            return Ok(feedEvent);
        }

        private bool FeedEventExists(long id)
        {
            return _feedEventService.GetFeedEvents().Count(e => e.Id == id) > 0;
        }
    }
}
