using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using HealthTrac.Data_Access;
using HealthTrac.Models;
using HealthTrac.Utilities;

namespace HealthTrac.Application.Services
{
    public class FeedEventService : IFeedEventService
    {
        private readonly IFeedEventRepository _feedEventRepository;
        private readonly IMembershipRepository _membershipRepository;
        private readonly IMoodRepository _moodRepository;
        private readonly IGroupRepository _groupRepository;
        private readonly IBadgeRepository _badgeRepository;
        private readonly IUnitOfWork _unit;

        public FeedEventService(IFeedEventRepository feedEventRepository, IMembershipRepository membershipRepository, IMoodRepository moodRepository, IGroupRepository groupRepository, IBadgeRepository badgeRepository, IUnitOfWork unit)
        {
            _feedEventRepository = feedEventRepository;
            _membershipRepository = membershipRepository;
            _moodRepository = moodRepository;
            _groupRepository = groupRepository;
            _badgeRepository = badgeRepository;
            _unit = unit;
        }

        public IList<FeedEvent> GetFeedEvents()
        {
            return _feedEventRepository.ReadAll().ToList();
        }

        public async Task<FeedEvent> FindFeedEvent(long id)
        {
            return await _feedEventRepository.GetById(id);
        }

        public IList<FeedEvent> GetFeedEventsByUser(string id)
        {
            var events = _feedEventRepository.GetByUser(id);
            return events.ToList();
        }

        public IList<FeedEvent> GetFeedEventsByGroup(long id)
        {
            var userIds = _membershipRepository.ReadAll().Where(m => m.GroupId == id & (m.Status == Status.Member || m.Status == Status.Admin)).Select(u => u.UserId);
            var events = _feedEventRepository.ReadAll().Where(e => userIds.Contains(e.UserId));
            return events.ToList();
        } 

        public async Task<long> CreateFeedEvent(FeedEvent feedEvent)
        {
            feedEvent = _feedEventRepository.Create(feedEvent);
            await _unit.Commit();
            return feedEvent.Id;
        }

        private object GetProperty<T>(Type type, string property, T entity)
        {
            return type.GetProperty(property).GetValue(entity);
        }
        
        public async Task<long> GenerateFeedEvent<T>(T entity)
        {
            var type = entity.GetType();
            var userId = (string) GetProperty(type, "UserId", entity);
            var eventId = (long) GetProperty(type, "Id", entity);
            var e = new FeedEvent {EventId = eventId, UserId = userId};
            if (! (await ValidFeedEvent(e, type, entity)))
            {
                if (! (await ValidFeedEvent(e, type.BaseType, entity)))
                {
                    throw new Exception("not valid feed event");
                }
            }
            _feedEventRepository.Create(e);
            return e.Id;
        }

        private async Task<bool> ValidFeedEvent<T>(FeedEvent e, Type type, T entity)
        {
            if (type == typeof(Activity))
            {
                e.Type = EventType.Activity;
                e.Date = (DateTime) GetProperty(type, "StartTime", entity);
                e.Description = string.Format("Completed {0:0.00} minutes of {1}", (double)GetProperty(type, "Duration", entity) / 60, (string)GetProperty(type, "Name", entity));
            }
            else if (type == typeof(UserBadge))
            {
                e.Type = EventType.Badge;
                e.Date = DateTime.UtcNow.LocalTime();
                var badge = await _badgeRepository.GetById((long) GetProperty(type, "BadgeId", entity));
                e.Description = string.Format("Earned the {0} badge", badge.Name);
            }
            else if (type == typeof(Food))
            {
                e.Type = EventType.Food;
                e.Date = (DateTime) GetProperty(type, "Time", entity);
                e.Description = string.Format("Ate {0}",(string) GetProperty(type, "FoodName", entity));
            }
            else if (type == typeof(Goal))
            {
                var goalType = (GoalType)GetProperty(type, "Type", entity);
                var timeFrame = (TimeFrame) GetProperty(type, "TimeFrame", entity);
                var progress = (double) GetProperty(type, "Progress", entity);
                var completed = (bool) GetProperty(type, "Completed", entity);
                Dictionary<GoalType, string> units = new Dictionary<GoalType, string>()
                {
                    { GoalType.Distance, "miles" },
                    { GoalType.Duration, "minutes" },
                    { GoalType.Steps, "steps" }
                };
                var target = (double) GetProperty(type, "Target", entity);
                if (goalType == GoalType.Duration)
                {
                    target /= 60;
                }
                if (progress < 1)
                {
                    e.Type = EventType.GoalSet;
                    e.Date = (DateTime) GetProperty(type, "SetDate", entity);
                    e.Description = string.Format("Set a new {0} {1} goal:  {2} {3}", timeFrame, goalType, target, units[goalType]);
                }
                else if (!completed)
                {
                    e.Type = EventType.GoalAchieved;
                    e.Date = DateTime.UtcNow.LocalTime();
                    e.Description = string.Format("Achieved a {0} {1} goal:  {2} {3}", timeFrame, goalType, target, units[goalType]);
                }
            }
            else if (type == typeof(Membership))
            {
                e.EventId = (long) GetProperty(type, "GroupId", entity);
                e.Date = DateTime.UtcNow.LocalTime();
                var status = (Status) GetProperty(type, "Status", entity);
                var group = await _groupRepository.GetById((long)GetProperty(type, "GroupId", entity));
                if (status == Status.Member || status == Status.Admin)
                {
                    e.Type = EventType.GroupJoin;
                    e.Description = string.Format("Joined {0}", group.GroupName);
                }
                else if (status == Status.Left)
                {
                    e.Type = EventType.GroupLeave;
                    e.Description = string.Format("Left {0}", group.GroupName);
                }
            }
            else if (type == typeof (UserMood))
            {
                e.Type = EventType.Mood;
                e.Date = (DateTime) GetProperty(type, "Time", entity);
                var mood = await _moodRepository.GetById((long) GetProperty(type, "MoodId", entity));
                e.Description = string.Format("Feeling {0}", mood.Type);
            }
            else if (type == typeof (EndOfDayReport))
            {
                e.Type = EventType.EndOfDay;
                var date = (DateTime) GetProperty(type, "Date", entity);
                e.Date = date;
                e.Description = string.Format("End of Day Report for {0}", date.ToLocalTime().ToShortDateString());
            }
            else
            {
                return false;
            }
            return true;
        }

        public async Task UpdateFeedEvent(FeedEvent feedEvent)
        {
            _feedEventRepository.Update(feedEvent);
            await _unit.Commit();
        }

        public async Task DeleteFeedEvent(long id)
        {
            await _feedEventRepository.Delete(id);
            await _unit.Commit();
        }

        private bool _disposed;

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        ~FeedEventService()
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