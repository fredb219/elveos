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
package com.bloatit.web.linkable.documentation;

import com.bloatit.framework.webprocessor.annotations.ParamContainer;
import com.bloatit.web.linkable.master.AliasAction;
import com.bloatit.web.url.DocumentationPageUrl;
import com.bloatit.web.url.DocumentationRootPageUrl;

@ParamContainer("documentation")
public final class DocumentationRootPage extends AliasAction {

    public DocumentationRootPage(final DocumentationRootPageUrl url) {
        super(url, new DocumentationPageUrl(DocumentationPage.DEFAULT_DOC));
    }

}
