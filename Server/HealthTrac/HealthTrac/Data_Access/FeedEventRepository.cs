using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using HealthTrac.Application;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public class FeedEventRepository : Repository<FeedEvent>, IFeedEventRepository
    {
        public FeedEventRepository(IDbSetFactory dbSetFactory)
            :base(dbSetFactory)
        {
            _dbSet = dbSetFactory.CreateDbSet<FeedEvent>();
            _dbSetFactory = dbSetFactory;
        }

        //cannot return enumerable because needs to stay ordered
        new public IList<FeedEvent> ReadAll()
        {
            var events = _dbSet.SqlQuery("SELECT * FROM dbo.FeedEvents ORDER BY Date DESC;").AsQueryable();
            return events.ToList();
        }

        public IList<FeedEvent> GetByUser(string userId)
        {
            var query = "SELECT * FROM dbo.FeedEvents WHERE UserId = @user ORDER BY Date DESC;";
            var user = new SqlParameter("@user", userId);
            object[] parameters = new object[] { user };
            var events = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return events.ToList();
        } 
    }
}