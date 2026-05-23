package com.strutslab.taglib;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class StatusBadgeTag extends TagSupport {

    private static final Map<String, String> STATUS_CSS_MAP = new HashMap<>();
    static {
        STATUS_CSS_MAP.put("未了", "badge-gray");
        STATUS_CSS_MAP.put("一部完了", "badge-yellow");
        STATUS_CSS_MAP.put("完了", "badge-green");
        STATUS_CSS_MAP.put("申請中", "badge-yellow");
        STATUS_CSS_MAP.put("承認済", "badge-green");
        STATUS_CSS_MAP.put("差戻", "badge-red");
        STATUS_CSS_MAP.put("在庫切れ", "badge-red");
        STATUS_CSS_MAP.put("僅少", "badge-yellow");
        STATUS_CSS_MAP.put("充足", "badge-green");
    }

    private String status;
    private String type = "general";

    public void setStatus(String v) { this.status = v; }
    public void setType(String v) { this.type = v; }

    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();

        String cssClass = STATUS_CSS_MAP.getOrDefault(status, "badge-gray");
        String displayStatus = (status != null) ? status : "";

        StringBuilder sb = new StringBuilder();
        sb.append("<span class=\"badge ").append(cssClass).append("\">");
        sb.append(escapeHtml(displayStatus));
        sb.append("</span>");

        try {
            out.print(sb.toString());
        } catch (IOException e) {
            throw new JspException(e);
        }

        return SKIP_BODY;
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
