/* 
 * Siberia utilities : siberia plugin providing severall utilities classes
 *
 * Copyright (C) 2008 Alexis PARIS
 * Project Lead:  Alexis Paris
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package org.siberia.utilities.awt;

import java.awt.Rectangle;
import java.awt.Point;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author alexis
 */
public class DistanceCalculator
{
    /** Creates a new instance of DistanceCalculator */
    private DistanceCalculator()
    {   /* do nothing */ }
    
    /** return the minimum distance between rect and the given position
     * @param rec
     * @param point
     * @return int the distance
     **/
    public static double getMinimumDistanceBetween(Rectangle rec, Point point)
    {   int dX = 0;
        int dY = 0;
        int yB = rec.y + rec.height;
        int xB = rec.x + rec.width;
        
        /* determine X size */
        if ( rec.x > point.x)
            dX = rec.x - point.x;
        else if ( xB < point.x)
            dX = point.x - xB;
        else
            dX = 0;
        
        /* determine Y size */
        if ( rec.y > point.y)
            dY = rec.y - point.y;
        else if ( yB < point.y)
            dY = point.y - yB;
        else
            dY = 0;
                
        /* no need to use Math API if one size is equal to zero */
        if ( dX == 0)      return dY;
        else if ( dY == 0) return dX;
        
        /* else ... */
        return Math.sqrt( Math.pow(dX, 2) + Math.pow(dY, 2) );
    }
    
    /** return the distance between the two points
     *  @param pointA point A
     *  @param pointB point B
     *  @return int the distance between the two points
     **/
    public static double getMinimumDistanceBetween(Point pointA, Point pointB)
    {   return Math.sqrt(Math.pow(pointA.x - pointB.x, 2) + Math.pow(pointA.y - pointB.y, 2)); }
    
    /** return the nearest point from another point.<br>
     *  Be careful, the returned point is a given point in the list, so changes to it will afect the element on the list
     *  @param point point A
     *  @param points list of candidates 
     *  @param decalPoint origin of the given points
     *  @return a Point
     **/
    public static Point getNearestPointFrom(Point point, List<Point> points, Point decalPoint)
    {   if ( points == null )     return null;
        
        if ( points.size() == 0 ) return null;
        
        double minimumDistance = -1;
        Point nearestPoint  = null;
        Point tmpPoint      = new Point();
        
        Point currentPoint  = null;
        for(Iterator<Point> pointIterator = points.iterator(); pointIterator.hasNext();)
        {   currentPoint = pointIterator.next();
            
            tmpPoint.setLocation(currentPoint.getX() + decalPoint.getX(), currentPoint.getY() + decalPoint.getY());
            double currentDistance = getMinimumDistanceBetween(point, tmpPoint);
            if ( currentDistance < minimumDistance || minimumDistance < 0 )
            {   minimumDistance = currentDistance;
                nearestPoint = currentPoint;
            }
        }
        
        return nearestPoint;
    }
    
    /** return the nearest point from another point<br>
     *  Be careful, the returned point is a given point in the list, so changes to it will afect the element on the list
     *  @param point point A
     *  @param points list of candidates 
     *  @return a Point
     **/
    public static Point getNearestPointFrom(Point point, List<Point> points)
    {   return getNearestPointFrom(point, points, new Point(0, 0)); }
}
