package com.bloatit.common;

import org.apache.log4j.Logger;

// TRACE
// DEBUG
// INFO
// WARN -> not correct behavior but non so serious
// ERROR -> should be reported to the admin
// FATAL -> should be reported to the admin

public final class Log {

    private Log() {
        // disactivate default ctor
    }

    private static final Logger FRAMEWORK = Logger.getLogger("com.bloatit.model");
    private static final Logger DATA = Logger.getLogger("com.bloatit.data");
    private static final Logger WEB = Logger.getLogger("com.bloatit.framework.webserver");
    private static final Logger SERVER = Logger.getLogger("com.bloatit.framework.webserver.server");
    private static final Logger MAIL = Logger.getLogger("com.bloatit.mail");

    public static Logger framework() {
        return FRAMEWORK;
    }

    public static Logger data() {
        return DATA;
    }

    public static Logger web() {
        return WEB;
    }

    public static Logger server() {
        return SERVER;
    }

    public static Logger mail() {
        return MAIL;
    }
}