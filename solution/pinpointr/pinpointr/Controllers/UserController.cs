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
        
        [HttpGet("[action]/{id}")]
        public async Task<ActionResult<User>> GetUser(int id)
        {
            User user = await _context.User.FindAsync(id);

            if (user == null) {
                return NotFound();
            }

            return user;
        }
        
        [HttpGet("[action]")]
        public IEnumerable<User> GetAllUsers()
        {
            return _context.User.ToList();
        }
        
        [HttpPut("[action]")]
        public IActionResult PutUser(User user)
        {
            
            _context.User.Add(user);
            _context.SaveChanges();

            return CreatedAtAction("GetUser", new { id = 0 }, user);
        }
    }
}