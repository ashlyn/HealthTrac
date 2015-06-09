using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using HealthTrac.Application;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public class UserBadgeRepository : Repository<UserBadge>, IUserBadgeRepository
    {
        public UserBadgeRepository(IDbSetFactory dbSetFactory)
            :base(dbSetFactory)
        {
            _dbSet = dbSetFactory.CreateDbSet<UserBadge>();
            _dbSetFactory = dbSetFactory;
        }

        public IList<UserBadge> GetByUser(string userId)
        {
            var query = "SELECT * FROM dbo.UserBadges WHERE UserId = @user;";
            var user = new SqlParameter("@user", userId);
            object[] parameters = new object[] { user };
            var userBadges = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return userBadges.ToList();
        } 
    }
}