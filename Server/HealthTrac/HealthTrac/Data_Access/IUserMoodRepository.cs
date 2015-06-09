using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public interface IUserMoodRepository
    {
        IList<UserMood> ReadAll();
        Task<UserMood> GetById(long id);
        IList<UserMood> GetByUser(string userId);
        UserMood Create(UserMood u);
        void Update(UserMood u);
        Task Delete(long id);
    }
}
