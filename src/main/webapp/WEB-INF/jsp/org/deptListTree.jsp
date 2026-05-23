<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ page import="java.util.List, com.strutslab.dto.DeptDto" %>
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
            // Recursive rendering helper function
            java.util.function.BiConsumer<Integer, List<DeptDto>> renderLevel = new java.util.function.BiConsumer<Integer, List<DeptDto>>() {
                public void accept(Integer level, List<DeptDto> items) {
                    for (DeptDto d : items) {
                        String code = d.getDeptCode() != null ? d.getDeptCode() : "";
                        String name = d.getDeptName() != null ? d.getDeptName() : "";
                        String type = d.getDeptType() != null ? d.getDeptType() : "";
                        String start = d.getStartDate() != null ? d.getStartDate() : "";
                        String end = d.getEndDate() != null ? d.getEndDate() : "";
                        String tel = d.getTel() != null ? d.getTel() : "";
    %>
        <tr>
            <td><a href="<%=request.getContextPath()%>/org/dept/edit.do?deptCode=<%= java.net.URLEncoder.encode(code, "UTF-8") %>"><%= code %></a></td>
            <td><% for (int i = 0; i < level; i++) { %><span class="tree-indent">&nbsp;&nbsp;</span><% } %><%= name %></td>
            <td><%= type %></td>
            <td>Lv.<%= d.getDeptLevel() %></td>
            <td><%= start %></td>
            <td><%= end %></td>
            <td><%= tel %></td>
            <td><a href="<%=request.getContextPath()%>/org/dept/save.do?method=new&parentDeptCode=<%= java.net.URLEncoder.encode(code, "UTF-8") %>" class="btn btn-small">+子部署追加</a></td>
        </tr>
    <%
                        if (d.getChildren() != null && !d.getChildren().isEmpty()) {
                            accept(level + 1, d.getChildren());
                        }
                    }
                }
            };
            renderLevel.accept(0, tree);
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
