<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ page import="java.util.List, com.strutslab.dto.EqpDto, com.strutslab.util.HtmlUtil" %>
<%
    List<EqpDto> list = (List<EqpDto>) request.getAttribute("eqpList");
    if (list == null) list = java.util.Collections.emptyList();
%>
<div class="list-table-container">
<form name="eqpListForm" method="post" action="<%=request.getContextPath()%>/mst/eqp/list.do">
<table class="list-table">
    <thead>
        <tr>
            <th><input type="checkbox" onclick="toggleAll(this)"/></th>
            <th>設備コード</th>
            <th>設備名</th>
            <th>設備種別</th>
            <th>電圧階級</th>
            <th>設置年月</th>
            <th>保全ランク</th>
            <th>担当部署</th>
        </tr>
    </thead>
    <tbody>
    <%
        if (list.isEmpty()) {
    %>
        <tr><td colspan="8" style="text-align:center;">検索結果がありません。</td></tr>
    <%
        } else {
            for (EqpDto eqp : list) {
                String code = eqp.getEquipmentCode() != null ? eqp.getEquipmentCode() : "";
                String name = eqp.getEquipmentName() != null ? eqp.getEquipmentName() : "";
                String type = eqp.getEquipmentType() != null ? eqp.getEquipmentType() : "";
                String voltage = eqp.getVoltageLevel() != null ? eqp.getVoltageLevel() : "";
                String install = eqp.getInstallDate() != null ? eqp.getInstallDate() : "";
                String rank = eqp.getMaintenanceRank() != null ? eqp.getMaintenanceRank() : "";
                String dept = eqp.getLocationAddress() != null ? eqp.getLocationAddress() : "";
    %>
        <tr>
            <td><input type="checkbox" name="selectedItems" value="<%= HtmlUtil.escape(code) %>"/></td>
            <td><a href="<%=request.getContextPath()%>/mst/eqp/edit.do?equipmentCode=<%= java.net.URLEncoder.encode(code, "UTF-8") %>"><%= HtmlUtil.escape(code) %></a></td>
            <td><%= HtmlUtil.escape(name) %></td>
            <td><%= HtmlUtil.escape(type) %></td>
            <td><%= HtmlUtil.escape(voltage) %></td>
            <td><%= HtmlUtil.escape(install) %></td>
            <td><%= HtmlUtil.escape(rank) %></td>
            <td><%= HtmlUtil.escape(dept) %></td>
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
    <input type="button" value="CSV出力" class="btn btn-primary"
        onclick="location.href='<%=request.getContextPath()%>/mst/eqp/list.do?csv=true'"/>
    <input type="button" value="選択削除" class="btn btn-danger"
        onclick="return confirmSubmit('削除してもよろしいですか？')"/>
    <input type="button" value="新規登録" class="btn btn-success"
        onclick="location.href='<%=request.getContextPath()%>/mst/eqp/edit.do?method=new'"/>
</div>
</form>
</div>
<script>
function toggleAll(src) {
    var checkboxes = document.getElementsByName('selectedItems');
    for (var i = 0; i < checkboxes.length; i++) {
        checkboxes[i].checked = src.checked;
    }
}
function confirmSubmit(msg) {
    if (!confirm(msg)) return false;
    var form = document.forms['eqpListForm'];
    var action = form.action;
    form.action = action + '?delete=true';
    form.submit();
    return false;
}
</script>
