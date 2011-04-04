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

import java.util.Date;
import java.util.EnumSet;

import com.bloatit.data.DaoMember.Role;
import com.bloatit.data.DaoTeamRight.UserTeamRight;
import com.bloatit.data.DaoUserContent;
import com.bloatit.framework.exceptions.lowlevel.UnauthorizedOperationException;
import com.bloatit.framework.exceptions.lowlevel.UnauthorizedOperationException.SpecialCode;
import com.bloatit.framework.utils.PageIterable;
import com.bloatit.model.lists.FileMetadataList;
import com.bloatit.model.right.Action;
import com.bloatit.model.right.UserContentRight;

/**
 * The Class UserContent. Model vision of the {@link DaoUserContent} class.
 *
 * @param <T> the generic type. Must be the concrete Dao version of the concrete
 *            subClass. For example: Feature extends UserContent<DaoFeature>
 */
public abstract class UserContent<T extends DaoUserContent> extends Identifiable<T> implements UserContentInterface<T> {

    /**
     * Instantiates a new user content.
     *
     * @param dao the dao
     */
    protected UserContent(final T dao) {
        super(dao);
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.model.UserContentInterface#getAuthor()
     */
    @Override
    public final Member getAuthor() {
        return Member.create(getDao().getAuthor());
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.model.UserContentInterface#getCreationDate()
     */
    @Override
    public final Date getCreationDate() {
        return getDao().getCreationDate();
    }

    @Override
    public final boolean canAccessAsTeam(final Team asTeam) {
        return canAccess(new UserContentRight.AsTeam(asTeam), Action.WRITE);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.model.UserContentInterface#setAsTeam(com.bloatit.model.Team
     * )
     */
    @Override
    public final void setAsTeam(final Team asTeam) throws UnauthorizedOperationException {
        tryAccess(new UserContentRight.AsTeam(asTeam), Action.WRITE);
        getDao().setAsTeam(asTeam.getDao());
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.model.UserContentInterface#getAsTeam()
     */
    @Override
    public final Team getAsTeam() {
        return Team.create(getDao().getAsTeam());
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.model.UserContentInterface#getFiles()
     */
    @Override
    public PageIterable<FileMetadata> getFiles() {
        return new FileMetadataList(getDao().getFiles());
    }

    // TODO right management
    public void delete() throws UnauthorizedOperationException {
        if (!hasUserPrivilege(Role.ADMIN)) {
            throw new UnauthorizedOperationException(SpecialCode.ADMIN_ONLY);
        }
        getDao().setIsDeleted(true);
    }

    // TODO right management
    public void restore() throws UnauthorizedOperationException {
        if (!hasUserPrivilege(Role.ADMIN)) {
            throw new UnauthorizedOperationException(SpecialCode.ADMIN_ONLY);
        }
        getDao().setIsDeleted(false);
    }

    // TODO right management
    @Override
    public boolean isDeleted() throws UnauthorizedOperationException {
        if (!hasUserPrivilege(Role.ADMIN)) {
            throw new UnauthorizedOperationException(SpecialCode.ADMIN_ONLY);
        }
        return getDao().isDeleted();
    }

    @Override
    protected boolean isMine(final Member member) {
        return getAuthor().isMine(member);
    }

    @Override
    protected EnumSet<UserTeamRight> calculateMyTeamRights(final Member member) {
        if (getAsTeam() != null && member.isInTeam(getAsTeam())) {
            return getAsTeam().getUserTeamRight(member);
        }
        return EnumSet.noneOf(UserTeamRight.class);
    }

    @Override
    public void addFile(FileMetadata file) {
        // TODO right management: only the owner can add file
        getDao().addFile(file.getDao());
    }

}
