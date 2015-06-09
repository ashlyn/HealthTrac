using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public interface IFeedEventService : IDisposable
    {
        IList<FeedEvent> GetFeedEvents();
        Task<FeedEvent> FindFeedEvent(long id);
        IList<FeedEvent> GetFeedEventsByUser(string id);
        IList<FeedEvent> GetFeedEventsByGroup(long id); 
        Task<long> CreateFeedEvent(FeedEvent feedEvent);
        Task<long> GenerateFeedEvent<T>(T t);
        Task UpdateFeedEvent(FeedEvent feedEvent);
        Task DeleteFeedEvent(long id);
    }
}
