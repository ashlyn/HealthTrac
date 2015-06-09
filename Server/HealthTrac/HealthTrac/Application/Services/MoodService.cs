using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using HealthTrac.Data_Access;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public class MoodService : IMoodService
    {
        private readonly IMoodRepository _moodRepository;
        private readonly IUnitOfWork _unit;

        public MoodService(IMoodRepository moodRepository, IUnitOfWork unit)
        {
            _moodRepository = moodRepository;
            _unit = unit;
        }

        public IList<Mood> GetMoods()
        {
            return _moodRepository.ReadAll().ToList();
        }

        public async Task<Mood> FindMood(long id)
        {
            return await _moodRepository.GetById(id);
        }

        public IList<Mood> Search(string key)
        {
            var moods = _moodRepository.ReadAll().Where(m => m.Type.ToLower().Contains(key.ToLower()));
            return moods.ToList();
        }

        public async Task<long> CreateMood(Mood mood)
        {
            _moodRepository.Create(mood);
            await _unit.Commit();
            return mood.Id;
        }

        public async Task UpdateMood(Mood mood)
        {
            _moodRepository.Update(mood);
            await _unit.Commit();
        }

        public async Task DeleteMood(long id)
        {
            await _moodRepository.Delete(id);
            await _unit.Commit();
        }

        #region Disposing
        private bool _disposed;

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        ~MoodService()
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