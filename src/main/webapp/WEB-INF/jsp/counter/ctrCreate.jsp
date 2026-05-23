<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/app-common.tld" prefix="app" %>
<%@ page import="com.strutslab.form.counter.CounterForm" %>
<%
    CounterForm cf = (CounterForm) request.getAttribute("counterForm");
    if (cf == null) cf = new CounterForm();

    String orderNo = cf.getOrderNo() != null ? cf.getOrderNo() : "(自動採番)";
    String incidentNo = cf.getIncidentNo() != null ? cf.getIncidentNo() : "";
    String orderDate = cf.getOrderDate() != null ? cf.getOrderDate() : "";
    String issuer = cf.getIssuer() != null ? cf.getIssuer() : "";
    String overallDeadline = cf.getOverallDeadline() != null ? cf.getOverallDeadline() : "";
    String overallPriority = cf.getOverallPriority() != null ? cf.getOverallPriority() : "";

    int detailCount = cf.getDetailCount();
    if (detailCount == 0) detailCount = 1;
%>
<html:form action="/counter/create" method="post">
<input type="hidden" name="method" value="save"/>

<app:sectionHeader title="対応指示登録" anchorId="ctrCreate"/>

<div class="form-section">
<table class="form-table">
    <tr>
        <th>指示番号</th>
        <td><strong><%= orderNo %></strong></td>
        <th>関連異常報告</th>
        <td>
            <% if (incidentNo != null && !incidentNo.isEmpty()) { %>
                <a href="<%=request.getContextPath()%>/inc/detail.do?incidentNo=<%= incidentNo %>"><%= incidentNo %></a>
            <% } else { %>
                <input type="text" name="incidentNo" value="<%= incidentNo %>" size="15" maxlength="20"/>
            <% } %>
        </td>
    </tr>
    <tr>
        <th>指示日 <span class="required">*</span></th>
        <td>
            <input type="text" name="orderDate" value="<%= orderDate %>" size="10" maxlength="8" placeholder="YYYYMMDD"/>
            <input type="button" value="📅" onclick="showDatePicker(this, 'orderDate')"/>
        </td>
        <th>指示者 <span class="required">*</span></th>
        <td>
            <input type="text" name="issuer" value="<%= issuer %>" size="20" maxlength="50"/>
            <input type="button" value="選択" onclick="window.open('<%=request.getContextPath()%>/org/emp/popup.do?target=issuer', 'empPopup', 'width=600,height=500');"/>
        </td>
    </tr>
    <tr>
        <th>期限 <span class="required">*</span></th>
        <td>
            <input type="text" name="overallDeadline" value="<%= overallDeadline %>" size="10" maxlength="8" placeholder="YYYYMMDD"/>
            <input type="button" value="📅" onclick="showDatePicker(this, 'overallDeadline')"/>
        </td>
        <th>優先度 <span class="required">*</span></th>
        <td>
            <select name="overallPriority">
                <option value="">-- 選択 --</option>
                <option value="高" <%= "高".equals(overallPriority) ? "selected" : "" %>>高</option>
                <option value="中" <%= "中".equals(overallPriority) ? "selected" : "" %>>中</option>
                <option value="低" <%= "低".equals(overallPriority) ? "selected" : "" %>>低</option>
            </select>
        </td>
    </tr>
</table>
</div>

<app:sectionHeader title="指示明細" anchorId="ctrDetails"/>
<div class="form-section">
<table class="list-table" id="detailTable">
    <thead>
        <tr>
            <th>No.</th>
            <th>指示内容 <span class="required">*</span></th>
            <th>担当者 <span class="required">*</span></th>
            <th>期限</th>
            <th>優先度</th>
            <th>操作</th>
        </tr>
    </thead>
    <tbody>
<%
    for (int i = 0; i < detailCount; i++) {
        String wc = cf.getDetailWorkContent(i);
        if (wc == null) wc = "";
        String person = cf.getDetailPerson(i);
        if (person == null) person = "";
        String dl = cf.getDetailDeadline(i);
        if (dl == null) dl = "";
        String pri = cf.getDetailPriority(i);
        if (pri == null) pri = "";
%>
        <tr>
            <td style="text-align:center;"><%= i + 1 %></td>
            <td>
                <input type="text" name="detailWorkContents[<%= i %>]" value="<%= wc %>" size="40" maxlength="200"/>
            </td>
            <td>
                <input type="text" name="detailPersons[<%= i %>]" value="<%= person %>" size="15" maxlength="30"/>
                <input type="button" value="選択" onclick="openEmpPopup(<%= i %>);"/>
            </td>
            <td>
                <input type="text" name="detailDeadlines[<%= i %>]" value="<%= dl %>" size="10" maxlength="8" placeholder="YYYYMMDD"/>
                <input type="button" value="📅" onclick="showDatePicker(this, 'detailDeadlines[<%= i %>]')"/>
            </td>
            <td>
                <select name="detailPriorities[<%= i %>]">
                    <option value="">--</option>
                    <option value="高" <%= "高".equals(pri) ? "selected" : "" %>>高</option>
                    <option value="中" <%= "中".equals(pri) ? "selected" : "" %>>中</option>
                    <option value="低" <%= "低".equals(pri) ? "selected" : "" %>>低</option>
                </select>
            </td>
            <td style="text-align:center;">
                <input type="submit" name="method" value="delRow" class="btn btn-danger btn-sm"
                    onclick="this.form.method.value='delRow';document.getElementById('delIndex').value='<%= i %>';"
                    <%= detailCount <= 1 ? "disabled" : "" %>/>
            </td>
        </tr>
<%
    }
%>
    </tbody>
</table>
<input type="hidden" id="delIndex" name="index" value=""/>
<div class="button-area">
    <input type="submit" name="method" value="addRow" class="btn btn-secondary"
        onclick="this.form.method.value='addRow';"/>
    <input type="button" value="+ 行追加" class="btn btn-secondary"
        onclick="this.form.method.value='addRow';this.form.submit();"/>
</div>
</div>

<div class="button-area">
    <input type="submit" name="method" value="save" class="btn btn-primary"
        onclick="this.form.method.value='save';return confirm('対応指示を登録してもよろしいですか？');"/>
    <input type="submit" name="method" value="temporary" class="btn btn-secondary"
        onclick="this.form.method.value='temporary';"/>
    <input type="button" value="キャンセル" class="btn btn-secondary"
        onclick="location.href='<%=request.getContextPath()%>/counter/list.do';"/>
</div>
</html:form>

<script>
function openEmpPopup(rowIdx) {
    var targetField = 'detailPersons[' + rowIdx + ']';
    window.open('<%=request.getContextPath()%>/org/emp/popup.do?target=' + targetField,
        'empPopup', 'width=600,height=500');
}

function showDatePicker(btn, targetField) {
    // Simple date picker placeholder - opens a popup or uses native date
    var dateStr = prompt('日付を入力してください (YYYYMMDD):', '');
    if (dateStr) {
        var form = btn.form;
        var elements = form.getElementsByName(targetField);
        if (elements.length > 0) {
            elements[0].value = dateStr;
        }
    }
}
</script>
