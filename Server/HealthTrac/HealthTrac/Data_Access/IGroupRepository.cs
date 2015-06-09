using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public interface IGroupRepository
    {
        IList<Group> ReadAll();
        Task<Group> GetById(long id);
        Group Create(Group g);
        void Update(Group g);
        Task Delete(long id);
    }
}
