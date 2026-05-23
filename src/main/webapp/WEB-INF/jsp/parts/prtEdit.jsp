<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%
    com.strutslab.form.parts.PartsForm partsForm = (com.strutslab.form.parts.PartsForm) request.getAttribute("partsForm");
    boolean isEdit = partsForm != null && partsForm.getPartCode() != null && !partsForm.getPartCode().isEmpty();
    String ctx = request.getContextPath();
%>
<%
    String errMsg = (String) request.getAttribute("errorMessage");
    if (errMsg != null) {
%>
    <div style="color:#c33;background:#fcc;padding:8px;margin-bottom:12px;border:1px solid #c33;">
        <%= errMsg %>
    </div>
<%
    }
%>
<html:form action="/parts/save" method="post" enctype="multipart/form-data">
<html:hidden property="method" value="save"/>
<% if (!isEdit) { %>
<html:hidden property="partCode"/>
<% } %>

<table class="form-table">
    <tr>
        <th>部品コード</th>
        <td>
            <% if (isEdit) { %>
                <html:text property="partCode" readonly="true" styleClass="readonly" style="width:120px;background:#eee;"/>
            <% } else { %>
                <span style="color:#999;">（自動採番）</span>
            <% } %>
        </td>
    </tr>
    <tr>
        <th>部品名 <span style="color:#c33;">*</span></th>
        <td><html:text property="partName" style="width:300px;"/></td>
    </tr>
    <tr>
        <th>部品種別</th>
        <td>
            <html:select property="partType">
                <html:option value="">-- 選択 --</html:option>
                <html:option value="ガスケット">ガスケット</html:option>
                <html:option value="ボルト">ボルト</html:option>
                <html:option value="絶縁油">絶縁油</html:option>
                <html:option value="ブッシング">ブッシング</html:option>
                <html:option value="その他">その他</html:option>
            </html:select>
        </td>
    </tr>
    <tr>
        <th>単位 <span style="color:#c33;">*</span></th>
        <td>
            <html:select property="unit">
                <html:option value="">-- 選択 --</html:option>
                <html:option value="個">個</html:option>
                <html:option value="式">式</html:option>
                <html:option value="L">L</html:option>
                <html:option value="kg">kg</html:option>
                <html:option value="m">m</html:option>
            </html:select>
        </td>
    </tr>
    <tr>
        <th>発注点</th>
        <td><html:text property="orderPoint" style="width:80px;"/></td>
    </tr>
    <tr>
        <th>安全在庫</th>
        <td><html:text property="safetyStock" style="width:80px;"/></td>
    </tr>
    <tr>
        <th>単価</th>
        <td><html:text property="unitPrice" style="width:100px;"/> 円</td>
    </tr>
    <tr>
        <th>仕入先</th>
        <td><html:text property="supplier" style="width:300px;"/></td>
    </tr>
    <tr>
        <th>備考</th>
        <td><html:textarea property="note" style="width:350px;height:60px;"/></td>
    </tr>
    <tr>
        <th>添付ファイル</th>
        <td><html:file property="attachFile" style="width:300px;"/></td>
    </tr>
    <tr>
        <th>該当設備</th>
        <td>
            <html:text property="applicableEquipmentCodes" style="width:200px;"/>
            <input type="button" value="設備選択" onclick="window.open('<%=ctx%>/mst/eqp/popup.do','eqpPopup','width=800,height=600,scrollbars=yes');"/>
            <br/><span style="font-size:11px;color:#666;">カンマ区切りで複数指定可、または設備選択ボタンから選択</span>
        </td>
    </tr>
</table>

<div style="text-align:center;margin-top:16px;">
    <html:submit value="保存" style="width:100px;"/>
    <input type="button" value="キャンセル" class="btn btn-back" style="width:100px;" onclick="history.back();"/>
</div>

</html:form>
