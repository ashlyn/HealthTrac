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
    public class MMoodController : ApiController
    {
        private readonly IMoodService _moodService;

        public MMoodController(IMoodService moodService)
        {
            _moodService = moodService;
        }

        // GET api/MMood
        public IList<Mood> GetMoods()
        {
            return _moodService.GetMoods();
        }

        // GET api/Activity/5
        [ResponseType(typeof(Mood))]
        public async Task<IHttpActionResult> GetMood(long id)
        {
            Mood mood = await _moodService.FindMood(id);
            if (mood == null)
            {
                return NotFound();
            }

            return Ok(mood);
        }

        // PUT api/MMood/5
        public async Task<IHttpActionResult> PutMood(long id, Mood mood)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != mood.Id)
            {
                return BadRequest();
            }

            try
            {
                await _moodService.UpdateMood(mood);
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!MoodExists(id))
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

        // POST api/MMood
        [ResponseType(typeof(Mood))]
        public async Task<IHttpActionResult> PostMood(Mood mood)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            mood.Id = await _moodService.CreateMood(mood);

            return CreatedAtRoute("DefaultApi", new { id = mood.Id }, mood);
        }

        // DELETE api/MMood/5
        [ResponseType(typeof(Mood))]
        public async Task<IHttpActionResult> DeleteMood(long id)
        {
            Mood mood = await _moodService.FindMood(id);
            if (mood == null)
            {
                return NotFound();
            }

            await _moodService.DeleteMood(id);

            return Ok(mood);
        }

        private bool MoodExists(long id)
        {
            return _moodService.GetMoods().Count(e => e.Id == id) > 0;
        }
    }
}
