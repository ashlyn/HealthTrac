using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public interface IChallengeService
    {
        IList<Challenge> GetChallenges();
        Task<Challenge> FindChallenge(long id);
        IList<Challenge> GetUserChallenges(string userId);
        IList<Challenge> GetChallengerChallenges(string userId);
        IList<Challenge> GetFriendChallenges(string userId);
        Task<long> CreateChallenge(Challenge challenge);
        Task UpdateChallenge(Challenge challenge);
        Task DeleteChallenge(long id);
    }
}
