<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.strutslab.util.HtmlUtil" %>
<h1><%= HtmlUtil.escape((String) request.getAttribute("tiles_title")) %></h1>
<%
    String _formArea = (String) request.getAttribute("tiles_formArea");
    if (_formArea != null && !_formArea.isEmpty()) {
        request.getRequestDispatcher(_formArea).include(request, response);
    }
%>
