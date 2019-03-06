using NpgsqlTypes;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;

namespace pinpointrAPI.Models
{
    public class SubmissionTags
    {
        public Submission submission { get; set; }
        public List<Tag> tags { get; set; }
    }
}