<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ page import="com.strutslab.util.HtmlUtil" %>
<%
    String deptCode = request.getParameter("deptCode");
    String deptName = request.getParameter("deptName");
    if (deptCode == null) deptCode = "";
    if (deptName == null) deptName = "";
%>
<html:form action="/org/dept/list" method="post">
<div class="search-form">
<table class="form-table">
    <tr>
        <th>部署コード</th>
        <td><input type="text" name="deptCode" value="<%= HtmlUtil.escape(deptCode) %>" size="15" maxlength="20"></td>
        <th>部署名</th>
        <td><input type="text" name="deptName" value="<%= HtmlUtil.escape(deptName) %>" size="30"></td>
    </tr>
</table>
<div class="button-area">
    <html:submit property="search" value="検索" styleClass="btn btn-primary"/>
    <html:submit property="clear" value="クリア" styleClass="btn btn-secondary"
        onclick="this.form.deptCode.value='';this.form.deptName.value='';return true;"/>
</div>
</div>
</html:form>
