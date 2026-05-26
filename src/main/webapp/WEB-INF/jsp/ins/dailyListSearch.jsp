<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ page import="java.util.List, com.strutslab.dto.EmpDto, com.strutslab.form.ins.DailyForm, com.strutslab.util.HtmlUtil" %>
<%
    DailyForm df = (DailyForm) request.getAttribute("dailyForm");
    if (df == null) df = new DailyForm();
    String targetDate = df.getTargetDate() != null ? df.getTargetDate() : "";
    String personCode = df.getPersonCode() != null ? df.getPersonCode() : "";
    String statusFilter = df.getStatusFilter() != null ? df.getStatusFilter() : "全部";
    List<EmpDto> empList = (List<EmpDto>) request.getAttribute("empList");
%>
<html:form action="/ins/daily" method="post">
<div class="search-form">
<table class="form-table">
    <tr>
        <th>点検日</th>
        <td>
            <input type="text" name="targetDate" value="<%= HtmlUtil.escape(targetDate) %>" size="10" maxlength="8" placeholder="YYYYMMDD">
        </td>
        <th>担当者</th>
        <td>
            <select name="personCode">
                <option value="">-- 全て --</option>
<%
    if (empList != null) {
        for (EmpDto emp : empList) {
            String code = emp.getEmpNo();
            String name = emp.getName();
            if (code == null) continue;
            String selected = code.equals(personCode) ? "selected" : "";
%>
                <option value="<%= HtmlUtil.escape(code) %>" <%= HtmlUtil.escape(selected) %>><%= HtmlUtil.escape(name) %></option>
<%
        }
    }
%>
            </select>
        </td>
        <th>ステータス</th>
        <td>
            <select name="statusFilter">
                <option value="全部" <%= "全部".equals(statusFilter) ? "selected" : "" %>>全部</option>
                <option value="未了" <%= "未了".equals(statusFilter) ? "selected" : "" %>>未了</option>
                <option value="一部完了" <%= "一部完了".equals(statusFilter) ? "selected" : "" %>>一部完了</option>
                <option value="完了" <%= "完了".equals(statusFilter) ? "selected" : "" %>>完了</option>
            </select>
        </td>
    </tr>
</table>
<div class="button-area">
    <html:submit property="search" value="表示" styleClass="btn btn-primary"/>
</div>
</div>
</html:form>
