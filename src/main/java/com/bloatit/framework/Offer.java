package com.bloatit.framework;

import java.util.Date;

import com.bloatit.framework.right.OfferRight;
import com.bloatit.framework.right.RightManager.Action;
import com.bloatit.model.data.DaoKudosable;
import com.bloatit.model.data.DaoOffer;

public class Offer extends Kudosable {

    private DaoOffer dao;

    public Offer(DaoOffer dao) {
        super();
        this.dao = dao;
    }

    public DaoOffer getDao() {
        return dao;
    }

    public Date getDateExpire() {
        return dao.getDateExpire();
    }

    public boolean canSetdatExpire() {
        return new OfferRight.DateExpire().canAccess(calculateRole(this), Action.WRITE);
    }

    public void setDateExpire(Date dateExpire) {
        new OfferRight.DateExpire().tryAccess(calculateRole(this), Action.WRITE);
        dao.setDateExpire(dateExpire);
    }

    public Demand getDemand() {
        return Demand.create(dao.getDemand());
    }

    public Description getDescription() {
        return new Description(dao.getDescription());
    }

    @Override
    protected DaoKudosable getDaoKudosable() {
        return dao;
    }

}
