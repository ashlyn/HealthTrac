using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using HealthTrac.Data_Access;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public class UserService : IUserService
    {
        private readonly IUserRepository _userRepository;
        private readonly IMembershipService _membershipService;
        private readonly IUnitOfWork _unit;

        public UserService(IUserRepository repository, IMembershipService membershipService, IUnitOfWork unit)
        {
            _userRepository = repository;
            _membershipService = membershipService;
            _unit = unit;
        }

        public IList<User> GetUsers()
        {
            return _userRepository.ReadAll().ToList();
        }

        public User FindUser(string id)
        {
            return _userRepository.GetById(id);
        }

        public IList<User> SearchForUsers(string key)
        {
            var users = _userRepository.ReadAll().Where(u => u.FullName.ToLower().Contains(key.ToLower()));
            return users.ToList();
        }

        public User FindSNUser(string snId)
        {
            var user = _userRepository.ReadAll().FirstOrDefault(s => s.Logins.Any(l => l.ProviderKey.Equals(snId)));
            return user;
        }

        public IList<User> GetGroupMembers(long gId)
        {
            var users = _userRepository.ReadAll().SelectMany(u => u.GroupMembership.Where(m => m.GroupId == gId).Select(g => g.User));
            return users.ToList();
        }

        public IList<User> GetFriends(string id)
        {
            var memberships = _membershipService.GetUserMemberships(id);
            var friends = memberships.SelectMany(m => _membershipService.GetGroupMemberships(m.GroupId)
                .Where(u => u.UserId != id && (u.Status == Status.Member || u.Status == Status.Admin)).Select(u => u.User)).Distinct(new UserComparer());
            return friends.ToList();
        } 

        public async Task<string> CreateUser(User user)
        {
            user.SecurityStamp = Guid.NewGuid().ToString();
            _userRepository.Create(user);
            await _unit.Commit();
            return user.Id;
        }

        public async Task UpdateUser(User user)
        {
            _userRepository.Update(user);
            await _unit.Commit();
        }

        public async Task DeleteUser(string id)
        {
            _userRepository.Delete(id);
            await _unit.Commit();
        }

        private bool _disposed;

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        ~UserService()
        {
            Dispose(false);
        }

        protected virtual void Dispose(bool disposing)
        {
            if (_disposed)
            {
                return;
            }
            if (disposing)
            {

            }
            _disposed = true;
        }
    }
}