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
    public class UserController : Controller
    {
        // initialize database connection
        private readonly RDSContext _context;
        public UserController(RDSContext context)
        {
            _context = context;
        }



        [HttpGet("[action]")]
        public IEnumerable<User> GetUsers()
        {
            return _context.User.ToList();
        }

        [HttpGet]
        public ActionResult<List<User>> GetAll()
        {
            return _context.User.ToList();
        }
    }
}