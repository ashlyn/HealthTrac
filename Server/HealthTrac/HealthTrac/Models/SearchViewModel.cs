using System.Collections.Generic;

namespace HealthTrac.Models
{
    public class SearchViewModel
    {
        public ICollection<User> Users;
        public ICollection<Group> Groups;
        public string Key;
    }
}