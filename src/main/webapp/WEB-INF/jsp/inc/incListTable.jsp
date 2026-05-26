<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/app-common.tld" prefix="app" %>
<%@ page import="java.util.List, com.strutslab.dto.IncidentDto, com.strutslab.util.HtmlUtil" %>
<%
    List<IncidentDto> list = (List<IncidentDto>) request.getAttribute("incidentList");
    if (list == null) list = java.util.Collections.emptyList();
%>
<div class="list-table-container">
<form name="incListForm" method="post" action="<%=request.getContextPath()%>/inc/list.do">
<table class="list-table">
    <thead>
        <tr>
            <th><input type="checkbox" onclick="toggleAll(this)"/></th>
            <th>報告番号</th>
            <th>発生日時</th>
            <th>設備名</th>
            <th>異常種別</th>
            <th>重大度</th>
            <th>ステータス</th>
            <th>担当班</th>
        </tr>
    </thead>
    <tbody>
    <%
        if (list.isEmpty()) {
    %>
        <tr><td colspan="8" style="text-align:center;">検索結果がありません。</td></tr>
    <%
        } else {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm");
            for (IncidentDto dto : list) {
                String incidentNo = dto.getIncidentNo() != null ? dto.getIncidentNo() : "";
                String dt = dto.getIncidentDatetime() != null ? sdf.format(dto.getIncidentDatetime()) : "";
                String eqpName = dto.getEquipmentName() != null ? dto.getEquipmentName() : "";
                String incType = dto.getIncidentType() != null ? dto.getIncidentType() : "";
                String sev = dto.getSeverity() != null ? dto.getSeverity() : "";
                String st = dto.getStatus() != null ? dto.getStatus() : "";
                String team = dto.getTmpActionPerson() != null ? dto.getTmpActionPerson() : "";
    %>
        <tr>
            <td><input type="checkbox" name="selectedItems" value="<%= HtmlUtil.escape(incidentNo) %>"/></td>
            <td><a href="<%=request.getContextPath()%>/inc/detail.do?incidentNo=<%= java.net.URLEncoder.encode(incidentNo, "UTF-8") %>"><%= HtmlUtil.escape(incidentNo) %></a></td>
            <td><%= HtmlUtil.escape(dt) %></td>
            <td><%= HtmlUtil.escape(eqpName) %></td>
            <td><%= HtmlUtil.escape(incType) %></td>
            <td><app:statusBadge status="<%= sev %>"/></td>
            <td><app:statusBadge status="<%= st %>"/></td>
            <td><%= HtmlUtil.escape(team) %></td>
        </tr>
    <%
            }
        }
    %>
    </tbody>
</table>

<!-- Pagination -->
<jsp:include page="/WEB-INF/jsp/common/paging.jsp"/>

<!-- Bulk Action & Buttons -->
<div class="button-area">
    <select name="bulkStatus">
        <option value="">一括ステータス変更</option>
        <option value="未了">未了</option>
        <option value="調査中">調査中</option>
        <option value="対応中">対応中</option>
        <option value="完了">完了</option>
        <option value="再発防止">再発防止</option>
        <option value="クローズ">クローズ</option>
    </select>
    <input type="submit" name="bulkUpdate" value="実行" class="btn btn-primary"
        onclick="return bulkUpdateConfirm()"/>
    <span style="margin-left:20px;"></span>
    <input type="button" value="CSV出力" class="btn btn-primary"
        onclick="location.href='<%=request.getContextPath()%>/inc/list.do?csv=true'"/>
    <input type="button" value="PDF出力" class="btn btn-secondary"
        onclick="alert('PDF出力機能は開発中です。');"/>
    <input type="button" value="新規報告" class="btn btn-success"
        onclick="location.href='<%=request.getContextPath()%>/inc/create.do'"/>
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
function bulkUpdateConfirm() {
    var checkboxes = document.getElementsByName('selectedItems');
    var checked = false;
    for (var i = 0; i < checkboxes.length; i++) {
        if (checkboxes[i].checked) { checked = true; break; }
    }
    if (!checked) { alert('更新対象を選択してください。'); return false; }
    var statusSelect = document.getElementsByName('bulkStatus')[0];
    if (!statusSelect.value) { alert('更新先ステータスを選択してください。'); return false; }
    return confirm('選択した項目のステータスを「' + statusSelect.value + '」に変更します。よろしいですか？');
}
</script>
