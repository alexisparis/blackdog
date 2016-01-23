package org.siberia.utilities.random;

import java.util.HashSet;
import java.util.Set;
import junit.framework.*;

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
public class RandomizerTest extends TestCase
{
    private int loopCount = 100;
    
    public RandomizerTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(RandomizerTest.class);
        
        return suite;
    }
    
    public void main(String[] args) {
        // pas de verification des parametres, ce n'est pas l'objet
        junit.swingui.TestRunner.run(RandomizerTest.class);
    }

    /**
     * Test of randomInteger method, of class org.siberia.util.Randomizer.
     */
    public void testRandomInteger() throws Exception
    {
        int[][] couples = new int[][]{{1, 2}, 
                                      {3, 4},
                                      {1, 9}, 
                                      {3, 3}};
        
        int value = 0;
        
        for(int index = 0; index < couples.length; index ++)
        {
            for(int i = 0; i < loopCount; i++)
            {   try
                {
                    value = Randomizer.randomInteger(couples[index][0], couples[index][1]);
                }
                catch(Exception e)
                {   e.printStackTrace(); }
                assertTrue("in [" + couples[index][0] + ", " + couples[index][1] + "] --> generated " + value + 
                                    " when i=" + i, value >= couples[index][0] && value <= couples[index][1]);
            }
        }
        
    }

    /**
     * Test of randomChar method, of class org.siberia.util.Randomizer.
     */
    public void testRandomChar() throws Exception
    {
        int value = 0;
        Character c = null;
        
        for(int i = 0; i < loopCount; i++)
        {   
            try
            {
                c = new Character(Randomizer.randomChar());
            }
            catch(Exception e)
            {   e.printStackTrace(); }
            
            assertTrue("value : " + c + " when i=" + i, (c.charValue() >= 'A' && c.charValue() <= 'z') && !
                                (c.charValue() > 'Z' && c.charValue() < 'a') );
        }
    }

    /**
     * Test of randomString method, of class org.siberia.util.Randomizer.
     */
    public void testRandomString() throws Exception
    {
        String c = null;
        Set<String> list = new HashSet<String>();
        int[] listing = new int[]{1, 2, 4, 10};
        
        for(int index = 0; index < listing.length; index++)
        {   for(int i = 0; i < 52; i++)
            {   
                try
                {   c = Randomizer.randomString(listing[index], list);
                    if ( c != null )
                        list.add(c);
                }
                catch(Exception e)
                {   e.printStackTrace(); }

                assertTrue("value : " + c, c != null );
            }
            list.clear();
        }
    }
    
}
