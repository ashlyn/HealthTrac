using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using HealthTrac.Data_Access;
using HealthTrac.Models;
using HealthTrac.Utilities;

namespace HealthTrac.Application.Services
{
    public class GoalService : IGoalService
    {
        private readonly IGoalRepository _goalRepository;
        private readonly IActivityRepository _activityRepository;
        private readonly IFeedEventService _feedEventService;
        private readonly IUnitOfWork _unit;

        public GoalService(IGoalRepository goalRepository, IActivityRepository activityRepository, IFeedEventService feedEventService, IUnitOfWork unit)
        {
            _goalRepository = goalRepository;
            _activityRepository = activityRepository;
            _feedEventService = feedEventService;
            _unit = unit;
        }

        public IList<Goal> GetGoals()
        {
            return _goalRepository.ReadAll().ToList();
        }

        public async Task<Goal> FindGoal(long id)
        {
            return await _goalRepository.GetById(id);
        }

        public IList<Goal> GetUserGoals(string userId)
        {
            var goals = _goalRepository.GetByUser(userId);
            return goals.ToList();
        }

        public async Task<double> AssessGoalProgress(Goal goal)
        {
            if (goal.Target <= 0)
            {
                return 0;
            }
            #region totalling
            double total = 0;
            var today = DateTime.UtcNow.LocalTime();
            IEnumerable<Activity> activities = new List<Activity>();
            if (goal.TimeFrame == TimeFrame.Daily)
            {
                activities = _activityRepository.GetUserActivitiesByDay(goal.UserId, today);
            }
            //kind-of internationalized way to get first and last days of current week
            else if (goal.TimeFrame == TimeFrame.Weekly)
            {
                activities = _activityRepository.GetUserActivitiesByWeek(goal.UserId, today);
            }
            else if (goal.TimeFrame == TimeFrame.Monthly)
            {
                activities = _activityRepository.GetUserActivitiesByMonth(goal.UserId, today);
            }
            else if (goal.TimeFrame == TimeFrame.Yearly)
            {
                activities = _activityRepository.GetUserActivitiesByYear(goal.UserId, today.Year);
            }

            if (goal.Type == GoalType.Duration)
            {
                total = activities.Sum(d => d.Duration);
            }
            else if (goal.Type == GoalType.Distance)
            {
                total = activities.Sum(d => d.Distance);
            }
            else if (goal.Type == GoalType.Steps)
            {
                total = activities.Sum(s => s.Steps);
            }
            #endregion totalling

            var progress = total/goal.Target;
            if (progress < goal.Progress && progress < 1)
            {
                goal.Completed = false;
            }
            else if (progress >= 1 && !goal.Completed)
            {
                goal.Progress = progress;
                await _feedEventService.GenerateFeedEvent(goal);
                goal.Completed = true;
            }
            goal.Progress = progress;
            _goalRepository.Update(goal);
            return goal.Progress;
        }

        public async Task<double> AssessGoalProgress(long id)
        {
            var goal = await _goalRepository.GetById(id);
            return await AssessGoalProgress(goal);
        }

        public async Task AssessAllGoalProgress(IList<Goal> goals)
        {
            foreach (Goal goal in goals)
            {
                await AssessGoalProgress(goal);
            }
            await _unit.Commit();
        }

        public async Task<long> CreateGoal(Goal goal)
        {
            goal = _goalRepository.Create(goal);
            await _unit.Commit();
            await _feedEventService.GenerateFeedEvent(goal);
            await AssessGoalProgress(goal);
            await _unit.Commit();
            return goal.Id;
        }

        public async Task UpdateGoal(Goal goal)
        {
            _goalRepository.Update(goal);
            await AssessGoalProgress(goal);
            await _unit.Commit();
        }

        public async Task DeleteGoal(long id)
        {
            await _goalRepository.Delete(id);
            var feedEvents = _feedEventService.GetFeedEvents().Where(e => (e.Type == EventType.GoalSet || e.Type == EventType.GoalAchieved) && e.EventId == id);
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

        ~GoalService()
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