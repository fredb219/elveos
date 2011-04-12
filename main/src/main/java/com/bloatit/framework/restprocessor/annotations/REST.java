package com.bloatit.framework.restprocessor.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.bloatit.framework.restprocessor.RestServer.RequestMethod;

/**
 * <p>
 * Annotation used to describe methods that can be used via ReST.
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface REST {
    String name();

    RequestMethod method();

    public String[] params() default {};
}