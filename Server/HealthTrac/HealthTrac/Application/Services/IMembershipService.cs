using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public interface IMembershipService : IDisposable
    {
        IList<Membership> GetMemberships();
        Task<Membership> FindMembership(long id);
        IList<Membership> GetUserMemberships(string userId);
        IList<Membership> GetUserInvites(string userId);
        IList<Membership> GetGroupMemberships(long groupId);
        Task<long> CreateMembership(Membership membership);
        Task UpdateMembership(Membership membership);
        Task DeleteMembership(long id);
    }
}
