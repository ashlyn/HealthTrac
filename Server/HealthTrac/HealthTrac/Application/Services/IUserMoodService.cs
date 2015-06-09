using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public interface IUserMoodService : IDisposable
    {
        IList<UserMood> GetUserMoods();
        Task<UserMood> FindUserMood(long id);
        IList<UserMood> GetMoodsByUser(string uId);
        IList<UserMood> Search(string key);
        Task<long> CreateUserMood(UserMood userMood);
        Task UpdateUserMood(UserMood userMood);
        Task DeleteUserMood(long id);
    }
}
