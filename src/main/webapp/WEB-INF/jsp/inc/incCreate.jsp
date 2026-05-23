<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/app-common.tld" prefix="app" %>
<%@ page import="java.util.List, com.strutslab.dto.IncidentDto, com.strutslab.form.inc.IncidentForm" %>
<%
    IncidentForm f = (IncidentForm) request.getAttribute("incidentForm");
    if (f == null) f = (IncidentForm) session.getAttribute("incidentForm");
    String incidentDateTime = f != null && f.getIncidentDateTime() != null ? f.getIncidentDateTime() : "";
    String finder = f != null && f.getFinder() != null ? f.getFinder() : "";
    String equipmentCode = f != null && f.getEquipmentCode() != null ? f.getEquipmentCode() : "";
    String equipmentName = f != null && f.getEquipmentName() != null ? f.getEquipmentName() : "";
    String weather = f != null && f.getWeather() != null ? f.getWeather() : "";
    Integer temperature = f != null ? f.getTemperature() : null;
    String tempStr = temperature != null ? temperature.toString() : "";
    String incidentType = f != null && f.getIncidentType() != null ? f.getIncidentType() : "";
    String severity = f != null && f.getSeverity() != null ? f.getSeverity() : "";
    String incidentPart = f != null && f.getIncidentPart() != null ? f.getIncidentPart() : "";
    String incidentDetail = f != null && f.getIncidentDetail() != null ? f.getIncidentDetail() : "";
    String tmpAction = f != null && f.getTmpAction() != null ? f.getTmpAction() : "";
    String tmpActionPerson = f != null && f.getTmpActionPerson() != null ? f.getTmpActionPerson() : "";
    String tmpActionDate = f != null && f.getTmpActionDate() != null ? f.getTmpActionDate() : "";
    String fromInspection = f != null && f.getFromInspection() != null ? f.getFromInspection() : "";

    String message = (String) request.getAttribute("message");
    List<IncidentDto> similarResults = (List<IncidentDto>) request.getAttribute("similarResults");
%>
<html:form action="/inc/create" method="post" enctype="multipart/form-data">
<input type="hidden" name="fromInspection" value="<%= fromInspection %>"/>

<%-- Block 1: Incident Information --%>
<app:sectionHeader title="① 発生情報" anchorId="block1"/>
<div class="form-section">
<table class="form-table">
    <tr>
        <th>発生日時 <span class="required">*</span></th>
        <td><input type="text" name="incidentDateTime" value="<%= incidentDateTime %>" size="16" maxlength="16" placeholder="YYYY-MM-DD HH:mm"/></td>
        <th>発見者</th>
        <td><input type="text" name="finder" value="<%= finder %>" size="20"/></td>
    </tr>
    <tr>
        <th>設備コード <span class="required">*</span></th>
        <td>
            <input type="text" name="equipmentCode" value="<%= equipmentCode %>" size="20" id="eqpCode"/>
            <input type="button" value="設備検索" class="btn btn-small btn-secondary"
                onclick="window.open('<%=request.getContextPath()%>/mst/eqp/list.do?popup=true','eqpPopup','width=800,height=600')"/>
        </td>
        <th>設備名</th>
        <td><input type="text" name="equipmentName" value="<%= equipmentName %>" size="30" readonly="readonly" style="background:#f5f5f5;"/></td>
    </tr>
    <tr>
        <th>天候</th>
        <td>
            <select name="weather">
                <option value="">-- 選択 --</option>
                <option value="晴" <%= "晴".equals(weather) ? "selected" : "" %>>晴</option>
                <option value="曇" <%= "曇".equals(weather) ? "selected" : "" %>>曇</option>
                <option value="雨" <%= "雨".equals(weather) ? "selected" : "" %>>雨</option>
                <option value="雪" <%= "雪".equals(weather) ? "selected" : "" %>>雪</option>
                <option value="台風" <%= "台風".equals(weather) ? "selected" : "" %>>台風</option>
            </select>
        </td>
        <th>気温（℃）</th>
        <td><input type="text" name="temperature" value="<%= tempStr %>" size="6" maxlength="3"/></td>
    </tr>
</table>
</div>

<%-- Block 2: Incident Details --%>
<app:sectionHeader title="② 異常内容" anchorId="block2"/>
<div class="form-section">
<table class="form-table">
    <tr>
        <th>異常種別 <span class="required">*</span></th>
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
        <th>重大度 <span class="required">*</span></th>
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
        <th>異常部位</th>
        <td colspan="3"><input type="text" name="incidentPart" value="<%= incidentPart %>" size="60"/></td>
    </tr>
    <tr>
        <th>異常内容詳細 <span class="required">*</span></th>
        <td colspan="3"><textarea name="incidentDetail" rows="5" cols="60"><%= incidentDetail %></textarea></td>
    </tr>
    <tr>
        <th>添付ファイル</th>
        <td colspan="3"><input type="file" name="files" multiple="multiple"/></td>
    </tr>
</table>
</div>

<%-- Similar Cases Search Button --%>
<div class="button-area" style="margin-bottom:10px;">
    <input type="submit" name="searchSimilar" value="類似事例検索" class="btn btn-info"
        onclick="return confirm('現在入力中の異常種別・部位で類似事例を検索しますか？');"/>
</div>

<%-- Similar Results Table --%>
<%
    if (similarResults != null && !similarResults.isEmpty()) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm");
%>
<app:sectionHeader title="類似事例検索結果" anchorId="similarResults"/>
<div class="form-section">
<table class="list-table">
    <thead>
        <tr>
            <th>報告番号</th>
            <th>発生日時</th>
            <th>設備名</th>
            <th>異常種別</th>
            <th>異常部位</th>
            <th>重大度</th>
            <th>ステータス</th>
        </tr>
    </thead>
    <tbody>
    <%
        for (IncidentDto dto : similarResults) {
            String sno = dto.getIncidentNo() != null ? dto.getIncidentNo() : "";
            String sdt = dto.getIncidentDatetime() != null ? sdf.format(dto.getIncidentDatetime()) : "";
            String sen = dto.getEquipmentName() != null ? dto.getEquipmentName() : "";
            String sit = dto.getIncidentType() != null ? dto.getIncidentType() : "";
            String sip = dto.getIncidentPart() != null ? dto.getIncidentPart() : "";
            String ssv = dto.getSeverity() != null ? dto.getSeverity() : "";
            String sst = dto.getStatus() != null ? dto.getStatus() : "";
    %>
        <tr>
            <td><a href="<%=request.getContextPath()%>/inc/detail.do?incidentNo=<%= java.net.URLEncoder.encode(sno, "UTF-8") %>" target="_blank"><%= sno %></a></td>
            <td><%= sdt %></td>
            <td><%= sen %></td>
            <td><%= sit %></td>
            <td><%= sip %></td>
            <td><app:statusBadge status="<%= ssv %>"/></td>
            <td><app:statusBadge status="<%= sst %>"/></td>
        </tr>
    <%
        }
    %>
    </tbody>
</table>
</div>
<%
    } else if (similarResults != null) {
%>
<div class="form-section">
    <p>類似事例は見つかりませんでした。</p>
</div>
<%
    }
%>

<%-- Block 3: Temporary Action --%>
<app:sectionHeader title="③ 暫定処置" anchorId="block3"/>
<div class="form-section">
<table class="form-table">
    <tr>
        <th>暫定処置内容</th>
        <td colspan="3"><textarea name="tmpAction" rows="3" cols="60"><%= tmpAction %></textarea></td>
    </tr>
    <tr>
        <th>処置担当者</th>
        <td><input type="text" name="tmpActionPerson" value="<%= tmpActionPerson %>" size="20"/></td>
        <th>処置日</th>
        <td><input type="text" name="tmpActionDate" value="<%= tmpActionDate %>" size="10" maxlength="10" placeholder="YYYY-MM-DD"/></td>
    </tr>
</table>
</div>

<%-- Action Buttons --%>
<div class="button-area">
    <input type="submit" name="save" value="登録" class="btn btn-primary"/>
    <input type="submit" name="tempSave" value="一時保存" class="btn btn-secondary"/>
    <input type="button" value="キャンセル" class="btn btn-secondary"
        onclick="location.href='<%=request.getContextPath()%>/inc/list.do'"/>
</div>

</html:form>

<%-- Messages --%>
<%
    if (message != null && !message.isEmpty()) {
%>
<div class="info-message" style="margin-top:12px;color:green;"><%= message %></div>
<%
    }
    if (request.getAttribute("org.apache.struts.action.ERROR") != null) {
%>
<div class="error-messages" style="margin-top:12px;">
    <html:errors/>
</div>
<%
    }
%>

<script>
// Popup handler for equipment search
var eqpPopupTimer = null;
function setupEqpPopupListener() {
    // The equipment search popup will call window.opener.setEquipment(code, name)
    // when a selection is made.
}
window.setEquipment = function(code, name) {
    document.getElementById('eqpCode').value = code;
    var eqpNameInputs = document.getElementsByName('equipmentName');
    if (eqpNameInputs.length > 0) eqpNameInputs[0].value = name || '';
};
</script>
