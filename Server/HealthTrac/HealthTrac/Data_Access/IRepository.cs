using System.Collections.Generic;
using System.Threading.Tasks;

namespace HealthTrac.Data_Access
{
    public interface IRepository<T>
    {
        IList<T> ReadAll();
        Task<T> GetById(long id);
        T Create(T t);
        void Update(T t);
        Task Delete(long id);
    }
}