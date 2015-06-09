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
    public class BadgeServiceTest
    {
        private IBadgeService _badgeService;
        private IList<Badge> _badges;
        
        [TestInitialize]
        public void TestInit()
        {
            _badges = new List<Badge>
            {
                new Badge { Id = 1, Name = "Join the Club", Description = "Created a profile!", ImageUrl = @"http://i.imgur.com/YIXDwLt.png" },
                new Badge { Id = 2,  Name = "Baby's first steps", Description = "Walked more than 100 steps in a day!", ImageUrl = @"http://i.imgur.com/br2lJaz.png" },
                new Badge { Id = 3, Name = "Marathoner", Description = "Ran 26 miles in a day!", ImageUrl = @"http://i.imgur.com/Opd9NNi.png" },
                new Badge { Id = 4, Name = "Couch Potato", Description = "Taken less than 5000 steps a day for a week!", ImageUrl = @"http://i.imgur.com/1dzpKnL.png" },
                new Badge { Id = 5, Name = "Training Wheels", Description = "Biked 10 miles in one activity", ImageUrl = @"http://i.imgur.com/dMuJjsI.png" },
                new Badge { Id = 6, Name = "Lance Armstrong", Description = "Biked 100 miles in a day!", ImageUrl = @"http://i.imgur.com/FL4VwAG.png" },
                new Badge { Id = 7, Name = "Power Walker", Description = "Walk 10 miles in one activity", ImageUrl = @"http://i.imgur.com/axjjPIR.png" },
                new Badge { Id = 8, Name = "Yogging", Description = "Jogged 10 miles in one activity!", ImageUrl = @"http://i.imgur.com/iSzclz4.png" },
                new Badge { Id = 9, Name = "Groupie", Description = "Member of 10 or more groups", ImageUrl = @"http://i.imgur.com/AQ48QbM.png" },
                new Badge { Id = 10,  Name = "One of Us!", Description = "Joined a group!", ImageUrl = @"http://i.imgur.com/eD27Qz1.png"},
                new Badge { Id = 11, Name = "Look at me. I am the captain now", Description = "Became a Group Admin", ImageUrl = @"http://i.imgur.com/zA8rijq.png" }
            };

            var badgeRepo = new Mock<IBadgeRepository>();
            badgeRepo.Setup(g => g.ReadAll()).Returns(_badges);
            badgeRepo.Setup(g => g.GetById(It.IsAny<long>()))
                .Returns<long>(i => Task.FromResult(_badges.Single(x => x.Id == i)));
            badgeRepo.Setup(g => g.Create(It.IsAny<Badge>()))
                .Returns(It.IsAny<Badge>);
            badgeRepo.Setup(g => g.Update(It.IsAny<Badge>()));

            var unitOfWork = new Mock<IUnitOfWork>();

            _badgeService = new BadgeService(badgeRepo.Object, unitOfWork.Object);
        }

        [TestMethod]
        public void GetAllBadges()
        {
            var badges = _badgeService.GetBadges();

            CollectionAssert.AreEqual(badges.ToArray(), _badges.ToArray());
        }

        [TestMethod]
        public void GetBadgeFromId()
        {
            long id = 8;
            var point = _badgeService.FindBadge(id);

            Assert.AreEqual(point.Result, _badges[(int)id - 1]);
        }
        [TestMethod]
        public void CreateBadge()
        {
            Badge b = new Badge { Id = 12, Name = "Test Subject", Description = "You made a passing unit test!" };
            long bID = _badgeService.CreateBadge(b).Result;

            Assert.AreEqual(b.Id, bID);
        }
    }
}
