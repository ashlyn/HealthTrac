using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using HealthTrac.Application;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public class UserMoodRepository : Repository<UserMood>, IUserMoodRepository
    {
        public UserMoodRepository(IDbSetFactory dbSetFactory)
            :base(dbSetFactory)
        {
            _dbSet = dbSetFactory.CreateDbSet<UserMood>();
            _dbSetFactory = dbSetFactory;
        }

        public IList<UserMood> GetByUser(string userId)
        {
            var query = "SELECT * FROM dbo.UserMoods WHERE UserId = @user;";
            var user = new SqlParameter("@user", userId);
            object[] parameters = new object[] { user };
            var moods = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return moods.ToList();
        } 
    }
}