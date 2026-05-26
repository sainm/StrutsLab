<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.strutslab.util.HtmlUtil" %>
<h1><%= HtmlUtil.escape((String) request.getAttribute("tiles_title")) %></h1>
<%
    String _searchArea = (String) request.getAttribute("tiles_searchArea");
    if (_searchArea != null && !_searchArea.isEmpty()) {
        request.getRequestDispatcher(_searchArea).include(request, response);
    }
    String _listArea = (String) request.getAttribute("tiles_listArea");
    if (_listArea != null && !_listArea.isEmpty()) {
        request.getRequestDispatcher(_listArea).include(request, response);
    }
%>
