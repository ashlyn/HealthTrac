using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using HealthTrac.Data_Access;
using HealthTrac.Models;
using HealthTrac.Utilities;

namespace HealthTrac.Application.Services
{
    public class EndOfDayReportService : IEndOfDayReportService
    {
        private readonly IEndOfDayReportRepository _endOfDayReportRepository;
        private readonly IFeedEventService _feedEventService;
        private readonly IGoalService _goalService;
        private readonly IUserService _userService; 
        private readonly IActivityRepository _activityRepository;
        private readonly IUnitOfWork _unit;

        public EndOfDayReportService(IEndOfDayReportRepository endOfDayReportRepository, IFeedEventService feedEventService, IGoalService goalService, IUserService userService, IActivityRepository activityRepository, IUnitOfWork unit)
        {
            _endOfDayReportRepository = endOfDayReportRepository;
            _feedEventService = feedEventService;
            _goalService = goalService;
            _userService = userService;
            _activityRepository = activityRepository;
            _unit = unit;
        }

        public IList<EndOfDayReport> GetEndOfDayReports()
        {
            return _endOfDayReportRepository.ReadAll().ToList();
        }

        public async Task<EndOfDayReport> FindEndOfDayReport(long id)
        {
            return await _endOfDayReportRepository.GetById(id);
        }

        public IList<EndOfDayReport> GetEndOfDayReportsByUser(string id)
        {
            var reports = _endOfDayReportRepository.GetByUser(id);
            return reports;
        }

        public async Task<long> CreateEndOfDayReport(EndOfDayReport report)
        {
            _endOfDayReportRepository.Create(report);
            await _unit.Commit();
            await _feedEventService.GenerateFeedEvent(report);
            await _unit.Commit();
            return report.Id;
        }

        public EndOfDayReport CalculateEndOfDayReport(DateTime date, User user)
        {
            #region totalling
            var activities = _activityRepository.GetUserActivitiesByDay(user.Id, date);
            if (activities.Count() == 0)
            {
                var r = new EndOfDayReport()
                {
                    TotalDistance = 0,
                    TotalDuration = 0,
                    TotalSteps = 0,
                    UserId = user.Id,
                    Date = date
                };
                return r;
            }

            var totals = from a in activities
                         group a by 1
                             into act
                             select new
                             {
                                 totalDuration = act.Sum(x => x.Duration),
                                 totalDistance = act.Sum(x => x.Distance),
                                 totalSteps = act.Sum(x => x.Steps)
                             };
            totals = totals.ToArray();

            #endregion totalling

            var report = new EndOfDayReport()
            {
                TotalDistance = totals.Single().totalDistance,
                TotalDuration = totals.Single().totalDuration,
                TotalSteps = totals.Single().totalSteps,
                UserId = user.Id,
                Date = date
            };
            return report;
        }

        public async Task<EndOfDayReport> GenerateEndOfDayReport(DateTime date, User user)
        {
            var report = CalculateEndOfDayReport(date, user);
            await CreateEndOfDayReport(report);
            await _goalService.AssessAllGoalProgress(_goalService.GetUserGoals(user.Id));
            return report;
        }

        public async Task GenerateEndOfDayReports()
        {
            DateTime date = DateTime.UtcNow.LocalTime();
            if (!(date.TimeOfDay > new TimeSpan(23, 58, 0)))
            {
                date = date.AddDays(-1);
            }
            var users = _userService.GetUsers();
            foreach (User u in users)
            {
                await GenerateEndOfDayReport(date, u);
            }
        }

        public async Task UpdateEndOfDayReport(EndOfDayReport report)
        {
            _endOfDayReportRepository.Update(report);
            await _unit.Commit();
        }

        public async Task DeleteEndOfDayReport(long id)
        {
            await _endOfDayReportRepository.Delete(id);
            var feedEvents = _feedEventService.GetFeedEvents().Where(e => e.Type == EventType.EndOfDay && e.EventId == id);
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

        ~EndOfDayReportService()
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