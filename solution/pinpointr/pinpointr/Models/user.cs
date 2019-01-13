using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace pinpointr.Models
{
    public class user
    {
        public int id { get; set; }
        public string username { get; set; }
        public string password { get; set; }
        public int student_id { get; set; }
        public int employee_id { get; set; }
        public string email { get; set; }
    }
}
