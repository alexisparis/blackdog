package org.siberia.utilities.awt;

import java.awt.Color;
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
public class ColorTranslatorTest extends TestCase
{   
    public ColorTranslatorTest(String testName)
    {   super(testName); }

    protected void setUp() throws Exception
    {   }

    protected void tearDown() throws Exception
    {   }

    public static Test suite()
    {   TestSuite suite = new TestSuite(ColorTranslatorTest.class);
        return suite;
    }

    public void testGetColorFor() throws Exception
    {
        assertEquals(Color.RED,ColorTranslator.getColorFor("#FF0000"));
        assertEquals(Color.GREEN, ColorTranslator.getColorFor("#00FF00"));
        assertEquals(Color.BLUE, ColorTranslator.getColorFor("#0000FF"));
        assertNotSame(new Color(52, 52, 52), ColorTranslator.getColorFor("#0F00FF"));
        
        assertEquals(Color.PINK, ColorTranslator.getColorFor("#FFAFAF"));
        assertNotSame(Color.PINK, ColorTranslator.getColorFor("#FFBEAE"));
    }

    public void testGetStringRepresentationOf() throws Exception
    {
        assertEquals("#FF0000", ColorTranslator.getStringRepresentationOf(Color.RED));
        assertEquals("#AFA0A0", ColorTranslator.getStringRepresentationOf(new Color(175, 160, 160)));
        assertNotSame("#AFA0A1", ColorTranslator.getStringRepresentationOf(new Color(175, 160, 160)));
        assertNotSame("#00FF01", ColorTranslator.getStringRepresentationOf(Color.BLUE));
    }
}
