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
package com.bloatit.web.linkable.documentation;

import com.bloatit.framework.webprocessor.components.HtmlDiv;
import com.bloatit.framework.webprocessor.components.HtmlParagraph;
import com.bloatit.framework.webprocessor.components.javascript.JsShowHide;
import com.bloatit.web.linkable.documentation.HtmlDocumentationRenderer.DocumentationType;
import com.bloatit.web.linkable.master.sidebar.SideBarElementLayout;

public class SideBarDocumentationBlock extends SideBarElementLayout {
    public SideBarDocumentationBlock(final String key) {
        add(new HtmlDocumentationRenderer(DocumentationType.FRAME, key));
    }

    public SideBarDocumentationBlock(final String key, final String hiddenTitle) {

        final JsShowHide showHide = new JsShowHide(this, false);
        showHide.setHasFallback(false);

        final HtmlParagraph showHideLink = new HtmlParagraph(hiddenTitle, "fake_link");
        add(showHideLink);
        showHide.addActuator(showHideLink);

        final HtmlDiv documentationRendererBlock = new HtmlDiv();
        final HtmlDocumentationRenderer documentationRenderer = new HtmlDocumentationRenderer(DocumentationType.FRAME, key);
        documentationRendererBlock.add(documentationRenderer);
        add(documentationRendererBlock);

        showHide.addListener(documentationRendererBlock);
        showHide.apply();
    }
}
