using System;
using System.Linq;
using System.Threading.Tasks;
using System.Web.Mvc;
using HealthTrac.Application.Services;
using HealthTrac.Models;
using HealthTrac.Utilities;
using System.Collections.Generic;

namespace HealthTrac.Controllers
{
    public class ChartController : Controller
    {
        private readonly IActivityService _activityService;
        private readonly IEndOfDayReportService _endOfDayReportService;
        private readonly IUserService _userService;

        public ChartController(IActivityService activityService, IEndOfDayReportService endOfDayReportService, IUserService userService)
        {
            _activityService = activityService;
            _endOfDayReportService = endOfDayReportService;
            _userService = userService;
        }

        // GET: Chart
        public ActionResult ActivityPie(User user)
        {
            var activities = _activityService.GetUserActivitiesByMonth(user.Id, DateTime.UtcNow.LocalTime());
            var chart = ChartHelper.ActivityTypePie(activities);
            var sn = user.Logins.SingleOrDefault().LoginProvider == "Facebook";
            var vm = new ChartViewModel {Chart = chart.Item1, UsesFacebook = sn, Caption = chart.Item2};
            return PartialView("_ChartPartial", vm);
        }

        public ActionResult StepsChart(User user)
        {
            var eod = _endOfDayReportService.GetEndOfDayReportsByUser(user.Id).Where(e => e.Date.Date > DateTime.UtcNow.AddDays(-30)).ToList();
            var r = _endOfDayReportService.CalculateEndOfDayReport(DateTime.UtcNow.LocalTime(), user);
            eod.Add(r);
            var chart = ChartHelper.DailyStepChart(eod);
            var sn = user.Logins.SingleOrDefault().LoginProvider == "Facebook";
            var vm = new ChartViewModel { Chart = chart.Item1, UsesFacebook = sn, Caption = chart.Item2 };
            return PartialView("_ChartPartial", vm);
        }

        public ActionResult ActivityStacked(User user)
        {
            var date = DateTime.UtcNow.LocalTime();
            var activities = _activityService.GetUserActivitiesByDateRange(user.Id, date.AddDays(-30), date);
            var chart = ChartHelper.DailyActivityTypeChart(activities);
            var sn = user.Logins.SingleOrDefault().LoginProvider == "Facebook";
            var vm = new ChartViewModel { Chart = chart.Item1, UsesFacebook = sn, Caption = chart.Item2 };
            return PartialView("_ChartPartial", vm);
        }

        public ActionResult PastDay(User user)
        {
            var date = DateTime.UtcNow.LocalTime();
            var activities = _activityService.GetUserActivitiesByDay(user.Id, date);
            var chart = ChartHelper.ActivityByHourChart(activities);
            var sn = user.Logins.SingleOrDefault().LoginProvider == "Facebook";
            var vm = new ChartViewModel { Chart = chart.Item1, UsesFacebook = sn, Caption = chart.Item2 };
            return PartialView("_ChartPartial", vm);
        }
        public ActionResult Scatter(User user)
        {
            var chart = ChartHelper.DistanceStepsScatter(user.Activities.ToList());
            var sn = user.Logins.SingleOrDefault().LoginProvider == "Facebook";
            var vm = new ChartViewModel { Chart = chart.Item1, UsesFacebook = sn, Caption = chart.Item2 };
            return PartialView("_ChartPartial", vm);
        }

        public ActionResult StepsVsGroup(string userId, Group group)
        {
            var user = _userService.FindUser(userId);
            var groupUsers = _userService.GetGroupMembers(group.Id).ToList();
            List<EndOfDayReport> groupEod = new List<EndOfDayReport>();
            foreach (User u in groupUsers)
            {
                var eod = _endOfDayReportService.GetEndOfDayReportsByUser(u.Id).Where(e => e.Date.Date > DateTime.UtcNow.LocalTime().AddDays(-30));
                var r2 = _endOfDayReportService.CalculateEndOfDayReport(DateTime.UtcNow.LocalTime(), u);
                groupEod.AddRange(eod);
                groupEod.Add(r2);
            }

            var chart = ChartHelper.UserVsGroupSteps(userId, groupEod);
            var sn = user.Logins.SingleOrDefault().LoginProvider == "Facebook";
            var vm = new ChartViewModel { Chart = chart.Item1, UsesFacebook = sn, Caption = chart.Item2 };            
            return PartialView("_ChartPartial", vm);
        }

        public ActionResult GroupActivityStacked(string userId, Group group)
        {
            var user = _userService.FindUser(userId);
            var date = DateTime.UtcNow.LocalTime();
            var groupUsers = _userService.GetGroupMembers(group.Id);
            List<Activity> groupActivities = new List<Activity>();
            foreach (User u in groupUsers)
            {
                groupActivities.AddRange(_activityService.GetUserActivitiesByDateRange(u.Id, date.AddDays(-30), date));
            }
            var chart = ChartHelper.DailyGroupActivityTypeChart(userId, groupActivities, groupUsers.Count);
            var sn = user.Logins.SingleOrDefault().LoginProvider == "Facebook";
            var vm = new ChartViewModel { Chart = chart.Item1, UsesFacebook = sn, Caption = chart.Item2 };
            return PartialView("_ChartPartial", vm);
        }

        public ActionResult FriendActivityStacked(string userId, string friendId, string name)
        {
            var user = _userService.FindUser(userId);
            var date = DateTime.UtcNow.LocalTime();
            var userActivities = _activityService.GetUserActivitiesByDateRange(userId, date.AddDays(-30), date);
            var friendActivities = _activityService.GetUserActivitiesByDateRange(friendId, date.AddDays(-30), date);
            var chart = ChartHelper.DailyFriendActivityTypeChart(userActivities, friendActivities, name);
            var sn = user.Logins.SingleOrDefault().LoginProvider == "Facebook";
            var vm = new ChartViewModel { Chart = chart.Item1, UsesFacebook = sn, Caption = chart.Item2 };
            return PartialView("_ChartPartial", vm);
        }
    }
}