<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%
    String dateFrom = request.getParameter("dateFrom");
    String dateTo = request.getParameter("dateTo");
    String equipmentType = request.getParameter("equipmentType");
    String partCode = request.getParameter("partCode");
    if (dateFrom == null) dateFrom = "";
    if (dateTo == null) dateTo = "";
    if (equipmentType == null) equipmentType = "";
    if (partCode == null) partCode = "";
%>
<html:form action="/parts/usage" method="post">
<div class="search-form">
<table class="form-table">
    <tr>
        <th>期間</th>
        <td>
            <input type="text" name="dateFrom" value="<%= dateFrom %>" size="8" maxlength="8" placeholder="YYYYMMDD"> 〜
            <input type="text" name="dateTo" value="<%= dateTo %>" size="8" maxlength="8" placeholder="YYYYMMDD">
        </td>
    </tr>
    <tr>
        <th>設備種別</th>
        <td>
            <select name="equipmentType">
                <option value="">-- 全て --</option>
                <option value="変圧器" <%= "変圧器".equals(equipmentType) ? "selected" : "" %>>変圧器</option>
                <option value="遮断器" <%= "遮断器".equals(equipmentType) ? "selected" : "" %>>遮断器</option>
                <option value="開閉器" <%= "開閉器".equals(equipmentType) ? "selected" : "" %>>開閉器</option>
                <option value="ケーブル" <%= "ケーブル".equals(equipmentType) ? "selected" : "" %>>ケーブル</option>
                <option value="保護継電器" <%= "保護継電器".equals(equipmentType) ? "selected" : "" %>>保護継電器</option>
            </select>
        </td>
        <th>部品コード</th>
        <td><input type="text" name="partCode" value="<%= partCode %>" size="15"></td>
    </tr>
</table>
<div class="button-area">
    <html:submit value="検索" styleClass="btn btn-primary"/>
    <html:submit property="clear" value="クリア" styleClass="btn btn-secondary"
        onclick="this.form.dateFrom.value='';this.form.dateTo.value='';this.form.equipmentType.value='';this.form.partCode.value='';return true;"/>
</div>
</div>
</html:form>
