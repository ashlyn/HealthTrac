using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace HealthTrac.Utilities
{
    public static class DateHelper
    {
        public static DateTime LocalTime(this DateTime dt)
        {
            return TimeZoneInfo.ConvertTimeFromUtc(dt,
                TimeZoneInfo.FindSystemTimeZoneById("Central Standard Time"));
        }
    }
}