/*
 * AbstractColdTypeTest.java
 * JUnit based test
 *
 * Created on 2 septembre 2006, 10:05
 */
package org.blackdog.type.session;

import junit.framework.*;

import org.blackdog.type.AudioItem;
import org.blackdog.type.Mp3SongItem;
import org.blackdog.type.base.RepeatMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 *
 * @author alexis
 */
public class SinglePlayableSessionTest
    extends TestCase
{
    public SinglePlayableSessionTest( String testName )
    {
        super( testName );
    }

    public static Test suite(  )
    {
        TestSuite suite = new TestSuite( SinglePlayableSessionTest.class );

        return suite;
    }

    public void testGen(  )
    {
	if ( true )
	{
	    return;
	}
        AudioItem item = new Mp3SongItem(  );
        SinglePlayableSession session = new SinglePlayableSession( item );

        assertTrue( item == session.getCurrentPlayable(  ) );

        session.goToNextPlayable( item, true, RepeatMode.ALL );

        assertTrue( item == session.getCurrentPlayable(  ) );

        session.goToNextPlayable( item, true, RepeatMode.ALL );

        assertTrue( item == session.getCurrentPlayable(  ) );

        session.goToPreviousPlayable( item, true, RepeatMode.ALL );

        assertTrue( item == session.getCurrentPlayable(  ) );

        session.goToPreviousPlayable( item, true, RepeatMode.ALL );

        assertTrue( item == session.getCurrentPlayable(  ) );

        assertTrue( item == session.getCurrentPlayable(  ) );

        session.goToNextPlayable( item, true, RepeatMode.ALL );
    }

    public void testGetRandomNumber(  )
    {
	if ( true )
	{
	    return;
	}
	
        SinglePlayableSession session = new SinglePlayableSession( null );

        int result = -1;
        int a = -1;
        int b = -1;

        int loop = 300;

        a = 0;
        b = 0;

        for ( int i = 0; i < loop; i++ )
        {
            result = session.getRandomNumber( a, b );
            this._testGetRandomNumber( a, b, result );
        }

        a = 0;
        b = 1;

        for ( int i = 0; i < loop; i++ )
        {
            result = session.getRandomNumber( a, b );
            this._testGetRandomNumber( a, b, result );
        }

        a = 0;
        b = 10;

        for ( int i = 0; i < loop; i++ )
        {
            result = session.getRandomNumber( a, b );
            this._testGetRandomNumber( a, b, result );
        }

        a = 10;
        b = 10;

        for ( int i = 0; i < loop; i++ )
        {
            result = session.getRandomNumber( a, b );
            this._testGetRandomNumber( a, b, result );
        }

        a = -10;
        b = 20;

        for ( int i = 0; i < loop; i++ )
        {
            result = session.getRandomNumber( a, b );
            this._testGetRandomNumber( a, b, result );
        }

        a = 35;
        b = -2;

        for ( int i = 0; i < loop; i++ )
        {
            result = session.getRandomNumber( a, b );
            this._testGetRandomNumber( a, b, result );
        }
    }

    /** check random */
    private void _testGetRandomNumber( int a, int b, int result )
    {
        assertTrue( result >= Math.min( a, b ) );
        assertTrue( result <= Math.max( a, b ) );
    }
}
