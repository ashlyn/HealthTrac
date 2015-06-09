using System.Linq;
using System.Threading.Tasks;
using System.Web.Mvc;
using DotNet.Highcharts;
using DotNet.Highcharts.Enums;
using DotNet.Highcharts.Helpers;
using DotNet.Highcharts.Options;
using HealthTrac.Application;
using HealthTrac.Application.Services;
using HealthTrac.Models;
using Microsoft.AspNet.Identity;
using System.Collections.Generic;
using HealthTrac.Utilities;
using System;

namespace HealthTrac.Controllers
{
    public class HomeController : Controller
    {
        private readonly IUserService _userService;
        private readonly IGoalService _goalService;
        private readonly IUnitOfWork _unit;

        public HomeController(IUserService userService, IGoalService goalService, IUnitOfWork unit)
        {
            _userService = userService;
            _goalService = goalService;
            _unit = unit;
        }

        public async Task<ActionResult> Index()
        {
            var user = _userService.FindUser(User.Identity.GetUserId());
            if (user != null)
            {
                await _goalService.AssessAllGoalProgress(user.Goals.ToList());
                await _unit.Commit();
            }
            return View(user);
        }

        public ActionResult About()
        {
            ViewBag.Message = "Your application description page.";

            return View();
        }

        public ActionResult Contact()
        {
            ViewBag.Message = "Your contact page.";

            return View();
        }

        /*public ActionResult ActivityTypeChart()
        {
            IList<Activity> activities = new List<Activity> 
            {
                new Activity { Id = 1, Name="Walking", Duration = 800, UserId = "2a7f80bb-5e84-4468-88ad-804f848d8f20", StartTime = new DateTime(2014, 2, 15), Type = ActivityType.Walking, Distance = 1200, Steps = 430 },
                new Activity { Id = 2, Name="Walking", Duration = 500, UserId = "2a7f80bb-5e84-4468-88ad-804f848d8f20", StartTime = new DateTime(2015, 3, 15), Type = ActivityType.Walking, Distance = 900, Steps = 370 },
                new Activity { Id = 3, Name="Jogging", Duration = 1000, UserId = "2a7f80bb-5b36-4468-88ad-804f848d8f20", StartTime = new DateTime(2015, 3, 18), Type = ActivityType.Jogging, Distance = 1500, Steps = 480 },
                new Activity { Id = 4, Name="Biking", Duration = 1500, UserId = "2a7f80bb-5b36-4468-88ad-804f848d8f20", StartTime = new DateTime(2015, 4, 2), Type = ActivityType.Biking, Distance = 2000, Steps = 600 },
                new Activity { Id = 5, Name="Running", Duration = 400, UserId = "2a7f80bb-3r56-4468-88ad-804f848d8f20", StartTime = new DateTime(2015, 4, 8), Type = ActivityType.Running, Distance = 600, Steps = 300 },
            };
            ViewBag.Message = "Pie chart of activities";
            Highcharts chart = ChartHelper.ActivityTypePie(activities);
            return PartialView();
        }*/

        public ActionResult GoalChart(Goal goal)
        {
            #region setup

            string timeFrame = "";
            switch (goal.TimeFrame)
            {
                case TimeFrame.Daily:
                    timeFrame = "day";
                    break;
                case TimeFrame.Weekly:
                    timeFrame = "week";
                    break;
                case TimeFrame.Monthly:
                    timeFrame = "month";
                    break;
                case TimeFrame.Yearly:
                    timeFrame = "year";
                    break;
            }
            string type = "";
            switch (goal.Type)
            {
                case GoalType.Steps:
                    type = "steps";
                    break;
                case GoalType.Duration:
                    type = "minutes";
                    break;
                case GoalType.Distance:
                    type = "miles";
                    break;
            }

            #endregion setup

            Highcharts chart = new Highcharts("chart")
                .InitChart(new Chart { PlotBorderWidth = 0, PlotShadow = false })
                .SetTitle(new Title
                {
                    Text = string.Format("{0} {1} each {2}", goal.Target, type, timeFrame),
                    Align = HorizontalAligns.Center,
                    VerticalAlign = VerticalAligns.Middle,
                    Y = 50
                })
                .SetTooltip(new Tooltip { PointFormat = "{series.name}: <b>{point.percentage:.1f}%</b>" })
                .SetPlotOptions(new PlotOptions
                {
                    Pie = new PlotOptionsPie
                    {
                        StartAngle = -90,
                        EndAngle = 90,
                        Center = new[] { new PercentageOrPixel(50, true), new PercentageOrPixel(75, true) },
                        DataLabels = new PlotOptionsPieDataLabels
                        {
                            Enabled = true,
                            Distance = -50,
                            Style = "fontWeight: 'bold', color: 'white', textShadow: '0px 1px 2px black'"

                        }
                    }
                })
                .SetSeries(new Series
                {
                    Type = ChartTypes.Pie,
                    Name = type,
                    PlotOptionsPie = new PlotOptionsPie { InnerSize = new PercentageOrPixel(50, true) },
                    Data = new Data(new object[]
                    {
                        new object[] {"Current Activity", goal.Progress*goal.Target},
                        new object[] {"Remaining Activity", 1 - (goal.Progress*goal.Target)}
                    })
                });
            return PartialView(chart);
        }
    }
}