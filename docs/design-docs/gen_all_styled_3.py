#!/usr/bin/env python3
"""Generate 12-16 styled Excel design docs."""
import sys; sys.path.insert(0, '.')
from _xlsx_gen import *

# ============ 12_画面遷移設計書 ============
w = XlsxWriter()
w.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w.add_sheet('Sheet2', [])

t12 = []
t,m = T('画面遷移設計書')
t12.append(t)
t12 += [
    S('文書管理情報', 8),
    K(['システムID', 'STRUTSLAB-001', '', '', 'システム名', '電力設備巡視点検管理システム', '', '']),
    K(['文書番号', 'DOC-TRANS-001', '', '', '版数', '1.0', '', '']),
    K(['作成日', '2026/05/23', '', '', '作成者', 'システム開発部', '', '']),
]
transitions = [
    ['共通','ログイン','ログイン','メインメニュー','画面遷移','T001'],
    ['共通','メインメニュー','設備マスタ','設備マスタ一覧','画面遷移','T002'],
    ['共通','メインメニュー','点検計画','年間点検計画一覧','画面遷移','T003'],
    ['共通','メインメニュー','点検実施(当日)','点検実施一覧','画面遷移','T004'],
    ['共通','メインメニュー','異常報告','異常報告一覧','画面遷移','T005'],
    ['共通','メインメニュー','対応指示','対応指示一覧','画面遷移','T006'],
    ['共通','メインメニュー','部署管理','部署マスタ一覧','画面遷移','T007'],
    ['共通','メインメニュー','担当者管理','担当者マスタ一覧','画面遷移','T008'],
    ['共通','メインメニュー','休日管理','休日カレンダー一覧','画面遷移','T009'],
    ['共通','メインメニュー','部品管理','保守部品一覧','画面遷移','T010'],
    ['共通','メインメニュー','レポート','総合レポート','画面遷移','T011'],
    ['マスタ','設備マスタ一覧','新規登録','設備マスタ登録','画面遷移','T101'],
    ['マスタ','設備マスタ一覧','設備code click','設備マスタ編集','画面遷移','T102'],
    ['マスタ','設備マスタ一覧','検索','同一画面(検索結果)','同一画面','T103'],
    ['マスタ','設備マスタ一覧','選択削除→confirm','同一画面(削除後)','同一画面','T104'],
    ['マスタ','設備マスタ登録/編集','親設備選択','親設備Popup','Popup','T105'],
    ['マスタ','点検項目マスタ一覧','copy','同一画面(copy後)','同一画面','T110'],
    ['マスタ','点検項目マスタ一覧','並順▲/▼','同一画面(並替後)','同一画面','T111'],
    ['点検','点検計画登録Wizard','Step1→進む','Step2','同一画面','T203'],
    ['点検','点検計画登録Wizard','Step2→戻る','Step1','同一画面','T204'],
    ['点検','点検計画登録Wizard','Step3→戻る','Step2','同一画面','T206'],
    ['点検','点検計画登録Wizard','Step3→進む','Step4(確認)','同一画面','T207'],
    ['点検','点検計画登録Wizard','Step4→確定','年間点検計画一覧','画面遷移','T209'],
    ['点検','点検計画登録Wizard','Step4→一時保存','Step4(保存完了)','同一画面','T210'],
    ['点検','点検実施一覧(当日)','行click','点検実施入力','画面遷移','T211'],
    ['点検','点検実施入力','異常報告へ(異常あり)','異常報告登録','画面遷移','T213'],
    ['点検','点検実施詳細','修正申請','同一画面(申請中)','同一画面','T214'],
    ['点検','点検実施承認一覧','一括承認→confirm','同一画面(通知)','同一画面','T215'],
    ['点検','点検実施承認一覧','一括差戻→理由','同一画面(通知)','同一画面','T216'],
    ['異常','異常報告一覧','検索条件保存','同一画面','同一画面','T302'],
    ['異常','異常報告詳細','調査開始(未了→調査中)','同一画面(調査中)','同一画面','T306'],
    ['異常','異常報告詳細','対応開始(調査中→対応中)','同一画面(対応中)','同一画面','T307'],
    ['異常','異常報告詳細','完了(対応中→完了)','同一画面(完了)','同一画面','T308'],
    ['異常','異常報告詳細','close','異常報告一覧','画面遷移','T309'],
    ['異常','異常報告詳細','再発防止策登録','是正処置報告書','画面遷移','T310'],
    ['対応','対応指示登録','行追加','同一画面(行追加後)','同一画面','T401'],
    ['対応','対応指示登録','行削除','同一画面(行削除後)','同一画面','T402'],
    ['対応','対応指示登録','担当者選択→Popup→反映','同一画面','同一画面','T403'],
    ['対応','対応指示登録','登録→成功','対応指示一覧','画面遷移','T404'],
    ['対応','対応指示一覧','印刷用表示','印刷用別window','新規window','T405'],
    ['対応','対応指示詳細','完了報告(明細)','同一画面(展開)','同一画面','T407'],
    ['対応','対応指示詳細','保存→全明細完了','同一画面(自動完了)','同一画面','T408'],
    ['対応','是正処置報告書','承認申請','異常報告詳細','画面遷移','T409'],
    ['組織','部署マスタ登録/編集','上位部署→Popup','同一画面','同一画面','T503'],
    ['組織','担当者マスタ一覧','一括lock/解除','同一画面(更新後)','同一画面','T504'],
    ['calendar','休日登録/編集','範囲指定一括登録','休日カレンダー一覧','画面遷移','T602'],
    ['保守','保守部品一覧','新規/編集','保守部品登録/編集','画面遷移','T701'],
    ['レポート','総合レポート','期間指定→表示','同一画面(集計表示)','同一画面','T801'],
    ['レポート','総合レポート','印刷用表示','印刷用別window','新規window','T802'],
]
t12 += [E(), S('全画面遷移一覧'), H(['モジュール', '遷移元', '操作', '遷移先', '種別', 'テスト経路ID', '', ''])]
for tr in transitions:
    t12.append(R(list(tr) + ['']*(8-len(tr))))
t12 += [E(), S('経路数集計')]
t12 += [
    R(['総経路数', str(len(transitions)), '', '', '', '', '', '']),
    R(['画面遷移(画面間)', str(sum(1 for t in transitions if t[4]=='画面遷移')), '', '', '', '', '', '']),
    R(['同一画面更新', str(sum(1 for t in transitions if t[4]=='同一画面')), '', '', '', '', '', '']),
    R(['Popup', str(sum(1 for t in transitions if t[4]=='Popup')), '', '', '', '', '', '']),
    R(['新規window', str(sum(1 for t in transitions if t[4]=='新規window')), '', '', '', '', '', '']),
]
w.add_sheet('画面遷移設計書', t12, col_widths=[8,22,22,26,14,14,10,10])
w.save('12_画面遷移設計書.xlsx')

# ============ 13_DB設計書 ============
w13 = XlsxWriter()
w13.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w13.add_sheet('Sheet2', [])

db13 = []
t,m = T('データベース設計書')
db13.append(t)
db13 += [
    S('文書管理情報', 8),
    K(['システムID', 'STRUTSLAB-001', '', '', 'DBMS', 'H2 Database 1.4.200（ファイルモード）', '', '']),
    K(['文書番号', 'DOC-DB-001', '', '', '版数', '1.0', '', '']),
    K(['作成日', '2026/05/23', '', '', '作成者', 'システム開発部', '', '']),
    K(['文字コード', 'UTF-8', '', '', '接続モード', 'ファイル（./struts-lab-db）', '', '']),
]

tables = [
    ('equipment (設備マスタ)', [
        ['equipment_code','VARCHAR(20)','PK','設備コード'],
        ['equipment_name','VARCHAR(100)','NOT NULL','設備名'],
        ['equipment_type','VARCHAR(10)','NOT NULL','設備種別'],
        ['voltage_level','VARCHAR(10)','','電圧階級'],
        ['parent_equipment_code','VARCHAR(20)','FK','親設備（自己参照）'],
        ['maintenance_rank','CHAR(1)','NOT NULL','S/A/B/C'],
        ['status','VARCHAR(5)','NOT NULL','運用中/停止中/廃止'],
    ]),
    ('inspection_template (点検項目テンプレート)', [
        ['template_id','INT','PK AUTO','templateID'],
        ['template_name','VARCHAR(100)','NOT NULL','template名'],
        ['equipment_type','VARCHAR(10)','NOT NULL','対象設備種別'],
        ['inspection_kind','VARCHAR(5)','NOT NULL','日常/定期/精密'],
    ]),
    ('inspection_items (点検項目3階層)', [
        ['item_id','INT','PK AUTO','項目ID'],
        ['template_id','INT','FK→inspection_template',''],
        ['parent_item_id','INT','FK→inspection_items','親項目（自己参照）'],
        ['item_level','INT','NOT NULL','1=大分類/2=中分類/3=個別'],
        ['item_name','VARCHAR(200)','NOT NULL','項目名'],
    ]),
    ('inspection_plans (点検計画)', [
        ['plan_id','INT','PK AUTO','計画ID'],
        ['fiscal_year','VARCHAR(4)','NOT NULL','年度'],
        ['equipment_code','VARCHAR(20)','FK→equipment','対象設備'],
        ['planned_date','VARCHAR(8)','NOT NULL','点検予定日'],
        ['status','VARCHAR(10)','NOT NULL','予定/実施済/中止/延期'],
        ['is_locked','BOOLEAN','NOT NULL','計画lock'],
    ]),
    ('inspection_results (点検実施ヘッダ)', [
        ['result_id','INT','PK AUTO','結果ID'],
        ['plan_id','INT','FK→inspection_plans','計画ID'],
        ['executed_date','VARCHAR(8)','NOT NULL','実施日'],
        ['summary_judge','VARCHAR(5)','','NORMAL/ABNORMAL/WATCH'],
        ['approval_status','VARCHAR(5)','','申請中/承認済/差戻'],
    ]),
    ('incidents (異常報告)', [
        ['incident_no','VARCHAR(20)','PK','INC-YYYYMMDD-NNN'],
        ['equipment_code','VARCHAR(20)','FK→equipment','対象設備'],
        ['incident_type','VARCHAR(10)','NOT NULL','異常種別'],
        ['severity','VARCHAR(5)','NOT NULL','軽微/中/重大/緊急'],
        ['status','VARCHAR(10)','NOT NULL','未了/調査中/対応中/完了/CAPA/close'],
    ]),
    ('counter_orders (対応指示ヘッダ)', [
        ['order_no','VARCHAR(20)','PK','CTR-YYYYMMDD-NNN'],
        ['incident_no','VARCHAR(20)','FK→incidents',''],
        ['status','VARCHAR(5)','NOT NULL','未了/処理中/完了'],
    ]),
    ('counter_order_details (対応指示明細)', [
        ['detail_id','INT','PK AUTO',''],
        ['order_no','VARCHAR(20)','FK→counter_orders',''],
        ['seq_no','INT','NOT NULL','行番号'],
        ['work_content','VARCHAR(500)','NOT NULL','作業内容'],
        ['status','VARCHAR(3)','NOT NULL','未了/完了'],
    ]),
    ('departments (部署マスタ)', [
        ['dept_code','VARCHAR(20)','PK','部署code'],
        ['dept_name','VARCHAR(100)','NOT NULL','部署名'],
        ['parent_dept_code','VARCHAR(20)','FK→departments','上位部署'],
        ['dept_level','INT','NOT NULL','1-4'],
        ['dept_type','VARCHAR(5)','NOT NULL','本社/支社/営業所/出張所'],
    ]),
    ('employees (担当者マスタ)', [
        ['emp_no','VARCHAR(20)','PK','EMP-NNNN'],
        ['name','VARCHAR(100)','NOT NULL','氏名'],
        ['dept_code','VARCHAR(20)','FK→departments','所属部署'],
        ['login_id','VARCHAR(50)','NOT NULL UNIQUE','loginID'],
        ['password_hash','VARCHAR(64)','NOT NULL','SHA-256'],
    ]),
    ('holidays (休日マスタ)', [
        ['holiday_id','INT','PK AUTO',''],
        ['holiday_date','VARCHAR(8)','NOT NULL UNIQUE','日付'],
        ['holiday_type','VARCHAR(10)','NOT NULL','法定/会社指定/点検停止'],
    ]),
    ('parts (保守部品マスタ)', [
        ['part_code','VARCHAR(20)','PK','部品code'],
        ['part_name','VARCHAR(100)','NOT NULL','部品名'],
        ['current_stock','INT','NOT NULL','現在庫数'],
        ['order_point','INT','','発注点'],
        ['safety_stock','INT','','安全在庫数'],
    ]),
    ('part_usages (部品使用実績)', [
        ['usage_id','INT','PK AUTO',''],
        ['part_code','VARCHAR(20)','FK→parts',''],
        ['equipment_code','VARCHAR(20)','FK→equipment',''],
        ['quantity','INT','NOT NULL','使用量'],
        ['stock_before','INT','NOT NULL','使用前在庫'],
        ['stock_after','INT','NOT NULL','使用後在庫'],
    ]),
]
for tname, cols in tables:
    db13 += [E(), S(tname), H(['カラム名', '型', '制約', '説明', '', '', '', ''])]
    for c in cols:
        db13.append(R(list(c) + ['']*(8-len(c))))

db13 += [E(), S('主要リレーション')]
rels = [
    'equipment.parent_equipment_code → equipment.equipment_code',
    'inspection_items.template_id → inspection_template.template_id',
    'inspection_items.parent_item_id → inspection_items.item_id',
    'inspection_plans.equipment_code → equipment.equipment_code',
    'inspection_results.plan_id → inspection_plans.plan_id',
    'incidents.equipment_code → equipment.equipment_code',
    'incident_timeline.incident_no → incidents.incident_no',
    'counter_orders.incident_no → incidents.incident_no',
    'counter_order_details.order_no → counter_orders.order_no',
    'departments.parent_dept_code → departments.dept_code',
    'employees.dept_code → departments.dept_code',
    'part_usages.part_code → parts.part_code',
    'part_usages.equipment_code → equipment.equipment_code',
]
for r in rels:
    db13.append(R([r] + ['']*7))
w13.add_sheet('DB設計書', db13, col_widths=[20,16,20,30,14,14,14,14])
w13.save('13_DB設計書.xlsx')

# ============ 14_共通部品設計書 ============
w14 = XlsxWriter()
w14.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w14.add_sheet('Sheet2', [])

c14 = []
t,m = T('共通部品設計書')
c14.append(t)
c14 += [
    S('文書管理情報', 8),
    K(['システムID', 'STRUTSLAB-001', '', '', '文書番号', 'DOC-COMMON-001', '', '']),
    K(['版数', '1.0', '', '', '作成日', '2026/05/23', '', '']),
    K(['作成者', 'システム開発部', '', '', '区分', '新規', '', '']),
]
c14 += [E(), S('Tilesレイアウト定義'), H(['template名', '継承', '属性', '説明', '', '', '', ''])]
tiles = [
    ['baseLayout','(root)','header, menu, body, footer','全画面共通base'],
    ['masterLayout','baseLayout','body=menuLayout','マスタ管理用'],
    ['wizardLayout','baseLayout','body=wizardContent','Wizard面用'],
    ['listLayout','baseLayout','body=searchArea+listArea','一覧面用'],
    ['inputLayout','baseLayout','body=formArea','入力面用'],
    ['popupLayout','(root)','header, body','Popup用'],
    ['printLayout','(root)','body','印刷用'],
]
for t in tiles: c14.append(R(list(t) + ['']*4))

c14 += [E(), S('共通JSPインクルード'), H(['ファイル名', 'パス', '役割', '', '', '', '', ''])]
jsps = [
    ['header.jsp','/WEB-INF/jsp/common/','title bar, login user表示'],
    ['menu.jsp','/WEB-INF/jsp/common/','モジュール別menu link'],
    ['footer.jsp','/WEB-INF/jsp/common/','copyright'],
    ['paging.jsp','/WEB-INF/jsp/common/','paging共通部品'],
    ['errorMessages.jsp','/WEB-INF/jsp/common/','<html:errors/>表示'],
    ['confirmDialog.jsp','/WEB-INF/jsp/common/','window.confirm用JS'],
    ['error.jsp','/WEB-INF/jsp/common/','isErrorPage=true'],
]
for j in jsps: c14.append(R(list(j) + ['']*5))

c14 += [E(), S('カスタムタグ'), H(['tag名', 'TLD', 'Java class', '機能', '', '', '', ''])]
tags = [
    ['eqp:treeSelect','eqp-tree.tld','EqpTreeSelectTag','設備tree選択(select)'],
    ['date:picker','date-picker.tld','DatePickerTag','日付入力補助(text+calendar Popup)'],
    ['app:sectionHeader','app-common.tld','SectionHeaderTag','section見出(anchor付)'],
    ['app:statusBadge','app-common.tld','StatusBadgeTag','status badge(色付span)'],
    ['app:inspectionChecklist','app-common.tld','InspectionChecklistTag','点検checklist 3階層table'],
    ['app:indexedRow','app-common.tld','IndexedRowTag','動的行(Indexed Properties反復)'],
    ['app:timeline','app-common.tld','TimelineTag','経過記録timeline表示'],
]
for t in tags: c14.append(R(list(t) + ['']*4))

c14 += [E(), S('MyBatis Mapper一覧'), H(['Mapper XML', 'Java Interface', '担当table', '', '', '', '', ''])]
mappers = [
    ['EquipmentMapper.xml','EqpDao.java','equipment'],
    ['InspectionTemplateMapper.xml','ChkItemDao.java','inspection_template, inspection_items'],
    ['InspectionPlanMapper.xml','PlanDao.java','inspection_plans'],
    ['InspectionResultMapper.xml','ExecDao.java','inspection_results, inspection_items_results'],
    ['IncidentMapper.xml','IncidentDao.java','incidents, incident_timeline, incident_attachments'],
    ['CounterOrderMapper.xml','CounterDao.java','counter_orders, counter_order_details'],
    ['CapaMapper.xml','CapaDao.java','capa_reports'],
    ['DeptMapper.xml','DeptDao.java','departments'],
    ['EmployeeMapper.xml','EmpDao.java','employees, employee_qualifications'],
    ['CalendarMapper.xml','CalendarDao.java','holidays'],
    ['PartsMapper.xml','PartsDao.java','parts, part_equipment_relations, part_usages'],
]
for m in mappers: c14.append(R(list(m) + ['']*5))
w14.add_sheet('共通部品設計書', c14, col_widths=[22,20,28,28,14,14,14,14])
w14.save('14_共通部品設計書.xlsx')

# ============ 15_エラーコード一覧 ============
w15 = XlsxWriter()
w15.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w15.add_sheet('Sheet2', [])

e15 = []
t,m = T('エラーコード一覧')
e15.append(t)
e15 += [
    S('文書管理情報', 8),
    K(['システムID', 'STRUTSLAB-001', '', '', '文書番号', 'DOC-ERR-001', '', '']),
    K(['版数', '1.0', '', '', '作成日', '2026/05/23', '', '']),
    K(['作成者', 'システム開発部', '', '', '', '', '', '']),
]
e15 += [E(), S('業務エラー'), H(['code', '区分', 'メッセージ(ja)', '発生条件', '', '', '', ''])]
biz_errs = [
    ['ERR-BIZ-001','重複','同じ設備codeが既に存在','設備code重複'],
    ['ERR-BIZ-002','重複','同じloginIDが既に使用','loginID重複'],
    ['ERR-BIZ-003','重複','指定範囲に既存休日','休日重複'],
    ['ERR-BIZ-006','状態不正','廃止済設備は親設備不可','廃止設備→親指定'],
    ['ERR-BIZ-007','状態不正','承認済点検結果は修正不可','承認済data修正'],
    ['ERR-BIZ-009','在庫不足','使用量が現在庫を超過','在庫超え使用'],
    ['ERR-BIZ-010','期限切れ','認定有効期限が切れています','期限切れ資格'],
    ['ERR-BIZ-012','操作不正','全明細完了していない','未了明細あり'],
    ['ERR-BIZ-013','操作不正','差戻理由未入力','理由未入力'],
    ['ERR-BIZ-014','過去日','過去日は指定不可','過去日指定'],
]
for e in biz_errs: e15.append(R(list(e) + ['']*4))

e15 += [E(), S('バリデーションエラー'), H(['code', '区分', 'メッセージ(ja)', '発生条件', '', '', '', ''])]
val_errs = [
    ['ERR-VAL-001','必須','{0}は必須','required'],
    ['ERR-VAL-002','最大長','{0}は{1}文字以内','maxlength'],
    ['ERR-VAL-003','数値範囲','{0}は{1}〜{2}','intRange'],
    ['ERR-VAL-004','形式','{0}は日付形式','date'],
    ['ERR-VAL-005','形式','フリガナはkatakana','mask'],
    ['ERR-VAL-006','一致','password不一致','confirm'],
    ['ERR-VAL-007','最小長','password8文字以上','minlength'],
    ['ERR-VAL-008','条件付必須','×判定時 実測値必須','conditional'],
    ['ERR-VAL-009','条件付必須','×/△判定時 所見必須','conditional'],
    ['ERR-VAL-010','条件付必須','推定原因必須','conditional'],
    ['ERR-VAL-011','条件付必須','対応内容必須','conditional'],
    ['ERR-VAL-014','file','file size{0}MB以下','file size'],
    ['ERR-VAL-015','file','file形式{0}のみ','file ext'],
    ['ERR-VAL-018','行数','明細1行以上必要','min rows'],
    ['ERR-VAL-019','行数','明細最大50行','max rows'],
]
for e in val_errs: e15.append(R(list(e) + ['']*4))

e15 += [E(), S('システムエラー'), H(['code', '区分', 'メッセージ(ja)', 'HTTP', '', '', '', ''])]
sys_errs = [
    ['ERR-SYS-001','DB接続','システムエラー発生 管理者連絡','500'],
    ['ERR-SYS-002','fileIO','file読書失敗','500'],
    ['ERR-SYS-003','認証','login失敗 ID/password確認','200'],
    ['ERR-SYS-004','session','session切れ 再login','200'],
    ['ERR-SYS-005','権限','操作権限なし','200'],
    ['ERR-SYS-006','404','pageなし','404'],
    ['ERR-SYS-007','500','予期せぬエラー','500'],
]
for e in sys_errs: e15.append(R(list(e) + ['']*4))
w15.add_sheet('エラーコード一覧', e15, col_widths=[14,12,30,24,14,14,14,14])
w15.save('15_エラーコード一覧.xlsx')

# ============ 16_UIテスト難点マップ ============
w16 = XlsxWriter()
w16.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w16.add_sheet('Sheet2', [])

t16 = []
t,m = T('UIテスト難点マップ')
t16.append(t)
t16 += [
    S('文書管理情報', 8),
    K(['システムID', 'STRUTSLAB-001', '', '', '文書番号', 'DOC-TEST-001', '', '']),
    K(['版数', '1.0', '', '', '作成日', '2026/05/23', '', '']),
    K(['作成者', 'システム開発部', '', '', '目的', '全画面×テスト難点カテゴリmatrix', '', '']),
]
t16 += [E(), S('テスト難点カテゴリ'), H(['ID', 'カテゴリ', '説明', 'Selenium困難性', '', '', '', ''])]
cats = [
    ['C01','Stale Element','動的行追加/削除(Submit→再描画)で既存要素参照無効','高'],
    ['C02','Indexed Properties','name属性 details[{n}].field 形式','高'],
    ['C03','Wizard','multi step data持回り+戻る→修正→進む','高'],
    ['C04','条件付validation','状態により必須項目変化','高'],
    ['C05','Popup→親画面','window.open→選択→opener値反映','中'],
    ['C06','File Upload','添付file+同時validation','中'],
    ['C07','一括操作+confirm','window.confirm OK/Cancel','低'],
    ['C08','動的column','期間によりcolumn数変動','中'],
    ['C09','nest table','大分類rowspan→中分類→項目','中'],
    ['C10','印刷用別window','window.open→別window内容検証','中'],
    ['C11','CSS色分け','badge/cell色class検証','低'],
    ['C12','paging複合','頁跨操作+全選択+状態変動','中'],
    ['C13','CSV出力','download file内容検証','低'],
    ['C14','session切れ','session timeout後挙動','中'],
    ['C15','多段階遷移','未了→調査中→対応中→完了→CAPA','高'],
]
for c in cats: t16.append(R(list(c) + ['']*4))

t16 += [E(), S('画面×テスト難点マトリクス'), H(['No.','画面名','C01','C02','C03','C04','C05','C06','C07','C08','C09','C10','C11','C12','C13','C14','C15'])]
matrix = [
    ['1','設備マスタ一覧','','','','','','','○','','','','○','○','○','',''],
    ['2','設備マスタ登録編集','','','','○','○','○','','','','','','','','',''],
    ['3','点検項目マスタ一覧','○','','','','','','○','','','','○','','','',''],
    ['4','点検項目マスタ登録編集','○','○','','','','','','','○','','','','','',''],
    ['5','年間点検計画一覧','','','','','','','','○','','','○','','','',''],
    ['6','点検計画登録Wizard','','','◎','○','','','','','','','','','','○',''],
    ['7','点検実施一覧(当日)','','','','','','','','','','','○','','','',''],
    ['8','点検実施入力','','','','◎','','○','','','○','','','','','',''],
    ['9','点検実施詳細修正','','','','○','','','○','','','','','','','',''],
    ['10','点検実施承認一覧','','','','○','','','○','','','','','○','','',''],
    ['11','異常報告一覧','','','','','','','○','','','','','○','○','○',''],
    ['12','異常報告登録','','','','','','○','','','','','','','','',''],
    ['13','異常報告詳細','','','','◎','','','','','','','','','','','◎'],
    ['14','対応指示登録','◎','◎','','','○','','','','','','','','','',''],
    ['15','対応指示一覧','','','','','','','○','','','','○','','○','○','',''],
    ['16','対応指示詳細完了','○','○','','○','','','','','','','','','','',''],
    ['17','是正処置報告書','','','','','','','','','','','','','','','',''],
    ['18','総合レポート','','','','','','','','','','','○','','','','',''],
    ['20','部署マスタ登録編集','','','','','○','','','','','','','','','',''],
    ['22','担当者マスタ登録編集','','','','','','','','','','','','','','',''],
    ['23','休日カレンダー一覧','','','','','','','','○','','','○','','','',''],
    ['26','保守部品登録編集','','','','','','○','','','','','','','','',''],
]
for m in matrix: t16.append(R(list(m) + ['']*(16-len(m))))
t16.append(E())
t16.append(R(['凡例: ◎=高度難点(組合わせ爆発), ○=難点あり', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '']))
w16.add_sheet('UIテスト難点マップ', t16, col_widths=[4,18,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4])
w16.save('16_UIテスト難点マップ.xlsx')

print("12-16 Done.")
