/*
 * AbstractColdTypeTest.java
 * JUnit based test
 *
 * Created on 2 septembre 2006, 10:05
 */
package org.blackdog.type.base;

import junit.framework.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 * test getStringRepresentation for AudioDuration
 *
 * @author alexis
 */
public class AudioDurationTest
    extends TestCase
{
    public AudioDurationTest( String testName )
    {
        super( testName );
    }

    public static Test suite(  )
    {
        TestSuite suite = new TestSuite( AudioDurationTest.class );

        return suite;
    }

    public void testStringRepresentation(  )
    {
        AudioDuration duration = null;
        String expected = null;
        long value = -1;

        value = -1;
        duration = new AudioDuration( value );
        expected = "";
        assertEquals( expected,
                      duration.getStringRepresentation(  ) );

        value = -10;
        duration = new AudioDuration( value );
        expected = "";
        assertEquals( expected,
                      duration.getStringRepresentation(  ) );

        value = -1000;
        duration = new AudioDuration( value );
        expected = "";
        assertEquals( expected,
                      duration.getStringRepresentation(  ) );

        value = 0;
        duration = new AudioDuration( value );
        expected = "0:00";
        assertEquals( expected,
                      duration.getStringRepresentation(  ) );

        value = 1000;
        duration = new AudioDuration( value );
        expected = "0:01";
        assertEquals( expected,
                      duration.getStringRepresentation(  ) );

        value = 1500;
        duration = new AudioDuration( value );
        expected = "0:01";
        assertEquals( expected,
                      duration.getStringRepresentation(  ) );

        value = 1999;
        duration = new AudioDuration( value );
        expected = "0:01";
        assertEquals( expected,
                      duration.getStringRepresentation(  ) );

        value = 10500;
        duration = new AudioDuration( value );
        expected = "0:10";
        assertEquals( expected,
                      duration.getStringRepresentation(  ) );

        value = 59000;
        duration = new AudioDuration( value );
        expected = "0:59";
        assertEquals( expected,
                      duration.getStringRepresentation(  ) );

        value = 3540000;
        duration = new AudioDuration( value );
        expected = "59:00";
        assertEquals( expected,
                      duration.getStringRepresentation(  ) );

        value = 3600000;
        duration = new AudioDuration( value );
        expected = "1:00:00";
        assertEquals( expected,
                      duration.getStringRepresentation(  ) );

        value = 3601000;
        duration = new AudioDuration( value );
        expected = "1:00:01";
        assertEquals( expected,
                      duration.getStringRepresentation(  ) );

        value = 3660000;
        duration = new AudioDuration( value );
        expected = "1:01:00";
        assertEquals( expected,
                      duration.getStringRepresentation(  ) );

        value = 3719000;
        duration = new AudioDuration( value );
        expected = "1:01:59";
        assertEquals( expected,
                      duration.getStringRepresentation(  ) );
    }
}
