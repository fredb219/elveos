/**
 *
 */
package com.bloatit.web.actions;

import java.util.Locale;

import com.bloatit.framework.exceptions.RedirectException;
import com.bloatit.framework.utils.MailUtils;
import com.bloatit.framework.webserver.Context;
import com.bloatit.framework.webserver.annotations.ParamConstraint;
import com.bloatit.framework.webserver.annotations.ParamContainer;
import com.bloatit.framework.webserver.annotations.RequestParam;
import com.bloatit.framework.webserver.annotations.tr;
import com.bloatit.framework.webserver.annotations.RequestParam.Role;
import com.bloatit.framework.webserver.masters.Action;
import com.bloatit.framework.webserver.url.Url;
import com.bloatit.model.Member;
import com.bloatit.web.url.LoginPageUrl;
import com.bloatit.web.url.MemberPageUrl;
import com.bloatit.web.url.RegisterActionUrl;
import com.bloatit.web.url.RegisterPageUrl;

/**
 * A response to a form used sign into the website (creation of a new user)
 */
@ParamContainer("member/docreate")
public class RegisterAction extends Action {

    public static final String LOGIN_CODE = "bloatit_login";
    public static final String PASSWORD_CODE = "bloatit_password";
    public static final String EMAIL_CODE = "bloatit_email";
    public static final String COUNTRY_CODE = "bloatit_country";
    public static final String LANGUAGE_CODE = "bloatit_lang";

    @RequestParam(name = RegisterAction.LOGIN_CODE, role = Role.POST)
    @ParamConstraint(min = "4", minErrorMsg = @tr("Number of characters for login has to be superior to 4"),//
                     max = "15", maxErrorMsg = @tr("Number of characters for login has to be inferior to 15"))
    private final String login;

    @RequestParam(name = RegisterAction.PASSWORD_CODE, role = Role.POST)
    @ParamConstraint(min = "4", minErrorMsg = @tr("Number of characters for password has to be superior to 4"),//
                     max = "15", maxErrorMsg = @tr("Number of characters for password has to be inferior to 15"))
    private final String password;

    @RequestParam(name = RegisterAction.EMAIL_CODE, role = Role.POST)
    @ParamConstraint(min = "4", minErrorMsg = @tr("Number of characters for email has to be superior to 5"),//
                     max = "30", maxErrorMsg = @tr("Number of characters for email address has to be inferior to 30"))
    private final String email;

    @RequestParam(name = RegisterAction.COUNTRY_CODE, role = Role.POST)
    private final String country;

    @RequestParam(name = RegisterAction.LANGUAGE_CODE, role = Role.POST)
    private final String lang;
    private final RegisterActionUrl url;

    public RegisterAction(final RegisterActionUrl url) {
        super(url);
        this.url = url;
        this.login = url.getLogin();
        this.password = url.getPassword();
        this.email = url.getEmail();
        this.lang = url.getLang();
        this.country = url.getCountry();
    }

    @Override
    protected final Url doProcess() throws RedirectException {
        final String userEmail = email.trim();
        if (!MailUtils.isValidEmail(userEmail)) {
            session.notifyError(Context.tr("Invalid email address : " + userEmail));
            session.addParameter(EMAIL_CODE, userEmail);
            session.addParameter(LOGIN_CODE, login);
            session.addParameter(PASSWORD_CODE, password);
            session.addParameter(COUNTRY_CODE, country);
            session.addParameter(LANGUAGE_CODE, lang);
            throw new RedirectException(new RegisterPageUrl());
        }

        final Locale locale = new Locale(lang, country);

        final Member m = new Member(login, password, email, locale);
        return new MemberPageUrl(m);
    }

    @Override
    protected final Url doProcessErrors() throws RedirectException {
        session.notifyList(url.getMessages());
        return new LoginPageUrl();
    }
}
