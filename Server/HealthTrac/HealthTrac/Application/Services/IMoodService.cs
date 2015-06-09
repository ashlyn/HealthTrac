using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public interface IMoodService : IDisposable
    {
        IList<Mood> GetMoods();
        Task<Mood> FindMood(long id);
        IList<Mood> Search(string key);
        Task<long> CreateMood(Mood mood);
        Task UpdateMood(Mood mood);
        Task DeleteMood(long id);
    }
}
