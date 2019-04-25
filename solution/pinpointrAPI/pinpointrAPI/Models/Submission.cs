using NpgsqlTypes;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace pinpointrAPI.Models
{
    public class Submission
    {
        public int id { get; set; }
        public int user_id { get; set; }
        public string image_url { get; set; }
        public string building_name { get; set; } = null;
        public string room_number { get; set; } = null;
        public NpgsqlPoint coordinates { get; set; }
        public double? altitude { get; set; } = 0;
        public bool is_completed { get; set; } = false;
        public DateTime obs_est { get; set; } = DateTime.Now.AddHours(22);
        public DateTime gen_est { get; set; } = DateTime.Now;
    }
}
