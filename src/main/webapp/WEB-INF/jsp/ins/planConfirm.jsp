<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ page import="com.strutslab.util.HtmlUtil" %>
<%
    String selEqpName = request.getParameter("selectedEqpName") != null ? request.getParameter("selectedEqpName") : "";
    String selTmplName = request.getParameter("selectedTmplName") != null ? request.getParameter("selectedTmplName") : "";
    String planDate = request.getParameter("planDate") != null ? request.getParameter("planDate") : "";
    String teamCode = request.getParameter("teamCode") != null ? request.getParameter("teamCode") : "";
    String personCode = request.getParameter("personCode") != null ? request.getParameter("personCode") : "";
    String note = request.getParameter("note") != null ? request.getParameter("note") : "";
    String ctx = request.getContextPath();
%>

<!-- Step indicator -->
<table style="width:100%;border-collapse:collapse;margin-bottom:16px;">
    <tr style="text-align:center;">
        <td style="padding:8px;background:#4a90d9;color:#fff;font-weight:bold;">① 設備選択 ✓</td>
        <td style="padding:8px;background:#4a90d9;color:#fff;font-weight:bold;">② テンプレート選択 ✓</td>
        <td style="padding:8px;background:#4a90d9;color:#fff;font-weight:bold;">③ 日程設定 ✓</td>
        <td style="padding:8px;background:#4a90d9;color:#fff;font-weight:bold;">④ 確認</td>
    </tr>
</table>

<h2>確認</h2>
<table class="form-table">
    <tr>
        <th>設備</th>
        <td><%= HtmlUtil.escape(selEqpName) %></td>
    </tr>
    <tr>
        <th>点検テンプレート</th>
        <td><%= HtmlUtil.escape(selTmplName) %></td>
    </tr>
    <tr>
        <th>予定日</th>
        <td><%= HtmlUtil.escape(planDate) %></td>
    </tr>
    <tr>
        <th>担当班</th>
        <td><%= HtmlUtil.escape(teamCode) %></td>
    </tr>
    <tr>
        <th>担当者</th>
        <td><%= HtmlUtil.escape(personCode) %></td>
    </tr>
    <tr>
        <th>備考</th>
        <td><%= HtmlUtil.escape(note.isEmpty() ? "&nbsp;" : note) %></td>
    </tr>
</table>

<div class="button-area" style="text-align:center;margin-top:16px;">
    <input type="button" value="戻る" class="btn btn-secondary"
        onclick="location.href='<%= ctx %>/ins/plan/wizard.do?step=step3';" style="margin-right:16px;"/>
    <input type="button" value="一時保存" class="btn btn-info"
        onclick="location.href='<%= ctx %>/ins/plan/wizard.do?step=tempSave';" style="margin-right:16px;"/>
    <input type="button" value="確定" class="btn btn-success"
        onclick="if(confirm('点検計画を登録しますか？')){location.href='<%= ctx %>/ins/plan/wizard.do?step=save';}"/>
</div>
