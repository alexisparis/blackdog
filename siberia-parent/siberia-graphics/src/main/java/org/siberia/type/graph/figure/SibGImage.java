package org.siberia.type.graph.figure;

import java.beans.PropertyVetoException;
import org.siberia.type.graph.VisualObject;
import org.siberia.type.SibString;
import org.siberia.type.AbstractSibType;

public class SibGImage extends GeometricObject
{
    // PENDING(api): revoir entièrement cette classe et son utilité
    /** file representing the image */
    private SibString file;
    
    public SibGImage()
    {   super(); }
    
    /** return a new VisualObject which proportions changed
     *  @param ratioX the X ratio for the proportions
     *  @param ratioY the Y ratio for the proportions
     **/
    public VisualObject createWithProportions(float ratioX, float ratioY)
    {   SibGImage newIma = new SibGImage();
        
        newIma.setOrigin((int)(this.getX().getValue().intValue() * ratioX),
                         (int)(this.getY().getValue().intValue() * ratioY));
        newIma.setSize((int)(this.getWidth().getValue().intValue()* ratioX),
                       (int)(this.getHeight().getValue().intValue() * ratioY));
        
        return newIma;
    }
    
    public void setFile(String image) throws PropertyVetoException
    {   this.file.setValue(image); }
    
    public String getImage()
    {   return (String)this.file.getValue(); }
    
}
