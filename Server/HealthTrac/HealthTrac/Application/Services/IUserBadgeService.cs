using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public interface IUserBadgeService : IDisposable
    {
        IList<UserBadge> GetUserBadges();
        Task<UserBadge> FindUserBadge(long id);
        IList<Badge> GetUserBadges(string userId);
        Task<long> CreateUserBadge(UserBadge userBadge);
        void CheckGroupBadgeProgress(string userId);
        void CheckActivityBadgeProgress(string userId, Activity activity);
        void UpdateUserBadge(UserBadge userBadge);
        Task DeleteUserBadge(long id);
    }
}