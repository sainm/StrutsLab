<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ page import="java.util.List, com.strutslab.dto.ChkTmplDto" %>
<%
    List<ChkTmplDto> list = (List<ChkTmplDto>) request.getAttribute("templateList");
    if (list == null) list = java.util.Collections.emptyList();
    String contextPath = request.getContextPath();
%>
<div class="list-table-container">
<form name="chkItemListForm" method="post" action="<%=contextPath%>/mst/chkItem/list.do">
<table class="list-table">
    <thead>
        <tr>
            <th>テンプレート名</th>
            <th>設備種別</th>
            <th>点検種別</th>
            <th>項目数</th>
            <th>最終更新日</th>
            <th>操作</th>
        </tr>
    </thead>
    <tbody>
    <%
        if (list.isEmpty()) {
    %>
        <tr><td colspan="6" style="text-align:center;">検索結果がありません。</td></tr>
    <%
        } else {
            for (ChkTmplDto tmpl : list) {
                int tid = tmpl.getTemplateId();
                String name = tmpl.getTemplateName() != null ? tmpl.getTemplateName() : "";
                String eqType = tmpl.getEquipmentType() != null ? tmpl.getEquipmentType() : "";
                String inspKind = tmpl.getInspectionKind() != null ? tmpl.getInspectionKind() : "";
                int itemCnt = tmpl.getItemCount();
                String updDate = tmpl.getLastUpdateDate() != null ? tmpl.getLastUpdateDate() : "";
    %>
        <tr>
            <td><a href="<%=contextPath%>/mst/chkItem/edit.do?templateId=<%= tid %>"><%= name %></a></td>
            <td><%= eqType %></td>
            <td><%= inspKind %></td>
            <td style="text-align:right;"><%= itemCnt %></td>
            <td><%= updDate %></td>
            <td>
                <input type="button" value="コピー" class="btn btn-small"
                    onclick="location.href='<%=contextPath%>/mst/chkItem/list.do?copy=<%= tid %>'"/>
                <input type="button" value="▲" class="btn btn-small"
                    onclick="location.href='<%=contextPath%>/mst/chkItem/list.do?moveUp=<%= tid %>'"/>
                <input type="button" value="▼" class="btn btn-small"
                    onclick="location.href='<%=contextPath%>/mst/chkItem/list.do?moveDown=<%= tid %>'"/>
            </td>
        </tr>
    <%
            }
        }
    %>
    </tbody>
</table>

<!-- Pagination -->
<jsp:include page="/WEB-INF/jsp/common/paging.jsp"/>

<!-- Action Buttons -->
<div class="button-area">
    <input type="button" value="新規テンプレート" class="btn btn-success"
        onclick="location.href='<%=contextPath%>/mst/chkItem/edit.do?method=new'"/>
</div>
</form>
</div>
