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
    public class MEndOfDayReportController : ApiController
    {
        private readonly IEndOfDayReportService _endOfDayReportService;

        public MEndOfDayReportController(IEndOfDayReportService endOfDayReportService)
        {
            _endOfDayReportService = endOfDayReportService;
        }

        // GET: api/MEndOfDayReport
        public IList<EndOfDayReport> GetEndOfDayReports()
        {
            return _endOfDayReportService.GetEndOfDayReports();
        }

        [HttpGet]
        [Route("api/mendofdayreport/generate")]
        public async Task<IHttpActionResult> GenerateEndOfDayReports()
        {
            try
            {
                await _endOfDayReportService.GenerateEndOfDayReports();
            }
            catch
            {
                return InternalServerError();
            }
            return Ok();

        }

        // GET api/MEndOfDayReport/5
        [ResponseType(typeof(EndOfDayReport))]
        public async Task<IHttpActionResult> GetEndOfDayReport(long id)
        {
            EndOfDayReport endOfDayReport = await _endOfDayReportService.FindEndOfDayReport(id);
            if (endOfDayReport == null)
            {
                return NotFound();
            }

            return Ok(endOfDayReport);
        }

        [HttpGet]
        [Route("api/mEndOfDayReport/user/{userId}")]
        public IList<EndOfDayReport> GetUserEndOfDayReports(string userId)
        {
            return _endOfDayReportService.GetEndOfDayReportsByUser(userId);
        }

        // PUT api/mEndOfDayReport/5
        public IHttpActionResult PutEndOfDayReport(long id, EndOfDayReport endOfDayReport)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != endOfDayReport.Id)
            {
                return BadRequest();
            }

            try
            {
                _endOfDayReportService.UpdateEndOfDayReport(endOfDayReport);
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!EndOfDayReportExists(id))
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

        // DELETE api/mEndOfDayReport/5
        [ResponseType(typeof(EndOfDayReport))]
        public async Task<IHttpActionResult> DeleteEndOfDayReport(long id)
        {
            EndOfDayReport endOfDayReport = await _endOfDayReportService.FindEndOfDayReport(id);
            if (endOfDayReport == null)
            {
                return NotFound();
            }

            await _endOfDayReportService.DeleteEndOfDayReport(id);

            return Ok(endOfDayReport);
        }

        private bool EndOfDayReportExists(long id)
        {
            return _endOfDayReportService.GetEndOfDayReports().Count(e => e.Id == id) > 0;
        }
    }
}