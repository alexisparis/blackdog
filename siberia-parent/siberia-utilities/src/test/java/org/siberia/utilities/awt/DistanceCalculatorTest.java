package org.siberia.utilities.awt;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;
import junit.framework.*;
import org.siberia.utilities.random.Randomizer;

/*
 * RandomizerTest.java
 * JUnit based test
 *
 * Created on 19 mai 2006, 21:08
 */

/**
 *
 * @author alexis
 */
public class DistanceCalculatorTest extends TestCase
{
    public DistanceCalculatorTest(String testName)
    {   super(testName); }

    protected void setUp() throws Exception
    {   }

    protected void tearDown() throws Exception
    {   }

    public static Test suite()
    {   TestSuite suite = new TestSuite(DistanceCalculatorTest.class);
        return suite;
    }

    public void testGetMinimumDistanceBetween() throws Exception
    {
        Rectangle rec1 = new Rectangle(0, 0, 20, 20);
        Rectangle rec2 = new Rectangle(0, 10, 70, 50);
        Rectangle rec3 = new Rectangle(500, 10, 70, 50);
        
        Point     p1   = new Point(50, 20);
        Point     p2   = new Point(50, 80);
        Point     p3   = new Point(100, 0);
        
        double delta = 0.0000000000001d;
        
        assertEquals(30, DistanceCalculator.getMinimumDistanceBetween(rec1, p1), delta);
        assertEquals(Math.sqrt( Math.pow(30, 2) + Math.pow(60, 2) ), DistanceCalculator.getMinimumDistanceBetween(rec1, p2), delta);
        assertEquals(80, DistanceCalculator.getMinimumDistanceBetween(rec1, p3), delta);
        assertEquals(0, DistanceCalculator.getMinimumDistanceBetween(rec2, p1), delta);
        assertEquals(20, DistanceCalculator.getMinimumDistanceBetween(rec2, p2), delta);
        assertEquals(Math.sqrt(1000), DistanceCalculator.getMinimumDistanceBetween(rec2, p3), delta);
        assertEquals(450, DistanceCalculator.getMinimumDistanceBetween(rec3, p1), delta);
        assertEquals(Math.sqrt(202900), DistanceCalculator.getMinimumDistanceBetween(rec3, p2), delta);
        assertEquals(Math.sqrt(160100), DistanceCalculator.getMinimumDistanceBetween(rec3, p3), delta);
        
        
        assertEquals(60, DistanceCalculator.getMinimumDistanceBetween(p1, p2), delta);
        assertEquals(Math.sqrt(2900), DistanceCalculator.getMinimumDistanceBetween(p1, p3), delta);
        assertEquals(Math.sqrt(8900), DistanceCalculator.getMinimumDistanceBetween(p2, p3), delta);
    }
}
