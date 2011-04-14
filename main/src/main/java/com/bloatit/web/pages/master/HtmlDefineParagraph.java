package com.bloatit.web.pages.master;

import com.bloatit.framework.webprocessor.components.HtmlParagraph;
import com.bloatit.framework.webprocessor.components.HtmlSpan;
import com.bloatit.framework.webprocessor.components.meta.HtmlElement;
import com.bloatit.framework.webprocessor.components.meta.XmlNode;

public class HtmlDefineParagraph extends HtmlParagraph {

    public HtmlDefineParagraph(final String key, final String text) {
        setCssClass("define_p");
        add(new HtmlSpan("define_key").addText(key));
        addText(text);
    }

    public HtmlDefineParagraph(final String key, final HtmlElement body) {
        setCssClass("define_p");
        add(new HtmlSpan("define_key").addText(key));
        add(body);
    }

    public XmlNode addCssClass(final String css) {
        setCssClass("define_p " + css);
        return this;
    }
}