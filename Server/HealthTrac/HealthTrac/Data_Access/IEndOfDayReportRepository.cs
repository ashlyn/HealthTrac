using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public interface IEndOfDayReportRepository
    {
        IList<EndOfDayReport> ReadAll();
        Task<EndOfDayReport> GetById(long id);
        IList<EndOfDayReport> GetByUser(string userId);
        EndOfDayReport Create(EndOfDayReport r);
        void Update(EndOfDayReport r);
        Task Delete(long id);
    }
}
