using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace pinpointrAPI.Models
{
    public class User
    {
        public int id { get; set; }
        public string username { get; set; }
        public string password { get; set; }
        public int? student_id { get; set; } = null;
        public int? employee_id { get; set; } = null;
        public string email { get; set; } = null;
        public int points { get; set; } = 0;
    }
}
