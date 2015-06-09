using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using HealthTrac.Application;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public class MembershipRepository : Repository<Membership>, IMembershipRepository
    {
        public MembershipRepository(IDbSetFactory dbSetFactory)
            :base(dbSetFactory)
        {
            _dbSet = dbSetFactory.CreateDbSet<Membership>();
            _dbSetFactory = dbSetFactory;
        }

        public IList<Membership> GetByUser(string userId)
        {
            var query = "SELECT * FROM dbo.Memberships WHERE UserId = @user AND (Status = 0 OR Status = 1);";
            var user = new SqlParameter("@user", userId);
            object[] parameters = new object[] {user};
            var memberships = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return memberships.ToList();
        }

        public IList<Membership> GetInvitesByUser(string userId)
        {
            var query = "SELECT * FROM dbo.Memberships WHERE UserId = @user AND Status = 4;";
            var user = new SqlParameter("@user", userId);
            object[] parameters = new object[] { user };
            var memberships = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return memberships.ToList();
        }

        public IList<Membership> GetByGroup(long groupId)
        {
            var query = "SELECT * FROM dbo.Memberships WHERE GroupId = @group AND (Status = 0 OR Status = 1);";
            var group = new SqlParameter("@group", groupId);
            object[] parameters = new object[]{ group };
            var memberships = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return memberships.ToList();
        }
    }
}