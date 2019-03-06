using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace pinpointrAPI.Models
{
    public class RDSContext : DbContext
    {
        public RDSContext(DbContextOptions<RDSContext> options) : base(options)
        {
        }
        public DbSet<User> User { get; set; }
        public DbSet<Submission> Submission { get; set; }
        public DbSet<Tag> Tag { get; set; }

        /// <summary>
        /// Tag has a composite primary key, therefore fluent API must be used
        /// </summary>
        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Tag>()
                .HasKey(t => new { t.submission_id, t.name });
        }

    }
}
