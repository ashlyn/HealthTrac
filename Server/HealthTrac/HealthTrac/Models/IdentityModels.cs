using System;
using System.Collections.Generic;
using System.Data.Entity;
using HealthTrac.Application;
using Microsoft.AspNet.Identity.EntityFramework;
using System.ComponentModel.DataAnnotations;

namespace HealthTrac.Models
{
    public class User : IdentityUser
    {
        public string FullName { get; set; }
        public string PreferredName { get; set; }
        public string Gender { get; set; }
        public int HeightInches { get; set; }
        public int HeightFeet { get; set; }
        public int Weight { get; set; }
        public string Email { get; set; }
        public string ImageUrl { get; set; }

        [DataType(DataType.Date)]
        public DateTime JoinDate
        {
            get
            {
                return this.joinDate.HasValue
                   ? this.joinDate.Value
                   : DateTime.Now;
            }

            set { this.joinDate = value; }
        }

        private DateTime? joinDate = null;

        [DataType(DataType.Date)]
        public DateTime BirthDate { get; set; }
        public string Location { get; set; }

        public virtual ICollection<UserBadge> Badges { get; set; }
        public virtual ICollection<Membership> GroupMembership { get; set; }
        public virtual ICollection<Activity> Activities { get; set; }
        public virtual ICollection<UserMood> Moods { get; set; }
        public virtual ICollection<Food> Foods { get; set; }
        public virtual ICollection<Goal> Goals { get; set; }
    }

    public class UserComparer : IEqualityComparer<User>
    {

        public bool Equals(User x, User y)
        {
            return x.Id == y.Id;
        }

        public int GetHashCode(User obj)
        {
            return obj.Id.GetHashCode();
        }
    }

    public class ApplicationDbContext : IdentityDbContext<User>
    {
        public ApplicationDbContext()
            : base("DefaultConnection")
        {
        }

        # region DbSets
        public DbSet<Group> Groups { get; set; }
        public DbSet<Membership> Memberships { get; set; }
        public DbSet<Activity> Activities { get; set; }
        public DbSet<Badge> Badges { get; set; }
        public DbSet<Challenge> Challenges { get; set; }
        public DbSet<EndOfDayReport> EndOfDayReports { get; set; }
        public DbSet<GeoPoint> GeoPoints { get; set; }
        public DbSet<Goal> Goals { get; set; }
        public DbSet<FeedEvent> FeedEvents { get; set; }
        public DbSet<Food> Foods { get; set; }
        public DbSet<Mood> Moods { get; set; }
        public DbSet<UserBadge> UserBadges { get; set; }
        public DbSet<UserMood> UserMoods { get; set; }

        #endregion
    }

    public class ApplicationContextAdapter : IDbSetFactory, IDbContext
    {
        private readonly ApplicationDbContext _context;

        public ApplicationContextAdapter(ApplicationDbContext context)
        {
            _context = context;
        }

        public DbSet<T> CreateDbSet<T>() where T : class
        {
            return _context.Set<T>();
        }

        public void ChangeObjectState(object entity, EntityState state)
        {
            _context.Entry(entity).State = state;
        }

        public void RefreshEntity<T>(ref T entity) where T : class
        {
            _context.Entry<T>(entity).Reload();
        }

        public void Dispose()
        {
            _context.Dispose();
        }

        public void SaveChanges()
        {
            _context.SaveChanges();
        }
    }
}