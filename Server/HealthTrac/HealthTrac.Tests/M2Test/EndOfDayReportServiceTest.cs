using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using HealthTrac.Application.Services;
using HealthTrac.Models;
using System.Collections.Generic;
using HealthTrac.Data_Access;
using Moq;
using HealthTrac.Application;
using System.Linq;
using System.Threading.Tasks;
namespace HealthTrac.Tests.M2Test
{
    [TestClass]
    public class EndOfDayReportServiceTest
    {
        IEndOfDayReportService eodService;
        IList<EndOfDayReport> listOfReports;

        [TestInitialize]
        public void TestInit()
        {
            listOfReports = new List<EndOfDayReport>
            {
                new EndOfDayReport{Id = 1, TotalDistance = 100, TotalDuration = 60, TotalSteps = 5, UserId = "yolo"},
                new EndOfDayReport{Id = 2, TotalDistance = 200, TotalDuration = 70, TotalSteps = 10, UserId = "swag"},
                new EndOfDayReport{Id = 3, TotalDistance = 300, TotalDuration = 80, TotalSteps = 15, UserId = "gg"},
                new EndOfDayReport{Id = 4, TotalDistance = 400, TotalDuration = 90, TotalSteps = 20, UserId = "nore"}

            };



            var mockContext = new Mock<ApplicationDbContext>();
            var eodRepo = new Mock<IEndOfDayReportRepository>();
            eodRepo.Setup(g => g.ReadAll()).Returns(listOfReports);
            eodRepo.Setup(g => g.GetById(It.IsAny<long>()))
                .Returns<long>(i => Task.FromResult(listOfReports.Where(x => x.Id.Equals(i)).Single()));
            eodRepo.Setup(g => g.GetByUser(It.IsAny<String>()))
                            .Returns<String>(i => listOfReports.Where(x => x.UserId == i).ToList());
            var activityRepo = new Mock<IActivityRepository>();
            var userRepo = new Mock<IUserRepository>();
            var groupRepo = new Mock<IGroupRepository>();
            var feedEventRepo = new Mock<IFeedEventRepository>();
            var membershipRepo = new Mock<IMembershipRepository>();
            var moodRepo = new Mock<IMoodRepository>();
            var badgeRepo = new Mock<IBadgeRepository>();
            var unitOfWork = new Mock<IUnitOfWork>();
            var goalRepo = new Mock<IGoalRepository>();
            var userBadgeService = new Mock<IUserBadgeService>();

            IFeedEventService feedService = new FeedEventService(feedEventRepo.Object, membershipRepo.Object, moodRepo.Object, groupRepo.Object, badgeRepo.Object, unitOfWork.Object);
            IMembershipService membershipService = new MembershipService(membershipRepo.Object, feedService, userBadgeService.Object, unitOfWork.Object);
            IUserService userService = new UserService(userRepo.Object, membershipService, unitOfWork.Object);
            IGoalService goalService = new GoalService(goalRepo.Object, activityRepo.Object, feedService, unitOfWork.Object);
            eodService = new EndOfDayReportService(eodRepo.Object, feedService, goalService, userService, activityRepo.Object, unitOfWork.Object);
        }


        [TestMethod]
        public void GetReports()
        {
            eodService.GenerateEndOfDayReports();
            var reports = eodService.GetEndOfDayReports();


            Assert.AreEqual(2, reports[1].Id);
            Assert.AreEqual(100, reports[0].TotalDistance);
            Assert.AreEqual(20, reports[3].TotalSteps);
            Assert.AreEqual("gg", reports[2].UserId);
        }

        [TestMethod]
        public void GetReportById()
        {
            var reports = eodService.FindEndOfDayReport(2);
            var duration = reports.Result.TotalDuration;

            Assert.AreEqual(70, duration);
        }

        [TestMethod]
        public void GetReportByUserId()
        {
            var reports = eodService.GetEndOfDayReportsByUser("yolo");
            var id = reports[0].Id;

            Assert.AreEqual(1, id);
        }

        [TestMethod]
        public void CreateReport()
        {
            var reports = eodService.GetEndOfDayReports();
            var id = eodService.CreateEndOfDayReport(reports[0]);


            Assert.AreEqual(1, id.Result);
        }

    }
}