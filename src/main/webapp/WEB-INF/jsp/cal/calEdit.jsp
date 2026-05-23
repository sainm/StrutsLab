<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%
    String ctx = request.getContextPath();
%>
<%
    String errMsg = (String) request.getAttribute("errorMessage");
    if (errMsg != null) {
%>
    <div style="color:#c33;background:#fcc;padding:8px;margin-bottom:12px;border:1px solid #c33;"><%= errMsg %></div>
<%
    }
%>

<h2>休日一括登録</h2>
<html:form action="/cal/save" method="post">
<html:hidden property="method" value="bulkRegister"/>
<table class="form-table">
    <tr>
        <th>日付範囲</th>
        <td>
            <html:text property="dateFrom" style="width:100px;"/> YYYYMMDD 〜
            <html:text property="dateTo" style="width:100px;"/> YYYYMMDD
        </td>
    </tr>
    <tr>
        <th>休日種別 <span style="color:#c33;">*</span></th>
        <td>
            <html:select property="holidayType">
                <html:option value="">-- 選択 --</html:option>
                <html:option value="法定休日">法定休日</html:option>
                <html:option value="会社指定休日">会社指定休日</html:option>
                <html:option value="点検停止">点検停止</html:option>
            </html:select>
        </td>
    </tr>
    <tr>
        <th>休日名称</th>
        <td><html:text property="holidayName" style="width:200px;"/></td>
    </tr>
</table>
<div style="text-align:center;margin-top:8px;">
    <html:submit value="一括登録" styleClass="btn btn-primary" style="width:120px;"/>
</div>
</html:form>

<hr/>

<h2>振替設定</h2>
<html:form action="/cal/save" method="post">
<html:hidden property="method" value="transferSetting"/>
<table class="form-table">
    <tr>
        <th>振替元</th>
        <td><html:text property="transferFrom" style="width:100px;"/> YYYYMMDD</td>
    </tr>
    <tr>
        <th>振替先</th>
        <td><html:text property="transferTo" style="width:100px;"/> YYYYMMDD</td>
    </tr>
</table>
<div style="text-align:center;margin-top:8px;">
    <html:submit value="振替設定" styleClass="btn btn-warning" style="width:120px;"/>
</div>
</html:form>

<hr/>

<div style="text-align:center;margin-top:16px;">
    <input type="button" value="戻る" class="btn btn-back" style="width:100px;" onclick="location.href='<%=ctx%>/cal/list.do'"/>
</div>
