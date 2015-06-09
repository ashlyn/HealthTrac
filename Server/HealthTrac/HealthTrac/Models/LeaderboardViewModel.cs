using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace HealthTrac.Models
{
    public class LeaderboardViewModel
    {
        public IList<Tuple<User, double>> Leaders;
    }
}