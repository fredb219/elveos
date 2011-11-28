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
package com.bloatit.web.linkable.timeline;

import static com.bloatit.framework.webprocessor.context.Context.tr;

import java.util.Locale;

import com.bloatit.framework.utils.i18n.Language;
import com.bloatit.framework.webprocessor.annotations.NonOptional;
import com.bloatit.framework.webprocessor.annotations.ParamContainer;
import com.bloatit.framework.webprocessor.annotations.RequestParam;
import com.bloatit.framework.webprocessor.annotations.RequestParam.Role;
import com.bloatit.framework.webprocessor.annotations.tr;
import com.bloatit.framework.webprocessor.components.HtmlDiv;
import com.bloatit.framework.webprocessor.components.HtmlImage;
import com.bloatit.framework.webprocessor.components.HtmlTitle;
import com.bloatit.framework.webprocessor.components.advanced.showdown.MarkdownEditor;
import com.bloatit.framework.webprocessor.components.form.FieldData;
import com.bloatit.framework.webprocessor.components.form.HtmlForm;
import com.bloatit.framework.webprocessor.components.form.HtmlHidden;
import com.bloatit.framework.webprocessor.components.form.HtmlSubmit;
import com.bloatit.framework.webprocessor.components.form.HtmlTextArea;
import com.bloatit.framework.webprocessor.components.form.HtmlTextField;
import com.bloatit.framework.webprocessor.components.meta.HtmlElement;
import com.bloatit.framework.webprocessor.components.meta.HtmlNode;
import com.bloatit.framework.webprocessor.context.Context;
import com.bloatit.framework.webprocessor.url.PageNotFoundUrl;
import com.bloatit.model.Description;
import com.bloatit.model.Image;
import com.bloatit.model.Member;
import com.bloatit.model.Translation;
import com.bloatit.web.WebConfiguration;
import com.bloatit.web.linkable.IndexPage;
import com.bloatit.web.linkable.master.Breadcrumb;
import com.bloatit.web.linkable.master.LoggedElveosPage;
import com.bloatit.web.url.TimelinePageUrl;
import com.bloatit.web.url.TranslateActionUrl;
import com.bloatit.web.url.TranslatePageUrl;

/**
 * Page that hosts the form to create a new feature
 */
@ParamContainer("timeline")
public final class TimelinePage extends LoggedElveosPage {

    private final TimelinePageUrl url;

    public TimelinePage(final TimelinePageUrl url) {
        super(url);
        this.url = url;
    }

    @Override
    protected String createPageTitle() {
        return tr("Timeline");
    }

    @Override
    public boolean isStable() {
        return true;
    }

    @Override
    public HtmlElement createRestrictedContent(final Member loggedUser) {

        final HtmlDiv layout = new HtmlDiv("timeline_page");

        final HtmlDiv menuBar = new HtmlDiv("menu_bar");
        layout.add(menuBar);
        {
            final HtmlDiv menuBarItemNewFeature = new HtmlDiv("menu_bar_item");
            menuBar.add(menuBarItemNewFeature);
            {
                final HtmlDiv menuBarItemImage = new HtmlDiv("menu_bar_item_image");
                menuBarItemNewFeature.add(menuBarItemImage);
                menuBarItemImage.add(new HtmlImage(new Image(WebConfiguration.getImgIdeaSmall()), Context.tr("Request a feature")));
                final HtmlDiv menuBarItemLink = new HtmlDiv("menu_bar_item_link");
                menuBarItemNewFeature.add(menuBarItemLink);
                menuBarItemLink.add(new PageNotFoundUrl().getHtmlLink(Context.tr("Request a feature")));
            }

            final HtmlDiv menuBarItemManageFollow = new HtmlDiv("menu_bar_item");
            menuBar.add(menuBarItemManageFollow);
            {
                final HtmlDiv menuBarItemImage = new HtmlDiv("menu_bar_item_image");
                menuBarItemManageFollow.add(menuBarItemImage);
                menuBarItemImage.add(new HtmlImage(new Image(WebConfiguration.getImgManageSmall()), Context.tr("Manage follows")));
                final HtmlDiv menuBarItemLink = new HtmlDiv("menu_bar_item_link");
                menuBarItemManageFollow.add(menuBarItemLink);
                menuBarItemLink.add(new PageNotFoundUrl().getHtmlLink(Context.tr("Manage follows")));
            }
            
            final HtmlDiv menuBarItemManageNotif = new HtmlDiv("menu_bar_item");
            menuBar.add(menuBarItemManageNotif);
            {
                final HtmlDiv menuBarItemImage = new HtmlDiv("menu_bar_item_image");
                menuBarItemManageNotif.add(menuBarItemImage);
                menuBarItemImage.add(new HtmlImage(new Image(WebConfiguration.getImgManageNotifSmall()), Context.tr("Manage notifications")));
                final HtmlDiv menuBarItemLink = new HtmlDiv("menu_bar_item_link");
                menuBarItemManageNotif.add(menuBarItemLink);
                menuBarItemLink.add(new PageNotFoundUrl().getHtmlLink(Context.tr("Manage notifications")));
            }
            
            final HtmlDiv menuBarItemRSS = new HtmlDiv("menu_bar_item");
            menuBar.add(menuBarItemRSS);
            {
                final HtmlDiv menuBarItemImage = new HtmlDiv("menu_bar_item_image");
                menuBarItemRSS.add(menuBarItemImage);
                menuBarItemImage.add(new HtmlImage(new Image(WebConfiguration.getImgRssSmall()), Context.tr("Rss feed")));
                final HtmlDiv menuBarItemLink = new HtmlDiv("menu_bar_item_link");
                menuBarItemRSS.add(menuBarItemLink);
                menuBarItemLink.add(new PageNotFoundUrl().getHtmlLink(Context.tr("Rss feed")));
            }
        }

        final HtmlDiv timelineBlock = new HtmlDiv("timeline_block");
        layout.add(timelineBlock);
        {
            final HtmlDiv leftColumn = new HtmlDiv("left_column");
            timelineBlock.add(leftColumn);
            final HtmlDiv timeColumn = new HtmlDiv("time_column");
            timelineBlock.add(timeColumn);
            {
                final HtmlDiv timeColumnHeader = new HtmlDiv("time_column_header");
                timeColumn.add(timeColumnHeader);
                
                timeColumn.add(generateDay("Dec 17", 200));
                timeColumn.add(generateDay("Dec 15", 100));
                timeColumn.add(generateDay("Dec 12", 150));
                timeColumn.add(generateDay("Dec 11", 200));
                
                final HtmlDiv timeColumnFooter = new HtmlDiv("time_column_footer");
                timeColumn.add(timeColumnFooter);
            }
            
            final HtmlDiv rightColumn = new HtmlDiv("right_column");
            timelineBlock.add(rightColumn);
            
        }
        return layout;
    }

    private HtmlNode generateDay(String text, int height) {
        final HtmlDiv dayBlock = new HtmlDiv("day");
        dayBlock.addText(text);
        dayBlock.addAttribute("style", "height: "+height+"px;");
        return dayBlock;
    }

    @Override
    protected Breadcrumb createBreadcrumb(final Member member) {
        return TimelinePage.generateBreadcrumb();
    }

    private static Breadcrumb generateBreadcrumb() {
        final Breadcrumb breadcrumb = IndexPage.generateBreadcrumb();

        breadcrumb.pushLink(new TimelinePageUrl().getHtmlLink(tr("Timeline")));

        return breadcrumb;
    }

    @Override
    public String getRefusalReason() {
        return tr("You must be logged to add a translation.");
    }
}
