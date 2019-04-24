package com.example.android.tflitecamerademo;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PointBuilding {
    private String name;
    private List<double[]> points = new ArrayList<double[]>();

    public PointBuilding(String name, InputStream stream) {
        this.name = name;
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        ReadFromStream(br);
    }

    public PointBuilding(File buildingFile) {
        this.name = buildingFile.getName().replaceFirst("[.][^.]+$", "");
        if (buildingFile != null && buildingFile.exists()) {
            try(BufferedReader br = new BufferedReader(new FileReader(buildingFile))) {
                ReadFromStream(br);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        try {
            Log.e("Building file error", "Can't find building file " + buildingFile.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }

    private void ReadFromStream(BufferedReader br) {
        try {
            String line;
            while ((line = br.readLine()) != null) {
                String[] separatedValues = line.split(",");
                double rawX = Double.parseDouble(separatedValues[0]);
                double rawY = Double.parseDouble(separatedValues[1]);

                this.addPoint(rawX, rawY);
                Log.i("Building file", "Writing to file");
            }
        } catch (java.io.IOException ex) {
            Log.e("Building file error", "Error while parsing building file output");
        } catch (Exception ex) {
            Log.e("Building file error", "Error reading from building file");
        }

    }

    public String getName() {
        return this.name;
    }

    public Boolean collidesWith(double inX, double inY) {



        boolean inside = false;
        int n = this.points.size();
        if (n < 3) {
            return false;
        }
        double p1x = this.points.get(0)[0];
        double p1y = this.points.get(0)[1];

        double p2x;
        double p2y;

        double xints = -Integer.MAX_VALUE;

        for (int i = 0; i <= n; i++)
        {
            p2x = this.points.get(i % n)[0];
            p2y = this.points.get(i % n)[1];

            if (inY > Math.min(p1y, p2y))
            {
                if (inY <= Math.max(p1y, p2y))
                {
                    if (inX <= Math.max(p1x, p2x))
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



    public void addPoint(double x, double y) {
        double[] toAdd = new double[2];
        toAdd[0] = x;
        toAdd[1] = y;

        this.points.add(toAdd);
    }
}
