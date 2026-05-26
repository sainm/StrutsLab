<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ page import="java.util.List, com.strutslab.dto.EmpDto, com.strutslab.util.HtmlUtil" %>
<%
    List<EmpDto> list = (List<EmpDto>) request.getAttribute("empList");
    if (list == null) list = java.util.Collections.emptyList();
    String ctx = request.getContextPath();
%>
<div class="list-table-container">
<form name="empListForm" method="post" action="<%=ctx%>/org/emp/list.do">
<table class="list-table">
    <thead>
        <tr>
            <th><input type="checkbox" onclick="toggleAll(this)"/></th>
            <th>社員番号</th>
            <th>氏名</th>
            <th>部署</th>
            <th>職位</th>
            <th>保有資格</th>
            <th>認定期限</th>
            <th>ステータス</th>
        </tr>
    </thead>
    <tbody>
    <%
        if (list.isEmpty()) {
    %>
        <tr><td colspan="8" style="text-align:center;">検索結果がありません。</td></tr>
    <%
        } else {
            for (EmpDto emp : list) {
                String no = emp.getEmpNo() != null ? emp.getEmpNo() : "";
                String name = emp.getName() != null ? emp.getName() : "";
                String dept = emp.getDeptCode() != null ? emp.getDeptCode() : "";
                String pos = emp.getPosition() != null ? emp.getPosition() : "";
                String expire = emp.getInspectionCertExpire() != null ? emp.getInspectionCertExpire() : "";
                boolean isLocked = emp.getIsLocked() != null && emp.getIsLocked();
                boolean isExpired = expire.length() == 8 && expire.compareTo(new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date())) < 0;
    %>
        <tr>
            <td><input type="checkbox" name="qualifications" value="<%= HtmlUtil.escape(no) %>"/></td>
            <td><a href="<%=ctx%>/org/emp/save.do?empNo=<%= java.net.URLEncoder.encode(no, "UTF-8") %>"><%= HtmlUtil.escape(no) %></a></td>
            <td><%= HtmlUtil.escape(name) %></td>
            <td><%= HtmlUtil.escape(dept) %></td>
            <td><%= HtmlUtil.escape(pos) %></td>
            <td>-</td>
            <td <% if (isExpired) { %>style="color:red;font-weight:bold;"<% } %>><%= HtmlUtil.escape(expire) %></td>
            <td><%= HtmlUtil.escape(isLocked ? "ロック中" : "有効") %></td>
        </tr>
    <%
            }
        }
    %>
    </tbody>
</table>

<jsp:include page="/WEB-INF/jsp/common/paging.jsp"/>

<div class="button-area">
    <input type="button" value="CSV出力" class="btn btn-primary"
        onclick="location.href='<%=ctx%>/org/emp/list.do?csv=true'"/>
    <input type="button" value="一括ロック" class="btn btn-warning"
        onclick="return bulkAction('lock');"/>
    <input type="button" value="一括解除" class="btn btn-secondary"
        onclick="return bulkAction('unlock');"/>
    <input type="button" value="新規登録" class="btn btn-success"
        onclick="location.href='<%=ctx%>/org/emp/save.do?method=new'"/>
</div>
</form>
</div>
<script>
function toggleAll(src) {
    var checkboxes = document.getElementsByName('qualifications');
    for (var i = 0; i < checkboxes.length; i++) {
        checkboxes[i].checked = src.checked;
    }
}
function bulkAction(action) {
    var form = document.forms['empListForm'];
    form.action = '<%=ctx%>/org/emp/list.do?' + action + '=true';
    form.submit();
    return false;
}
</script>
