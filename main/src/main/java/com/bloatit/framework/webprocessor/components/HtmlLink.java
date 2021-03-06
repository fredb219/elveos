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

package com.bloatit.framework.webprocessor.components;

import com.bloatit.framework.webprocessor.components.meta.HtmlBranch;
import com.bloatit.framework.webprocessor.components.meta.HtmlNode;
import com.bloatit.framework.webprocessor.components.meta.HtmlText;

/**
 * Class to create htmllinks ({@code<a href="...">plop</a>})
 */
public class HtmlLink extends HtmlBranch {

    /**
     * Creates a link with a given URL and a given displayed element. Element
     * can be any HtmlNode Will generate :
     * 
     * <pre>
     * <a href="url">displayed</a>
     * </pre>
     */
    public HtmlLink(final String url, final HtmlNode displayed) {
        super("a");
        addAttribute("href", url);
        add(displayed);
    }

    /**
     * Creates a link with a given URL and a given displayed element. Element
     * can be any HtmlNode Will generate :
     * 
     * <pre>
     * <a href="url">displayedText</a>
     * </pre>
     */
    public HtmlLink(final String url, final String displayedText) {
        super("a");
        addAttribute("href", url);
        add(new HtmlText(displayedText));
    }

    /**
     * Creates a blank link. Use add to further add elements
     * 
     * @param url
     */
    public HtmlLink(final String url) {
        super("a");
        addAttribute("href", url);
    }

    private HtmlLink(final HtmlLink htmlLink) {
        super(htmlLink);
    }

    public void setTitle(final String string) {
        addAttribute("title", string);
    }

    @Override
    public HtmlLink clone() {
        return new HtmlLink(this);
    }

    /**
     * Convenience method to set the link as no follow
     */
    public void setNoFollow() {
        addAttribute("rel", "nofollow");
    }

    public void setOpenInNewPage() {
        addAttribute("target", "_blank");
    }
}
