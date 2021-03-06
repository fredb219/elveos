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
package com.bloatit.framework.webprocessor.components.renderer;

import com.bloatit.framework.webprocessor.components.HtmlDiv;
import com.bloatit.framework.webprocessor.components.meta.HtmlNonEscapedText;
import com.github.rjeschke.txtmark.Processor;

/**
 * <p>
 * A class used to represent Markdown text in html format
 * </p>
 */
public class HtmlMarkdownRenderer extends HtmlDiv {
    private String renderered;

    /**
     * Creates a new MarkdownRenderer based on markdown or html text
     *
     * @param text the content to display, must be markdown text if
     *            <code>alreadyRenderer</code> is <code>true</code> or html text
     *            if <code>alreadyRenderer</code> is <code>false</code>
     */
    public HtmlMarkdownRenderer(final String text) {
        this(text, true);
    }

    public HtmlMarkdownRenderer(final String text, boolean safeMode) {
        super("markdown_block");
        renderered = Processor.process(text, safeMode);
        add(new HtmlNonEscapedText(renderered));
    }

    /**
     * @return the html content
     */
    public String getRendereredContent() {
        return renderered;
    }
}
