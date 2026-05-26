<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ page import="java.util.List, com.strutslab.dto.EqpDto, com.strutslab.util.HtmlUtil" %>
<%
    List<EqpDto> eqpList = (List<EqpDto>) request.getAttribute("eqpList");
    if (eqpList == null) eqpList = new java.util.ArrayList<EqpDto>();
    String selCode = request.getParameter("selectedEqpCode");
    if (selCode == null) selCode = "";
    String errMsg = (String) request.getAttribute("errorMessage");
%>
<% if (errMsg != null) { %>
    <div style="color:#c33;background:#fcc;padding:8px;margin-bottom:12px;border:1px solid #c33;"><%= HtmlUtil.escape(errMsg) %></div>
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

<script>
var eqpData = {
<%
    for (int i = 0; i < eqpList.size(); i++) {
        EqpDto eqp = eqpList.get(i);
%>
    "<%= HtmlUtil.escape(eqp.getEquipmentCode()) %>": {
        name: "<%= HtmlUtil.escape(eqp.getEquipmentName() != null ? eqp.getEquipmentName() : "") %>",
        type: "<%= HtmlUtil.escape(eqp.getEquipmentType() != null ? eqp.getEquipmentType() : "") %>",
        voltage: "<%= HtmlUtil.escape(eqp.getVoltageLevel() != null ? eqp.getVoltageLevel() : "") %>",
        capacity: "<%= HtmlUtil.escape(eqp.getRatedCapacity() != null ? eqp.getRatedCapacity().toString() : "") %>",
        current: "<%= HtmlUtil.escape(eqp.getRatedCurrent() != null ? eqp.getRatedCurrent().toString() : "") %>",
        freq: "<%= HtmlUtil.escape(eqp.getFrequency() != null ? eqp.getFrequency() : "") %>",
        parent: "<%= HtmlUtil.escape(eqp.getParentEquipmentCode() != null ? eqp.getParentEquipmentCode() : "") %>",
        install: "<%= HtmlUtil.escape(eqp.getInstallDate() != null ? eqp.getInstallDate() : "") %>",
        location: "<%= HtmlUtil.escape(eqp.getLocationAddress() != null ? eqp.getLocationAddress() : "") %>",
        rank: "<%= HtmlUtil.escape(eqp.getMaintenanceRank() != null ? eqp.getMaintenanceRank() : "") %>",
        interval: "<%= HtmlUtil.escape(eqp.getInspectionInterval() != null ? eqp.getInspectionInterval().toString() : "") %>",
        status: "<%= HtmlUtil.escape(eqp.getStatus() != null ? eqp.getStatus() : "") %>"
    }<%= i < eqpList.size() - 1 ? "," : "" %>
<%
    }
%>
};

function showEqpInfo(code) {
    var info = document.getElementById("eqpInfo");
    if (!code || !eqpData[code]) {
        info.innerHTML = "<em>設備を選択すると情報が表示されます。</em>";
        return;
    }
    var d = eqpData[code];
    var html = '<table style="font-size:0.92em;width:100%;">';
    html += '<tr><th style="text-align:right;width:100px;">設備名称:</th><td>' + d.name + '</td></tr>';
    html += '<tr><th style="text-align:right;">設備種別:</th><td>' + d.type + '</td></tr>';
    html += '<tr><th style="text-align:right;">電圧階級:</th><td>' + (d.voltage || '-') + '</td></tr>';
    html += '<tr><th style="text-align:right;">保守ランク:</th><td>' + (d.rank || '-') + '</td></tr>';
    html += '<tr><th style="text-align:right;">点検間隔:</th><td>' + (d.interval ? d.interval + 'ヶ月' : '-') + '</td></tr>';
    html += '<tr><th style="text-align:right;">ステータス:</th><td>' + (d.status || '-') + '</td></tr>';
    html += '<tr><th style="text-align:right;">設置場所:</th><td>' + (d.location || '-') + '</td></tr>';
    html += '</table>';
    info.innerHTML = html;
}
</script>

<html:form action="/ins/plan/wizard" method="post">
<html:hidden property="step" value="1"/>
<input type="hidden" name="method" value=""/>

<h2>設備選択</h2>
<table class="form-table">
    <tr>
        <th>設備 <span style="color:#c33;">*</span></th>
        <td>
            <select name="selectedEqpCode" style="width:300px;" onchange="showEqpInfo(this.value);">
                <option value="">-- 選択 --</option>
<%
    for (EqpDto eqp : eqpList) {
        String code = eqp.getEquipmentCode() != null ? eqp.getEquipmentCode() : "";
        String name = eqp.getEquipmentName() != null ? eqp.getEquipmentName() : "";
        String type = eqp.getEquipmentType() != null ? eqp.getEquipmentType() : "";
%>
                <option value="<%= HtmlUtil.escape(code) %>" <%= code.equals(selCode) ? "selected" : "" %>><%= HtmlUtil.escape(code) %> - <%= HtmlUtil.escape(name) %> (<%= HtmlUtil.escape(type) %>)</option>
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
    <input type="submit" value="進む" class="btn btn-primary" onclick="this.form.method.value='step2';"/>
</div>
</html:form>
<script>showEqpInfo("<%= HtmlUtil.escapeJavaScript(selCode) %>");</script>
