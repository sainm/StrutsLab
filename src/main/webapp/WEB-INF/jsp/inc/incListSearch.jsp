<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ page import="com.strutslab.util.HtmlUtil" %>
<%
    String incDateFrom = request.getParameter("incDateFrom");
    String incDateTo = request.getParameter("incDateTo");
    String equipmentType = request.getParameter("equipmentType");
    String incidentType = request.getParameter("incidentType");
    String status = request.getParameter("status");
    String severity = request.getParameter("severity");
    String team = request.getParameter("team");
    String keyword = request.getParameter("keyword");
    if (incDateFrom == null) incDateFrom = "";
    if (incDateTo == null) incDateTo = "";
    if (equipmentType == null) equipmentType = "";
    if (incidentType == null) incidentType = "";
    if (status == null) status = "";
    if (severity == null) severity = "";
    if (team == null) team = "";
    if (keyword == null) keyword = "";
%>
<html:form action="/inc/list" method="post">
<div class="search-form">
<table class="form-table">
    <tr>
        <th>発生日（From）</th>
        <td><input type="text" name="incDateFrom" value="<%= HtmlUtil.escape(incDateFrom) %>" size="10" maxlength="10" placeholder="YYYY-MM-DD"/></td>
        <th>発生日（To）</th>
        <td><input type="text" name="incDateTo" value="<%= HtmlUtil.escape(incDateTo) %>" size="10" maxlength="10" placeholder="YYYY-MM-DD"/></td>
    </tr>
    <tr>
        <th>設備種別</th>
        <td>
            <select name="equipmentType">
                <option value="">-- 選択 --</option>
                <option value="変圧器" <%= "変圧器".equals(equipmentType) ? "selected" : "" %>>変圧器</option>
                <option value="遮断器" <%= "遮断器".equals(equipmentType) ? "selected" : "" %>>遮断器</option>
                <option value="開閉器" <%= "開閉器".equals(equipmentType) ? "selected" : "" %>>開閉器</option>
                <option value="ケーブル" <%= "ケーブル".equals(equipmentType) ? "selected" : "" %>>ケーブル</option>
                <option value="母線" <%= "母線".equals(equipmentType) ? "selected" : "" %>>母線</option>
                <option value="保護継電器" <%= "保護継電器".equals(equipmentType) ? "selected" : "" %>>保護継電器</option>
                <option value="計器用変成器" <%= "計器用変成器".equals(equipmentType) ? "selected" : "" %>>計器用変成器</option>
            </select>
        </td>
        <th>異常種別</th>
        <td>
            <select name="incidentType">
                <option value="">-- 選択 --</option>
                <option value="絶縁不良" <%= "絶縁不良".equals(incidentType) ? "selected" : "" %>>絶縁不良</option>
                <option value="過熱" <%= "過熱".equals(incidentType) ? "selected" : "" %>>過熱</option>
                <option value="振動異常" <%= "振動異常".equals(incidentType) ? "selected" : "" %>>振動異常</option>
                <option value="油漏れ" <%= "油漏れ".equals(incidentType) ? "selected" : "" %>>油漏れ</option>
                <option value="ガス発生" <%= "ガス発生".equals(incidentType) ? "selected" : "" %>>ガス発生</option>
                <option value="コロナ" <%= "コロナ".equals(incidentType) ? "selected" : "" %>>コロナ</option>
                <option value="その他" <%= "その他".equals(incidentType) ? "selected" : "" %>>その他</option>
            </select>
        </td>
    </tr>
    <tr>
        <th>ステータス</th>
        <td>
            <select name="status">
                <option value="">-- 選択 --</option>
                <option value="未了" <%= "未了".equals(status) ? "selected" : "" %>>未了</option>
                <option value="調査中" <%= "調査中".equals(status) ? "selected" : "" %>>調査中</option>
                <option value="対応中" <%= "対応中".equals(status) ? "selected" : "" %>>対応中</option>
                <option value="完了" <%= "完了".equals(status) ? "selected" : "" %>>完了</option>
                <option value="再発防止" <%= "再発防止".equals(status) ? "selected" : "" %>>再発防止</option>
                <option value="クローズ" <%= "クローズ".equals(status) ? "selected" : "" %>>クローズ</option>
            </select>
        </td>
        <th>重大度</th>
        <td>
            <select name="severity">
                <option value="">-- 選択 --</option>
                <option value="軽微" <%= "軽微".equals(severity) ? "selected" : "" %>>軽微</option>
                <option value="中" <%= "中".equals(severity) ? "selected" : "" %>>中</option>
                <option value="重大" <%= "重大".equals(severity) ? "selected" : "" %>>重大</option>
                <option value="緊急" <%= "緊急".equals(severity) ? "selected" : "" %>>緊急</option>
            </select>
        </td>
    </tr>
    <tr>
        <th>担当班</th>
        <td><input type="text" name="team" value="<%= HtmlUtil.escape(team) %>" size="20"/></td>
        <th>キーワード</th>
        <td><input type="text" name="keyword" value="<%= HtmlUtil.escape(keyword) %>" size="20"/></td>
    </tr>
</table>
<div class="button-area">
    <html:submit property="search" value="検索" styleClass="btn btn-primary"/>
    <html:submit property="clear" value="クリア" styleClass="btn btn-secondary"
        onclick="this.form.incDateFrom.value='';this.form.incDateTo.value='';this.form.equipmentType.value='';this.form.incidentType.value='';this.form.status.value='';this.form.severity.value='';this.form.team.value='';this.form.keyword.value='';return true;"/>
    <html:submit property="saveCondition" value="検索条件保存" styleClass="btn btn-secondary"/>
    <html:submit property="loadCondition" value="検索条件呼出" styleClass="btn btn-secondary"/>
</div>
<%
    String message = (String) request.getAttribute("message");
    if (message != null && !message.isEmpty()) {
%>
    <div class="info-message" style="margin-top:8px;color:green;"><%= HtmlUtil.escape(message) %></div>
<%
    }
%>
</div>
</html:form>
