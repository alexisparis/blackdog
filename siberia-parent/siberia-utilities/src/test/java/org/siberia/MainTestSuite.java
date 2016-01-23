package org.siberia;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * Main test suite
 *
 * @author alexis
 */
public class MainTestSuite extends TestCase
{
    public MainTestSuite(String testName)
    {   super(testName); }
   
    public static Test suite()
    {   TestSuite suite = new TestSuite();
       
        return suite;
    }
}
