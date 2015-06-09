using System;
using System.ComponentModel.DataAnnotations;

namespace HealthTrac.Models
{
    public enum GoalType
    {
        Duration,
        Distance,
        Steps
    }

    public enum TimeFrame
    {
        Daily,
        Weekly,
        Monthly,
        Yearly
    }

    public class Goal
    {
        public long Id { get; set; }
        public GoalType Type { get; set; }
        public double Target { get; set; }

        public double Progress
        {
            get { return this.progress != 0 ? this.progress : 0; }
            set { this.progress = value; }
        }

        private double progress;

        public bool Completed
        {
            get { return this.completed; }
            set { this.completed = value; }
        }
        private bool completed = false;
        public TimeFrame TimeFrame { get; set; }
        public DateTime SetDate
        {
            get
            {
                return this.setDate.HasValue
                   ? this.setDate.Value
                   : DateTime.Now;
            }

            set { this.setDate = value; }
        }
        private DateTime? setDate = null;
        [Required]
        public string UserId { get; set; }
        public virtual User User { get; set; }
    }
}