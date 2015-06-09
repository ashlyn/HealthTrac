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
    public class MGeoPointController : ApiController 
    {
        private readonly IGeoPointService _geoPointService;

        public MGeoPointController(IGeoPointService geoPointService)
        {
            _geoPointService = geoPointService;
        }

        // GET api/GeoPoint
        public IList<GeoPoint> GetGeoPoints()
        {
            return _geoPointService.GetGeoPoints();
        }

        // GET api/GeoPoint/5
        [ResponseType(typeof(GeoPoint))]
        public async Task<IHttpActionResult> GetGeoPoint(long id)
        {
            GeoPoint point = await _geoPointService.FindGeoPoint(id);
            if (point == null)
            {
                return NotFound();
            }

            return Ok(point);
        }

        // PUT api/GeoPoint/5
        public async Task<IHttpActionResult> PutGeoPoint(long id, GeoPoint point)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != point.Id)
            {
                return BadRequest();
            }

            try
            {
                await _geoPointService.UpdateGeoPoint(point);
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!GeoPointExists(id))
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

        // POST api/GeoPoint
        [ResponseType(typeof(GeoPoint))]
        public async Task<IHttpActionResult> PostGeoPoint(GeoPoint point)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            await _geoPointService.CreateGeoPoint(point);

            return CreatedAtRoute("DefaultApi", new { id = point.Id }, point);
        }

        // DELETE api/GeoPoint/5
        [ResponseType(typeof(GeoPoint))]
        public async Task<IHttpActionResult> DeleteGeoPoint(long id)
        {
            GeoPoint point = await _geoPointService.FindGeoPoint(id);
            if (point == null)
            {
                return NotFound();
            }

            await _geoPointService.DeleteGeoPoint(id);

            return Ok(point);
        }

        private bool GeoPointExists(long id)
        {
            return _geoPointService.GetGeoPoints().Count(e => e.Id == id) > 0;
        }
    }
}