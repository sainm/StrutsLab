<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ page import="java.util.List, com.strutslab.dto.DeptDto, com.strutslab.util.HtmlUtil" %>
<%
    List<DeptDto> tree = (List<DeptDto>) request.getAttribute("deptTree");
    if (tree == null) tree = java.util.Collections.emptyList();
%>
<div class="list-table-container">
<form name="deptListForm" method="post" action="<%=request.getContextPath()%>/org/dept/list.do">
<table class="list-table">
    <thead>
        <tr>
            <th>部署コード</th>
            <th>部署名</th>
            <th>部署種別</th>
            <th>階層</th>
            <th>開始日</th>
            <th>終了日</th>
            <th>電話番号</th>
            <th>操作</th>
        </tr>
    </thead>
    <tbody>
    <%
        if (tree.isEmpty()) {
    %>
        <tr><td colspan="8" style="text-align:center;">検索結果がありません。</td></tr>
    <%
        } else {
            // Iterative tree traversal using a Stack (avoids anonymous inner class)
            java.util.Stack<Object[]> stack = new java.util.Stack<Object[]>();
            for (int i = tree.size() - 1; i >= 0; i--) {
                stack.push(new Object[]{Integer.valueOf(0), tree.get(i)});
            }
            while (!stack.isEmpty()) {
                Object[] frame = stack.pop();
                int level = ((Integer) frame[0]).intValue();
                DeptDto d = (DeptDto) frame[1];
                String code = d.getDeptCode() != null ? d.getDeptCode() : "";
                String name = d.getDeptName() != null ? d.getDeptName() : "";
                String type = d.getDeptType() != null ? d.getDeptType() : "";
                String start = d.getStartDate() != null ? d.getStartDate() : "";
                String end = d.getEndDate() != null ? d.getEndDate() : "";
                String tel = d.getTel() != null ? d.getTel() : "";
                // Push children in reverse order so first child appears first
                if (d.getChildren() != null && !d.getChildren().isEmpty()) {
                    for (int i = d.getChildren().size() - 1; i >= 0; i--) {
                        stack.push(new Object[]{Integer.valueOf(level + 1), d.getChildren().get(i)});
                    }
                }
    %>
        <tr>
            <td><a href="<%=request.getContextPath()%>/org/dept/edit.do?deptCode=<%= java.net.URLEncoder.encode(code, "UTF-8") %>"><%= HtmlUtil.escape(code) %></a></td>
            <td><% for (int i = 0; i < level; i++) { %><span class="tree-indent">&nbsp;&nbsp;</span><% } %><%= HtmlUtil.escape(name) %></td>
            <td><%= HtmlUtil.escape(type) %></td>
            <td>Lv.<%= HtmlUtil.escape(String.valueOf(d.getDeptLevel())) %></td>
            <td><%= HtmlUtil.escape(start) %></td>
            <td><%= HtmlUtil.escape(end) %></td>
            <td><%= HtmlUtil.escape(tel) %></td>
            <td><a href="<%=request.getContextPath()%>/org/dept/save.do?method=new&parentDeptCode=<%= java.net.URLEncoder.encode(code, "UTF-8") %>" class="btn btn-small">+子部署追加</a></td>
        </tr>
    <%
            }
        }
    %>
    </tbody>
</table>

<div class="button-area">
    <input type="button" value="CSV出力" class="btn btn-primary"
        onclick="location.href='<%=request.getContextPath()%>/org/dept/list.do?csv=true'"/>
    <input type="button" value="新規登録" class="btn btn-success"
        onclick="location.href='<%=request.getContextPath()%>/org/dept/save.do?method=new'"/>
</div>
</form>
</div>
