using NpgsqlTypes;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;

namespace pinpointrAPI.Models
{
    public class Location
    {
        public string building_no { get; set; }
        public string room_no { get; set; }
    }
}