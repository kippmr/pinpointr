using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;

namespace pinpointrAPI.Helpers
{
    public class locationHelper : Controller
    {
        static List<double> seBound = new List<double> { 43.257330, -79.912568 };
        static List<double> nwBound = new List<double> { 43.268491, -79.929586 };

        public static bool isInMcMaster(double lat, double lon)
        {
            if ((lat <= nwBound[0] && lat >= seBound[0]) && (lon >= nwBound[1] && lon <= seBound[1]))
            {
                return true;
            }
            return false;
        }
    }
}