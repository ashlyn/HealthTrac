using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public interface IGoalService : IDisposable
    {
        IList<Goal> GetGoals();
        Task<Goal> FindGoal(long id);
        IList<Goal> GetUserGoals(string userId);
        Task<double> AssessGoalProgress(long id);
        Task<double> AssessGoalProgress(Goal goal);
        Task AssessAllGoalProgress(IList<Goal> goals);
        Task<long> CreateGoal(Goal goal);
        Task UpdateGoal(Goal goal);
        Task DeleteGoal(long id);
    }
}
