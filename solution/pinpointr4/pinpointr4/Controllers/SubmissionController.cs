using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using pinpointrAPI.Models;
using pinpointrAPI.Helpers;

namespace pinpointrAPI.Controllers
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

            if (submission == null)
            {
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
        /// Add a submission to the database
        /// </summary>
        /// <param name="submission">contains expected submission values</param>
        /// <returns>Created submission</returns>
        [HttpPut("[action]")]
        public IActionResult PutSubmission(Submission submission)
        {

            _context.Submission.Add(submission);
            _context.SaveChanges();

            return CreatedAtAction("GetSubmission", new { submission.id }, submission);
        }

        /// <summary>
        /// Uploads an image to S3 bucket using unique identifier
        /// </summary>
        /// <param name="file">image to be uploaded</param>
        /// <returns>Success</returns>
        [HttpPost("[action]")]
        public async Task<IActionResult> UploadSubmissionImage(IFormFile file)
        {
            // File validation, must be image
            if (!file.ContentType.Contains("image"))
            {
                return BadRequest();
            }

            // Call the upload service
            var imageResponse = await S3Helper.UploadObject(file, _bucket);

            return Ok();
        }
    }
}