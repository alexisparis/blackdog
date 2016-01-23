package org.siberia.utilities.task;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import junit.framework.*;

/**
 *
 * @author alexis
 */
public class SimpleTaskStatusTest extends TestCase
{
    public SimpleTaskStatusTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(SimpleTaskStatusTest.class);
        
        return suite;
    }
    
    public void test0()
    {
        final SimpleTaskStatus status = new SimpleTaskStatus("", "label");
        
        assertEquals("label", status.getLabel());
        
        final float[]  ratio = new float[]{12f, 50f, 78f, 100f};
        final String[] text  = new String[]{"a", "b", "c", "d"};
        
        
        status.addPropertyChangeListener(new PropertyChangeListener()
        {
            int currentRatioIndex = 0;
            int currentTextIndex  = 0;
            
            public void propertyChange(PropertyChangeEvent evt)
            {
                if ( evt.getSource() == status )
                {   if ( evt.getPropertyName().equals(TaskStatus.PROPERTY_LABEL) )
                    {   assertEquals(text[currentTextIndex++], evt.getNewValue()); }
                    else if ( evt.getPropertyName().equals(TaskStatus.PROPERTY_COMPLETED) )
                    {   assertEquals(ratio[currentRatioIndex++], evt.getNewValue()); }
                }
            }
        });
        
        status.setLabel(text[0]);
        status.setPercentageCompleted(ratio[0]);
        status.setPercentageCompleted(ratio[1]);
        status.setLabel(text[1]);
        status.setPercentageCompleted(ratio[2]);
        status.setLabel(text[2]);
        status.setPercentageCompleted(ratio[3]);
        status.setLabel(text[3]);
    }
}
