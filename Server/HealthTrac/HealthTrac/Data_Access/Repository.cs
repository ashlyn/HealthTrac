using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Threading.Tasks;
using HealthTrac.Application;

namespace HealthTrac.Data_Access
{
    public class Repository<T> : IRepository<T> where T : class
    {
        protected DbSet<T> _dbSet;
        protected IDbSetFactory _dbSetFactory;

        protected Repository(IDbSetFactory dbSetFactory)
        {
            _dbSet = dbSetFactory.CreateDbSet<T>();
            _dbSetFactory = dbSetFactory;
        }

        public IList<T> ReadAll()
        {
            return _dbSet.ToList();
        }

        public async Task<T> GetById(long id)
        {
            return await _dbSet.FindAsync(id);
        }

        public T Create(T t)
        {
            _dbSet.Add(t);

            return t;
        }

        public void Update(T t)
        {
            _dbSetFactory.ChangeObjectState(t, EntityState.Modified);
        }

        public async Task Delete(long id)
        {
            var t = await _dbSet.FindAsync(id);
            _dbSetFactory.ChangeObjectState(t, EntityState.Deleted);
        }
    }
}