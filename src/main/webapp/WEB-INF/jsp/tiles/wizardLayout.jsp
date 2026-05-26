<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.strutslab.util.HtmlUtil" %>
<h1><%= HtmlUtil.escape((String) request.getAttribute("tiles_title")) %></h1>
<%
    String _wizContent = (String) request.getAttribute("tiles_wizardContent");
    if (_wizContent != null && !_wizContent.isEmpty()) {
        request.getRequestDispatcher(_wizContent).include(request, response);
    }
%>
