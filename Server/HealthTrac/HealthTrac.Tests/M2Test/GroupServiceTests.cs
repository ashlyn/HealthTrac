using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using System.Data.Entity;
using HealthTrac.Models;
using HealthTrac.Application;
using HealthTrac.Application.Services;
using HealthTrac.Data_Access;
using System.Threading.Tasks;
using System.Collections.Generic;
using System.Linq;

namespace HealthTrac.Tests.M2Test
{
    [TestClass]
    public class GroupServiceTests
    {
        IGroupService groupService;
        IList<Group> listOfGroups;

        [TestInitialize]
        public void TestInit()
        {
            listOfGroups = new List<Group> 
            {
                new Group { GroupName = "Runnrs4Lyfe", Description = "Friendly group of lifetime runners (50+)", ImageUrl = @"http://i.imgur.com/A7F8ZDp.png", GroupMembers = new List<Membership> { }, Id = 0 },
                new Group { GroupName = "Pizza team", Description = "We are teh pizza eaters", ImageUrl = @"http://i.imgur.com/8u90xhH.png", GroupMembers = new List<Membership> { }, Id = 1 },
                new Group { GroupName = "Team Team", Description = "Team team Team Team", ImageUrl = @"http://i.imgur.com/aidzS63.png", GroupMembers = new List<Membership> { }, Id = 2 },
                new Group { GroupName = "Cheer for life!", Description = "Lyk dis if u cheer evertim", ImageUrl = @"http://i.imgur.com/cawYNKA.png", GroupMembers = new List<Membership> { }, Id = 3 },
                new Group { GroupName = "Fight Club", Description = "You don't talk about it", ImageUrl = @"http://i.imgur.com/1xXBpoV.png", GroupMembers = new List<Membership> { }, Id = 4 },
            };

            List<Group> groups = new List<Group>(listOfGroups);

            //var mockSet = new Mock<DbSet<Group>>();

            var mockContext = new Mock<ApplicationDbContext>();

            var groupRepo = new Mock<IGroupRepository>();
            groupRepo.Setup(g => g.ReadAll()).Returns(listOfGroups);
            groupRepo.Setup(g => g.GetById(It.IsAny<long>()))
                .Returns<long>(i => Task.FromResult(listOfGroups.Where(x => x.Id == i).Single()));
            var feedEventRepo = new Mock<IFeedEventRepository>();
            var membershipRepo = new Mock<IMembershipRepository>();
            var moodRepo = new Mock<IMoodRepository>();
            var badgeRepo = new Mock<IBadgeRepository>();
            var unitOfWork = new Mock<IUnitOfWork>();
            var userBadgeService = new Mock<IUserBadgeService>();

            IFeedEventService feedService = new FeedEventService(feedEventRepo.Object, membershipRepo.Object, moodRepo.Object, groupRepo.Object, badgeRepo.Object, unitOfWork.Object);
            IMembershipService membershipService = new MembershipService(membershipRepo.Object, feedService, userBadgeService.Object, unitOfWork.Object);
            groupService = new GroupService(groupRepo.Object, membershipService, feedService, unitOfWork.Object);
        }

        [TestMethod]
        public void GetAllGroups()
        {
            var groupCheck = groupService.GetGroups();

            Assert.AreEqual(5, groupCheck.Count);
            Assert.AreEqual("Runnrs4Lyfe", groupCheck[0].GroupName);
            Assert.AreEqual("Team Team", groupCheck[2].GroupName);
            Assert.AreEqual("You don't talk about it", groupCheck[4].Description);
        }

        [TestMethod]
        public void GetGroupById()
        {
            long id = 3;

            var groupCheck = groupService.FindGroup(id).Result;
            Assert.AreEqual(listOfGroups[3], groupCheck);
        }
        [TestMethod]
        public void GroupSearch()
        {
            string key = "team";

            var groupCheck = groupService.Search(key);
            IList<Group> groupsWithThe = new List<Group>();
            groupsWithThe.Add(listOfGroups[1]);
            groupsWithThe.Add(listOfGroups[2]);


            CollectionAssert.AreEqual(groupsWithThe.ToArray(), groupCheck.ToArray());

        }
    }
}