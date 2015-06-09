using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public interface IMoodRepository
    {
        IList<Mood> ReadAll();
        Task<Mood> GetById(long id);
        Mood Create(Mood m);
        void Update(Mood m);
        Task Delete(long id);
    }
}
