<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%
    String equipmentType = request.getParameter("equipmentType");
    String voltageLevel = request.getParameter("voltageLevel");
    String yearFrom = request.getParameter("yearFrom");
    String yearTo = request.getParameter("yearTo");
    String maintenanceRank = request.getParameter("maintenanceRank");
    String deptName = request.getParameter("deptName");
    if (equipmentType == null) equipmentType = "";
    if (voltageLevel == null) voltageLevel = "";
    if (yearFrom == null) yearFrom = "";
    if (yearTo == null) yearTo = "";
    if (maintenanceRank == null) maintenanceRank = "";
    if (deptName == null) deptName = "";
%>
<html:form action="/mst/eqp/list" method="post">
<div class="search-form">
<table class="form-table">
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
        <th>電圧階級</th>
        <td>
            <select name="voltageLevel">
                <option value="">-- 選択 --</option>
                <option value="66kV" <%= "66kV".equals(voltageLevel) ? "selected" : "" %>>66kV</option>
                <option value="154kV" <%= "154kV".equals(voltageLevel) ? "selected" : "" %>>154kV</option>
                <option value="275kV" <%= "275kV".equals(voltageLevel) ? "selected" : "" %>>275kV</option>
                <option value="500kV" <%= "500kV".equals(voltageLevel) ? "selected" : "" %>>500kV</option>
            </select>
        </td>
    </tr>
    <tr>
        <th>設置年</th>
        <td colspan="3">
            <input type="text" name="yearFrom" value="<%= yearFrom %>" size="6" maxlength="7" placeholder="YYYY-MM"> 〜
            <input type="text" name="yearTo" value="<%= yearTo %>" size="6" maxlength="7" placeholder="YYYY-MM">
        </td>
    </tr>
    <tr>
        <th>保全ランク</th>
        <td>
            <select name="maintenanceRank">
                <option value="">-- 選択 --</option>
                <option value="S" <%= "S".equals(maintenanceRank) ? "selected" : "" %>>S</option>
                <option value="A" <%= "A".equals(maintenanceRank) ? "selected" : "" %>>A</option>
                <option value="B" <%= "B".equals(maintenanceRank) ? "selected" : "" %>>B</option>
                <option value="C" <%= "C".equals(maintenanceRank) ? "selected" : "" %>>C</option>
            </select>
        </td>
        <th>担当部署</th>
        <td>
            <input type="text" name="deptName" value="<%= deptName %>" size="20">
        </td>
    </tr>
</table>
<div class="button-area">
    <html:submit property="search" value="検索" styleClass="btn btn-primary"/>
    <html:submit property="clear" value="クリア" styleClass="btn btn-secondary"
        onclick="this.form.equipmentType.value='';this.form.voltageLevel.value='';this.form.yearFrom.value='';this.form.yearTo.value='';this.form.maintenanceRank.value='';this.form.deptName.value='';return true;"/>
</div>
</div>
</html:form>
