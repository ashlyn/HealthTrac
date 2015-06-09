using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public interface IFoodRepository
    {
        IList<Food> ReadAll();
        Task<Food> GetById(long id);
        Food Create(Food b);
        void Update(Food b);
        Task Delete(long id);
    }
}
