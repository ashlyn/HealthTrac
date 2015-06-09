using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public interface IEndOfDayReportService
    {
        IList<EndOfDayReport> GetEndOfDayReports();
        Task<EndOfDayReport> FindEndOfDayReport(long id);
        IList<EndOfDayReport> GetEndOfDayReportsByUser(string id);
        Task<long> CreateEndOfDayReport(EndOfDayReport report);
        EndOfDayReport CalculateEndOfDayReport(DateTime date, User user);
        Task<EndOfDayReport> GenerateEndOfDayReport(DateTime date, User user);
        Task GenerateEndOfDayReports();
        Task UpdateEndOfDayReport(EndOfDayReport report);
        Task DeleteEndOfDayReport(long id);
    }
}
