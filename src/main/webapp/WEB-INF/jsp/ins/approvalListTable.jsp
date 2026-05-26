<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/app-common.tld" prefix="app" %>
<%@ page import="java.util.List, com.strutslab.dto.ExecResultDto, com.strutslab.util.HtmlUtil" %>
<%
    List<ExecResultDto> list = (List<ExecResultDto>) request.getAttribute("approvalList");
    if (list == null) list = java.util.Collections.emptyList();
    Integer totalCount = (Integer) request.getAttribute("totalCount");
    if (totalCount == null) totalCount = 0;
%>
<div class="list-table-container">
<form name="approvalForm" method="post" action="<%=request.getContextPath()%>/ins/approval/list.do">
<table class="list-table">
    <thead>
        <tr>
            <th><input type="checkbox" onclick="toggleAll(this)"/></th>
            <th>申請日時</th>
            <th>設備名</th>
            <th>申請者</th>
            <th>修正理由</th>
            <th>ステータス</th>
        </tr>
    </thead>
    <tbody>
<%
    if (list.isEmpty()) {
%>
        <tr><td colspan="6" style="text-align:center;">該当する承認データはありません。</td></tr>
<%
    } else {
        for (ExecResultDto dto : list) {
            int resultId = dto.getResultId();
            String executedDate = dto.getExecutedDate() != null ? dto.getExecutedDate() : "";
            String eqpName = dto.getEquipmentName() != null ? dto.getEquipmentName() : "";
            String executedBy = dto.getExecutedBy() != null ? dto.getExecutedBy() : "";
            String rejectReason = dto.getRejectReason();
            String reasonDisplay = rejectReason != null && rejectReason.length() > 20
                    ? rejectReason.substring(0, 20) + "…"
                    : (rejectReason != null ? rejectReason : "");
            String status = dto.getApprovalStatus() != null ? dto.getApprovalStatus() : "";
%>
        <tr>
            <td><input type="checkbox" name="selectedItems" value="<%= HtmlUtil.escape(resultId) %>"/></td>
            <td><a href="<%=request.getContextPath()%>/ins/exec/detail.do?resultId=<%= HtmlUtil.escape(resultId) %>"><%= HtmlUtil.escape(executedDate) %></a></td>
            <td><%= HtmlUtil.escape(eqpName) %></td>
            <td><%= HtmlUtil.escape(executedBy) %></td>
            <td><%= HtmlUtil.escape(reasonDisplay) %></td>
            <td><app:statusBadge status="<%= status %>"/></td>
        </tr>
<%
        }
    }
%>
    </tbody>
</table>

<div class="info-messages">全<%= totalCount %>件</div>

<!-- Reject reason textarea (hidden by default, shown via scriptlet toggle) -->
<%
    String showReject = request.getParameter("showReject");
    String rejectStyle = "true".equals(showReject) ? "" : "display:none;";
%>
<div id="rejectReasonArea" style="<%= HtmlUtil.escape(rejectStyle) %>">
<table class="form-table">
    <tr>
        <th>差戻理由 <span class="required">*</span></th>
        <td><textarea name="rejectReason" rows="3" cols="60" placeholder="差戻理由を入力してください。"></textarea></td>
    </tr>
</table>
</div>

<div class="button-area">
    <input type="submit" name="bulkApprove" value="一括承認" class="btn btn-success"
        onclick="return confirm('選択した項目を承認してもよろしいですか？');"/>
    <input type="button" value="一括差戻し" class="btn btn-danger"
        onclick="return showRejectReason();"/>
    <input type="submit" name="bulkReject" value="差戻し実行" class="btn btn-danger"
        onclick="return confirm('選択した項目を差戻してもよろしいですか？');"/>
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
function showRejectReason() {
    var area = document.getElementById('rejectReasonArea');
    if (area.style.display === 'none') {
        area.style.display = '';
        return false;
    }
    return confirm('選択した項目を差戻してもよろしいですか？');
}
</script>
