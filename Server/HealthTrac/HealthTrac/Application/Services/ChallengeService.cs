using System;
using System.Collections.Generic;
using HealthTrac.Models;
using System.Threading.Tasks;
using HealthTrac.Data_Access;

namespace HealthTrac.Application.Services
{
    public class ChallengeService : IChallengeService
    {
        private readonly IChallengeRepository _challengeRepository;
        private readonly IUnitOfWork _unit;

        public ChallengeService(IChallengeRepository challengeRepository, IUnitOfWork unit)
        {
            _challengeRepository = challengeRepository;
            _unit = unit;
        }

        public IList<Challenge> GetChallenges()
        {
            return _challengeRepository.ReadAll();
        }

        public async Task<Challenge> FindChallenge(long id)
        {
            return await _challengeRepository.GetById(id);
        }

        public IList<Challenge> GetUserChallenges(string userId)
        {
            return _challengeRepository.GetByUser(userId);
        }

        public IList<Challenge> GetChallengerChallenges(string userId)
        {
            return _challengeRepository.GetByChallenger(userId);
        }

        public IList<Challenge> GetFriendChallenges(string userId)
        {
            return _challengeRepository.GetByFriend(userId);
        }

        public async Task<long> CreateChallenge(Challenge challenge)
        {
            if (challenge.FriendGoalId < 1)
            {
                challenge.FriendGoalId = null;
            }
            _challengeRepository.Create(challenge);
            await _unit.Commit();
            return challenge.Id;
        }

        public async Task UpdateChallenge(Challenge challenge)
        {
            _challengeRepository.Update(challenge);
            await _unit.Commit();
        }

        public async Task DeleteChallenge(long id)
        {
            await _challengeRepository.Delete(id);
            await _unit.Commit();
        }

        #region Disposing
        private bool _disposed;

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        ~ChallengeService()
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