using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace HealthTrac.Models
{
    public class Challenge
    {
        public long Id { get; set; }
        public string ChallengerId { get; set; }
        public long ChallengerGoalId { get; set; }
        public string FriendId { get; set; }
        public long? FriendGoalId { get; set; }
        public bool Accepted { get; set; }
        public virtual Goal ChallengerGoal { get; set; }
        public virtual Goal FriendGoal { get; set; }
    }
}