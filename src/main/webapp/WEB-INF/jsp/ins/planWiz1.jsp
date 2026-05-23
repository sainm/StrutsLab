<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ page import="java.util.List, com.strutslab.dto.EqpDto" %>
<%
    List<EqpDto> eqpList = (List<EqpDto>) request.getAttribute("eqpList");
    if (eqpList == null) eqpList = new java.util.ArrayList<EqpDto>();
    String selCode = request.getParameter("selectedEqpCode");
    if (selCode == null) selCode = "";
    String errMsg = (String) request.getAttribute("errorMessage");
%>
<% if (errMsg != null) { %>
    <div style="color:#c33;background:#fcc;padding:8px;margin-bottom:12px;border:1px solid #c33;"><%= errMsg %></div>
<% } %>

<!-- Step indicator -->
<table style="width:100%;border-collapse:collapse;margin-bottom:16px;">
    <tr style="text-align:center;">
        <td style="padding:8px;background:#4a90d9;color:#fff;font-weight:bold;">① 設備選択</td>
        <td style="padding:8px;background:#ddd;color:#666;">② テンプレート選択</td>
        <td style="padding:8px;background:#ddd;color:#666;">③ 日程設定</td>
        <td style="padding:8px;background:#ddd;color:#666;">④ 確認</td>
    </tr>
</table>

<html:form action="/ins/plan/wizard" method="post">
<html:hidden property="step" value="1"/>

<h2>設備選択</h2>
<table class="form-table">
    <tr>
        <th>設備 <span style="color:#c33;">*</span></th>
        <td>
            <select name="selectedEqpCode" style="width:300px;">
                <option value="">-- 選択 --</option>
<%
    for (EqpDto eqp : eqpList) {
        String code = eqp.getEquipmentCode() != null ? eqp.getEquipmentCode() : "";
        String name = eqp.getEquipmentName() != null ? eqp.getEquipmentName() : "";
        String type = eqp.getEquipmentType() != null ? eqp.getEquipmentType() : "";
%>
                <option value="<%= code %>" <%= code.equals(selCode) ? "selected" : "" %>><%= code %> - <%= name %> (<%= type %>)</option>
<%
    }
%>
            </select>
        </td>
    </tr>
    <tr>
        <th>設備情報</th>
        <td id="eqpInfo">
            <em>設備を選択すると情報が表示されます。</em>
        </td>
    </tr>
</table>

<div class="button-area" style="text-align:center;margin-top:16px;">
    <input type="button" value="キャンセル" class="btn btn-secondary"
        onclick="location.href='<%= request.getContextPath() %>/menu.do';" style="margin-right:16px;"/>
    <html:submit property="step" value="進む" styleClass="btn btn-primary"/>
</div>
</html:form>
