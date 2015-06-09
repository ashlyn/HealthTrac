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
    public class MFoodController : ApiController
    {
        private readonly IFoodService _foodService;

        public MFoodController(IFoodService foodService)
        {
            _foodService = foodService;
        }

        // GET api/Food
        public IList<Food> GetActivities()
        {
            return _foodService.GetFoods();
        }

        // GET api/Food/5
        [ResponseType(typeof(Food))]
        public async Task<IHttpActionResult> GetFood(long id)
        {
            Food food = await _foodService.FindFood(id);
            if (food == null)
            {
                return NotFound();
            }

            return Ok(food);
        }

        /*[HttpGet]
        [Route("api/mfood/user/{userId}")]
        public IList<Food> GetUserFood(string userId)
        {
            return _foodRepository.GetByUser(userId);
        }*/

        // PUT api/Food/5
        public async Task<IHttpActionResult> PutFood(long id, Food food)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != food.Id)
            {
                return BadRequest();
            }

            try
            {
                await _foodService.UpdateFood(food);
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!FoodExists(id))
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

        // POST api/Food
        [ResponseType(typeof(Food))]
        public async Task<IHttpActionResult> PostFood(Food food)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            food.Id = await _foodService.CreateFood(food);

            return CreatedAtRoute("DefaultApi", new { id = food.Id }, food);
        }

        // DELETE api/Food/5
        [ResponseType(typeof(Food))]
        public async Task<IHttpActionResult> DeleteFood(long id)
        {
            Food food = await _foodService.FindFood(id);
            if (food == null)
            {
                return NotFound();
            }

            await _foodService.DeleteFood(id);

            return Ok(food);
        }

        private bool FoodExists(long id)
        {
            return _foodService.GetFoods().Count(e => e.Id == id) > 0;
        }
    }
}
