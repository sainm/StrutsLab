<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ page import="java.util.List, com.strutslab.dto.PartsUsageDto" %>
<%
    List<PartsUsageDto> list = (List<PartsUsageDto>) request.getAttribute("usageList");
    if (list == null) list = java.util.Collections.emptyList();
    String ctx = request.getContextPath();
%>
<div class="list-table-container">
<table class="list-table">
    <thead>
        <tr>
            <th>使用日</th>
            <th>部品コード</th>
            <th>部品名</th>
            <th>設備</th>
            <th>数量</th>
            <th>使用前在庫</th>
            <th>使用后在庫</th>
            <th>用途</th>
            <th>実行者</th>
        </tr>
    </thead>
    <tbody>
    <%
        if (list.isEmpty()) {
    %>
        <tr><td colspan="9" style="text-align:center;">検索結果がありません。</td></tr>
    <%
        } else {
            for (PartsUsageDto u : list) {
                String date = u.getUsageDate() != null ? u.getUsageDate() : "";
                String code = u.getPartCode() != null ? u.getPartCode() : "";
                String name = u.getPartName() != null ? u.getPartName() : "";
                String eqp = u.getEquipmentName() != null ? u.getEquipmentName() : (u.getEquipmentCode() != null ? u.getEquipmentCode() : "");
                int qty = u.getQuantity() != null ? u.getQuantity() : 0;
                Integer sb = u.getStockBefore();
                Integer sa = u.getStockAfter();
                String purpose = u.getPurpose() != null ? u.getPurpose() : "";
                String usedBy = u.getUsedBy() != null ? u.getUsedBy() : "";
                boolean discrepancy = u.getNote() != null && "在庫数不整合".equals(u.getNote());
    %>
        <tr<% if (discrepancy) { %> style="background:#fcc;"<% } %>>
            <td><%= date %></td>
            <td><%= code %></td>
            <td><%= name %></td>
            <td><%= eqp %></td>
            <td style="text-align:right;"><%= qty %></td>
            <td style="text-align:right;"><%= sb != null ? sb : "" %></td>
            <td style="text-align:right;"><%= sa != null ? sa : "" %></td>
            <td><%= purpose %></td>
            <td><%= usedBy %></td>
        </tr>
    <%
            }
        }
    %>
    </tbody>
</table>

<jsp:include page="/WEB-INF/jsp/common/paging.jsp"/>

<div class="button-area">
    <input type="button" value="部品一覧に戻る" class="btn btn-back"
        onclick="location.href='<%=ctx%>/parts/list.do'"/>
</div>
</div>
