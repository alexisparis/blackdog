package org.siberia.utilities.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import junit.framework.*;

/**
 *
 * @author alexis
 */
public class IteratorGroupTest extends TestCase
{
    public IteratorGroupTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(IteratorGroupTest.class);
        
        return suite;
    }
    
    public void testIterable1()
    {
        IteratorGroup<String> it = new IteratorGroup<String>((Iterable<String>)null);
        
        assertFalse(it.hasNext());
        
        it = new IteratorGroup<String>((Iterable<String>)null);
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }

    public void testIterable2()
    {
        Collection<String> collec1 = null;
        
        Collection<String> collec2 = null;
        
        Collection<String> collec3 = null;
                
        IteratorGroup<String> it = new IteratorGroup<String>(collec1, collec2, collec3);
        
        assertFalse(it.hasNext());
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }

    public void testIterable3()
    {
        Collection<String> collec1 = new ArrayList<String>();
        
        Collection<String> collec2 = null;
        
        Collection<String> collec3 = new ArrayList<String>();
                
        IteratorGroup<String> it = new IteratorGroup<String>(collec1, collec2, collec3);
        
        assertFalse(it.hasNext());
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }

    public void testIterable4()
    {
        Collection<String> collec1 = new ArrayList<String>();
        collec1.add("c");
        
        IteratorGroup<String> it = new IteratorGroup<String>(collec1);
        
        assertEquals("c", it.next());
        
        assertFalse(it.hasNext());
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }

    public void testIterable5()
    {
        Collection<String> collec1 = null;
        
        Collection<String> collec2 = null;
        
        Collection<String> collec3 = new ArrayList<String>();
        collec3.add("c");
        
        IteratorGroup<String> it = new IteratorGroup<String>(collec1, collec2, collec3);
        
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        
        assertEquals("c", it.next());
        
        assertFalse(it.hasNext());
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }

    public void testIterable6()
    {
        Collection<String> collec1 = new ArrayList<String>();
        collec1.add("a");
        collec1.add("b");
        
        Collection<String> collec2 = null;
        
        Collection<String> collec3 = new ArrayList<String>();
        collec3.add("c");
        collec3.add("d");
                
        IteratorGroup<String> it = new IteratorGroup<String>(collec1, collec2, collec3);
        
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        
        assertEquals("a", it.next());
        
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        
        assertEquals("b", it.next());
        
        assertEquals("c", it.next());
        
        assertEquals("d", it.next());
        assertFalse(it.hasNext());
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }

    public void testIterable7()
    {
        Collection<String> collec1 = new ArrayList<String>();
        collec1.add("a");
        collec1.add("b");
        
        Collection<String> collec2 = null;
        
        Collection<String> collec3 = new ArrayList<String>();
        collec1.add("c");
        collec1.add("d");
                
        IteratorGroup<String> it = new IteratorGroup<String>(collec1, collec2, collec3);
        
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        
        assertEquals("a", it.next());
        
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        
        assertEquals("b", it.next());
        
        assertEquals("c", it.next());
        
        assertEquals("d", it.next());
        assertFalse(it.hasNext());
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }

    public void testIterable8()
    {
        Collection<String> collec1 = new ArrayList<String>();
        collec1.add("a");
        collec1.add("b");
        
        Collection<String> collec2 = null;
        
        Collection<String> collec3 = new ArrayList<String>();
        collec1.add("c");
        collec1.add("d");
                
        IteratorGroup<String> it = new IteratorGroup<String>(collec1, collec2, collec3);
        
        assertEquals("a", it.next());
        
        assertEquals("b", it.next());
        
        assertEquals("c", it.next());
        
        assertEquals("d", it.next());
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }
    
    /* #########################################################################
     * ################################ Iterator ###############################
     * ######################################################################### */
    
    
    public void testIterator1()
    {
        IteratorGroup<String> it = new IteratorGroup<String>((Iterator<String>)null);
        
        assertFalse(it.hasNext());
        
        it = new IteratorGroup<String>((Iterable<String>)null);
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }

    public void testIterator2()
    {
        Iterator<String> ite1 = null;
        
        Iterator<String> ite2 = null;
        
        Iterator<String> ite3 = null;
                
        IteratorGroup<String> it = new IteratorGroup<String>(ite1, ite2, ite3);
        
        assertFalse(it.hasNext());
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }

    public void testIterator3()
    {
        Collection<String> collec1 = new ArrayList<String>();
        
        Iterator<String> ite2 = null;
        
        Collection<String> collec3 = new ArrayList<String>();
                
        IteratorGroup<String> it = new IteratorGroup<String>(collec1.iterator(), ite2, collec3.iterator());
        
        assertFalse(it.hasNext());
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }

    public void testIterator4()
    {
        Collection<String> collec1 = new ArrayList<String>();
        collec1.add("c");
        
        IteratorGroup<String> it = new IteratorGroup<String>(collec1.iterator());
        
        assertEquals("c", it.next());
        
        assertFalse(it.hasNext());
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }

    public void testIterator5()
    {
        Iterator<String> ite1 = null;
        
        Iterator<String> ite2 = null;
        
        Collection<String> collec3 = new ArrayList<String>();
        collec3.add("c");
        
        IteratorGroup<String> it = new IteratorGroup<String>(ite1, ite2, collec3.iterator());
        
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        
        assertEquals("c", it.next());
        
        assertFalse(it.hasNext());
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }

    public void testIterator6()
    {
        Collection<String> collec1 = new ArrayList<String>();
        collec1.add("a");
        collec1.add("b");
        
        Iterator<String> ite2 = null;
        
        Collection<String> collec3 = new ArrayList<String>();
        collec3.add("c");
        collec3.add("d");
                
        IteratorGroup<String> it = new IteratorGroup<String>(collec1.iterator(), ite2, collec3.iterator());
        
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        
        assertEquals("a", it.next());
        
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        
        assertEquals("b", it.next());
        
        assertEquals("c", it.next());
        
        assertEquals("d", it.next());
        assertFalse(it.hasNext());
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }

    public void testIterator7()
    {
        Collection<String> collec1 = new ArrayList<String>();
        collec1.add("a");
        collec1.add("b");
        
        Iterator<String> ite2 = null;
        
        Collection<String> collec3 = new ArrayList<String>();
        collec1.add("c");
        collec1.add("d");
                
        IteratorGroup<String> it = new IteratorGroup<String>(collec1.iterator(), ite2, collec3.iterator());
        
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        
        assertEquals("a", it.next());
        
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        
        assertEquals("b", it.next());
        
        assertEquals("c", it.next());
        
        assertEquals("d", it.next());
        assertFalse(it.hasNext());
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }

    public void testIterator8()
    {
        Collection<String> collec1 = new ArrayList<String>();
        collec1.add("a");
        collec1.add("b");
        
        Iterator<String> ite2 = null;
        
        Collection<String> collec3 = new ArrayList<String>();
        collec1.add("c");
        collec1.add("d");
                
        IteratorGroup<String> it = new IteratorGroup<String>(collec1.iterator(), ite2, collec3.iterator());
        
        assertEquals("a", it.next());
        
        assertEquals("b", it.next());
        
        assertEquals("c", it.next());
        
        assertEquals("d", it.next());
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }

    public void testIterator9()
    {
        Collection<String> collec1 = new ArrayList<String>();
        collec1.add("a");
        collec1.add("b");
        
        Iterator<String> ite2 = null;
        
        Collection<String> collec3 = new ArrayList<String>();
        collec1.add("c");
        collec1.add("d");
        
        Iterator<String> ite1 = collec1.iterator();
        ite1.next();
                
        IteratorGroup<String> it = new IteratorGroup<String>(ite1, ite2, collec3.iterator());
        
        assertEquals("b", it.next());
        
        assertEquals("c", it.next());
        
        assertEquals("d", it.next());
        
        try
        {   it.next();
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }
    
}
