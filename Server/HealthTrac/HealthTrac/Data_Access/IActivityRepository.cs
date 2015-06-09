using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public interface IActivityRepository
    {
        IList<Activity> ReadAll();
        Task<Activity> GetById(long id);
        IList<Activity> GetByUser(string userId);
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

        Activity Create(Activity a);
        void Update(Activity a);
        Task Delete(long id);
    }
}
