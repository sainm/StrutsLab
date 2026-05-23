<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%
    com.strutslab.form.org.DeptForm deptForm = (com.strutslab.form.org.DeptForm) request.getAttribute("deptForm");
    boolean isEdit = deptForm != null && deptForm.getDeptCode() != null && !deptForm.getDeptCode().isEmpty();
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
    String warnMsg = (String) request.getAttribute("warningMessage");
    if (warnMsg != null) {
%>
    <div style="color:#c90;background:#ffe;padding:8px;margin-bottom:12px;border:1px solid #c90;">
        <%= warnMsg %>
    </div>
<%
    }
%>
<html:form action="/org/dept/save" method="post">
<html:hidden property="method" value="save"/>
<% if (!isEdit) { %>
<html:hidden property="deptCode"/>
<% } %>

<table class="form-table">
    <tr>
        <th>部署コード</th>
        <td>
            <% if (isEdit) { %>
                <html:text property="deptCode" readonly="true" styleClass="readonly" style="width:120px;background:#eee;"/>
            <% } else { %>
                <span style="color:#999;">（自動採番）</span>
            <% } %>
        </td>
    </tr>
    <tr>
        <th>部署名 <span style="color:#c33;">*</span></th>
        <td><html:text property="deptName" style="width:300px;"/></td>
    </tr>
    <tr>
        <th>親部署</th>
        <td>
            <html:text property="parentDeptCode" readonly="true" style="width:120px;background:#eee;"/>
            <html:hidden property="parentDeptName"/>
            <span id="parentDeptNameDisplay"><%= deptForm != null && deptForm.getParentDeptName() != null ? deptForm.getParentDeptName() : "" %></span>
            <input type="button" value="選択" onclick="window.open('<%=ctx%>/org/dept/popup.do','deptPopup','width=800,height=600,scrollbars=yes');"/>
        </td>
    </tr>
    <tr>
        <th>階層</th>
        <td>
            <html:select property="deptLevel">
                <html:option value="1">Lv.1</html:option>
                <html:option value="2">Lv.2</html:option>
                <html:option value="3">Lv.3</html:option>
                <html:option value="4">Lv.4</html:option>
            </html:select>
        </td>
    </tr>
    <tr>
        <th>部署種別 <span style="color:#c33;">*</span></th>
        <td>
            <html:select property="deptType">
                <html:option value="">-- 選択 --</html:option>
                <html:option value="本社">本社</html:option>
                <html:option value="支社">支社</html:option>
                <html:option value="営業所">営業所</html:option>
                <html:option value="出張所">出張所</html:option>
                <html:option value="工場">工場</html:option>
                <html:option value="変電所">変電所</html:option>
            </html:select>
        </td>
    </tr>
    <tr>
        <th>開始日 <span style="color:#c33;">*</span></th>
        <td><html:text property="startDate" style="width:100px;"/> YYYYMMDD</td>
    </tr>
    <tr>
        <th>終了日</th>
        <td><html:text property="endDate" style="width:100px;"/> YYYYMMDD（異動・廃止時）</td>
    </tr>
    <tr>
        <th>住所</th>
        <td><html:text property="address" style="width:350px;"/></td>
    </tr>
    <tr>
        <th>電話番号</th>
        <td><html:text property="tel" style="width:150px;"/></td>
    </tr>
</table>

<div style="text-align:center;margin-top:16px;">
    <html:submit value="保存" style="width:100px;"/>
    <input type="button" value="キャンセル" class="btn btn-back" style="width:100px;" onclick="history.back();"/>
</div>

</html:form>
