package org.neo4j.spatial.algo;

import org.neo4j.spatial.core.LineSegment;
import org.neo4j.spatial.core.MultiPolyline;
import org.neo4j.spatial.core.Point;
import org.neo4j.spatial.core.Polygon;
import org.neo4j.spatial.core.Polyline;

public abstract class Distance {
    /**
     * @param a
     * @param b
     * @return The minimum distance between two polygons. Returns 0 if one polygon is (partially) contained by the other
     */
    public abstract double distance(Polygon a, Polygon b);

    /**
     *
     * @param polygon
     * @param multiPolyline
     * @return The minimum distance between a polygon and multi polyline. Returns 0 if the multi polyline intersects with or is (partially) containted by the polygon
     */
    public abstract double distance(Polygon polygon, MultiPolyline multiPolyline);

    /**
     * @param polygon
     * @param polyline
     * @return The minimum distance between a polygon and polyline. Returns 0 if the polyline intersects with or is (partially) containted by the polygon
     */
    public abstract double distance(Polygon polygon, Polyline polyline);

    /**
     * @param polygon
     * @param lineSegment
     * @return The minimum distance between a polygon and line segment. Returns 0 if the line segment is (partially) contained by the polygon
     */
    public abstract double distance(Polygon polygon, LineSegment lineSegment);

    /**
     * @param polygon
     * @param point
     * @return The minimum distance between a polygon and point. Returns 0 if point is within the polygon
     */
    public abstract double distance(Polygon polygon, Point point);

    /**
     * @param a
     * @param b
     * @return The minimum distance between two polylines. Returns 0 if they distance
     */
    public abstract double distance(Polyline a, Polyline b);

    /**
     * @param a
     * @param b
     * @return The minimum distance between two multipolylines. Returns 0 if they distance
     */
    public abstract double distance(MultiPolyline a, MultiPolyline b);

    /**
     * @param a
     * @param b
     * @return The minimum distance between a multipolyline and an polyline. Returns 0 if they distance
     */
    public abstract double distance(MultiPolyline a, Polyline b);

    /**
     * @param a
     * @param b
     * @return The minimum distance between a multipolyline and a line segment. Returns 0 if they distance
     */
    public abstract double distance(MultiPolyline a, LineSegment b);

    /**
     * @param polyline
     * @param lineSegment
     * @return The minimum distance between a polyline and line segment. Returns 0 if they distance
     */
    public abstract double distance(Polyline polyline, LineSegment lineSegment);

    /**
     * @param polyline
     * @param point
     * @return The minimum distance between a polyline and point
     */
    public abstract double distance(Polyline polyline, Point point);

    /**
     * @param lineSegment
     * @return The distance between the two end points of a line segment
     */
    public abstract double distance(LineSegment lineSegment);

    /**
     * @param lineSegment
     * @param point
     * @return The minimum distance between a line segment and a point
     */
    public abstract double distance(LineSegment lineSegment, Point point);

    /**
     * @param a
     * @param b
     * @return The minimum distance between two line segments
     */
    public abstract double distance(LineSegment a, LineSegment b);

    /**
     * @param p1
     * @param p2
     * @return The minimum distance between two points
     */
    public abstract double distance(Point p1, Point p2);

    protected double getMinDistance(LineSegment[] aLS, LineSegment[] bLS) {
        double minDistance = Double.MAX_VALUE;

        for (LineSegment aLineSegment : aLS) {
            for (LineSegment bLineSegment : bLS) {
                double current = distance(aLineSegment, bLineSegment);
                if (current < minDistance) {
                    minDistance = current;
                }
            }
        }
        return minDistance;
    }
}
