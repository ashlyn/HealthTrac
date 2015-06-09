using System.Drawing;
using DotNet.Highcharts;
using DotNet.Highcharts.Enums;
using DotNet.Highcharts.Helpers;
using DotNet.Highcharts.Options;
using HealthTrac.Models;
using System.Collections.Generic;
using System.Linq;
using System;
using System.Globalization;

namespace HealthTrac.Utilities
{
    public static class ChartHelper
    {
        public static Highcharts CreateGoalChart(this Goal goal)
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

            var target = goal.Target;
            if (goal.Type == GoalType.Duration)
            {
                target /= 60;
            }

            #endregion setup

            #region chart
            Highcharts chart = new Highcharts("goal_" + goal.Id)
                .InitChart(new Chart { PlotBorderWidth = 0, PlotShadow = false, ClassName = "goal chart", Reflow = true})
                .SetTitle(new Title
                {
                    Text = string.Format("{0:0.##} {1} each {2}", target, type, timeFrame),
                    Align = HorizontalAligns.Center,
                    VerticalAlign = VerticalAligns.Bottom,
                    Y = -60
                })
                .SetCredits(new Credits
                {
                    Enabled = false
                })
                .SetTooltip(new Tooltip { PointFormat = "{series.name}: <b>{point.y:.1f} ({point.percentage:.1f}%)</b>" })
                .SetPlotOptions(new PlotOptions
                {
                    Pie = new PlotOptionsPie
                    {
                        StartAngle = -90,
                        EndAngle = 90,
                        Center = new[] { new PercentageOrPixel(50, true), new PercentageOrPixel(75, true) },
                        DataLabels = new PlotOptionsPieDataLabels
                        {
                            Enabled = false,
                            Distance = -50,
                            Style = "fontWeight: 'bold', color: 'white', textShadow: '0px 1px 2px black'"
                        },
                        Colors = new[]
                        {
                            ColorTranslator.FromHtml("#238795"),
                            ColorTranslator.FromHtml("#BDBDBD")
                        }
                    }
                })
                .SetOptions(new GlobalOptions
                {
                    Colors = new[]
                    {
                        ColorTranslator.FromHtml("#238795"),
                        ColorTranslator.FromHtml("#F44336"),
                        ColorTranslator.FromHtml("#558B2F"),
                        ColorTranslator.FromHtml("#F49136"),
                        ColorTranslator.FromHtml("#BDBDBD")
                    }
                })
                .SetSeries(new Series
                {
                    Type = ChartTypes.Pie,
                    Name = type,
                    PlotOptionsPie = new PlotOptionsPie { InnerSize = new PercentageOrPixel(50, true) },
                    Data = new Data(new object[]
                    {
                        new object[] {"Current Activity", goal.Progress * target},
                        new object[] {"Remaining Activity", (1 - goal.Progress) * target}
                    })
                });
            #endregion chart
            return chart;
        }

        public static Tuple<Highcharts, string> ActivityTypePie(IList<Activity> userActivities)
        {
            #region setup
            if (!userActivities.Any())
            {
                return new Tuple<Highcharts, string>(null, "You haven't worked out this month.");
            }
            var t = from a in userActivities
                         group a by 1
                             into act
                             select new
                             {
                                 Running = act.Count(a => a.Type == ActivityType.Running),
                                 Biking = act.Count(a => a.Type == ActivityType.Biking),
                                 Jogging = act.Count(a => a.Type == ActivityType.Jogging),
                                 Walking = act.Count(a => a.Type == ActivityType.Walking),
                                 Other = act.Count(a => a.Type == ActivityType.Other)
                             };
            var totals = t.SelectMany(prop => new[] {prop.Running, prop.Biking, prop.Jogging, prop.Walking, prop.Other}).ToList();
            var types = new[] {"Running", "Biking", "Jogging", "Walking", "Other"};
            #endregion setup

            #region chart
            Highcharts chart = new Highcharts("activity_type")
                .InitChart(new Chart { PlotBorderWidth = 0, PlotShadow = false, ClassName = "activityType chart", Reflow = true})
                .SetTitle(new Title
                {
                    Text = DateTime.UtcNow.LocalTime().ToString("MMMM", CultureInfo.InvariantCulture) + " Activities by Type",
                    Align = HorizontalAligns.Center,
                    Y = 15
                })
                .SetCredits(new Credits
                {
                    Enabled = false
                })
                .SetTooltip(new Tooltip { Formatter = "function() { return '<b>'+ this.point.name +'</b>: '+ Highcharts.numberFormat(this.percentage, 2) +' %'; }", ValueDecimals = 2})
                .SetOptions(new GlobalOptions
                {
                    Colors = new[]
                    {
                        ColorTranslator.FromHtml("#238795"),
                        ColorTranslator.FromHtml("#F44336"),
                        ColorTranslator.FromHtml("#558B2F"),
                        ColorTranslator.FromHtml("#F49136"),
                        ColorTranslator.FromHtml("#BDBDBD")
                    }
                })
                .SetPlotOptions(new PlotOptions
                {
                    Pie = new PlotOptionsPie
                    {
                        AllowPointSelect = true,
                        Cursor = Cursors.Pointer,
                        DataLabels = new PlotOptionsPieDataLabels
                        {
                            Enabled = false,
                            Color = ColorTranslator.FromHtml("#000000"),
                            ConnectorColor = ColorTranslator.FromHtml("#000000"),
                            Formatter = "function() { return '<b>'+ this.point.name +'</b>: '+ this.percentage +' %'; }"
                        }
                    }
                })
                .SetSeries(new Series
                {
                    Type = ChartTypes.Pie,
                    Name = "Activity Type",
                    Data = new Data(new object[] 
                    {
                        new object[] {"Running", totals[0]},
                        new object[] {"Biking", totals[1]},                        
                        new object[] {"Jogging", totals[2]},
                        new object[] { "Walking", totals[3]},
                        new object[] { "Other", totals[4]}
                    })
                });
            #endregion chart

            #region description
            string desc = "";
            if (userActivities.Count > 1)
            {
                var maxIndex = totals.Select((value, index) => new {Value = value, Index = index})
                    .Aggregate((a, b) => (a.Value > b.Value) ? a : b)
                    .Index;
                desc =
                    string.Format(
                        "{0} was your most common activity this month. You did {1} {0} activities this month.",
                        types[maxIndex], totals[maxIndex]);
            }
            else
            {
                desc = string.Format("You have only logged one activity this month. It was a {0} activity.",
                    userActivities.SingleOrDefault().Type);
            }
            #endregion description
            return new Tuple<Highcharts, string>(chart, desc);
        }

        public static Tuple<Highcharts, string> DailyStepChart(IList<EndOfDayReport> eodReports)
        {
            var dailySteps = eodReports.OrderBy(e => e.Date).Select(e => e.TotalSteps).ToList();
            if (!dailySteps.Any() || dailySteps.Sum() == 0)
            {
                return new Tuple<Highcharts, string>(null, "You haven't had activity.");
            }

            #region sma
            var sma = new int[dailySteps.Count];
            for (int i = 0; i < dailySteps.Count; i++)
            {
                if (i <= 4)
                {
                    var smaTemp = 0;
                    switch (i)
                    {
                        case 0:
                            smaTemp = dailySteps[i];
                            break;
                        case 1:
                            smaTemp = (dailySteps[i] + dailySteps[i - 1]) / 2;
                            break;
                        case 2:
                            smaTemp = (dailySteps[i] + dailySteps[i - 1] + dailySteps[i - 2]) / 3;
                            break;
                        case 3:
                            smaTemp = (dailySteps[i] + dailySteps[i - 1] + dailySteps[i - 2] + dailySteps[i - 3]) / 4;
                            break;
                        case 4:
                            smaTemp = (dailySteps[i] + dailySteps[i - 1] + dailySteps[i - 2] + dailySteps[i - 3] + dailySteps[i - 4]) / 5;
                            break;
                    }
                    sma[i] = smaTemp;
                }
                else
                {
                    var sumOfDays = 0;
                    for (int j = i - 5; j < i; j++)
                    {
                        sumOfDays += dailySteps[j];
                    }
                    sma[i] = sumOfDays / 5;
                }
            }

            var smaData = sma.Select(s => (object)s).ToArray();
            var dailyStepData = dailySteps.Select(s => (object) s).ToArray();
            var dates = eodReports.Select(e => e.Date.ToShortDateString()).ToArray();
            #endregion sma

            #region description
            string desc = "";
            if (eodReports[eodReports.Count - 1].TotalSteps < sma[sma.Count() - 1])
            {
                desc = string.Format("You're doing great! To keep on track, get {0} steps of activity today.",
                    sma[sma.Count() - 1]);
            }
            else
            {
                desc = string.Format("You're doing great! You exceeded your expected steps of {0} steps with {1} steps today.",
                    sma[sma.Count() - 1], eodReports[eodReports.Count - 1].TotalSteps);
            }
            #endregion description

            #region chart
            Highcharts chart = new Highcharts("step_chart")
                .InitChart(new Chart {PlotBorderWidth = 0, PlotShadow = false, ZoomType = ZoomTypes.Xy, ClassName = "step chart", Reflow = true})
                .SetTitle(new Title
                {
                    Text = "Steps Per Day",
                    X = -20
                })
                .SetXAxis(new XAxis { Categories = dates, TickInterval = Math.Max((int)(dates.Count() / 5), 1), ShowLastLabel = true })
                .SetYAxis(new YAxis
                {
                    Title = new YAxisTitle { Text = "Steps" },
                    PlotLines = new[]
                    {
                        new YAxisPlotLines
                        {
                            Value = 0,
                            Width = 1,
                            Color = ColorTranslator.FromHtml("#808080")
                        }
                    },
                    Min = 0
                })
                .SetOptions(new GlobalOptions
                {
                    Colors = new[]
                    {
                        ColorTranslator.FromHtml("#238795"),
                        ColorTranslator.FromHtml("#F44336"),
                        ColorTranslator.FromHtml("#558B2F"),
                        ColorTranslator.FromHtml("#F49136"),
                        ColorTranslator.FromHtml("#BDBDBD")
                    }
                })
                .SetCredits(new Credits
                {
                    Enabled = false
                })
                .SetTooltip(new Tooltip
                {
                    Formatter = @"function() { return this.y +' Steps';}"
                })
                .SetLegend(new Legend
                {
                    Layout = Layouts.Horizontal,
                    Align = HorizontalAligns.Center,
                    VerticalAlign = VerticalAligns.Bottom,
                    BorderWidth = 0
                })
                .SetSeries(new[]
                {
                    new Series 
                    { 
                        Type = ChartTypes.Column,
                        Name = "Steps", 
                        Data = new Data(dailyStepData) 
                    },
                    new Series 
                    { 
                        Type = ChartTypes.Spline,
                        Name = "Simple Moving Average", 
                        Data = new Data(smaData) 
                    }
                }
            );
            #endregion chart

            return new Tuple<Highcharts, string>(chart, desc);
        }

        public static Tuple<Highcharts, string> UserVsGroupSteps(string userId, List<EndOfDayReport> eodReports)
        {
            #region setup
            var myStepData = eodReports.OrderBy(e => e.Date).Where(e => e.UserId == userId).Select(e => e.TotalSteps);
             var userTotal = from e in eodReports
                orderby e.Date
                group e by ((DateTime) e.Date).Date
                into r
                select r.Where(e => e.UserId == userId).Select(e => (object) e.TotalSteps).FirstOrDefault();
            var mySteps = userTotal.ToArray();
            var groupStepData = eodReports.OrderBy(e => e.Date).GroupBy(g => g.Date.Date).Select(a => (int) a.Average(e => e.TotalSteps));

            //var mySteps = myStepData.Select(m => (object) m).ToArray();
            var groupSteps = groupStepData.Select(g => (object) g).ToArray();

            if ((!mySteps.Any() && !groupSteps.Any()) || (myStepData.Sum() == 0))
            {
                return new Tuple<Highcharts, string>(null, "You haven't had activity.");
            }
            var dates = eodReports.OrderBy(e => e.Date).Select(e => e.Date.ToShortDateString()).Distinct().ToArray();
            #endregion setup

            #region description
            string desc = "";
            var groupAvg = (int) groupStepData.Average();
            var userAvg = (int) myStepData.Average();
            if (groupAvg > userAvg)
            {
                 desc = string.Format("You are {0:#######0} steps below the average of {1:#######0} steps in this group. Step it up!", groupAvg - userAvg, groupAvg);
            }
            else if (groupAvg == userAvg)
            {
                desc = string.Format("Your step average is the same as the group's average of {0}", groupAvg);
            }
            else
            {
                 desc = string.Format("You are {0:#######0} steps above the average of {1:#######0} steps in this group. Keep on steppin'!", userAvg - groupAvg, groupAvg);
            }
            #endregion description

            #region chart
            Highcharts chart = new Highcharts("group_step")
                .InitChart(new Chart { PlotBorderWidth = 0, PlotShadow = false, ZoomType = ZoomTypes.Xy, ClassName = "groupStep chart", Reflow = true })
                .SetTitle(new Title
                {
                    Text = "Your Daily Steps vs. Group Average",
                    X = -20
                })
                .SetXAxis(new XAxis { Categories = dates, TickInterval = Math.Max((int)(dates.Count() / 5), 1), ShowLastLabel = true })
                .SetYAxis(new YAxis
                {
                    Title = new YAxisTitle { Text = "Steps" },
                    PlotLines = new[]
                    {
                        new YAxisPlotLines
                        {
                            Value = 0,
                            Width = 1,
                            Color = ColorTranslator.FromHtml("#808080")
                        }
                    },
                    Min = 0
                })
                .SetOptions(new GlobalOptions
                {
                    Colors = new[]
                    {
                        ColorTranslator.FromHtml("#238795"),
                        ColorTranslator.FromHtml("#F44336"),
                        ColorTranslator.FromHtml("#558B2F"),
                        ColorTranslator.FromHtml("#F49136"),
                        ColorTranslator.FromHtml("#BDBDBD")
                    }
                })
                .SetCredits(new Credits
                {
                    Enabled = false
                })
                .SetTooltip(new Tooltip
                {
                    Formatter = @"function() { return this.y +' Steps';}"
                })
                .SetLegend(new Legend
                {
                    Layout = Layouts.Horizontal,
                    Align = HorizontalAligns.Center,
                    VerticalAlign = VerticalAligns.Bottom,
                    BorderWidth = 0
                })
                .SetSeries(new[]
                {
                    new Series 
                    { 
                        Type = ChartTypes.Column,
                        Name = "My Steps", 
                        Data = new Data(mySteps) 
                    },
                    new Series 
                    { 
                        Type = ChartTypes.Spline,
                        Name = "Group Average Steps", 
                        Data = new Data(groupSteps) 
                    }
                }
            );
            #endregion chart

            return new Tuple<Highcharts, string>(chart, desc);
        }

        public static Tuple<Highcharts, string> DailyGroupActivityTypeChart(string userId, IList<Activity> groupActivities, int users)
        {
            #region setup
            if (!groupActivities.Any())
            {
                return new Tuple<Highcharts, string>(null, "You haven't worked out this month.");
            }

            var userTotal = from a in groupActivities
                        orderby a.StartTime
                        group a by a.StartTime.Date
                            into act
                            select new
                            {
                                Running = act.Where(a => a.Type == ActivityType.Running && a.UserId == userId).Sum(a => a.Duration / 60),
                                Biking = act.Where(a => a.Type == ActivityType.Biking && a.UserId == userId).Sum(a => a.Duration / 60),
                                Jogging = act.Where(a => a.Type == ActivityType.Jogging && a.UserId == userId).Sum(a => a.Duration / 60),
                                Walking = act.Where(a => a.Type == ActivityType.Walking && a.UserId == userId).Sum(a => a.Duration / 60),
                                Other = act.Where(a => a.Type == ActivityType.Other && a.UserId == userId).Sum(a => a.Duration / 60),
                                Sum = act.Where(a => a.UserId == userId).Sum(a => a.Duration / 60)
                            };

            var userGroups = from t in userTotal
                         group t by 1
                             into g
                             select new
                             {
                                 Running = g.Select(t => (object)t.Running).ToArray(),
                                 Biking = g.Select(t => (object)t.Biking).ToArray(),
                                 Jogging = g.Select(t => (object)t.Jogging).ToArray(),
                                 Walking = g.Select(t => (object)t.Walking).ToArray(),
                                 Other = g.Select(t => (object)t.Other).ToArray(),
                                 Sum = g.Select(t => (object)t.Sum).ToArray()
                             };

            var groupTotal = from a in groupActivities
                             orderby a.StartTime
                            group a by a.StartTime.Date
                                into act
                                select new
                                {
                                    Running = act.Where(a => a.Type == ActivityType.Running).Sum(a => a.Duration / 60) / users,
                                    Biking = act.Where(a => a.Type == ActivityType.Biking).Sum(a => a.Duration / 60) / users,
                                    Jogging = act.Where(a => a.Type == ActivityType.Jogging).Sum(a => a.Duration / 60) / users,
                                    Walking = act.Where(a => a.Type == ActivityType.Walking).Sum(a => a.Duration / 60) / users,
                                    Other = act.Where(a => a.Type == ActivityType.Other).Sum(a => a.Duration / 60) / users,
                                    Sum = act.Average(a => a.Duration / 60)
                                };

            var groupGroups = from t in groupTotal
                             group t by 1
                                 into g
                                 select new
                                 {
                                     Running = g.Select(t => (object)t.Running).ToArray(),
                                     Biking = g.Select(t => (object)t.Biking).ToArray(),
                                     Jogging = g.Select(t => (object)t.Jogging).ToArray(),
                                     Walking = g.Select(t => (object)t.Walking).ToArray(),
                                     Other = g.Select(t => (object)t.Other).ToArray(),
                                     Sum = g.Select(t => (object)t.Sum).ToArray()
                                 };

            var userTotals = userGroups.SelectMany(prop => new[] { prop.Running, prop.Biking, prop.Jogging, prop.Walking, prop.Other, prop.Sum }).ToArray();
            var groupTotals = groupGroups.SelectMany(prop => new[] { prop.Running, prop.Biking, prop.Jogging, prop.Walking, prop.Other, prop.Sum }).ToArray();
            var dates = groupActivities.OrderBy(e => e.StartTime).Select(a => a.StartTime.Date).Distinct().Select(d => d.ToShortDateString()).ToArray();
            #endregion setup

            #region chart
            Highcharts chart = new Highcharts("group_activity")
               .InitChart(new Chart { DefaultSeriesType = ChartTypes.Column, PlotBorderWidth = 0, PlotShadow = false, ClassName = "groupActivityType chart", Reflow = true })
               .SetTitle(new Title { Text = "30-Day Group Activity Summary" })
               .SetXAxis(new XAxis
               {
                   Categories = dates,
                   TickmarkPlacement = Placement.On
               })
               .SetYAxis(new YAxis
               {
                   Title = new YAxisTitle { Align = AxisTitleAligns.Middle, Text = "Minutes" }
               })
               .SetCredits(new Credits
               {
                   Enabled = false
               })
               .SetTooltip(new Tooltip { Formatter = @"function() { return this.series.name +': '+ Highcharts.numberFormat(this.y, 0)  + ' minutes'; }", ValueDecimals = 2 })
               .SetOptions(new GlobalOptions
               {
                   Colors = new[]
                    {
                        ColorTranslator.FromHtml("#238795"),
                        ColorTranslator.FromHtml("#F44336"),
                        ColorTranslator.FromHtml("#558B2F"),
                        ColorTranslator.FromHtml("#F49136"),
                        ColorTranslator.FromHtml("#BDBDBD"),

                        ColorTranslator.FromHtml("#76B3BC"),
                        ColorTranslator.FromHtml("#EF7371"),
                        ColorTranslator.FromHtml("#8DAE6F"),
                        ColorTranslator.FromHtml("#F0A764"),
                        ColorTranslator.FromHtml("#E6E6E6")
                    }
               })
               .SetLegend(new Legend
               {
                   Enabled = false
               })
               .SetPlotOptions(new PlotOptions
               {
                   Column = new PlotOptionsColumn
                   {
                       Stacking = Stackings.Normal
                   }
               })
               .SetSeries(new[]
                    {
                        new Series { Name = "You Running", Data = new Data(userTotals[0]), Stack = "User" },
                        new Series { Name = "You Biking", Data = new Data(userTotals[1]), Stack = "User" },
                        new Series { Name = "You Jogging", Data = new Data(userTotals[2]), Stack = "User" },
                        new Series { Name = "You Walking", Data = new Data(userTotals[3]), Stack = "User" },
                        new Series { Name = "You Other", Data = new Data(userTotals[4]), Stack = "User" },

                        new Series { Name = "Group Running", Data = new Data(groupTotals[0]), Stack = "Group" },
                        new Series { Name = "Group Biking", Data = new Data(groupTotals[1]), Stack = "Group" },
                        new Series { Name = "Group Jogging", Data = new Data(groupTotals[2]), Stack = "Group" },
                        new Series { Name = "Group Walking", Data = new Data(groupTotals[3]), Stack = "Group" },
                        new Series { Name = "Group Other", Data = new Data(groupTotals[4]), Stack = "Group" }
                  
                    });
            #endregion chart


            var desc = string.Format("Your group worked out an average of {0} minutes while you worked out an average of {1} minutes in the past 30 days.", 
                Math.Round(userTotal.Select(a => a.Sum).Average(), 2), Math.Round(groupTotal.Select(a => a.Sum).Average(), 2));
            return new Tuple<Highcharts, string>(chart, desc);
        }

        public static Tuple<Highcharts, string> DailyActivityTypeChart(IList<Activity> userActivities)
        {
            #region setup
            if (!userActivities.Any())
            {
                return new Tuple<Highcharts, string>(null, "You haven't worked out this month.");
            }

            var activities = userActivities.OrderBy(a => a.StartTime);
            var total = from a in activities
                    orderby a.StartTime
                    group a by a.StartTime.Date
                        into act
                        select new
                        {
                            Running = act.Where(a => a.Type == ActivityType.Running).Sum(a => a.Duration / 60),
                            Biking = act.Where(a => a.Type == ActivityType.Biking).Sum(a => a.Duration / 60),
                            Jogging = act.Where(a => a.Type == ActivityType.Jogging).Sum(a => a.Duration / 60),
                            Walking = act.Where(a => a.Type == ActivityType.Walking).Sum(a => a.Duration / 60),
                            Other = act.Where(a => a.Type == ActivityType.Other).Sum(a => a.Duration / 60),
                            Sum = act.Sum(a => a.Duration /60)
                        };

            var groups = from t in total
                            group t by 1
                            into g
                            select new
                            {
                                Running = g.Select(t => (object) t.Running).ToArray(),
                                Biking = g.Select(t => (object) t.Biking).ToArray(),
                                Jogging = g.Select(t => (object) t.Jogging).ToArray(),
                                Walking = g.Select(t => (object) t.Walking).ToArray(),
                                Other = g.Select(t => (object) t.Other).ToArray(), 
                                Sum = g.Select(t => (object) t.Sum).ToArray()
                            };
            var totals = groups.SelectMany(prop => new[] { prop.Running, prop.Biking, prop.Jogging, prop.Walking, prop.Other, prop.Sum }).ToArray();
            var dates = userActivities.OrderBy(a => a.StartTime).Select(a => a.StartTime.Date).Distinct().Select(d => d.ToShortDateString()).ToArray();
            #endregion setup

            #region chart
            Highcharts chart = new Highcharts("daily_activity")
               .InitChart(new Chart { DefaultSeriesType = ChartTypes.Column, PlotBorderWidth = 0, PlotShadow = false, ClassName = "dailyActivityType chart", Reflow = true })
               .SetTitle(new Title { Text = "30-Day Activity Summary" })
               .SetXAxis(new XAxis
               {
                   Categories = dates,
                   TickmarkPlacement = Placement.On
               })
               .SetYAxis(new YAxis
               {
                   Title = new YAxisTitle {Align = AxisTitleAligns.Middle, Text = "Minutes"}
               })
               .SetCredits(new Credits
               {
                   Enabled = false
               })
               .SetLegend(new Legend
               {
                   Layout = Layouts.Horizontal,
                   Align = HorizontalAligns.Center,
                   VerticalAlign = VerticalAligns.Bottom,
                   Floating = false,
                   X = 25,
                   BackgroundColor = new BackColorOrGradient(ColorTranslator.FromHtml("#FFFFFF")),
                   BorderColor = ColorTranslator.FromHtml("#CCC"),
                   BorderWidth = 0,
                   Shadow = false
               })
               .SetTooltip(new Tooltip { Formatter = @"function() { return this.series.name +': '+ Highcharts.numberFormat(this.y, 0)  + ' minutes'; }", ValueDecimals = 2})
               .SetOptions(new GlobalOptions
               {
                   Colors = new[]
                    {
                        ColorTranslator.FromHtml("#238795"),
                        ColorTranslator.FromHtml("#F44336"),
                        ColorTranslator.FromHtml("#558B2F"),
                        ColorTranslator.FromHtml("#F49136"),
                        ColorTranslator.FromHtml("#BDBDBD")
                    }
               })
               .SetPlotOptions(new PlotOptions
               {
                   Column = new PlotOptionsColumn
                   {
                       Stacking = Stackings.Normal
                   }
               })
               .SetSeries(new[]
                    {
                        new Series { Name = "Running", Data = new Data(totals[0]) },
                        new Series { Name = "Biking", Data = new Data(totals[1]) },
                        new Series { Name = "Jogging", Data = new Data(totals[2]) },
                        new Series { Name = "Walking", Data = new Data(totals[3]) },
                        new Series { Name = "Other", Data = new Data(totals[4]) }
                    });
            #endregion chart

            var maxIndex = totals[5].Select( (value, index) => new { Value = (double) value, Index = (int) index } )
                            .Aggregate( (a, b) => (a.Value > b.Value) ? a : b )
                            .Index;
            var desc = string.Format("On your most active day in the last 30 days, {0}, you worked out {1:###0} minutes.", dates[maxIndex], totals[5][maxIndex]);
            return new Tuple<Highcharts, string>(chart, desc);
        }

        private static string[] GetHours()
        {
            var am = Enumerable.Range(1, 12).ToArray().Select(i => i.ToString() + "am").ToList();
            var last = am.Last();
            am.Insert(0, last);
            am.RemoveAt(am.Count - 1);            
            var pm = am.Select(h => h.Replace("a", "p")).ToList();
            pm.Add(last);
            var hours = am.Concat(pm).ToArray();
            return hours;
        }

        //pass in list of activities from one day
        public static Tuple<Highcharts, string> ActivityByHourChart(IList<Activity> dayActivities)
        {
            #region setup
            if (!dayActivities.Any())
            {
                return new Tuple<Highcharts, string>(null, "You haven't worked out yet today.");
            }

            dayActivities = dayActivities.OrderBy(a => a.StartTime).ToList();
            double[] hourlyActivity = new double[25];
            foreach (Activity a in dayActivities)
            {
                DateTime endTime = a.StartTime.AddSeconds(a.Duration); //get end time of activity
                if (a.StartTime.Hour == endTime.Hour)
                {
                    hourlyActivity[a.StartTime.Hour] += (a.Duration/60);  //if activity was less than an hour, add duration converted to minutes
                }
                else
                {
                    hourlyActivity[a.StartTime.Hour] += (60 - a.StartTime.Minute); //first hour time is 60- start minute
                    double timeRemaining = a.Duration - ((60 - a.StartTime.Minute) * 60); //find remaining activity time
                    timeRemaining = timeRemaining / 60; //convert to minutes 
                    int j = a.StartTime.Hour + 1;
                    while (timeRemaining > 60)
                    {
                        hourlyActivity[j] = 60;
                        timeRemaining -= 60;
                        j++;
                    }
                    if (timeRemaining > 0)
                    {
                        hourlyActivity[j] += timeRemaining;
                    }

                }
            }
            hourlyActivity[24] = 0;
            var d = hourlyActivity.Select(a => (object) a).ToArray();
            var hours = GetHours();
            #endregion setup

            #region chart
            Highcharts chart = new Highcharts("hourly_activity")
                .InitChart(new Chart { DefaultSeriesType = ChartTypes.Column, PlotBorderWidth = 0, PlotShadow = false, ClassName = "hourly chart", Reflow = true })
                .SetTitle(new Title
                {
                    Text = "Minutes Per Day",
                    Align = HorizontalAligns.Center,
                    Floating = false
                })
                .SetCredits(new Credits
                {
                    Enabled = false
                })
                .SetLegend(new Legend
                {
                    Enabled = false
                })
                .SetXAxis(new XAxis { Categories = hours, TickInterval = 2, TickmarkPlacement = Placement.On })
                .SetYAxis(new YAxis
                {
                    Title = new YAxisTitle { Text = "Minutes" },
                    PlotLines = new[]
                    {
                        new YAxisPlotLines
                        {
                            Value = 0,
                            Width = 1,
                            Color = ColorTranslator.FromHtml("#808080")
                        }
                    },
                    Max = 60,
                    Min = 0
                })
                .SetOptions(new GlobalOptions
                {
                    Colors = new[]
                    {
                        ColorTranslator.FromHtml("#238795"),
                        ColorTranslator.FromHtml("#F44336"),
                        ColorTranslator.FromHtml("#558B2F"),
                        ColorTranslator.FromHtml("#F49136"),
                        ColorTranslator.FromHtml("#BDBDBD")
                    }
                })
                .SetTooltip(new Tooltip
                {
                    Formatter = @"function() { return this.x + ': ' + Highcharts.numberFormat(this.y, 0) + ' minutes';}",
                    ValueDecimals = 2
                })
                .SetSeries(new[]
                {
                    new Series 
                    { 
                        Type = ChartTypes.Column,
                        Name = "Hourly Activity", 
                        Data = new Data(d) 
                    },
                }
                );
#endregion chart

            var maxIndex = hourlyActivity.Select((value, index) => new { Value = value, Index = index })
                            .Aggregate((a, b) => (a.Value > b.Value) ? a : b)
                            .Index;
            var desc = string.Format("You were most active around {0} today with {1:#0} minutes of activity.", hours[maxIndex], hourlyActivity[maxIndex]);
            return new Tuple<Highcharts, string>(chart, desc);
        }

        public static Tuple<Highcharts, string> DistanceStepsScatter(IList<Activity> activities)
        {
            #region setup
            if (!activities.Any())
            {
                return new Tuple<Highcharts, string>(null, "You haven't worked out yet today.");
            }

            var n = activities.Count();
            int steps = 0;
            double distance = 0;
            object[,] data = new object[n,2];
            for (int i = 0; i < n; i++)
            {
                data[i, 0] = activities[i].Steps;
                data[i, 1] = activities[i].Distance;

                steps += activities[i].Steps;
                distance += activities[i].Distance;
            }

            string desc = "";
            if (distance == 0)
            {
                desc = "You haven't logged any distance yet. Get moving!";
            }
            else
            {
                var avg = steps/distance;
                desc = string.Format("On average, you take {0:####0} steps per mile.", avg);
            }            
            #endregion setup

            #region chart
            Highcharts chart = new Highcharts("scatter")
                .InitChart(new Chart { DefaultSeriesType = ChartTypes.Scatter, ZoomType = ZoomTypes.Xy, ClassName = "scatter chart", Reflow = true})
                .SetTitle(new Title { Text = "Steps vs. Distance" })
                .SetYAxis(new YAxis { Title = new YAxisTitle { Text = "Distance (mi)" }, Min = 0 })
                .SetXAxis(new XAxis
                {
                    Title = new XAxisTitle { Text = "Steps" },
                    StartOnTick = true,
                    EndOnTick = true,
                    ShowLastLabel = true,
                    Min = 0
                })
                .SetOptions(new GlobalOptions
                {
                    Colors = new[]
                    {
                        ColorTranslator.FromHtml("#238795"),
                        ColorTranslator.FromHtml("#F44336"),
                        ColorTranslator.FromHtml("#558B2F"),
                        ColorTranslator.FromHtml("#F49136"),
                        ColorTranslator.FromHtml("#BDBDBD")
                    }
                })
                .SetTooltip(new Tooltip { Formatter = "function() {return ''+ this.x +' steps, '+ this.y +' miles'; }" })
                .SetCredits(new Credits
                {
                    Enabled = false
                })
                .SetLegend(new Legend
                {
                    Enabled = false
                })
                .SetSeries(new[] { new Series
                                      {
                                          Name = "Activities",
                                          Data = new Data(data)
                                      }
                });
            #endregion chart

            return new Tuple<Highcharts, string>(chart, desc);
        }

        public static Tuple<Highcharts, string> DailyFriendActivityTypeChart(IList<Activity> userActivities, IList<Activity> friendActivities, string name)
        {
            #region setup
            if (!userActivities.Any() || !friendActivities.Any())
            {
                return new Tuple<Highcharts, string>(null, "You haven't worked out this month.");
            }

            var userTotal = from a in userActivities
                            orderby a.StartTime
                            group a by a.StartTime.Date
                                into act
                                select new
                                {
                                    Running = act.Where(a => a.Type == ActivityType.Running).Sum(a => a.Duration / 60),
                                    Biking = act.Where(a => a.Type == ActivityType.Biking).Sum(a => a.Duration / 60),
                                    Jogging = act.Where(a => a.Type == ActivityType.Jogging).Sum(a => a.Duration / 60),
                                    Walking = act.Where(a => a.Type == ActivityType.Walking).Sum(a => a.Duration / 60),
                                    Other = act.Where(a => a.Type == ActivityType.Other).Sum(a => a.Duration / 60),
                                    Sum = act.Sum(a => a.Duration / 60)
                                };

            var userGroups = from t in userTotal
                             group t by 1
                                 into g
                                 select new
                                 {
                                     Running = g.Select(t => (object)t.Running).ToArray(),
                                     Biking = g.Select(t => (object)t.Biking).ToArray(),
                                     Jogging = g.Select(t => (object)t.Jogging).ToArray(),
                                     Walking = g.Select(t => (object)t.Walking).ToArray(),
                                     Other = g.Select(t => (object)t.Other).ToArray(),
                                     Sum = g.Select(t => (object)t.Sum).ToArray()
                                 };

            var friendTotal = from a in friendActivities
                             orderby a.StartTime
                             group a by a.StartTime.Date
                                 into act
                                 select new
                                 {
                                     Running = act.Where(a => a.Type == ActivityType.Running).Sum(a => a.Duration / 60),
                                     Biking = act.Where(a => a.Type == ActivityType.Biking).Sum(a => a.Duration / 60),
                                     Jogging = act.Where(a => a.Type == ActivityType.Jogging).Sum(a => a.Duration / 60),
                                     Walking = act.Where(a => a.Type == ActivityType.Walking).Sum(a => a.Duration / 60),
                                     Other = act.Where(a => a.Type == ActivityType.Other).Sum(a => a.Duration / 60),
                                     Sum = act.Average(a => a.Duration / 60)
                                 };

            var friendGroups = from t in friendTotal
                              group t by 1
                                  into g
                                  select new
                                  {
                                      Running = g.Select(t => (object)t.Running).ToArray(),
                                      Biking = g.Select(t => (object)t.Biking).ToArray(),
                                      Jogging = g.Select(t => (object)t.Jogging).ToArray(),
                                      Walking = g.Select(t => (object)t.Walking).ToArray(),
                                      Other = g.Select(t => (object)t.Other).ToArray(),
                                      Sum = g.Select(t => (object)t.Sum).ToArray()
                                  };

            var userTotals = userGroups.SelectMany(prop => new[] { prop.Running, prop.Biking, prop.Jogging, prop.Walking, prop.Other, prop.Sum }).ToArray();
            var friendTotals = friendGroups.SelectMany(prop => new[] { prop.Running, prop.Biking, prop.Jogging, prop.Walking, prop.Other, prop.Sum }).ToArray();
            var dates = friendActivities.Concat(userActivities).OrderBy(e => e.StartTime).Select(a => a.StartTime.Date).Distinct().Select(d => d.ToShortDateString()).ToArray();
            #endregion setup

            #region chart
            Highcharts chart = new Highcharts("friend_activity")
               .InitChart(new Chart { DefaultSeriesType = ChartTypes.Column, PlotBorderWidth = 0, PlotShadow = false, ClassName = "friendActivityType chart", Reflow = true })
               .SetTitle(new Title { Text = string.Format("You vs. {0} 30-Day Activity Summary", name) })
               .SetXAxis(new XAxis
               {
                   Categories = dates,
                   TickmarkPlacement = Placement.On
               })
               .SetYAxis(new YAxis
               {
                   Title = new YAxisTitle { Align = AxisTitleAligns.Middle, Text = "Minutes" }
               })
               .SetCredits(new Credits
               {
                   Enabled = false
               })
               .SetTooltip(new Tooltip { Formatter = @"function() { return this.series.name +': '+ Highcharts.numberFormat(this.y, 0)  + ' minutes'; }", ValueDecimals = 2 })
               .SetOptions(new GlobalOptions
               {
                   Colors = new[]
                    {
                        ColorTranslator.FromHtml("#238795"),
                        ColorTranslator.FromHtml("#F44336"),
                        ColorTranslator.FromHtml("#558B2F"),
                        ColorTranslator.FromHtml("#F49136"),
                        ColorTranslator.FromHtml("#BDBDBD"),

                        ColorTranslator.FromHtml("#76B3BC"),
                        ColorTranslator.FromHtml("#EF7371"),
                        ColorTranslator.FromHtml("#8DAE6F"),
                        ColorTranslator.FromHtml("#F0A764"),
                        ColorTranslator.FromHtml("#E6E6E6")
                    }
               })
               .SetLegend(new Legend
               {
                   Enabled = false
               })
               .SetPlotOptions(new PlotOptions
               {
                   Column = new PlotOptionsColumn
                   {
                       Stacking = Stackings.Normal
                   }
               })
               .SetSeries(new[]
                    {
                        new Series { Name = "You Running", Data = new Data(userTotals[0]), Stack = "User" },
                        new Series { Name = "You Biking", Data = new Data(userTotals[1]), Stack = "User" },
                        new Series { Name = "You Jogging", Data = new Data(userTotals[2]), Stack = "User" },
                        new Series { Name = "You Walking", Data = new Data(userTotals[3]), Stack = "User" },
                        new Series { Name = "You Other", Data = new Data(userTotals[4]), Stack = "User" },

                        new Series { Name = name + " Running", Data = new Data(friendTotals[0]), Stack = "Friend" },
                        new Series { Name = name + " Biking", Data = new Data(friendTotals[1]), Stack = "Friend" },
                        new Series { Name = name + " Jogging", Data = new Data(friendTotals[2]), Stack = "Friend" },
                        new Series { Name = name + " Walking", Data = new Data(friendTotals[3]), Stack = "Friend" },
                        new Series { Name = name + " Other", Data = new Data(friendTotals[4]), Stack = "Friend" }
                  
                    });
            #endregion chart


            var desc = string.Format("{0} worked out an average of {1} minutes while you worked out an average of {2} minutes in the past 30 days.", name,
                Math.Round(friendTotal.Select(a => a.Sum).Average(), 2), Math.Round(userTotal.Select(a => a.Sum).Average(), 2));
            return new Tuple<Highcharts, string>(chart, desc);
        }
    }
}