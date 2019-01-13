using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace pinpointr.Models
{
    public class RDSContext : DbContext
    {
        public RDSContext(DbContextOptions<RDSContext> options) : base(options)
        {
        }
        public DbSet<user> user { get; set; }

    }
}
