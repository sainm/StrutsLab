#!/usr/bin/env python3
"""Generate 08-16 styled Excel design docs."""
import sys; sys.path.insert(0, '.')
from _xlsx_gen import *

def screen_sheet(name, sid, pg, sub, layout, els, tbl=None, smap=None, tests=None):
    d = []
    t,m = T(f'画面設計書 — {name}')
    d.append(t)
    d += [
        S('文書管理情報', 8),
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

def pg_doc(pg_id, pg_name, sub, overview, io_def, logic, valid_rules, transitions=None, smap=None):
    d = []
    t,m = T(f'プログラム設計書 — {pg_name}')
    d.append(t)
    d += [
        S('文書管理情報', 8),
        K(['システムID', 'STRUTSLAB-001', '', '', 'サブシステム名', sub, '', '']),
        K(['プログラムID', pg_id, '', '', 'プログラム名', pg_name, '', '']),
        K(['文書番号', f'DOC-PG-{pg_id}', '', '', '版数', '1.0', '', '']),
        K(['作成日', '2026/05/23', '', '', '作成者', 'システム開発部', '', '']),
        K(['区分', '新規', '', '', '', '', '', '']),
    ]
    d += [E(), S('プログラム概要'), R([overview] + ['']*7)]
    d += [E(), S('入出力定義'), H(['区分', '項目名', '型', 'Formプロパティ', '必須', '備考', '', ''])]
    for r in io_def: d.append(R(list(r) + ['']*(8-len(r))))
    d += [E(), S('処理詳細'), H(['No.', '処理内容', '関連DAO', '備考', '', '', '', ''])]
    for i, r in enumerate(logic, 1):
        d.append(R([str(i), r[0], r[1] if len(r)>1 else '', r[2] if len(r)>2 else ''] + ['']*4))
    d += [E(), S('入力検証ルール'), H(['No.', '対象項目', '検証ルール', 'エラーメッセージ(ja)', '', '', '', ''])]
    for i, r in enumerate(valid_rules, 1):
        d.append(R([str(i)] + list(r) + ['']*(7-len(r))))
    if transitions:
        d += [E(), S('状態遷移'), H(['遷移元', '遷移先', 'トリガー', '', '', '', '', ''])]
        for t in transitions: d.append(R(list(t) + ['']*(8-len(t))))
    if smap:
        d += [E(), S('struts-config.xml'), H(['項目', '値', '', '', '', '', '', ''])]
        for k,v in smap.items(): d.append(R([k, v] + ['']*6))
    return d

# ============ 08_プログラム設計書_マスタ管理 ============
w08 = XlsxWriter()
w08.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w08.add_sheet('Sheet2', [])

w08.add_sheet('PG-MST-EQP-001', pg_doc('PG-MST-EQP-001','設備マスタ一覧','マスタ管理',
    '設備マスタの検索・一覧表示。複合検索(5条件AND)、paging、CSV出力、選択削除。session scopeで検索条件保持。',
    [['入力','設備種別','String','eqpSearchType','','select'],['入力','電圧階級','String','eqpSearchVoltage','',''],
     ['入力','設置年from/to','String','eqpSearchYearFrom/To','',''],['入力','保全ランク','String','eqpSearchRank','',''],
     ['入力','担当部署','String','eqpSearchDept','','部分一致'],['出力','検索結果','List<EqpDto>','eqpList','','']],
    [['Form値→検索条件DTO構築(未入力条件null→WHERE除外)','','MyBatis <if>動的SQL'],
     ['DAO.search()実行','EqpDao.search()',''],['paging: 総件数→offset計算','EqpDao.count()','LIMIT/OFFSET'],
     ['Request属性設定→JSP描画','','']],
    [['eqpSearchYearFrom/To','YYYY形式, from≦to','設置年の範囲が正しくありません'],['eqpSearchDept','最大50文字','']],
    [['一覧','検索結果表示','検索/クリア'],['一覧','CSV出力','CSV出力'],['一覧','編集画面','click'],['一覧','削除→再表示','選択削除+confirm']],
    {'action path':'/mst/eqp/list','action class':'EqpListAction','form bean':'EqpSearchForm','scope':'session'}))

w08.add_sheet('PG-MST-EQP-002', pg_doc('PG-MST-EQP-002','設備マスタ登録・編集','マスタ管理',
    '設備マスタの新規登録・編集。5section大form、親設備Popup選択、添付file処理。DispatchAction。',
    [['入力','設備名','String','eqpName','○',''],['入力','設備種別','String','eqpType','○',''],
     ['入力','設備status','String','eqpStatus','○',''],['入力','電圧階級','String','eqpVoltage','○',''],
     ['入力','親設備code','String','eqpParentCode','','Popup'],['入力','保全ランク','String','eqpRank','○',''],
     ['入力','点検周期','Integer','eqpInterval','','1-120'],['入力','添付file','FormFile','eqpFile','','']],
    [['編集時 既存data取得(新規skip)','EqpDao.findById()',''],['Form→DTO→Validator','','error時INPUT'],
     ['親設備存在+状態check(廃止不可)','EqpDao.findById()',''],['新規:insert/編集:update','EqpDao',''],
     ['添付file→filesystem','','/attachments/eqp/']],
    [['eqpName','空不可 max100','設備名を入力してください'],['eqpType','空不可',''],
     ['eqpInterval','1〜120','点検周期は1〜120ヶ月'],['eqpParentCode','廃止不可',''],
     ['eqpFile','max10MB pdf/jpg/png','']],
    [['表示','編集中','入力'],['編集中','error→再表示','保存→NG'],['編集中','一覧','保存→OK'],['編集中','Popup','選択']],
    {'action path':'/mst/eqp/save','action class':'EqpSaveAction(DispatchAction)','form bean':'EqpForm(ValidatorForm)','scope':'request'}))

w08.add_sheet('PG-MST-CHK-001', pg_doc('PG-MST-CHK-001','点検項目マスタ一覧','マスタ管理',
    '点検項目templateの検索・一覧。template copy、並順変更(sort_order swap)。',
    [['入力','設備種別','String','chkSearchType','',''],['入力','点検種別','String','chkSearchKind','',''],
     ['出力','template list','List<ChkTmplDto>','tmplList','','']],
    [['検索条件→DAO.search()','ChkItemDao.search()',''],['並順変更:指定行と前後行sort_order swap','ChkItemDao.swapOrder()',''],
     ['copy:名称「_コピー」付加→全項目複製','ChkItemDao.copy()','重複時「_コピー2」']],
    [['chkSearchType','空可',''],['chkSearchKind','空可','']],
    [['一覧','検索結果表示','検索'],['一覧','並順変更→再表示','▲/▼'],['一覧','copy→再表示','copy'],['一覧','編集','click']],
    {'action path':'/mst/chkitem/list','action class':'CheckItemListAction','form bean':'CheckItemSearchForm','scope':'session'}))

w08.add_sheet('PG-MST-CHK-002', pg_doc('PG-MST-CHK-002','点検項目マスタ登録編集','マスタ管理',
    '点検項目templateの3階層tree編集。大分類→中分類→個別項目の動的行追加/削除(Submit→再描画)。Indexed+Nested。',
    [['入力','template名','String','tmplName','○',''],['入力','設備種別','String','eqpType','○',''],
     ['入力','点検種別','String','checkKind','○',''],['入力','大分類[{n}].name','String[]','cat1Names','','Indexed'],
     ['入力','中分類[{n}][{m}]','String[][]','cat2Names','','Nested'],['入力','項目[{i}].*','ItemDto[]','items','','Indexed']],
    [['動的行操作判定(addRow/delRow/save)','',''],['行追加:配列size+1→空data→INPUT forward','','既存行index維持'],
     ['行削除:指定index除去→詰直','',''],['保存:3階層→transaction','ChkItemDao','大→中→項目順']],
    [['tmplName','空不可 重複不可',''],['cat1Names[{n}]','空不可',''],['items[{i}].name','空不可','']],
    [['表示','行追加→再表示','+'],['表示','行削除→再表示','削除'],['表示','保存→一覧','保存'],['表示','一括追加','一括項目追加']],
    {'action path':'/mst/chkitem/save','action class':'CheckItemSaveAction','form bean':'CheckItemForm(ValidatorForm)','scope':'request'}))
w08.save('08_プログラム設計書_マスタ管理.xlsx')

# ============ 09_プログラム設計書_点検計画・実施 ============
w09 = XlsxWriter()
w09.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w09.add_sheet('Sheet2', [])

w09.add_sheet('PG-INS-PLAN-001', pg_doc('PG-INS-PLAN-001','年間点検計画一覧','点検計画',
    '年度×設備×月matrix形式で点検計画と実績を表示。動的column(12ヶ月)table生成。計画lock/解除。',
    [['入力','年度','String','planYear','○',''],['入力','設備種別','String','eqpType','',''],
     ['出力','matrix data','Map','monthlyMatrix','','行=設備 列=月']],
    [['年度→会計期間(4月〜3月)開始月計算','',''],['対象設備一覧→行生成','EqpDao.findByType()',''],
     ['各月計画/実績count','PlanDao.countMonthly()','GROUP BY'],['JSP matrix描画','',''],
     ['計画lock:plan_locked=1','PlanDao.lockYear()','']],
    [['planYear','空不可',''],['eqpType','空可','']],
    [['表示','matrix表示','表示'],['表示','計画lock','lock'],['表示','計画解除','解除']],
    {'action path':'/ins/plan/yearly','action class':'YearlyPlanAction','form bean':'YearlyPlanForm'}))

w09.add_sheet('PG-INS-PLAN-002', pg_doc('PG-INS-PLAN-002','点検計画登録Wizard','点検計画',
    '4stepWizard方式の点検計画登録。step間data hidden持回り。一時保存→session復元可能。',
    [['入力(全step)','wizardForm','PlanWizardForm','planWizardForm','○','hidden持回り'],
     ['Step1出力','選択設備','EqpDto','selectedEqp','',''],['Step2出力','template一覧','List','tmplList','',''],
     ['Step3出力','担当者一覧','List','empList','','班連動'],['Step4出力','確認data','PlanConfirmDto','confirmData','','']],
    [['step param分岐(step1/2/3/confirm)','',''],['Step1:設備選択→validation→step2','',''],
     ['Step2:template選択→step3','',''],['Step3:日程・担当者→validation→confirm','','班→担当者連動'],
     ['Step4:全入力表示→一時保存or確定','','一時保存=session 確定=DB'],
     ['途中離脱再開:session復元(切時最初)','',''],['「戻る」:前step(hidden値維持,修正→再進可)','','']],
    [['Step1:selectedEqp','空不可',''],['Step2:selectedTmpl','空不可',''],['Step3:planDate','未来日','']],
    [['Step1','Step2','進む'],['Step2','Step1','戻る'],['Step2','Step3','進む'],['Step3','Step2','戻る'],
     ['Step3','確認','進む'],['確認','Step3','戻る'],['確認','一覧','確定'],['確認','session','一時保存']],
    {'action path':'/ins/plan/wizard','action class':'PlanWizardAction','form bean':'PlanWizardForm','scope':'session'}))

w09.add_sheet('PG-INS-EXEC-001', pg_doc('PG-INS-EXEC-001','点検実施入力','点検実施',
    'nest checklist形式で点検結果入力。判定値(○/×/△)条件付validation。写真添付(max5枚/項目)。総合判定異常→異常報告。',
    [['入力','判定[{i}]','String[]','execJudge','○','radio'],['入力','実測値[{i}]','String[]','execValue','×時必須',''],
     ['入力','所見[{i}]','String[]','execNote','×/△時必須',''],['入力','写真[{i}][{j}]','FormFile[][]','execPhoto','',''],
     ['入力','総合判定','String','summaryJudge','○',''],['入力','総合所見','String','summaryNote','','']],
    [['点検項目master→checklist構築','ChkItemDao.findByTmpl()','3階層'],['各項目判定応validation','Validator+Action内追加',''],
     ['×→実測値+所見必須 △→所見必須 ○→任意','',''],['写真保存:項目{i}/写真{j}→filesystem','',''],
     ['総合判定異常→異常報告用data→session','','Forward:inc.create']],
    [['execJudge[{i}]','空不可',''],['execValue[{i}]','×時:空不可 数値',''],['execNote[{i}]','×/△時:空不可',''],
     ['summaryJudge','空不可',''],['execPhoto','jpg/png max5MB','']],
    [['表示','判定入力','各項目判定'],['判定入力','error→再表示','保存→NG'],['判定入力','保存→一覧','保存→OK'],
     ['判定入力','異常報告登録','異常→異常報告へ']],
    {'action path':'/ins/exec/input','action class':'ExecInputAction','form bean':'ExecForm(ValidatorForm)'}))

w09.add_sheet('PG-INS-EXEC-002', pg_doc('PG-INS-EXEC-002','点検実施詳細・修正','点検実施',
    '実施済点検結果照会と修正申請。修正理由必須。承認flow連携。',
    [['入力','修正理由','String','modifyReason','申請時○',''],['出力','実施結果全data','ExecResultDto','execResult','','']],
    [['指定点検ID→全data取得','ExecDao.findById()',''],['修正申請:理由必須→approval_status=申請中','ExecDao.requestModify()',''],
     ['承認済data修正不可(button非表示)','','']],
    [['modifyReason','申請時:空不可 max500','']],
    [['表示','修正申請→申請中','修正申請→理由OK'],['表示','申請中→操作不可','承認待ち'],
     ['申請中','承認→表示(修正反映)','承認者操作'],['申請中','差戻→表示(取消)','差戻']],
    {'action path':'/ins/exec/detail','action class':'ExecDetailAction','form bean':'ExecForm'}))

w09.add_sheet('PG-INS-APPR-001', pg_doc('PG-INS-APPR-001','点検実施承認一覧','点検実施',
    '点検結果修正申請の承認/差戻一覧。一括操作。差戻時理由必須。操作後通知message。',
    [['入力','期間from/to','String','apprDateFrom/To','',''],['入力','担当班','String','apprTeam','',''],
     ['出力','承認待ちlist','List<ApprovalDto>','approvalList','','']],
    [['検索条件→承認待ち一覧','ExecDao.findPendingApprovals()',''],['一括承認:選択行status=承認済','ExecDao.bulkApprove()',''],
     ['一括差戻:理由→reject_reason→status=差戻','ExecDao.bulkReject()',''],['操作結果message→request','','']],
    [['apprDateFrom/To','空可 YYYYMMDD',''],['rejectReason','一括差戻時:空不可','']],
    [['一覧','一括承認→通知','承認→confirmOK'],['一覧','一括差戻→理由→通知','差戻→confirm→理由必須'],
     ['一覧','検索結果表示','検索'],['一覧','全件表示','クリア']],
    {'action path':'/ins/approval/list','action class':'ApprovalListAction','form bean':'ApprovalForm'}))
w09.save('09_プログラム設計書_点検計画・実施.xlsx')

# ============ 10_プログラム設計書_異常報告・対応指示 ============
w10 = XlsxWriter()
w10.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w10.add_sheet('Sheet2', [])

w10.add_sheet('PG-INC-LIST-001', pg_doc('PG-INC-LIST-001','異常報告一覧','異常報告',
    '異常報告の複合検索(7条件AND)・一覧。検索条件保存/呼出、一括status更新、CSV/PDF出力。',
    [['入力','発生日from/to','String','incDateFrom/To','',''],['入力','設備種別','String','incEqpType','',''],
     ['入力','異常種別','String','incType','',''],['入力','status','String','incStatus','',''],
     ['入力','重大度','String','incSeverity','',''],['入力','keyword','String','incKeyword','','部分一致'],
     ['出力','検索結果','List<IncidentDto>','incidentList','','']],
    [['検索条件DTO→動的SQL','','MyBatis <where><if>'],['DAO.search()(JOIN equipment)','IncidentDao',''],
     ['検索条件保存:session','','save/loadCondition'],['一括status更新','IncidentDao.bulkUpdateStatus()',''],
     ['CSV出力:結果→CSV stream→HttpServletResponse','','']],
    [['incDateFrom/To','空可 YYYYMMDD from≦to',''],['incEqpType','空可',''],['incKeyword','max100','']],
    [['表示','検索結果表示','検索'],['表示','条件保存','検索条件保存'],['表示','一括更新→再表示','実行→confirmOK'],['表示','CSV/PDF','出力']],
    {'action path':'/inc/list','action class':'IncidentListAction','form bean':'IncidentSearchForm','scope':'session'}))

w10.add_sheet('PG-INC-CREATE-001', pg_doc('PG-INC-CREATE-001','異常報告登録','異常報告',
    '異常報告新規登録。点検実施data引継、類似事例検索(同面内表示)、file添付。',
    [['入力','発生日時','String','incDateTime','○','引継'],['入力','発見者','String','finder','○',''],
     ['入力','異常種別','String','incType','○',''],['入力','重大度','String','severity','○',''],
     ['入力','異常部位','String','incPart','○',''],['入力','異常詳細','String','incDetail','○','textarea'],
     ['入力','添付file','FormFile[]','incFiles','','']],
    [['引継data表示(hidden/session)','','fromInspection判定'],['類似事例検索:種別+部位→全文→同面下部','IncidentDao.searchSimilar()',''],
     ['登録:Form→DTO→insert(報告番号INC-YYYYMMDD-NNN)','IncidentDao.insert()',''],['添付file保存','','']],
    [['incDateTime','空不可',''],['incType','空不可',''],['severity','空不可',''],['incPart','空不可',''],
     ['incDetail','空不可 max2000',''],['incFiles','jpg/png/pdf max10MB','']],
    [['表示','類似事例検索→同面下部','類似事例検索'],['表示','登録→一覧','登録'],
     ['表示','一時保存→表示継続','一時保存'],['表示','error→再表示','登録→NG']],
    {'action path':'/inc/create','action class':'IncidentCreateAction','form bean':'IncidentForm(ValidatorForm)'}))

w10.add_sheet('PG-INC-DETAIL-001', pg_doc('PG-INC-DETAIL-001','異常報告詳細','異常報告',
    '異常報告詳細表示と多段階status遷移。各遷移時条件付必須項目変化。timeline表示。',
    [['入力','推定原因','String','cause','調査中→対応中時○','条件付'],['入力','対応内容','String','counterDetail','対応中→完了時○','条件付'],
     ['出力','異常報告全data','IncidentDto','incident','',''],['出力','timeline','List<TimelineDto>','timeline','','時系列sort']],
    [['異常報告ID→全data','IncidentDao.findById()',''],['timeline(時系列sort)','IncidentDao.getTimeline()',''],
     ['status遷移:現在status→遷移先','','未了→調査中→対応中→完了→close/CAPA'],
     ['調査→対応:推定原因must','','Validator+Action内追加'],['対応→完了:対応内容must','',''],
     ['完了→CAPA:是正処置画面へ','','Forward:counter.capa']],
    [['cause','調査中→対応中時:空不可',''],['counterDetail','対応中→完了時:空不可','']],
    [['未了','調査中','調査開始'],['調査中','対応中','対応開始(推定原因必須)'],
     ['対応中','完了','完了(対応内容必須)'],['完了','close','close'],['完了','是正処置','CAPA']],
    {'action path':'/inc/detail','action class':'IncidentDetailAction','form bean':'IncidentForm','parameter':'method'}))

w10.add_sheet('PG-CTR-CREATE-001', pg_doc('PG-CTR-CREATE-001','対応指示登録','対応指示',
    '対応指示新規登録。Header+明細親子構造、Indexed Properties動的行管理、担当者Popup選択。全明細完了check。',
    [['入力','指示日','String','ctrDate','○',''],['入力','指示者','String','issuer','○',''],
     ['入力','優先度','String','overallPriority','○',''],['入力','明細[{n}].作業内容','String[]','details[{n}].workContent','○','Indexed'],
     ['入力','明細[{n}].担当者','String[]','details[{n}].person','○','Popup'],['入力','明細[{n}].期限','String[]','details[{n}].deadline','','']],
    [['動的行操作判定(addRow/delRow/save)','',''],['行追加:配列size+1→INPUT(最下行空行追加)','','既存行index維持'],
     ['行削除:指定index除去→詰直','',''],['保存:header+全明細transaction(CTR-YYYYMMDD-NNN)','CounterDao',''],
     ['全明細完了→header自動完了','','']],
    [['details[{n}].workContent','空不可',''],['details[{n}].person','空不可',''],
     ['details[{n}].deadline','YYYYMMDD 過去不可',''],['詳細行数','1行以上 max50','']],
    [['表示','行追加→再表示','+'],['表示','行削除→再表示','削除'],['表示','Popup→値反映','選択'],['表示','保存→一覧','登録']],
    {'action path':'/counter/create','action class':'CounterCreateAction','form bean':'CounterForm(ValidatorForm)','parameter':'method'}))

w10.add_sheet('PG-CTR-DETAIL-001', pg_doc('PG-CTR-DETAIL-001','対応指示詳細・完了','対応指示',
    '対応指示詳細表示と各明細個別完了報告。完了時実績入力行展開。全明細完了→header自動完了。部品選択→在庫alert。',
    [['入力','明細[{n}].実績時間','String[]','actualHours','完了時○',''],['入力','明細[{n}].使用部品','String[]','usedPart','',''],
     ['入力','明細[{n}].使用量','Integer[]','usedQty','',''],['入力','明細[{n}].所見','String[]','note','完了時○','']],
    [['指示ID→全data','CounterDao.findById()',''],['完了報告展開:指定明細完了form表示','','INPUT forward'],
     ['明細完了:status=完了 実績保存→在庫引当','',''],['全明細完了→header自動完了','',''],
     ['部品選択→在庫check→発注点下回警告','PartsDao.checkStock()','警告のみ']],
    [['actualHours','0より大',''],['usedQty','0より大',''],['note','空不可','']],
    [['表示','完了報告展開','完了報告'],['展開','入力→保存→展開閉','保存'],['個別完了','全明細完了→header自動完了','最終明細完了時']],
    {'action path':'/counter/detail','action class':'CounterDetailAction','form bean':'CounterDetailForm'}))

w10.add_sheet('PG-CTR-CAPA-001', pg_doc('PG-CTR-CAPA-001','是正処置報告書','是正処置',
    '異常報告派生是正処置報告書登録。なぜなぜ分析5段階、再発防止策、効果確認。',
    [['入力','なぜ①〜⑤','String[]','why[1]〜why[5]','○','textarea×5'],['入力','再発防止策','String','countermeasure','○',''],
     ['入力','効果確認方法','String','verifyMethod','○',''],['入力','効果確認期限','String','verifyDate','○','YYYYMMDD 未来日']],
    [['異常報告data→初期表示','IncidentDao.findById()','引継'],['なぜなぜ:5段階入力→保存','',''],
     ['再発防止策・効果確認→承認申請','CapaDao.insert()','']],
    [['why[1]〜[5]','空不可',''],['countermeasure','空不可 max2000',''],['verifyMethod','空不可 max1000',''],
     ['verifyDate','YYYYMMDD 未来日','']],
    [['表示','入力→登録→異常報告詳細','登録'],['表示','error→再表示','登録→NG']],
    {'action path':'/counter/capa/create','action class':'CapaAction','form bean':'CapaForm(ValidatorForm)'}))
w10.save('10_プログラム設計書_異常報告・対応指示.xlsx')

# ============ 11_プログラム設計書_組織・カレンダー・保守部品 ============
w11 = XlsxWriter()
w11.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w11.add_sheet('Sheet2', [])

w11.add_sheet('PG-ORG-DEPT', pg_doc('PG-ORG-DEPT','部署マスタ管理','組織管理',
    '部署master一覧(4階層tree表示)、登録・編集、統廃合履歴管理。上位部署tree Popup選択。過去日異動警告。',
    [['入力','部署名','String','deptName','○',''],['入力','上位部署','String','parentDept','','Popup'],
     ['入力','有効開始日','String','startDate','○',''],['入力','部署種別','String','deptType','○','']],
    [['tree表示:4階層再帰構築(階層=indent幅)','DeptDao.findAll()',''],['登録:code自動採番→INSERT','DeptDao.insert()',''],
     ['過去日異動check:開始日<本日→警告(保存可)','','']],
    [['deptName','空不可 重複不可',''],['endDate','startDate<endDate','']],
    [['一覧','編集','click'],['一覧','子部署追加','+'],['編集','tree Popup','上位部署選択']],
    {'action path':'/org/dept/*','action class':'DeptListAction / DeptSaveAction','form bean':'DeptSearchForm / DeptForm'}))

w11.add_sheet('PG-ORG-EMP', pg_doc('PG-ORG-EMP','担当者マスタ管理','組織管理',
    '担当者master一覧(複合検索)、登録・編集(5section大form)。資格毎期限管理。loginID重複check。password SHA-256。',
    [['入力','氏名','String','empName','○',''],['入力','フリガナ','String','empKana','','katakana'],
     ['入力','部署','String','empDept','○',''],['入力','保有資格[]','String[]','empQual','','checkbox'],
     ['入力','点検員ランク','String','insRank','',''],['入力','認定有効期限','String','insExpire','',''],
     ['入力','loginID','String','loginId','○','重複不可'],['入力','password','String','password','○','']],
    [['社員番号自動採番(EMP-NNNN)→5section Form','',''],['資格毎期限validation','','条件付'],
     ['loginID重複check','EmpDao.countByLoginId()',''],['password:確認一致→SHA-256→保存','','']],
    [['empName','空不可',''],['empKana','katakanaのみ',''],['loginId','重複不可',''],
     ['password','8文字以上',''],['insExpire','未来日','']],
    [['表示','5section編集',''],['編集','保存→一覧','保存'],['編集','error→再表示','保存→NG']],
    {'action path':'/org/emp/*','action class':'EmpListAction / EmpSaveAction','form bean':'EmpSearchForm / EmpForm(ValidatorForm)'}))

w11.add_sheet('PG-CAL', pg_doc('PG-CAL','休日カレンダー管理','カレンダー管理',
    '休日calendar月別table表示(色分け)、範囲指定一括登録、重複check、振替出勤日設定。',
    [['入力','年度','String','calYear','○',''],['入力','日付範囲from/to','String','calDateFrom/To','○',''],
     ['入力','休日種別','String','calType','○',''],['入力','振替日','String','transferFrom/To','','']],
    [['月別calendar描画:日付計算→曜日→週割→7列×5-6行','',''],['休日色分け:法定=赤 会社=青 停止=黄','','CSS class'],
     ['範囲一括登録:from〜to全date INSERT(重複→error)','CalDao.bulkInsert()',''],['振替設定:休日→出勤日登録','','']],
    [['calDateFrom/To','from≦to',''],['calDateFrom/To','重複不可',''],['transferFrom','休日であること',''],['transferTo','休日でないこと','']],
    [['表示','月別calendar','年度選択→表示'],['一覧','登録/編集','日付click'],['登録','一括登録→一覧','一括登録']],
    {'action path':'/cal/*','action class':'CalendarListAction / CalendarSaveAction','form bean':'CalendarForm / CalendarRegForm'}))

w11.add_sheet('PG-PRT', pg_doc('PG-PRT','保守部品管理','保守部品管理',
    '保守部品一覧・登録・編集、使用実績管理。在庫alert(発注点下回)。適用設備checkbox tree選択。',
    [['入力','部品名','String','prtName','○',''],['入力','部品種別','String','prtType','',''],
     ['入力','適用設備[]','String[]','prtEqp','','checkbox tree'],['入力','発注点','Integer','orderPoint','',''],
     ['入力','安全在庫数','Integer','safetyStock','',''],['入力','単価','Integer','price','','']],
    [['一覧:在庫<発注点→僅少badge(黄) =0→切badge(赤)','','CSS class'],['登録:適用設備tree→複数選択→保存','',''],
     ['使用実績:点検/修繕→部品使用→在庫自動減算','PartsDao.updateStock()',''],['在庫警告:使用後在庫≦発注点→alert','','警告のみ']],
    [['prtName','空不可',''],['orderPoint','0以上',''],['safetyStock','orderPoint>safetyStock','']],
    [['一覧','在庫alert表示',''],['編集','適用設備tree選択',''],['使用実績','在庫自動減算→警告判定',''],['登録/編集','保存→一覧','保存']],
    {'action path':'/parts/*','action class':'PartsXxxAction','form bean':'PartsXxxForm'}))
w11.save('11_プログラム設計書_組織・カレンダー・保守部品.xlsx')

print("08-11 Done.")
