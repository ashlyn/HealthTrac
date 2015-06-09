using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using HealthTrac.Application.Services;
using System.Collections;
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
    public class GoalTest
    {

        private GoalService _goalService;
        private IList<Goal> _goalList;

        [TestInitialize]
        public void TestInit()
        {
            _goalList = new List<Goal>
            {
                new Goal { Id = 0, Target = 1000, Type = GoalType.Duration },
                new Goal { Id = 1, Progress = .25, UserId = "user-123" },
                new Goal { Id = 2, Target = 1500, Progress = .5 }
            };

            var mockContext = new Mock<ApplicationDbContext>();

            var goalRepo = new Mock<IGoalRepository>();
            goalRepo.Setup(g => g.ReadAll()).Returns(_goalList);
            goalRepo.Setup(g => g.GetById(It.IsAny<long>()))
                .Returns<long>(i => Task.FromResult(_goalList.Where(x => x.Id == i).Single()));
            goalRepo.Setup(g => g.GetByUser(It.IsAny<string>()))
                .Returns<string>(i => _goalList.Where(x => x.UserId == i).ToList());

            var groupRepo = new Mock<IGroupRepository>();
            var activityRepo = new Mock<IActivityRepository>();
            var feedEventRepo = new Mock<IFeedEventRepository>();
            var membershipRepo = new Mock<IMembershipRepository>();
            var moodRepo = new Mock<IMoodRepository>();
            var badgeRepo = new Mock<IBadgeRepository>();
            var unitOfWork = new Mock<IUnitOfWork>();

            IFeedEventService feedService = new FeedEventService(feedEventRepo.Object, membershipRepo.Object, moodRepo.Object, groupRepo.Object, badgeRepo.Object, unitOfWork.Object);

            _goalService = new GoalService(goalRepo.Object, activityRepo.Object, feedService, unitOfWork.Object);
        }

        [TestMethod]
        public void GetAllGoals()
        {
            var goals = _goalService.GetGoals();

            Assert.AreEqual(1000, goals[0].Target);
            Assert.AreEqual(.25, goals[1].Progress);
        }

        [TestMethod]
        public void GetGoalById()
        {
            long id = 2;
            var goal = _goalService.FindGoal(id);

            Assert.AreEqual(_goalList[2], goal.Result);
        }

        [TestMethod]
        public void GetGoalByUserId()
        {
            string userId = "user-123";
            var goals = _goalService.GetUserGoals(userId);
            
            Assert.AreEqual(.25, goals[0].Progress);
        }
    }
}
