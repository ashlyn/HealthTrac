using System;
using System.ComponentModel.DataAnnotations;

namespace HealthTrac.Models
{
    public class EndOfDayReport
    {
        public long Id { get; set; }
        public double TotalDuration { get; set; }
        public int TotalSteps { get; set; }
        public double TotalDistance { get; set; }
        public DateTime Date { get; set; }
        [Required]
        public string UserId { get; set; }
    }
}