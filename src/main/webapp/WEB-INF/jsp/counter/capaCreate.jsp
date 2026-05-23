<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/app-common.tld" prefix="app" %>
<%@ page import="java.util.Map, com.strutslab.form.counter.CapaForm" %>
<%
    CapaForm capaForm = (CapaForm) request.getAttribute("capaForm");
    if (capaForm == null) capaForm = new CapaForm();
    String incidentNo = capaForm.getIncidentNo() != null ? capaForm.getIncidentNo() : "";
    String why1 = capaForm.getWhy1() != null ? capaForm.getWhy1() : "";
    String why2 = capaForm.getWhy2() != null ? capaForm.getWhy2() : "";
    String why3 = capaForm.getWhy3() != null ? capaForm.getWhy3() : "";
    String why4 = capaForm.getWhy4() != null ? capaForm.getWhy4() : "";
    String why5 = capaForm.getWhy5() != null ? capaForm.getWhy5() : "";
    String countermeasure = capaForm.getCountermeasure() != null ? capaForm.getCountermeasure() : "";
    String verifyMethod = capaForm.getVerifyMethod() != null ? capaForm.getVerifyMethod() : "";
    String verifyDate = capaForm.getVerifyDate() != null ? capaForm.getVerifyDate() : "";

    Map<String, Object> incidentInfo = (Map<String, Object>) request.getAttribute("incidentInfo");
%>
<html:form action="/counter/capa/create" method="post">

<app:sectionHeader title="是正処置報告書" anchorId="capaCreate"/>

<div class="form-section">
<table class="form-table">
    <tr>
        <th>異常報告番号</th>
        <td colspan="3">
            <strong><%= incidentNo %></strong>
            <input type="hidden" name="incidentNo" value="<%= incidentNo %>"/>
            <% if (incidentInfo != null) { %>
                &nbsp;&nbsp;
                <span>発生日: <%= incidentInfo.get("incidentDate") != null ? incidentInfo.get("incidentDate") : "" %></span>
                &nbsp;&nbsp;
                <span>内容: <%= incidentInfo.get("description") != null ? incidentInfo.get("description") : "" %></span>
            <% } %>
        </td>
    </tr>
</table>
</div>

<app:sectionHeader title="5-Why 分析" anchorId="capaWhy"/>
<div class="form-section">
<table class="form-table">
    <tr>
        <th style="width:80px;">なぜ① <span class="required">*</span></th>
        <td>
            <textarea name="why1" rows="3" cols="80" placeholder="なぜその問題が発生したのか？"><%= why1 %></textarea>
        </td>
    </tr>
    <tr>
        <th>なぜ② <span class="required">*</span></th>
        <td>
            <textarea name="why2" rows="3" cols="80" placeholder="なぜ①の原因は？"><%= why2 %></textarea>
        </td>
    </tr>
    <tr>
        <th>なぜ③ <span class="required">*</span></th>
        <td>
            <textarea name="why3" rows="3" cols="80" placeholder="なぜ②の原因は？"><%= why3 %></textarea>
        </td>
    </tr>
    <tr>
        <th>なぜ④ <span class="required">*</span></th>
        <td>
            <textarea name="why4" rows="3" cols="80" placeholder="なぜ③の原因は？"><%= why4 %></textarea>
        </td>
    </tr>
    <tr>
        <th>なぜ⑤ <span class="required">*</span></th>
        <td>
            <textarea name="why5" rows="3" cols="80" placeholder="なぜ④の原因は？（根本原因）"><%= why5 %></textarea>
        </td>
    </tr>
</table>
</div>

<app:sectionHeader title="是正処置・検証" anchorId="capaAction"/>
<div class="form-section">
<table class="form-table">
    <tr>
        <th>是正処置 <span class="required">*</span></th>
        <td>
            <textarea name="countermeasure" rows="4" cols="80" placeholder="是正処置の内容を記述してください。"><%= countermeasure %></textarea>
        </td>
    </tr>
    <tr>
        <th>検証方法 <span class="required">*</span></th>
        <td>
            <textarea name="verifyMethod" rows="3" cols="80" placeholder="是正処置の有効性を検証する方法を記述してください。"><%= verifyMethod %></textarea>
        </td>
    </tr>
    <tr>
        <th>検証期限 <span class="required">*</span></th>
        <td>
            <input type="text" name="verifyDate" value="<%= verifyDate %>" size="10" maxlength="8" placeholder="YYYYMMDD"/>
            <input type="button" value="📅" onclick="var d=prompt('日付を入力(YYYYMMDD):','');if(d){this.form.verifyDate.value=d;}"/>
        </td>
    </tr>
</table>
</div>

<div class="button-area">
    <input type="submit" name="save" value="承認申請" class="btn btn-primary"
        onclick="return confirm('是正処置報告書を提出してもよろしいですか？');"/>
    <input type="button" value="キャンセル" class="btn btn-secondary"
        onclick="location.href='<%=request.getContextPath()%>/counter/list.do';"/>
</div>
</html:form>
