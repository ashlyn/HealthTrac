using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using HealthTrac.Application.Services;
using System.Collections.Generic;
using HealthTrac.Models;
using Moq;
using HealthTrac.Data_Access;
using HealthTrac.Application;
using System.Threading.Tasks;
using System.Linq;

namespace HealthTrac.Tests.M2Test
{
    [TestClass]
    public class UserServiceTest
    {
        IUserService userService;
        IList<User> listOfUsers;

        [TestInitialize]
        public void TestInit()
        {
            listOfUsers = new List<User>
            {
                new User{FullName = "Josh Dunne", PreferredName = "Josh", Gender = "Male", HeightFeet = 6, HeightInches = 9, Weight = 500, Email = "isthisreal@gmail.com"},
                new User{FullName = "Noah Gould", PreferredName = "Noah", Gender = "Male", HeightFeet = 6, HeightInches = 5, Weight = 400, Email = "noitisnt@gmail.com"},
                new User{FullName = "Ashlyn Lee", PreferredName = "Ashlyn", Gender = "Female", HeightFeet = 6, HeightInches = 3, Weight = 300, Email = "spoof@gmail.com"},
                new User{FullName = "Mike Casper", PreferredName = "Mike", Gender = "Male", HeightFeet = 6, HeightInches = 7, Weight = 200, Email = "lol@gmail.com"}

            };

            List<User> users = new List<User>(listOfUsers);

            var mockContext = new Mock<ApplicationDbContext>();

            var userRepo = new Mock<IUserRepository>();
            userRepo.Setup(g => g.ReadAll()).Returns(users);
            userRepo.Setup(g => g.GetById(It.IsAny<String>()))
                            .Returns<String>(i => Task.FromResult(listOfUsers.Where(x => x.Id.Equals(i)).Single()).Result);
            var groupRepo = new Mock<IGroupRepository>();
            var feedEventRepo = new Mock<IFeedEventRepository>();
            var membershipRepo = new Mock<IMembershipRepository>();
            var moodRepo = new Mock<IMoodRepository>();
            var badgeRepo = new Mock<IBadgeRepository>();
            var unitOfWork = new Mock<IUnitOfWork>();
            var userBadgeService = new Mock<IUserBadgeService>();

            IFeedEventService feedService = new FeedEventService(feedEventRepo.Object, membershipRepo.Object, moodRepo.Object, groupRepo.Object, badgeRepo.Object, unitOfWork.Object);
            IMembershipService membershipService = new MembershipService(membershipRepo.Object, feedService, userBadgeService.Object, unitOfWork.Object);
            userService = new UserService(userRepo.Object, membershipService, unitOfWork.Object);
        }


        [TestMethod]
        public void GetAllUsers() {
            var userCheck = userService.GetUsers();

            Assert.AreEqual(4, userCheck.Count);
            Assert.AreEqual("Josh Dunne", userCheck[0].FullName);
            Assert.AreEqual(300, userCheck[2].Weight);
            Assert.AreEqual("Mike", userCheck[3].PreferredName);

        }
        
        [TestMethod]
        public void FindUser()
        {
            var users = userService.GetUsers();
            var userId = users[0].Id;
            var userCheck = userService.FindUser(userId);

            Assert.AreEqual(users[0], userCheck);
        }

        [TestMethod]
        public void SearchForUser()
        {
            var users = userService.GetUsers();
            var user = users[0];
            var key = "Dunne";
            var userCheck = userService.SearchForUsers(key);

            Assert.AreEqual(user, userCheck[0]);
        }
    }
}
