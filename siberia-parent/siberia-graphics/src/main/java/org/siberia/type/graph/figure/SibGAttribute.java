package org.siberia.type.graph.figure;

import org.siberia.type.AbstractSibType;
import org.siberia.type.graph.VisualObject;

public class SibGAttribute extends GeometricObject
{
    // PENDING(api): revoir entièrement cette classe et son utilité
    
    public SibGAttribute()
    {   super(); }

    public Object getValue()
    {   return null; }

    public void setValue(Object value)
    {   /* do nothing */ }
    
    /** return a new VisualObject which proportions changed
     *  @param ratioX the X ratio for the proportions
     *  @param ratioY the Y ratio for the proportions
     **/
    public VisualObject createWithProportions(float ratioX, float ratioY)
    {   SibGAttribute newAtt = new SibGAttribute();
        
        newAtt.setOrigin((int)(this.getX().getValue().intValue() * ratioX),
                         (int)(this.getY().getValue().intValue() * ratioY));
        newAtt.setSize((int)(this.getWidth().getValue().intValue() * ratioX),
                       (int)(this.getHeight().getValue().intValue() * ratioY));
        
        return newAtt;
    }
}
