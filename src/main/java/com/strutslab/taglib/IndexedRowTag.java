package com.strutslab.taglib;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class IndexedRowTag extends TagSupport {

    private int index;
    private String prefix;
    private String bean;

    public void setIndex(int v) { this.index = v; }
    public void setPrefix(String v) { this.prefix = v; }
    public void setBean(String v) { this.bean = v; }

    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();

        // Resolve the bean from page/request/session/application scope
        Object beanObj = pageContext.findAttribute(bean);

        String seqNo = String.valueOf(index + 1);
        String workContent = getBeanProperty(beanObj, "workContent");
        String person = getBeanProperty(beanObj, "person");
        String deadline = getBeanProperty(beanObj, "deadline");
        String priority = getBeanProperty(beanObj, "priority");

        String idxStr = String.valueOf(index);
        String indexedPrefix = prefix + "[" + idxStr + "]";

        StringBuilder sb = new StringBuilder();
        sb.append("<tr>\n");

        // seq_no
        sb.append("  <td class=\"seq-no\">").append(seqNo).append("</td>\n");

        // work_content input
        sb.append("  <td>");
        sb.append("<input type=\"text\" name=\"").append(indexedPrefix).append(".workContent\"");
        sb.append(" value=\"").append(nullToEmpty(workContent)).append("\"");
        sb.append(" size=\"30\" />");
        sb.append("</td>\n");

        // person text + popup button
        sb.append("  <td>");
        sb.append("<input type=\"text\" name=\"").append(indexedPrefix).append(".person\"");
        sb.append(" value=\"").append(nullToEmpty(person)).append("\"");
        sb.append(" size=\"15\" />");
        sb.append("<button type=\"button\" class=\"popup-btn\"");
        sb.append(" onclick=\"openPersonPopup('").append(indexedPrefix).append(".person')\">");
        sb.append("検索</button>");
        sb.append("</td>\n");

        // deadline input
        sb.append("  <td>");
        sb.append("<input type=\"text\" name=\"").append(indexedPrefix).append(".deadline\"");
        sb.append(" value=\"").append(nullToEmpty(deadline)).append("\"");
        sb.append(" size=\"10\" maxlength=\"8\" class=\"datepicker\" />");
        sb.append("</td>\n");

        // priority select
        sb.append("  <td>");
        sb.append("<select name=\"").append(indexedPrefix).append(".priority\">");
        String[] priorities = {"高", "中", "低"};
        for (String p : priorities) {
            sb.append("<option value=\"").append(p).append("\"");
            if (p.equals(priority)) {
                sb.append(" selected=\"selected\"");
            }
            sb.append(">").append(p).append("</option>");
        }
        sb.append("</select>");
        sb.append("</td>\n");

        // delete button
        sb.append("  <td>");
        sb.append("<button type=\"button\" class=\"delete-row-btn\" onclick=\"deleteRow(this)\">");
        sb.append("削除</button>");
        sb.append("</td>\n");

        sb.append("</tr>\n");

        try {
            out.print(sb.toString());
        } catch (IOException e) {
            throw new JspException(e);
        }

        return SKIP_BODY;
    }

    private String getBeanProperty(Object bean, String prop) {
        if (bean == null) return null;
        try {
            String getter = "get" + Character.toUpperCase(prop.charAt(0)) + prop.substring(1);
            Method m = bean.getClass().getMethod(getter);
            Object val = m.invoke(bean);
            return val != null ? val.toString() : null;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private String nullToEmpty(String s) {
        return s != null ? escapeHtml(s) : "";
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
