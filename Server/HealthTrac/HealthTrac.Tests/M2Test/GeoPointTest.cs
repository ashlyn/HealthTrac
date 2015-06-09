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
    public class GeoPointTest
    {
        IGeoPointService geoPointService;
        IList<GeoPoint> geoPoints;

        [TestInitialize]
        public void TestInit() 
        {
            geoPoints = new List<GeoPoint>
            {
                new GeoPoint { Id = 0, ActivityId = 0, Latitude = 69.9, Longitude = 420.4},
                new GeoPoint { Id = 1, ActivityId = 1, Latitude = 12, Longitude = 24},
                new GeoPoint { Id = 2, ActivityId = 0, Latitude = 68, Longitude = 419}
            };

            var mockContext = new Mock<ApplicationDbContext>();

            var geoPointRepo = new Mock<IGeoPointRepository>();
            geoPointRepo.Setup(g => g.ReadAll()).Returns(geoPoints);
            geoPointRepo.Setup( g => g.GetById(It.IsAny<long>()))
                .Returns<long>( i => Task.FromResult(geoPoints.Where( x => x.Id == i).Single()));
            geoPointRepo.Setup(g => g.Create(It.IsAny<GeoPoint>()))
                .Returns(It.IsAny<GeoPoint>);
            //geoPointRepo.Setup(g => g.Update(It.IsAny<GeoPoint>()));
            var feedEventRepo = new Mock<IFeedEventRepository>();
            var membershipRepo = new Mock<IMembershipRepository>();
            var moodRepo = new Mock<IMoodRepository>();
            var badgeRepo = new Mock<IBadgeRepository>();
            var unitOfWork = new Mock<IUnitOfWork>();

            geoPointService = new GeoPointService(geoPointRepo.Object, unitOfWork.Object);
        }

        [TestMethod]
        public void GetAllGeoPoints()
        {
            var points = geoPointService.GetGeoPoints();

            Assert.AreEqual(69.9, points[0].Latitude);
            Assert.AreEqual(1, points[1].Id);
        }

        [TestMethod]
        public void GetGeoPointFromId()
        {
            long id = 0;
            var point = geoPointService.FindGeoPoint(id);

            Assert.AreEqual(point.Result, geoPoints[0]);
        }
        [TestMethod]
        public void CreateGeoPoint()
        {
            GeoPoint g = new GeoPoint { Id = 3, ActivityId = 2, Latitude = 36, Longitude = 37 };
            long gID = geoPointService.CreateGeoPoint(g).Result;

            Assert.AreEqual(g.Id, gID);
        }
    }
}
