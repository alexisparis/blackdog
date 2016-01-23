package org.siberia.type;

import org.siberia.SiberiaTextPlugin;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.SibBaseType;

/**
 *  Class representing a formatted text
 *
 *  @author alexis
 */
@Bean(  name="text",
        internationalizationRef="org.siberia.rc.i18n.type.SibText",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public abstract class SibText extends SibBaseType<String>
{   
    /** type of the text */
    protected String  textType;
    
    /** does it support spell check */
    protected boolean supportSpellCheck   = false;
    
    /** does it support automatic line return */
    private boolean   automaticLineReturn = false;
    
    /** create a new instanceof ColdText */
    public SibText()
    {   super();
        this.setType("text/plain");
    }
    
    /** initialize the type of the text
     *  @param type the type of the text
     */
    protected void setType(String type)
    {   this.textType = type; }
    
    /** return the type of the text
     *  @return the type of the text
     */
    public String getType()
    {   return this.textType; }
    
    /** tell if it supports style
     *  @param supportStyle true if the text have to support style
     */
    protected void setSupportStyle(boolean supportStyle)
    {   if ( ! supportStyle )
            this.setType("text/plain");
    }
    
    /** tell if it supports style
     *  @return true if the text have to support style
     */
    public boolean supportStyle()
    {   if ( this.getType() != null )
            return ! this.getType().equals("text/plain");
        return false;
    }
    
    /** tell if it supports spell check
     *  @param support true if the text have to support spell check
     */
    protected void setSupportSpellCheck(boolean support)
    {   this.supportSpellCheck = support; }
    
    /** tell if it supports spell check
     *  @return true if the text have to support spell check
     */
    public boolean supportSpellCheck()
    {   return this.supportSpellCheck; }

    /** tell if it supports automatic line break
     *  @return true if the text have to support automatic line break
     */
    public boolean isAutomaticLineReturn()
    {   return automaticLineReturn; }

    /** tell if it supports automatic line break
     *  @param automaticLineReturn true if the text have to support automatic line break
     */
    protected void setAutomaticLineReturn(boolean automaticLineReturn)
    {   this.automaticLineReturn = automaticLineReturn; }
        
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContent()
    {   return "   automatic line return ? " + this.isAutomaticLineReturn() + "<br>" +
               "   support spellCheck    ? " + this.supportSpellCheck() + "<br>" +
               "   support style         ? " + this.supportStyle() + "<br>" +
               super.getHtmlContent();
    }
    
    /** create another instance
     *  @return a clone
     */
    public Object clone()
    {   SibText other = (SibText)super.clone();
        
        other.setSupportStyle(this.supportStyle());
        other.setSupportSpellCheck(this.supportSpellCheck());
        other.setAutomaticLineReturn(this.isAutomaticLineReturn());
        other.setType(this.getType());
        
        return other;
    }
}
