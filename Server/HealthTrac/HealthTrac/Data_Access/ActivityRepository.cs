using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Globalization;
using System.Linq;
using HealthTrac.Application;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public class ActivityRepository : Repository<Activity>, IActivityRepository
    {
        public ActivityRepository(IDbSetFactory dbSetFactory)
            :base(dbSetFactory)
        {
            _dbSet = dbSetFactory.CreateDbSet<Activity>();
            _dbSetFactory = dbSetFactory;
        }

        public IList<Activity> GetByUser(string userId)
        {
            var query = "SELECT * FROM dbo.Activities WHERE UserId = @user;";
            var user = new SqlParameter("@user", userId);
            object[] parameters = new object[] { user };
            var activities = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return activities.ToList();
        }

        public IList<Activity> GetActivitiesByDateRange(DateTime from, DateTime to)
        {
            //var activities = _dbSet.Where(a => a.StartTime >= from && a.StartTime <= to);
            var query = "SELECT * FROM dbo.Activities WHERE StartTime >= @from AND StartTime < @to;";
            var fromParam = new SqlParameter("@from", from);
            var toParam = new SqlParameter("@to", to);
            object[] parameters = new object[] { fromParam, toParam };
            var activities =_dbSet.SqlQuery(query, parameters).AsQueryable();
            return activities.ToList();
        }

        public IList<Activity> GetUserActivitiesByDateRange(string userId, DateTime from, DateTime to)
        {
            var query = "SELECT * FROM dbo.Activities WHERE StartTime >= @from AND StartTime < @to AND UserId = @user;";
            var fromParam = new SqlParameter("@from", from);
            var toParam = new SqlParameter("@to", to);
            var userParam = new SqlParameter("@user", userId);
            object[] parameters = new object[] { fromParam, toParam, userParam };
            var activities = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return activities.ToList();
        } 

        public IList<Activity> GetActivitiesByDay(DateTime date)
        {
            //var activites = _dbSet.Where(a => a.StartTime.DayOfYear == dayOfYear && a.StartTime.Year == year);
            var query = "SELECT * FROM dbo.Activities WHERE DATEPART(DAYOFYEAR, StartTime) = @day AND DATEPART(YEAR, StartTime) = @year;";
            var day = new SqlParameter("@day", date.DayOfYear);
            var year = new SqlParameter("@year", date.Year);
            object[] parameters = new object[] { day, year };
            var activities =_dbSet.SqlQuery(query, parameters).AsQueryable();
            return activities.ToList();
        }

        public IList<Activity> GetUserActivitiesByDay(string userId, DateTime date)
        {
            //var activites = _dbSet.Where(a => a.StartTime.DayOfYear == dayOfYear && a.StartTime.Year == year);
            var query = "SELECT * FROM dbo.Activities WHERE DATEPART(DAYOFYEAR, StartTime) = @day AND DATEPART(YEAR, StartTime) = @year AND UserId = @user;";
            var day = new SqlParameter("@day", date.DayOfYear);
            var year = new SqlParameter("@year", date.Year);
            var user = new SqlParameter("@user", userId);
            object[] parameters = new object[] { day, year, user };
            var activities = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return activities.ToList();
        }

        public IList<Activity> GetActivitiesByWeek(DateTime date)
        {
            var firstDay = date.AddDays(CultureInfo.CurrentCulture.DateTimeFormat.FirstDayOfWeek - date.DayOfWeek);
            firstDay -= firstDay.TimeOfDay;
            var activities = GetActivitiesByDateRange(firstDay, firstDay.AddDays(7));
            return activities.ToList();
        }

        public IList<Activity> GetUserActivitiesByWeek(string userId, DateTime date)
        {
            var firstDay = date.AddDays(CultureInfo.CurrentCulture.DateTimeFormat.FirstDayOfWeek - date.DayOfWeek);
            firstDay -= firstDay.TimeOfDay;
            var activities = GetUserActivitiesByDateRange(userId, firstDay, firstDay.AddDays(7));
            return activities.ToList();
        }

        public IList<Activity> GetActivitiesByMonth(DateTime date)
        {
            //var activities = _dbSet.Where(a => a.StartTime.Month == month && a.StartTime.Year == year);
            var query = "SELECT * FROM dbo.Activities WHERE DATEPART(MONTH, StartTime) = @month AND DATEPART(YEAR, StartTime) = @year;";
            var month = new SqlParameter("@month", date.Month);
            var year = new SqlParameter("@year", date.Year);
            object[] parameters = new object[] { month, year };
            var activities = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return activities.ToList();
        }

        public IList<Activity> GetUserActivitiesByMonth(string userId, DateTime date)
        {
            //var activities = _dbSet.Where(a => a.StartTime.Month == month && a.StartTime.Year == year);
            var query = "SELECT * FROM dbo.Activities WHERE DATEPART(MONTH, StartTime) = @month AND DATEPART(YEAR, StartTime) = @year AND UserId = @user;";
            var month = new SqlParameter("@month", date.Month);
            var year = new SqlParameter("@year", date.Year);
            var user = new SqlParameter("@user", userId);
            object[] parameters = new object[] { month, year, user };
            var activities = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return activities.ToList();
        }

        public IList<Activity> GetActivitiesByYear(int year)
        {
            //var activities = _dbSet.Where(a => a.StartTime.Year == year);
            var query = "SELECT * FROM dbo.Activities WHERE DATEPART(YEAR, StartTime) = @year;";
            var yearParam = new SqlParameter("@year", year);
            object[] parameters = new object[] { yearParam };
            var activities = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return activities.ToList();
        }

        public IList<Activity> GetUserActivitiesByYear(string userId, int year)
        {
            //var activities = _dbSet.Where(a => a.StartTime.Year == year);
            var query = "SELECT * FROM dbo.Activities WHERE DATEPART(YEAR, StartTime) = @year AND UserId = @user;";
            var yearParam = new SqlParameter("@year", year);
            var userParam = new SqlParameter("@user", userId);
            object[] parameters = new object[] { yearParam, userParam };
            var activities = _dbSet.SqlQuery(query, parameters).AsQueryable();
            return activities.ToList();
        } 
    }
}