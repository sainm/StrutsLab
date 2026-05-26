package com.strutslab.taglib;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class StatusBadgeTag extends TagSupport {

    private static final Map<String, String> STATUS_CSS_MAP;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("未了", "badge-gray");
        map.put("調査中", "badge-yellow");
        map.put("対応中", "badge-orange");
        map.put("一部完了", "badge-yellow");
        map.put("完了", "badge-green");
        map.put("再発防止", "badge-blue");
        map.put("クローズ", "badge-green");
        map.put("申請中", "badge-yellow");
        map.put("承認済", "badge-green");
        map.put("差戻", "badge-red");
        map.put("在庫切れ", "badge-red");
        map.put("僅少", "badge-yellow");
        map.put("充足", "badge-green");
        map.put("軽微", "badge-gray");
        map.put("中", "badge-yellow");
        map.put("重大", "badge-orange");
        map.put("緊急", "badge-red");
        STATUS_CSS_MAP = Collections.unmodifiableMap(map);
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

    @Override
    public void release() {
        super.release();
        this.status = null;
        this.type = "general";
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
