package presentation.components;

import logic.Episode;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;

public class DataPoint {
    private float m_X, m_Y;
    private Episode m_Episode;

    public DataPoint(float xVal, float yVal, Episode eVal) {
        m_X = xVal;
        m_Y = yVal;
        m_Episode = eVal;
    }

    public static DataPoint findDataPoint(ArrayList<DataPoint> points, float x, float y, float r) {
        for (DataPoint point : points) {
            float dist = (x - point.m_X) * (x - point.m_X) + (y - point.m_Y) * (y - point.m_Y);
            if (dist < r * r)
                return point;
        }
        return null;
    }

    public static HashMap<Integer, ArrayList<DataPoint>> splitBySeason(ArrayList<DataPoint> points) {
        HashMap<Integer, ArrayList<DataPoint>> map = new HashMap<>();

        for (DataPoint point: points) {
            if (point.getEpisode().getRating() > 0.0f) {
                Integer key = point.getEpisode().getSeasonNumber();

                ArrayList<DataPoint> list = map.get(key);
                if (list == null) {
                    list = new ArrayList<>();
                    map.put(key, list);
                }
                list.add(point);
            }
        }
        return map;
    }

    public static Line2D.Float linearRegression(ArrayList<DataPoint> points) {
        float sumx=0.0f, sumy=0.0f, sumxx=0.0f, sumyy=0.0f, sumxy=0.0f;
        float mx, my, xxvar, yyvar, xyvar;
        int n = points.size();
        float minX, maxX;

        minX = points.get(0).getX();
        maxX = minX;

        for (DataPoint point: points) {
            float x = point.getX();
            float y = point.getY();
            if (x < minX)
                minX = x;
            if (x > maxX)
                maxX = x;
            sumx += x;
            sumy += y;
            sumxx += x * x;
            sumyy += y * y;
            sumxy += x * y;
        }
        mx = sumx / n;
        my = sumy / n;
        xxvar = (sumxx - 2.0f * mx * sumx + n * mx * mx) / n;
        yyvar = (sumyy - 2.0f * my * sumy + n * my * my)/ n;
        xyvar = (sumxy - mx * sumy - my * sumx + n * mx * my) / n;
        float slope = xyvar / xxvar;
        float intercept = my - slope * mx;
        return new Line2D.Float(minX, minX * slope + intercept, maxX, maxX * slope + intercept);
    }

    public float getX() {
        return m_X;
    }

    public float getY() {
        return m_Y;
    }

    public Episode getEpisode() {
        return m_Episode;
    }
}
