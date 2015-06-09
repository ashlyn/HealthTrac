using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using HealthTrac.Application;
using HealthTrac.Application.Services;
using HealthTrac.Data_Access;
using HealthTrac.Models;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace HealthTrac.Tests.M2Test
{
    [TestClass]
    public class UserBadgeServiceTest
    {
        private IUserBadgeService _userBadgeService;
        private IList<UserBadge> _userBadges;

        [TestInitialize]
        public void TestInit()
        {
            _userBadges = new List<UserBadge>
            {
                new UserBadge {Id = 0, UserId = "asdf", BadgeId = 1 },
                new UserBadge {Id = 1, UserId = "asdf", BadgeId = 3 },
                new UserBadge {Id = 2, UserId = "asdf", BadgeId = 2 },
                new UserBadge {Id = 3, UserId = "jkl;", BadgeId = 2 },
                new UserBadge {Id = 4, UserId = "jkl;", BadgeId = 1 }
            };

            var userBadgeRepo = new Mock<IUserBadgeRepository>();
            userBadgeRepo.Setup(m => m.ReadAll()).Returns(_userBadges);
            userBadgeRepo.Setup(m => m.GetById(It.IsAny<long>()))
                .Returns<long>(i => Task.FromResult(_userBadges.Single(x => x.Id == i)));
            userBadgeRepo.Setup(m => m.GetByUser(It.IsAny<string>()))
                .Returns<string>(i => _userBadges.Where(x => x.UserId == i).ToList());
            userBadgeRepo.Setup(m => m.Create(It.IsAny<UserBadge>()))
                .Returns(It.IsAny<UserBadge>);
            userBadgeRepo.Setup(m => m.Update(It.IsAny<UserBadge>()));

            var feedEventService = new Mock<IFeedEventService>();
            var membershipService = new Mock<IMembershipService>();
            var userRepo = new Mock<IUserRepository>();
            var unit = new Mock<IUnitOfWork>();

            _userBadgeService = new UserBadgeService(userBadgeRepo.Object, feedEventService.Object, userRepo.Object, unit.Object);
        }

        [TestMethod]
        public void GetAllUserBadges()
        {
            var userBadges = _userBadgeService.GetUserBadges();

            CollectionAssert.AreEqual(userBadges.ToArray(), _userBadges.ToArray());
        }

        [TestMethod]
        public void GetUserBadgeById()
        {
            long id = 2;
            var userBadge = _userBadgeService.FindUserBadge(id);

            Assert.AreEqual(userBadge.Result, _userBadges[(int)id]);
        }

        [TestMethod]
        public void GetUserBadgesByUser()
        {
            string userId = "asdf";
            var userBadges = _userBadgeService.GetUserBadges(userId).ToArray();
            CollectionAssert.AreEqual(userBadges.ToArray(), _userBadges.Where(m => m.UserId == userId).Select(b => b.Badge).ToArray());
        }


        [TestMethod]
        public async Task CreateUserBadge()
        {
            UserBadge b = new UserBadge { Id = 5, UserId = "qwerty", BadgeId = 2 };
            long bID = await _userBadgeService.CreateUserBadge(b);

            Assert.AreEqual(b.Id, bID);
        }
    }
}
