using HealthTrac.Application;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public class GeoPointRepository : Repository<GeoPoint>, IGeoPointRepository
    {
        public GeoPointRepository(IDbSetFactory dbSetFactory)
            :base(dbSetFactory)
        {
            _dbSet = dbSetFactory.CreateDbSet<GeoPoint>();
            _dbSetFactory = dbSetFactory;
        }
    }
}