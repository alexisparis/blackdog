package org.siberia.type;

/** 
 * Basic class for all element that contains code
 *
 *  @author alexis
 */
public abstract class SibCode extends SibText
{
    /** String sequence representing comment */
    private String  commentSequence          = "";
    
    /** indicates if import management is allowed */
    private boolean importManagementAllowed = false;
    
    /** create a new code */
    public SibCode()
    {   super();
        
        this.setAutomaticLineReturn(false);
    }
    
    /** returns the class that can parse the content of the object
     *  @return the class responsible for the parsing of the content
     */
    public abstract Class getParserClass();
    
    /** returns the class that can manage import
     *  @return the class responsible for management of import
     */
    public abstract Class getImportManagerClass();
    
    /** set the String representing the comment
     *  @param commentSequence the String representing the comment
     */
    public void setCommentSequence(String commentSequence)
    {   this.commentSequence = commentSequence; }
    
    /** return the String representing the comment
     *  @return the String representing the comment
     */
    public String getCommentSequence()
    {   return this.commentSequence; }

    /** tell if the code support import management
     *  @return true if the code support import management
     */
    public boolean supportImportManagement()
    {   return importManagementAllowed; }

    /** initialize if the code support import management
     *  @param importManagementAllowed true if the code support import management
     */
    public void setSupportImportManagement(boolean importManagementAllowed) 
    {   this.importManagementAllowed = importManagementAllowed; }
        
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContent()
    {   return "   support import management ? " + this.supportImportManagement() + "<br>" +
               "   comment sequence          ? " + this.getCommentSequence() + "<br>" + 
               super.getHtmlContent();
    }
    
    /** create another instance
     *  @return a clone
     */
    public Object clone()
    {   SibCode other = (SibCode)super.clone();
        
        other.setCommentSequence(this.getCommentSequence());
        other.setSupportImportManagement(this.supportImportManagement());
        
        return other;
    }
}
