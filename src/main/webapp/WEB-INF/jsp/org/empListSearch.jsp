<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ page import="com.strutslab.util.HtmlUtil" %>
<%
    String deptCode = request.getParameter("deptCode");
    String position = request.getParameter("position");
    String yearFrom = request.getParameter("yearFrom");
    String yearTo = request.getParameter("yearTo");
    if (deptCode == null) deptCode = "";
    if (position == null) position = "";
    if (yearFrom == null) yearFrom = "";
    if (yearTo == null) yearTo = "";

    String[] quals = request.getParameterValues("qualifications");
%>
<html:form action="/org/emp/list" method="post">
<div class="search-form">
<table class="form-table">
    <tr>
        <th>部署</th>
        <td>
            <select name="deptCode">
                <option value="">-- 全て --</option>
                <option value="HONSHA" <%= "HONSHA".equals(deptCode) ? "selected" : "" %>>本社</option>
                <option value="TOHOKU" <%= "TOHOKU".equals(deptCode) ? "selected" : "" %>>東北支社</option>
                <option value="KANTO" <%= "KANTO".equals(deptCode) ? "selected" : "" %>>関東支社</option>
            </select>
        </td>
        <th>職位</th>
        <td>
            <select name="position">
                <option value="">-- 全て --</option>
                <option value="一般" <%= "一般".equals(position) ? "selected" : "" %>>一般</option>
                <option value="主任" <%= "主任".equals(position) ? "selected" : "" %>>主任</option>
                <option value="係長" <%= "係長".equals(position) ? "selected" : "" %>>係長</option>
                <option value="課長" <%= "課長".equals(position) ? "selected" : "" %>>課長</option>
                <option value="部長" <%= "部長".equals(position) ? "selected" : "" %>>部長</option>
            </select>
        </td>
    </tr>
    <tr>
        <th>入社年月</th>
        <td colspan="3">
            <input type="text" name="yearFrom" value="<%= HtmlUtil.escape(yearFrom) %>" size="6" maxlength="6" placeholder="YYYYMM"> 〜
            <input type="text" name="yearTo" value="<%= HtmlUtil.escape(yearTo) %>" size="6" maxlength="6" placeholder="YYYYMM">
        </td>
    </tr>
    <tr>
        <th>保有資格</th>
        <td colspan="3">
            <label><input type="checkbox" name="qualifications" value="FIRST_AID" <%= contains(quals, "FIRST_AID") ? "checked" : "" %>> 応急手当</label>
            <label><input type="checkbox" name="qualifications" value="HIGH_VOLTAGE" <%= contains(quals, "HIGH_VOLTAGE") ? "checked" : "" %>> 高圧取扱</label>
            <label><input type="checkbox" name="qualifications" value="CRANE" <%= contains(quals, "CRANE") ? "checked" : "" %>> クレーン</label>
            <label><input type="checkbox" name="qualifications" value="WELDING" <%= contains(quals, "WELDING") ? "checked" : "" %>> 溶接</label>
            <label><input type="checkbox" name="qualifications" value="INSPECTOR" <%= contains(quals, "INSPECTOR") ? "checked" : "" %>> 点検員</label>
        </td>
    </tr>
</table>
<div class="button-area">
    <html:submit property="search" value="検索" styleClass="btn btn-primary"/>
    <html:submit property="clear" value="クリア" styleClass="btn btn-secondary"
        onclick="this.form.deptCode.value='';this.form.position.value='';this.form.yearFrom.value='';this.form.yearTo.value='';return true;"/>
</div>
</div>
</html:form>
<%!
    private boolean contains(String[] arr, String val) {
        if (arr == null) return false;
        for (String s : arr) { if (val.equals(s)) return true; }
        return false;
    }
%>
