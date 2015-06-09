using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using HealthTrac.Application.Services;
using HealthTrac.Models;
using System.Collections.Generic;
using Moq;
using HealthTrac.Data_Access;
using HealthTrac.Application;
using System.Threading.Tasks;
using System.Linq;

namespace HealthTrac.Tests.M2Test
{
    [TestClass]
    public class ActivityTest
    {
        IActivityService activityService;
        IList<Activity> activities;

        [TestInitialize]
        public void TestInit()
        {
            activities = new List<Activity> 
            {
                new Activity { Id = 1, Name="Walking", Duration = 800, UserId = "2a7f80bb-5e84-4468-88ad-804f848d8f20", StartTime = new DateTime(2014, 2, 15), Type = ActivityType.Walking, Distance = 1200, Steps = 430 },
                new Activity { Id = 2, Name="Walking", Duration = 500, UserId = "2a7f80bb-5e84-4468-88ad-804f848d8f20", StartTime = new DateTime(2015, 3, 15), Type = ActivityType.Walking, Distance = 900, Steps = 370 },
                new Activity { Id = 3, Name="Jogging", Duration = 1000, UserId = "2a7f80bb-5b36-4468-88ad-804f848d8f20", StartTime = new DateTime(2015, 3, 18), Type = ActivityType.Jogging, Distance = 1500, Steps = 480 },
                new Activity { Id = 4, Name="Biking", Duration = 1500, UserId = "2a7f80bb-5b36-4468-88ad-804f848d8f20", StartTime = new DateTime(2015, 4, 2), Type = ActivityType.Biking, Distance = 2000, Steps = 600 },
                new Activity { Id = 5, Name="Running", Duration = 400, UserId = "2a7f80bb-3r56-4468-88ad-804f848d8f20", StartTime = new DateTime(2015, 4, 8), Type = ActivityType.Running, Distance = 600, Steps = 300 },
            };

            var mockContext = new Mock<ApplicationDbContext>();
            var activityRepo = new Mock<IActivityRepository>();
            activityRepo.Setup(a => a.ReadAll()).Returns(activities);
            activityRepo.Setup(a => a.GetById(It.IsAny<long>()))
                .Returns<long>(i => Task.FromResult(activities.Where(x => x.Id == i).Single()));
            activityRepo.Setup(a => a.GetByUser(It.IsAny<string>()))
                .Returns<string>(i => activities.Where(x => x.UserId == i).ToList());
            activityRepo.Setup(a => a.GetActivitiesByDateRange(It.IsAny<DateTime>(), It.IsAny<DateTime>()))
                .Returns<DateTime, DateTime>( (i,j) => activities.Where(x => DateTime.Compare(x.StartTime, i) >= 0 && DateTime.Compare(x.StartTime, j) <= 0).ToList());
            activityRepo.Setup(a => a.GetUserActivitiesByDateRange(It.IsAny<string>(), It.IsAny<DateTime>(), It.IsAny<DateTime>()))
                .Returns<string,DateTime, DateTime>((s, i, j) => activities.Where(x => DateTime.Compare(x.StartTime, i) >= 0 && DateTime.Compare(x.StartTime, j) <= 0 && x.UserId == s).ToList());
            activityRepo.Setup(a => a.GetActivitiesByDay(It.IsAny<DateTime>()))
                .Returns<DateTime>(i => activities.Where(x => DateTime.Compare(x.StartTime, i) == 0).ToList());
            activityRepo.Setup(a => a.GetUserActivitiesByDay(It.IsAny<String>(),It.IsAny<DateTime>()))
                .Returns<string, DateTime>((s,i) => activities.Where(x => DateTime.Compare(x.StartTime, i) == 0 && x.UserId  == s).ToList());
            activityRepo.Setup(a => a.GetActivitiesByMonth(It.IsAny<DateTime>()))
                .Returns<DateTime>(i => activities.Where(x => x.StartTime.Month == i.Month).ToList());
            activityRepo.Setup(a => a.GetActivitiesByYear(It.IsAny<int>()))
                .Returns<int>(i => activities.Where(x => x.StartTime.Year == i).ToList());            

            var unitOfWork = new Mock<IUnitOfWork>();
            var goalRepo = new Mock<IGoalRepository>();
            var feedEventRepo = new Mock<IFeedEventRepository>();
            var membershipRepo = new Mock<IMembershipRepository>();
            var moodRepo = new Mock<IMoodRepository>();
            var groupRepo = new Mock<IGroupRepository>();
            var badgeRepo = new Mock<IBadgeRepository>();
            var userRepo = new Mock<IUserRepository>();
            var userBadgeService = new Mock<IUserBadgeService>();

            IFeedEventService feedService = new FeedEventService(feedEventRepo.Object, membershipRepo.Object, moodRepo.Object, groupRepo.Object, badgeRepo.Object, unitOfWork.Object);
            IGoalService goalService = new GoalService(goalRepo.Object, activityRepo.Object, feedService, unitOfWork.Object);
            IFeedEventService feedEventService = new FeedEventService(feedEventRepo.Object, membershipRepo.Object, moodRepo.Object, groupRepo.Object, badgeRepo.Object, unitOfWork.Object);            
            IMembershipService membershipService = new MembershipService(membershipRepo.Object, feedEventService, userBadgeService.Object, unitOfWork.Object);
            IUserService userService = new UserService(userRepo.Object, membershipService, unitOfWork.Object );
            activityService = new ActivityService(activityRepo.Object, goalService,feedEventService, userService, userBadgeService.Object, unitOfWork.Object);
        }

        [TestMethod]
        public void GetAllActivities()
        {
            var checkActivities = activityService.GetActivities();

            CollectionAssert.AreEqual(activities.ToArray(), checkActivities.ToArray());
        }

        [TestMethod]
        public void GetActivity()
        {
            var checkActivity = activityService.FindActivity(2);

            Assert.AreEqual(activities[1], checkActivity.Result);
        }

        [TestMethod]
        public void GetUserActivities()
        {
            string userId = "2a7f80bb-5e84-4468-88ad-804f848d8f20";
            var checkActivities = activityService.GetUserActivities(userId);
            IList<Activity> userActivities = new List<Activity> { activities[0], activities[1]};
            CollectionAssert.AreEqual(userActivities.ToArray(), checkActivities.ToArray());
        }

        [TestMethod]
        public void GetActivitiesByDateRange()
        {
            var checkActivities = activityService.GetActivitiesByDateRange(new DateTime(2015, 3, 14), new DateTime(2015, 4, 3));
            IList<Activity> userActivities = new List<Activity> { activities[1], activities[2], activities[3] };
            CollectionAssert.AreEqual(userActivities.ToArray(), checkActivities.ToArray());
        }

        [TestMethod]
        public void GetUserActivitiesByDateRange()
        {
            string userId = "2a7f80bb-5e84-4468-88ad-804f848d8f20";
            var checkActivities = activityService.GetUserActivitiesByDateRange(userId, new DateTime(2015, 3, 14), new DateTime(2015, 4, 3));
            IList<Activity> userActivities = new List<Activity> { activities[1] };
            CollectionAssert.AreEqual(userActivities.ToArray(), checkActivities.ToArray());
        }

        [TestMethod]
        public void GetActivitiesByDay()
        {
            var checkActivities = activityService.GetActivitiesByDay(new DateTime(2015, 4, 2));
            IList<Activity> userActivities = new List<Activity> { activities[3] };
            CollectionAssert.AreEqual(userActivities.ToArray(), checkActivities.ToArray());
        }

        [TestMethod]
        public void GetUserActivitiesByDay()
        {
            string userId = "2a7f80bb-5e84-4468-88ad-804f848d8f20";
            var checkActivities = activityService.GetUserActivitiesByDay(userId, new DateTime(2015, 3, 15));
            IList<Activity> userActivities = new List<Activity> { activities[1] };
            CollectionAssert.AreEqual(userActivities.ToArray(), checkActivities.ToArray());
        }

        [TestMethod]
        public void GetActivitiesByMonth()
        {
            var checkActivities = activityService.GetActivitiesByMonth(new DateTime(2015, 4, 2));
            IList<Activity> userActivities = new List<Activity> { activities[3], activities[4] };
            CollectionAssert.AreEqual(userActivities.ToArray(), checkActivities.ToArray());
        }

        [TestMethod]
        public void GetActivitiesByYear()
        {
            var checkActivities = activityService.GetActivitiesByYear(2015);
            IList<Activity> userActivities = new List<Activity> { activities[1], activities[2], activities[3], activities[4] };
            CollectionAssert.AreEqual(userActivities.ToArray(), checkActivities.ToArray());
        }
    }
}
