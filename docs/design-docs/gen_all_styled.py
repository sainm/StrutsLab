#!/usr/bin/env python3
"""Generate all 16 design Excel files with formatting (borders, colors, fonts, merged cells)."""
import sys; sys.path.insert(0, '.')
from _xlsx_gen import *

# ============ 01_システム概要書 ============
w = XlsxWriter()
w.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w.add_sheet('Sheet2', [])

d01 = []
t,m = T('システム概要書')
d01.append(t)
d01 += doc_info_8()
d01.append(E())
d01 += [S('システムアーキテクチャ'), H(['レイヤー', '採用技術', 'バージョン', '役割', '', '', '', ''])]
arch_rows = [
    ['プレゼンテーション層', 'Struts 1.3', '1.3.10', 'ActionServlet, Action, ActionForm, JSP'],
    ['プレゼンテーション層', 'Tiles', '1.3.10', '画面レイアウト共通化（header/body/footer/menu）'],
    ['プレゼンテーション層', 'JSP + カスタムタグ', '', 'スクリプトレット主体、独自タグライブラリ'],
    ['ビジネスロジック層', 'Actionクラス', '', 'Struts1 Action + DispatchAction'],
    ['データアクセス層', 'MyBatis', '3.5.x', 'SQLマッピング、Mapper.xml'],
    ['データベース', 'H2 Database', '1.4.200', 'ファイルモード（~/struts-lab-db）'],
    ['バリデーション', 'Validator Plug-in', '1.3.10', 'validation.xml, validation-rules.xml'],
    ['ビルドツール', 'Maven', '3.x', 'war → Tomcat 8.x にデプロイ'],
    ['サーブレットコンテナ', 'Apache Tomcat', '8.5.x', 'Java 1.8 対応'],
]
for r in arch_rows: d01.append(R(r + ['']*4))
d01.append(E())
d01 += [S('機能モジュール一覧'), H(['モジュール名', '略称', '対象画面数', '主なテスト難点', '', '', '', ''])]
mods = [
    ['マスタ管理', 'MASTER', '4', '多段検索、ツリー選択、親子設定、CSV出力'],
    ['点検計画・実施', 'INSPECT', '7', 'Wizard、ネストチェック、写真添付、条件付きバリデーション、承認フロー'],
    ['異常報告', 'INCIDENT', '3', 'ステータス遷移、条件付き必須項目、類似事例検索'],
    ['対応指示・是正', 'COUNTER', '4', '動的行追加、Nested Properties、ポップアップ選択、全明細完了チェック'],
    ['組織・要員管理', 'ORG', '4', '階層ツリー、資格期限チェック、多セクションフォーム'],
    ['カレンダー・休日', 'CALENDAR', '2', 'カレンダー表示、一括登録、重複チェック'],
    ['保守部品管理', 'PARTS', '3', 'チェックボックスツリー、在庫アラート、数量自動引当'],
    ['レポート', 'REPORT', '1', 'クロス集計、テーブル形式グラフ、期間指定'],
]
for r in mods: d01.append(R(r + ['']*4))
d01.append(E())
d01 += [S('画面一覧（全27画面）'), H(['No.', 'モジュール', '画面ID', '画面名', '種別', '主要Struts1要素', 'テスト難点', ''])]
screens = [
    ['1','MASTER','SCR-MST-EQP-001','設備マスタ一覧','一覧','Action,Form,Paging','多段検索,一括操作,CSV'],
    ['2','MASTER','SCR-MST-EQP-002','設備マスタ登録・編集','登録','Form,Validator,Popup','条件付き必須,Popup→親画面'],
    ['3','MASTER','SCR-MST-CHK-001','点検項目マスタ一覧','一覧','Action,Form','テンプレートコピー,並順変更'],
    ['4','MASTER','SCR-MST-CHK-002','点検項目マスタ登録編集','登録','DispatchAction,動的行','3階層ツリー編集'],
    ['5','INSPECT','SCR-INS-PLAN-001','年間点検計画一覧','一覧','Action,Form','マトリクス表示(動的カラム)'],
    ['6','INSPECT','SCR-INS-PLAN-002','点検計画登録（Wizard）','登録','Form,Wizard 4step','マルチステップ,途中復帰'],
    ['7','INSPECT','SCR-INS-DAILY-001','点検実施一覧（当日）','一覧','Action,Form','バッジ,直接遷移'],
    ['8','INSPECT','SCR-INS-EXEC-001','点検実施入力','入力','Form,FileUpload,Validator','ネスト,条件付き,写真'],
    ['9','INSPECT','SCR-INS-EXEC-002','点検実施詳細・修正','表示','Form,Validator','修正申請→承認フロー'],
    ['10','INSPECT','SCR-INS-APPR-001','点検実施承認一覧','一覧','Action,一括操作','一括承認/差戻し'],
    ['11','INCIDENT','SCR-INC-LIST-001','異常報告一覧','一覧','Action,Form','7条件複合検索,条件保存'],
    ['12','INCIDENT','SCR-INC-CREATE-001','異常報告登録','登録','Form,FileUpload','データ引継,類似事例'],
    ['13','INCIDENT','SCR-INC-DETAIL-001','異常報告詳細','詳細','Form,LookupDispatch','多段階遷移'],
    ['14','COUNTER','SCR-CTR-CREATE-001','対応指示登録','登録','Form,Indexed,Popup','動的行(Submit),index再振'],
    ['15','COUNTER','SCR-CTR-LIST-001','対応指示一覧','一覧','Action,Form','複合検索,印刷用window'],
    ['16','COUNTER','SCR-CTR-DETAIL-001','対応指示詳細・完了','詳細','Form,Validator','明細個別更新,自動完了'],
    ['17','COUNTER','SCR-CTR-CAPA-001','是正処置報告書','登録','Form,Validator','なぜなぜ分析5段階'],
    ['18','REPORT','SCR-RPT-SUMMARY-001','総合レポート','表示','Action,Form','クロス集計,擬似グラフ'],
    ['19','ORG','SCR-ORG-DEPT-001','部署マスタ一覧','一覧','Action,Form','4階層ツリー表示'],
    ['20','ORG','SCR-ORG-DEPT-002','部署マスタ登録・編集','登録','Form,Validator','ツリーPopup,過去日'],
    ['21','ORG','SCR-ORG-EMP-001','担当者マスタ一覧','一覧','Action,Form','複合検索,資格期限'],
    ['22','ORG','SCR-ORG-EMP-002','担当者マスタ登録・編集','登録','Form,Validator','5section,認定期限'],
    ['23','CALENDAR','SCR-CAL-LIST-001','休日カレンダー一覧','一覧','Action,Form','月別表示,色分け'],
    ['24','CALENDAR','SCR-CAL-REG-001','休日登録・編集','登録','Form,Validator','範囲一括,重複'],
    ['25','PARTS','SCR-PRT-LIST-001','保守部品一覧','一覧','Action,Form','在庫アラート'],
    ['26','PARTS','SCR-PRT-REG-001','保守部品登録・編集','登録','Form,Validator,File','checkboxツリー'],
    ['27','PARTS','SCR-PRT-USAGE-001','部品使用実績一覧','一覧','Action,Form','在庫自動引当'],
]
for s in screens: d01.append(R(s + ['']))
d01.append(E())
d01 += [S('Struts1 設定ファイル構成'), H(['ファイル', '配置パス', '役割', '', '', '', '', ''])]
cfgs = [
    ['web.xml', '/WEB-INF/web.xml', 'ActionServlet, Tiles, Validator'],
    ['struts-config.xml', '/WEB-INF/struts-config.xml', 'Action, FormBean, Forward, Plug-in'],
    ['tiles-defs.xml', '/WEB-INF/tiles-defs.xml', 'Tilesレイアウト定義'],
    ['validation.xml', '/WEB-INF/validation.xml', 'Validator検証ルール'],
    ['validator-rules.xml', '/WEB-INF/validator-rules.xml', 'Validator基本ルール'],
    ['mybatis-config.xml', 'クラスパス直下', 'MyBatis, DataSource, Mapper'],
    ['ApplicationResources_ja.properties', 'クラスパス直下', '日本語メッセージリソース'],
    ['*.tld', '/WEB-INF/tld/', 'カスタムタグ定義'],
]
for r in cfgs: d01.append(R(r + ['']*5))
w.add_sheet('システム概要書', d01, col_widths=[5,12,20,20,22,22,18,14])
w.save('01_システム概要書.xlsx')

# ============ 02_画面設計書_マスタ管理 ============
w02 = XlsxWriter()
w02.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w02.add_sheet('Sheet2', [])

def screen_sheet(name, sid, pg, sub, layout, els, tbl=None, smap=None, tests=None):
    """Generate a styled screen design sheet."""
    d = []
    t,m = T(f'画面設計書 — {name}')
    d.append(t)
    d += [
        R(['文書管理情報', '', '', '', '', '', '', '']),
        K(['システムID', 'STRUTSLAB-001', '', '', 'サブシステム名', sub, '', '']),
        K(['画面ID', sid, '', '', '画面名', name, '', '']),
        K(['プログラムID', pg, '', '', '文書番号', f'DOC-SCR-{sid}', '', '']),
        K(['版数', '1.0', '', '', '作成日', '2026/05/23', '', '']),
        K(['作成者', 'システム開発部', '', '', '区分', '新規', '', '']),
    ]
    d += [E(), S('画面レイアウト'), R([layout] + ['']*7)]
    d += [E(), S('画面要素一覧'), H(['要素ID', '種別', '表示名', '入力値/選択肢', '説明', 'テスト難点タグ', '', ''])]
    for e in els: d.append(R(list(e) + ['']*(8-len(e))))
    if tbl:
        d += [E(), S('テーブル列定義'), H(['列名', '型', 'ソート', '説明', '', '', '', ''])]
        for tc in tbl: d.append(R(list(tc) + ['']*(8-len(tc))))
    if smap:
        d += [E(), S('Struts1マッピング'), H(['項目', '値', '', '', '', '', '', ''])]
        for k,v in smap.items(): d.append(R([k, v] + ['']*6))
    if tests:
        d += [E(), S('テスト難点詳細')]
        for t in tests: d.append(R([t] + ['']*7))
    return d

# 設備マスタ一覧
eqp_list = screen_sheet('設備マスタ一覧', 'SCR-MST-EQP-001', 'PG-MST-EQP-001', 'マスタ管理',
    '上部：検索条件（設備種別select, 電圧階級select, 設置年from/to, 保全ランクselect, 担当部署text）。検索/クリアボタン。\n下部：一覧テーブル（設備コード, 設備名, 設備種別, 電圧階級, 設置年月, 保全ランク, 担当部署）。ページング。CSV出力, 選択削除, 新規登録ボタン。各行にcheckbox。',
    [['eqp-search-type','select','設備種別','変圧器/遮断器/開閉器/ケーブル/母線/保護継電器/計器用変成器','5条件AND検索','複合検索'],
     ['eqp-search-voltage','select','電圧階級','66kV/154kV/275kV/500kV','',''],
     ['eqp-search-year-from','text','設置年(from)','YYYY','','from>toバリデーション'],
     ['eqp-search-year-to','text','設置年(to)','YYYY','',''],
     ['eqp-search-rank','select','保全ランク','S/A/B/C','',''],
     ['eqp-search-dept','text','担当部署','部分一致','',''],
     ['eqp-select-all','checkbox','全選択','','当該ページのみ全選択','全選択scope検証'],
     ['eqp-check-{n}','checkbox','選択','','指数付きname属性',''],
     ['eqp-code-{n}','link','設備コード','クリック→編集画面','',''],
     ['eqp-btn-csv','submit','CSV出力','','Content-Disposition検証',''],
     ['eqp-btn-delete','submit','選択削除','','window.confirm','confirm OK/Cancel'],
     ['eqp-btn-new','submit','新規登録','','',''],
     ['eqp-paging','link','ページング','前へ 1 2 3 … 次へ','','ページ跨ぎ不可']],
    [['設備コード','VARCHAR','○',''],['設備名','VARCHAR','○',''],['設備種別','VARCHAR','○',''],
     ['電圧階級','VARCHAR','○',''],['設置年月','VARCHAR','',''],['保全ランク','CHAR','○',''],['担当部署','VARCHAR','','']],
    {'Action Path':'/mst/eqp/list','Action Class':'EqpListAction','Form Bean':'EqpSearchForm','Forward':'eqp.list.tiles'},
    ['多段検索(5条件AND)：全組合せ検索結果検証','全選択checkbox：page内のみ選択、他page無影響','CSV出力：検索条件に応じた出力内容確認','選択削除：confirm dialog OK/Cancel両方'])

# 設備マスタ登録編集
eqp_edit = screen_sheet('設備マスタ登録・編集', 'SCR-MST-EQP-002', 'PG-MST-EQP-002', 'マスタ管理',
    '5セクション大フォーム（基本情報/電気仕様/設置場所/保全区分/備考）。セクション間アンカーリンク。親設備選択Popup→値反映。添付ファイル。',
    [['eqp-name','text','設備名','','必須',''],
     ['eqp-type','select','設備種別','変圧器/遮断器/…','必須',''],
     ['eqp-status','select','設備ステータス','運用中/停止中/廃止','廃止→親設備不可',''],
     ['eqp-voltage','select','電圧階級','66kV/154kV/275kV/500kV','',''],
     ['eqp-parent','text','親設備','読取専用','Popup選択結果','Stale Element'],
     ['eqp-parent-btn','button','選択','window.open→選択→opener反映','Window handle切替',''],
     ['eqp-interval','text','点検周期(月)','1〜120','',''],
     ['eqp-file','file','添付','','file size/形式制限',''],
     ['eqp-btn-save','submit','登録/更新','','POST→validation→遷移',''],
     ['eqp-btn-back','submit','戻る','','一覧へ','']],
    None,
    {'Action Path':'/mst/eqp/save','Action Class':'EqpSaveAction(DispatchAction)','Form Bean':'EqpForm(ValidatorForm)','Forward':'eqp.list.tiles'},
    ['親設備選択Popup：Window handle→検索→選択→opener値反映','5セクションform：アンカー移動で値維持','廃止設備→親不可','添付+同時validation：error時file維持'])

# 点検項目マスタ一覧
chk_list = screen_sheet('点検項目マスタ一覧', 'SCR-MST-CHK-001', 'PG-MST-CHK-001', 'マスタ管理',
    '上部検索：設備種別×点検種別(日常/定期/精密)。一覧：テンプレート名, 設備種別, 点検種別, 項目数, 最終更新日。各行に編集/コピー/削除。並順変更▲/▼。',
    [['chk-search-type','select','設備種別','変圧器/遮断器/…','',''],
     ['chk-search-kind','select','点検種別','日常/定期/精密','',''],
     ['chk-tmpl-name-{n}','link','テンプレート名','クリック→編集','',''],
     ['chk-btn-copy-{n}','submit','コピー','','copy後名称重複check',''],
     ['chk-btn-order-up-{n}','submit','▲','','並順上へ→index変化','stale element'],
     ['chk-btn-order-down-{n}','submit','▼','','並順下へ→index変化','stale element']],
    [['テンプレート名','VARCHAR','○',''],['設備種別','VARCHAR','○',''],['点検種別','VARCHAR','○',''],
     ['項目数','INT','○',''],['最終更新日','DATE','○','']],
    {'Action Path':'/mst/chkitem/list','Action Class':'CheckItemListAction','Form Bean':'CheckItemSearchForm','Forward':'chkitem.list.tiles'},
    ['並順変更：▲/▼後要素index変動(stale)','copy→名称重複validation'])

# 点検項目マスタ登録編集
chk_edit = screen_sheet('点検項目マスタ登録・編集', 'SCR-MST-CHK-002', 'PG-MST-CHK-002', 'マスタ管理',
    '上部基本情報(テンプレート名/設備種別/点検種別)。3階層ツリー編集：大分類→中分類→項目。各行に追加/削除ボタン(Submit→再描画)。一括項目追加→行数指定→戻って追加。',
    [['chk-tmpl-name','text','テンプレート名','','必須',''],
     ['chk-cat1-name-{n}','text','大分類名','','',''],
     ['chk-btn-add-cat1','submit','+大分類追加','','動的行追加(Submit)','index再振'],
     ['chk-cat2-name-{n}-{m}','text','中分類名','','','Nested index'],
     ['chk-btn-add-cat2-{n}','submit','+中分類追加','','',''],
     ['chk-item-name-{i}','text','項目名','','必須',''],
     ['chk-item-criteria-{i}','select','判定基準','○のみ/○×△','',''],
     ['chk-btn-del-{i}','submit','削除','','行削除→index再振','stale element'],
     ['chk-btn-bulk-add','submit','一括項目追加','','別画面遷移→戻る','']],
    None,
    {'Action Path':'/mst/chkitem/save','Action Class':'CheckItemSaveAction','Form Bean':'CheckItemForm(ValidatorForm)','Forward':'chkitem.list.tiles'},
    ['3階層tree動的行追加：大分類→中分類→項目の順でindex変動','行削除後index再振：削除→追加繰返で連番維持','一括項目追加：別画面→戻る→追加data保持'])

w02.add_sheet('設備マスタ一覧', eqp_list, col_widths=[18,10,16,26,26,18,14,14])
w02.add_sheet('設備マスタ登録編集', eqp_edit, col_widths=[18,10,16,26,26,18,14,14])
w02.add_sheet('点検項目マスタ一覧', chk_list, col_widths=[18,10,16,26,26,18,14,14])
w02.add_sheet('点検項目マスタ登録編集', chk_edit, col_widths=[18,10,16,26,26,18,14,14])
w02.save('02_画面設計書_マスタ管理.xlsx')

# ============ 03_画面設計書_点検計画・実施 ============
w03 = XlsxWriter()
w03.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w03.add_sheet('Sheet2', [])

s5 = screen_sheet('年間点検計画一覧', 'SCR-INS-PLAN-001', 'PG-INS-PLAN-001', '点検計画',
    '年度×設備×月マトリクス表示。行=設備、列=4月〜3月(12カラム)。セル内「計画:N回 実績:N回」。未達赤背景、超過黄背景。CSV出力、計画ロック/解除。',
    [['plan-year','select','年度','2025/2026/2027','',''],
     ['plan-cell-{r}-{m}','text','マトリクスセル','計画:N 実績:N','動的カラム(12)',''],
     ['plan-btn-lock','submit','計画ロック/解除','','confirm','']],
    [['設備名','VARCHAR','',''],['4月〜3月','動的(12列)','','']],
    {'Action Path':'/ins/plan/yearly','Action Class':'YearlyPlanAction','Form Bean':'YearlyPlanForm','Forward':'ins.plan.yearly.tiles'},
    ['動的カラム(月数=12だが行数可変)','色分けCSS class検証','ロック後編集不可'])

s6 = screen_sheet('点検計画登録Wizard', 'SCR-INS-PLAN-002', 'PG-INS-PLAN-002', '点検計画',
    '4step Wizard：①対象設備選択→②点検項目テンプレート選択→③日程・担当者→④確認・登録。各step「戻る」「進む」。確認面「一時保存」「確定」。データhidden持回り。',
    [['wiz-step-indicator','text','step表示','①→②→③→④','',''],
     ['wiz-btn-next-{n}','submit','進む','','hidden値validation',''],
     ['wiz-btn-back-{n}','submit','戻る','','前画面data保持',''],
     ['wiz-btn-temp-save','submit','一時保存','','session保存',''],
     ['wiz-btn-commit','submit','確定','','DB書込','']],
    None,
    {'Action Path':'/ins/plan/wizard','Action Class':'PlanWizardAction','Form Bean':'PlanWizardForm','Forward':'step1/2/3/confirm'},
    ['4step data持回り(hidden→再POST)','戻る→修正→進むloop全path','一時保存→session切れ→再開不可'])

s7 = screen_sheet('点検実施一覧(当日)', 'SCR-INS-DAILY-001', 'PG-INS-DAILY-001', '点検実施',
    '対象日(本日default)、担当者選択。一覧：設備名、点検種別、予定時刻、status badge(未了=灰/一部完了=黄/完了=緑)、担当者。行click→実施入力直接遷移。',
    [['daily-date','text','対象日','YYYYMMDD','',''],
     ['daily-status-badge-{n}','span','status badge','未了/一部完了/完了','CSS class検証','']],
    [['設備名','VARCHAR','',''],['点検種別','VARCHAR','',''],['予定時刻','VARCHAR','',''],
     ['status','VARCHAR','',''],['担当者','VARCHAR','','']],
    {'Action Path':'/ins/daily','Action Class':'DailyListAction','Form Bean':'DailyForm','Forward':'ins.daily.list.tiles'},
    ['badge色分け(CSS class)','一覧→実施入力直接遷移param引継'])

s8 = screen_sheet('点検実施入力', 'SCR-INS-EXEC-001', 'PG-INS-EXEC-001', '点検実施',
    '3block構成：①設備基本情報(表示) ②checklist 3階層nest table ③総合判定。各項目判定radio(○/×/△)、実測値、所見。×→実測値+所見必須、△→所見必須。写真添付(max5枚/項目)。総合判定異常→異常報告へ。',
    [['exec-judge-{i}','radio','判定','○/×/△','条件付きvalidation','全組合わせ=3^N'],
     ['exec-value-{i}','text','実測値','','×時必須',''],
     ['exec-note-{i}','textarea','所見','','×/△時必須',''],
     ['exec-photo-{i}','file','写真','max5枚/項目','file添付',''],
     ['exec-summary-judge','radio','総合判定','正常/異常あり/要観察','',''],
     ['exec-btn-incident','submit','異常報告へ','異常時のみ表示','条件付button表示','data引継']],
    None,
    {'Action Path':'/ins/exec/input','Action Class':'ExecInputAction','Form Bean':'ExecForm(ValidatorForm)','Forward':'ins.daily.list / inc.create'},
    ['nest table：tr親子関係DOM上flat','条件付きvalidation組合わせ爆発(3^N)','写真添付+同時validation','異常報告へbutton条件表示'])

s9 = screen_sheet('点検実施詳細・修正', 'SCR-INS-EXEC-002', 'PG-INS-EXEC-002', '点検実施',
    '実施済点検結果全block表示(読取専用)。修正申請→修正理由(textarea必須)→承認申請。承認済は再修正不可。',
    [['exec-mod-reason','textarea','修正理由','','申請時必須',''],
     ['exec-mod-status','text','申請status','申請中/承認済/差戻','','']],
    None,
    {'Action Path':'/ins/exec/detail','Action Class':'ExecDetailAction','Form Bean':'ExecForm','Forward':'ins.exec.detail.tiles'},
    ['修正申請→承認→再表示全path','修正理由validation(申請時のみ必須)','承認済後修正不可'])

s10 = screen_sheet('点検実施承認一覧', 'SCR-INS-APPR-001', 'PG-INS-APPR-001', '点検実施',
    '検索(期間/担当班/status)。一覧checkbox付。一括承認/一括差戻し。差戻時理由必須。操作後通知message。',
    [['appr-check-{n}','checkbox','選択','','',''],
     ['appr-btn-approve','submit','一括承認','','confirm',''],
     ['appr-btn-reject','submit','一括差戻し','','理由必須','confirm+textarea'],
     ['appr-reject-reason','textarea','差戻理由','','一括差戻時必須','']],
    [['申請日時','TIMESTAMP','',''],['設備名','VARCHAR','',''],['申請者','VARCHAR','',''],
     ['修正理由','VARCHAR','','一部表示'],['status','VARCHAR','','']],
    {'Action Path':'/ins/approval/list','Action Class':'ApprovalListAction','Form Bean':'ApprovalForm','Forward':'ins.appr.list.tiles'},
    ['一括承認：confirm OK→全件処理','一括差戻：confirm→理由必須→未入力validation'])

w03.add_sheet('年間点検計画一覧', s5, col_widths=[18,10,16,26,26,18,14,14])
w03.add_sheet('点検計画登録Wizard', s6, col_widths=[18,10,16,26,26,18,14,14])
w03.add_sheet('点検実施一覧(当日)', s7, col_widths=[18,10,16,26,26,18,14,14])
w03.add_sheet('点検実施入力', s8, col_widths=[18,10,16,26,26,18,14,14])
w03.add_sheet('点検実施詳細修正', s9, col_widths=[18,10,16,26,26,18,14,14])
w03.add_sheet('点検実施承認一覧', s10, col_widths=[18,10,16,26,26,18,14,14])
w03.save('03_画面設計書_点検計画・実施.xlsx')

# ============ 04〜07：簡略版（同じパターンで全画面） ============

# 04_異常報告
w04 = XlsxWriter()
w04.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w04.add_sheet('Sheet2', [])

s11 = screen_sheet('異常報告一覧', 'SCR-INC-LIST-001', 'PG-INC-LIST-001', '異常報告',
    '7条件複合検索（発生日×設備種別×異常種別×status×重大度×担当班×keyword）。検索条件保存/呼出。一覧checkbox付。一括status更新、CSV/PDF出力。',
    [['inc-s-date-from/to','text','発生日from/to','YYYYMMDD','',''],
     ['inc-s-type','select','設備種別','','',''],
     ['inc-s-inc-type','select','異常種別','絶縁不良/過熱/振動/油漏/ガス/コロナ/他','',''],
     ['inc-s-status','select','status','全/未了/調査中/対応中/完了/再発防止/close','',''],
     ['inc-s-severity','select','重大度','全/軽微/中/重大/緊急','',''],
     ['inc-btn-save-cond','submit','検索条件保存','','session保存→呼出','再現性']],
    [['報告番号','VARCHAR','○','INC-YYYYMMDD-NNN'],['発生日時','TIMESTAMP','○',''],
     ['設備名','VARCHAR','○',''],['異常種別','VARCHAR','○',''],['重大度','VARCHAR','○',''],
     ['status','VARCHAR','○',''],['担当班','VARCHAR','','']],
    {'Action Path':'/inc/list','Action Class':'IncidentListAction','Form Bean':'IncidentSearchForm','Forward':'inc.list.tiles'},
    ['7条件複合検索全組合わせ検証困難','検索条件保存→呼出再現性'])

s12 = screen_sheet('異常報告登録', 'SCR-INC-CREATE-001', 'PG-INC-CREATE-001', '異常報告',
    '3block構成：①発生情報(点検からdata引継) ②異常内容(種別/重大度/部位/詳細/添付max3) ③暫定処置。類似事例検索→同面下部結果表示。登録/一時保存。',
    [['inc-create-datetime','text','発生日時','YYYYMMDD HH:MM','点検から引継',''],
     ['inc-create-type','select','異常種別','','必須',''],
     ['inc-create-severity','select','重大度','軽微/中/重大/緊急','必須',''],
     ['inc-create-file-{n}','file','添付{n}','','max3枚',''],
     ['inc-btn-similar','submit','類似事例検索','','同面下部結果表示','DOM追加']],
    None,
    {'Action Path':'/inc/create','Action Class':'IncidentCreateAction','Form Bean':'IncidentForm(ValidatorForm)','Forward':'inc.list / inc.create'},
    ['点検→異常報告data引継(hidden/session)','類似事例検索DOM動的追加','添付+validation同時'])

s13 = screen_sheet('異常報告詳細', 'SCR-INC-DETAIL-001', 'PG-INC-DETAIL-001', '異常報告',
    '3block表示+経過記録timeline。多段階status遷移：未了→調査中→対応中→完了→close/再発防止。各遷移時条件付必須項目変化(推定原因/対応内容)。button表示/非表示切替。',
    [['inc-dtl-timeline','table','経過記録','登録→調査中→対応中→完了→再発防止','',''],
     ['inc-dtl-btn-investigate','submit','調査開始','未了時のみ表示','','条件付button表示'],
     ['inc-dtl-btn-counter','submit','対応開始','調査中時のみ','推定原因必須','条件付必須'],
     ['inc-dtl-cause','textarea','推定原因','調査→対応時必須','',''],
     ['inc-dtl-btn-complete','submit','完了','対応中時のみ','対応内容必須',''],
     ['inc-dtl-btn-capa','submit','再発防止策登録','完了時のみ','CAPAへ遷移','']],
    None,
    {'Action Path':'/inc/detail','Action Class':'IncidentDetailAction','Form Bean':'IncidentForm','Forward':'inc.detail / counter.capa'},
    ['多段階遷移(5段階)全path網羅','各遷移時条件付必須項目','button表示/非表示状態別切替','timeline時系列sort'])

w04.add_sheet('異常報告一覧', s11, col_widths=[18,10,16,26,26,18,14,14])
w04.add_sheet('異常報告登録', s12, col_widths=[18,10,16,26,26,18,14,14])
w04.add_sheet('異常報告詳細', s13, col_widths=[18,10,16,26,26,18,14,14])
w04.save('04_画面設計書_異常報告.xlsx')

# 05_対応指示・是正
w05 = XlsxWriter()
w05.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w05.add_sheet('Sheet2', [])

s14 = screen_sheet('対応指示登録', 'SCR-CTR-CREATE-001', 'PG-CTR-CREATE-001', '対応指示',
    'Header+N明細の親子構造。明細Indexed Properties(details[0].workContent形式)。各明細：作業内容/担当者(text+Popup選択)/期限/優先度。行追加(Submit→再描画)/行削除(index再振)。登録/一時保存。',
    [['ctr-detail-work-{n}','text','作業内容[{n}]','','必須','Indexed Properties'],
     ['ctr-detail-person-{n}','text','担当者[{n}]','読取専用','Popup選択結果','Stale Element'],
     ['ctr-detail-person-btn-{n}','button','選択','window.open→選択→opener反映','Window handle切替',''],
     ['ctr-detail-del-{n}','submit','削除','','行削除→index再振','stale element'],
     ['ctr-btn-add-row','submit','+行追加','','Submit→再描画','index変動→既存要素stale']],
    None,
    {'Action Path':'/counter/create','Action Class':'CounterCreateAction','Form Bean':'CounterForm(ValidatorForm)','Forward':'counter.list / counter.create'},
    ['動的行追加(Submit→再描画)：追加後既存行stale','Indexed Properties name属性深nest','行削除後index再振：削除→追加→削除','Popup→親画面：Window handle+opener操作'])

s15 = screen_sheet('対応指示一覧', 'SCR-CTR-LIST-001', 'PG-CTR-LIST-001', '対応指示',
    '複合検索(指示日範囲×担当者×status×優先度)。一覧：指示番号/指示日/関連異常報告/優先度/明細数/完了数/全体status。印刷用表示(window.open)。CSV出力。',
    [['ctr-btn-print','button','印刷用表示','window.open','別window','新規window handle'],
     ['ctr-btn-bulk-upd','submit','一括status更新','','confirm','']],
    [['指示番号','VARCHAR','○',''],['指示日','DATE','○',''],['関連異常番号','VARCHAR','',''],
     ['優先度','VARCHAR','',''],['完了/全明細','VARCHAR','',''],['status','VARCHAR','○','']],
    {'Action Path':'/counter/list','Action Class':'CounterListAction','Form Bean':'CounterSearchForm','Forward':'counter.list.tiles'},
    ['印刷用別window handle切替+内容検証','完了数/全明細数表示更新'])

s16 = screen_sheet('対応指示詳細・完了報告', 'SCR-CTR-DETAIL-001', 'PG-CTR-DETAIL-001', '対応指示',
    '指示header+明細一覧。各行完了報告button→実績時間/使用部品/使用量/所見入力行展開。全明細完了→header自動完了。使用部品→在庫超えalert。',
    [['ctr-dtl-btn-complete-{n}','submit','完了報告','','押下→入力行展開','DOM追加'],
     ['ctr-dtl-actual-hours-{n}','text','実績作業時間','数値(時間)','完了時必須','条件付必須'],
     ['ctr-dtl-used-part-{n}','select','使用部品','部品master','在庫超えalert',''],
     ['ctr-dtl-header-status','text','全体status','自動計算','全明細完了→自動完了','']],
    None,
    {'Action Path':'/counter/detail','Action Class':'CounterDetailAction','Form Bean':'CounterDetailForm','Forward':'counter.list / counter.detail'},
    ['明細個別完了報告→DOM展開','全明細完了→header自動完了trigger','部品選択→在庫超え条件付表示'])

s17 = screen_sheet('是正処置報告書', 'SCR-CTR-CAPA-001', 'PG-CTR-CAPA-001', '是正処置',
    '異常報告引継表示。なぜなぜ分析5段階(textarea×5)。再発防止策/効果確認方法/効果確認期限。承認申請。',
    [['capa-why{1-5}','textarea','なぜ①〜⑤','','必須×5',''],
     ['capa-countermeasure','textarea','再発防止策','','必須',''],
     ['capa-verify-method','textarea','効果確認方法','','必須',''],
     ['capa-verify-date','text','効果確認期限','YYYYMMDD','必須 未来日','']],
    None,
    {'Action Path':'/counter/capa/create','Action Class':'CapaAction','Form Bean':'CapaForm(ValidatorForm)','Forward':'counter.list / counter.capa'},
    ['なぜなぜ5段階：各段入力値検証','全textarea必須validation','異常報告→是正data引継'])

w05.add_sheet('対応指示登録', s14, col_widths=[20,10,18,26,26,18,14,14])
w05.add_sheet('対応指示一覧', s15, col_widths=[18,10,16,26,26,18,14,14])
w05.add_sheet('対応指示詳細完了', s16, col_widths=[20,10,18,26,26,18,14,14])
w05.add_sheet('是正処置報告書', s17, col_widths=[18,10,16,26,26,18,14,14])
w05.save('05_画面設計書_対応指示・是正.xlsx')

# 06_組織・カレンダー・保守部品（簡略9画面を1sheetずつ）
w06 = XlsxWriter()
w06.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w06.add_sheet('Sheet2', [])

sc06_data = [
    ('部署マスタ一覧','SCR-ORG-DEPT-001','組織管理','4階層tree表示(本社→支社→営業所→出張所)。table形式、indentで階層表現。編集/子部署追加link。統廃合履歴。',
     ['4階層tree indent検証','階層別追加/編集画面遷移']),
    ('部署マスタ登録編集','SCR-ORG-DEPT-002','組織管理','上位部署選択(tree Popup→値反映)。有効期間(開始/終了)。過去日異動警告。部署種別select。',
     ['過去日異動警告validation','tree Popup選択値反映']),
    ('担当者マスタ一覧','SCR-ORG-EMP-001','組織管理','複合検索(部署×職位×資格checkbox×入社年)。一覧：社員番号/氏名/部署/職位/資格/認定期限。期限切赤背景。一括lock/解除。',
     ['複数checkbox資格選択AND/OR検索','期限切CSS class検証']),
    ('担当者マスタ登録編集','SCR-ORG-EMP-002','組織管理','5section大form：①基本情報②所属職位③保有資格(資格毎認定日/期限)④点検員認定⑤account。password確認一致。loginID重複check。',
     ['5section一括validation','資格毎期限validation(n個条件付)','password一致','loginID重複']),
    ('休日カレンダー一覧','SCR-CAL-LIST-001','カレンダー管理','年度選択→月別calendar表示(table 7列×5-6行)。休日種別色分け(法定=赤/会社指定=青/点検停止=黄)。日付click→登録編集。',
     ['月別calendar cell色検証','年度切替data範囲']),
    ('休日登録編集','SCR-CAL-REG-001','カレンダー管理','日付範囲指定(from/to)→一括登録。重複check。振替出勤日設定。休日種別選択。',
     ['範囲指定一括登録重複check','計画停止期間と通常休日重複']),
    ('保守部品一覧','SCR-PRT-LIST-001','保守部品管理','複合検索(設備種別×部品種別×在庫status×keyword)。在庫badge(充足=緑/僅少=黄/在庫切=赤)。',
     ['在庫badge色分け(CSS class)','在庫僅少/切れ境界値']),
    ('保守部品登録編集','SCR-PRT-REG-001','保守部品管理','適用設備選択(checkbox tree：設備種別→設備名2階層 複数選択可)。発注点/安全在庫数(発注点>安全在庫validation)。添付file(仕様書PDF)。',
     ['checkbox tree階層表示','発注点>安全在庫validation']),
    ('部品使用実績一覧','SCR-PRT-USAGE-001','保守部品管理','期間×設備種別×部品検索。一覧：使用日/部品名/設備名/使用量/使用前後在庫/目的。在庫発注点下回→警告。',
     ['在庫自動引当表示(使用後在庫≦発注点で警告)']),
]
for name, sid, sub, layout, tests in sc06_data:
    pg_id = sid.replace('SCR-','PG-')
    s = screen_sheet(name, sid, pg_id, sub, layout, [], None,
        {'Action Path':f'/{sid.split("-")[1].lower()}/{sid.split("-")[2].lower()}/{"list" if "一覧" in name else "save"}',
         'Action Class': f'{name.replace("一覧","List").replace("登録編集","Save")}Action',
         'Form Bean': f'{name.replace("一覧","Search").replace("登録編集","")}Form'},
        tests)
    w06.add_sheet(name[:15], s, col_widths=[18,10,16,26,26,18,14,14])
w06.save('06_画面設計書_組織・カレンダー・保守部品.xlsx')

# 07_レポート
w07 = XlsxWriter()
w07.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w07.add_sheet('Sheet2', [])

s18 = screen_sheet('総合レポート', 'SCR-RPT-SUMMARY-001', 'PG-RPT-SUMMARY-001', 'レポート',
    '上部期間指定(from/to YYYYMM)/設備種別/担当班。①点検実施率推移 table形式月別graph(動的column)。目標値(95%)未満赤。②異常発生傾向 月別×異常種別cross。③設備別異常件数rank top10。CSV/印刷。',
    [['rpt-rate-cell-{r}-{m}','text','実施率cell','%表示','目標未満=赤','動的column'],
     ['rpt-cross-cell-{r}-{c}','text','cross集計cell','件数','',''],
     ['rpt-ranking-{n}','text','rank行','設備名/件数','',''],
     ['rpt-btn-print','button','印刷用表示','window.open','別window','']],
    [['月','動的','','from〜to全月'],['設備名/異常種別','VARCHAR','','']],
    {'Action Path':'/report/summary','Action Class':'SummaryReportAction','Form Bean':'ReportForm','Forward':'report.summary.tiles'},
    ['動的column(期間で月数可変) header/data対応','実施率色分け(CSS)','cross集計数値正確性'])
w07.add_sheet('総合レポート', s18, col_widths=[18,10,16,26,26,18,14,14])
w07.save('07_画面設計書_レポート.xlsx')

# ============ 08〜16 は後続で生成 ============
print("01-07 Done (28 sheets total).")
