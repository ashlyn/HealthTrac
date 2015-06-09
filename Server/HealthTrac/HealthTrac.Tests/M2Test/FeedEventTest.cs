using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections;
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
    public class FeedEventTest
    {

        private IList<FeedEvent> _feedEventList;
        private FeedEventService _feedEventService;

        [TestInitialize]
        public void TestInit()
        {
            _feedEventList = new List<FeedEvent>
            {
                new FeedEvent { Id = 0, Description = "ran some distance", UserId = "user-123", Type = EventType.Activity},
                new FeedEvent { Id = 1, Description = "ate some food", Type = EventType.Food, UserId = "user-123" },
                new FeedEvent { Id = 2, Description = "set a new goal", Type = EventType.GoalSet, UserId = "user-456" }
            };

            var mockContext = new Mock<ApplicationDbContext>();

            var feedEventRepo = new Mock<IFeedEventRepository>();
            feedEventRepo.Setup(u => u.ReadAll()).Returns(_feedEventList);
            feedEventRepo.Setup(u => u.GetById(It.IsAny<long>()))
                .Returns<long>(i => Task.FromResult(_feedEventList.Where(x => x.Id == i).Single()));
            feedEventRepo.Setup(u => u.GetByUser(It.IsAny<string>()))
                .Returns<string>(i => _feedEventList.Where(x => x.UserId == i).ToList());

            var groupRepo = new Mock<IGroupRepository>();
            var membershipRepo = new Mock<IMembershipRepository>();
            var moodRepo = new Mock<IMoodRepository>();
            var badgeRepo = new Mock<IBadgeRepository>();
            var unitOfWork = new Mock<IUnitOfWork>();

            _feedEventService = new FeedEventService(feedEventRepo.Object, membershipRepo.Object, moodRepo.Object, groupRepo.Object, badgeRepo.Object, unitOfWork.Object);
        }

        [TestMethod]
        public void GetAllFeedEvents()
        {
            var feedEvents = _feedEventService.GetFeedEvents();

            Assert.AreEqual("set a new goal", feedEvents[2].Description);
            Assert.AreEqual(EventType.Activity, feedEvents[0].Type);
        }

        [TestMethod]
        public void GetFeedEventsByUserId()
        {
            string userId = "user-123";
            var feedEvents = _feedEventService.GetFeedEventsByUser(userId);
            
            Assert.AreEqual(userId, feedEvents[0].UserId);
            Assert.AreEqual("ate some food", feedEvents[1].Description);
        }

        [TestMethod]
        public void GetFeedEventById()
        {
            var id = 2;
            var feedEvent = _feedEventService.FindFeedEvent(id);

            Assert.AreEqual(feedEvent.Result, _feedEventList[2]);
        }
    }
}
