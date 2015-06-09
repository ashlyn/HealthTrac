using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public interface IFoodService : IDisposable
    {
        IList<Food> GetFoods();
        Task<Food> FindFood(long id);
        IList<Food> SearchForFoods(string key);
        Task<long> CreateFood(Food food);
        Task UpdateFood(Food food);
        Task DeleteFood(long id);
    }
}
