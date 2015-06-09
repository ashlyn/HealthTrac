using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public interface IUserService : IDisposable
    {
        IList<User> GetUsers();
        User FindUser(string id);
        IList<User> SearchForUsers(string key);
        User FindSNUser(string snId);
        IList<User> GetGroupMembers(long gId);
        IList<User> GetFriends(string id); 
        Task<string> CreateUser(User user);
        Task UpdateUser(User user);
        Task DeleteUser(string id);
    }
}
