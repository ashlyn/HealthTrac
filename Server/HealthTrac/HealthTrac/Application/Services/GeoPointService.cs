using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using HealthTrac.Data_Access;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public class GeoPointService : IGeoPointService
    {
        private readonly IGeoPointRepository _geoPointRepository;
        private readonly IUnitOfWork _unit;

        public GeoPointService(IGeoPointRepository geoPointRepository, IUnitOfWork unit)
        {
            _geoPointRepository = geoPointRepository;
            _unit = unit;
        }

        public IList<GeoPoint> GetGeoPoints()
        {
            return _geoPointRepository.ReadAll().ToList();
        }

        public async Task<GeoPoint> FindGeoPoint(long id)
        {
            return await _geoPointRepository.GetById(id);
        }

        public IList<GeoPoint> GetActivityPoints(long aId)
        {
            var points = _geoPointRepository.ReadAll().Where(p => p.ActivityId == aId);
            return points.ToList();
        }

        public async Task<long> CreateGeoPoint(GeoPoint geoPoint)
        {
            _geoPointRepository.Create(geoPoint);
            await _unit.Commit();
            return geoPoint.Id;
        }

        public async Task UpdateGeoPoint(GeoPoint geoPoint)
        {
            _geoPointRepository.Update(geoPoint);
            await _unit.Commit();
        }

        public async Task DeleteGeoPoint(long id)
        {
            await _geoPointRepository.Delete(id);
            await _unit.Commit();
        }

        private bool _disposed;

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        ~GeoPointService()
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
    }
}