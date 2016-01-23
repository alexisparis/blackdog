package org.siberia.utilities.lang;

import java.awt.Color;
import junit.framework.*;

/**
 *
 * @author alexis
 */
public class NumberConversionTest extends TestCase
{   
    public NumberConversionTest(String testName)
    {   super(testName); }

    protected void setUp() throws Exception
    {   }

    protected void tearDown() throws Exception
    {   }

    public static Test suite()
    {   TestSuite suite = new TestSuite(NumberConversionTest.class);
        return suite;
    }
    
    public void testBaseMethods() throws Exception
    {   char[] chars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
                                  'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        char[] majChars = new char[]{'A', 'B',
                                  'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 
                                  'V', 'W', 'X', 'Y', 'Z'};
        for(int i = 0; i < chars.length; i++)
        {   int index = i;
            assertEquals("for char : " + chars[i] + ", index expected is : " + index + " and i=" + i, index,
                                NumberConversion.getValue(chars[i]));
        }
        
        for(int i = 0; i < majChars.length; i++)
        {   int index = i + 10;
            assertEquals("for char : " + majChars[i] + ", index expected is : " + index + " and i=" + i, index,
                                NumberConversion.getValue(majChars[i]));
        }
        
        for(int i = 0; i < 36; i++)
        {   
            char c = chars[i];
            
            assertEquals((char)NumberConversion.getCharacter(i), (char)Character.toUpperCase(c));
        }
    }

    public void testGetDecimalValue() throws Exception
    {
        assertEquals(255,NumberConversion.getDecimalValue("FF"));
        assertEquals(240, NumberConversion.getDecimalValue("F0"));
        assertEquals(0, NumberConversion.getDecimalValue("00"));
        assertEquals(1, NumberConversion.getDecimalValue("00001"));
        
        
        assertEquals(250, NumberConversion.convert("FA", 16));
        assertEquals("11111010", NumberConversion.convert(250, 2));
        
        assertEquals("88", NumberConversion.convert(200, 24));
        
        try
        {   assertEquals("88", NumberConversion.convert(200, 1));
            assertFalse(true);
        }
        catch(IllegalArgumentException e)
        {   assertTrue(true); }
        try
        {   assertEquals("88", NumberConversion.convert(200, 37));
            assertFalse(true);
        }
        catch(IllegalArgumentException e)
        {   assertTrue(true); }
        
        assertEquals("5K", NumberConversion.convert(200, 36));
        assertEquals("0", NumberConversion.convert(0, 36));
    }

    public void testGetHexaValue() throws Exception
    {   
        assertEquals("FF", NumberConversion.getHexaValue(255));
        assertEquals("F0", NumberConversion.getHexaValue(240));
        assertEquals("0", NumberConversion.getHexaValue(0));
        assertEquals("1", NumberConversion.getHexaValue(1));
    }
}
