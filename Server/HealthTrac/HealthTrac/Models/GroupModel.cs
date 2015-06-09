using System.Collections.Generic;

namespace HealthTrac.Models
{
    public class Group
    {
        public long Id { get; set; }
        public string GroupName { get; set; }
        public string Description { get; set; }
        public string ImageUrl { get; set; }
        public virtual ICollection<Membership> GroupMembers { get; set; }
    }
}
