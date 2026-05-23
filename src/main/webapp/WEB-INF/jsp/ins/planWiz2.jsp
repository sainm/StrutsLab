<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ page import="java.util.List, com.strutslab.dto.ChkTmplDto" %>
<%
    List<ChkTmplDto> tmplList = (List<ChkTmplDto>) request.getAttribute("tmplList");
    if (tmplList == null) tmplList = new java.util.ArrayList<ChkTmplDto>();
    String errMsg = (String) request.getAttribute("errorMessage");
%>
<% if (errMsg != null) { %>
    <div style="color:#c33;background:#fcc;padding:8px;margin-bottom:12px;border:1px solid #c33;"><%= errMsg %></div>
<% } %>

<!-- Step indicator -->
<table style="width:100%;border-collapse:collapse;margin-bottom:16px;">
    <tr style="text-align:center;">
        <td style="padding:8px;background:#4a90d9;color:#fff;font-weight:bold;">① 設備選択 ✓</td>
        <td style="padding:8px;background:#4a90d9;color:#fff;font-weight:bold;">② テンプレート選択</td>
        <td style="padding:8px;background:#ddd;color:#666;">③ 日程設定</td>
        <td style="padding:8px;background:#ddd;color:#666;">④ 確認</td>
    </tr>
</table>

<html:form action="/ins/plan/wizard" method="post">
<html:hidden property="step" value="2"/>

<h2>点検テンプレート選択</h2>
<table class="form-table">
    <tr>
        <th>選択設備</th>
        <td><%= request.getAttribute("selectedEqpName") != null ? request.getAttribute("selectedEqpName") : "" %></td>
    </tr>
    <tr>
        <th>テンプレート <span style="color:#c33;">*</span></th>
        <td>
<%
    if (tmplList.isEmpty()) {
%>
            <em>該当するテンプレートがありません。</em>
<%
    } else {
        String selTmpl = request.getParameter("selectedTmplId");
        if (selTmpl == null) selTmpl = "";
        for (ChkTmplDto tmpl : tmplList) {
            int tid = tmpl.getTemplateId();
            String tname = tmpl.getTemplateName() != null ? tmpl.getTemplateName() : "";
            String kind = tmpl.getInspectionKind() != null ? tmpl.getInspectionKind() : "";
%>
            <label style="display:block;padding:4px 0;">
                <input type="radio" name="selectedTmplId" value="<%= tid %>"
                    <%= String.valueOf(tid).equals(selTmpl) ? "checked" : "" %>>
                <%= tname %> (<%= kind %>)
            </label>
<%
        }
    }
%>
        </td>
    </tr>
</table>

<div class="button-area" style="text-align:center;margin-top:16px;">
    <input type="button" value="戻る" class="btn btn-secondary"
        onclick="location.href='<%= request.getContextPath() %>/ins/plan/wizard.do?step=step1';" style="margin-right:16px;"/>
    <html:submit property="step" value="進む" styleClass="btn btn-primary"/>
</div>
</html:form>
