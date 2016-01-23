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
import org.blackdog.type.PlayList;
import org.blackdog.type.base.RepeatMode;

import java.beans.PropertyVetoException;

/**
 *
 *
 * @author alexis
 */
public class PlayListPlayableSessionTest
    extends TestCase
{
    /** test 1 */
    private static final boolean TEST_1_ENABLED = true;
    /** test 2 */
    private static final boolean TEST_2_ENABLED = true;
    /** test 3 */
    private static final boolean TEST_3_ENABLED = true;
    /** test 4 */
    private static final boolean TEST_4_ENABLED = true;
    /** test 5 */
    private static final boolean TEST_5_ENABLED = true;
    /** test 6 */
    private static final boolean TEST_6_ENABLED = true;
    /** test 7 */
    private static final boolean TEST_7_ENABLED = true;
    /** test 8 */
    private static final boolean TEST_8_ENABLED = true;
    /** test 9 */
    private static final boolean TEST_9_ENABLED = true;
    
    
    public PlayListPlayableSessionTest( String testName )
    {
        super( testName );
    }

    public static Test suite(  )
    {
        TestSuite suite = new TestSuite( PlayListPlayableSessionTest.class );

        return suite;
    }

    public void testGen(  )
                 throws PropertyVetoException
    {
	if ( ! TEST_1_ENABLED )
	{
	    return;
	}
	
	int[] sizes = new int[]{15, 24, 1, 0, 2};//, 5, 9, 115};

        for ( int j = 0; j < sizes.length; j++ )
        {
            PlayList playlist = new PlayList(  );

            int size = sizes[j];

            AudioItem[] items = new AudioItem[size];

            for ( int i = 0; i < items.length; i++ )
            {
                AudioItem item = new Mp3SongItem(  );
                items[i] = item;

                item.setName( "item" + ( i + 1 ) );

                playlist.add( item );
            }

            PlayableSession session = new PlayListPlayableSession( playlist );

            assertTrue( session.getCurrentPlayable(  ) == null );

            /* ##################################### */
            /** test current playable initialisation */
            /* ##################################### */
            session.setCurrentPlayable( ( ( items.length > 0 ) ? items[0] : null ) );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[0] : null ) );

            /* ################################################################# */
            /* test go to next & previous with repeat mode as current no shuffle */
            /* ################################################################# */
            session.goToNextPlayable( session.getCurrentPlayable(  ),
                                      false,
                                      RepeatMode.CURRENT );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[0] : null ) );

            session.goToNextPlayable( session.getCurrentPlayable(  ),
                                      false,
                                      RepeatMode.CURRENT );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[0] : null ) );

            session.goToPreviousPlayable( session.getCurrentPlayable(  ),
                                          false,
                                          RepeatMode.CURRENT );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[0] : null ) );

            session.goToPreviousPlayable( session.getCurrentPlayable(  ),
                                          false,
                                          RepeatMode.CURRENT );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[0] : null ) );

            /* ############################################################### */
            /** test go to next & previous with repeat mode as current shuffle */
            /* ############################################################### */
            session.goToNextPlayable( session.getCurrentPlayable(  ),
                                      true,
                                      RepeatMode.CURRENT );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[0] : null ) );

            session.goToNextPlayable( session.getCurrentPlayable(  ),
                                      true,
                                      RepeatMode.CURRENT );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[0] : null ) );

            session.goToPreviousPlayable( session.getCurrentPlayable(  ),
                                          true,
                                          RepeatMode.CURRENT );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[0] : null ) );

            session.goToPreviousPlayable( session.getCurrentPlayable(  ),
                                          true,
                                          RepeatMode.CURRENT );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[0] : null ) );

            /* ################################################################# */
            /* test go to next & previous with repeat mode as current no shuffle */
            /* ################################################################# */
            session.setCurrentPlayable( ( ( items.length > 0 ) ? items[items.length - 1] : null ) );

            session.goToNextPlayable( session.getCurrentPlayable(  ),
                                      false,
                                      RepeatMode.CURRENT );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[items.length - 1] : null ) );

            session.goToNextPlayable( session.getCurrentPlayable(  ),
                                      false,
                                      RepeatMode.CURRENT );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[items.length - 1] : null ) );

            session.goToPreviousPlayable( session.getCurrentPlayable(  ),
                                          false,
                                          RepeatMode.CURRENT );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[items.length - 1] : null ) );

            session.goToPreviousPlayable( session.getCurrentPlayable(  ),
                                          false,
                                          RepeatMode.CURRENT );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[items.length - 1] : null ) );

            /* ############################################################### */
            /** test go to next & previous with repeat mode as current shuffle */
            /* ############################################################### */
            session.goToNextPlayable( session.getCurrentPlayable(  ),
                                      true,
                                      RepeatMode.CURRENT );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[items.length - 1] : null ) );

            session.goToNextPlayable( session.getCurrentPlayable(  ),
                                      true,
                                      RepeatMode.CURRENT );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[items.length - 1] : null ) );

            session.goToPreviousPlayable( session.getCurrentPlayable(  ),
                                          true,
                                          RepeatMode.CURRENT );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[items.length - 1] : null ) );

            session.goToPreviousPlayable( session.getCurrentPlayable(  ),
                                          true,
                                          RepeatMode.CURRENT );

            assertTrue( session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[items.length - 1] : null ) );

            /* ####################################################### */
            /** test go to next & previous with repeat none as current */
            /* ####################################################### */
	    session.dispose();
            session.setCurrentPlayable( ( ( items.length > 0 ) ? items[0] : null ) );

            for ( int i = 1; i < 100; i++ )
            {
                session.goToNextPlayable( session.getCurrentPlayable(  ),
                                          false,
                                          RepeatMode.NONE );

                if ( i >= items.length )
                {
                    assertTrue( "i=" + i, session.getCurrentPlayable(  ) == null );
                } else
                {
                    assertTrue( "i=" + i, session.getCurrentPlayable(  ) == items[i] );
                }
            }

            /* ####################################################### */
            /** test go to next & previous with repeat none as current */
            /* ####################################################### */
	    session.dispose();
            session.setCurrentPlayable( ( ( items.length > 0 ) ? items[items.length - 1] : null ) );

            for ( int i = 1; i < 100; i++ )
            {
                session.goToPreviousPlayable( session.getCurrentPlayable(  ),
                                              false,
                                              RepeatMode.NONE );

                System.out.println( "i=" + i + " current playable : " +
                                    ( 
                                        ( session.getCurrentPlayable(  ) == null ) ? null
                                                                                   : session.getCurrentPlayable(  )
                                                                                            .getName(  )
                                     ) );

                if ( i >= items.length )
                {
                    assertTrue( "i=" + i + " and size=" + size,
                                session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[0] : null ) );
                } else
                {
                    assertTrue( "i=" + i + " and size=" + size,
                                session.getCurrentPlayable(  ) == items[items.length - i - 1] );
                }
            }

            /* ###################################################### */
            /** test go to next & previous with repeat all as current */
            /* ###################################################### */
            session.setCurrentPlayable( ( ( items.length > 0 ) ? items[0] : null ) );

            for ( int i = 1; i < 100; i++ )
            {
                session.goToNextPlayable( session.getCurrentPlayable(  ),
                                          false,
                                          RepeatMode.ALL );

                assertTrue( "i=" + i + " and size=" + size,
                            session.getCurrentPlayable(  ) == ( ( items.length > 0 ) ? items[i % items.length] : null ) );
            }

            /* ###################################################### */
            /** test go to next & previous with repeat all as current */
            /* ###################################################### */
	    session.dispose();
            session.setCurrentPlayable( ( ( items.length > 0 ) ? items[items.length - 1] : null ) );

            for ( int i = 1; i < 100; i++ )
            {
                session.goToPreviousPlayable( session.getCurrentPlayable(  ),
                                              false,
                                              RepeatMode.ALL );

                int position = ( items.length - i - 1 );

                if ( position >= 0 )
                {
                    position = position % items.length;
                } else
                {
                    if ( items.length > 0 )
                    {
                        while ( position < 0 )
                        {
                            position += items.length;
                        }
                    }
                }

                assertTrue( "i=" + i + " and size=" + size,
                            session.getCurrentPlayable(  ) == ( ( items.length == 0 ) ? null : items[position] ) );
            }
        }
    }

    public void testGen2(  )
                  throws PropertyVetoException
    {
	if ( ! TEST_2_ENABLED )
	{
	    return;
	}
        PlayList playlist = new PlayList(  );

        AudioItem[] items = new AudioItem[24];

        for ( int i = 0; i < items.length; i++ )
        {
            AudioItem item = new Mp3SongItem(  );
            items[i] = item;

            item.setName( "item" + ( i + 1 ) );

            playlist.add( item );
        }

        PlayableSession session = new PlayListPlayableSession( playlist );
        session.setCurrentPlayable( items[0] );

        session.goToNextPlayable( session.getCurrentPlayable(  ),
                                  false,
                                  RepeatMode.ALL );
        assertTrue( "current playable is : " + session.getCurrentPlayable(  ),
                    items[1] == session.getCurrentPlayable(  ) );

        session.goToNextPlayable( session.getCurrentPlayable(  ),
                                  false,
                                  RepeatMode.ALL );
        assertTrue( "current playable is : " + session.getCurrentPlayable(  ),
                    items[2] == session.getCurrentPlayable(  ) );

        session.goToNextPlayable( session.getCurrentPlayable(  ),
                                  false,
                                  RepeatMode.ALL );
        assertTrue( "current playable is : " + session.getCurrentPlayable(  ),
                    items[3] == session.getCurrentPlayable(  ) );

	session.goToNextPlayable(session.getCurrentPlayable(), false, RepeatMode.ALL);
	assertTrue("current playable is : " + session.getCurrentPlayable(), items[4] == session.getCurrentPlayable());
    }
    
    public void testGen3() throws PropertyVetoException
    {
	if ( ! TEST_3_ENABLED )
	{
	    return;
	}
	PlayList playlist = new PlayList(  );
	
	AudioItem  item0 = new Mp3SongItem();
	item0.setName("item0");
	playlist.add(item0);
	AudioItem  item1 = new Mp3SongItem();
	item1.setName("item1");
	playlist.add(item1);
	AudioItem  item2 = new Mp3SongItem();
	item2.setName("item2");
	playlist.add(item2);
	AudioItem  item3 = new Mp3SongItem();
	item3.setName("item3");
	playlist.add(item3);
	AudioItem  item4 = new Mp3SongItem();
	item4.setName("item4");
	playlist.add(item4);
	
	PlayListPlayableSession session = new PlayListPlayableSession( playlist );
	session.setCurrentPlayable( item0 );
	
	assertEquals(1, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	session.goToNextPlayable( session.getCurrentPlayable(  ), false, RepeatMode.ALL );
	
	assertEquals(2, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	assertTrue( "current playable is : " + session.getCurrentPlayable(  ),
		    item1 == session.getCurrentPlayable(  ) );
	
	session.setCurrentPlayable( item3 );
	assertEquals(3, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	assertTrue( "current playable is : " + session.getCurrentPlayable(  ),
		    item3 == session.getCurrentPlayable(  ) );
	
	session.goToPreviousPlayable( session.getCurrentPlayable(  ), false, RepeatMode.ALL );
	
	assertEquals(3, session.getPlayedItemCount());
	assertEquals(1, session.getPosition());
	assertTrue( "current playable is : " + session.getCurrentPlayable(  ),
		    item1 == session.getCurrentPlayable(  ) );
	
	session.setCurrentPlayable(item4);
	assertEquals(3, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	assertTrue( "current playable is : " + session.getCurrentPlayable(  ),
		    item4 == session.getCurrentPlayable(  ) );
    }
    
    public void testGen4() throws PropertyVetoException
    {
	if ( ! TEST_4_ENABLED )
	{
	    return;
	}
	PlayList playlist = new PlayList(  );
	
	AudioItem  item0 = new Mp3SongItem();
	item0.setName("item0");
	playlist.add(item0);
	AudioItem  item1 = new Mp3SongItem();
	item1.setName("item1");
	playlist.add(item1);
	AudioItem  item2 = new Mp3SongItem();
	item2.setName("item2");
	playlist.add(item2);
	AudioItem  item3 = new Mp3SongItem();
	item3.setName("item3");
	playlist.add(item3);
	AudioItem  item4 = new Mp3SongItem();
	item4.setName("item4");
	playlist.add(item4);
	
	PlayListPlayableSession session = new PlayListPlayableSession( playlist );
	session.setCurrentPlayable( item0 );
	
	assertEquals(1, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	session.goToNextPlayable( session.getCurrentPlayable(  ), false, RepeatMode.ALL );
	
	assertEquals(2, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	assertTrue( "current playable is : " + session.getCurrentPlayable(  ),
		    item1 == session.getCurrentPlayable(  ) );
	
	session.setCurrentPlayable( item3 );
	assertEquals(3, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	assertTrue( "current playable is : " + session.getCurrentPlayable(  ),
		    item3 == session.getCurrentPlayable(  ) );
	
	session.goToPreviousPlayable( session.getCurrentPlayable(  ), false, RepeatMode.ALL );
	
	assertEquals(3, session.getPlayedItemCount());
	assertEquals(1, session.getPosition());
	assertTrue( "current playable is : " + session.getCurrentPlayable(  ),
		    item1 == session.getCurrentPlayable(  ) );
	
	session.goToPreviousPlayable( session.getCurrentPlayable(  ), false, RepeatMode.ALL );
	
	assertEquals(3, session.getPlayedItemCount());
	assertEquals(0, session.getPosition());
	assertTrue( "current playable is : " + session.getCurrentPlayable(  ),
		    item0 == session.getCurrentPlayable(  ) );
	
	session.setCurrentPlayable(item3);
	assertEquals(2, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	assertTrue( "current playable is : " + session.getCurrentPlayable(  ),
		    item3 == session.getCurrentPlayable(  ) );
    }
    
    public void testGen5() throws PropertyVetoException
    {
	if ( ! TEST_5_ENABLED )
	{
	    return;
	}
	PlayList playlist = new PlayList(  );
	
	AudioItem  item0 = new Mp3SongItem();
	item0.setName("item0");
	playlist.add(item0);
	AudioItem  item1 = new Mp3SongItem();
	item1.setName("item1");
	playlist.add(item1);
	AudioItem  item2 = new Mp3SongItem();
	item2.setName("item2");
	playlist.add(item2);
	AudioItem  item3 = new Mp3SongItem();
	item3.setName("item3");
	playlist.add(item3);
	AudioItem  item4 = new Mp3SongItem();
	item4.setName("item4");
	playlist.add(item4);
	
	PlayListPlayableSession session = new PlayListPlayableSession( playlist );
	session.setCurrentPlayable( item0 );
	
	assertEquals(1, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	session.goToPreviousPlayable(session.getCurrentPlayable(), false, RepeatMode.NONE);
	
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item0);
    }
    
    public void testGen6() throws PropertyVetoException
    {
	if ( ! TEST_6_ENABLED )
	{
	    return;
	}
	PlayList playlist = new PlayList(  );
	
	AudioItem  item0 = new Mp3SongItem();
	item0.setName("item0");
	playlist.add(item0);
	AudioItem  item1 = new Mp3SongItem();
	item1.setName("item1");
	playlist.add(item1);
	AudioItem  item2 = new Mp3SongItem();
	item2.setName("item2");
	playlist.add(item2);
	AudioItem  item3 = new Mp3SongItem();
	item3.setName("item3");
	playlist.add(item3);
	AudioItem  item4 = new Mp3SongItem();
	item4.setName("item4");
	playlist.add(item4);
	
	PlayListPlayableSession session = new PlayListPlayableSession( playlist );
	session.setCurrentPlayable( item0 );
	
	assertEquals(1, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	session.goToPreviousPlayable(session.getCurrentPlayable(), false, RepeatMode.ALL);
	
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item4);
	
	assertEquals(2, session.getPlayedItemCount());
	assertEquals(0, session.getPosition());
    }
    
    public void testGen7() throws PropertyVetoException
    {
	if ( ! TEST_7_ENABLED )
	{
	    return;
	}
	PlayList playlist = new PlayList(  );
	
	AudioItem  item0 = new Mp3SongItem();
	item0.setName("item0");
	playlist.add(item0);
	AudioItem  item1 = new Mp3SongItem();
	item1.setName("item1");
	playlist.add(item1);
	AudioItem  item2 = new Mp3SongItem();
	item2.setName("item2");
	playlist.add(item2);
	AudioItem  item3 = new Mp3SongItem();
	item3.setName("item3");
	playlist.add(item3);
	AudioItem  item4 = new Mp3SongItem();
	item4.setName("item4");
	playlist.add(item4);
	
	PlayListPlayableSession session = new PlayListPlayableSession( playlist );
	session.setCurrentPlayable( item0 );
	
	assertEquals(1, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	session.goToPreviousPlayable(session.getCurrentPlayable(), false, RepeatMode.ALL);
	
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item4);
	
	assertEquals(2, session.getPlayedItemCount());
	assertEquals(0, session.getPosition());
	
	session.goToPreviousPlayable(session.getCurrentPlayable(), false, RepeatMode.ALL);
	
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item3);
	
	assertEquals(3, session.getPlayedItemCount());
	assertEquals(0, session.getPosition());
	
	session.goToNextPlayable(session.getCurrentPlayable(), false, RepeatMode.ALL);
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item4);
	
	assertEquals(3, session.getPlayedItemCount());
	assertEquals(1, session.getPosition());
	
	session.goToNextPlayable(session.getCurrentPlayable(), false, RepeatMode.ALL);
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item0);
	
	assertEquals(3, session.getPlayedItemCount());
	
	assertEquals(-1, session.getPosition());
    }
    
    public void testGen8() throws PropertyVetoException
    {
	if ( ! TEST_8_ENABLED )
	{
	    return;
	}
	PlayList playlist = new PlayList(  );
	
	AudioItem  item0 = new Mp3SongItem();
	item0.setName("item0");
	playlist.add(item0);
	AudioItem  item1 = new Mp3SongItem();
	item1.setName("item1");
	playlist.add(item1);
	AudioItem  item2 = new Mp3SongItem();
	item2.setName("item2");
	playlist.add(item2);
	AudioItem  item3 = new Mp3SongItem();
	item3.setName("item3");
	playlist.add(item3);
	AudioItem  item4 = new Mp3SongItem();
	item4.setName("item4");
	playlist.add(item4);
	
	PlayListPlayableSession session = new PlayListPlayableSession( playlist );
	session.setCurrentPlayable( item3 );
	
	assertEquals(1, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	session.setCurrentPlayable( item0 );
	
	assertEquals(2, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	session.goToPreviousPlayable(session.getCurrentPlayable(), false, RepeatMode.ALL);
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item3);
	
	assertEquals(2, session.getPlayedItemCount());
	assertEquals(0, session.getPosition());
	
	session.goToNextPlayable(session.getCurrentPlayable(), false, RepeatMode.ALL);
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item0);
	
	assertEquals(2, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	session.goToPreviousPlayable(session.getCurrentPlayable(), false, RepeatMode.ALL);
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item3);
	
	assertEquals(2, session.getPlayedItemCount());
	assertEquals(0, session.getPosition());
	
	session.setCurrentPlayable( item1 );
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item1);
	
	assertEquals(2, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	session.setCurrentPlayable( item4 );
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item4);
	
	assertEquals(3, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	session.goToPreviousPlayable(session.getCurrentPlayable(), false, RepeatMode.ALL);
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item1);
	
	assertEquals(3, session.getPlayedItemCount());
	assertEquals(1, session.getPosition());
	
	session.goToPreviousPlayable(session.getCurrentPlayable(), false, RepeatMode.ALL);
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item3);
	
	assertEquals(3, session.getPlayedItemCount());
	assertEquals(0, session.getPosition());
	
	session.goToPreviousPlayable(session.getCurrentPlayable(), false, RepeatMode.ALL);
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item2);
	
	assertEquals(4, session.getPlayedItemCount());
	assertEquals(0, session.getPosition());
	
	session.goToNextPlayable(session.getCurrentPlayable(), false, RepeatMode.ALL);
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item3);
	assertEquals(4, session.getPlayedItemCount());
	assertEquals(1, session.getPosition());
	
	session.goToNextPlayable(session.getCurrentPlayable(), false, RepeatMode.ALL);
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item1);
	assertEquals(4, session.getPlayedItemCount());
	assertEquals(2, session.getPosition());
	
	session.goToNextPlayable(session.getCurrentPlayable(), false, RepeatMode.ALL);
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item4);
	assertEquals(4, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	session.goToNextPlayable(session.getCurrentPlayable(), false, RepeatMode.ALL);
	assertTrue("" + session.getCurrentPlayable(), session.getCurrentPlayable() == item0);
	assertEquals(5, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
    }
    
    public void testGen9() throws PropertyVetoException
    {
	if ( ! TEST_9_ENABLED )
	{
	    return;
	}
	PlayList playlist = new PlayList(  );
	
	AudioItem  item0 = new Mp3SongItem();
	item0.setName("item0");
	playlist.add(item0);
	AudioItem  item1 = new Mp3SongItem();
	item1.setName("item1");
	playlist.add(item1);
	AudioItem  item2 = new Mp3SongItem();
	item2.setName("item2");
	playlist.add(item2);
	AudioItem  item3 = new Mp3SongItem();
	item3.setName("item3");
	playlist.add(item3);
	AudioItem  item4 = new Mp3SongItem();
	item4.setName("item4");
	playlist.add(item4);
	
	PlayListPlayableSession session = new PlayListPlayableSession( playlist );
	session.setCurrentPlayable( item3 );
	
	assertEquals(1, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	session.setCurrentPlayable( null );
	
	assertEquals(1, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	session.setCurrentPlayable( item3 );
	
	assertEquals(1, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
	
	session.setCurrentPlayable( null );
	
	assertEquals(1, session.getPlayedItemCount());
	assertEquals(-1, session.getPosition());
    }
	
}
