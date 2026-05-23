package com.strutslab.taglib;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.strutslab.dto.TimelineDto;

public class TimelineTag extends TagSupport {

    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();

        List<TimelineDto> timeline = (List<TimelineDto>) pageContext.getRequest().getAttribute("timeline");
        if (timeline == null) {
            timeline = new ArrayList<>();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<table class=\"timeline\">\n");
        sb.append("  <thead>\n");
        sb.append("    <tr>");
        sb.append("<th>日時</th>");
        sb.append("<th>操作者</th>");
        sb.append("<th>操作内容</th>");
        sb.append("<th>変更前ステータス</th>");
        sb.append("<th>変更後ステータス</th>");
        sb.append("</tr>\n");
        sb.append("  </thead>\n");
        sb.append("  <tbody>\n");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        for (TimelineDto dto : timeline) {
            String dt = dto.getActionDatetime() != null ? sdf.format(dto.getActionDatetime()) : "";
            sb.append("    <tr>");
            sb.append("<td class=\"timeline-datetime\">").append(escapeHtml(dt)).append("</td>");
            sb.append("<td class=\"timeline-user\">").append(escapeHtml(dto.getActionUser())).append("</td>");
            sb.append("<td class=\"timeline-content\">").append(escapeHtml(dto.getActionContent())).append("</td>");
            sb.append("<td class=\"timeline-status\">").append(escapeHtml(dto.getStatusFrom())).append("</td>");
            sb.append("<td class=\"timeline-status\">").append(escapeHtml(dto.getStatusTo())).append("</td>");
            sb.append("</tr>\n");
        }

        sb.append("  </tbody>\n");
        sb.append("</table>\n");

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
