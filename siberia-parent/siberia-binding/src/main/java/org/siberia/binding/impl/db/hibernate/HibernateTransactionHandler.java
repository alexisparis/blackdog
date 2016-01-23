/* 
 * Siberia binding : siberia plugin defining persistence services
 *
 * Copyright (C) 2008 Alexis PARIS
 * Project Lead:  Alexis Paris
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package org.siberia.binding.impl.db.hibernate;

import org.hibernate.classic.Session;
import org.siberia.binding.transaction.Status;
import org.siberia.binding.transaction.Transaction;

/**
 *
 * Encapsulation of an hibernate transaction to convey to Siberia transaction standard
 *
 * @author alexis
 */
public class HibernateTransactionHandler implements Transaction
{
    /** inner transaction */
    private org.hibernate.Transaction innerTransaction   = null;
    
    /** session */
    private Session                   session            = null;
    
    /** marked as rollbacked */
    private boolean                   markedAsRollbacked = false;
    
    /**
     * Creates a new instance of HibernateTransactionHandler
     * 
     *	@param session an hibernate Session
     *  @param innerTransaction an hibernate Transaction
     */
    public HibernateTransactionHandler(Session session, org.hibernate.Transaction hibernateTransaction)
    {
	this.session          = session;
	this.innerTransaction = hibernateTransaction;
    }
    
    /** return the session to use for his transaction
     *	@return a Session
     */
    public Session getSession()
    {
	return this.session;
    }

    /**
     * Modify the transaction associated with the target object such that the
     *  only possible outcome of the transaction is to roll back the transaction.
     */
    public void setRollbackOnly()
    {
	this.markedAsRollbacked = true;
    }

    /**
     * Rollback the transaction represented by this Transaction object.
     */
    public void rollback() throws Exception
    {
	this.innerTransaction.rollback();
	this.release();
    }

    /**
     * Obtain the status of the transaction associated with the target Transaction object.
     * 
     * @return a Status
     */
    public Status getStatus()
    {
	Status status = null;
	
	if ( this.markedAsRollbacked )
	{
	    status = Status.MARKED_ROLLBACK;
	}
	else
	{
	    if ( this.innerTransaction.wasCommitted() )
	    {
		status = Status.COMMITTED;
	    }
	    else if ( this.innerTransaction.wasRolledBack() )
	    {
		status = Status.ROLLEDBACK;
	    }
	}
	
	if ( status == null )
	{
	    if ( this.session != null )
	    {
		if ( this.session.isConnected() )
		{
		    status = Status.ACTIVE;
		}
	    }
	    if ( status == null )
	    {
		status = Status.UNKNOWN;
	    }
	}
	
	return status;
    }

    /**
     * Complete the transaction represented by this Transaction object.
     */
    public void commit() throws Exception
    {
	this.session.flush();
	this.innerTransaction.commit();
	this.release();
    }
    
    /** dispose, clear all resource taken by the transaction */
    public void release() throws Exception
    {
	if ( this.session != null )
	{
	    if ( ! this.session.isOpen() )
	    {
//		this.session.close();
	    }
	    this.session = null;
	}
    }

    public String toString()
    {
	return super.toString() + " with hibernate transaction : " + this.innerTransaction + " with session : " + this.getSession();
    }
}
