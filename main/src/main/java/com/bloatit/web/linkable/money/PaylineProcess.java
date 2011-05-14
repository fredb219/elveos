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
package com.bloatit.web.linkable.money;

import java.math.BigDecimal;

import com.bloatit.common.Log;
import com.bloatit.framework.exceptions.highlevel.ShallNotPassException;
import com.bloatit.framework.mails.ElveosMail;
import com.bloatit.framework.mails.ElveosMail.ChargingAccountSuccess;
import com.bloatit.framework.model.ModelAccessor;
import com.bloatit.framework.webprocessor.annotations.ParamContainer;
import com.bloatit.framework.webprocessor.annotations.ParamContainer.Protocol;
import com.bloatit.framework.webprocessor.annotations.RequestParam;
import com.bloatit.framework.webprocessor.context.Context;
import com.bloatit.framework.webprocessor.context.SessionManager;
import com.bloatit.framework.webprocessor.url.Url;
import com.bloatit.framework.webprocessor.url.UrlString;
import com.bloatit.model.Actor;
import com.bloatit.model.BankTransaction;
import com.bloatit.model.ElveosUserToken;
import com.bloatit.model.Member;
import com.bloatit.model.Payline;
import com.bloatit.model.Payline.Reponse;
import com.bloatit.model.Payline.TokenNotfoundException;
import com.bloatit.model.Team;
import com.bloatit.model.managers.MemberManager;
import com.bloatit.model.managers.TeamManager;
import com.bloatit.model.right.UnauthorizedOperationException;
import com.bloatit.web.actions.PaymentProcess;
import com.bloatit.web.actions.WebProcess;
import com.bloatit.web.url.PaylineActionUrl;
import com.bloatit.web.url.PaylineNotifyActionUrl;
import com.bloatit.web.url.PaylineProcessUrl;
import com.bloatit.web.url.PaylineReturnActionUrl;

@ParamContainer(value = "payline/process", protocol = Protocol.HTTPS)
public class PaylineProcess extends WebProcess {

    @RequestParam
    private Actor<?> actor;

    @SuppressWarnings("unused")
    @RequestParam
    private PaymentProcess parentProcess;

    private boolean success = false;

    // Make the payment request.
    private final Payline payline = new Payline();

    private final PaylineProcessUrl url;

    public PaylineProcess(final PaylineProcessUrl url) {
        super(url);
        this.url = url;
        actor = url.getActor();
    }

    public synchronized boolean isSuccessful() {
        return success;
    }

    @Override
    protected synchronized Url doProcess(final ElveosUserToken userToken) {
        url.getParentProcess().addChildProcess(this);
        return new PaylineActionUrl(this);
    }

    @Override
    protected synchronized Url doProcessErrors(final ElveosUserToken userToken) {
        return session.getLastVisitedPage();
    }

    @Override
    public synchronized void doLoad() {
        if (actor instanceof Member) {
            actor = MemberManager.getById(actor.getId());
        } else if (actor instanceof Team) {
            actor = TeamManager.getById(actor.getId());
        }
        // actor = (Actor<?>) DBRequests.getById(DaoActor.class,
        // actor.getId()).accept(new DataVisitorConstructor());
    }

    synchronized Url initiatePayment() {
        // Constructing the urls.
        final PaylineReturnActionUrl paylineReturnActionUrl = new PaylineReturnActionUrl("ok", this);
        final String returnUrl = paylineReturnActionUrl.externalUrlString();
        final PaylineReturnActionUrl paylineReturnActionUrlCancel = new PaylineReturnActionUrl("cancel", this);
        final String cancelUrl = paylineReturnActionUrlCancel.externalUrlString();
        final PaylineNotifyActionUrl paylineNotifyActionUrl = new PaylineNotifyActionUrl(this);
        final String notificationUrl = paylineNotifyActionUrl.externalUrlString();

        Reponse reponse;
        try {
            reponse = payline.doPayment(actor, getAmount(), cancelUrl, returnUrl, notificationUrl);
            SessionManager.storeTemporarySession(reponse.getToken(), session);

            // Normal case It is accepted !
            if (reponse.isAccepted()) {
                return new UrlString(reponse.getRedirectUrl());
            }
            session.notifyBad(reponse.getMessage());
        } catch (final UnauthorizedOperationException e) {
            throw new ShallNotPassException("Not authorized", e);
        }
        return Context.getSession().pickPreferredPage();
    }

    private BigDecimal getAmount() {
        return ((PaymentProcess) getFather()).getAmountToPay();
    }

    synchronized void validatePayment(final String token) throws UnauthorizedOperationException {
        try {
            final Reponse paymentDetails = payline.getPaymentDetails(token);
            final String message = paymentDetails.getMessage().replace("\n", ". ");
            if (paymentDetails.isAccepted()) {
                payline.validatePayment(token);

                // The payline process is critical. We must be sure the DB is
                // updated right NOW!
                ModelAccessor.close();
                ModelAccessor.open();

                success = true;
                // Notify the user:
                final BankTransaction bankTransaction = BankTransaction.getByToken(token);
                if (bankTransaction == null) {
                    session.notifyBad(Context.tr("Cannot validate your payment. Reason: Token not found."));
                    return;
                }
                final String valueStr = Context.getLocalizator().getCurrency(bankTransaction.getValue()).getSimpleEuroString();
                final String paidValueStr = Context.getLocalizator().getCurrency(bankTransaction.getValuePaid()).getTwoDecimalEuroString();
                session.notifyGood(Context.tr("Payment of {0} accepted.", paidValueStr));
                // By mail
                final ChargingAccountSuccess mail = new ElveosMail.ChargingAccountSuccess(bankTransaction.getReference(), paidValueStr, valueStr);
                // TODO reactivate the send mail
                // mail.sendMail(bankTransaction.getAuthor(),
                // "payline-process");
            } else {
                payline.cancelPayement(token);
                Log.framework().info("Payline transaction failure. (Reason: " + message + ")");
                session.notifyBad("Payment canceled. Reason: " + message + ".");
            }
        } catch (final TokenNotfoundException e) {
            Log.web().fatal("Token not found.", e);
            session.notifyBad("Payment canceled. Reason: Internal error. Please report the bug.");
        }
    }

    synchronized void refusePayment(final String token) {
        try {
            final Reponse paymentDetails = payline.getPaymentDetails(token);
            final String message = paymentDetails.getMessage().replace("\n", ". ");
            Log.framework().info("Payline transaction failure. (Reason: " + message + ")");
            session.notifyBad("Payment canceled. Reason: " + message + ".");
            payline.cancelPayement(token);
        } catch (final TokenNotfoundException e) {
            Log.web().fatal("Token not found.", e);
            session.notifyBad("Payment canceled. Reason: Payment refused. Please report the bug.");
        }
    }

    public synchronized String getPaymentReference(final String token) {
        final BankTransaction bankTransaction = BankTransaction.getByToken(token);
        if (bankTransaction == null) {
            return "";
        }
        try {
            return bankTransaction.getReference();
        } catch (final UnauthorizedOperationException e) {
            Log.web().fatal("Cannot find a reference.", e);
            return "Reference-not-Found";
        }
    }
}
