<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%
    com.strutslab.form.mst.EqpForm eqpForm = (com.strutslab.form.mst.EqpForm) request.getAttribute("eqpForm");
    boolean isEdit = eqpForm != null && eqpForm.getEquipmentCode() != null && !eqpForm.getEquipmentCode().isEmpty();
    String ctx = request.getContextPath();
%>
<%
    String errMsg = (String) request.getAttribute("errorMessage");
    if (errMsg != null) {
%>
    <div style="color:#c33;background:#fcc;padding:8px;margin-bottom:12px;border:1px solid #c33;">
        <%= errMsg %>
    </div>
<%
    }
%>
<html:form action="/mst/eqp/save" method="post" enctype="multipart/form-data">
<html:hidden property="method" value="save"/>
<% if (!isEdit) { %>
<html:hidden property="equipmentCode"/>
<% } %>

<!-- Section 1: 基本情報 -->
<h2>基本情報</h2>
<table class="form-table">
    <tr>
        <th>設備コード</th>
        <td>
            <% if (isEdit) { %>
                <html:text property="equipmentCode" readonly="true" styleClass="readonly" style="width:120px;background:#eee;"/>
            <% } else { %>
                <span style="color:#999;">（自動採番）</span>
            <% } %>
        </td>
    </tr>
    <tr>
        <th>設備名称 <span style="color:#c33;">*</span></th>
        <td><html:text property="equipmentName" style="width:300px;"/></td>
    </tr>
    <tr>
        <th>設備種別 <span style="color:#c33;">*</span></th>
        <td>
            <html:select property="equipmentType">
                <html:option value="">-- 選択 --</html:option>
                <html:option value="変圧器">変圧器</html:option>
                <html:option value="遮断器">遮断器</html:option>
                <html:option value="断路器">断路器</html:option>
                <html:option value="避雷器">避雷器</html:option>
                <html:option value="コンデンサ">コンデンサ</html:option>
                <html:option value="電線路">電線路</html:option>
                <html:option value="開閉器">開閉器</html:option>
                <html:option value="計器用変成器">計器用変成器</html:option>
                <html:option value="その他">その他</html:option>
            </html:select>
        </td>
    </tr>
    <tr>
        <th>ステータス</th>
        <td>
            <html:select property="status">
                <html:option value="運用中">運用中</html:option>
                <html:option value="停止中">停止中</html:option>
                <html:option value="廃止">廃止</html:option>
            </html:select>
        </td>
    </tr>
</table>

<!-- Section 2: 電気仕様 -->
<h2>電気仕様</h2>
<table class="form-table">
    <tr>
        <th>電圧階級</th>
        <td>
            <html:select property="voltageLevel">
                <html:option value="">-- 選択 --</html:option>
                <html:option value="6.6kV">6.6kV</html:option>
                <html:option value="22kV">22kV</html:option>
                <html:option value="33kV">33kV</html:option>
                <html:option value="66kV">66kV</html:option>
                <html:option value="77kV">77kV</html:option>
                <html:option value="110kV">110kV</html:option>
                <html:option value="154kV">154kV</html:option>
                <html:option value="220kV">220kV</html:option>
                <html:option value="275kV">275kV</html:option>
                <html:option value="500kV">500kV</html:option>
            </html:select>
        </td>
    </tr>
    <tr>
        <th>定格容量</th>
        <td><html:text property="ratedCapacity" style="width:120px;"/> kVA</td>
    </tr>
    <tr>
        <th>定格電流</th>
        <td><html:text property="ratedCurrent" style="width:120px;"/> A</td>
    </tr>
    <tr>
        <th>周波数</th>
        <td>
            <html:select property="frequency">
                <html:option value="">-- 選択 --</html:option>
                <html:option value="50Hz">50Hz</html:option>
                <html:option value="60Hz">60Hz</html:option>
            </html:select>
        </td>
    </tr>
</table>

<!-- Section 3: 設置場所 -->
<h2>設置場所</h2>
<table class="form-table">
    <tr>
        <th>親設備コード</th>
        <td>
            <html:text property="parentEquipmentCode" readonly="true" style="width:120px;background:#eee;"/>
            <input type="button" value="選択" onclick="window.open('<%=ctx%>/mst/eqp/popup.do','eqpPopup','width=800,height=600,scrollbars=yes');"/>
        </td>
    </tr>
    <tr>
        <th>設置年月</th>
        <td><html:text property="installDate" style="width:100px;"/> YYYYMM</td>
    </tr>
    <tr>
        <th>所在地</th>
        <td><html:text property="locationAddress" style="width:350px;"/></td>
    </tr>
    <tr>
        <th>座標</th>
        <td><html:text property="coordinates" style="width:250px;"/></td>
    </tr>
</table>

<!-- Section 4: 保全区分 -->
<h2>保全区分</h2>
<table class="form-table">
    <tr>
        <th>保全区分</th>
        <td>
            <html:select property="maintenanceRank">
                <html:option value="">-- 選択 --</html:option>
                <html:option value="S">S</html:option>
                <html:option value="A">A</html:option>
                <html:option value="B">B</html:option>
                <html:option value="C">C</html:option>
            </html:select>
        </td>
    </tr>
    <tr>
        <th>点検周期</th>
        <td><html:text property="inspectionInterval" style="width:80px;"/> ヶ月</td>
    </tr>
    <tr>
        <th>前回点検日</th>
        <td><html:text property="lastInspectionDate" style="width:100px;"/> YYYYMMDD</td>
    </tr>
    <tr>
        <th>次回点検日</th>
        <td><html:text property="nextInspectionDate" style="width:100px;"/> YYYYMMDD</td>
    </tr>
</table>

<!-- Section 5: 備考 -->
<h2>備考</h2>
<table class="form-table">
    <tr>
        <th>備考</th>
        <td><html:textarea property="note" style="width:350px;height:80px;"/></td>
    </tr>
    <tr>
        <th>添付ファイル</th>
        <td><html:file property="attachFile" style="width:300px;"/></td>
    </tr>
</table>

<!-- Buttons -->
<div style="text-align:center;margin-top:16px;">
    <html:submit value="保存" style="width:100px;"/>
    <input type="button" value="キャンセル" class="btn btn-back" style="width:100px;" onclick="history.back();"/>
</div>

</html:form>
