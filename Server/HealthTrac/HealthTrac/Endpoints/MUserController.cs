using System;
using System.Collections.Generic;
using System.Data.Entity.Infrastructure;
using System.Data.Entity.Validation;
using System.Linq;
using System.Net;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Description;
using HealthTrac.Application.Services;
using HealthTrac.Models;

namespace HealthTrac.Controllers
{
    public class MUserController : ApiController
    {
        private readonly IUserService _userService;

        public MUserController(IUserService userService)
        {
            _userService = userService;
        }

        // GET api/User
        public IList<User> GetUsers()
        {
            return _userService.GetUsers();
        }

        // GET api/User/5
        [ResponseType(typeof(User))]
        [Route("api/muser/{id}")]
        public IHttpActionResult GetUser(string id)
        {
            User user = _userService.FindUser(id);
            if (user == null)
            {
                return NotFound();
            }

            return Ok(user);
        }

        [HttpGet]
        [Route("api/muser/group/{groupId}")]
        public IList<User> UsersInGroup(long groupId)
        {
            var users = _userService.GetGroupMembers(groupId);
            return users;
        }

        [HttpGet]
        [ActionName("Search")]
        public IList<User> SearchByName(string name)
        {
            return _userService.SearchForUsers(name);
        }

        [HttpGet]
        [Route("api/muser/key/{socialKey}")]
        public User FindSocialUser(string socialKey)
        {
            return _userService.FindSNUser(socialKey);
        }

        [HttpGet]
        [Route("api/muser/friends/{id}")]
        public IList<User> GetUserFriends(string id)
        {
            return _userService.GetFriends(id);
        } 

        // PUT api/User/5
        [HttpPut]
        [Route("api/muser/{id}")]
        public async Task<IHttpActionResult> PutUser(string id, User user)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != user.Id)
            {
                return BadRequest();
            }

            try
            {
                await _userService.UpdateUser(user);
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!UserExists(id))
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

        // POST api/User
        [ResponseType(typeof(User))]
        public async Task<IHttpActionResult> PostUser(User user)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            try
            {
               user.Id = await _userService.CreateUser(user);
            }
            catch (DbUpdateException)
            {
                if (UserExists(user.Id))
                {
                    return Conflict();
                }
                else
                {
                    throw;
                }
            }
            catch (DbEntityValidationException e) {
                Console.Out.WriteLine(e.Message);
            }

            return CreatedAtRoute("DefaultApi", new { id = user.Id }, user);
        }

        // DELETE api/User/5
        [HttpDelete]
        [ResponseType(typeof(User))]
        [Route("api/muser/{id}")]
        public async Task<IHttpActionResult> DeleteUser(string id)
        {
            try
            {
                await _userService.DeleteUser(id);
            }
            catch
            {
                return NotFound();
            }

            return Ok(id);
        }

        private bool UserExists(string id)
        {
            return _userService.GetUsers().Count(e => e.Id == id) > 0;
        }
    }
}
