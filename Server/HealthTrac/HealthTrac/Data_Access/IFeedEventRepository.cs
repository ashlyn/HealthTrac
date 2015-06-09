using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public interface IFeedEventRepository
    {
        IList<FeedEvent> ReadAll();
        Task<FeedEvent> GetById(long id);
        IList<FeedEvent> GetByUser(string userId);
        FeedEvent Create(FeedEvent u);
        void Update(FeedEvent u);
        Task Delete(long id);
    }
}
