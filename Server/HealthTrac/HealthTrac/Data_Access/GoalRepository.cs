using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using HealthTrac.Application;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public class GoalRepository : Repository<Goal>, IGoalRepository
    {
        public GoalRepository(IDbSetFactory dbSetFactory)
            :base(dbSetFactory)
        {
            _dbSet = dbSetFactory.CreateDbSet<Goal>();
            _dbSetFactory = dbSetFactory;
        }

        public IList<Goal> GetByUser(string userId)
        {
            var query = "SELECT * FROM dbo.Goals WHERE UserId = @user;";
            var user = new SqlParameter("@user", userId);
            object[] parameters = new object[] { user };
            var goals = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return goals.ToList();
        } 
    }
}