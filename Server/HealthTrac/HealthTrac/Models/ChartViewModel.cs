using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using DotNet.Highcharts;

namespace HealthTrac.Models
{
    public class ChartViewModel
    {
        public string Caption { get; set; }
        public Highcharts Chart { get; set; }
        public bool UsesFacebook { get; set; }
    }
}