package test.html;

import java.util.Collections;
import java.util.Iterator;
import test.Text;

/**
 * <p>Class used to directly input text (any format) into the page.</p>
 * <p>Usage
 * another_component.add(new HtmlText("<span class="plop">foo</span>));
 * </p>
 */
public final class HtmlText extends HtmlNode {

    private String content;

    /**
     * Creates a component to add raw Html to a page
     * @param content the Html string to add
     */
    public HtmlText(String content) {
        super();
        this.content = content;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Iterator<HtmlNode> iterator() {
        return Collections.EMPTY_LIST.iterator();
    }

    @Override
    public final void write(Text txt) {
        txt.writeLine(content);
    }
}
