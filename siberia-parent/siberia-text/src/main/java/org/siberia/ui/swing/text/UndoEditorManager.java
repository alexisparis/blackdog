package org.siberia.ui.swing.text;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;


/**
 *
 * @author alexis
 */
public class UndoEditorManager extends UndoManager
{
    /** defines if it works in group mode */
    private boolean                    groupActivated = false;
    
    /** indicates if a list is currently beeing feeded */
    private boolean groupIsBeingFilled                = false;
    
    private int limit = 1000;
    
    private int indexOfNextAdd;
    
    /** indicates if the number of UndoableEdit processed at a time exceeds capabilities */
    private boolean hasExceeded = false;     ///////////////// TO INTEGRATE
    
    /** Creates a new instance of UndoEditorManager */
    public UndoEditorManager()
    {   super();
        
        this.
        
        indexOfNextAdd = 0;
    }
    
    /** defines if the future action has to be grouped
     *  @param activated
     */
    public void setGroupActionActivated(boolean activated)
    {   this.groupActivated = activated; }
    
    /** add an undoableEdit to the manager */
    public synchronized boolean addEdit(UndoableEdit anEdit)
    {   System.out.println("appel à addEdit : " + anEdit);
//        System.out.println("addEdit .. " + (anEdit != null ? anEdit.getClass() : "") );
//        System.out.println("\tisInProgress ? " + this.isInProgress());
//        System.out.println("\tgroup mode   ? " + this.groupActivated);
//        
//        System.out.println("affichage de edits : ");
//        for (int i = 0; i < this.edits.size(); i++)
//            System.out.println("\t" + this.edits.elementAt(i).getClass());
        
        if ( ! this.isInProgress() ) return false;
        
        System.out.println("groupActivated : " + this.groupActivated);
            
        if ( this.groupActivated )
        {   if ( ! (this.editToBeUndone() instanceof UndoManager) )
            {   
                System.out.println("\teditToBeUndone is NOT NOT an UndoManager");
                UndoManager newManager = new UndoManager();
                newManager.setLimit(this.limit);  // attention, la limite peut poser de graves problemes
                //super.addEdit(newManager);
                this.edits.add(newManager);
            
                indexOfNextAdd = edits.size(); 
                System.out.println("\tindexOfNextAdd : " + indexOfNextAdd);
            }
            
            if ( this.editToBeUndone() instanceof UndoManager )
            {   
                System.out.println("\teditToBeUndone is an UndoManager");
                ((UndoManager)this.editToBeUndone()).addEdit(anEdit);
//                System.out.println("\tindexOfNextAdd : " + indexOfNextAdd);
        
//        System.out.println("gaga affichage de edits : ");
//        for (int i = 0; i < this.edits.size(); i++)
//            System.out.println("\t" + this.edits.elementAt(i).getClass());
                
                return true;
            }
            else
            {   
//                System.out.println("\teditToBeUndone is NOT an UndoManager");
                
                if ( this.edits.size() > 0 )
                {   
                    System.out.println("\ta");
                    ((UndoManager)this.edits.elementAt(this.edits.size() - 1)).addEdit(anEdit);
                }
                else
                {   
                    System.out.println("\tb");
                    this.edits.add(anEdit);
                }
                return true;
            }
            //throw new RuntimeException("gros probleme dans l'undoManager"); }
        }
        else
        {
            System.out.println("\tajout simple");
            boolean retVal;

            // Trim from the indexOfNextAdd to the end, as we'll
            // never reach these edits once the new one is added.
            trimEdits(indexOfNextAdd, edits.size()-1);

            ///////////////
            if (!this.isInProgress())
            {
                return false;
            } else {
	    UndoableEdit last = lastEdit();

	    // If this is the first subedit received, just add it.
	    // Otherwise, give the last one a chance to absorb the new
	    // one.  If it won't, give the new one a chance to absorb
	    // the last one.

            System.out.println("\t\tlast : " + (last == null ? "null":last.getClass()));
	    if (last == null) {
                System.out.println("\t\taddElement");
		edits.addElement(anEdit);
	    }
	    else //if (last instanceof UndoManager)//!last.addEdit(anEdit) || )
            {
                System.out.println("\t\ti");
		if (anEdit.replaceEdit(last)) {
		    edits.removeElementAt(edits.size()-1);
                    System.out.println("\t\tj");
		}
                System.out.println("il ajoute quand meme ce connard !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		edits.addElement(anEdit);
	    }

            retVal =  true;
            }
            ///////////////
            
            
            if (this.isInProgress()) {
              retVal = true;
            }

            // Maybe super added this edit, maybe it didn't (perhaps
            // an in progress compound edit took it instead. Or perhaps
            // this UndoManager is no longer in progress). So make sure
            // the indexOfNextAdd is pointed at the right place.
            indexOfNextAdd = edits.size();
            
            System.out.println("indexofNextAdd : " + indexOfNextAdd);

            // Enforce the limit
            trimForLimit();

            return retVal;
        }
        
    }

    /**
     * If this UndoManager is inProgress, undo the last significant
     * UndoableEdit before indexOfNextAdd, and all insignificant edits back to
     * it. Updates indexOfNextAdd accordingly.
     *
     * <p>If not inProgress, indexOfNextAdd is ignored and super's routine is
     * called.</p>
     */
    public synchronized void undo() throws CannotUndoException {
//        System.out.println("appel a undo");
        if (this.isInProgress())
        {
            UndoableEdit edit = editToBeUndone();
            
//            System.out.println("\tedit to undone : " + edit);
            
            if ( edit instanceof UndoManager )
            {   UndoManager manager = (UndoManager)edit;
//                System.out.println("\tindexOfNextAdd : " + indexOfNextAdd);
                
                while ( manager.canUndo() )
                {   manager.undo(); }
        
//            System.out.println("gogo affichage de edits : ");
//            for (int i = 0; i < this.edits.size(); i++)
//                System.out.println("\t" + this.edits.elementAt(i).getClass());
//                System.out.println("\tindexOfNextAdd : " + indexOfNextAdd);
                indexOfNextAdd --;
                
                return;
            }
            if (edit == null) {
                throw new CannotUndoException();
            }
            undoTo(edit);
            
        
//            System.out.println("gigi affichage de edits : ");
//            for (int i = 0; i < this.edits.size(); i++)
//                System.out.println("\t" + this.edits.elementAt(i).getClass());
            
        }
        else
        {   super.undo(); }
    }

    /**
     * If this <code>UndoManager</code> is <code>inProgress</code>,
     * redoes the last significant <code>UndoableEdit</code> at
     * <code>indexOfNextAdd</code> or after, and all insignificant
     * edits up to it. Updates <code>indexOfNextAdd</code> accordingly.
     *
     * <p>If not <code>inProgress</code>, <code>indexOfNextAdd</code>
     * is ignored and super's routine is called.</p>
     */
    public synchronized void redo() throws CannotRedoException {
        if (this.isInProgress())
        {
            UndoableEdit edit = editToBeRedone();
            
            if ( edit instanceof UndoManager )
            {   UndoManager manager = (UndoManager)edit;
                
                while ( manager.canRedo() )
                {   manager.redo(); }
                
                indexOfNextAdd ++;
                return;
            }
            
            if (edit == null) {
                throw new CannotRedoException();
            }
            redoTo(edit);
        }
        else
        {   super.redo(); }
    }   
     
    /**
     * Empty the undo manager, sending each edit a die message
     * in the process.
     */
    public synchronized void discardAllEdits() {
        Enumeration cursor = edits.elements();
        while (cursor.hasMoreElements()) {
            UndoableEdit e = (UndoableEdit)cursor.nextElement();
            e.die();
        }
        edits = new Vector(limit);
        indexOfNextAdd = 0;
        // PENDING(rjrjr) when vector grows a removeRange() method
        // (expected in JDK 1.2), trimEdits() will be nice and
        // efficient, and this method can call that instead.
    }

    /**
     * Reduce the number of queued edits to a range of size limit,
     * centered on indexOfNextAdd.  
     */
    protected void trimForLimit() {
        if (limit >= 0) {
            int size = edits.size();
//          System.out.print("limit: " + limit +
//                           " size: " + size +
//                           " indexOfNextAdd: " + indexOfNextAdd +
//                           "\n");
        
            if (size > limit) {
                int halfLimit = limit/2;
                int keepFrom = indexOfNextAdd - 1 - halfLimit;
                int keepTo   = indexOfNextAdd - 1 + halfLimit;

                // These are ints we're playing with, so dividing by two
                // rounds down for odd numbers, so make sure the limit was
                // honored properly. Note that the keep range is
                // inclusive.

                if (keepTo - keepFrom + 1 > limit) {
                    keepFrom++;
                }

                // The keep range is centered on indexOfNextAdd,
                // but odds are good that the actual edits Vector
                // isn't. Move the keep range to keep it legal.

                if (keepFrom < 0) {
                    keepTo -= keepFrom;
                    keepFrom = 0;
                }
                if (keepTo >= size) {
                    int delta = size - keepTo - 1;
                    keepTo += delta;
                    keepFrom += delta;
                }

//              System.out.println("Keeping " + keepFrom + " " + keepTo);
                trimEdits(keepTo+1, size-1);
                trimEdits(0, keepFrom-1);
            }
        }
    }
        
    /**
     * Tell the edits in the given range (inclusive) to die, and
     * remove them from edits. from > to is a no-op. 
     */
    protected void trimEdits(int from, int to) {
        if (from <= to) {
//          System.out.println("Trimming " + from + " " + to + " with index " +
//                           indexOfNextAdd);
            for (int i = to; from <= i; i--) {
                UndoableEdit e = (UndoableEdit)edits.elementAt(i);
//              System.out.println("JUM: Discarding " +
//                                 e.getUndoPresentationName());
                e.die();
                // PENDING(rjrjr) when Vector supports range deletion (JDK
                // 1.2) , we can optimize the next line considerably. 
                edits.removeElementAt(i);
            }

            if (indexOfNextAdd > to) {
//              System.out.print("...right...");
                indexOfNextAdd -= to-from+1;
            } else if (indexOfNextAdd >= from) {
//              System.out.println("...mid...");
                indexOfNextAdd = from;
            }

//          System.out.println("new index " + indexOfNextAdd);
        }
    }

    /**
     * Set the maximum number of edits this UndoManager will hold. If
     * edits need to be discarded to shrink the limit, they will be
     * told to die in the reverse of the order that they were added.
     *
     * @see #addEdit
     * @see #getLimit
     */
    public synchronized void setLimit(int l) {
        if (this.isInProgress()) throw new RuntimeException("Attempt to call UndoManager.setLimit() after UndoManager.end() has been called");
        limit = l;
        trimForLimit();
    }
     

    /**
     * Returns the the next significant edit to be undone if undo is
     * called. May return null
     */
    protected UndoableEdit editToBeUndone() {
        int i = indexOfNextAdd;
        while (i > 0) {
            UndoableEdit edit = (UndoableEdit)edits.elementAt(--i);
            if (edit.isSignificant()) {
                return edit;
            }
        }

        return null;
    }

    /**
     * Returns the the next significant edit to be redone if redo is
     * called. May return null
     */
    protected UndoableEdit editToBeRedone() {
        int count = edits.size();
        int i = indexOfNextAdd;

        while (i < count) {
            UndoableEdit edit = (UndoableEdit)edits.elementAt(i++);
            if (edit.isSignificant()) {
                return edit;
            }
        }

        return null;
    }

    /**
     * Undoes all changes from indexOfNextAdd to edit. Updates indexOfNextAdd accordingly.
     */
    protected void undoTo(UndoableEdit edit) throws CannotUndoException {
        boolean done = false;
        while (!done) {
            UndoableEdit next = (UndoableEdit)edits.elementAt(--indexOfNextAdd);
            next.undo();
            done = next == edit;
        }
    }

    /**
     * Redoes all changes from indexOfNextAdd to edit. Updates indexOfNextAdd accordingly.
     */
    protected void redoTo(UndoableEdit edit) throws CannotRedoException {
        boolean done = false;
        while (!done) {
            UndoableEdit next = (UndoableEdit)edits.elementAt(indexOfNextAdd++);
            next.redo();
            done = next == edit;
        }
    }

    /**
     * Undo or redo as appropriate. Suitable for binding to an action
     * that toggles between these two functions. Only makes sense
     * to send this if limit == 1.
     *
     * @see #canUndoOrRedo
     * @see #getUndoOrRedoPresentationName
     */
    public synchronized void undoOrRedo() throws CannotRedoException,
        CannotUndoException {
        if (indexOfNextAdd == edits.size()) {
            undo();
        } else {
            redo();
        }
    }

    /**
     * Return true if calling undoOrRedo will undo or redo. Suitable
     * for deciding to enable a command that toggles between the two
     * functions, which only makes sense to use if limit == 1.
     *
     * @see #undoOrRedo
     */
    public synchronized boolean canUndoOrRedo() {
        if (indexOfNextAdd == edits.size()) {
            return canUndo();
        } else {
            return canRedo();
        }
    } 


    /**
     * Sending end() to an UndoManager turns it into a plain old
     * (ended) CompoundEdit.
     *
     * <p> Calls super's end() method (making inProgress false), then
     * sends die() to the unreachable edits at indexOfNextAdd and
     * beyond, in the reverse of the order in which they were added.
     */
    public synchronized void end() {
	super.end();
        this.trimEdits(indexOfNextAdd, edits.size()-1);
    }

    /**
     * Return the appropriate name for a command that toggles between
     * undo and redo.  Only makes sense to use such a command if limit
     * == 1 and we're not in progress.
     */
    public synchronized String getUndoOrRedoPresentationName() {
        if (indexOfNextAdd == edits.size()) {
            return getUndoPresentationName();
        } else {
            return getRedoPresentationName();
        }
    }

    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a String representation of this object
     */
    public String toString() {
        return super.toString() + " limit: " + limit + 
            " indexOfNextAdd: " + indexOfNextAdd;
    }

    /**
     * Overridden to preserve usual semantics: returns true if a redo
     * operation would be successful now, false otherwise
     */
    public synchronized boolean canRedo() {
        
//            System.out.println("geugeu affichage de edits : ");
//            for (int i = 0; i < this.edits.size(); i++)
//                System.out.println("\t" + this.edits.elementAt(i).hashCode());
        
        if (this.isInProgress()) {
            UndoableEdit edit = editToBeRedone();
//            System.out.println("canRedo : " + (edit != null && edit.canRedo()));
//            System.out.println("edit : " + edit.hashCode() + (edit != null ? edit.canRedo() : "beuh"));// + " : " + edit.getClass());
            return edit != null && edit.canRedo();
        } else {
//            System.out.println("redo aieee");
            return super.canRedo();
        }
    }

    /**
     * Overridden to preserve usual semantics: returns true if an undo
     * operation would be successful now, false otherwise
     */
    public synchronized boolean canUndo() {
        
//            System.out.println("giugiu affichage de edits : ");
//            for (int i = 0; i < this.edits.size(); i++)
//                System.out.println("\t" + this.edits.elementAt(i).hashCode());
        
        if (this.isInProgress()) {
            UndoableEdit edit = editToBeUndone();
//            System.out.println("canUndo : " + (edit != null && edit.canUndo()));
//            System.out.println("edit : " + edit.hashCode() + (edit != null ? edit.canUndo() : "beuh"));// + " : " + edit.getClass());
            return edit != null && edit.canUndo();
        } else {
//            System.out.println("undo aieee");
            return super.canUndo();
        }
    }
    
}
