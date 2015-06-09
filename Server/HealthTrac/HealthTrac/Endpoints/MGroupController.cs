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

namespace HealthTrac.Controllers
{
    public class MGroupController : ApiController
    {
        private readonly IGroupService _groupService;

        public MGroupController(IGroupService groupService)
        {
            _groupService = groupService;
        }

        // GET api/GroupApi
        public IList<Group> GetGroups()
        {
            return _groupService.GetGroups();
        }

        // GET api/GroupApi/5
        [HttpGet]
        [ResponseType(typeof(Group))]
        public async Task<IHttpActionResult> GetGroup(long id)
        {
            Group group = await _groupService.FindGroup(id);
            if (group == null)
            {
                return NotFound();
            }

            return Ok(group);
        }

        [HttpGet]
        [ActionName("Search")]
        public IList<Group> SearchByName(string name)
        {
            return _groupService.Search(name);
        }

        [HttpGet]
        [Route("api/mgroup/user/{userId}")]
        public IList<Group> UsersByGroup(string userId)
        {
            var groups = _groupService.GetUserGroups(userId);
            return groups;
        }

        [HttpGet]
        [Route("api/mgroup/invites/{userId}")]
        public IList<Group> UserInvitedGroups(string userId)
        {
            var groups = _groupService.GetUserInvitedGroups(userId);
            return groups;
        }
            
        [HttpGet]
        [Route("api/mgroup/leaderboard/{id}/{category}/{n}")]
        public IList<Tuple<User, double>> GetLeaderBoard(long id, string category, int n)
        {
            var leaders = _groupService.GetLeaderBoard(id, category, n);
            return leaders;
        }

        [HttpGet]
        [Route("api/mgroup/leaderboard/{id}/{category}")]
        public IList<Tuple<User, double>> GetLeaderBoard(long id, string category)
        {
            var leaders = _groupService.GetLeaderBoard(id, category);
            return leaders;
        }

        // PUT api/GroupApi/5
        public async Task<IHttpActionResult> PutGroup(long id, Group group)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != group.Id)
            {
                return BadRequest();
            }

            try
            {
                await _groupService.UpdateGroup(group);

            }
            catch (DbUpdateConcurrencyException)
            {
                if (!GroupExists(id))
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

        // POST api/GroupApi
        [ResponseType(typeof(Group))]
        public async Task<IHttpActionResult> PostGroup(Group group)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            await _groupService.CreateGroup(group);

            return CreatedAtRoute("DefaultApi", new { id = group.Id }, group);
        }

        // DELETE api/GroupApi/5
        [ResponseType(typeof(Group))]
        public async Task<IHttpActionResult> DeleteGroup(long id)
        {
            Group group = await _groupService.FindGroup(id);
            if (group == null)
            {
                return NotFound();
            }

           // db.Groups.Remove(group);
            //await db.SaveChangesAsync();
            await _groupService.DeleteGroup(id);

            return Ok(group);
        }

        private bool GroupExists(long id)
        {
            return _groupService.GetGroups().Count(e => e.Id == id) > 0;
        }
    }
}