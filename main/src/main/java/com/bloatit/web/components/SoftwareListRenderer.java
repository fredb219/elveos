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
package com.bloatit.web.components;

import com.bloatit.framework.webprocessor.components.HtmlDiv;
import com.bloatit.framework.webprocessor.components.HtmlLink;
import com.bloatit.framework.webprocessor.components.HtmlRenderer;
import com.bloatit.framework.webprocessor.components.meta.HtmlNode;
import com.bloatit.framework.webprocessor.context.Context;
import com.bloatit.model.Software;
import com.bloatit.web.components.HtmlFollowButton.HtmlFollowSoftwareButton;
import com.bloatit.web.linkable.softwares.SoftwaresTools;
import com.bloatit.web.url.SoftwarePageUrl;

/**
 * A simple renderer for teams that display only their name on one line, plus a
 * link to their page
 */
public class SoftwareListRenderer implements HtmlRenderer<Software> {
    @Override
    public HtmlNode generate(final Software software) {
        final SoftwarePageUrl memberUrl = new SoftwarePageUrl(software);
        final HtmlDiv box = new HtmlDiv("actor-box");

        box.add(new HtmlDiv("actor-box-avatar").add((new SoftwaresTools.Logo(software))));

        final HtmlDiv content = new HtmlDiv("actor-box-content");
        box.add(content);

        // Name
        final HtmlDiv nameBox = new HtmlDiv("actor-box-actor-name");
        HtmlLink htmlLink;
        htmlLink = memberUrl.getHtmlLink(software.getName());
        htmlLink.setCssClass("software-link");
        nameBox.add(htmlLink);
        content.add(nameBox);

        // Subtitle
        final HtmlDiv subtitle = new HtmlDiv("actor-box-subtitle");
        content.add(subtitle);
        subtitle.addText(Context.trn("{0} feature", "{0} features", software.getFeatures().size(), software.getFeatures().size()));

        // Follow
        content.add(new HtmlFollowSoftwareButton(software));

        return box;
    }
}
