/*
 * Copyright (C) 2010 BloatIt. This file is part of BloatIt. BloatIt is free
 * software: you can redistribute it and/or modify it under the terms of the GNU
 * Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * BloatIt is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details. You should have received a copy of the GNU Affero General Public
 * License along with BloatIt. If not, see <http://www.gnu.org/licenses/>.
 */
package com.bloatit.web.pages;

import static com.bloatit.framework.webserver.Context.tr;

import com.bloatit.common.Log;
import com.bloatit.framework.exceptions.RedirectException;
import com.bloatit.framework.exceptions.UnauthorizedOperationException;
import com.bloatit.framework.utils.PageIterable;
import com.bloatit.framework.webserver.Context;
import com.bloatit.framework.webserver.PageNotFoundException;
import com.bloatit.framework.webserver.annotations.Message.Level;
import com.bloatit.framework.webserver.annotations.ParamConstraint;
import com.bloatit.framework.webserver.annotations.ParamContainer;
import com.bloatit.framework.webserver.annotations.RequestParam;
import com.bloatit.framework.webserver.annotations.tr;
import com.bloatit.framework.webserver.components.HtmlDiv;
import com.bloatit.framework.webserver.components.HtmlLink;
import com.bloatit.framework.webserver.components.HtmlList;
import com.bloatit.framework.webserver.components.HtmlListItem;
import com.bloatit.framework.webserver.components.HtmlParagraph;
import com.bloatit.framework.webserver.components.HtmlRenderer;
import com.bloatit.framework.webserver.components.HtmlTitleBlock;
import com.bloatit.framework.webserver.components.PlaceHolderElement;
import com.bloatit.framework.webserver.components.meta.HtmlText;
import com.bloatit.framework.webserver.components.meta.XmlNode;
import com.bloatit.model.Group;
import com.bloatit.model.Member;
import com.bloatit.model.right.Action;
import com.bloatit.web.components.HtmlPagedList;
import com.bloatit.web.components.TeamListRenderer;
import com.bloatit.web.pages.master.MasterPage;
import com.bloatit.web.url.MemberPageUrl;
import com.bloatit.web.url.TeamPageUrl;

/**
 * <p>
 * A page used to display member information.
 * </p>
 * <p>
 * If the consulted member is the same as the logged member, then this page will
 * propose to edit account parameters
 * </p>
 */
@ParamContainer("member")
public final class MemberPage extends MasterPage {
    // Keep me here ! I am needed for the Url generation !
    private HtmlPagedList<Group> pagedTeamList;
    private final MemberPageUrl url;

    public static final String MEMBER_FIELD_NAME = "id";

    @ParamConstraint(optionalErrorMsg = @tr("The id of the member is incorrect or missing"))
    @RequestParam(name = MEMBER_FIELD_NAME, level = Level.ERROR)
    private final Member member;

    public MemberPage(final MemberPageUrl url) {
        super(url);
        this.url = url;
        this.member = url.getMember();
    }

    @Override
    protected void doCreate() throws RedirectException {
        session.notifyList(url.getMessages());
        if (url.getMessages().hasMessage(Level.ERROR)) {
            throw new PageNotFoundException();
        }

        final HtmlDiv master = new HtmlDiv("padding_box");
        add(master);

        try {
            final HtmlTitleBlock memberTitle = new HtmlTitleBlock(Context.tr("Member: ") + member.getDisplayName(), 1);
            master.add(memberTitle);
            final HtmlList memberInfo = new HtmlList();
            memberTitle.add(memberInfo);

            memberInfo.add((tr("Full name: ") + member.getFullname()));
            memberInfo.add(tr("Login: ") + member.getLogin());
            if (member.canAccessEmail(Action.READ)) {
                memberInfo.add(new HtmlText(tr("Email: ") + member.getEmail()));
            }
            memberInfo.add(new HtmlText(tr("Karma: ") + member.getKarma()));

            // A list of all users group
            final HtmlTitleBlock memberGroups = new HtmlTitleBlock(Context.tr("List of groups"), 2);
            memberTitle.add(memberGroups);
            final PageIterable<Group> teamList = member.getGroups();
            final HtmlRenderer<Group> teamRenderer = new TeamListRenderer();
            final MemberPageUrl clonedUrl = new MemberPageUrl(url);
            pagedTeamList = new HtmlPagedList<Group>(teamRenderer, teamList, clonedUrl, clonedUrl.getPagedTeamListUrl());
            memberGroups.add(pagedTeamList);

        } catch (final UnauthorizedOperationException e) {
            add(new HtmlParagraph(tr("For obscure reasons, you are not allowed to see the details of this member.")));
        }
    }

    @Override
    protected String getPageTitle() {
        if (member != null) {
            try {
                return tr("Member - ") + member.getLogin();
            } catch (final UnauthorizedOperationException e) {
                return tr("Member - John Doe");
            }
        }
        return tr("Member - No member");
    }

    @Override
    public boolean isStable() {
        return true;
    }

    private class GroupListRenderer implements HtmlRenderer<Group> {
        @Override
        public XmlNode generate(final Group team) {
            final TeamPageUrl teamUrl = new TeamPageUrl(team);
            try {
                HtmlLink htmlLink;
                htmlLink = teamUrl.getHtmlLink(team.getLogin());

                return new HtmlListItem(htmlLink);
            } catch (final UnauthorizedOperationException e) {
                Log.web().warn(e);
            }
            return new PlaceHolderElement();
        }
    }
}
