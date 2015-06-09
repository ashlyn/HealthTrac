using System;
using System.ComponentModel.DataAnnotations;

namespace HealthTrac.Models
{
    public class UserMood
    {
        public long Id { get; set; }
        public DateTime Time
        {
            get
            {
                return this.time.HasValue
                   ? this.time.Value
                   : DateTime.Now;
            }

            set { this.time = value; }
        }

        private DateTime? time = null;
        [Required]
        public long MoodId { get; set; }
        public virtual Mood Mood { get; set; }
        [Required]
        public string UserId { get; set; }
        public virtual User User { get; set; }
    }
}