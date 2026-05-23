<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/app-common.tld" prefix="app" %>
<%@ page import="com.strutslab.dto.ExecResultDto,
                 com.strutslab.form.ins.ExecForm" %>
<%
    ExecResultDto planInfo = (ExecResultDto) request.getAttribute("planInfo");
    ExecForm ef = (ExecForm) request.getAttribute("execForm");
    if (ef == null) ef = (ExecForm) session.getAttribute("execForm");
    String summaryJudge = ef != null && ef.getSummaryJudge() != null ? ef.getSummaryJudge() : "";
    String summaryNote = ef != null && ef.getSummaryNote() != null ? ef.getSummaryNote() : "";
    String executedDate = ef != null && ef.getExecutedDate() != null ? ef.getExecutedDate() : "";
    int planId = (ef != null) ? ef.getPlanId() : 0;
    int resultId = (ef != null) ? ef.getResultId() : 0;

    String eqpName = planInfo != null ? planInfo.getEquipmentName() : "";
    String eqpCode = planInfo != null ? planInfo.getEquipmentCode() : "";
    String inspKind = planInfo != null ? planInfo.getInspectionKind() : "";
    String tmplName = planInfo != null ? planInfo.getTemplateName() : "";
    String personCode = planInfo != null ? planInfo.getPersonCode() : "";
%>
<html:form action="/ins/exec/input" method="post" enctype="multipart/form-data">
<input type="hidden" name="planId" value="<%= planId %>"/>
<input type="hidden" name="resultId" value="<%= resultId %>"/>

<app:sectionHeader title="点検設備情報" anchorId="equipmentInfo"/>
<div class="form-section">
<table class="form-table">
    <tr>
        <th>設備名</th>
        <td><%= eqpName %></td>
        <th>設備コード</th>
        <td><%= eqpCode %></td>
    </tr>
    <tr>
        <th>点検種別</th>
        <td><%= inspKind %></td>
        <th>点検テンプレート</th>
        <td><%= tmplName %></td>
    </tr>
    <tr>
        <th>担当者</th>
        <td><%= personCode %></td>
        <th>点検日</th>
        <td><input type="text" name="executedDate" value="<%= executedDate %>" size="10" maxlength="8"/></td>
    </tr>
</table>
</div>

<app:sectionHeader title="点検項目" anchorId="checklist"/>
<div class="form-section">
    <!-- Nested checklist rendered by custom tag -->
    <app:inspectionChecklist/>
</div>

<app:sectionHeader title="総合判定" anchorId="summary"/>
<div class="form-section">
<table class="form-table">
    <tr>
        <th>総合判定</th>
        <td>
            <label><input type="radio" name="summaryJudge" value="NORMAL"
                <%= "NORMAL".equals(summaryJudge) ? "checked" : "" %>> 正常</label>
            <label><input type="radio" name="summaryJudge" value="ABNORMAL"
                <%= "ABNORMAL".equals(summaryJudge) ? "checked" : "" %>> 異常あり</label>
            <label><input type="radio" name="summaryJudge" value="WATCH"
                <%= "WATCH".equals(summaryJudge) ? "checked" : "" %>> 要観察</label>
        </td>
    </tr>
    <tr>
        <th>総合所見</th>
        <td><textarea name="summaryNote" rows="4" cols="60"><%= summaryNote %></textarea></td>
    </tr>
</table>
</div>

<div class="button-area">
    <input type="submit" name="save" value="保存" class="btn btn-primary"/>
    <input type="button" value="戻る" class="btn btn-secondary"
        onclick="location.href='<%=request.getContextPath()%>/ins/daily.do'"/>
<%
    if ("ABNORMAL".equals(summaryJudge)) {
%>
    <input type="button" value="異常報告へ" class="btn btn-danger"
        onclick="location.href='<%=request.getContextPath()%>/inc/create.do?planId=<%= planId %>'"/>
<%
    }
%>
</div>

</html:form>

<%
    // Display validation errors if any
    if (request.getAttribute("org.apache.struts.action.ERROR") != null) {
%>
<div class="error-messages">
    <html:errors/>
</div>
<%
    }
%>
