<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ page import="java.util.List, java.util.Map" %>
<%
    String ctx = request.getContextPath();
    String year = (String) request.getAttribute("year");
    if (year == null) {
        year = new java.text.SimpleDateFormat("yyyy").format(new java.util.Date());
    }
    List<Map<String, Object>> months = (List<Map<String, Object>>) request.getAttribute("months");
    if (months == null) months = java.util.Collections.emptyList();

    // Color map for holiday types
    String getColor(String type) {
        if ("法定休日".equals(type)) return "#fcc";
        if ("会社指定休日".equals(type)) return "#cce";
        if ("点検停止".equals(type)) return "#ffa";
        if ("振替休日".equals(type)) return "#cfc";
        return "#fff";
    }
%>

<%
    String errMsg = (String) request.getAttribute("errorMessage");
    if (errMsg != null) {
%>
    <div style="color:#c33;background:#fcc;padding:8px;margin-bottom:12px;border:1px solid #c33;"><%= errMsg %></div>
<%
    }
    String successMsg = (String) request.getAttribute("successMessage");
    if (successMsg != null) {
%>
    <div style="color:#090;background:#dfd;padding:8px;margin-bottom:12px;border:1px solid:#090;"><%= successMsg %></div>
<%
    }
%>

<html:form action="/cal/list" method="post">
<div class="search-form">
<table class="form-table">
    <tr>
        <th>年度</th>
        <td>
            <select name="year">
                <%
                    int currentY = Integer.parseInt(new java.text.SimpleDateFormat("yyyy").format(new java.util.Date()));
                    for (int y = currentY - 2; y <= currentY + 2; y++) {
                %>
                    <option value="<%= y %>" <%= String.valueOf(y).equals(year) ? "selected" : "" %>><%= y %>年</option>
                <% } %>
            </select>
            <html:submit value="表示" styleClass="btn btn-primary"/>
        </td>
    </tr>
</table>
</div>
</html:form>

<p style="margin:8px 0;">
    <span style="background:#fcc;padding:2px 6px;">法定休日</span>&nbsp;
    <span style="background:#cce;padding:2px 6px;">会社指定休日</span>&nbsp;
    <span style="background:#ffa;padding:2px 6px;">点検停止</span>&nbsp;
    <span style="background:#cfc;padding:2px 6px;">振替休日</span>
</p>

<div style="display:flex;flex-wrap:wrap;gap:16px;">
<%
    String[] monthNames = {"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"};
    String[] dowNames = {"日","月","火","水","木","金","土"};

    for (Map<String, Object> month : months) {
        int m = (Integer) month.get("month");
        List<List<Map<String, Object>>> weeks = (List<List<Map<String, Object>>>) month.get("weeks");
%>
    <div style="border:1px solid #ccc;padding:8px;width:300px;">
        <h3 style="text-align:center;margin:4px 0;"><%= monthNames[m-1] %></h3>
        <table style="width:100%;border-collapse:collapse;font-size:12px;">
            <tr>
                <% for (int d = 0; d < 7; d++) { %>
                    <th style="border:1px solid #ddd;padding:2px;text-align:center;<%= d == 0 ? "color:red;" : (d == 6 ? "color:blue;" : "") %>"><%= dowNames[d] %></th>
                <% } %>
            </tr>
            <%
                for (List<Map<String, Object>> week : weeks) {
            %>
                <tr>
                    <% for (Map<String, Object> cell : week) { %>
                        <td style="border:1px solid #ddd;padding:2px;text-align:center;vertical-align:top;height:40px;
                            <% if (cell.get("holidayType") != null) {
                                String t = (String) cell.get("holidayType");
                                if ("法定休日".equals(t)) { %>background:#fcc;<% }
                                else if ("会社指定休日".equals(t)) { %>background:#cce;<% }
                                else if ("点検停止".equals(t)) { %>background:#ffa;<% }
                                else if ("振替休日".equals(t)) { %>background:#cfc;<% }
                            } else if (cell.get("day") == null) { %>background:#f5f5f5;<% } %>
                            <% if (cell.get("day") != null) { %>cursor:pointer;<% } %>"
                            <% if (cell.get("day") != null) {
                                String dateStr = (String) cell.get("dateStr");
                                String hid = cell.get("holidayId") != null ? String.valueOf(cell.get("holidayId")) : "";
                            %>
                                onclick="location.href='<%=ctx%>/cal/save.do?holidayId=<%= hid %>&dateStr=<%= dateStr %>'"
                            <% } %>>
                            <% if (cell.get("day") != null) { %>
                                <div style="font-weight:bold;"><%= cell.get("day") %></div>
                                <% if (cell.get("holidayName") != null) { %>
                                    <div style="font-size:9px;"><%= cell.get("holidayName") %></div>
                                <% } %>
                            <% } %>
                        </td>
                    <% } %>
                </tr>
            <% } %>
        </table>
    </div>
<%
    }
%>
</div>

<div style="text-align:center;margin-top:16px;">
    <input type="button" value="休日一括登録" class="btn btn-primary"
        onclick="location.href='<%=ctx%>/cal/save.do?method=new'"/>
</div>
