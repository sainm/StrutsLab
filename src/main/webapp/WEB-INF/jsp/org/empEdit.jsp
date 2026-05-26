<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ page import="com.strutslab.util.HtmlUtil" %>
<%
    com.strutslab.form.org.EmpForm empForm = (com.strutslab.form.org.EmpForm) request.getAttribute("empForm");
    boolean isEdit = empForm != null && empForm.getEmpNo() != null && !empForm.getEmpNo().isEmpty();
    String ctx = request.getContextPath();
%>
<%
    String errMsg = (String) request.getAttribute("errorMessage");
    if (errMsg != null) {
%>
    <div style="color:#c33;background:#fcc;padding:8px;margin-bottom:12px;border:1px solid #c33;">
        <%= HtmlUtil.escape(errMsg) %>
    </div>
<%
    }
%>
<html:form action="/org/emp/save" method="post">
<html:hidden property="method" value="save"/>
<html:hidden property="empNo"/>

<!-- Section 1: 基本情報 -->
<h2>基本情報</h2>
<table class="form-table">
    <tr>
        <th>社員番号</th>
        <td>
            <% if (isEdit) { %>
                <html:text property="empNo" readonly="true" styleClass="readonly" style="width:120px;background:#eee;"/>
            <% } else { %>
                <span style="color:#999;">（自動採番 EMP-NNNN）</span>
            <% } %>
        </td>
    </tr>
    <tr>
        <th>氏名 <span style="color:#c33;">*</span></th>
        <td><html:text property="name" style="width:200px;"/></td>
    </tr>
    <tr>
        <th>氏名カナ</th>
        <td><html:text property="nameKana" style="width:200px;"/> カタカナ</td>
    </tr>
    <tr>
        <th>生年月日</th>
        <td><html:text property="birthDate" style="width:100px;"/> YYYYMMDD</td>
    </tr>
    <tr>
        <th>入社年月</th>
        <td><html:text property="joinDate" style="width:80px;"/> YYYYMM</td>
    </tr>
</table>

<!-- Section 2: 所属・職位 -->
<h2>所属・職位</h2>
<table class="form-table">
    <tr>
        <th>部署</th>
        <td>
            <html:text property="deptCode" readonly="true" style="width:120px;background:#eee;"/>
            <span id="deptNameDisplay"><%= HtmlUtil.escape(empForm != null && empForm.getDeptName() != null ? empForm.getDeptName() : "") %></span>
            <input type="button" value="選択" onclick="window.open('<%=ctx%>/org/dept/popup.do','deptPopup','width=800,height=600,scrollbars=yes');"/>
        </td>
    </tr>
    <tr>
        <th>職位</th>
        <td>
            <html:select property="position">
                <html:option value="">-- 選択 --</html:option>
                <html:option value="一般">一般</html:option>
                <html:option value="主任">主任</html:option>
                <html:option value="係長">係長</html:option>
                <html:option value="課長">課長</html:option>
                <html:option value="部長">部長</html:option>
            </html:select>
        </td>
    </tr>
    <tr>
        <th>配属日</th>
        <td><html:text property="assignDate" style="width:100px;"/> YYYYMMDD</td>
    </tr>
</table>

<!-- Section 3: 保有資格・技能 -->
<h2>保有資格・技能</h2>
<table class="form-table">
    <tr>
        <th>保有資格</th>
        <td>
            <label><html:multibox property="qualifications" value="FIRST_AID"/>応急手当</label>&nbsp;
            <label><html:multibox property="qualifications" value="HIGH_VOLTAGE"/>高圧取扱</label>&nbsp;
            <label><html:multibox property="qualifications" value="CRANE"/>クレーン</label>&nbsp;
            <label><html:multibox property="qualifications" value="WELDING"/>溶接</label>&nbsp;
            <label><html:multibox property="qualifications" value="INSPECTOR"/>点検員</label>
        </td>
    </tr>
</table>

<!-- Section 4: 点検員認定 -->
<h2>点検員認定</h2>
<table class="form-table">
    <tr>
        <th>点検員ランク</th>
        <td>
            <html:select property="inspectionRank">
                <html:option value="">-- 選択 --</html:option>
                <html:option value="A">A</html:option>
                <html:option value="B">B</html:option>
                <html:option value="C">C</html:option>
            </html:select>
        </td>
    </tr>
    <tr>
        <th>認定日</th>
        <td><html:text property="inspectionCertDate" style="width:100px;"/> YYYYMMDD</td>
    </tr>
    <tr>
        <th>有効期限</th>
        <td><html:text property="inspectionCertExpire" style="width:100px;"/> YYYYMMDD（未来日付必須）</td>
    </tr>
</table>

<!-- Section 5: アカウント情報 -->
<h2>アカウント情報</h2>
<table class="form-table">
    <tr>
        <th>ログインID <span style="color:#c33;">*</span></th>
        <td><html:text property="loginId" style="width:150px;"/></td>
    </tr>
    <tr>
        <th>パスワード <%= HtmlUtil.escape(isEdit ? "" : "<span style=\"color:#c33;\">*</span>") %></th>
        <td><html:password property="password" style="width:150px;"/> <%= HtmlUtil.escape(isEdit ? "（変更時のみ入力）" : "") %></td>
    </tr>
    <tr>
        <th>パスワード（確認）</th>
        <td><html:password property="passwordConfirm" style="width:150px;"/></td>
    </tr>
</table>

<div style="text-align:center;margin-top:16px;">
    <html:submit value="保存" style="width:100px;"/>
    <input type="button" value="キャンセル" class="btn btn-back" style="width:100px;" onclick="history.back();"/>
</div>

</html:form>
