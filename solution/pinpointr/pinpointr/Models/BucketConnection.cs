using NpgsqlTypes;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace pinpointr.Models
{
    public class BucketConnection
    {
        public string access_key { get; set; }
        public string access_secret { get; set; }
        public string bucket { get; set; }
    }
}
