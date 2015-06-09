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
    public class MChallengeController : ApiController
    {
        private readonly IChallengeService _challengeService;

        public MChallengeController(IChallengeService challengeService)
        {
            _challengeService = challengeService;
        }

        // GET api/Challenge
        public IList<Challenge> GetChallenges()
        {
            return _challengeService.GetChallenges();
        }

        // GET api/Challenge/5
        [ResponseType(typeof(Challenge))]
        public async Task<IHttpActionResult> GetChallenge(long id)
        {
            Challenge challenge = await _challengeService.FindChallenge(id);
            if (challenge == null)
            {
                return NotFound();
            }

            return Ok(challenge);
        }

        [HttpGet]
        [Route("api/mchallenge/user/{userId}")]
        public IList<Challenge> GetUserChallenges(string userId)
        {
            return _challengeService.GetUserChallenges(userId);
        }

        [HttpGet]
        [Route("api/mchallenge/challenger/{userId}")]
        public IList<Challenge> GetChallengerChallenges(string userId)
        {
            return _challengeService.GetChallengerChallenges(userId);
        }

        [HttpGet]
        [Route("api/mchallenge/friend/{userId}")]
        public IList<Challenge> GetFriendChallenges(string userId)
        {
            return _challengeService.GetFriendChallenges(userId);
        }

        // PUT api/Challenge/5
        public async Task<IHttpActionResult> PutChallenge(long id, Challenge challenge)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != challenge.Id)
            {
                return BadRequest();
            }

            try
            {
                await _challengeService.UpdateChallenge(challenge);
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ChallengeExists(id))
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

        // POST api/Challenge
        [ResponseType(typeof(Challenge))]
        public async Task<IHttpActionResult> PostChallenge(Challenge challenge)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            challenge.Id = await _challengeService.CreateChallenge(challenge);

            return CreatedAtRoute("DefaultApi", new { id = challenge.Id }, challenge);
        }

        // DELETE api/Challenge/5
        [ResponseType(typeof(Challenge))]
        public async Task<IHttpActionResult> DeleteChallenge(long id)
        {
            Challenge challenge = await _challengeService.FindChallenge(id);
            if (challenge == null)
            {
                return NotFound();
            }

            await _challengeService.DeleteChallenge(id);

            return Ok(challenge);
        }

        private bool ChallengeExists(long id)
        {
            return _challengeService.GetChallenges().Count(e => e.Id == id) > 0;
        }
    }
}
