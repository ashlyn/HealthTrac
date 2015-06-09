using HealthTrac.Application;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public class GroupRepository : Repository<Group>, IGroupRepository
    {
        public GroupRepository(IDbSetFactory dbSetFactory)
            :base(dbSetFactory)
        {
            _dbSet = dbSetFactory.CreateDbSet<Group>();
            _dbSetFactory = dbSetFactory;
        }
    }
}