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
    public class MoodServiceTest
    {
        private IMoodService _moodService;
        private IList<Mood> _moods;

        [TestInitialize]
        public void TestInit()
        {
            _moods = new List<Mood>
            {
                new Mood {Id = 0, Type = "angry", ImageUrl = @"http://i.imgur.com/Uf8zoJw.png"},
                new Mood {Id = 1, Type = "anxious", ImageUrl = @"http://i.imgur.com/VlIReSR.png"},
                new Mood {Id = 2, Type = "accomplished", ImageUrl = @"http://i.imgur.com/g8FZ2xd.png"},
                new Mood {Id = 3, Type = "fabulous", ImageUrl = @"http://i.imgur.com/j9HOXL2.png"},
                new Mood {Id = 4, Type = "happy", ImageUrl = @"http://i.imgur.com/SIzErP7.png"},
                new Mood {Id = 5, Type = "motivated", ImageUrl = @"http://i.imgur.com/yLjnqxK.png"},
                new Mood {Id = 6, Type = "sad", ImageUrl = @"http://i.imgur.com/UWqJu9z.png"},
                new Mood {Id = 7, Type = "salty", ImageUrl = @"http://i.imgur.com/eCagqac.png"},
                new Mood {Id = 8, Type = "sick", ImageUrl = @"http://i.imgur.com/2hKP5cZ.png"},
                new Mood {Id = 9, Type = "sweaty", ImageUrl = @"http://i.imgur.com/oqlyjOj.png"},
                new Mood {Id = 10, Type = "tired", ImageUrl = @"http://i.imgur.com/ZvNj7d1.png"},
                new Mood {Id = 11, Type = "victorious", ImageUrl = @"http://i.imgur.com/1GWpCgQ.png"}
            };

            var moodRepo = new Mock<IMoodRepository>();
            moodRepo.Setup(m => m.ReadAll()).Returns(_moods);
            moodRepo.Setup(m => m.GetById(It.IsAny<long>()))
                .Returns<long>(i => Task.FromResult(_moods.Single(x => x.Id == i)));
            moodRepo.Setup(m => m.Create(It.IsAny<Mood>()))
                .Returns(It.IsAny<Mood>);
            moodRepo.Setup(m => m.Update(It.IsAny<Mood>()));

            var unit = new Mock<IUnitOfWork>();

            _moodService = new MoodService(moodRepo.Object, unit.Object);
        }

        [TestMethod]
        public void GetAllMoods()
        {
            var moods = _moodService.GetMoods();

            CollectionAssert.AreEqual(moods.ToArray(), _moods.ToArray());
        }

        [TestMethod]
        public void GetMoodById()
        {
            long id = 5;
            var mood = _moodService.FindMood(id);

            Assert.AreEqual(mood.Result, _moods[(int)id]);
        }

        [TestMethod]
        public void CreateMood()
        {
            Mood m = new Mood { Id = 12, Type = "frustrated", ImageUrl = @"http://i.imgur.com/UWqJu9z.png" };
            long mID = _moodService.CreateMood(m).Result;

            Assert.AreEqual(m.Id, mID);
        }
    }
}
