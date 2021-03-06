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
package com.bloatit.web.linkable.members;

import java.util.Locale;

import com.bloatit.common.Log;
import com.bloatit.framework.exceptions.highlevel.ShallNotPassException;
import com.bloatit.framework.mailsender.Mail;
import com.bloatit.framework.mailsender.MailServer;
import com.bloatit.framework.webprocessor.annotations.MaxConstraint;
import com.bloatit.framework.webprocessor.annotations.MinConstraint;
import com.bloatit.framework.webprocessor.annotations.Optional;
import com.bloatit.framework.webprocessor.annotations.ParamContainer;
import com.bloatit.framework.webprocessor.annotations.ParamContainer.Protocol;
import com.bloatit.framework.webprocessor.annotations.RequestParam;
import com.bloatit.framework.webprocessor.annotations.RequestParam.Role;
import com.bloatit.framework.webprocessor.annotations.tr;
import com.bloatit.framework.webprocessor.components.form.FormField;
import com.bloatit.framework.webprocessor.context.Context;
import com.bloatit.framework.webprocessor.url.Url;
import com.bloatit.model.Member;
import com.bloatit.model.managers.MemberManager;
import com.bloatit.model.right.UnauthorizedOperationException;
import com.bloatit.web.linkable.master.LoggedElveosAction;
import com.bloatit.web.url.MemberActivationActionUrl;
import com.bloatit.web.url.MemberPageUrl;
import com.bloatit.web.url.ModifyDetailActionUrl;
import com.bloatit.web.url.ModifyMemberPageUrl;

@ParamContainer(value = "member/domodifydetail", protocol = Protocol.HTTPS)
public class ModifyDetailAction extends LoggedElveosAction {
    @RequestParam(role = Role.POST)
    @Optional
    @MinConstraint(min = 4, message = @tr("Number of characters for email has to be superior to %constraint% but your text is %valueLength% characters long."))
    @MaxConstraint(max = 255, message = @tr("Number of characters for email has to be inferior to %constraint% but your text is %valueLength% characters long."))
    @FormField(label = @tr("Email"), isShort = false)
    private final String email;

    @RequestParam(role = Role.POST)
    @Optional
    @FormField(label = @tr("Country"))
    private final String country;

    @RequestParam(role = Role.POST)
    @Optional
    @FormField(label = @tr("Language"))
    private final String lang;

    private final ModifyDetailActionUrl url;

    public ModifyDetailAction(final ModifyDetailActionUrl url) {
        super(url);
        this.email = url.getEmail();
        this.country = url.getCountry();
        this.lang = url.getLang();
        this.url = url;
    }

    @Override
    protected Url doProcessRestricted(final Member me) {
        try {
            // EMAIL
            if (email != null && !email.trim().isEmpty() && !email.equals(me.getEmail())) {
                session.notifyGood(Context.tr("New email will replace the old one after you validate it with the link we send you."));
                me.setEmailToActivate(email.trim());

                final String activationKey = me.getEmailActivationKey();
                final MemberActivationActionUrl url = new MemberActivationActionUrl(activationKey, me.getLogin());

                final String content = Context.tr("Hi {0},\n\nYou wanted to change the email for your Elveos.org account. Please click on the following link to activate your new email: \n\n {1}",
                                                  me.getLogin(),
                                                  url.externalUrlString());

                final Mail activationMail = new Mail(email, Context.tr("Elveos.org new email activation"), content, "member-domodify");

                MailServer.getInstance().send(activationMail);
            }

            // LANGUAGE
            String langString = null;
            if (lang != null && !lang.isEmpty() && !lang.equals(me.getLocale().getLanguage())) {
                session.notifyGood(Context.tr("Language updated."));
                langString = lang;
            }

            // COUNTRY
            String countryString = null;
            if (country != null && !country.isEmpty() && !country.equals(me.getLocale().getCountry())) {
                session.notifyGood(Context.tr("Country updated."));
                countryString = country;
            }

            // LOCALE
            if (langString != null || countryString != null) {
                final String locale = ((langString != null) ? langString : me.getLocale().getLanguage()) + "_"
                        + ((countryString != null) ? countryString : me.getLocale().getCountry());
                me.setLocal(new Locale(locale));
            }
        } catch (final UnauthorizedOperationException e) {
            throw new ShallNotPassException(e);
        }

        return new MemberPageUrl(me);
    }

    @Override
    protected Url checkRightsAndEverything(final Member me) {
        try {
            if (email != null && !email.trim().isEmpty() && !email.equals(me.getEmail())
                    && (MemberManager.emailExists(email) && !email.equals(me.getEmailToActivate()))) {
                session.notifyError(Context.tr("Email already used."));
                url.getEmailParameter().addErrorMessage(Context.tr("Email already used."));
                return doProcessErrors();
            }
        } catch (final UnauthorizedOperationException e) {
            session.notifyWarning(Context.tr("Fail to read your email."));
            Log.web().error("Fail to read an email", e);
            return doProcessErrors();
        }
        return NO_ERROR;

    }

    @Override
    protected Url doProcessErrors() {
        return new ModifyMemberPageUrl();
    }

    @Override
    protected String getRefusalReason() {
        return Context.tr("You must be logged to modify your account settings.");
    }

    @Override
    protected void transmitParameters() {
        session.addParameter(url.getEmailParameter());
        session.addParameter(url.getCountryParameter());
        session.addParameter(url.getLangParameter());
    }
}
