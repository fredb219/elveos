package com.bloatit.model.data;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.bloatit.model.data.util.SessionManager;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class DaoExternalAccount extends DaoAccount {
    public enum AccountType {
        IBAN
    }

    @Basic(optional = false)
    private String bankCode;
    @Basic(optional = false)
    @Enumerated
    private AccountType type;

    public DaoExternalAccount() {
        super();
    }

    public static DaoExternalAccount createAndPersist(DaoActor Actor, AccountType type, String bankCode) {
        final Session session = SessionManager.getSessionFactory().getCurrentSession();
        final DaoExternalAccount account = new DaoExternalAccount(Actor, type, bankCode);
        try {
            session.save(account);
        } catch (final HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return account;
    }

    // TODO verify the bank code validity
    private DaoExternalAccount(DaoActor Actor, AccountType type, String bankCode) {
        super(Actor);
        this.type = type;
        this.bankCode = bankCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public AccountType getType() {
        return type;
    }

    // ======================================================================
    // For hibernate mapping
    // ======================================================================

    protected void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    protected void setType(AccountType type) {
        this.type = type;
    }

}
