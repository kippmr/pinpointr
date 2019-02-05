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
        public string image { get; set; }
        public NpgsqlPoint coordinates { get; set; }
        public double? altitude { get; set; }
        public List<string> tags { get; set; }
        public DateTime gen_est { get; set; }
    }
}
