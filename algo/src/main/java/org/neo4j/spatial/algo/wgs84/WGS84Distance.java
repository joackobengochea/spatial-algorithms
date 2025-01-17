package org.neo4j.spatial.algo.wgs84;

import org.neo4j.spatial.algo.Distance;
import org.neo4j.spatial.algo.wgs84.intersect.WGS84Intersect;
import org.neo4j.spatial.algo.wgs84.intersect.WGS84MCSweepLineIntersect;
import org.neo4j.spatial.core.LineSegment;
import org.neo4j.spatial.core.MultiPolyline;
import org.neo4j.spatial.core.Point;
import org.neo4j.spatial.core.Polygon;
import org.neo4j.spatial.core.Polyline;
import org.neo4j.spatial.core.Vector;

public class WGS84Distance extends Distance {
    @Override
    public double distance(Polygon a, Polygon b) {
        boolean intersects = new WGS84MCSweepLineIntersect().doesIntersect(a, b);

        //Check if one polygon is (partially) contained by the other
        if (intersects) {
            return 0;
        } else  if (WGS84Within.within(a, b.getShells()[0].getPoints()[0]) || WGS84Within.within(b, a.getShells()[0].getPoints()[0])) {
            return 0;
        }

        LineSegment[] aLS = a.toLineSegments();
        LineSegment[] bLS = b.toLineSegments();

        return getMinDistance(aLS, bLS);
    }

    @Override
    public double distance(Polygon polygon, MultiPolyline multiPolyline) {
        boolean intersects = new WGS84MCSweepLineIntersect().doesIntersect(polygon, multiPolyline);

        //Check if the multi polyline is (partially) contained by the polygon
        if (intersects) {
            return 0;
        } else  if (WGS84Within.within(polygon, multiPolyline.getChildren()[0].getPoints()[0])) {
            return 0;
        }

        LineSegment[] aLS = polygon.toLineSegments();
        LineSegment[] bLS = multiPolyline.toLineSegments();

        return getMinDistance(aLS, bLS);
    }

    @Override
    public double distance(Polygon polygon, Polyline polyline) {
        boolean intersects = new WGS84MCSweepLineIntersect().doesIntersect(polygon, polyline);

        //Check if the polyline is (partially) contained by the polygon
        if (intersects) {
            return 0;
        } else  if (WGS84Within.within(polygon, polyline.getPoints()[0])) {
            return 0;
        }

        LineSegment[] aLS = polygon.toLineSegments();
        LineSegment[] bLS = polyline.toLineSegments();

        return getMinDistance(aLS, bLS);
    }

    @Override
    public double distance(Polygon polygon, LineSegment lineSegment) {
        LineSegment[] lineSegments = polygon.toLineSegments();

        double minDistance = Double.MAX_VALUE;

        for (LineSegment currentLineSegment : lineSegments) {
            double current = distance(currentLineSegment, lineSegment);

            if (current < minDistance) {
                minDistance = current;
            }
        }

        return minDistance;
    }

    @Override
    public double distance(Polygon polygon, Point point) {
        if (WGS84Within.within(polygon, point)) {
            return 0;
        }

        LineSegment[] lineSegments = polygon.toLineSegments();

        double minDistance = Double.MAX_VALUE;

        for (LineSegment lineSegment : lineSegments) {
            double current = distance(lineSegment, point);
            if (current < minDistance) {
                minDistance = current;
            }
        }

        return minDistance;
    }

    @Override
    public double distance(MultiPolyline a, MultiPolyline b) {
        LineSegment[] aLS = a.toLineSegments();
        LineSegment[] bLS = b.toLineSegments();

        return getMinDistance(aLS, bLS);
    }

    @Override
    public double distance(MultiPolyline a, Polyline b) {
        LineSegment[] aLS = a.toLineSegments();
        LineSegment[] bLS = b.toLineSegments();

        return getMinDistance(aLS, bLS);
    }

    @Override
    public double distance(MultiPolyline a, LineSegment b) {
        double minDistance = Double.MAX_VALUE;

        LineSegment[] aLS = a.toLineSegments();

        for (LineSegment aLineSegment : aLS) {
            double current = distance(aLineSegment, b);
            if (current < minDistance) {
                minDistance = current;
            }
        }

        return minDistance;
    }

    @Override
    public double distance(Polyline a, Polyline b) {
        LineSegment[] aLS = a.toLineSegments();
        LineSegment[] bLS = b.toLineSegments();

        return getMinDistance(aLS, bLS);
    }

    @Override
    public double distance(Polyline polyline, LineSegment lineSegment) {
        double minDistance = Double.MAX_VALUE;

        LineSegment[] aLS = polyline.toLineSegments();

        for (LineSegment aLineSegment : aLS) {
            double current = distance(aLineSegment, lineSegment);
            if (current < minDistance) {
                minDistance = current;
            }
        }

        return minDistance;
    }

    @Override
    public double distance(Polyline polyline, Point point) {
        double minDistance = Double.MAX_VALUE;

        LineSegment[] aLS = polyline.toLineSegments();

        for (LineSegment aLineSegment : aLS) {
            double current = distance(aLineSegment, point);
            if (current < minDistance) {
                minDistance = current;
            }
        }

        return minDistance;
    }

    @Override
    public double distance(LineSegment lineSegment) {
        Point u = lineSegment.getPoints()[0];
        Point v = lineSegment.getPoints()[1];

        return distance(u, v);
    }

    @Override
    public double distance(LineSegment lineSegment, Point point) {
        Vector u1 = new Vector(lineSegment.getPoints()[0]);
        Vector u2 = new Vector(lineSegment.getPoints()[1]);
        Vector v = new Vector(point);

        //Check whether the point is within the extent of the line segment
        Vector u1v = v.subtract(u1);
        Vector u2v = v.subtract(u2);
        Vector u1u2 = u2.subtract(u1);
        Vector u2u1 = u1.subtract(u2);

        //These dot products tell us whether the point is on the same side as a point of the line segment compared to the remaining point of the line segment
        double extent1 = u1v.dot(u1u2);
        double extent2 = u2v.dot(u2u1);

        boolean isSameHemisphere = v.dot(u1) >= 0 && v.dot(u2) >= 0;

        boolean withinExtend = extent1 >= 0 && extent2 >= 0 && isSameHemisphere;

        if (withinExtend && !u1.equals(u2)) {
            Vector c1 = u1.cross(u2); // u1×u2 = vector representing great circle through the line segments
            Vector c2 = v.cross(c1);  // u0×c1 = vector representing great circle through the point normal to c1
            Vector n = c1.cross(c2);  // c2×c1 = nearest point on c1 to n0

            return WGSUtil.distance(v, n);
        } else {
            double d1 = WGSUtil.distance(v, u1);
            double d2 = WGSUtil.distance(v, u2);

            return d1 < d2 ? d1 : d2;
        }
    }

    @Override
    public double distance(LineSegment a, LineSegment b) {
        Point intersect = WGS84Intersect.lineSegmentIntersect(a, b);
        if (intersect != null) {
            return 0;
        }

        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < a.getPoints().length; i++) {
            double distance = distance(b, a.getPoints()[i]);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        for (int i = 0; i < b.getPoints().length; i++) {
            double distance = distance(a, b.getPoints()[i]);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }

        return minDistance;
    }

    @Override
    public double distance(Point p1, Point p2) {
        Vector u = new Vector(p1);
        Vector v = new Vector(p2);

        //WGS84Distance (in meters)
        return WGSUtil.distance(u, v);
    }
}
