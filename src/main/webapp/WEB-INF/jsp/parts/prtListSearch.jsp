<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%
    String equipmentType = request.getParameter("equipmentType");
    String partType = request.getParameter("partType");
    String stockStatus = request.getParameter("stockStatus");
    String keyword = request.getParameter("keyword");
    if (equipmentType == null) equipmentType = "";
    if (partType == null) partType = "";
    if (stockStatus == null) stockStatus = "";
    if (keyword == null) keyword = "";
%>
<html:form action="/parts/list" method="post">
<div class="search-form">
<table class="form-table">
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
        <th>部品種別</th>
        <td>
            <select name="partType">
                <option value="">-- 全て --</option>
                <option value="ガスケット" <%= "ガスケット".equals(partType) ? "selected" : "" %>>ガスケット</option>
                <option value="ボルト" <%= "ボルト".equals(partType) ? "selected" : "" %>>ボルト</option>
                <option value="絶縁油" <%= "絶縁油".equals(partType) ? "selected" : "" %>>絶縁油</option>
                <option value="ブッシング" <%= "ブッシング".equals(partType) ? "selected" : "" %>>ブッシング</option>
                <option value="その他" <%= "その他".equals(partType) ? "selected" : "" %>>その他</option>
            </select>
        </td>
    </tr>
    <tr>
        <th>在庫状態</th>
        <td>
            <select name="stockStatus">
                <option value="">-- 全て --</option>
                <option value="out" <%= "out".equals(stockStatus) ? "selected" : "" %>>在庫切れ</option>
                <option value="low" <%= "low".equals(stockStatus) ? "selected" : "" %>>発注点以下</option>
                <option value="ok" <%= "ok".equals(stockStatus) ? "selected" : "" %>>十分</option>
            </select>
        </td>
        <th>キーワード</th>
        <td><input type="text" name="keyword" value="<%= keyword %>" size="20" placeholder="部品コード/部品名"></td>
    </tr>
</table>
<div class="button-area">
    <html:submit property="search" value="検索" styleClass="btn btn-primary"/>
    <html:submit property="clear" value="クリア" styleClass="btn btn-secondary"
        onclick="this.form.equipmentType.value='';this.form.partType.value='';this.form.stockStatus.value='';this.form.keyword.value='';return true;"/>
</div>
</div>
</html:form>
