package org.siberia.utilities.security.check;

import java.io.File;
import junit.framework.*;

/**
 *
 * @author alexis
 */
public class CheckMethodTest extends TestCase
{
    public CheckMethodTest(String testName)
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
        TestSuite suite = new TestSuite(CheckMethodTest.class);
        
        return suite;
    }
    
    public void testCheckSumEnum()
    {
        CheckSum ch = CheckSum.MD5_SUM;
        
        assertEquals(".md5", ch.extension());
        assertEquals("md5", ch.shortName());
        assertTrue(ch.method() == SimpleCheckMethod.MD5_METHOD);
        
        ch = CheckSum.SHA1_SUM;
        
        assertEquals(".sha1", ch.extension());
        assertEquals("sha1", ch.shortName());
        assertTrue(ch.method() == SimpleCheckMethod.SHA1_METHOD);
        
        ch = CheckSum.NONE;
        
        assertEquals("", ch.extension());
        assertEquals("none", ch.shortName());
        assertNull(ch.method());
    }

    /**
     * Test of location methods of PluginLink
     */
    public void testMethods() throws Exception
    {
        CheckMethod method = null;
        
        String prefix = "src/test/java/";
        
        File f = null;
        
        // ########################################## //
        
        f = new File(prefix + "org/siberia/utilities/security/check/bcprov-jdk15-124.pom");
        
        // SHA-1
        method = SimpleCheckMethod.SHA1_METHOD;
        
        assertTrue("error with SHA-1",
                   method.isValid(f, new File(prefix + "org/siberia/utilities/security/check/bcprov-jdk15-124.pom.sha1")));
        
        // MD5
        method = SimpleCheckMethod.MD5_METHOD;
        
        assertTrue("error with MD5",
                   method.isValid(f, new File(prefix + "org/siberia/utilities/security/check/bcprov-jdk15-124.pom.md5")));
        
        // ########################################## //
        
        f = new File(prefix + "org/siberia/utilities/security/check/file");
        
        // SHA-1
        method = SimpleCheckMethod.SHA1_METHOD;
        
        assertTrue("error with SHA-1",
                   method.isValid(f, new File(prefix + "org/siberia/utilities/security/check/file.sha1.correct")));
        assertFalse("error with SHA-1",
                   method.isValid(f, new File(prefix + "org/siberia/utilities/security/check/file.sha1.wrong")));
        
        // MD5
        method = SimpleCheckMethod.MD5_METHOD;
        
        assertTrue("error with MD5",
                   method.isValid(f, new File(prefix + "org/siberia/utilities/security/check/file.md5.correct")));
        assertFalse("error with MD5",
                   method.isValid(f, new File(prefix + "org/siberia/utilities/security/check/file.md5.wrong")));
        
    }
    
}
