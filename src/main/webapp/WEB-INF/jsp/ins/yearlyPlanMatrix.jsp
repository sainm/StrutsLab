<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ page import="java.util.List, java.util.Map, com.strutslab.dto.EqpDto, com.strutslab.dto.PlanCellDto" %>
<%
    List<EqpDto> eqpList = (List<EqpDto>) request.getAttribute("eqpList");
    if (eqpList == null) eqpList = new java.util.ArrayList<EqpDto>();
    List<String> months = (List<String>) request.getAttribute("months");
    if (months == null) months = java.util.Arrays.asList("4月","5月","6月","7月","8月","9月","10月","11月","12月","1月","2月","3月");
    Map<String, Map<Integer, PlanCellDto>> yearlyMatrix = (Map<String, Map<Integer, PlanCellDto>>) request.getAttribute("yearlyMatrix");
    if (yearlyMatrix == null) yearlyMatrix = new java.util.LinkedHashMap<String, Map<Integer, PlanCellDto>>();
    int[] monthNums = {4,5,6,7,8,9,10,11,12,1,2,3};
    String ctx = request.getContextPath();
%>
<div class="list-table-container">
<table class="list-table">
    <thead>
        <tr>
            <th>設備名</th>
<%
    for (String m : months) {
%>
            <th><%= m %></th>
<%
    }
%>
        </tr>
    </thead>
    <tbody>
<%
    if (eqpList.isEmpty()) {
%>
        <tr><td colspan="13" style="text-align:center;">該当する設備がありません。</td></tr>
<%
    } else {
        for (EqpDto eqp : eqpList) {
            String code = eqp.getEquipmentCode() != null ? eqp.getEquipmentCode() : "";
            String name = eqp.getEquipmentName() != null ? eqp.getEquipmentName() : "";
            Map<Integer, PlanCellDto> monthMap = yearlyMatrix.get(code);
%>
        <tr>
            <td style="white-space:nowrap;font-weight:bold;"><%= name %></td>
<%
            for (int mn : monthNums) {
                PlanCellDto cell = (monthMap != null) ? monthMap.get(mn) : null;
                int planned = (cell != null) ? cell.getPlannedCount() : 0;
                int actual = (cell != null) ? cell.getActualCount() : 0;
                boolean alert = actual < planned;
%>
            <td class="<%= alert ? "cell-warning" : "" %>" style="<%= alert ? "background:#fcc;" : "" %>text-align:center;">
                計画:<%= planned %> 実績:<%= actual %>
            </td>
<%
            }
%>
        </tr>
<%
        }
    }
%>
    </tbody>
</table>

<div class="button-area">
    <input type="button" value="CSV出力" class="btn btn-primary"
        onclick="location.href='<%= ctx %>/ins/yearly/plan.do?csv=true'"/>
    <input type="button" value="計画ロック" class="btn btn-warning"
        onclick="if(confirm('この年度の計画をロックしますか？')){location.href='<%= ctx %>/ins/yearly/plan.do?lock=true';}"/>
    <input type="button" value="ロック解除" class="btn btn-secondary"
        onclick="if(confirm('ロックを解除しますか？')){location.href='<%= ctx %>/ins/yearly/plan.do?unlock=true';}"/>
</div>
</div>
