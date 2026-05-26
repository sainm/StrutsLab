<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ page import="com.strutslab.form.counter.CounterSearchForm, com.strutslab.util.HtmlUtil" %>
<%
    CounterSearchForm sf = (CounterSearchForm) request.getAttribute("counterSearchForm");
    if (sf == null) sf = new CounterSearchForm();
    String dateFrom = sf.getDateFrom() != null ? sf.getDateFrom() : "";
    String dateTo = sf.getDateTo() != null ? sf.getDateTo() : "";
    String person = sf.getPerson() != null ? sf.getPerson() : "";
    String status = sf.getStatus() != null ? sf.getStatus() : "";
    String priority = sf.getPriority() != null ? sf.getPriority() : "";
%>
<html:form action="/counter/list" method="post">
<div class="search-form">
<table class="form-table">
    <tr>
        <th>指示日（開始）</th>
        <td>
            <input type="text" name="dateFrom" value="<%= HtmlUtil.escape(dateFrom) %>" size="10" maxlength="8" placeholder="YYYYMMDD"/>
        </td>
        <th>指示日（終了）</th>
        <td>
            <input type="text" name="dateTo" value="<%= HtmlUtil.escape(dateTo) %>" size="10" maxlength="8" placeholder="YYYYMMDD"/>
        </td>
        <th>担当者</th>
        <td>
            <input type="text" name="person" value="<%= HtmlUtil.escape(person) %>" size="15" maxlength="30"/>
            <input type="button" value="選択" onclick="window.open('<%=request.getContextPath()%>/org/emp/popup.do?target=person', 'empPopup', 'width=600,height=500');"/>
        </td>
    </tr>
    <tr>
        <th>ステータス</th>
        <td>
            <select name="status">
                <option value="">-- 全て --</option>
                <option value="未了" <%= "未了".equals(status) ? "selected" : "" %>>未了</option>
                <option value="処理中" <%= "処理中".equals(status) ? "selected" : "" %>>処理中</option>
                <option value="完了" <%= "完了".equals(status) ? "selected" : "" %>>完了</option>
            </select>
        </td>
        <th>優先度</th>
        <td>
            <select name="priority">
                <option value="">-- 全て --</option>
                <option value="高" <%= "高".equals(priority) ? "selected" : "" %>>高</option>
                <option value="中" <%= "中".equals(priority) ? "selected" : "" %>>中</option>
                <option value="低" <%= "低".equals(priority) ? "selected" : "" %>>低</option>
            </select>
        </td>
        <td colspan="2"></td>
    </tr>
</table>
<div class="button-area">
    <html:submit property="search" value="検索" styleClass="btn btn-primary"/>
    <input type="button" value="クリア" class="btn btn-secondary"
        onclick="this.form.dateFrom.value='';this.form.dateTo.value='';this.form.person.value='';this.form.status.value='';this.form.priority.value='';this.form.submit();"/>
    <input type="button" value="新規登録" class="btn btn-success"
        onclick="location.href='<%=request.getContextPath()%>/counter/create.do';"/>
</div>
</div>
</html:form>
