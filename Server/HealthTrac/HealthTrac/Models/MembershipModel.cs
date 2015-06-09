using System.ComponentModel.DataAnnotations;

namespace HealthTrac.Models
{
    public enum Status
    {
        Member,
        Admin,
        Left,
        Banned,
        Invited
    }
    public class Membership
    {
        public long Id { get; set; }
        [Required]
        public string UserId { get; set; }
        [Required]
        public long GroupId { get; set; }
        public Status Status { get; set; }
        public virtual User User { get; set; }
        public virtual Group Group { get; set; }
    }
}