/*
 * Copyright (C) 2010 BloatIt.
 *
 * This file is part of BloatIt.
 *
 * BloatIt is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BloatIt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with BloatIt. If not, see <http://www.gnu.org/licenses/>.
 */

package com.bloatit.web.html.pages;

import com.bloatit.web.html.HtmlElement;
import com.bloatit.web.html.HtmlNode;

/**
 * Class to describe HtmlContainers
 */
public abstract class HtmlContainerElement extends HtmlElement {

    protected HtmlContainerElement() {
        super();
    }

    public HtmlContainerElement(final String tag) {
        super(tag);
    }

    @Override
    public HtmlContainerElement add(final HtmlNode html) {
        super.add(html);
        return this;
    }

    @Override
    public HtmlContainerElement addText(final String text) {
        super.addText(text);
        return this;
    }

    @Override
    public HtmlContainerElement addAttribute(final String name, final String value) {
        super.addAttribute(name, value);
        return this;
    }

    @Override
    public HtmlContainerElement setCssClass(final String cssClass) {
        super.setCssClass(cssClass);
        return this;
    }

    @Override
    public HtmlContainerElement setId(final String id) {
        super.setId(id);
        return this;
    }

}