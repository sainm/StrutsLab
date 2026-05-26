<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ page import="java.util.List, com.strutslab.dto.PartsDto, com.strutslab.util.HtmlUtil" %>
<%
    List<PartsDto> list = (List<PartsDto>) request.getAttribute("partsList");
    if (list == null) list = java.util.Collections.emptyList();
    String ctx = request.getContextPath();
%>
<div class="list-table-container">
<form name="partsListForm" method="post" action="<%=ctx%>/parts/list.do">
<table class="list-table">
    <thead>
        <tr>
            <th>部品コード</th>
            <th>部品名</th>
            <th>部品種別</th>
            <th>単位</th>
            <th>発注点</th>
            <th>現在庫</th>
            <th>在庫状態</th>
            <th>単価</th>
            <th>仕入先</th>
        </tr>
    </thead>
    <tbody>
    <%
        if (list.isEmpty()) {
    %>
        <tr><td colspan="9" style="text-align:center;">検索結果がありません。</td></tr>
    <%
        } else {
            for (PartsDto p : list) {
                String code = p.getPartCode() != null ? p.getPartCode() : "";
                String name = p.getPartName() != null ? p.getPartName() : "";
                String type = p.getPartType() != null ? p.getPartType() : "";
                String unit = p.getUnit() != null ? p.getUnit() : "";
                Integer op = p.getOrderPoint();
                Integer stock = p.getCurrentStock();
                Integer price = p.getUnitPrice();
                String supplier = p.getSupplier() != null ? p.getSupplier() : "";
                java.util.Map stockBadgeMap = (java.util.Map) request.getAttribute("stockBadgeMap");
                String badgeStatus = stockBadgeMap != null ? (String) stockBadgeMap.get(code) : "ok";
                if (badgeStatus == null) badgeStatus = "ok";
                String badgeColor = "";
                String badgeText = "";
                if ("out".equals(badgeStatus)) { badgeColor = "#c33"; badgeText = "在庫切れ"; }
                else if ("low".equals(badgeStatus)) { badgeColor = "#c90"; badgeText = "要注意"; }
                else { badgeColor = "#090"; badgeText = "十分"; }
    %>
        <tr>
            <td><a href="<%=ctx%>/parts/save.do?partCode=<%= java.net.URLEncoder.encode(code, "UTF-8") %>"><%= HtmlUtil.escape(code) %></a></td>
            <td><%= HtmlUtil.escape(name) %></td>
            <td><%= HtmlUtil.escape(type) %></td>
            <td><%= HtmlUtil.escape(unit) %></td>
            <td style="text-align:right;"><%= HtmlUtil.escape(op != null ? String.valueOf(op) : "") %></td>
            <td style="text-align:right;"><%= HtmlUtil.escape(stock != null ? String.valueOf(stock) : "0") %></td>
            <td><span style="display:inline-block;padding:2px 8px;border-radius:4px;color:#fff;background:<%= HtmlUtil.escape(badgeColor) %>;"><%= HtmlUtil.escape(badgeText) %></span></td>
            <td style="text-align:right;"><%= price != null ? String.format("%,d", price) : "" %></td>
            <td><%= HtmlUtil.escape(supplier) %></td>
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
        onclick="location.href='<%=ctx%>/parts/list.do?csv=true'"/>
    <input type="button" value="使用実績" class="btn btn-info"
        onclick="location.href='<%=ctx%>/parts/usage.do'"/>
    <input type="button" value="新規登録" class="btn btn-success"
        onclick="location.href='<%=ctx%>/parts/save.do?method=new'"/>
</div>
</form>
</div>
