package com.strutslab.taglib;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class DatePickerTag extends TagSupport {

    private String name;
    private String property;
    private String format = "YYYYMMDD";
    private boolean pastDisabled = false;

    public void setName(String v) { this.name = v; }
    public void setProperty(String v) { this.property = v; }
    public void setFormat(String v) { this.format = v; }
    public void setPastDisabled(boolean v) { this.pastDisabled = v; }

    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();

        // Resolve value from form bean
        String value = resolveValue();

        StringBuilder sb = new StringBuilder();
        sb.append("<input type=\"text\"");
        sb.append(" name=\"").append(name).append(".").append(property).append("\"");
        sb.append(" value=\"");
        if (value != null) {
            sb.append(escapeHtml(value));
        }
        sb.append("\"");
        sb.append(" size=\"10\" maxlength=\"8\"");
        if (pastDisabled) {
            sb.append(" class=\"datepicker-past-disabled\"");
        } else {
            sb.append(" class=\"datepicker\"");
        }
        sb.append(" />");

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
        this.name = null;
        this.property = null;
        this.format = "YYYYMMDD";
        this.pastDisabled = false;
    }

    private String resolveValue() {
        if (name == null || property == null || property.isEmpty()) return null;
        // Look for a form bean in page/request/session/application scope
        Object bean = pageContext.findAttribute(name);
        if (bean == null) return null;

        try {
            String getter = "get" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
            Method m = bean.getClass().getMethod(getter);
            Object val = m.invoke(bean);
            return val != null ? val.toString() : null;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
