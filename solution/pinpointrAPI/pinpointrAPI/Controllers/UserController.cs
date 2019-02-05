using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using pinpointrAPI.Models;

namespace pinpointrAPI.Controllers
{
    /// <summary>
    /// UserController responsible for handling user endpoints
    /// </summary>
    [Route("api/[controller]")]
    [ApiController]
    public class UserController : Controller
    {
        // initialize database connection
        private readonly RDSContext _context;
        /// <summary>
        /// Create database connection
        /// </summary>
        /// <param name="context"></param>
        public UserController(RDSContext context)
        {
            _context = context;
        }

        /// <summary>
        /// Get a single user given their id
        /// </summary>
        /// <param name="id">unique id of user</param>
        /// <returns>Single User</returns>
        [HttpGet("[action]/{id}")]
        public async Task<ActionResult<User>> GetUser(int id)
        {
            User user = await _context.User.FindAsync(id);

            if (user == null)
            {
                return NotFound();
            }

            return user;
        }

        /// <summary>
        /// Get list of all users
        /// </summary>
        /// <returns>List of Users</returns>
        [HttpGet("[action]")]
        public IEnumerable<User> GetAllUsers()
        {
            return _context.User.ToList();
        }

        /// <summary>
        /// Add user to database
        /// </summary>
        /// <param name="user">contains expected user values</param>
        /// <returns>Created user</returns>
        [HttpPut("[action]")]
        public IActionResult PutUser(User user)
        {

            _context.User.Add(user);
            _context.SaveChanges();

            return CreatedAtAction("GetUser", new { user.id }, user);
        }
    }
}