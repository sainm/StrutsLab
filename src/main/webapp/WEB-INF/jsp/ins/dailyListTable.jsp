<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/app-common.tld" prefix="app" %>
<%@ page import="java.util.List, com.strutslab.dto.ExecResultDto, com.strutslab.util.HtmlUtil" %>
<%
    List<ExecResultDto> list = (List<ExecResultDto>) request.getAttribute("dailyList");
    if (list == null) list = java.util.Collections.emptyList();
    String targetDate = (String) request.getAttribute("targetDate");
%>
<div class="list-table-container">
<table class="list-table">
    <thead>
        <tr>
            <th>設備名</th>
            <th>点検種別</th>
            <th>予定時刻</th>
            <th>ステータス</th>
            <th>担当者</th>
        </tr>
    </thead>
    <tbody>
<%
    if (list.isEmpty()) {
%>
        <tr><td colspan="5" style="text-align:center;">点検予定はありません。</td></tr>
<%
    } else {
        for (ExecResultDto dto : list) {
            String eqpName = dto.getEquipmentName() != null ? dto.getEquipmentName() : "";
            String inspKind = dto.getInspectionKind() != null ? dto.getInspectionKind() : "";
            String plannedTime = dto.getPlannedTime() != null ? dto.getPlannedTime() : "";
            String status = dto.getApprovalStatus() != null ? dto.getApprovalStatus() : "未了";
            String person = dto.getExecutedBy() != null ? dto.getExecutedBy() : "";
            int planId = dto.getPlanId();
            int resultId = dto.getResultId();
%>
        <tr>
            <td><a href="<%=request.getContextPath()%>/ins/exec/input.do?planId=<%= HtmlUtil.escape(planId) %><%= HtmlUtil.escape(resultId > 0 ? "&resultId=" + resultId : "") %>"><%= HtmlUtil.escape(eqpName) %></a></td>
            <td><%= HtmlUtil.escape(inspKind) %></td>
            <td><%= HtmlUtil.escape(plannedTime) %></td>
            <td><app:statusBadge status="<%= status %>"/></td>
            <td><%= HtmlUtil.escape(person) %></td>
        </tr>
<%
        }
    }
%>
    </tbody>
</table>
</div>
