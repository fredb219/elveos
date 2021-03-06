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

import com.bloatit.framework.webprocessor.components.meta.HtmlLeaf;
import com.bloatit.framework.webprocessor.components.meta.HtmlNode;
import com.bloatit.framework.webprocessor.components.meta.HtmlText;

/**
 * Handles Html list, both numbered and not numbered
 */
public class HtmlList extends HtmlLeaf {

    public enum ListType {
        BULLET, NUMBERED
    }

    /**
     * Creates a list (not numbered)
     */
    public HtmlList() {
        super("ul");
    }

    /**
     * Creates a list with the precised type (numbered or bullet)
     * 
     * @param type the type of the list
     */
    public HtmlList(final ListType type) {
        super((type == ListType.BULLET) ? "ul" : "ol");
    }

    public final HtmlList add(final String element) {
        return add(new HtmlText(element));
    }
    
    @Override
    public boolean selfClosable() {
        return false;
    }

    @Override
    public final HtmlList add(final HtmlNode element) {
        super.add(new HtmlListItem(element));
        return this;
    }
}
