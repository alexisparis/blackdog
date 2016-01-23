package org.siberia.rc.persistence;

import java.io.File;
import org.siberia.type.ColdBoolean;
import org.siberia.type.ColdCollection;
import org.siberia.type.ColdColor;
import org.siberia.type.ColdEnumeration;
import org.siberia.type.ColdFloat;
import org.siberia.type.ColdFont;
import org.siberia.type.ColdList;
import org.siberia.type.ColdString;
import org.siberia.type.AbstractColdType;
import org.siberia.type.exception.NonConfigurableTypeException;
import org.siberia.xml.TypePersistenceManager;

/**
 *
 * @author alexis
 */
public class PersistenceTester
{
    
    /** Creates a new instance of PersistenceTester */
    public PersistenceTester()
    {
    }
    
    public static void main(String[] args)
    {
        ColdCollection list = new ColdList(null, "list_test", AbstractColdType.class, false, true, false);
        ColdString a = new ColdString(null, "a", "1");
        ColdString c = new ColdString(null, "b", "2");
        ColdBoolean b = new ColdBoolean(null, "r", true);
        list.add(a);
        list.add(b);
        list.add(c);
        ColdEnumeration en = new ColdEnumeration(null, "enum");
        en.setConfigurable(true);
        try
        {   
            en.setSelectionMode(ColdEnumeration.MULTIPLE_SELECTION);
            en.addElement("e");
            en.addElement("f", true);
            en.addElement("g", true);
            en.addElement("d", true);
            en.addElement("r", true);
            en.addElement("ty", true);
        }
        catch(NonConfigurableTypeException e)
        {   e.printStackTrace(); }
        
        ColdColor color = new ColdColor(null, "couleur", "#FF0022");
        ColdFloat f = new ColdFloat(null, "fl", 3.4f);
        ColdFont font = new ColdFont(null, "r");
        font.setFont("Arial");
        
        AbstractColdType type = color;
        try
        {
            File file = new File("/home/alexis/myDarkProject/metamodelisation/sr" +
                                "c/org/siberia/type/rc/saveType.xml");
            TypePersistenceManager.getInstance().marshall(type, file);
            
            System.err.println("MARSHALLED finished");
            
            AbstractColdType save = TypePersistenceManager.getInstance().unmarshall(file);
            
            System.out.println("isEquals : " + type.equals(save));
            if ( save == null )
                System.err.println("WARNING :: marshalled object is null");
            System.out.println("----------------------------------------------------------------------");
            System.out.println("name 1          : " + type.getName());
            System.out.println("name 2          : " + save.getName());
            System.out.println("----------------------------------------------------------------------");
            System.out.println("class 1         : " + type.getClass());
            System.out.println("class 2         : " + save.getClass());
            System.out.println("----------------------------------------------------------------------");
            if ( type instanceof ColdCollection && save instanceof ColdCollection )
            {   System.out.println("allowed class 1 : " + ((ColdCollection)type).getAllowedClass());
                System.out.println("allowed class 2 : " + ((ColdCollection)save).getAllowedClass());
                System.out.println("----------------------------------------------------------------------");
                System.out.println("size 1          : " + ((ColdCollection)type).size());
                System.out.println("size 2          : " + ((ColdCollection)save).size());
                System.out.println("----------------------------------------------------------------------");
            }
            
            System.out.println("toHtml : " + save.toHtml());
        }
        catch(Exception e)
        {   e.printStackTrace(); }
    }
    
}
