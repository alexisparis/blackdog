package org.siberia.utilities.task;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import junit.framework.*;

/**
 *
 * @author alexis
 */
public class TaskStatusContainerTest extends TestCase
{
    public TaskStatusContainerTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(TaskStatusContainerTest.class);
        
        return suite;
    }
    
    public void testWithOnlyOneTaskStatus()
    {
        final TaskStatusContainer container = new TaskStatusContainer();
        final SimpleTaskStatus status = new SimpleTaskStatus("", "label");
        container.append(status, 100);
        
        assertEquals("label", status.getLabel());
        
        final float[]  ratio = new float[]{10f, 48f, 79f, 100f};
        final String[] text  = new String[]{"a", "b", "c", "d"};
        
        
        container.addPropertyChangeListener(new PropertyChangeListener()
        {
            int currentRatioIndex = 0;
            int currentTextIndex  = 0;
            
            public void propertyChange(PropertyChangeEvent evt)
            {
                if ( evt.getSource() == container )
                {   if ( evt.getPropertyName().equals(TaskStatus.PROPERTY_LABEL) )
                    {   int index = currentTextIndex;
                        assertEquals("at index=" + index, text[currentTextIndex++], evt.getNewValue());
                    }
                    else if ( evt.getPropertyName().equals(TaskStatus.PROPERTY_COMPLETED) )
                    {   int index = currentRatioIndex;
                        assertEquals("currentRatioIndex=" + index, ratio[currentRatioIndex++], evt.getNewValue());
                    }
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
    
    public void testWithSeverallTaskStatus()
    {
        final TaskStatusContainer container = new TaskStatusContainer();
        final SimpleTaskStatus status = new SimpleTaskStatus("", "label");
        final SimpleTaskStatus status2 = new SimpleTaskStatus("", "label_2");
        
        assertEquals("label", status.getLabel());
        
        final float[]  ratio   = new float[]{50f, 80f, 100f};
        final String[] text    = new String[]{"a", "b", "c"};
        final float[]  ratio_2 = new float[]{25f, 75f, 80f, 100f};
        final String[] text_2  = new String[]{"d", "e", "f", "g"};
        
        final float[]  ratioResult = new float[]{(1f/3f), (8f/15f), (2f/3f), (1125f/1500f), (1375f/1500f), (14f/15f), 1};
        final String[] textResult  = new String[]{"label", "a", "b", "c", "label_2", "d", "e", "f", "g"};
        
        
        container.addPropertyChangeListener(new PropertyChangeListener()
        {
            int currentRatioIndex = 0;
            int currentTextIndex  = 0;
            
            public void propertyChange(PropertyChangeEvent evt)
            {
                if ( evt.getSource() == container )
                {   if ( evt.getPropertyName().equals(TaskStatus.PROPERTY_LABEL) )
                    {   int index = currentTextIndex;
                        assertEquals("for index " + index, textResult[currentTextIndex++], evt.getNewValue());
                    }
                    else if ( evt.getPropertyName().equals(TaskStatus.PROPERTY_COMPLETED) )
                    {   int index = currentRatioIndex;
                        float result = ratioResult[currentRatioIndex++];
                        double gap = result - (((Number)evt.getNewValue()).doubleValue() / 100f );
                        System.out.println("gap : " + gap + ", expected : " + result + ", result : " + (((Number)evt.getNewValue()).doubleValue() / 100f ));
                        assertTrue("for index " + index, (gap < 0.01f) );
                    }
                }
            }
        });
        
        container.append(status, 100);
        container.append(status2, 50);
        
        status.setLabel(text[0]);
        status.setPercentageCompleted(ratio[0]);
        status.setPercentageCompleted(ratio[1]);
        status.setLabel(text[1]);
        status.setLabel(text[2]);
        status.setPercentageCompleted(ratio[2]);
        
        status2.setLabel(text_2[0]);
        status2.setLabel(text_2[1]);
        status2.setPercentageCompleted(ratio_2[0]);
        status2.setLabel(text_2[2]);
        status2.setLabel(text_2[3]);
        status2.setPercentageCompleted(ratio_2[1]);
        status2.setPercentageCompleted(ratio_2[2]);
        status2.setPercentageCompleted(ratio_2[3]);
    }
}
