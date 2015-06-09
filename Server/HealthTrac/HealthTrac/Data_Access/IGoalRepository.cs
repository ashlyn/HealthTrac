using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public interface IGoalRepository
    {
        IList<Goal> ReadAll();
        Task<Goal> GetById(long id);
        IList<Goal> GetByUser(string userid);
        Goal Create(Goal g);
        void Update(Goal g);
        Task Delete(long id);
    }
}
