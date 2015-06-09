using System.ComponentModel.DataAnnotations.Schema;

namespace HealthTrac.Models
{
    public class Badge
    {
        [DatabaseGenerated(DatabaseGeneratedOption.None)]
        public long Id { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
        public string ImageUrl { get; set; }     

    }

}