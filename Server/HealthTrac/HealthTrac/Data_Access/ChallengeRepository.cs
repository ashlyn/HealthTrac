using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using HealthTrac.Application;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public class ChallengeRepository : Repository<Challenge>, IChallengeRepository
    {
        public ChallengeRepository(IDbSetFactory dbSetFactory)
            :base(dbSetFactory)
        {
            _dbSet = dbSetFactory.CreateDbSet<Challenge>();
            _dbSetFactory = dbSetFactory;
        }

        public IList<Challenge> GetByChallenger(string userId)
        {
            var query = "SELECT * FROM dbo.Challenges WHERE ChallengerId = @user;";
            var user = new SqlParameter("@user", userId);
            object[] parameters = new object[] { user };
            var challenges = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return challenges.ToList();
        }

        public IList<Challenge> GetByFriend(string userId)
        {
            var query = "SELECT * FROM dbo.Challenges WHERE FriendId = @user;";
            var user = new SqlParameter("@user", userId);
            object[] parameters = new object[] { user };
            var challenges = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return challenges.ToList();
        }

        public IList<Challenge> GetByUser(string userId)
        {
            var query = "SELECT * FROM dbo.Challenges WHERE ChallengerId = @user OR FriendId = @user;";
            var user = new SqlParameter("@user", userId);
            object[] parameters = new object[] { user };
            var challenges = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return challenges.ToList();
        }
    }
}