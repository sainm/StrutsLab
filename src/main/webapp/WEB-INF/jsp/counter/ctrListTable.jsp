<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/app-common.tld" prefix="app" %>
<%@ page import="java.util.List, java.util.Map, com.strutslab.dto.CounterDto" %>
<%
    List<CounterDto> orderList = (List<CounterDto>) request.getAttribute("orderList");
    if (orderList == null) orderList = java.util.Collections.emptyList();
    Integer totalCount = (Integer) request.getAttribute("totalCount");
    if (totalCount == null) totalCount = 0;
    Map<String, Integer> detailCounts = (Map<String, Integer>) request.getAttribute("detailCounts");
    if (detailCounts == null) detailCounts = new java.util.HashMap<>();
    Map<String, Integer> completeCounts = (Map<String, Integer>) request.getAttribute("completeCounts");
    if (completeCounts == null) completeCounts = new java.util.HashMap<>();
%>
<div class="list-table-container">
<form name="counterListForm" method="post" action="<%=request.getContextPath()%>/counter/list.do">
<table class="list-table">
    <thead>
        <tr>
            <th><input type="checkbox" onclick="toggleAll(this)"/></th>
            <th>指示番号</th>
            <th>指示日</th>
            <th>関連異常報告</th>
            <th>優先度</th>
            <th>明細数</th>
            <th>完了/全明細</th>
            <th>ステータス</th>
        </tr>
    </thead>
    <tbody>
<%
    if (orderList.isEmpty()) {
%>
        <tr><td colspan="8" style="text-align:center;">該当する対応指示はありません。</td></tr>
<%
    } else {
        for (CounterDto dto : orderList) {
            String orderNoStr = dto.getOrderNo() != null ? dto.getOrderNo() : "";
            String orderDateStr = dto.getOrderDate() != null ? dto.getOrderDate() : "";
            String incidentNoStr = dto.getIncidentNo() != null ? dto.getIncidentNo() : "";
            String priorityStr = dto.getOverallPriority() != null ? dto.getOverallPriority() : "";
            String statusStr = dto.getStatus() != null ? dto.getStatus() : "";

            int total = detailCounts.containsKey(orderNoStr) ? detailCounts.get(orderNoStr) : 0;
            int complete = completeCounts.containsKey(orderNoStr) ? completeCounts.get(orderNoStr) : 0;
%>
        <tr>
            <td><input type="checkbox" name="selectedItems" value="<%= orderNoStr %>"/></td>
            <td><a href="<%=request.getContextPath()%>/counter/detail.do?orderNo=<%= orderNoStr %>"><%= orderNoStr %></a></td>
            <td><%= orderDateStr %></td>
            <td>
                <% if (incidentNoStr != null && !incidentNoStr.isEmpty()) { %>
                    <a href="<%=request.getContextPath()%>/inc/detail.do?incidentNo=<%= incidentNoStr %>"><%= incidentNoStr %></a>
                <% } %>
            </td>
            <td><%= priorityStr %></td>
            <td style="text-align:center;"><%= total %></td>
            <td style="text-align:center;"><%= complete %>/<%= total %></td>
            <td><app:statusBadge status="<%= statusStr %>"/></td>
        </tr>
<%
        }
    }
%>
    </tbody>
</table>

<div class="info-messages">全<%= totalCount %>件</div>

<div class="button-area">
    <input type="submit" name="bulkUpdateStatus" value="一括ステータス更新" class="btn btn-warning"
        onclick="return confirm('選択した項目のステータスを更新してもよろしいですか？');"/>
    <input type="hidden" name="newStatus" value=""/>
    <input type="button" value="印刷用表示" class="btn btn-secondary"
        onclick="window.open('<%=request.getContextPath()%>/counter/print.do','printWindow','width=800,height=600');"/>
    <input type="submit" name="csv" value="CSV出力" class="btn btn-secondary"/>
</div>
</form>
</div>

<script>
function toggleAll(src) {
    var checkboxes = document.getElementsByName('selectedItems');
    for (var i = 0; i < checkboxes.length; i++) {
        checkboxes[i].checked = src.checked;
    }
}
</script>
