using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using System.Threading.Tasks;
using System.Diagnostics;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using pinpointrAPI.Models;
using pinpointrAPI.Helpers;
using System.Net;
using System.Net.Mail;

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

        private string emailBody;

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
            this.emailBody = "test";
            sendEmail();
            return _context.Submission.ToList();
        }

        /// <summary>
        /// Gets list of all submissions that have not expired and are not completed
        /// </summary>
        /// <returns></returns>
        [HttpGet("[action]")]
        public IEnumerable<Submission> GetCurrentSubmissions()
        {
            var now = DateTime.Now;
            var submissions = _context.Submission.Where(s => s.obs_est >= now && !s.is_completed).ToList();

            return submissions;
        }

        /// <summary>
        /// Get tags for a single submission
        /// </summary>
        /// <param name="id">id of submission</param>
        /// <returns>List of tags</returns>
        [HttpGet("[action]/{id}")]
        public ActionResult<IEnumerable<Tag>> GetTags(int id)
        {
            IEnumerable<Tag> tags = _context.Tag.Where((Tag tag) => tag.submission_id == id);
            if (tags == null || tags.Count() == 0)
                return BadRequest("No tag information for that submission");
            return Ok(tags);
        }

        /// <summary>
        /// Uploads image to the S3 bucket
        /// </summary>
        /// <param name="file">image to be uploaded</param>
        /// <returns>unique id of image to connect to a submission</returns>
        [HttpPost("[action]")]
        public async Task<IActionResult> PostImage(IFormFile file)
        {
            // File validation, must be image
            if (!file.ContentType.Contains("image"))
                return BadRequest("File uploaded must be an image");
            
            var image_url = Guid.NewGuid().ToString();

            // Call the upload service
            var imageResponse = await S3Helper.UploadObject(image_url, file, _bucket);
            
            if (imageResponse.success)
                return Ok(imageResponse.file_name);
            return BadRequest("Image was not uploaded");
        }

        /// <summary>
        /// Post submission and link tags through foriegn key
        /// </summary>
        /// <param name="user_id">id of submitting user</param>
        /// <param name="coordinates">lon/lat of submission</param>
        /// <param name="tags">tags of submission</param>
        /// <param name="image_url">url of image that was previously uploaded</param>
        /// <param name="altitude">altitude of submission</param>
        /// <param name="is_completed">is the issue already completed</param>
        /// <returns>GET request of created submission</returns>
        [HttpPost("[action]")]
        public async Task<IActionResult> PostSubmission([FromHeader] int user_id, [FromHeader] List<double> coordinates, 
        [FromBody] List<Tag> tags,
        [FromHeader] string image_url = null, [FromHeader] double altitude = 0, [FromHeader] bool is_completed = false, 
        [FromHeader] string building_name = null, [FromHeader] string room_number = null, [FromHeader] string comment = null)
        {
            // Validate coord format
            if (coordinates.Count() != 2)
                return BadRequest("Must have two coordinates");
            if (tags.Count() == 0)
                return BadRequest("Must have at least one tag");

            Submission submission = new Submission()
            {
                user_id = user_id,
                image_url = image_url,
                coordinates = new NpgsqlTypes.NpgsqlPoint(coordinates[0], coordinates[1]),
                altitude = altitude,
                is_completed = is_completed,
                building_name = building_name,
                room_number = room_number,
                comment = comment
            };
            

            _context.Submission.Add(submission);

            try 
            {
                await _context.SaveChangesAsync();
            } catch (Exception ex)
            {
                return BadRequest(ex);
            }

            // give all distinct tags the submission_id
            List<Tag> distinct_tags = tags.GroupBy(x => x.name).Select(y => y.First()).ToList();
            distinct_tags.Distinct().AsParallel().ForAll( tag => { tag.submission_id = submission.id; });

            _context.Tag.AddRange(distinct_tags);

            try 
            {
                await _context.SaveChangesAsync();
            } catch (Exception ex)
            {
                return BadRequest(ex);
            }


            emailBody = "New submission with image url: https://s3.us-east-2.amazonaws.com/pinpointrbucket/" + submission.image_url + "\n"
                + "At coordinates: " + coordinates[0] + "," + coordinates[1] + "\n"
                + "In building: " + building_name + ", room: " + room_number + "\n" + "Tags: ";

            foreach (var tag in distinct_tags)
                emailBody += tag + ", ";
            emailBody.Substring(emailBody.Length - 2);

            sendEmail();

            return CreatedAtAction("GetSubmission", new { submission.id }, submission);
        }

        /// <summary>
        /// Mark a submission as completed
        /// </summary>
        /// <param name="id"></param>
        /// <returns>Completed submission</returns>
        [HttpPut("[action]")]
        public async Task<IActionResult> PutCompleted([FromHeader] int id)
        {
            Submission submission = await _context.Submission.FindAsync(id);

            if (submission == null)
                return BadRequest("Invalid id");
            else if (submission.is_completed == true)
                return BadRequest("Already completed");
            else
                submission.is_completed = true;

            try
            {
                _context.Update(submission);
                _context.SaveChanges();
            } catch (Exception ex)
            {
                return BadRequest(ex);
            }

            return CreatedAtAction("GetSubmission", new { id }, submission);
        }

        public void sendEmail()
        {
            var fromAddress = new MailAddress("redrocketman117@gmail.com", "pinpointrSubmission");
            var toAddress = new MailAddress("redrocketman117@gmail.com", "pinpointrSubmission");
            const string fromPassword = "mcmasterpinpointr";
            const string subject = "new Pinpointr Submission";

            var smtp = new SmtpClient
            {
                Host = "smtp.gmail.com",
                Port = 587,
                EnableSsl = true,
                DeliveryMethod = SmtpDeliveryMethod.Network,
                UseDefaultCredentials = false,
                Credentials = new NetworkCredential(fromAddress.Address, fromPassword)
            };

            using (var message = new MailMessage(fromAddress, toAddress)
            {
                Subject = subject,
                Body = emailBody
            })
            {
                smtp.Send(message);
            }
        }

    }
}