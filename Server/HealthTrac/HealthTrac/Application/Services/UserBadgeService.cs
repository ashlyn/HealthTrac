using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using HealthTrac.Data_Access;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public class UserBadgeService : IUserBadgeService
    {
        private readonly IUserBadgeRepository _userBadgeRepository;
        private readonly IFeedEventService _feedEventService;
        private readonly IUserRepository _userRepository;
        private readonly IUnitOfWork _unit;

        public UserBadgeService(IUserBadgeRepository userBadgeRepository, IFeedEventService feedEventService, IUserRepository userRepository, IUnitOfWork unit)
        {
            _userBadgeRepository = userBadgeRepository;
            _feedEventService = feedEventService;
            _unit = unit;
            _userRepository = userRepository;
        }

        public IList<UserBadge> GetUserBadges()
        {
            return _userBadgeRepository.ReadAll().ToList();
        }

        public async Task<UserBadge> FindUserBadge(long id)
        {
            return await _userBadgeRepository.GetById(id);
        }

        public IList<Badge> GetUserBadges(string userId)
        {
            var userBadges = _userBadgeRepository.GetByUser(userId).Select(b => b.Badge);
            return userBadges.ToList();
        }

        public async Task<long> CreateUserBadge(UserBadge userBadge)
        {
            _userBadgeRepository.Create(userBadge);
            await _unit.Commit();
            await _feedEventService.GenerateFeedEvent(userBadge);
            await _unit.Commit();
            return userBadge.Id;
        }

        public async void UpdateUserBadge(UserBadge userBadge)
        {
            _userBadgeRepository.Update(userBadge);
            await _unit.Commit();
        }

        public async Task DeleteUserBadge(long id)
        {
            await _userBadgeRepository.Delete(id);
            var feedEvents = _feedEventService.GetFeedEvents().Where(e => e.Type == EventType.Badge && e.EventId == id);
            foreach (FeedEvent f in feedEvents)
            {
                await _feedEventService.DeleteFeedEvent(f.Id);
            }
            await _unit.Commit();
        }

        public void CheckGroupBadgeProgress(string userId)
        {
            var badges = GetUserBadges(userId);
            var has10GroupBadge = badges.Select(b => b.Id).Contains(9);
            var memberships = _userRepository.GetById(userId).GroupMembership;

            if (!has10GroupBadge && memberships.Count >= 10)
            {
                var userBadge = new UserBadge {UserId = userId, BadgeId = 9};
                _userBadgeRepository.Create(userBadge);
            }
        }

        public void CheckActivityBadgeProgress(string userId, Activity activity)
        {
            var badgeIds = GetUserBadges(userId).Select(b => b.Id).ToList();

            if (activity.Type == ActivityType.Walking) 
            {
                if(!badgeIds.Contains(2) && activity.Steps > 100)
                {
                    var userBadge = new UserBadge { UserId = userId, BadgeId = 2 };
                    _userBadgeRepository.Create(userBadge);
                }
                if (!badgeIds.Contains(7) && activity.Distance > 10)
                {
                    var userBadge = new UserBadge { UserId = userId, BadgeId = 7 };
                    _userBadgeRepository.Create(userBadge);
                }
            }
            else if (activity.Type == ActivityType.Running)
            {
                if (!badgeIds.Contains(3) && activity.Distance > 26)
                {
                    var userBadge = new UserBadge { UserId = userId, BadgeId = 3 };
                    _userBadgeRepository.Create(userBadge);
                }
            }
            else if (activity.Type == ActivityType.Biking)
            {
                if (!badgeIds.Contains(5) && activity.Distance > 10)
                {
                    var userBadge = new UserBadge { UserId = userId, BadgeId = 5 };
                    _userBadgeRepository.Create(userBadge);
                }
                else if (!badgeIds.Contains(6) && activity.Distance > 100)
                {
                    var userBadge = new UserBadge { UserId = userId, BadgeId = 6 };
                    _userBadgeRepository.Create(userBadge);
                }
            }
            else if (activity.Type == ActivityType.Jogging)
            {
                if (!badgeIds.Contains(8) && activity.Distance > 10)
                {
                    var userBadge = new UserBadge { UserId = userId, BadgeId = 8 };
                    _userBadgeRepository.Create(userBadge);
                }
            }
        }

        #region Disposing
        private bool _disposed;

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        ~UserBadgeService()
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
        #endregion
    }
}