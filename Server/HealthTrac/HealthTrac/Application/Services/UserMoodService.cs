using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using HealthTrac.Data_Access;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public class UserMoodService : IUserMoodService
    {
        private readonly IUserMoodRepository _userMoodRepository;
        private readonly IFeedEventService _feedEventService;
        private readonly IUnitOfWork _unit;

        public UserMoodService(IUserMoodRepository userMoodRepository, IFeedEventService feedEventService, IUnitOfWork unit)
        {
            _userMoodRepository = userMoodRepository;
            _feedEventService = feedEventService;
            _unit = unit;
        }

        public IList<UserMood> GetUserMoods()
        {
            return _userMoodRepository.ReadAll().ToList();
        }

        public Task<UserMood> FindUserMood(long id)
        {
            return _userMoodRepository.GetById(id);
        }

        public IList<UserMood> GetMoodsByUser(string userId)
        {
            var uMoods = _userMoodRepository.GetByUser(userId);
            return uMoods.ToList();
        }

        public IList<UserMood> Search(string key)
        {
            var uMoods = _userMoodRepository.ReadAll().Where(u => (u.Mood.Type + " " + u.User.FullName).ToLower().Contains(key.ToLower()));
            return uMoods.ToList();
        }

        public async Task<long> CreateUserMood(UserMood userMood)
        {
            userMood = _userMoodRepository.Create(userMood);
            await _unit.Commit();
            await _feedEventService.GenerateFeedEvent(userMood);
            await _unit.Commit();
            return userMood.Id;
        }

        public async Task UpdateUserMood(UserMood userMood)
        {
            _userMoodRepository.Update(userMood);
            await _unit.Commit();
        }

        public async Task DeleteUserMood(long id)
        {
            await _userMoodRepository.Delete(id);
            var feedEvents = _feedEventService.GetFeedEvents().Where(e => e.Type == EventType.Mood && e.EventId == id);
            foreach (FeedEvent f in feedEvents)
            {
                await _feedEventService.DeleteFeedEvent(f.Id);
            }
            await _unit.Commit();
        }

        #region Disposing
        private bool _disposed;

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        ~UserMoodService()
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