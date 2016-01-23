/*
 * AbstractColdTypeTest.java
 * JUnit based test
 *
 * Created on 2 septembre 2006, 10:05
 */

package org.siberia.base.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import junit.framework.*;

/**
 *
 * @author alexis
 */
public class CollectionIntersectorTest extends TestCase
{
    
    public CollectionIntersectorTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(CollectionIntersectorTest.class);
        
        return suite;
    }

    public void testIntersection()
    {   
	CollectionIntersector<String> stringIntersector = new CollectionIntersector<String>();
	
	Collection<String> list1  = null;
	Collection<String> list2  = null;
	Collection<String> list3  = null;
	Collection<String> list4  = null;
	Collection<String> list5  = null;
	
	Set<String>        result = null;
	
	/** 1 */
	result = stringIntersector.intersection(list1);
	assertEquals(0, result.size());
	
	/** 2 */
	result = stringIntersector.intersection(list1, list2);
	assertEquals(0, result.size());
	
	/** 3 */
	result = stringIntersector.intersection(list1, null);
	assertEquals(0, result.size());
	
	/** 4 */
	result = stringIntersector.intersection(null, list1);
	assertEquals(0, result.size());
	
	/** 5 */
	result = stringIntersector.intersection(null);
	assertEquals(0, result.size());
	
	/** 6 */
	list1 = createList();
	list2 = createList();
	result = stringIntersector.intersection(list1, list2);
	assertEquals(0, result.size());
	
	/** 7 */
	list1 = createList("a");
	list2 = createList("a");
	result = stringIntersector.intersection(list1, list2);
	assertEquals(1, result.size());
	assertTrue(equals(result, new String[]{"a"}));
	
	/** 8 */
	list1 = createList("a", "b");
	list2 = createList("a");
	result = stringIntersector.intersection(list1, list2);
	assertEquals(1, result.size());
	assertTrue(equals(result, new String[]{"a"}));
	
	/** 9 */
	list1 = createList("a");
	list2 = createList("a", "b");
	result = stringIntersector.intersection(list1, list2);
	assertEquals(1, result.size());
	assertTrue(equals(result, new String[]{"a"}));
	
	/** 10 */
	list1 = createList("a", "b");
	list2 = createList("a", "b");
	result = stringIntersector.intersection(list1, list2);
	assertEquals(2, result.size());
	assertTrue(equals(result, new String[]{"a", "b"}));
	
	/** 10 */
	list1 = createList("a",      "c");
	list2 = createList("a", "b", "c");
	list3 = createList("a",      "c", "d");
	result = stringIntersector.intersection(list1, list2, list3);
	assertEquals(2, result.size());
	assertTrue(equals(result, new String[]{"c", "a"}));
    }
    
    /** create a list according to the given items
     */
    private Collection<String> createList()
    {
	return this.createList((String[])null);
    }
    
    /** create a list according to the given items
     */
    private Collection<String> createList(String... items)
    {
	Collection<String> collec = new ArrayList<String>();
	
	if ( items != null )
	{
	    for(int i = 0; i < items.length; i++)
	    {
		collec.add(items[i]);
	    }
	}
	
	return collec;
    }
    
    /** return true if the set seems to be equals to array
     */
    private boolean equals(Set set, Object[] objects)
    {
	boolean result = false;
	
	if ( set == null || set.size() == 0 )
	{
	    if ( objects == null || objects.length == 0 )
	    {
		result = true;
	    }
	}
	else
	{
	    if ( objects != null )
	    {
		if ( objects.length == set.size() )
		{
		    result = true;
		    
		    for(int i = 0; i < objects.length; i++)
		    {
			Object current = objects[i];
			
			if ( ! set.contains(current) )
			{
			    result = false;
			    break;
			}
		    }
		}
	    }
	}
	
	return result;
    }
}
