using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public interface IGeoPointRepository
    {
        IList<GeoPoint> ReadAll();
        Task<GeoPoint> GetById(long id);
        GeoPoint Create(GeoPoint g);
        void Update(GeoPoint g);
        Task Delete(long id);
    }
}
