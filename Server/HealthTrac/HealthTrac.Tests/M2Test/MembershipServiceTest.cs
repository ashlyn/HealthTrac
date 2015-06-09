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
    public class MembershipServiceTest
    {
        private IMembershipService _membershipService;
        private IList<Membership> _memberships;

        [TestInitialize]
        public void TestInit()
        {
            _memberships = new List<Membership>
            {
                new Membership {Id = 0, UserId = "asdf", GroupId = 2, Status = Status.Member},
                new Membership {Id = 1, UserId = "asdf", GroupId = 1, Status = Status.Admin},
                new Membership {Id = 2, UserId = "asdf", GroupId = 3, Status = Status.Banned},
                new Membership {Id = 3, UserId = "jkl;", GroupId = 2, Status = Status.Admin},
                new Membership {Id = 4, UserId = "jkl;", GroupId = 1, Status = Status.Left}
            };

            var membershipRepo = new Mock<IMembershipRepository>();
            membershipRepo.Setup(m => m.ReadAll()).Returns(_memberships);
            membershipRepo.Setup(m => m.GetById(It.IsAny<long>()))
                .Returns<long>(i => Task.FromResult(_memberships.Single(x => x.Id == i)));
            membershipRepo.Setup(m => m.GetByUser(It.IsAny<string>()))
                .Returns<string>(i => _memberships.Where(x => x.UserId == i).ToList());
            membershipRepo.Setup(m => m.GetByGroup(It.IsAny<long>()))
                .Returns<long>(i => _memberships.Where(x => x.GroupId == i).ToList());
            membershipRepo.Setup(m => m.Create(It.IsAny<Membership>()))
                .Returns(It.IsAny<Membership>);
            membershipRepo.Setup(m => m.Update(It.IsAny<Membership>()));

            var feedEventService = new Mock<IFeedEventService>();
            var badgeService = new Mock<IUserBadgeService>();
            var unit = new Mock<IUnitOfWork>();

            _membershipService = new MembershipService(membershipRepo.Object, feedEventService.Object, badgeService.Object, unit.Object);
        }
        
        [TestMethod]
        public void GetAllMemberships()
        {
            var memberships = _membershipService.GetMemberships();

            CollectionAssert.AreEqual(memberships.ToArray(), _memberships.ToArray());
        }

        [TestMethod]
        public void GetMembershipById()
        {
            long id = 2;
            var membership = _membershipService.FindMembership(id);

            Assert.AreEqual(membership.Result, _memberships[(int)id]);
        }

        [TestMethod]
        public void GetMembershipsByUser()
        {
            string userId = "asdf";
            var memberships = _membershipService.GetUserMemberships(userId);

            CollectionAssert.AreEqual(memberships.ToArray(), _memberships.Where(m => m.UserId == userId).ToArray());
        }

        [TestMethod]
        public void GetMembershipsByGroup()
        {
            long groupId = 1;
            var memberships = _membershipService.GetGroupMemberships(groupId);

            CollectionAssert.AreEqual(memberships.ToArray(), _memberships.Where(m => m.GroupId == groupId).ToArray());
        }

        [TestMethod]
        public async Task CreateMembership()
        {
            Membership m = new Membership { Id = 5, UserId = "qwerty", GroupId = 3, Status = Status.Invited };
            long mID = await _membershipService.CreateMembership(m);

            Assert.AreEqual(m.Id, mID);
        }
    }
}
