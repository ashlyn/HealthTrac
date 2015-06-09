using System.ComponentModel.DataAnnotations;

namespace HealthTrac.Models
{
    public class UserBadge
    {
        public long Id { get; set; }
        [Required]
        public string UserId { get; set; }
        [Required]
        public long BadgeId { get; set; }
        public virtual Badge Badge { get; set; }
    }
}