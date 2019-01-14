using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using pinpointr.Models;

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
        
        [HttpGet("[action]")]
        public IEnumerable<Submission> GetAllSubmissions()
        {
            return _context.Submission.ToList();
        }
    }
}