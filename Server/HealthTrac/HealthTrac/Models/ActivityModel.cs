using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using Microsoft.Ajax.Utilities;

namespace HealthTrac.Models
{
    public enum ActivityType {
        Running,
        Biking,
        Jogging,
        Walking,
        Other
    }
    
    public class Activity
    {
        public long Id { get; set; }

        public string Name
        {
            get { return !this.name.IsNullOrWhiteSpace() ? this.name : Type.ToString(); }
            set { this.name = value; }
        }
        private string name;
        public double Duration { get; set; }
        [Required]
        public string UserId { get; set; }
        public DateTime StartTime { get; set; }
        public ActivityType Type { get; set; }
        public double Distance { get; set; }
        public int Steps { get; set; }
        public virtual ICollection<GeoPoint> RoutePoints { get; set; }
		public virtual User User { get; set; }
    }
}
