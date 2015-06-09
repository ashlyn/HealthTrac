using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public interface IGeoPointService : IDisposable
    {
        IList<GeoPoint> GetGeoPoints();
        Task<GeoPoint> FindGeoPoint(long id);
        IList<GeoPoint> GetActivityPoints(long aId);
        Task<long> CreateGeoPoint(GeoPoint geoPoint);
        Task UpdateGeoPoint(GeoPoint geoPoint);
        Task DeleteGeoPoint(long id);
    }
}
