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
package com.bloatit.framework.webprocessor.masters;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import com.bloatit.common.Log;
import com.bloatit.framework.exceptions.highlevel.BadProgrammerException;
import com.bloatit.framework.exceptions.lowlevel.RedirectException;
import com.bloatit.framework.webprocessor.ErrorMessage;
import com.bloatit.framework.webprocessor.ErrorMessage.Level;
import com.bloatit.framework.webprocessor.ModelAccessor;
import com.bloatit.framework.webprocessor.WebProcessor;
import com.bloatit.framework.webprocessor.annotations.Message;
import com.bloatit.framework.webprocessor.annotations.ParamContainer;
import com.bloatit.framework.webprocessor.components.HtmlGenericElement;
import com.bloatit.framework.webprocessor.components.PlaceHolderElement;
import com.bloatit.framework.webprocessor.components.meta.HtmlBranch;
import com.bloatit.framework.webprocessor.components.meta.HtmlElement;
import com.bloatit.framework.webprocessor.components.meta.XmlText;
import com.bloatit.framework.webprocessor.context.Context;
import com.bloatit.framework.webprocessor.context.Session;
import com.bloatit.framework.webprocessor.masters.Header.Robot;
import com.bloatit.framework.webprocessor.masters.HttpResponse.StatusCode;
import com.bloatit.framework.webprocessor.url.Messages;
import com.bloatit.framework.webprocessor.url.Url;
import com.bloatit.web.pages.master.HtmlNotification;

public abstract class Page implements Linkable {

    private final Url thisUrl;
    private Header pageHeader;

    public Page(final Url url) {
        super();
        ModelAccessor.setReadOnly();
        this.thisUrl = url;
    }

    @Override
    public final void writeToHttp(final HttpResponse response, final WebProcessor server) throws RedirectException, IOException {
        response.setStatus(getResponseStatus());
        response.writePage(create());
    }

    public final String getName() {
        if (getClass().getAnnotation(ParamContainer.class) != null) {
            return getClass().getAnnotation(ParamContainer.class).value();
        }
        return getClass().getName().toLowerCase(Locale.ENGLISH);
    }

    public final Url getUrl() {
        return thisUrl;
    }

    // -----------------------------------------------------------------------
    // Template method pattern: Abstract methods
    // -----------------------------------------------------------------------

    public abstract boolean isStable();

    /**
     * Indicates the status of the page
     * 
     * @return a status code matching the real status of the response
     */
    protected abstract StatusCode getResponseStatus();

    /**
     * This method is called only if {@link #createBody()} and
     * {@link #createBodyOnParameterError()} does not throw exception.
     * 
     * @return the title of the page.
     */
    protected abstract String getTitle();

    /**
     * Create the body tag of the HTML page. This method is called when there
     * <b>is no error</b> in the parameters from the {@link Url}.
     * 
     * @return the body tag of the HTML page.
     * @throws RedirectException if there is a problem during the page
     *             construction and we want to redirect on an other page.
     */
    protected abstract HtmlElement createBody() throws RedirectException;

    /**
     * Create the body tag of the HTML page. This method is called when there
     * <b>is at least one error</b> in the parameters from the {@link Url}.
     * 
     * @return the body tag of the HTML page.
     * @throws RedirectException if there is a problem during the page
     *             construction and we want to redirect on an other page.
     */
    protected abstract HtmlElement createBodyOnParameterError() throws RedirectException;

    /**
     * Adds a user notification
     */
    protected abstract void addNotification(final HtmlNotification note);

    /**
     * A method that returns the description of the page as inserted inside the
     * {@code <meta name="description">} tag in page header.
     * 
     * @return the string describing the page
     */
    protected abstract String getPageDescription();

    /**
     * A method that returns the list of page specific robots information
     * inserted inside the {@code <meta name="robots>} tag in page header.
     * 
     * @return the list of keywords
     */
    protected abstract Set<Robot> getRobots();

    // -----------------------------------------------------------------------
    // Template method pattern: procedure.
    // -----------------------------------------------------------------------

    private final HtmlElement create() throws RedirectException {
        Log.framework().trace("Writing page: " + thisUrl.urlString());
        final PlaceHolderElement page = new PlaceHolderElement();

        HtmlElement bodyContent;
        if (thisUrl.hasError()) {
            final Messages messages = thisUrl.getMessages();
            Context.getSession().notifyList(messages);
            for (final Message message : messages) {
                Log.framework().trace("Error messages from Url system: " + message.getMessage());
            }
            // Abstract method cf: template method pattern
            bodyContent = createBodyOnParameterError();
        } else {
            // Abstract method cf: template method pattern
            bodyContent = createBody();
        }

        page.add(new XmlText("<!DOCTYPE html>"));

        final HtmlBranch html = new HtmlGenericElement("html");
        page.add(html);
        html.addAttribute("xmlns", "http://www.w3.org/1999/xhtml");
        html.addAttribute("xml:lang", Context.getLocalizator().getCode());
        pageHeader = new Header(getTitle(), getPageDescription(), getRobots());
        html.add(pageHeader);

        html.add(bodyContent);

        // Set the last stable page into the session
        // Abstract method cf: template method pattern
        if (isStable()) {
            Context.getSession().setTargetPage(null);
            Context.getSession().setLastStablePage(thisUrl);
        }
        Context.getSession().setLastVisitedPage(thisUrl);

        // Display waiting notifications
        // Abstract method cf: template method pattern
        addWaitingNotifications();

        // Do not forget to add the css/js files.
        for (final String css : page.getAllCss()) {
            pageHeader.addCss(css);
        }
        for (final String js : page.getAllJs()) {
            pageHeader.addJs(js);
        }

        return page;
    }

    private void addWaitingNotifications() {
        for (final ErrorMessage notification : Context.getSession().getNotifications()) {
            switch (notification.getLevel()) {
                case FATAL:
                    addNotification(new HtmlNotification(Level.FATAL, notification.getMessage()));
                    break;
                case WARNING:
                    addNotification(new HtmlNotification(Level.WARNING, notification.getMessage()));
                    break;
                case INFO:
                    addNotification(new HtmlNotification(Level.INFO, notification.getMessage()));
                    break;
                default:
                    throw new BadProgrammerException("Unknown level: " + notification.getLevel());
            }
        }
        Context.getSession().flushNotifications();
    }
}
