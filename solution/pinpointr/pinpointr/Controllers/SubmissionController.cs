using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using pinpointr.Models;
using pinpointr.Helpers;

namespace pinpointr.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class SubmissionController : Controller
    {
        // initialize database connection
        private readonly RDSContext _context;

        public SubmissionController(RDSContext context)
        {
            _context = context;
        }
        
        [HttpGet("[action]/{id}")]
        public async Task<ActionResult<Submission>> GetSubmission(int id)
        {
            Submission submission = await _context.Submission.FindAsync(id);

            if (submission == null) {
                return NotFound();
            }

            return submission;
        }
        
        [HttpGet("[action]")]
        public IEnumerable<Submission> GetAllSubmissions()
        {
            return _context.Submission.ToList();
        }
        
        [HttpPut("[action]")]
        public IActionResult PutSubmission(Submission submission)
        {
            
            _context.Submission.Add(submission);
            _context.SaveChanges();

            return CreatedAtAction("GetSubmission", new { id = 0 }, submission);
        }

        [HttpPost("[action]")]
        public async Task<IActionResult> UploadSubmissionImage(IFormFile file) 
        {
            // File validation, must be image
            if (!file.ContentType.Contains("image"))
            {
                return BadRequest();
            }

            // Call the upload service
            var imageResponse = await AmazonS3Service.UploadObject(file);

            return Ok();
        }
    }
}