<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%
    com.strutslab.form.mst.CheckItemForm form = (com.strutslab.form.mst.CheckItemForm)
        request.getAttribute("chkItemForm");
    boolean isEdit = form != null && form.getTemplateId() > 0;
    String ctx = request.getContextPath();
    String errMsg = (String) request.getAttribute("errorMessage");
%>
<% if (errMsg != null) { %>
    <div style="color:#c33;background:#fcc;padding:8px;margin-bottom:12px;border:1px solid #c33;">
        <%= errMsg %>
    </div>
<% } %>

<html:form action="/mst/chkitem/save" method="post">
<html:hidden property="method" value="save"/>
<html:hidden property="templateId"/>

<!-- ===== Section 1: Template Info ===== -->
<h2>テンプレート基本情報</h2>
<table class="form-table">
    <tr>
        <th>テンプレート名 <span style="color:#c33;">*</span></th>
        <td><html:text property="templateName" style="width:300px;"/></td>
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
        <th>点検種別 <span style="color:#c33;">*</span></th>
        <td>
            <html:select property="inspectionKind">
                <html:option value="">-- 選択 --</html:option>
                <html:option value="巡視">巡視</html:option>
                <html:option value="定期">定期</html:option>
                <html:option value="臨時">臨時</html:option>
                <html:option value="詳細">詳細</html:option>
            </html:select>
        </td>
    </tr>
</table>

<!-- ===== Section 2: 3-level Tree ===== -->
<h2>点検項目（3階層ツリー）</h2>
<div id="treeArea" style="margin-left:8px;">
<%
    String[] c1Names = form != null ? form.getCat1Names() : null;
    if (c1Names != null) {
        int itemIdx = 0;
        for (int ci = 0; ci < c1Names.length; ci++) {
            String ciStr = String.valueOf(ci);
%>
    <!-- 大分類 <%= ci + 1 %> -->
    <div class="cat1-row" style="margin-bottom:6px;padding:6px;border:1px solid #ccc;background:#fafafa;">
        <strong>大分類<%= ci + 1 %>:</strong>
        <html:text property='<%= "cat1Names[" + ci + "]" %>' style="width:200px;"/>
        <html:hidden property='<%= "cat1Ids[" + ci + "]" %>'/>
        <input type="button" value="+中分類追加"
               onclick="document.forms[0].method.value='addCat2';
                        var inp=document.createElement('input');inp.type='hidden';inp.name='cat1Idx';inp.value='<%= ciStr %>';
                        document.forms[0].appendChild(inp);document.forms[0].submit();"/>
        <input type="button" value="削除"
               onclick="if(confirm('この大分類を削除しますか？')){/* TODO: server-side delete for cat1 */}" style="color:#c33;"/>
    </div>
<%
            String[][] c2All = form.getCat2Names();
            String[] c2Row = (c2All != null && ci < c2All.length) ? c2All[ci] : null;
            if (c2Row != null) {
                for (int cj = 0; cj < c2Row.length; cj++) {
                    String ci2Str = String.valueOf(ci);
                    String cjStr = String.valueOf(cj);
%>
    <!-- 中分類 <%= ci + 1 %>-<%= cj + 1 %> -->
    <div class="cat2-row" style="margin-left:24px;margin-bottom:4px;padding:4px;border:1px solid #ddd;background:#f5f5f5;">
        <strong>中分類<%= ci + 1 %>-<%= cj + 1 %>:</strong>
        <html:text property='<%= "cat2Names[" + ci + "][" + cj + "]" %>' style="width:180px;"/>
        <input type="button" value="+項目追加"
               onclick="document.forms[0].method.value='addItem';
                        var f=document.forms[0];
                        var i1=document.createElement('input');i1.type='hidden';i1.name='cat1Idx';i1.value='<%= ci2Str %>';
                        var i2=document.createElement('input');i2.type='hidden';i2.name='cat2Idx';i2.value='<%= cjStr %>';
                        f.appendChild(i1);f.appendChild(i2);f.submit();"/>
        <input type="button" value="削除" style="color:#c33;"
               onclick="if(confirm('この中分類を削除しますか？')){/* TODO: server-side delete for cat2 */}"/>
    </div>
<%
                    // Render items belonging to this cat2 (using tracking arrays)
                    int[] ic1 = form.getItemCat1Idxs();
                    int[] ic2 = form.getItemCat2Idxs();
                    while (itemIdx < (form.getItemNames() != null ? form.getItemNames().length : 0)
                            && ic1 != null && ic2 != null
                            && itemIdx < ic1.length && itemIdx < ic2.length
                            && ic1[itemIdx] == ci && ic2[itemIdx] == cj) {
                        String idxStr = String.valueOf(itemIdx);
%>
    <div class="item-row" style="margin-left:48px;margin-bottom:2px;padding:2px;">
        項目<%= ci + 1 %>-<%= cj + 1 %>-<%= (itemIdx + 1) %>:
        項目名<html:text property='<%= "itemNames[" + idxStr + "]" %>' style="width:150px;"/>
        判定基準<html:select property='<%= "itemJudgeCriterias[" + idxStr + "]" %>'>
            <html:option value="">--</html:option>
            <html:option value="○のみ">○のみ</html:option>
            <html:option value="○×△">○×△</html:option>
        </html:select>
        正常範囲<html:text property='<%= "itemNormalRanges[" + idxStr + "]" %>' style="width:80px;"/>
        単位<html:text property='<%= "itemUnits[" + idxStr + "]" %>' style="width:50px;"/>
        <html:hidden property='<%= "itemIds[" + idxStr + "]" %>'/>
        <html:hidden property='<%= "itemCat1Idxs[" + idxStr + "]" %>'/>
        <html:hidden property='<%= "itemCat2Idxs[" + idxStr + "]" %>'/>
        <input type="button" value="削除" style="color:#c33;"
               onclick="document.forms[0].method.value='delRow';
                        var inp=document.createElement('input');inp.type='hidden';inp.name='rowIdx';inp.value='<%= idxStr %>';
                        document.forms[0].appendChild(inp);document.forms[0].submit();"/>
    </div>
<%
                        itemIdx++;
                    }
                }
            }
        }
    }
%>
</div>

<!-- +大分類追加 button -->
<div style="margin:12px 0 16px 8px;">
    <input type="button" value="+大分類追加" class="btn"
           onclick="document.forms[0].method.value='addCat1';document.forms[0].submit();"
           style="background:#4a90d9;color:#fff;padding:6px 14px;border:none;cursor:pointer;"/>
</div>

<!-- Save / Cancel -->
<div style="text-align:center;margin-top:16px;">
    <html:submit value="保存" style="width:100px;"/>
    <input type="button" value="キャンセル" class="btn btn-back" style="width:100px;"
           onclick="location.href='<%= ctx %>/mst/chkitem/list.do';"/>
</div>

</html:form>
