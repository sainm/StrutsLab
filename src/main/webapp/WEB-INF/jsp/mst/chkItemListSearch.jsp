<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%
    String equipmentType = request.getParameter("equipmentType");
    String inspectionKind = request.getParameter("inspectionKind");
    if (equipmentType == null) equipmentType = "";
    if (inspectionKind == null) inspectionKind = "";
%>
<html:form action="/mst/chkItem/list" method="post">
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
        <th>点検種別</th>
        <td>
            <select name="inspectionKind">
                <option value="">-- 選択 --</option>
                <option value="日常" <%= "日常".equals(inspectionKind) ? "selected" : "" %>>日常点検</option>
                <option value="定期" <%= "定期".equals(inspectionKind) ? "selected" : "" %>>定期点検</option>
                <option value="精密" <%= "精密".equals(inspectionKind) ? "selected" : "" %>>精密点検</option>
            </select>
        </td>
    </tr>
</table>
<div class="button-area">
    <html:submit property="search" value="検索" styleClass="btn btn-primary"/>
    <html:submit property="clear" value="クリア" styleClass="btn btn-secondary"
        onclick="this.form.equipmentType.value='';this.form.inspectionKind.value='';return true;"/>
</div>
</div>
</html:form>
