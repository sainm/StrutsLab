<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ page import="java.util.List, java.util.Map" %>
<%
    String ctx = request.getContextPath();
    List<Map<String, Object>> completionMatrix = (List<Map<String, Object>>) request.getAttribute("completionMatrix");
    List<Map<String, Object>> crossTab = (List<Map<String, Object>>) request.getAttribute("crossTab");
    List<Map<String, Object>> ranking = (List<Map<String, Object>>) request.getAttribute("ranking");
    if (completionMatrix == null) completionMatrix = java.util.Collections.emptyList();
    if (crossTab == null) crossTab = java.util.Collections.emptyList();
    if (ranking == null) ranking = java.util.Collections.emptyList();
%>
<%
    com.strutslab.form.report.ReportForm reportForm = (com.strutslab.form.report.ReportForm) request.getAttribute("reportForm");
    String dateFrom = reportForm != null && reportForm.getDateFrom() != null ? reportForm.getDateFrom() : "";
    String dateTo = reportForm != null && reportForm.getDateTo() != null ? reportForm.getDateTo() : "";
    String eqType = reportForm != null && reportForm.getEquipmentType() != null ? reportForm.getEquipmentType() : "";
    String team = reportForm != null && reportForm.getTeam() != null ? reportForm.getTeam() : "";
%>

<html:form action="/report/summary" method="post">
<div class="search-form">
<table class="form-table">
    <tr>
        <th>対象期間</th>
        <td>
            <input type="text" name="dateFrom" value="<%= dateFrom %>" size="6" maxlength="6" placeholder="YYYYMM"> 〜
            <input type="text" name="dateTo" value="<%= dateTo %>" size="6" maxlength="6" placeholder="YYYYMM">
        </td>
        <th>設備種別</th>
        <td>
            <select name="equipmentType">
                <option value="">-- 全て --</option>
                <option value="変圧器" <%= "変圧器".equals(eqType) ? "selected" : "" %>>変圧器</option>
                <option value="遮断器" <%= "遮断器".equals(eqType) ? "selected" : "" %>>遮断器</option>
                <option value="開閉器" <%= "開閉器".equals(eqType) ? "selected" : "" %>>開閉器</option>
                <option value="ケーブル" <%= "ケーブル".equals(eqType) ? "selected" : "" %>>ケーブル</option>
            </select>
        </td>
    </tr>
    <tr>
        <th>担当チーム</th>
        <td>
            <input type="text" name="team" value="<%= team %>" size="20">
        </td>
        <td colspan="2"></td>
    </tr>
</table>
<div class="button-area">
    <html:submit value="表示" styleClass="btn btn-primary"/>
</div>
</div>
</html:form>

<!-- Section 1: 実施率推移 -->
<h2 style="margin-top:24px;">点検実施率推移</h2>
<%
    if (completionMatrix.isEmpty()) {
%>
    <p>データがありません。</p>
<%
    } else {
        // Group by equipment type and month
        java.util.Map<String, java.util.Map<String, Map<String, Object>>> matrix = new java.util.LinkedHashMap<>();
        java.util.Set<String> months = new java.util.LinkedHashSet<>();
        for (Map<String, Object> row : completionMatrix) {
            String eqpType = (String) row.get("equipmentType");
            String month = (String) row.get("month");
            if (!matrix.containsKey(eqpType)) matrix.put(eqpType, new java.util.LinkedHashMap<String, Map<String, Object>>());
            matrix.get(eqpType).put(month, row);
            months.add(month);
        }
%>
<table class="list-table" style="width:auto;">
    <thead>
        <tr>
            <th>設備種別</th>
            <% for (String m : months) { %>
                <th style="text-align:center;"><%= m.length() >= 6 ? m.substring(4) : m %></th>
            <% } %>
        </tr>
    </thead>
    <tbody>
    <%
        for (java.util.Map.Entry<String, java.util.Map<String, Map<String, Object>>> entry : matrix.entrySet()) {
    %>
        <tr>
            <td><%= entry.getKey() %></td>
            <%
                for (String m : months) {
                    Map<String, Object> cell = entry.getValue().get(m);
                    String rate = cell != null ? String.valueOf(cell.get("completionRate")) : "-";
                    double rateVal = 0;
                    try { rateVal = Double.parseDouble(rate); } catch (Exception e) {}
            %>
                <td style="text-align:right;<%= rateVal < 95.0 ? "color:red;font-weight:bold;" : "" %>"><%= rate %>%</td>
            <% } %>
        </tr>
    <% } %>
    </tbody>
</table>
<%
    }
%>

<!-- Section 2: 異常発生傾向 -->
<h2 style="margin-top:24px;">異常発生傾向</h2>
<%
    if (crossTab.isEmpty()) {
%>
    <p>データがありません。</p>
<%
    } else {
        // Cross-tab: month x type
        java.util.Map<String, java.util.Map<String, Object>> crossMap = new java.util.LinkedHashMap<>();
        java.util.Set<String> crossMonths = new java.util.LinkedHashSet<>();
        java.util.Set<String> crossTypes = new java.util.LinkedHashSet<>();
        for (Map<String, Object> row : crossTab) {
            String month = (String) row.get("month");
            String type = (String) row.get("incidentType");
            if (!crossMap.containsKey(month)) crossMap.put(month, new java.util.LinkedHashMap<String, Object>());
            crossMap.get(month).put(type, row.get("count"));
            crossMonths.add(month);
            crossTypes.add(type);
        }
%>
<table class="list-table" style="width:auto;">
    <thead>
        <tr>
            <th>月</th>
            <% for (String t : crossTypes) { %>
                <th style="text-align:center;"><%= t %></th>
            <% } %>
        </tr>
    </thead>
    <tbody>
    <% for (String m : crossMonths) { %>
        <tr>
            <td><%= m %></td>
            <% for (String t : crossTypes) { %>
                <td style="text-align:right;"><%= crossMap.get(m).getOrDefault(t, "") %></td>
            <% } %>
        </tr>
    <% } %>
    </tbody>
</table>
<%
    }
%>

<!-- Section 3: 設備別ランキング -->
<h2 style="margin-top:24px;">設備別異常発生ランキング（Top 10）</h2>
<%
    if (ranking.isEmpty()) {
%>
    <p>データがありません。</p>
<%
    } else {
%>
<table class="list-table" style="width:auto;">
    <thead>
        <tr>
            <th>順位</th>
            <th>設備コード</th>
            <th>設備名</th>
            <th>設備種別</th>
            <th>異常件数</th>
        </tr>
    </thead>
    <tbody>
    <%
        int rank = 1;
        for (Map<String, Object> row : ranking) {
    %>
        <tr>
            <td style="text-align:center;"><%= rank++ %></td>
            <td><%= row.get("equipmentCode") %></td>
            <td><%= row.get("equipmentName") %></td>
            <td><%= row.get("equipmentType") %></td>
            <td style="text-align:right;font-weight:bold;"><%= row.get("incidentCount") %></td>
        </tr>
    <% } %>
    </tbody>
</table>
<%
    }
%>

<div style="text-align:center;margin-top:16px;">
    <input type="button" value="CSV出力" class="btn btn-primary"
        onclick="location.href='<%=ctx%>/report/summary.do?csv=true&dateFrom=<%= dateFrom %>&dateTo=<%= dateTo %>'"/>
    <input type="button" value="印刷用表示" class="btn btn-secondary"
        onclick="window.open('<%=ctx%>/report/print.do?...','printWin','width=900,height=700');"/>
</div>
