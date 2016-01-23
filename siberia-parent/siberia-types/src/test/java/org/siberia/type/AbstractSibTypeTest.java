/*
 * AbstractColdTypeTest.java
 * JUnit based test
 *
 * Created on 2 septembre 2006, 10:05
 */

package org.siberia.type;

import junit.framework.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Iterator;

/**
 *
 * @author alexis
 */
public class AbstractSibTypeTest extends TestCase
{
    
    public AbstractSibTypeTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(AbstractSibTypeTest.class);
        
        return suite;
    }

    public void testIsLeaf()
    {   SibType instance = new SibTypeImpl();
        SibType child    = new SibTypeImpl();
        
        boolean expResult = false;
        boolean result = instance.isLeaf();
        assertEquals(expResult, result);
        
        expResult = true;
        result = instance.isLeaf();
        assertEquals(expResult, result);
        
        child.setParent(instance);
        
        expResult = false;
        result = instance.isLeaf();
        assertEquals(expResult, result);
        
        instance.removeChildElement(child);
        
        expResult = true;
        result = instance.isLeaf();
        assertEquals(expResult, result);
    }

    public void testIsReadOnly()
    {   
        AbstractSibType instance = new SibTypeImpl();
        
        boolean expResult = false;
        boolean result = instance.isReadOnly();
        assertEquals(expResult, result);
        
        instance.setReadOnly(false);
        expResult = false;
        result = instance.isReadOnly();
        assertEquals(expResult, result);
        
        instance.setReadOnly(true);
        expResult = true;
        result = instance.isReadOnly();
        assertEquals(expResult, result);
    }

    public void testSetReadOnly()
    {   
        AbstractSibType instance = new SibTypeImpl();
        
        boolean readOnly = true;
        instance.setReadOnly(readOnly);
        assertTrue(instance.isReadOnly());
        
        readOnly = false;
        instance.setReadOnly(readOnly);
        assertFalse(instance.isReadOnly());
        
        readOnly = true;
        instance.setReadOnly(readOnly);
        assertTrue(instance.isReadOnly());
    }

    public void testIsValid()
    {
        AbstractSibType instance = new SibTypeImpl();
        
        boolean expResult = true;
        boolean result = instance.isValid();
        assertEquals(expResult, result);
    }

    public void testToString()
    {   
        AbstractSibType instance = new SibTypeImpl();
        
        int maxLength = 0;
        String expResult = "name : []";
        String result = instance.toString(maxLength);
        assertEquals(expResult.substring(0, maxLength), result);
        
        maxLength = 10;
        expResult = "name : []";
        result = instance.toString(maxLength);
        assertEquals(expResult.substring(0, maxLength), result);
        
        maxLength = 9;
        expResult = "name : []";
        result = instance.toString(maxLength);
        assertEquals(expResult.substring(0, maxLength), result);
    }

    public void testValueAsString()
    {
        AbstractSibType instance = new SibTypeImpl();
        
        String expResult = "value as string";
        String result = instance.valueAsString();
        assertEquals(expResult, result);
    }

    public void testClone() throws PropertyVetoException
    {
        AbstractSibType instance  = new SibTypeImpl();
        AbstractSibType instance2 = new SibTypeImpl();
        
        assertEquals(instance2, instance);
        
        instance2.setName("nom");
        assertNotSame(instance2, instance);
        
        instance2.setName("name");
        assertEquals(instance2, instance);
    }

    public void testEquals() throws PropertyVetoException
    {
        AbstractSibType instance  = new SibTypeImpl();
        AbstractSibType instance2 = new SibTypeImpl();
        
        assertTrue(instance2.equals(instance));
        
        instance2.setName("nom");
        assertFalse(instance2.equals(instance));
        
        instance2.setName("name");
        assertTrue(instance2.equals(instance));
    }

    public void testHashCode()
    {
        AbstractSibType instance = new SibTypeImpl();
        
        int expResult = instance.getName().hashCode();
        int result = instance.hashCode();
        assertEquals(expResult, result);
    }

    public void testSetParent() throws PropertyVetoException
    {   
        SibType parent = new SibTypeImpl();
        parent.setName("parent");
        AbstractSibType instance = new SibTypeImpl();
        
        instance.setParent(parent);
        
        assertTrue( instance.getParent() == parent );
        
        instance.setParent(null);
        
        assertNull( instance.getParent() );
    }

    public void testGetParent()
    {
        AbstractSibType instance = new SibTypeImpl();
        
        SibType expResult = null;
        SibType result = instance.getParent();
        assertEquals(expResult, result);
    }

    public void testGetPath()
    {
        AbstractSibType ins1 = new SibTypeImpl();
        AbstractSibType ins2 = new SibTypeImpl();
        AbstractSibType ins3 = new SibTypeImpl();
        AbstractSibType ins4 = new SibTypeImpl();
        AbstractSibType ins5 = new SibTypeImpl();
        AbstractSibType ins6 = new SibTypeImpl();
        
        ins6.setParent(ins5);
        ins5.setParent(ins4);
        ins4.setParent(ins3);
        ins3.setParent(ins2);
        ins2.setParent(ins1);
        
        SibType[] result = ins1.getPath();
        assertNull(result);
        
        result = ins1.getPath();
        assertEquals(5, result.length);
        
        assertTrue( result[0] == ins1 );
        assertTrue( result[1] == ins2 );
        assertTrue( result[2] == ins3 );
        assertTrue( result[3] == ins4 );
        assertTrue( result[4] == ins5 );
    }

    public void testGetChildrenCount()
    {
        AbstractSibType ins1 = new SibTypeImpl();
        AbstractSibType ins2 = new SibTypeImpl();
        AbstractSibType ins3 = new SibTypeImpl();
        AbstractSibType ins4 = new SibTypeImpl();
        AbstractSibType ins5 = new SibTypeImpl();
        AbstractSibType ins6 = new SibTypeImpl();
        
        ins6.setParent(ins5);
        ins6.setParent(ins4);
        ins6.setParent(ins3);
        ins6.setParent(ins2);
        ins6.setParent(ins1);
        
        assertEquals(5, ins6.getChildrenCount());
        assertEquals(0, ins1.getChildrenCount());
    }

    public void testContainsChild()
    {
        AbstractSibType ins1 = new SibTypeImpl();
        AbstractSibType ins2 = new SibTypeImpl();
        AbstractSibType ins3 = new SibTypeImpl();
        AbstractSibType ins4 = new SibTypeImpl();
        AbstractSibType ins5 = new SibTypeImpl();
        AbstractSibType ins6 = new SibTypeImpl();
        
        ins6.setParent(ins5);
        ins5.setParent(ins4);
        ins5.setParent(ins3);
        ins5.setParent(ins2);
        ins5.setParent(ins1);
        
        assertTrue(ins6.containsChild(ins5));
        assertFalse(ins6.containsChild(ins4));
        
        assertTrue(ins5.containsChild(ins3));
        assertFalse(ins3.containsChild(ins5));
    }

    public void testGetChildAt()
    {
        AbstractSibType ins1 = new SibTypeImpl();
        AbstractSibType ins2 = new SibTypeImpl();
        AbstractSibType ins3 = new SibTypeImpl();
        AbstractSibType ins4 = new SibTypeImpl();
        AbstractSibType ins5 = new SibTypeImpl();
        AbstractSibType ins6 = new SibTypeImpl();
        
        ins6.setParent(ins5);
        ins6.setParent(ins4);
        ins6.setParent(ins3);
        ins6.setParent(ins2);
        ins6.setParent(ins1);
        
        assertTrue(ins5 == ins6.getChildAt(0));
        assertTrue(ins4 == ins6.getChildAt(1));
        assertTrue(ins3 == ins6.getChildAt(2));
        assertTrue(ins2 == ins6.getChildAt(3));
        assertTrue(ins1 == ins6.getChildAt(4));
        assertNull(ins6.getChildAt(5));
    }

    public void testAddChildElement()
    {
        SibType item = new SibTypeImpl();
        AbstractSibType instance = new SibTypeImpl();
        
//        instance.addChildElement(null);
//        
//        instance.addChildElement(item);
//        
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    public void testInsertChildElement()
    {
        System.out.println("insertChildElement");
        
        int index = 0;
        SibType item = null;
        AbstractSibType instance = new SibTypeImpl();
        
        instance.insertChildElement(index, item);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testRemoveChildElement()
    {
        System.out.println("removeChildElement");
        
        SibType item = null;
        AbstractSibType instance = new SibTypeImpl();
        
        instance.removeChildElement(item);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testChildren()
    {
        System.out.println("children");
        
        AbstractSibType instance = new SibTypeImpl();
        
        Iterator<SibType> expResult = null;
        Iterator<SibType> result = instance.children();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testHasForAncestor()
    {
        System.out.println("hasForAncestor");
        
        SibType object = null;
        AbstractSibType instance = new SibTypeImpl();
        
        boolean expResult = true;
        boolean result = instance.hasForAncestor(object);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testGetFirstAncestorOfType()
    {
        System.out.println("getFirstAncestorOfType");
        
        Class classObject = null;
        AbstractSibType instance = new SibTypeImpl();
        
        SibType expResult = null;
        SibType result = instance.getFirstAncestorOfType(classObject);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testIndexOfChild()
    {
        System.out.println("indexOfChild");
        
        Object child = null;
        AbstractSibType instance = new SibTypeImpl();
        
        int expResult = 0;
        int result = instance.indexOfChild(child);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testGetIndexInParent()
    {
        System.out.println("getIndexInParent");
        
        AbstractSibType instance = new SibTypeImpl();
        
        int expResult = 0;
        int result = instance.getIndexInParent();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testGetChildAsValue()
    {
        System.out.println("getChildAsValue");
        
        String code = "";
        AbstractSibType instance = new SibTypeImpl();
        
        String expResult = "";
        String result = instance.getChildAsValue(code);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testGetChildNamed()
    {
        System.out.println("getChildNamed");
        
        String name = "";
        AbstractSibType instance = new SibTypeImpl();
        
        SibType expResult = null;
        SibType result = instance.getChildNamed(name);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testFirePropertyChange()
    {
        System.out.println("firePropertyChange");
        
        String propertyName = "";
        Object oldValue = null;
        Object newValue = null;
        AbstractSibType instance = new SibTypeImpl();
        
        instance.firePropertyChange(propertyName, oldValue, newValue);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testAddPropertyChangeListener()
    {
        System.out.println("addPropertyChangeListener");
        
        PropertyChangeListener listener = null;
        AbstractSibType instance = new SibTypeImpl();
        
        instance.addPropertyChangeListener(listener);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testRemovePropertyChangeListener()
    {
        System.out.println("removePropertyChangeListener");
        
        PropertyChangeListener listener = null;
        AbstractSibType instance = new SibTypeImpl();
        
        instance.removePropertyChangeListener(listener);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testClearPropertyChangeListenerList()
    {
        System.out.println("clearPropertyChangeListenerList");
        
        AbstractSibType instance = new SibTypeImpl();
        
        instance.clearPropertyChangeListenerList();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testToHtml()
    {
        System.out.println("toHtml");
        
        AbstractSibType instance = new SibTypeImpl();
        
        String expResult = "";
        String result = instance.toHtml();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testGetHtmlContentAndDesc()
    {
        System.out.println("getHtmlContentAndDesc");
        
        AbstractSibType instance = new SibTypeImpl();
        
        String expResult = "";
        String result = instance.getHtmlContentAndDesc();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testGetHtmlContent()
    {
        System.out.println("getHtmlContent");
        
        AbstractSibType instance = new SibTypeImpl();
        
        String expResult = "";
        String result = instance.getHtmlContent();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testGetName()
    {
        System.out.println("getName");
        
        AbstractSibType instance = new SibTypeImpl();
        
        String expResult = "";
        String result = instance.getName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testSetName() throws PropertyVetoException
    {
        System.out.println("setName");
        
        String value = "";
        AbstractSibType instance = new SibTypeImpl();
        
        instance.setName(value);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testNameCouldChange()
    {
        System.out.println("nameCouldChange");
        
        AbstractSibType instance = new SibTypeImpl();
        
        boolean expResult = true;
        boolean result = instance.nameCouldChange();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testSetNameCouldChange() throws PropertyVetoException
    {
        System.out.println("setNameCouldChange");
        
        boolean couldChange = true;
        AbstractSibType instance = new SibTypeImpl();
        
        instance.setNameCouldChange(couldChange);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testCanBeMoved()
    {
        System.out.println("canBeMoved");
        
        AbstractSibType instance = new SibTypeImpl();
        
        boolean expResult = true;
        boolean result = instance.canBeMoved();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public void testSetMoveable() throws PropertyVetoException
    {
        System.out.println("setMoveable");
        
        boolean moveable = true;
        AbstractSibType instance = new SibTypeImpl();
        
        instance.setMoveable(moveable);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    private class SibTypeImpl extends AbstractSibType
    {

        /*  return a String representation of the value
         *  @return a String representation of the value
         */
        public String valueAsString()
        {   return "value as string"; }
        
    }

    
}
