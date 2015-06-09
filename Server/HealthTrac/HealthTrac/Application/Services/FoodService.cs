using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using HealthTrac.Data_Access;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public class FoodService : IFoodService
    {
        private readonly IFoodRepository _foodRepository;
        private readonly IFeedEventService _feedEventService;
        private readonly IUnitOfWork _unit;

        public FoodService(IFoodRepository foodRepository, IFeedEventService feedEventService, IUnitOfWork unit)
        {
            _foodRepository = foodRepository;
            _feedEventService = feedEventService;
            _unit = unit;
        }

        public IList<Food> GetFoods()
        {
            return _foodRepository.ReadAll().ToList();
        }

        public Task<Food> FindFood(long id)
        {
            return _foodRepository.GetById(id);
        }

        public IList<Food> SearchForFoods(string key)
        {
            var foods = _foodRepository.ReadAll().Where(f => f.FoodName.ToLower().Contains(key.ToLower()));
            return foods.ToList();
        }

        public async Task<long> CreateFood(Food food)
        {
            food = _foodRepository.Create(food);
            await _unit.Commit();
            await _feedEventService.GenerateFeedEvent(food);
            await _unit.Commit();
            return food.Id;
        }

        public async Task UpdateFood(Food food)
        {
            _foodRepository.Update(food);
            await _unit.Commit();
        }

        public async Task DeleteFood(long id)
        {
            await _foodRepository.Delete(id);
            var feedEvents = _feedEventService.GetFeedEvents().Where(e => e.Type == EventType.Food && e.EventId == id);
            foreach (FeedEvent f in feedEvents)
            {
                await _feedEventService.DeleteFeedEvent(f.Id);
            }
            await _unit.Commit();
        }

        #region Disposing
        private bool _disposed;

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        ~FoodService()
        {
            Dispose(false);
        }

        protected virtual void Dispose(bool disposing)
        {
            if (_disposed)
            {
                return;
            }
            if (disposing)
            {

            }
            _disposed = true;
        }
        #endregion
    }
}