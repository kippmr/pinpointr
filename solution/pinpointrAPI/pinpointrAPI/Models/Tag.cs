using NpgsqlTypes;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace pinpointrAPI.Models
{
    public class Tag
    {
        public int id { get; set; }
        public int submission_id { get; set; }
        public string name { get; set; }
        public int who_submitted { get; set; }
        public double? user_percentage { get; set; }
        public double? ai_percentage { get; set; }
    }
}