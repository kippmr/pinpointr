using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using pinpointrAPI.Models;

namespace pinpointrAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class uploadController : ControllerBase
    {
        uploadHelper uh = new uploadHelper();

        // GET /api/upload
        [HttpGet]
        public ActionResult<string> Get()
        {
            uh.getConnectionInfo();
            return "false";
        }


    }
}