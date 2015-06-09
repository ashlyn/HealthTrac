using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public interface IBadgeRepository
    {
        IList<Badge> ReadAll();
        Task<Badge> GetById(long id);
        Badge Create(Badge b);
        void Update(Badge b);
        Task Delete(long id);
    }
}
