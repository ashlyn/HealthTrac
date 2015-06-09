using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public interface IMembershipRepository
    {
        IList<Membership> ReadAll();
        Task<Membership> GetById(long id);
        IList<Membership> GetByUser(string userId);
        IList<Membership> GetInvitesByUser(string userId);
        IList<Membership> GetByGroup(long groupId);
        Membership Create(Membership m);
        void Update(Membership m);
        Task Delete(long id);
    }
}
