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
package com.bloatit.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.metadata.ClassMetadata;

import com.bloatit.common.Log;
import com.bloatit.data.DaoGroupRight.UserGroupRight;
import com.bloatit.data.DaoJoinGroupInvitation.State;
import com.bloatit.data.queries.QueryCollection;
import com.bloatit.framework.exceptions.FatalErrorException;
import com.bloatit.framework.exceptions.NonOptionalParameterException;
import com.bloatit.framework.utils.PageIterable;

/**
 * Ok if you need a comment to understand what is a member, then I cannot do
 * anything for you ...
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
//@formatter:off
@NamedQueries(value = { @NamedQuery(
                           name = "member.byLogin",
                           query = "FROM DaoMember WHERE login = :login"),
                       @NamedQuery(
                           name = "member.byLoginPassword",
                           query = "FROM DaoMember WHERE login = :login AND password = :password"),
                           
                       @NamedQuery(
                           name = "member.getRecievedInvitations.byStateGroup",
                           query = "FROM DaoJoinGroupInvitation " +
                               	   "WHERE receiver = :receiver " +
                               	   "AND state = :state  " +
                               	   "AND group = :group"),
                   	   @NamedQuery(
           	               name = "member.getRecievedInvitations.byStateGroup.size",
           	               query =  "SELECT count(*)" +
           	               		    "FROM DaoJoinGroupInvitation " +
                   	                "WHERE receiver = :receiver " +
                   	                "AND state = :state  " +
                   	                "AND group = :group"),
                   	                
                       @NamedQuery(
                           name = "member.getRecievedInvitations.byState",
                           query = "FROM DaoJoinGroupInvitation " +
                                   "WHERE receiver = :receiver " +
                                   "AND state = :state "),
                       @NamedQuery(
                           name = "member.getRecievedInvitations.byState.size",
                           query = "SELECT count(*)" +
                           		   "FROM DaoJoinGroupInvitation " +
                                   "WHERE receiver = :receiver " +
                                   "AND state = :state "),            
                                   
                       @NamedQuery(
                           name = "member.getSentInvitations.byState",
                           query = "FROM DaoJoinGroupInvitation " +
                                   "WHERE sender = :sender " +
                                   "AND state = :state "),
                       @NamedQuery(
                           name = "member.getSentInvitations.byState.size",
                           query = "SELECT count(*)" +
                                   "FROM DaoJoinGroupInvitation " +
                                   "WHERE sender = :sender " +
                                   "AND state = :state "),
                                   
                       @NamedQuery(
                           name = "member.getSentInvitations.byStateGroup",
                           query = "SELECT count(*)" +
                                   "FROM DaoJoinGroupInvitation " +
                                   "WHERE sender = :sender " +
                                   "AND state = :state " +
                                   "AND group = :group"),
                       @NamedQuery(
                           name = "member.getSentInvitations.byStateGroup.size",
                           query = "SELECT count(*)" +
                                   "FROM DaoJoinGroupInvitation " +
                                   "WHERE sender = :sender " +
                                   "AND state = :state " +
                                   "AND group = :group"),
                                   
                       @NamedQuery(
                           name = "member.isInGroup",
                           query = "SELECT count(*) " +
                                   "FROM DaoMember m " + 
                                   "JOIN m.groupMembership AS gm " +
                                   "JOIN gm.bloatitGroup AS g " + 
                                   "WHERE m = :member AND g = :group"),
                       
                                   
                      }
             )
// @formatter:on
public class DaoMember extends DaoActor {

    public enum Role {
        NORMAL, PRIVILEGED, REVIEWER, MODERATOR, ADMIN
    }

    public enum ActivationState {
        VALIDATING, ACTIVE, DELETED
    }

    private String fullname;

    @Basic(optional = false)
    private String password;

    @Basic(optional = false)
    private Integer karma;

    @Basic(optional = false)
    @Enumerated
    private Role role;

    @Basic(optional = false)
    @Enumerated
    private ActivationState state;

    @Basic(optional = false)
    @Column(unique = true)
    private String email;

    @Basic(optional = false)
    private Locale locale;

    @ManyToOne(optional = true, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private DaoFileMetadata avatar;

    // this property is for hibernate mapping.
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<DaoGroupMembership> groupMembership = new ArrayList<DaoGroupMembership>(0);

    // ======================================================================
    // Static HQL requests
    // ======================================================================

    /**
     * Find a DaoMember using its login.
     * 
     * @param login the member login.
     * @return null if not found. (or if login == null)
     */
    public static DaoMember getByLogin(final String login) {
        final Session session = SessionManager.getSessionFactory().getCurrentSession();
        final Query q = session.getNamedQuery("member.byLogin");
        q.setString("login", login);
        return (DaoMember) q.uniqueResult();
    }

    /**
     * Find a DaoMember using its login, and password. This method can be use to
     * authenticate a use.
     * 
     * @param login the member login.
     * @param password the password of the member "login". It is a string
     *            corresponding to the string in the database. This method does
     *            not perform any sha1 or md5 transformation.
     * @return null if not found. (or if login == null or password == null)
     */
    public static DaoMember getByLoginAndPassword(final String login, final String password) {
        final Session session = SessionManager.getSessionFactory().getCurrentSession();
        final Query q = session.getNamedQuery("member.byLoginPassword");
        q.setString("login", login);
        q.setString("password", password);
        return (DaoMember) q.uniqueResult();
    }

    // ======================================================================
    // Construction
    // ======================================================================

    /**
     * Create a member. The member login must be unique, and you cannot change
     * it.
     * 
     * @param login The login of the member.
     * @param password The password of the member (md5 ??)
     * @param locale the locale of the user.
     * @return The newly created DaoMember
     * @throws HibernateException If there is any problem connecting to the db.
     *             Or if the member as a non unique login. If an exception is
     *             thrown then the transaction is rolled back and reopened.
     */
    public static DaoMember createAndPersist(final String login, final String password, final String email, final Locale locale) {
        final Session session = SessionManager.getSessionFactory().getCurrentSession();
        final DaoMember theMember = new DaoMember(login, password, email, locale);
        try {
            session.save(theMember);
        } catch (final HibernateException e) {
            session.getTransaction().rollback();
            SessionManager.getSessionFactory().getCurrentSession().beginTransaction();
            throw e;
        }
        return theMember;
    }

    /**
     * You have to use CreateAndPersist instead of this constructor
     * 
     * @param locale is the locale in which this user is. (The country and
     *            language.)
     * @see DaoMember#createAndPersist(String, String, String, Locale)
     */
    private DaoMember(final String login, final String password, final String email, final Locale locale) {
        super(login);
        if (locale == null) {
            throw new NonOptionalParameterException("locale cannot be null!");
        }
        if (email == null) {
            throw new NonOptionalParameterException("email cannot be null!");
        }
        if (email.isEmpty()) {
            throw new NonOptionalParameterException("email cannot be empty!");
        }
        if (password == null) {
            throw new NonOptionalParameterException("Password cannot be null!");
        }
        if (password.isEmpty()) {
            throw new NonOptionalParameterException("Password cannot be empty!");
        }
        setLocale(locale);
        this.email = email;
        this.role = Role.NORMAL;
        this.state = ActivationState.VALIDATING;
        this.password = password;
        this.karma = 0;
        this.fullname = "";
    }

    /**
     * @param aGroup the group in which this member is added.
     */
    public void addToGroup(final DaoGroup aGroup) {
        final DaoGroupMembership daoGroupMembership = new DaoGroupMembership(this, aGroup);
        if (this.groupMembership.contains(daoGroupMembership)) {
            throw new FatalErrorException("This member is already in the group: " + aGroup.getId());
        }
        this.groupMembership.add(daoGroupMembership);
    }

    /**
     * @param aGroup the group from which this member is removed.
     */
    public void removeFromGroup(final DaoGroup aGroup) {
        final DaoGroupMembership link = DaoGroupMembership.get(aGroup, this);
        if (link != null) {
            this.groupMembership.remove(link);
            aGroup.getGroupMembership().remove(link);
            SessionManager.getSessionFactory().getCurrentSession().delete(link);
        } else {
            Log.data().error("Try to remove a non existing DaoGroupMembership: group = " + aGroup.getId() + " member = " + getId());
        }
    }

    public void addGroupRight(final DaoGroup aGroup, final UserGroupRight newRight) {
        final DaoGroupMembership link = DaoGroupMembership.get(aGroup, this);
        if (link != null) {
            link.addUserRight(newRight);
        } else {
            Log.data().error("Trying to give user some rights in a group he doesn't belong: group = " + aGroup.getId() + " member = " + getId());
        }
    }

    public Set<UserGroupRight> getGroupRights(final DaoGroup aGroup) {
        return aGroup.getUserGroupRight(this);
    }

    public void removeGroupRight(final DaoGroup aGroup, final UserGroupRight removeRight) {
        final DaoGroupMembership link = DaoGroupMembership.get(aGroup, this);
        for (final DaoGroupRight dgr : link.getRights()) {
            if (dgr.getUserStatus().equals(removeRight)) {
                link.getRights().remove(dgr);
                SessionManager.getSessionFactory().getCurrentSession().delete(dgr);
                return;
            }
        }
        Log.data().error("Trying to remove user some rights in a group he doesn't belong: group = " + aGroup.getId() + " member = " + getId()
                + " right : " + removeRight);
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public void setActivationState(final ActivationState state) {
        this.state = state;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setFullname(final String firstname) {
        this.fullname = firstname;
    }

    @Override
    public void setContact(final String email) {
        this.email = email;
    }

    public void addToKarma(final int value) {
        this.karma += value;
    }

    public void setLocale(final Locale locale) {
        this.locale = locale;
    }

    // ======================================================================
    // Getters
    // ======================================================================

    /**
     * [ Maybe it could be cool to have a parameter to list all the PUBLIC or
     * PROTECTED groups. ]
     * 
     * @return All the groups this member is in. (Use a HQL query)
     */
    public PageIterable<DaoGroup> getGroups() {
        final Session session = SessionManager.getSessionFactory().getCurrentSession();
        final Query filter = session.createFilter(getGroupMembership(), "select this.bloatitGroup order by login");
        final Query count = session.createFilter(getGroupMembership(), "select count(*)");
        return new QueryCollection<DaoGroup>(filter, count);
    }

    public Role getRole() {
        return this.role;
    }

    public ActivationState getActivationState() {
        return this.state;
    }

    public String getFullname() {
        return this.fullname;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public String getContact() {
        return this.email;
    }

    public Locale getLocale() {
        return this.locale;
    }

    /**
     * @return All the demands created by this member.
     */
    public PageIterable<DaoDemand> getDemands() {
        return getUserContent(DaoDemand.class);
    }

    /**
     * @return All the kudos created by this member.
     */
    public PageIterable<DaoKudos> getKudos() {
        return getUserContent(DaoKudos.class);
    }

    /**
     * @return All the Transactions created by this member.
     */
    public PageIterable<DaoContribution> getTransactions() {
        return getUserContent(DaoContribution.class);
    }

    /**
     * @return All the Comments created by this member.
     */
    public PageIterable<DaoComment> getComments() {
        return getUserContent(DaoComment.class);
    }

    /**
     * @return All the Offers created by this member.
     */
    public PageIterable<DaoOffer> getOffers() {
        return getUserContent(DaoOffer.class);
    }

    /**
     * @return All the Translations created by this member.
     */
    public PageIterable<DaoTranslation> getTranslations() {
        return getUserContent(DaoTranslation.class);
    }

    /**
     * @return All the received invitation to join a group which are in a
     *         specified state
     */
    public PageIterable<DaoJoinGroupInvitation> getReceivedInvitation(final State state) {
        return new QueryCollection<DaoJoinGroupInvitation>("member.getReceivedInvitations.byState").setEntity("receiver", this).setParameter("state",
                                                                                                                                             state);
    }

    /**
     * @param state the state of the invitation (ACCEPTED, PENDING, REFUSED)
     * @param group the group for which the invitations have been sent
     * @return All the received invitation to join a specific group, which are
     *         in a given state
     */
    public PageIterable<DaoJoinGroupInvitation> getReceivedInvitation(final State state, final DaoGroup group) {
        return new QueryCollection<DaoJoinGroupInvitation>("member.getReceivedInvitations.byStateGroup").setEntity("receiver", this)
                                                                                                        .setParameter("state", state)
                                                                                                        .setEntity("group", group);
    }

    public PageIterable<DaoJoinGroupInvitation> getSentInvitation(final State state, final DaoGroup group) {
        return new QueryCollection<DaoJoinGroupInvitation>("member.getSentInvitations.byStateGroup").setEntity("sender", this)
                                                                                                    .setParameter("state", state)
                                                                                                    .setEntity("group", group);
    }

    /**
     * @return All the sent invitation to join a group which are in a specified
     *         state
     */
    public PageIterable<DaoJoinGroupInvitation> getSentInvitation(final State state) {
        return new QueryCollection<DaoJoinGroupInvitation>("member.getSentInvitations.byState").setEntity("sender", this).setEntity("state", state);
    }

    /**
     * @return if the current member is in the "group".
     */
    public boolean isInGroup(final DaoGroup group) {
        final Query q = SessionManager.getNamedQuery("member.isInGroup");
        q.setEntity("member", this);
        q.setEntity("group", group);
        return ((Long) q.uniqueResult()) >= 1;
    }

    public Integer getKarma() {
        return this.karma;
    }

    /**
     * Base method to all the get something created by the user.
     */
    private <T extends DaoUserContent> PageIterable<T> getUserContent(final Class<T> theClass) {
        final ClassMetadata meta = SessionManager.getSessionFactory().getClassMetadata(theClass);
        Query query = SessionManager.createQuery("from " + meta.getEntityName() + " as x where x.member = :author");
        Query size = SessionManager.createQuery("SELECT count(*) from " + meta.getEntityName() + " as x where x.member = :author");
        final QueryCollection<T> q = new QueryCollection<T>(query, size);
        q.setEntity("author", this);
        return q;
    }

    /**
     * used by DaoGroup
     */
    protected List<DaoGroupMembership> getGroupMembership() {
        return this.groupMembership;
    }

    /**
     * @return the avatar
     */
    public DaoFileMetadata getAvatar() {
        return this.avatar;
    }

    /**
     * @param avatar
     */
    public void setAvatar(final DaoFileMetadata avatar) {
        this.avatar = avatar;
    }

    // ======================================================================
    // Visitor.
    // ======================================================================

    @Override
    public <ReturnType> ReturnType accept(final DataClassVisitor<ReturnType> visitor) {
        return visitor.visit(this);
    }

    // ======================================================================
    // For hibernate mapping
    // ======================================================================

    protected DaoMember() {
        super();
    }
}
