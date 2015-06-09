using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Charon.ActivityPrediction;
using HealthTrac.Data_Access;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public class ActivityService : IActivityService
    {
        private readonly IActivityRepository _activityRepository;
        private readonly IGoalService _goalService;
        private readonly IFeedEventService _feedEventService;
        private readonly IUserService _userService;
        private readonly IUserBadgeService _userBadgeService;
        private readonly IUnitOfWork _unit;

        private const double MetersPerInch = 0.0254;
        private const double KilogramsPerPound = 0.4536;
        private const double MetersPerMile = 1609.34;

        public ActivityService(IActivityRepository activityRepository, IGoalService goalService, IFeedEventService feedEventService, IUserService userService, IUserBadgeService userBadgeService, IUnitOfWork unit)
        {
            _activityRepository = activityRepository;
            _goalService = goalService;
            _feedEventService = feedEventService;
            _userService = userService;
            _userBadgeService = userBadgeService;
            _unit = unit;
        }

        public IList<Activity> GetActivities()
        {
            return _activityRepository.ReadAll().ToList();
        }

        public async Task<Activity> FindActivity(long id)
        {
            return await _activityRepository.GetById(id);
        }

        public IList<Activity> GetUserActivities(string userId)
        {
            var activites = _activityRepository.GetByUser(userId);
            return activites.ToList();
        }

        public IList<Activity> GetActivitiesByDateRange(DateTime from, DateTime to)
        {
            var activities = _activityRepository.GetActivitiesByDateRange(from, to);
            return activities.ToList();
        }

        public IList<Activity> GetUserActivitiesByDateRange(string userId, DateTime from, DateTime to)
        {
            var activities = _activityRepository.GetUserActivitiesByDateRange(userId, from, to);
            return activities.ToList();
        }

        public IList<Activity> GetActivitiesByDay(DateTime date)
        {
            var activities = _activityRepository.GetActivitiesByDay(date);
            return activities.ToList();
        }

        public IList<Activity> GetUserActivitiesByDay(string userId, DateTime date)
        {
            var activities = _activityRepository.GetUserActivitiesByDay(userId, date);
            return activities.ToList();
        }

        public IList<Activity> GetActivitiesByWeek(DateTime date)
        {
            var activities = _activityRepository.GetActivitiesByWeek(date);
            return activities.ToList();
        }

        public IList<Activity> GetUserActivitiesByWeek(string userId, DateTime date)
        {
            var activities = _activityRepository.GetUserActivitiesByWeek(userId, date);
            return activities.ToList();
        }

        public IList<Activity> GetActivitiesByMonth(DateTime date)
        {
            var activities = _activityRepository.GetActivitiesByMonth(date);
            return activities.ToList();
        }

        public IList<Activity> GetUserActivitiesByMonth(string userId, DateTime date)
        {
            var activities = _activityRepository.GetUserActivitiesByMonth(userId, date);
            return activities.ToList();
        }

        public IList<Activity> GetActivitiesByYear(int year)
        {
            var activities = _activityRepository.GetActivitiesByYear(year);
            return activities.ToList();
        }

        public IList<Activity> GetUserActivitiesByYear(string userId, int year)
        {
            var activities = _activityRepository.GetUserActivitiesByYear(userId, year);
            return activities.ToList();
        }

        public async Task<long> CreateActivity(Activity activity)
        {
            activity = _activityRepository.Create(activity);
            await _unit.Commit();
            await _feedEventService.GenerateFeedEvent(activity);
            await UpdateGoals(activity.UserId);
            _userBadgeService.CheckActivityBadgeProgress(activity.UserId, activity);
            await _unit.Commit();
            return activity.Id;
        }

        public ActivityType ClassifyActivity(Activity activity)
        {
            User user;
            if (activity.User == null)
            {
                user = _userService.FindUser(activity.UserId);
            }
            else
            {
                user = activity.User;
            }
            List<string> data = new List<string>()
            {
                user.BirthDate.Ticks.ToString(), GetUserHeightInMeters(user).ToString(), ConvertToKilograms(user.Weight).ToString(),
                activity.Duration.ToString(), (activity.Distance * MetersPerMile).ToString(), activity.Steps.ToString()
            };
            var typeStr = Predictor.predict(data);
            typeStr = typeStr.ToUpper();
            switch (typeStr)
            {
                case "R": return ActivityType.Running;
                case "J": return ActivityType.Jogging;
                case "B": return ActivityType.Biking;
                case "W": return ActivityType.Walking;
                case "O": return ActivityType.Other;
                default:  return ActivityType.Other;
            }
        }

        public void BuildForest()
        {
            ForestGeneration.createForest();
        }

        #region conversions
        private double GetUserHeightInMeters(User u)
        {
            var totalInches = u.HeightInches + (12*u.HeightFeet);
            return totalInches*MetersPerInch;
        }

        private double ConvertToKilograms(double lbs)
        {
            return lbs*KilogramsPerPound;
        }
        #endregion conversions

        public async Task UpdateActivity(Activity activity)
        {
            _activityRepository.Update(activity);
            await _unit.Commit();
            await UpdateGoals(activity.UserId);
        }

        private async Task UpdateGoals()
        {
            var goals = _goalService.GetGoals();
            await _goalService.AssessAllGoalProgress(goals);
            await _unit.Commit();
        }

        private async Task UpdateGoals(string userId)
        {
            var goals = _goalService.GetUserGoals(userId);
            await _goalService.AssessAllGoalProgress(goals);
            await _unit.Commit();
        }

        public async Task DeleteActivity(long id)
        {
            await _activityRepository.Delete(id);
            var feedEvents = _feedEventService.GetFeedEvents().Where(e => e.Type == EventType.Activity && e.EventId == id);
            foreach (FeedEvent f in feedEvents)
            {
                await _feedEventService.DeleteFeedEvent(f.Id);
            }
            await _unit.Commit();
            await UpdateGoals();
        }

        #region Disposing
        private bool _disposed;

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        ~ActivityService()
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