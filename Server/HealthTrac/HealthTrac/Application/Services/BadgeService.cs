using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using HealthTrac.Data_Access;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public class BadgeService : IBadgeService
    {
        private readonly IBadgeRepository _badgeRepository;
        private readonly IUnitOfWork _unit;

        public BadgeService(IBadgeRepository badgeRepository, IUnitOfWork unit)
        {
            _badgeRepository = badgeRepository;
            _unit = unit;
        }

        public IList<Badge> GetBadges()
        {
            return _badgeRepository.ReadAll().ToList();
        }

        public async Task<Badge> FindBadge(long id)
        {
            return await _badgeRepository.GetById(id);
        }

        public IList<Badge> SearchForBadges(string key)
        {
            key = key.ToLower();
            var badges = _badgeRepository.ReadAll().Where(b => b.Description.ToLower().Contains(key) || b.Name.ToLower().Contains(key));
            return badges.ToList();
        }

        public async Task<long> CreateBadge(Badge badge)
        {
            _badgeRepository.Create(badge);
            await _unit.Commit();
            return badge.Id;
        }

        public async Task UpdateBadge(Badge badge)
        {
            _badgeRepository.Update(badge);
            await _unit.Commit();
        }

        public async Task DeleteBadge(long id)
        {
            await _badgeRepository.Delete(id);
            await _unit.Commit();
        }

        #region Disposing
        private bool _disposed;

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        ~BadgeService()
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