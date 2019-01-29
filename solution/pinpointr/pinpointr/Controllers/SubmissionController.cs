using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using pinpointr.Models;
using pinpointr.Helpers;
using System.Diagnostics;

namespace pinpointr.Controllers
{
    /// <summary>
    /// SubmissionController responsible for handling subission admittance and image tagging
    /// </summary>
    [Route("api/[controller]")]
    [ApiController]
    public class SubmissionController : Controller
    {
        // initialize database connection
        private readonly RDSContext _context;
        private readonly BucketConnection _bucket;

        /// <summary>
        /// Create database connection and AWS S3 Bucket connection
        /// </summary>
        /// <param name="context"> database connection</param>
        public SubmissionController(RDSContext context)
        {
            _context = context;
            
            // Get the appsettings.json for aws bucket credentials
            var config = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("appsettings.json").Build();

            // Add credentials to POCO
            _bucket = new BucketConnection
            {
                access_key = config["BucketConnection:access_key"],
                access_secret = config["BucketConnection:access_secret"],
                bucket = config["BucketConnection:bucket"]
            };
        }
        
        /// <summary>
        /// Get a single submission from database given id
        /// </summary>
        /// <param name="id">id of submission</param>
        /// <returns>Single submission</returns>
        [HttpGet("[action]/{id}")]
        public async Task<ActionResult<Submission>> GetSubmission(int id)
        {
            Submission submission = await _context.Submission.FindAsync(id);

            if (submission == null) {
                return NotFound();
            }

            return submission;
        }
        
        /// <summary>
        /// Gets list of all submissions
        /// </summary>
        /// <returns>List of submissions</returns>
        [HttpGet("[action]")]
        public IEnumerable<Submission> GetAllSubmissions()
        {
            return _context.Submission.ToList();
        }

        /// <summary>
        /// Post submission to S3 bucket and create database entry
        /// </summary>
        /// <param name="user_id">id of user submitting</param>
        /// <param name="coordinates">lat/lon</param>
        /// <param name="altitude"></param>
        /// <param name="tags">tags from tensorflow</param>
        /// <param name="file">image to be uploaded</param>
        /// <returns></returns>
        [HttpPost("[action]")]
        public async Task<IActionResult> PostSubmission([FromHeader] List<double> coordinates, [FromHeader] List<string> tags, 
            IFormFile file, [FromHeader] int user_id = 0, [FromHeader] double altitude = 0) 
        {
            // File validation, must be image
            if (!file.ContentType.Contains("image"))
                return BadRequest("File uploaded must be an image");
            if (coordinates.Count() != 2)
                return BadRequest("Must have two coordinates");

            Submission submission = new Submission()
            {
                user_id = user_id,
                image = Guid.NewGuid().ToString(),
                coordinates = new NpgsqlTypes.NpgsqlPoint(coordinates[0], coordinates[1]),
                altitude = altitude,
                tags = tags,
                gen_est = DateTime.Now
            };

            _context.Submission.Add(submission);

            _context.SaveChanges();

            // Call the upload service
            var imageResponse = await AmazonS3Service.UploadObject(submission.image, file, _bucket);

            return CreatedAtAction("GetSubmission", new { submission.id }, submission);
        }
    }
}