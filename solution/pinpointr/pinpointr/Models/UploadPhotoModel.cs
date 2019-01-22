using NpgsqlTypes;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace pinpointr.Models
{
    public class UploadPhotoModel
    {
        public string file_name { get; set; }
        public Boolean success { get; set; }
    }
}
