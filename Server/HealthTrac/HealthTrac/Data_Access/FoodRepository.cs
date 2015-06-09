using HealthTrac.Application;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public class FoodRepository : Repository<Food>, IFoodRepository
    {
        public FoodRepository(IDbSetFactory dbSetFactory)
            :base(dbSetFactory)
        {
            _dbSet = dbSetFactory.CreateDbSet<Food>();
            _dbSetFactory = dbSetFactory;
        }
    }
}