<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%
    String fiscalYear = request.getParameter("fiscalYear");
    String equipmentType = request.getParameter("equipmentType");
    String team = request.getParameter("team");
    if (fiscalYear == null) fiscalYear = "";
    if (equipmentType == null) equipmentType = "";
    if (team == null) team = "";
    String ctx = request.getContextPath();
%>
<html:form action="/ins/yearly/plan" method="post">
<div class="search-form">
<table class="form-table">
    <tr>
        <th>年度</th>
        <td>
            <select name="fiscalYear">
                <option value="">-- 選択 --</option>
                <option value="2025" <%= "2025".equals(fiscalYear) ? "selected" : "" %>>2025年度</option>
                <option value="2026" <%= "2026".equals(fiscalYear) ? "selected" : "" %>>2026年度</option>
                <option value="2027" <%= "2027".equals(fiscalYear) ? "selected" : "" %>>2027年度</option>
            </select>
        </td>
        <th>設備種別</th>
        <td>
            <select name="equipmentType">
                <option value="">-- 全て --</option>
                <option value="変圧器" <%= "変圧器".equals(equipmentType) ? "selected" : "" %>>変圧器</option>
                <option value="遮断器" <%= "遮断器".equals(equipmentType) ? "selected" : "" %>>遮断器</option>
                <option value="開閉器" <%= "開閉器".equals(equipmentType) ? "selected" : "" %>>開閉器</option>
                <option value="ケーブル" <%= "ケーブル".equals(equipmentType) ? "selected" : "" %>>ケーブル</option>
                <option value="母線" <%= "母線".equals(equipmentType) ? "selected" : "" %>>母線</option>
                <option value="保護継電器" <%= "保護継電器".equals(equipmentType) ? "selected" : "" %>>保護継電器</option>
                <option value="計器用変成器" <%= "計器用変成器".equals(equipmentType) ? "selected" : "" %>>計器用変成器</option>
            </select>
        </td>
        <th>担当班</th>
        <td>
            <select name="team">
                <option value="">-- 全て --</option>
                <option value="A班" <%= "A班".equals(team) ? "selected" : "" %>>A班</option>
                <option value="B班" <%= "B班".equals(team) ? "selected" : "" %>>B班</option>
                <option value="C班" <%= "C班".equals(team) ? "selected" : "" %>>C班</option>
            </select>
        </td>
    </tr>
</table>
<div class="button-area">
    <html:submit property="search" value="表示" styleClass="btn btn-primary"/>
    <html:submit property="clear" value="クリア" styleClass="btn btn-secondary"
        onclick="this.form.fiscalYear.value='';this.form.equipmentType.value='';this.form.team.value='';return true;"/>
</div>
</div>
</html:form>
