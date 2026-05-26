package com.strutslab.taglib;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class SectionHeaderTag extends TagSupport {

    private String title;
    private String anchorId;

    public void setTitle(String v) { this.title = v; }
    public void setAnchorId(String v) { this.anchorId = v; }

    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();

        StringBuilder sb = new StringBuilder();
        sb.append("<h2 id=\"").append(escapeHtml(anchorId)).append("\">");
        sb.append(escapeHtml(title));
        sb.append("</h2>");

        try {
            out.print(sb.toString());
        } catch (IOException e) {
            throw new JspException(e);
        }

        return SKIP_BODY;
    }

    @Override
    public void release() {
        super.release();
        this.title = null;
        this.anchorId = null;
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
