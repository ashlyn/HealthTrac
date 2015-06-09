using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using HealthTrac.Application.Services;
using HealthTrac.Models;
using System.Collections.Generic;
using Moq;
using HealthTrac.Data_Access;
using System.Threading.Tasks;
using System.Linq;
using HealthTrac.Application;

namespace HealthTrac.Tests.M2Test
{
    [TestClass]
    public class UserMoodTest
    {

        private IUserMoodService _userMoodService;
        private IList<UserMood> _userMoodList;

        [TestInitialize]
        public void TestInit()
        {
            _userMoodList = new List<UserMood>
            {
                new UserMood { Id = 0, MoodId = 0, UserId = "user-123" },
                new UserMood { Id = 1, MoodId = 1, UserId = "user-123" },
                new UserMood { Id = 2, MoodId = 2, UserId = "user-456" }
            };

            var mockContext = new Mock<ApplicationDbContext>();

            var userMoodRepo = new Mock<IUserMoodRepository>();
            userMoodRepo.Setup(u => u.ReadAll()).Returns(_userMoodList);
            userMoodRepo.Setup(u => u.GetById(It.IsAny<long>()))
                .Returns<long>(i => Task.FromResult(_userMoodList.Where(x => x.Id == i).Single()));
            userMoodRepo.Setup(u => u.GetByUser(It.IsAny<string>()))
                .Returns<string>(i => _userMoodList.Where(x => x.UserId == i).ToList());

            var groupRepo = new Mock<IGroupRepository>();
            var feedEventRepo = new Mock<IFeedEventRepository>();
            var membershipRepo = new Mock<IMembershipRepository>();
            var moodRepo = new Mock<IMoodRepository>();
            var badgeRepo = new Mock<IBadgeRepository>();
            var unitOfWork = new Mock<IUnitOfWork>();

            IFeedEventService feedService = new FeedEventService(feedEventRepo.Object, membershipRepo.Object, moodRepo.Object, groupRepo.Object, badgeRepo.Object, unitOfWork.Object);

            _userMoodService = new UserMoodService(userMoodRepo.Object, feedService, unitOfWork.Object);
        }

        [TestMethod]
        public void GetAllUserMoodsTest()
        {
            var userMoods = _userMoodService.GetUserMoods();

            Assert.AreEqual(0, userMoods[0].Id);
            Assert.AreEqual(2, userMoods[2].MoodId);
            Assert.AreEqual("user-123", userMoods[1].UserId);
        }

        [TestMethod]
        public void GetUserMoodById()
        {
            var id = 2;
            var userMood = _userMoodService.FindUserMood(id);
            
            Assert.AreEqual(userMood.Result, _userMoodList[2]);
        }

        [TestMethod]
        public void GetUserMoodByUserId()
        {
            string userId = "user-123";
            var userMoods = _userMoodService.GetMoodsByUser(userId);

            Assert.AreEqual(userId, userMoods[0].UserId);
        }
    }
}
