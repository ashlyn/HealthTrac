using System;
using System.ComponentModel.DataAnnotations;

namespace HealthTrac.Models
{
    public enum EventType
    {
        Badge,
        GroupJoin,
        GroupLeave,
        Activity,
        Food,
        GoalSet,
        GoalAchieved,
        Mood,
        EndOfDay
    }
    public class FeedEvent
    {
        public long Id { get; set; }
        public long EventId { get; set; }
        public EventType Type { get; set; }
        public string Description { get; set; }
        [Required]
        public string UserId { get; set; }
        public DateTime Date
        {
            get
            {
                return this.date.HasValue
                   ? this.date.Value
                   : DateTime.Now;
            }

            set { this.date = value; }
        }
        private DateTime? date = null;
        public virtual User User { get; set; }
    }
}