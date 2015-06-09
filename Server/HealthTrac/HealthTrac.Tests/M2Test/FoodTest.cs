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
    public class FoodTest
    {
        private FoodService _foodService;
        private IList<Food> _foodList;

        [TestInitialize]
        public void TestInit()
        {
            _foodList = new List<Food>
            {
                new Food { Id = 0, Amount = 10, FoodName = "Food1" },
                new Food { Id = 1, Amount = 100, FoodName = "Food2" },
                new Food { Id = 2, Amount = 15, FoodName = "Food3" }
            };

            var mockContext = new Mock<ApplicationDbContext>();

            var foodRepo = new Mock<IFoodRepository>();
            foodRepo.Setup(f => f.ReadAll()).Returns(_foodList);
            foodRepo.Setup(f => f.GetById(It.IsAny<long>()))
                .Returns<long>(i => Task.FromResult(_foodList.Where(x => x.Id == i).Single()));

            var groupRepo = new Mock<IGroupRepository>();
            var feedEventRepo = new Mock<IFeedEventRepository>();
            var membershipRepo = new Mock<IMembershipRepository>();
            var moodRepo = new Mock<IMoodRepository>();
            var badgeRepo = new Mock<IBadgeRepository>();
            var unitOfWork = new Mock<IUnitOfWork>();

            IFeedEventService feedService = new FeedEventService(feedEventRepo.Object, membershipRepo.Object, moodRepo.Object, groupRepo.Object, badgeRepo.Object, unitOfWork.Object);

            _foodService = new FoodService(foodRepo.Object, feedService, unitOfWork.Object);
        }

        [TestMethod]
        public void GetAllFoods()
        {
            var foods = _foodService.GetFoods();

            Assert.AreEqual("Food1", foods[0].FoodName);
            Assert.AreEqual(100, foods[1].Amount);
            Assert.AreEqual(2, foods[2].Id);
        }

        [TestMethod]
        public void FindFoods()
        {
            long id = 1;

            var food = _foodService.FindFood(id);
            Assert.AreEqual(_foodList[1], food.Result);
        }

        [TestMethod]
        public void SearchFoods()
        {
            string query = "Food";
            var foods = _foodService.SearchForFoods(query);

            Assert.AreEqual("Food1", foods[0].FoodName);
            Assert.AreEqual("Food2", foods[1].FoodName);
        }
    }
}
