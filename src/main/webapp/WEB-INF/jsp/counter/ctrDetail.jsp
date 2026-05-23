<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/app-common.tld" prefix="app" %>
<%@ page import="java.util.List, com.strutslab.dto.CounterDto, com.strutslab.dto.CounterDetailDto, com.strutslab.form.counter.CounterDetailForm" %>
<%
    CounterDto order = (CounterDto) request.getAttribute("order");
    List<CounterDetailDto> details = (List<CounterDetailDto>) request.getAttribute("details");
    if (details == null) details = java.util.Collections.emptyList();
    Integer completeIndex = (Integer) request.getAttribute("completeIndex");

    CounterDetailForm df = (CounterDetailForm) request.getAttribute("counterDetailForm");
    if (df == null) df = new CounterDetailForm();

    String orderNo = order != null ? order.getOrderNo() : "";
    String incidentNo = order != null && order.getIncidentNo() != null ? order.getIncidentNo() : "";
    String orderDate = order != null && order.getOrderDate() != null ? order.getOrderDate() : "";
    String issuer = order != null && order.getIssuer() != null ? order.getIssuer() : "";
    String deadline = order != null && order.getOverallDeadline() != null ? order.getOverallDeadline() : "";
    String priority = order != null && order.getOverallPriority() != null ? order.getOverallPriority() : "";
    String status = order != null && order.getStatus() != null ? order.getStatus() : "";
%>
<html:form action="/counter/detail" method="post">
<input type="hidden" name="method" value="saveCompletion"/>
<input type="hidden" name="orderNo" value="<%= orderNo %>"/>

<app:sectionHeader title="対応指示詳細" anchorId="ctrDetail"/>

<div class="form-section">
<table class="form-table">
    <tr>
        <th>指示番号</th>
        <td><strong><%= orderNo %></strong></td>
        <th>関連異常報告</th>
        <td>
            <% if (incidentNo != null && !incidentNo.isEmpty()) { %>
                <a href="<%=request.getContextPath()%>/inc/detail.do?incidentNo=<%= incidentNo %>"><%= incidentNo %></a>
            <% } %>
        </td>
    </tr>
    <tr>
        <th>指示日</th>
        <td><%= orderDate %></td>
        <th>指示者</th>
        <td><%= issuer %></td>
    </tr>
    <tr>
        <th>期限</th>
        <td><%= deadline %></td>
        <th>優先度</th>
        <td><%= priority %></td>
    </tr>
    <tr>
        <th>ステータス</th>
        <td colspan="3"><app:statusBadge status="<%= status %>"/></td>
    </tr>
</table>
</div>

<app:sectionHeader title="指示明細" anchorId="ctrDetailItems"/>
<div class="form-section">
<table class="list-table">
    <thead>
        <tr>
            <th>No.</th>
            <th>指示内容</th>
            <th>担当者</th>
            <th>期限</th>
            <th>優先度</th>
            <th>ステータス</th>
            <th>実作業時間</th>
            <th>使用部品</th>
            <th>使用数</th>
            <th>備考</th>
            <th>操作</th>
        </tr>
    </thead>
    <tbody>
<%
    if (details.isEmpty()) {
%>
        <tr><td colspan="11" style="text-align:center;">明細はありません。</td></tr>
<%
    } else {
        for (int i = 0; i < details.size(); i++) {
            CounterDetailDto d = details.get(i);
            int seqNo = d.getSeqNo();
            String wc = d.getWorkContent() != null ? d.getWorkContent() : "";
            String person = d.getPersonName() != null ? d.getPersonName() : "";
            String dl = d.getDeadline() != null ? d.getDeadline() : "";
            String pri = d.getPriority() != null ? d.getPriority() : "";
            String detStatus = d.getStatus() != null ? d.getStatus() : "";
            Double actH = d.getActualHours();
            String partCode = d.getUsedPartCode() != null ? d.getUsedPartCode() : "";
            Integer usedQty = d.getUsedQuantity();
            String note = d.getNote() != null ? d.getNote() : "";
            boolean isComplete = "完了".equals(detStatus);
%>
        <tr>
            <td style="text-align:center;"><%= seqNo %></td>
            <td><%= wc %></td>
            <td><%= person %></td>
            <td><%= dl %></td>
            <td><%= pri %></td>
            <td><app:statusBadge status="<%= detStatus %>"/></td>
            <td>
                <% if (isComplete) { %>
                    <%= actH != null ? actH : "" %>
                <% } else { %>
                    <input type="text" name="actualHours[<%= i %>]" value="<%= df.getActualHour(i) != null ? df.getActualHour(i) : "" %>" size="5" placeholder="時間"/>
                <% } %>
            </td>
            <td>
                <% if (isComplete) { %>
                    <%= partCode %>
                <% } else { %>
                    <input type="text" name="usedPartCodes[<%= i %>]" value="<%= df.getUsedPartCode(i) != null ? df.getUsedPartCode(i) : partCode %>" size="10"/>
                <% } %>
            </td>
            <td>
                <% if (isComplete) { %>
                    <%= usedQty != null ? usedQty : "" %>
                <% } else { %>
                    <input type="text" name="usedQuantities[<%= i %>]" value="<%= df.getUsedQuantity(i) != null ? df.getUsedQuantity(i) : "" %>" size="5" placeholder="数量"/>
                <% } %>
            </td>
            <td>
                <% if (isComplete) { %>
                    <%= note %>
                <% } else { %>
                    <input type="text" name="notes[<%= i %>]" value="<%= df.getNote(i) != null ? df.getNote(i) : note %>" size="10"/>
                <% } %>
            </td>
            <td style="text-align:center;">
                <% if (!isComplete) { %>
                    <input type="submit" name="method" value="completeDetail" class="btn btn-success btn-sm"
                        onclick="this.form.method.value='saveCompletion';this.form.detailIndex.value='<%= i %>';return confirm('この明細を完了してもよろしいですか？');"/>
                <% } else { %>
                    <span class="badge-green">完了済</span>
                <% } %>
            </td>
        </tr>
<%
        }
    }
%>
    </tbody>
</table>
</div>

<input type="hidden" name="detailIndex" value=""/>

<div class="button-area">
    <% if (!"完了".equals(status)) { %>
    <input type="submit" name="method" value="saveCompletion" class="btn btn-primary"
        onclick="this.form.method.value='saveCompletion';return confirm('変更を保存してもよろしいですか？');"/>
    <% } %>
    <input type="button" value="戻る" class="btn btn-secondary"
        onclick="location.href='<%=request.getContextPath()%>/counter/list.do';"/>
</div>
</html:form>

<%-- Auto-complete indicator --%>
<%
    boolean allComplete = true;
    for (CounterDetailDto d : details) {
        if (!"完了".equals(d.getStatus())) { allComplete = false; break; }
    }
    if (allComplete && details.size() > 0) {
%>
<script>
    alert('全明細が完了しました。対応指示のステータスが「完了」に更新されました。');
</script>
<%
    }
%>
