using System.Collections.Generic;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Net;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Description;
using HealthTrac.Application.Services;
using HealthTrac.Models;

namespace HealthTrac.Controllers
{
    public class MMembershipController : ApiController
    {
        private readonly IMembershipService _membershipService;

        public MMembershipController(IMembershipService membershipService)
        {
            _membershipService = membershipService;
        }

        // GET api/MembershipApi
        public IList<Membership> GetMemberships()
        {
            return _membershipService.GetMemberships();
        }

        // GET api/MembershipApi/5
        [ResponseType(typeof(Membership))]
        //[Route("api/mmembership/{id}")]
        public async Task<IHttpActionResult> GetMembership(long id)
        {
            Membership membership = await _membershipService.FindMembership(id);
            if (membership == null)
            {
                return NotFound();
            }

            return Ok(membership);
        }

        // PUT api/MembershipApi/5
        public async Task<IHttpActionResult> PutMembership(long id, Membership membership)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != membership.Id)
            {
                return BadRequest();
            }

            try
            {
                await _membershipService.UpdateMembership(membership);
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!MembershipExists(id))
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

        // POST api/MembershipApi
        [ResponseType(typeof(Membership))]
        public async Task<IHttpActionResult> PostMembership(Membership membership)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            membership.Id = await _membershipService.CreateMembership(membership);

            return CreatedAtRoute("DefaultApi", new { id = membership.Id }, membership);
        }

        // DELETE api/MembershipApi/5
        [ResponseType(typeof(Membership))]
        public async Task<IHttpActionResult> DeleteMembership(long id)
        {
            Membership membership = await _membershipService.FindMembership(id);
            if (membership == null)
            {
                return NotFound();
            }

            await _membershipService.DeleteMembership(id);

            return Ok(membership);
        }

        private bool MembershipExists(long id)
        {
            return _membershipService.GetMemberships().Count(e => e.Id == id) > 0;
        }
    }
}