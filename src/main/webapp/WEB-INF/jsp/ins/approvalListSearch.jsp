<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ page import="java.util.List, com.strutslab.dto.EmpDto, com.strutslab.form.ins.ApprovalForm, com.strutslab.util.HtmlUtil" %>
<%
    ApprovalForm af = (ApprovalForm) request.getAttribute("approvalForm");
    if (af == null) af = new ApprovalForm();
    String dateFrom = af.getDateFrom() != null ? af.getDateFrom() : "";
    String dateTo = af.getDateTo() != null ? af.getDateTo() : "";
    String team = af.getTeam() != null ? af.getTeam() : "";
    String status = af.getStatus() != null ? af.getStatus() : "";
    List<EmpDto> teamList = (List<EmpDto>) request.getAttribute("teamList");
%>
<html:form action="/ins/approval/list" method="post">
<div class="search-form">
<table class="form-table">
    <tr>
        <th>対象期間（開始）</th>
        <td>
            <input type="text" name="dateFrom" value="<%= HtmlUtil.escape(dateFrom) %>" size="10" maxlength="8" placeholder="YYYYMMDD">
        </td>
        <th>対象期間（終了）</th>
        <td>
            <input type="text" name="dateTo" value="<%= HtmlUtil.escape(dateTo) %>" size="10" maxlength="8" placeholder="YYYYMMDD">
        </td>
    </tr>
    <tr>
        <th>チーム</th>
        <td>
            <select name="team">
                <option value="">-- 全て --</option>
<%
    if (teamList != null) {
        for (EmpDto emp : teamList) {
            String code = emp.getEmpNo();
            String name = emp.getName();
            if (code == null) continue;
            String selected = code.equals(team) ? "selected" : "";
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
            <select name="status">
                <option value="">-- 全て --</option>
                <option value="申請中" <%= "申請中".equals(status) ? "selected" : "" %>>申請中</option>
                <option value="承認済" <%= "承認済".equals(status) ? "selected" : "" %>>承認済</option>
                <option value="差戻" <%= "差戻".equals(status) ? "selected" : "" %>>差戻</option>
            </select>
        </td>
    </tr>
</table>
<div class="button-area">
    <html:submit property="search" value="検索" styleClass="btn btn-primary"/>
    <input type="button" value="クリア" class="btn btn-secondary"
        onclick="this.form.dateFrom.value='';this.form.dateTo.value='';this.form.team.value='';this.form.status.value='';this.form.submit();"/>
</div>
</div>
</html:form>
