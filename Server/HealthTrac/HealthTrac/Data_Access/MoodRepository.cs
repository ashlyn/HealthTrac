using HealthTrac.Application;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public class MoodRepository : Repository<Mood>, IMoodRepository
    {
        public MoodRepository(IDbSetFactory dbSetFactory)
            :base(dbSetFactory)
        {
            _dbSet = dbSetFactory.CreateDbSet<Mood>();
            _dbSetFactory = dbSetFactory;
        }
    }
}