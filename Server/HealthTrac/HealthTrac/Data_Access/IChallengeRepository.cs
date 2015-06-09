using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public interface IChallengeRepository
    {
        IList<Challenge> ReadAll();
        Task<Challenge> GetById(long id);
        IList<Challenge> GetByChallenger(string userId);
        IList<Challenge> GetByFriend(string userId);
        IList<Challenge> GetByUser(string userId);
        Challenge Create(Challenge a);
        void Update(Challenge a);
        Task Delete(long id);
    }
}
