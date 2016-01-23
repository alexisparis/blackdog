package org.siberia.parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.siberia.parser.impl.IntegerParser;
import org.siberia.parser.impl.StringParser;


/**
 *
 * @author alexis
 */
public class ParserRegistryTest extends TestCase
{   
    public ParserRegistryTest(String testName)
    {   super(testName); }

    protected void setUp() throws Exception
    {   }

    protected void tearDown() throws Exception
    {   }

    public static Test suite()
    {   TestSuite suite = new TestSuite(ParserRegistryTest.class);
        return suite;
    }
    
    public void testRegisterParser() throws Exception
    {   Parser parser = new IntegerParser();
        ParserRegistry registry = new ParserRegistry();
        registry.registerParser("integer", parser);
        registry.registerParser("string", new StringParser());
        
        assertEquals(registry.getParser("integer"), parser);
        
        registry.registerParser("integer", new StringParser());
        
        assertNotSame(registry.getParser("integer"), parser);
    }
    
    public void testUnregisterParser() throws Exception
    {   Parser parser = new IntegerParser();
        ParserRegistry registry = new ParserRegistry();
        registry.registerParser("integer", parser);
        registry.registerParser("string", new StringParser());
        
        registry.unregisterParser("integer");
        
        assertNull(registry.getParser("integer"));
        
        assertNotNull(registry.getParser("string"));
    }
    
    public void testRegisterParserClass() throws Exception
    {   Parser parser = new IntegerParser();
        ParserRegistry registry = new ParserRegistry();
        registry.registerParser(java.lang.Integer.class, parser);
        registry.registerParser(java.lang.String.class, new StringParser());
        
        assertEquals(registry.getParser(java.lang.Integer.class), parser);
        
        registry.registerParser(java.lang.Integer.class, new StringParser());
        
        assertNotSame(registry.getParser(java.lang.Integer.class), parser);
    }
    
    public void testUnregisterParserClass() throws Exception
    {   Parser parser = new IntegerParser();
        ParserRegistry registry = new ParserRegistry();
        registry.registerParser(java.lang.Integer.class, parser);
        registry.registerParser(java.lang.String.class, new StringParser());
        
        registry.unregisterParser(java.lang.Integer.class);
        
        assertNull(registry.getParser(java.lang.Integer.class));
        
        assertNotNull(registry.getParser(java.lang.String.class));
    }
    
    public void testGetParser() throws Exception
    {   /* already done */ }
    
    public void testGetParserClass() throws Exception
    {   /* already done */ }
}
