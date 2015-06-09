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
    public class MBadgeController : ApiController
    {
        private readonly IBadgeService _badgeService;

        public MBadgeController(IBadgeService badgeService)
        {
            _badgeService = badgeService;
        }

        // GET api/Badge
        public IList<Badge> GetBadges()
        {
            return _badgeService.GetBadges();
        }

        // GET api/Badge/5
        [ResponseType(typeof(Badge))]
        public async Task<IHttpActionResult> GetBadge(long id)
        {
            Badge badge = await _badgeService.FindBadge(id);
            if (badge == null)
            {
                return NotFound();
            }

            return Ok(badge);
        }

        [HttpGet]
        [ActionName("Search")]
        public IList<Badge> SearchByName(string name)
        {
            return _badgeService.SearchForBadges(name);
        }

        // PUT api/Badge/5
        public async Task<IHttpActionResult> PutBadge(long id, Badge badge)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != badge.Id)
            {
                return BadRequest();
            }

            try
            {
                await _badgeService.UpdateBadge(badge);
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!BadgeExists(id))
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

        // POST api/Badge
        [ResponseType(typeof(Badge))]
        public async Task<IHttpActionResult> PostBadge(Badge badge)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            badge.Id = await _badgeService.CreateBadge(badge);

            return CreatedAtRoute("DefaultApi", new { id = badge.Id }, badge);
        }

        // DELETE api/Badge/5
        [ResponseType(typeof(Badge))]
        public async Task<IHttpActionResult> DeleteBadge(long id)
        {
            Badge badge = await _badgeService.FindBadge(id);
            if (badge == null)
            {
                return NotFound();
            }

            await _badgeService.DeleteBadge(id);

            return Ok(badge);
        }

        private bool BadgeExists(long id)
        {
            return _badgeService.GetBadges().Count(e => e.Id == id) > 0;
        }
    }
}