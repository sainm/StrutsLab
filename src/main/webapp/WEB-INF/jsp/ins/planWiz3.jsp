<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ page import="com.strutslab.util.HtmlUtil" %>
<%
    String errMsg = (String) request.getAttribute("errorMessage");
    String planDate = request.getParameter("planDate") != null ? request.getParameter("planDate") : "";
    String teamCode = request.getParameter("teamCode") != null ? request.getParameter("teamCode") : "";
    String personCode = request.getParameter("personCode") != null ? request.getParameter("personCode") : "";
    String note = request.getParameter("note") != null ? request.getParameter("note") : "";
%>
<% if (errMsg != null) { %>
    <div style="color:#c33;background:#fcc;padding:8px;margin-bottom:12px;border:1px solid #c33;"><%= HtmlUtil.escape(errMsg) %></div>
<% } %>

<!-- Step indicator -->
<table style="width:100%;border-collapse:collapse;margin-bottom:16px;">
    <tr style="text-align:center;">
        <td style="padding:8px;background:#4a90d9;color:#fff;font-weight:bold;">① 設備選択 ✓</td>
        <td style="padding:8px;background:#4a90d9;color:#fff;font-weight:bold;">② テンプレート選択 ✓</td>
        <td style="padding:8px;background:#4a90d9;color:#fff;font-weight:bold;">③ 日程設定</td>
        <td style="padding:8px;background:#ddd;color:#666;">④ 確認</td>
    </tr>
</table>

<html:form action="/ins/plan/wizard" method="post">
<html:hidden property="step" value="3"/>

<h2>日程設定</h2>
<table class="form-table">
    <tr>
        <th>予定日 <span style="color:#c33;">*</span></th>
        <td>
            <input type="text" name="planDate" value="<%= HtmlUtil.escape(planDate) %>" size="10" maxlength="8" placeholder="YYYYMMDD"/>
            <span style="font-size:0.9em;color:#666;margin-left:8px;">※ 西暦8桁 (例: 20260401)</span>
        </td>
    </tr>
    <tr>
        <th>担当班 <span style="color:#c33;">*</span></th>
        <td>
            <select name="teamCode">
                <option value="">-- 選択 --</option>
                <option value="A班" <%= "A班".equals(teamCode) ? "selected" : "" %>>A班</option>
                <option value="B班" <%= "B班".equals(teamCode) ? "selected" : "" %>>B班</option>
                <option value="C班" <%= "C班".equals(teamCode) ? "selected" : "" %>>C班</option>
            </select>
        </td>
    </tr>
    <tr>
        <th>担当者</th>
        <td>
            <select name="personCode">
                <option value="">-- 選択 --</option>
                <option value="EMP001" <%= "EMP001".equals(personCode) ? "selected" : "" %>>田中 太郎</option>
                <option value="EMP002" <%= "EMP002".equals(personCode) ? "selected" : "" %>>山田 花子</option>
                <option value="EMP003" <%= "EMP003".equals(personCode) ? "selected" : "" %>>佐藤 次郎</option>
            </select>
        </td>
    </tr>
    <tr>
        <th>備考</th>
        <td>
            <textarea name="note" rows="3" cols="50"><%= HtmlUtil.escape(note) %></textarea>
        </td>
    </tr>
</table>

<div class="button-area" style="text-align:center;margin-top:16px;">
    <input type="button" value="戻る" class="btn btn-secondary"
        onclick="location.href='<%= request.getContextPath() %>/ins/plan/wizard.do?step=step2';" style="margin-right:16px;"/>
    <html:submit property="step" value="確認画面へ" styleClass="btn btn-primary"/>
</div>
</html:form>
