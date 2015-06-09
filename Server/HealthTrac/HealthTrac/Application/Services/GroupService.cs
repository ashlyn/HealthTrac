using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using HealthTrac.Data_Access;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public class GroupService : IGroupService
    {
        private readonly IGroupRepository _groupRepository;
        private readonly IMembershipService _membershipService;
        private readonly IFeedEventService _feedEventService;
        private readonly IUnitOfWork _unit;

        public GroupService(IGroupRepository groupRepository, IMembershipService membershipService, IFeedEventService feedEventService, IUnitOfWork unit)
        {
            _groupRepository = groupRepository;
            _membershipService = membershipService;
            _feedEventService = feedEventService;
            _unit = unit;
        }

        public IList<Group> GetGroups()
        {
            return _groupRepository.ReadAll().ToList();
        }

        public async Task<Group> FindGroup(long id)
        {
            return await _groupRepository.GetById(id);
        }

        public IList<Group> GetUserGroups(string userId)
        {
            var groups = _groupRepository.ReadAll().SelectMany(g => g.GroupMembers.Where(m => m.UserId == userId).Select(r => r.Group));
            return groups.ToList();
        }

        public IList<Group> GetUserInvitedGroups(string userId)
        {
            var groups = _membershipService.GetUserInvites(userId).Select(m => m.Group);
            return groups.ToList();
        } 

        public IList<Group> Search(string key)
        {
            var groups = _groupRepository.ReadAll().Where(g => g.GroupName.ToLower().Contains(key.ToLower()) || g.Description.ToLower().Contains(key.ToLower()));
            return groups.ToList();
        }

        public async Task<long> CreateGroup(Group group)
        {
            group = _groupRepository.Create(group);
            await _unit.Commit();
            return group.Id;
        }

        public async Task UpdateGroup(Group group)
        {
            _groupRepository.Update(group);
            await _unit.Commit();
        }

        public IList<Tuple<User, double>> GetLeaderBoard(long groupId, string type)
        {
            return GetLeaderBoard(groupId, type, 10);
        }

        public IList<Tuple<User, double>> GetLeaderBoard(long groupId, string type, int n)
        {
            IList<Tuple<User, double>> leaders = null;
            var users = _membershipService.GetGroupMemberships(groupId).Select(u => u.User);
            type = type.Trim().ToLower();
            if(type == "duration")
            {
                leaders = users
                    .GroupBy(l => new {User = l, Total = l.Activities.Sum(a => a.Duration)})
                    .OrderByDescending(g => g.Key.Total)
                    .Take(n)
                    .Select(u => Tuple.Create(u.Key.User, u.Key.Total))
                    .ToList();
            } 
            else if (type == "distance") 
            {
                leaders = users
                    .GroupBy(l => new { User = l, Total = l.Activities.Sum(a => a.Distance) })
                    .OrderByDescending(g => g.Key.Total)
                    .Take(n)
                    .Select(u => Tuple.Create(u.Key.User, u.Key.Total))
                    .ToList();
            }
            else if (type.Equals("steps"))
            {
                leaders = users
                    .GroupBy(l => new { User = l, Total = (double) l.Activities.Sum(a => a.Steps) })
                    .OrderByDescending(g => g.Key.Total)
                    .Take(n)
                    .Select(u => Tuple.Create(u.Key.User, u.Key.Total))
                    .ToList();
            }
            return leaders;
        }

        public async Task DeleteGroup(long id)
        {
            var memberships = _membershipService.GetMemberships().Where(m => m.GroupId == id);
            foreach (Membership m in memberships)
            {
                await _membershipService.DeleteMembership(m.Id);
            }
            await _groupRepository.Delete(id);
            var feedEvents = _feedEventService.GetFeedEvents().Where(e => (e.Type == EventType.GroupJoin || e.Type == EventType.GroupLeave) && e.EventId == id);
            foreach (FeedEvent f in feedEvents)
            {
                await _feedEventService.DeleteFeedEvent(f.Id);
            }
            await _unit.Commit();
        }

        private bool _disposed;

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        ~GroupService()
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
