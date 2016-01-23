package org.siberia.parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.siberia.parser.impl.DoubleParser;
import org.siberia.parser.impl.IntegerParser;
import org.siberia.parser.impl.StringParser;


/**
 *
 * @author alexis
 */
public class ParserExceptionTest extends TestCase
{   
    public ParserExceptionTest(String testName)
    {   super(testName); }

    protected void setUp() throws Exception
    {   }

    protected void tearDown() throws Exception
    {   }

    public static Test suite()
    {   TestSuite suite = new TestSuite(ParserRegistryTest.class);
        return suite;
    }
    
    public void test() throws Exception
    {   
        Parser parser = null;
        ParseException exception = null;
        String value = null;
        
        parser = new StringParser();
        value = "er";
        exception = new ParseException(value, parser);
        
        assertEquals("Could not parse '" + value + "' with " + parser, exception.getMessage());
        
        parser = new IntegerParser();
        value = "e";
        exception = new ParseException(value, parser);
        
        assertEquals("Could not parse '" + value + "' with " + parser, exception.getMessage());
        
        parser = new DoubleParser();
        value = "sdfsdfsdf";
        exception = new ParseException(value, parser);
        
        assertEquals("Could not parse '" + value + "' with " + parser, exception.getMessage());
    }
}
