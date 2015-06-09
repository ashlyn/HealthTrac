using System;
using System.ComponentModel.DataAnnotations;

namespace HealthTrac.Models
{
    public enum Unit
    {
        Oz,
        FlOz,
        Cups,
        Grams,
        Tbsp,
        Milliliters
    }

    public class Food
    {
        public long Id { get; set; }
        public string FoodName { get; set; }
        public double Amount { get; set; }
        public Unit Unit { get; set; }
        public DateTime Time { get; set; }
        [Required]
        public string UserId { get; set; }
        public virtual User User { get; set; }
    }
}