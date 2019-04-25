using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.IO;

namespace pinpointrAPI.Models
{

    public class PointBuilding
    {
        string name;
        List<double[]> points = new List<double[]>();
        string[] rawData;

        public PointBuilding(string filePath)
        {
            this.name = Path.GetFileNameWithoutExtension(filePath);
            this.rawData = File.ReadAllLines(filePath);
            this.parseRawData();
        }

        private void parseRawData()
        {
            for (int i = 0; i < rawData.Length; i++)
            {
                string[] seperatedValues = rawData[i].Split(",");
                double rawX = double.Parse(seperatedValues[0]);
                double rawY = double.Parse(seperatedValues[1]);

                this.addPoint(rawX, rawY);

                Console.WriteLine("Adding point " + rawX + "," + rawY + "for building " + this.getName());
            }
        }

        public string getName()
        {
            return this.name;
        }

        public Boolean collidesWith(double inX, double inY)
        {
            bool inside = false;
            int n = this.points.Count;

            double p1x = this.points.ElementAt(0)[0];
            double p1y = this.points.ElementAt(0)[1];

            double p2x;
            double p2y;

            double xints = -int.MaxValue;

            for (int i = 0; i <= n; i++)
            {
                p2x = this.points.ElementAt(i % n)[0];
                p2y = this.points.ElementAt(i % n)[1];

                if (inY > Math.Min(p1y, p2y))
                {
                    if (inY <= Math.Max(p1y, p2y))
                    {
                        if (inX <= Math.Max(p1x, p2x))
                        {
                            if (p1y != p2y)
                            {
                                xints = (inY - p1y) * (p2x - p1x) / (p2y - p1y) + p1x;
                            }
                            if (p1x == p2x || inX <= xints)
                            {
                                inside = !inside;
                            }
                        }
                    }
                }

                p1x = p2x;
                p1y = p2y;
            }

            return inside;
        }

        public void addPoint(double x, double y)
        {
            double[] toAdd = new double[2];
            toAdd[0] = x;
            toAdd[1] = y;

            this.points.Add(toAdd);
        }

        public List<double[]> getPoints()
        {
            return this.points;
        }

        public double[] getPoint(int index)
        {
            return this.points.ElementAt(index);
        }
    }
}
