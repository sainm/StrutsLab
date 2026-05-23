<%@ page contentType="text/html; charset=UTF-8" %>
<ul>
    <li><a href="<%=request.getContextPath()%>/login.do">メインメニュー</a></li>
</ul>
<div class="module-title">マスタ管理</div>
<ul>
    <li><a href="<%=request.getContextPath()%>/mst/eqp/list.do">設備マスタ一覧</a></li>
    <li><a href="<%=request.getContextPath()%>/mst/chkitem/list.do">点検項目マスタ一覧</a></li>
</ul>
<div class="module-title">点検計画・実施</div>
<ul>
    <li><a href="<%=request.getContextPath()%>/ins/plan/yearly.do">年間点検計画一覧</a></li>
    <li><a href="<%=request.getContextPath()%>/ins/plan/wizard.do">点検計画登録</a></li>
    <li><a href="<%=request.getContextPath()%>/ins/daily.do">点検実施一覧</a></li>
    <li><a href="<%=request.getContextPath()%>/ins/approval/list.do">点検実施承認一覧</a></li>
</ul>
<div class="module-title">異常報告・対応指示</div>
<ul>
    <li><a href="<%=request.getContextPath()%>/inc/list.do">異常報告一覧</a></li>
    <li><a href="<%=request.getContextPath()%>/counter/list.do">対応指示一覧</a></li>
</ul>
<div class="module-title">組織・要員管理</div>
<ul>
    <li><a href="<%=request.getContextPath()%>/org/dept/list.do">部署マスタ一覧</a></li>
    <li><a href="<%=request.getContextPath()%>/org/emp/list.do">担当者マスタ一覧</a></li>
</ul>
<div class="module-title">その他</div>
<ul>
    <li><a href="<%=request.getContextPath()%>/cal/list.do">休日カレンダー</a></li>
    <li><a href="<%=request.getContextPath()%>/parts/list.do">保守部品一覧</a></li>
    <li><a href="<%=request.getContextPath()%>/report/summary.do">総合レポート</a></li>
</ul>
