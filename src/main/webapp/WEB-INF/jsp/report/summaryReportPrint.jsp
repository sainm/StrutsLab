<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, java.util.Map, com.strutslab.util.HtmlUtil" %>
<%
    String ctx = request.getContextPath();
    List<Map<String, Object>> completionMatrix = (List<Map<String, Object>>) request.getAttribute("completionMatrix");
    List<Map<String, Object>> crossTab = (List<Map<String, Object>>) request.getAttribute("crossTab");
    List<Map<String, Object>> ranking = (List<Map<String, Object>>) request.getAttribute("ranking");
    if (completionMatrix == null) completionMatrix = java.util.Collections.emptyList();
    if (crossTab == null) crossTab = java.util.Collections.emptyList();
    if (ranking == null) ranking = java.util.Collections.emptyList();

    com.strutslab.form.report.ReportForm reportForm = (com.strutslab.form.report.ReportForm) request.getAttribute("reportForm");
    String dateFrom = reportForm != null && reportForm.getDateFrom() != null ? reportForm.getDateFrom() : "";
    String dateTo = reportForm != null && reportForm.getDateTo() != null ? reportForm.getDateTo() : "";
%>

<div style="padding:16px;">

<div class="no-print" style="text-align:right;margin-bottom:12px;">
    <input type="button" value="印刷" class="btn btn-primary" onclick="window.print();"/>
    <input type="button" value="閉じる" class="btn btn-secondary" onclick="window.close();"/>
</div>

<h1 style="text-align:center;margin-bottom:4px;">総合レポート</h1>
<p style="text-align:center;color:#666;margin-bottom:20px;"><%= HtmlUtil.escape(dateFrom) %> 〜 <%= HtmlUtil.escape(dateTo) %></p>

<!-- Section 1: 実施率推移 -->
<h2>点検実施率推移</h2>
<%
    if (completionMatrix.isEmpty()) {
%>
    <p>データがありません。</p>
<%
    } else {
        java.util.Map<String, java.util.Map<String, Map<String, Object>>> matrix = new java.util.LinkedHashMap<String, java.util.Map<String, Map<String, Object>>>();
        java.util.Set<String> months = new java.util.LinkedHashSet<String>();
        for (Map<String, Object> row : completionMatrix) {
            String eqpType = (String) row.get("equipmentType");
            String month = (String) row.get("month");
            if (month == null) continue;
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
                <th style="text-align:center;"><%= HtmlUtil.escape(m.length() >= 6 ? m.substring(4) : m) %></th>
            <% } %>
        </tr>
    </thead>
    <tbody>
    <%
        for (java.util.Map.Entry<String, java.util.Map<String, Map<String, Object>>> entry : matrix.entrySet()) {
    %>
        <tr>
            <td><%= HtmlUtil.escape(entry.getKey()) %></td>
            <%
                for (String m : months) {
                    Map<String, Object> cell = entry.getValue().get(m);
                    String rate = cell != null ? String.valueOf(cell.get("completionRate")) : "-";
                    double rateVal = 0;
                    try { rateVal = Double.parseDouble(rate); } catch (Exception e) {}
            %>
                <td style="text-align:right;<%= HtmlUtil.escape(rateVal < 95.0 ? "color:red;font-weight:bold;" : "") %>"><%= HtmlUtil.escape(rate) %>%</td>
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
        java.util.Map<String, java.util.Map<String, Object>> crossMap = new java.util.LinkedHashMap<String, java.util.Map<String, Object>>();
        java.util.Set<String> crossMonths = new java.util.LinkedHashSet<String>();
        java.util.Set<String> crossTypes = new java.util.LinkedHashSet<String>();
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
                <th style="text-align:center;"><%= HtmlUtil.escape(t) %></th>
            <% } %>
        </tr>
    </thead>
    <tbody>
    <% for (String m : crossMonths) { %>
        <tr>
            <td><%= HtmlUtil.escape(m) %></td>
            <% for (String t : crossTypes) { %>
                <td style="text-align:right;"><%= HtmlUtil.escape(String.valueOf(crossMap.get(m).getOrDefault(t, ""))) %></td>
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
            <td style="text-align:center;"><%= HtmlUtil.escape(String.valueOf(rank++)) %></td>
            <td><%= HtmlUtil.escape(String.valueOf(row.get("equipmentCode"))) %></td>
            <td><%= HtmlUtil.escape(String.valueOf(row.get("equipmentName"))) %></td>
            <td><%= HtmlUtil.escape(String.valueOf(row.get("equipmentType"))) %></td>
            <td style="text-align:right;font-weight:bold;"><%= HtmlUtil.escape(String.valueOf(row.get("incidentCount"))) %></td>
        </tr>
    <% } %>
    </tbody>
</table>
<%
    }
%>

</div>
