using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public interface IBadgeService : IDisposable
    {
        IList<Badge> GetBadges();
        Task<Badge> FindBadge(long id);
        IList<Badge> SearchForBadges(string key);
        Task<long> CreateBadge(Badge badge);
        Task UpdateBadge(Badge badge);
        Task DeleteBadge(long id);
    }
}
