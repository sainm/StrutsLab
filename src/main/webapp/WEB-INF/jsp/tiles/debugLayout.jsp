<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.strutslab.util.HtmlUtil" %>
<p>DEBUG: title=[<%= request.getAttribute("tiles_title") %>]</p>
<p>DEBUG: formArea=[<%= request.getAttribute("tiles_formArea") %>]</p>
<%
    java.util.Enumeration<String> names = request.getAttributeNames();
    out.print("<p>All request attrs: ");
    while (names.hasMoreElements()) {
        String n = names.nextElement();
        out.print(n + "=[" + request.getAttribute(n) + "], ");
    }
    out.print("</p>");
%>
