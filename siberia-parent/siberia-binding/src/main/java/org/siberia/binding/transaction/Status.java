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
package org.siberia.binding.transaction;

/**
 *
 * Taken from JTA
 *
 * @author alexis
 */
public enum Status
{
    ACTIVE,          // A transaction is associated with the target object and it is in the active state.
    COMMITTED,       // A transaction is associated with the target object and it has been committed.
    COMMITTING,      // A transaction is associated with the target object and it is in the process of committing.
    MARKED_ROLLBACK, // A transaction is associated with the target object and it has been marked for rollback, perhaps as a result of a setRollbackOnly operation.
    NO_TRANSACTION,  // No transaction is currently associated with the target object.
    PREPARED,        // A transaction is associated with the target object and it has been prepared.
    PREPARING,       // A transaction is associated with the target object and it is in the process of preparing.
    ROLLEDBACK,      // A transaction is associated with the target object and the outcome has been determined to be rollback.
    ROLLING_BACK,    // A transaction is associated with the target object and it is in the process of rolling back.
    UNKNOWN;         // A transaction is associated with the target object but its current status cannot be determined.
}
