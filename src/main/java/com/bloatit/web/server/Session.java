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

package com.bloatit.web.server;

import com.bloatit.framework.AuthToken;


public class Session {

    boolean isLogged() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    AuthToken getAuthToken() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String _(String s){
        return "";
    }

    Language getLanguage() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setAuthToken(AuthToken token) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setLogged(boolean b) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}