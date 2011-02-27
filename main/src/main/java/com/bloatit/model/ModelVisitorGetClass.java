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

import com.bloatit.data.DaoBankTransaction;
import com.bloatit.data.DaoBatch;
import com.bloatit.data.DaoBug;
import com.bloatit.data.DaoComment;
import com.bloatit.data.DaoContribution;
import com.bloatit.data.DaoDemand;
import com.bloatit.data.DaoDescription;
import com.bloatit.data.DaoExternalAccount;
import com.bloatit.data.DaoFileMetadata;
import com.bloatit.data.DaoGroup;
import com.bloatit.data.DaoHighlightDemand;
import com.bloatit.data.DaoInternalAccount;
import com.bloatit.data.DaoJoinGroupInvitation;
import com.bloatit.data.DaoKudos;
import com.bloatit.data.DaoMember;
import com.bloatit.data.DaoOffer;
import com.bloatit.data.DaoProject;
import com.bloatit.data.DaoRelease;
import com.bloatit.data.DaoTransaction;
import com.bloatit.data.DaoTranslation;

/**
 * The Class ModelVisitorGetClass.
 */
public class ModelVisitorGetClass implements ModelClassVisitor<Class<?>> {

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.ExternalAccount
     * )
     */
    @Override
    public Class<?> visit(ExternalAccount model) {
        return DaoExternalAccount.class;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.InternalAccount
     * )
     */
    @Override
    public Class<?> visit(InternalAccount model) {
        return DaoInternalAccount.class;
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.Member)
     */
    @Override
    public Class<?> visit(Member model) {
        return DaoMember.class;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.BankTransaction
     * )
     */
    @Override
    public Class<?> visit(BankTransaction model) {
        return DaoBankTransaction.class;
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.Batch)
     */
    @Override
    public Class<?> visit(Batch model) {
        return DaoBatch.class;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.Description)
     */
    @Override
    public Class<?> visit(Description model) {
        return DaoDescription.class;
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.Group)
     */
    @Override
    public Class<?> visit(Group model) {
        return DaoGroup.class;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.HighlightDemand
     * )
     */
    @Override
    public Class<?> visit(HighlightDemand model) {
        return DaoHighlightDemand.class;
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.
     * JoinGroupInvitation)
     */
    @Override
    public Class<?> visit(JoinGroupInvitation model) {
        return DaoJoinGroupInvitation.class;
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.Project)
     */
    @Override
    public Class<?> visit(Project model) {
        return DaoProject.class;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.Transaction)
     */
    @Override
    public Class<?> visit(Transaction model) {
        return DaoTransaction.class;
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.Bug)
     */
    @Override
    public Class<?> visit(Bug model) {
        return DaoBug.class;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.Contribution)
     */
    @Override
    public Class<?> visit(Contribution model) {
        return DaoContribution.class;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.FileMetadata)
     */
    @Override
    public Class<?> visit(FileMetadata model) {
        return DaoFileMetadata.class;
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.Kudos)
     */
    @Override
    public Class<?> visit(Kudos model) {
        return DaoKudos.class;
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.Comment)
     */
    @Override
    public Class<?> visit(Comment model) {
        return DaoComment.class;
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.Demand)
     */
    @Override
    public Class<?> visit(Demand model) {
        return DaoDemand.class;
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.Offer)
     */
    @Override
    public Class<?> visit(Offer model) {
        return DaoOffer.class;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.Translation)
     */
    @Override
    public Class<?> visit(Translation model) {
        return DaoTranslation.class;
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.model.ModelClassVisitor#visit(com.bloatit.model.Release)
     */
    @Override
    public Class<?> visit(Release model) {
        return DaoRelease.class;
    }
}