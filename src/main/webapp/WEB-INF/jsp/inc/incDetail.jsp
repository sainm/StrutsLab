<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/app-common.tld" prefix="app" %>
<%@ page import="com.strutslab.dto.IncidentDto, com.strutslab.form.inc.IncidentForm" %>
<%
    IncidentDto inc = (IncidentDto) request.getAttribute("incident");
    IncidentForm f = (IncidentForm) request.getAttribute("incidentForm");
    if (inc == null) inc = new IncidentDto();
    if (f == null) f = new IncidentForm();

    String incidentNo = inc.getIncidentNo() != null ? inc.getIncidentNo() : (f.getIncidentNo() != null ? f.getIncidentNo() : "");
    String incidentDateTime = inc.getIncidentDatetime() != null ? new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm").format(inc.getIncidentDatetime()) : (f.getIncidentDateTime() != null ? f.getIncidentDateTime() : "");
    String finder = inc.getFinder() != null ? inc.getFinder() : (f.getFinder() != null ? f.getFinder() : "");
    String equipmentCode = inc.getEquipmentCode() != null ? inc.getEquipmentCode() : (f.getEquipmentCode() != null ? f.getEquipmentCode() : "");
    String equipmentName = inc.getEquipmentName() != null ? inc.getEquipmentName() : (f.getEquipmentName() != null ? f.getEquipmentName() : "");
    String weather = inc.getWeather() != null ? inc.getWeather() : (f.getWeather() != null ? f.getWeather() : "");
    Integer temperature = inc.getTemperature() != null ? inc.getTemperature() : f.getTemperature();
    String tempStr = temperature != null ? temperature.toString() : "";
    String incidentType = inc.getIncidentType() != null ? inc.getIncidentType() : (f.getIncidentType() != null ? f.getIncidentType() : "");
    String severity = inc.getSeverity() != null ? inc.getSeverity() : (f.getSeverity() != null ? f.getSeverity() : "");
    String incidentPart = inc.getIncidentPart() != null ? inc.getIncidentPart() : (f.getIncidentPart() != null ? f.getIncidentPart() : "");
    String incidentDetail = inc.getIncidentDetail() != null ? inc.getIncidentDetail() : (f.getIncidentDetail() != null ? f.getIncidentDetail() : "");
    String tmpAction = inc.getTmpAction() != null ? inc.getTmpAction() : (f.getTmpAction() != null ? f.getTmpAction() : "");
    String tmpActionPerson = inc.getTmpActionPerson() != null ? inc.getTmpActionPerson() : (f.getTmpActionPerson() != null ? f.getTmpActionPerson() : "");
    String tmpActionDate = inc.getTmpActionDate() != null ? new java.text.SimpleDateFormat("yyyy/MM/dd").format(inc.getTmpActionDate()) : (f.getTmpActionDate() != null ? f.getTmpActionDate() : "");
    String cause = inc.getCause() != null ? inc.getCause() : (f.getCause() != null ? f.getCause() : "");
    String counterDetail = inc.getCounterDetail() != null ? inc.getCounterDetail() : (f.getCounterDetail() != null ? f.getCounterDetail() : "");
    String status = inc.getStatus() != null ? inc.getStatus() : (f.getStatus() != null ? f.getStatus() : "");

    boolean showCause = "調査中".equals(status);
    boolean showCounterDetail = "対応中".equals(status);
%>
<html:form action="/inc/detail" method="post">
<input type="hidden" name="incidentNo" value="<%= incidentNo %>"/>

<%-- Block 1: Incident Information (Readonly) --%>
<app:sectionHeader title="① 発生情報" anchorId="block1"/>
<div class="form-section">
<table class="form-table">
    <tr>
        <th>報告番号</th><td><%= incidentNo %></td>
        <th>ステータス</th><td><app:statusBadge status="<%= status %>"/></td>
    </tr>
    <tr>
        <th>発生日時</th><td><%= incidentDateTime %></td>
        <th>発見者</th><td><%= finder %></td>
    </tr>
    <tr>
        <th>設備コード</th><td><%= equipmentCode %></td>
        <th>設備名</th><td><%= equipmentName %></td>
    </tr>
    <tr>
        <th>天候</th><td><%= weather %></td>
        <th>気温（℃）</th><td><%= tempStr %></td>
    </tr>
</table>
</div>

<%-- Block 2: Incident Details (Readonly) --%>
<app:sectionHeader title="② 異常内容" anchorId="block2"/>
<div class="form-section">
<table class="form-table">
    <tr>
        <th>異常種別</th><td><%= incidentType %></td>
        <th>重大度</th><td><app:statusBadge status="<%= severity %>"/></td>
    </tr>
    <tr>
        <th>異常部位</th><td colspan="3"><%= incidentPart %></td>
    </tr>
    <tr>
        <th>異常内容詳細</th>
        <td colspan="3"><pre style="white-space:pre-wrap;margin:0;"><%= incidentDetail %></pre></td>
    </tr>
    <tr>
        <th>添付ファイル</th>
        <td colspan="3" id="attachArea">
            <%
                java.io.File attachDir = new java.io.File(application.getRealPath("/attachments/inc/" + incidentNo));
                if (attachDir.exists()) {
                    java.io.File[] files = attachDir.listFiles();
                    if (files != null && files.length > 0) {
                        for (java.io.File af : files) {
                            String fileName = af.getName();
            %>
                <a href="<%=request.getContextPath()%>/attachments/inc/<%= incidentNo %>/<%= fileName %>" target="_blank"><%= fileName %></a><br/>
            <%
                        }
                    } else {
            %>
                添付ファイルなし
            <%
                    }
                } else {
            %>
                添付ファイルなし
            <%
                }
            %>
        </td>
    </tr>
</table>
</div>

<%-- Block 3: Temporary Action (Readonly) --%>
<app:sectionHeader title="③ 暫定処置" anchorId="block3"/>
<div class="form-section">
<table class="form-table">
    <tr>
        <th>暫定処置内容</th>
        <td colspan="3"><pre style="white-space:pre-wrap;margin:0;"><%= tmpAction %></pre></td>
    </tr>
    <tr>
        <th>処置担当者</th><td><%= tmpActionPerson %></td>
        <th>処置日</th><td><%= tmpActionDate %></td>
    </tr>
</table>
</div>

<%-- Cause section (shown when transitioning from 調査中 to 対応中) --%>
<%
    if (showCause) {
%>
<app:sectionHeader title="原因分析" anchorId="causeSection"/>
<div class="form-section">
<table class="form-table">
    <tr>
        <th>原因 <span class="required">*</span></th>
        <td><textarea name="cause" rows="4" cols="60"><%= cause %></textarea></td>
    </tr>
</table>
</div>
<%
    }
%>

<%-- Counter Detail section (shown when transitioning from 対応中 to 完了) --%>
<%
    if (showCounterDetail) {
%>
<app:sectionHeader title="対応内容" anchorId="counterSection"/>
<div class="form-section">
<table class="form-table">
    <tr>
        <th>対応内容 <span class="required">*</span></th>
        <td><textarea name="counterDetail" rows="4" cols="60"><%= counterDetail %></textarea></td>
    </tr>
</table>
</div>
<%
    }
%>

<%-- Timeline --%>
<app:sectionHeader title="ステータス履歴" anchorId="timeline"/>
<div class="form-section">
    <app:timeline/>
</div>

<%-- Status Transition Buttons (conditional) --%>
<div class="button-area">
<%
    if ("未了".equals(status)) {
%>
    <input type="submit" name="method" value="investigate" class="btn btn-primary"
        onclick="this.form.method.value='investigate';return confirm('ステータスを「調査中」に変更します。よろしいですか？');"/>
    <script>document.querySelector('input[value="investigate"]').value = '調査開始';</script>
<%
    } else if ("調査中".equals(status)) {
%>
    <input type="submit" name="method" value="counter" class="btn btn-primary"
        onclick="this.form.method.value='counter';return confirm('ステータスを「対応中」に変更します。よろしいですか？');"/>
    <script>document.querySelector('input[value="counter"]').value = '対応開始（原因入力必須）';</script>
<%
    } else if ("対応中".equals(status)) {
%>
    <input type="submit" name="method" value="complete" class="btn btn-primary"
        onclick="this.form.method.value='complete';return confirm('ステータスを「完了」に変更します。よろしいですか？');"/>
    <script>document.querySelector('input[value="complete"]').value = '完了報告（対応内容入力必須）';</script>
<%
    } else if ("完了".equals(status)) {
%>
    <input type="submit" name="method" value="closeIncident" class="btn btn-secondary"
        onclick="this.form.method.value='closeIncident';return confirm('ステータスを「クローズ」に変更します。よろしいですか？');"/>
    <script>document.querySelector('input[value="closeIncident"]').value = 'クローズ';</script>
    <input type="submit" name="method" value="capa" class="btn btn-primary"
        onclick="this.form.method.value='capa';"/>
    <script>
        var capaBtn = document.querySelector('input[value="capa"]');
        if (capaBtn) capaBtn.value = '是正処置報告書作成';
    </script>
<%
    }
%>
    <input type="button" value="一覧に戻る" class="btn btn-secondary"
        onclick="location.href='<%=request.getContextPath()%>/inc/list.do'"/>
</div>

</html:form>

<%
    if (request.getAttribute("org.apache.struts.action.ERROR") != null) {
%>
<div class="error-messages" style="margin-top:12px;">
    <html:errors/>
</div>
<%
    }
%>

<script>
// Fix button labels - since we use value for method parameter, we patch display text
(function() {
    var buttons = document.querySelectorAll('input[type="submit"][name="method"]');
    for (var i = 0; i < buttons.length; i++) {
        var btn = buttons[i];
        // The value attribute is used as the method parameter
        // Display text is set via inline script above
    }
})();
</script>
