using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public interface IUserBadgeRepository
    {
        IList<UserBadge> ReadAll();
        Task<UserBadge> GetById(long id);
        IList<UserBadge> GetByUser(string userId);
        UserBadge Create(UserBadge a);
        void Update(UserBadge a);
        Task Delete(long id);
    }
}
