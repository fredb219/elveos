//
// Copyright (c) 2011 Linkeos.
//
// This file is part of Elveos.org.
// Elveos.org is free software: you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the
// Free Software Foundation, either version 3 of the License, or (at your
// option) any later version.
//
// Elveos.org is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
// more details.
// You should have received a copy of the GNU General Public License along
// with Elveos.org. If not, see http://www.gnu.org/licenses/.
//
package com.bloatit.model;

import com.bloatit.data.DaoIdentifiable;
import com.bloatit.framework.exceptions.lowlevel.NonOptionalParameterException;
import com.bloatit.model.right.Action;
import com.bloatit.model.right.GenericAccessor;
import com.bloatit.model.right.RestrictedObject;
import com.bloatit.model.right.UnauthorizedOperationException;

/**
 * An identifiable is the base class for each class that map a dao class.
 *
 * @author Thomas Guyard
 * @param <T> is the dao being mapped.
 */
public abstract class Identifiable<T extends DaoIdentifiable> extends RestrictedObject implements IdentifiableInterface {

    private final T dao;

    protected Identifiable(final T dao) {
        if (dao == null) {
            throw new NonOptionalParameterException();
        }
        if (dao.getId() != null) {
            CacheManager.add(dao.getId(), this);
        }
        this.dao = dao;
    }

    /**
     * Can access.
     *
     * @param accessor the accessor
     * @param action the action
     * @return true, if successful
     */
    protected final boolean canAccess(final GenericAccessor<?> accessor, final Action action) {
        return accessor.canAccess(new Rights(this), action);
    }

    /**
     * Try access.
     *
     * @param accessor the accessor
     * @param action the action
     * @throws U the unauthorized operation exception
     */
    final <U extends UnauthorizedOperationException> void tryAccess(final GenericAccessor<U> accessor, final Action action) throws U {
        accessor.tryAccess(new Rights(this), action);
    }

    @Override
    public Rights getRights() {
        return new Rights(this);
    }

    /**
     * @return a unique identifier for this object.
     */
    @Override
    public final Integer getId() {
        return getDao().getId();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getId();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Identifiable<?> other = (Identifiable<?>) obj;
        if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    /**
     * @return the dao
     */
    public final T getDao() {
        return dao;
    }
}
