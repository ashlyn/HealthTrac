using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using HealthTrac.Application;
using HealthTrac.Models;

namespace HealthTrac.Data_Access
{
    public class UserRepository : IUserRepository
    {
        private readonly IDbSet<User> _dbSet;
        private readonly IDbSetFactory _dbSetFactory;

        public UserRepository(IDbSetFactory dbSetFactory)
        {
            _dbSet = dbSetFactory.CreateDbSet<User>();
            _dbSetFactory = dbSetFactory;
        }

        public IList<User> ReadAll()
        {
            return _dbSet.ToList();
        }

        public User GetById(string id)
        {
            return _dbSet.Find(id);
        }

        public string Create(User user)
        {
            var u = _dbSet.Add(user);
            return u.Id;
        }

        public void Update(User user)
        {
            _dbSetFactory.ChangeObjectState(user, EntityState.Modified);
        }

        public void Delete(string id)
        {
            var u = _dbSet.Find(id);
            _dbSetFactory.ChangeObjectState(u, EntityState.Deleted);
        }
    }
}