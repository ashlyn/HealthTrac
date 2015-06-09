using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using HealthTrac.Application;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public class EndOfDayReportRepository : Repository<EndOfDayReport>, IEndOfDayReportRepository
    {
        public EndOfDayReportRepository(IDbSetFactory dbSetFactory)
            :base(dbSetFactory)
        {
            _dbSet = dbSetFactory.CreateDbSet<EndOfDayReport>();
            _dbSetFactory = dbSetFactory;
        }

        public IList<EndOfDayReport> GetByUser(string userId)
        {
            var query = "SELECT * FROM dbo.EndOfDayReports WHERE UserId = @user;";
            var user = new SqlParameter("@user", userId);
            object[] parameters = new object[] { user };
            var reports = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return reports.ToList();
        } 
    }
}