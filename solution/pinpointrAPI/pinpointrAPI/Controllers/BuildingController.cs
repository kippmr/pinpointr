using System;
using System.IO;
using System.Collections.Generic;
using System.Linq;
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
    [Route("api/[controller]")]
    [ApiController]
    public class BuildingController
    {

        List<PointBuilding> pointBuildings = new List<PointBuilding>();

        public BuildingController()
        {
            string[] buildingFiles = Directory.GetFiles("./Helpers/Buildings/", "*.txt", SearchOption.AllDirectories);
            

            foreach (string s in buildingFiles)
            {
                pointBuildings.Add(new PointBuilding(s));
            }
        }

        // POST /api/Building
        [HttpPost]
        public ActionResult<string> Post(string xCoord, string yCoord)
        {
            return xCoord + 

            foreach (PointBuilding pb in pointBuildings)
            {
                if (pb.collidesWith(43.258517, -79.92011))
                {
                    return pb.getName();
                }
            }
            return null;
        }
    }
}
