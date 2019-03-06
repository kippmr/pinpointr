using NpgsqlTypes;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;

namespace pinpointrAPI.Models
{
    public class Tag
    {
        public int submission_id { get; set; }
        public string name { get; set; }
        public bool user_submitted { get; set; }
        public double? ai_percentage { get; set; } = null;
    }
}