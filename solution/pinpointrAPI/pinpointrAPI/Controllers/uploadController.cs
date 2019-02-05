using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using pinpointrAPI.Helpers;

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
            return "false";
        }

        // POST /api/upload
        [HttpPost]
        public ActionResult<bool> Post(IFormFile in_file)
        {
            uh.uploadBlob(in_file);
            return true;
        }

    }
}