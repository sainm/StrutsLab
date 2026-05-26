<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/app-common.tld" prefix="app" %>
<%@ page import="java.util.List,
                 com.strutslab.dto.ExecResultDto,
                 com.strutslab.dto.ExecItemResultDto,
                 com.strutslab.form.ins.ExecForm, com.strutslab.util.HtmlUtil" %>
<%
    ExecResultDto result = (ExecResultDto) request.getAttribute("execResult");
    List<ExecItemResultDto> items = (List<ExecItemResultDto>) request.getAttribute("execItems");
    if (items == null) items = java.util.Collections.emptyList();

    String statusDisplay = "";
    String statusClass = "";
    if (result != null) {
        String st = result.getApprovalStatus();
        if (st == null) st = "";
        statusDisplay = st;
        if ("承認済".equals(st)) statusClass = "badge-green";
        else if ("申請中".equals(st)) statusClass = "badge-yellow";
        else if ("差戻".equals(st)) statusClass = "badge-red";
        else statusClass = "badge-gray";
    }
%>
<html:form action="/ins/exec/detail" method="post">
<input type="hidden" name="resultId" value="<%= HtmlUtil.escape(result != null ? result.getResultId() : 0) %>"/>

<app:sectionHeader title="点検結果詳細" anchorId="execDetail"/>

<%
    if (result != null) {
%>
<div class="form-section">
<table class="form-table">
    <tr>
        <th>ステータス</th>
        <td><span class="badge <%= HtmlUtil.escape(statusClass) %>"><%= HtmlUtil.escape(statusDisplay) %></span></td>
        <th>設備名</th>
        <td><%= HtmlUtil.escape(result.getEquipmentName() != null ? result.getEquipmentName() : "") %></td>
    </tr>
    <tr>
        <th>設備コード</th>
        <td><%= HtmlUtil.escape(result.getEquipmentCode() != null ? result.getEquipmentCode() : "") %></td>
        <th>点検種別</th>
        <td><%= HtmlUtil.escape(result.getInspectionKind() != null ? result.getInspectionKind() : "") %></td>
    </tr>
    <tr>
        <th>点検日</th>
        <td><%= HtmlUtil.escape(result.getExecutedDate() != null ? result.getExecutedDate() : "") %></td>
        <th>点検者</th>
        <td><%= HtmlUtil.escape(result.getExecutedBy() != null ? result.getExecutedBy() : "") %></td>
    </tr>
    <tr>
        <th>総合判定</th>
        <td colspan="3"><%= HtmlUtil.escape(result.getSummaryJudge() != null ? result.getSummaryJudge() : "") %></td>
    </tr>
    <tr>
        <th>総合所見</th>
        <td colspan="3"><%= HtmlUtil.escape(result.getSummaryNote() != null ? result.getSummaryNote() : "") %></td>
    </tr>
    <tr>
        <th>次回推奨日</th>
        <td colspan="3"><%= HtmlUtil.escape(result.getNextRecommendedDate() != null ? result.getNextRecommendedDate() : "") %></td>
    </tr>
</table>
</div>

<app:sectionHeader title="点検項目結果" anchorId="itemResults"/>
<div class="form-section">
<table class="list-table">
    <thead>
        <tr>
            <th>項目名</th>
            <th>判定</th>
            <th>測定値</th>
            <th>備考</th>
        </tr>
    </thead>
    <tbody>
<%
        if (items.isEmpty()) {
%>
        <tr><td colspan="4" style="text-align:center;">点検項目結果はありません。</td></tr>
<%
        } else {
            for (ExecItemResultDto item : items) {
                String name = item.getItemName() != null ? item.getItemName() : "";
                String judge = item.getJudge() != null ? item.getJudge() : "";
                String val = item.getMeasuredValue() != null ? item.getMeasuredValue() : "";
                String note = item.getNote() != null ? item.getNote() : "";
%>
        <tr>
            <td><%= HtmlUtil.escape(name) %></td>
            <td><%= HtmlUtil.escape(judge) %></td>
            <td><%= HtmlUtil.escape(val) %></td>
            <td><%= HtmlUtil.escape(note) %></td>
        </tr>
<%
            }
        }
%>
    </tbody>
</table>
</div>

<app:sectionHeader title="修正申請" anchorId="modify"/>
<div class="form-section">
<%
        boolean canModify = !"承認済".equals(result.getApprovalStatus());
        if (canModify) {
%>
    <table class="form-table">
        <tr>
            <th>修正理由 <span class="required">*</span></th>
            <td>
                <textarea name="modifyReason" rows="3" cols="60"
                    placeholder="修正理由を入力してください。"></textarea>
            </td>
        </tr>
    </table>
    <div class="button-area">
        <input type="submit" name="modify" value="修正申請" class="btn btn-warning"
            onclick="return confirm('修正申請を送信してもよろしいですか？');"/>
    </div>
<%
        } else {
%>
    <p>承認済のため修正申請できません。</p>
<%
        }
%>
</div>

<%
    } else {
%>
    <p>結果データが見つかりません。</p>
<%
    }
%>

<div class="button-area">
    <input type="button" value="戻る" class="btn btn-secondary"
        onclick="history.back();"/>
</div>
</html:form>

<%
    // Display messages
    String msg = (String) request.getAttribute("org.apache.struts.action.ACTION_MESSAGE");
    if (msg != null) {
%>
<div class="info-messages"><%= HtmlUtil.escape(msg) %></div>
<%
    }
%>
