using System.Threading.Tasks;

namespace HealthTrac.Application
{
    public interface IUnitOfWork
    {
        Task Commit();
        void Rollback();
    }
}
