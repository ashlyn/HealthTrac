using System.Collections.Generic;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public interface IUserRepository
    {
        IList<User> ReadAll();
        User GetById(string id);
        string Create(User user);
        void Update(User user);
        void Delete(string id);
    }
}