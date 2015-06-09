using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public interface IActivityService : IDisposable
    {
        IList<Activity> GetActivities();
        Task<Activity> FindActivity(long id);
        IList<Activity> GetUserActivities(string userId);
        ActivityType ClassifyActivity(Activity activity);
        void BuildForest();
        IList<Activity> GetActivitiesByDateRange(DateTime from, DateTime to);
        IList<Activity> GetUserActivitiesByDateRange(string userId, DateTime from, DateTime to);
        IList<Activity> GetActivitiesByDay(DateTime date);
        IList<Activity> GetUserActivitiesByDay(string userId, DateTime date);
        IList<Activity> GetActivitiesByWeek(DateTime date);
        IList<Activity> GetUserActivitiesByWeek(string userId, DateTime date);
        IList<Activity> GetActivitiesByMonth(DateTime date);
        IList<Activity> GetUserActivitiesByMonth(string userId, DateTime date);
        IList<Activity> GetActivitiesByYear(int year);
        IList<Activity> GetUserActivitiesByYear(string userId, int year);
        Task<long> CreateActivity(Activity activity);      
        Task UpdateActivity(Activity activity);
        Task DeleteActivity(long id);
    }
}
