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
package com.bloatit.framework.webprocessor.components.form;

import com.bloatit.framework.webprocessor.components.HtmlDiv;
import com.bloatit.framework.webprocessor.components.meta.HtmlNode;

/**
 * <p>
 * Class used to created html textarea blocks
 * </p>
 * <p>
 *
 * <pre>
 * <div>
 *      <div>
 * <label for="...">labeltext</label>
 *
 * </p>
 * <div> <textarea name="..." class="cssClass" ...>defaultValue</textarea>
 * </div> </div> </pre> </p>
 */
public final class HtmlTextArea extends HtmlFormField {

    public HtmlTextArea(final String name, final int rows, final int cols) {
        super(InputBlock.create(new HtmlSimpleTextArea(rows, cols)), name);
    }

    public HtmlTextArea(final String name, final String label, final int rows, final int cols) {
        super(InputBlock.create(new HtmlSimpleTextArea(rows, cols)), name, label);
    }

    @Override
    public void setComment(final HtmlNode comment) {
        final HtmlDiv commentBlock = new HtmlDiv("comment");
        commentBlock.add(comment);
        this.commentPh.add(commentBlock);
    }

    @Override
    protected void doSetDefaultStringValue(final String value) {
        ((HtmlSimpleTextArea) this.inputBlock.getInputElement()).setDefaultValue(value);
    }
}
