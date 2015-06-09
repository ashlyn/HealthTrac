using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public interface IGroupService : IDisposable
    {
        IList<Group> GetGroups();
        Task<Group> FindGroup(long id);
        IList<Group> GetUserGroups(string userId);
        IList<Group> GetUserInvitedGroups(string userId);
        IList<Tuple<User, double>> GetLeaderBoard(long groupId, string type);
        IList<Tuple<User, double>> GetLeaderBoard(long groupId, string type, int n);
        IList<Group> Search(string key);
        Task<long> CreateGroup(Group group);
        Task UpdateGroup(Group group);
        Task DeleteGroup(long id);
    }
}
