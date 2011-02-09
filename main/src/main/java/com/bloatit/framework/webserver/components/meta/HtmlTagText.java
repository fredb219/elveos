package com.bloatit.framework.webserver.components.meta;

import java.util.Collections;
import java.util.Iterator;

import com.bloatit.framework.exceptions.NonOptionalParameterException;
import com.bloatit.framework.webserver.components.writers.HtmlStream;

/**
 * <p>
 * HtmlTagText are used to input <b>un</b>escaped text
 * </p>
 * <p>
 * Should be used in the weird situations where other standard tags are not
 * flexible enough.
 * </p>
 * <p>
 * Usage :
 * 
 * <pre>
 * {@code another_component.add(new HtmlTagText("<span class="plop">foo</span>));}
 * </pre>
 * 
 * <b>Note : </b> In the previous example, class <code>HtmlSpan</code> should be
 * used
 * </p>
 */
public class HtmlTagText extends HtmlNode {

    protected String content;

    protected HtmlTagText() {
        super();
    }

    /**
     * Creates a component to add raw Html to a page
     * 
     * @param content
     *            the Html string to add
     */
    public HtmlTagText(final String content) {
        super();
        if (content == null) {
            throw new NonOptionalParameterException();
        }
        this.content = content;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Iterator<HtmlNode> iterator() {
        return Collections.EMPTY_LIST.iterator();
    }

    /**
     * Do not use Only present as a quick hack to write a tad cleaner html
     * content
     */
    public String _getContent() {
        return content;
    }

    @Override
    public final void write(final HtmlStream txt) {
        txt.writeRawText(content);
    }
}