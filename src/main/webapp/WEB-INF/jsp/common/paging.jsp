<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.strutslab.util.HtmlUtil" %>
<%
    Integer current = (Integer) request.getAttribute("currentPage");
    Integer total = (Integer) request.getAttribute("totalPages");
    String baseUrl = (String) request.getAttribute("pagingUrl");
    if (current == null) current = 1;
    if (total == null) total = 1;
    if (baseUrl == null) baseUrl = "";
%>
<div class="paging">
    <% if (current > 1) { %>
        <a href="<%= HtmlUtil.escape(baseUrl) %>&page=<%=current-1%>">前へ</a>
    <% } %>
    <% for (int i = 1; i <= total; i++) { %>
        <% if (i == current) { %>
            <span class="current"><%=i%></span>
        <% } else { %>
            <a href="<%= HtmlUtil.escape(baseUrl) %>&page=<%=i%>"><%=i%></a>
        <% } %>
    <% } %>
    <% if (current < total) { %>
        <a href="<%= HtmlUtil.escape(baseUrl) %>&page=<%=current+1%>">次へ</a>
    <% } %>
</div>
