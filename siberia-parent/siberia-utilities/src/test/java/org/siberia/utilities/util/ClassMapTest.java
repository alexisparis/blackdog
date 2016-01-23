package org.siberia.utilities.util;

import java.util.List;
import junit.framework.*;

/**
 *
 * @author alexis
 */
public class ClassMapTest extends TestCase
{
    public ClassMapTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(ClassMapTest.class);
        
        return suite;
    }
    
    //
    //                  Classes           Interfaces
    //
    //                      A    B          I1
    //                     / \
    //                    /   \
    //                  A1_1  A1_2
    //                 /         
    //                /           
    //              A2_1
    //              /
    //             /
    //           A3_1             C         I2
    //
    
    public void test0()
    {
        ClassMap<String> map = new ClassMap<String>();
        
        map.put(A.class   , "a");
        map.put(A1_1.class, "a1_1");
        map.put(A2_1.class, "a2_1");
        map.put(A3_1.class, "a3_1");
        map.put(A1_2.class, "a1_2");
        
        map.put(I1.class  , "i1");
        map.put(I2.class  , "i2");
        
        assertEquals("a", map.search(A.class));
        assertEquals("a1_1", map.search(A1_1.class));
        assertEquals("a1_2", map.search(A1_2.class));
        assertEquals("a2_1", map.search(A2_1.class));
        assertEquals("a3_1", map.search(A3_1.class));
        assertEquals("i1", map.search(I1.class));
        assertEquals("i2", map.search(I2.class));
        assertEquals("i1", map.search(B.class));
        assertEquals("i2", map.search(C.class));
        assertNull(map.search(String.class));
        assertNull(map.search(Integer.class));
        
        assertEquals("a", map.get(A.class));
        assertEquals("a1_1", map.get(A1_1.class));
        assertEquals("a1_2", map.get(A1_2.class));
        assertEquals("a2_1", map.get(A2_1.class));
        assertEquals("a3_1", map.get(A3_1.class));
        assertEquals("i1", map.get(I1.class));
        assertEquals("i2", map.get(I2.class));
        assertNull(map.get(B.class));
        assertNull(map.get(C.class));
        assertNull(map.get(String.class));
        assertNull(map.get(Integer.class));
    }
    
    public void test1()
    {
        ClassMap<String> map = new ClassMap<String>();
        
        map.put(A.class   , "a");
        map.put(A1_1.class, "a1_1");
        map.put(A2_1.class, "a2_1");
        map.put(A3_1.class, "a3_1");
        map.put(A1_2.class, "a1_2");
        
        map.put(I1.class  , "i1");
        map.put(I2.class  , "i2");
        
        /* declare entities */
        Object o = null;
        List<String> list = null;
        
        o = new A();
        list = map.getRecursively(o);
        
        assertEquals(2, list.size());
        assertTrue(list.contains("a"));
        assertTrue(list.contains("i1"));
        
        /** put the same element as for class A */
        map.put(I1.class  , "a");
        
        list = map.getRecursively(o, true);
        
        assertEquals(1, list.size());
        assertEquals("a", list.get(0));
        
        map.put(I1.class  , "i1");
    }
    
    public void test2()
    {
        ClassMap<String> map = new ClassMap<String>();
        
        map.put(A.class   , "a");
        map.put(A1_1.class, "a1_1");
        map.put(A2_1.class, "a2_1");
        map.put(A3_1.class, "a3_1");
        map.put(A1_2.class, "a1_2");
        
        map.put(I1.class  , "i1");
        map.put(I2.class  , "i2");
        
        /* declare entities */
        Object o = null;
        List<String> list = null;
        
        o = new A1_1();
        list = map.getRecursively(o);
        
        assertEquals(3, list.size());
        assertTrue(list.contains("a"));
        assertTrue(list.contains("i1"));
        assertTrue(list.contains("a1_1"));
        
        map.put(A.class   , "a1_1");
        list = map.getRecursively(o, true);
        
        assertEquals(2, list.size());
        assertTrue(list.contains("i1"));
        assertTrue(list.contains("a1_1"));
    }
    
    public void test3()
    {
        ClassMap<String> map = new ClassMap<String>();
        
        map.put(A.class   , "a");
        map.put(A1_1.class, "a1_1");
        map.put(A2_1.class, "a2_1");
        map.put(A3_1.class, "a3_1");
        map.put(A1_2.class, "a1_2");
        
        map.put(I1.class  , "i1");
        map.put(I2.class  , "i2");
        
        /* declare entities */
        Object o = null;
        List<String> list = null;
        
        o = new C();
        list = map.getRecursively(o);
        
        assertEquals(1, list.size());
        assertTrue(list.contains("i2"));
        
        o = new B();
        list = map.getRecursively(o);
        
        assertEquals(1, list.size());
        assertTrue(list.contains("i1"));
    }
    
    /** inner classes */
    private interface I1
    {   }
    
    private interface I2
    {   }
    
    private class A implements I1
    {   }
    
    private class B implements I1
    {   }
    
    private class A1_1 extends A
    {   }
    
    private class A1_2 extends A
    {   }
    
    private class A2_1 extends A1_1
    {   }
    
    private class A3_1 extends A2_1 implements I2
    {   }
    
    private class C implements I2
    {   }
    
}
