using System.ComponentModel.DataAnnotations;

namespace HealthTrac.Models
{
    public class GeoPoint
    {
        public long Id { get; set; }
        public double Latitude { get; set; }
        public double Longitude { get; set; }
        [Required]
        public long ActivityId { get; set; }
        public virtual Activity Activity { get; set; }
    }
}