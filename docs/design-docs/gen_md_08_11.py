#!/usr/bin/env python3
"""Generate 08-11 program design MD files."""
import os

OUT = '/home/lh/source/StrutsLab/docs/design-docs'

def write_md(filename, lines):
    path = os.path.join(OUT, filename)
    with open(path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines) + '\n')
    print(f'  -> {filename}')

def kv_table(pairs):
    out = ['| 項目 | 値 |', '| --- | --- |']
    for k, v in pairs:
        out.append(f'| {k} | {v} |')
    out.append('')
    return out

def table(headers, rows):
    out = ['| ' + ' | '.join(headers) + ' |', '| ' + ' | '.join(['---'] * len(headers)) + ' |']
    for row in rows:
        padded = list(row) + [''] * (len(headers) - len(row))
        out.append('| ' + ' | '.join(str(c) for c in padded) + ' |')
    out.append('')
    return out

def pg_section(lines, pg_id, pg_name, subsystem, overview, io_def, logic, valid_rules, transitions=None, struts_map=None):
    lines.append(f'## {pg_name} ({pg_id})')
    lines.append('')
    lines += kv_table([
        ('プログラムID', pg_id), ('プログラム名', pg_name),
        ('サブシステム', subsystem), ('版数', '1.0'), ('作成日', '2026/05/23'),
    ])
    lines.append('### プログラム概要')
    lines.append('')
    lines.append(overview)
    lines.append('')
    lines.append('### 入出力定義')
    lines.append('')
    lines += table(['区分', '項目名', '型', 'Formプロパティ', '必須', '備考'], io_def)
    lines.append('### 処理詳細（Action.execute フロー）')
    lines.append('')
    lines += table(['No.', '処理内容', '関連DAO', '備考'], [[str(i+1)] + list(r) + ['']*(3-len(r)) for i, r in enumerate(logic)])
    lines.append('### 入力検証ルール')
    lines.append('')
    lines += table(['No.', '対象項目', '検証ルール', 'エラーメッセージ（ja）'], [[str(i+1)] + list(r) + ['']*(4-len(r)) for i, r in enumerate(valid_rules)])
    if transitions:
        lines.append('### 状態遷移')
        lines.append('')
        lines += table(['遷移元', '遷移先', 'トリガー'], transitions)
    if struts_map:
        lines.append('### struts-config.xml マッピング')
        lines.append('')
        lines += kv_table(struts_map)
    lines.append('---')
    lines.append('')
    return lines

# ===== 08_プログラム設計書_マスタ管理.md =====
lines08 = ['# 08 プログラム設計書 — マスタ管理', '']

pg_section(lines08, 'PG-MST-EQP-001', '設備マスタ一覧', 'マスタ管理',
    '設備マスタの検索・一覧表示を行う。複合検索（5条件AND）、ページング、CSV出力、選択削除機能を持つ。',
    [['入力','設備種別','String','eqpSearchType','','select'],['入力','電圧階級','String','eqpSearchVoltage','',''],
     ['入力','設置年(from)','String','eqpSearchYearFrom','',''],['入力','設置年(to)','String','eqpSearchYearTo','',''],
     ['入力','保全ランク','String','eqpSearchRank','',''],['入力','担当部署','String','eqpSearchDept','','部分一致'],
     ['出力','検索結果リスト','List<EqpDto>','eqpList','',''],['出力','ページ情報','PagingInfo','pagingInfo','','']],
    [['HttpServletRequestからFormにバインド済み値を解析','',''],
     ['Form値から検索条件DTO構築（未入力条件はnull→WHERE句除外）','','MyBatis <if>動的SQL'],
     ['DAO.search() 実行','EqpDao.search()',''],
     ['ページング: 総件数取得→offset計算','EqpDao.count()','LIMIT/OFFSET'],
     ['リクエスト属性に検索結果とページ情報を設定','',''],
     ['Forward: eqp.list (JSP再描画)','','']],
    [['eqpSearchType','空可',''],['eqpSearchVoltage','空可',''],
     ['eqpSearchYearFrom','YYYY形式、from≦to','設置年の範囲が正しくありません'],
     ['eqpSearchYearTo','YYYY形式',''],['eqpSearchDept','最大50文字','']],
    [['一覧表示','検索結果表示','検索/クリア'],['一覧表示','CSV出力','CSV出力ボタン'],
     ['一覧表示','編集画面','設備コードクリック'],['一覧表示','削除→再表示','選択削除+confirmOK']],
    [('action path','/mst/eqp/list'),('action class','com.struts-lab.action.mst.EqpListAction'),
     ('form bean','EqpSearchForm'),('scope','session'),('forward eqp.list','/jsp/mst/eqpList.jsp')])

pg_section(lines08, 'PG-MST-EQP-002', '設備マスタ登録・編集', 'マスタ管理',
    '設備マスタの新規登録・編集を行う。5セクション大フォーム、親設備ポップアップ選択、添付ファイル処理。DispatchActionによるCRUD操作。',
    [['入力','設備名','String','eqpName','○',''],['入力','設備種別','String','eqpType','○','select'],
     ['入力','設備ステータス','String','eqpStatus','○','select'],['入力','電圧階級','String','eqpVoltage','○',''],
     ['入力','定格容量','Integer','eqpCapacity','',''],['入力','定格電流','Integer','eqpCurrent','',''],
     ['入力','親設備コード','String','eqpParentCode','','popup選択'],['入力','保全ランク','String','eqpRank','○',''],
     ['入力','点検周期','Integer','eqpInterval','','1-120'],['入力','添付ファイル','FormFile','eqpFile','','']],
    [['編集時、設備コードから既存データ取得（新規時スキップ）','EqpDao.findById()',''],
     ['Form→DTO変換→Validator実行','','エラー時INPUT forward'],
     ['親設備コードの存在＋状態チェック（廃止不可）','EqpDao.findById()',''],
     ['新規: DAO.insert() / 編集: DAO.update()','EqpDao','トランザクション'],
     ['添付ファイル→ファイルシステム保存','','/attachments/eqp/{eqpCode}/'],
     ['Forward: SUCCESS→eqp.list','','']],
    [['eqpName','空不可、最大100文字','設備名を入力してください'],
     ['eqpType','空不可','設備種別を選択してください'],
     ['eqpInterval','1～120の整数','点検周期は1～120ヶ月の範囲で入力してください'],
     ['eqpParentCode','廃止設備不可','廃止済み設備は親設備に指定できません'],
     ['eqpFile','サイズMax 10MB, 拡張子 pdf/jpg/png','ファイルは10MB以下のPDF/JPG/PNG形式にしてください']],
    [['初期表示','編集中','項目入力'],['編集中','バリデーションエラー→再表示','保存→NG'],
     ['編集中','一覧画面','保存→OK'],['編集中','親設備選択ポップアップ','選択ボタン']],
    [('action path','/mst/eqp/save'),('action class','com.struts-lab.action.mst.EqpSaveAction (extends DispatchAction)'),
     ('form bean','EqpForm (ValidatorForm)'),('scope','request'),
     ('forward eqp.list','/jsp/mst/eqpList.jsp'),('input','/jsp/mst/eqpEdit.jsp'),('validate','true')])

pg_section(lines08, 'PG-MST-CHK-001', '点検項目マスタ一覧', 'マスタ管理',
    '点検項目テンプレートの検索・一覧表示。テンプレートコピー、並び順変更（sort_orderスワップ）機能を持つ。',
    [['入力','設備種別','String','chkSearchType','',''],['入力','点検種別','String','chkSearchKind','',''],
     ['出力','テンプレートリスト','List<ChkTmplDto>','tmplList','','']],
    [['検索条件→DAO.search()','ChkItemDao.search()',''],
     ['並び順変更: 指定行と前/後行のsort_orderスワップ','ChkItemDao.swapOrder()','2行のsort_order値を入れ替え'],
     ['コピー: テンプレート名に「_コピー」付加→全項目複製','ChkItemDao.copy()','名称重複時「_コピー2」']],
    [['chkSearchType','空可',''],['chkSearchKind','空可','']],
    [['一覧表示','検索結果表示','検索'],['一覧表示','並び順変更→再表示','▲/▼'],
     ['一覧表示','コピー→再表示','コピー'],['一覧表示','編集画面','クリック']],
    [('action path','/mst/chkitem/list'),('action class','com.struts-lab.action.mst.CheckItemListAction'),
     ('form bean','CheckItemSearchForm'),('scope','session'),('forward chkitem.list','/jsp/mst/chkItemList.jsp')])

pg_section(lines08, 'PG-MST-CHK-002', '点検項目マスタ登録・編集', 'マスタ管理',
    '点検項目テンプレートの3階層ツリー編集。大分類→中分類→個別項目の動的行追加/削除（Submit→再描画）。Indexed + Nested Properties。',
    [['入力','テンプレート名','String','tmplName','○',''],['入力','設備種別','String','eqpType','○',''],
     ['入力','点検種別','String','checkKind','○',''],['入力','大分類[{n}].name','String[]','cat1Names','','Indexed'],
     ['入力','中分類[{n}][{m}].name','String[][]','cat2Names','','Nested'],
     ['入力','項目[{i}].*','ItemDto[]','items','','Indexed']],
    [['動的行操作の判定（addRow/delRow/save）','',''],
     ['行追加: Formの配列サイズ+1→新しいindexに空データ→INPUT forward','','既存行indexは維持'],
     ['行削除: 指定indexを配列から除去→残りをindex=0から詰め直し','','配列再構築'],
     ['保存: 3階層構造をトランザクション保存','ChkItemDao','大分類→中分類→項目の順']],
    [['tmplName','空不可、重複不可','テンプレート名は必須です / 同名が存在します'],
     ['cat1Names[{n}]','空不可','大分類名を入力してください'],
     ['items[{i}].name','空不可','項目名を入力してください']],
    [['表示','行追加→再表示','＋ボタン'],['表示','行削除→再表示','削除'],
     ['表示','保存→一覧','保存'],['表示','一括追加画面','一括項目追加']])
write_md('08_プログラム設計書_マスタ管理.md', lines08)

# ===== 09_プログラム設計書_点検計画・実施.md =====
lines09 = ['# 09 プログラム設計書 — 点検計画・実施', '']

pg_section(lines09, 'PG-INS-PLAN-001', '年間点検計画一覧', '点検計画',
    '年度×設備×月のマトリクス形式で点検計画と実績を表示。動的カラム（12ヶ月）テーブル生成。計画ロック/解除機能。',
    [['入力','年度','String','planYear','○',''],['入力','設備種別','String','eqpType','',''],
     ['入力','担当班','String','team','',''],
     ['出力','マトリクスデータ','Map<String,Map<String,PlanCellDto>>','monthlyMatrix','','行=設備,列=月']],
    [['年度から会計期間（4月～翌年3月）の開始月計算','',''],
     ['対象設備一覧取得→行生成','EqpDao.findByType()',''],
     ['各月の計画/実績カウント取得','PlanDao.countMonthly()','SQL GROUP BY集計'],
     ['JSPでマトリクス描画（実施率=実績/計画×100%）','',''],
     ['計画ロック: plan_locked=1に更新→ロック後登録不可','PlanDao.lockYear()','']],
    [['planYear','空不可',''],['eqpType','空可','']],
    [['表示','マトリクス表示','表示'],['表示','計画ロック','ロック'],['表示','計画解除','解除']])

pg_section(lines09, 'PG-INS-PLAN-002', '点検計画登録Wizard', '点検計画',
    '4ステップWizard方式の点検計画登録。ステップ間データはhiddenフィールドで持ち回り。一時保存→セッションから復元可能。',
    [['入力(全step)','wizardForm(全項目)','PlanWizardForm','planWizardForm','○','hidden持ち回り'],
     ['Step1出力','選択設備情報','EqpDto','selectedEqp','',''],
     ['Step2出力','テンプレート一覧','List<ChkTmplDto>','tmplList','',''],
     ['Step3出力','担当者一覧','List<EmpDto>','empList','','班に連動'],
     ['Step4出力','確認用全データ','PlanConfirmDto','confirmData','','']],
    [['stepパラメータで分岐（step1/step2/step3/confirm）','',''],
     ['Step1: 設備選択→バリデーション→step2へ（選択設備をhidden保持）','',''],
     ['Step2: テンプレート選択→step3へ','',''],
     ['Step3: 日程・担当者設定→バリデーション→confirm（班→担当者連動）','',''],
     ['Step4: 全入力表示→一時保存or確定（一時保存=セッション/確定=DB）','',''],
     ['途中離脱再開: セッションから復元（切れ時は最初から）','',''],
     ['「戻る」: 前stepに戻る（hidden値維持、修正→再進む可）','','']],
    [['Step1: selectedEqp','空不可','設備を選択してください'],
     ['Step2: selectedTmpl','空不可','テンプレートを選択してください'],
     ['Step3: planDate','未来日','点検日は未来日を指定してください'],
     ['Step3: team/person','空不可','']],
    [['Step1','Step2','進む'],['Step2','Step1','戻る'],['Step2','Step3','進む'],
     ['Step3','Step2','戻る'],['Step3','確認画面','進む'],['確認画面','Step3','戻る'],
     ['確認画面','一覧','確定'],['確認画面','セッション保存','一時保存']])

pg_section(lines09, 'PG-INS-EXEC-001', '点検実施入力', '点検実施',
    'ネストされたチェックリスト形式で点検結果を入力。判定値（○/×/△）に応じた条件付きバリデーション。写真添付（最大5枚/項目）。総合判定「異常あり」→異常報告へデータ引継ぎ。',
    [['入力','判定[{i}]','String[]','execJudge','○','radio: ○/×/△'],
     ['入力','実測値[{i}]','String[]','execValue','×時必須',''],['入力','所見[{i}]','String[]','execNote','×/△時必須',''],
     ['入力','写真[{i}][{j}]','FormFile[][]','execPhoto','','max 5枚/項目'],
     ['入力','総合判定','String','summaryJudge','○','radio'],['入力','総合所見','String','summaryNote','','']],
    [['点検項目マスタから3階層チェックリスト構築','ChkItemDao.findByTmpl()',''],
     ['各項目の判定値に応じたバリデーション','','Validator + Action内追加検証'],
     ['×→実測値+所見必須、△→所見必須、○→任意','',''],
     ['写真保存: 項目{i}/写真{j}→filesystem','','/attachments/ins/{planId}/{itemIdx}/'],
     ['総合判定「異常あり」→異常報告登録用データをセッションへ','','Forward: inc.create']],
    [['execJudge[{i}]','空不可','判定を選択してください'],
     ['execValue[{i}]','×時: 空不可、数値形式','×判定の場合は実測値を入力してください'],
     ['execNote[{i}]','×/△時: 空不可','×/△判定の場合は所見を入力してください'],
     ['summaryJudge','空不可',''],['execPhoto[{i}][{j}]','jpg/png, 1枚Max 5MB','']],
    [['表示','判定入力','各項目判定'],['判定入力','バリデーションエラー→再表示','保存→NG'],
     ['判定入力','保存→一覧','保存→OK'],['判定入力','異常報告登録画面','異常あり→異常報告へ']])

pg_section(lines09, 'PG-INS-EXEC-002', '点検実施詳細・修正', '点検実施',
    '実施済み点検結果の照会と修正申請。修正申請には修正理由が必須。承認フロー連携。',
    [['入力','修正理由','String','modifyReason','申請時○',''],['出力','実施結果全データ','ExecResultDto','execResult','','']],
    [['指定点検IDの全データ取得','ExecDao.findById()',''],
     ['修正申請: 理由必須→approval_status=申請中に更新','ExecDao.requestModify()',''],
     ['承認済みデータは修正不可（申請ボタン非表示）','','']],
    [['modifyReason','申請時: 空不可、最大500文字','修正理由を入力してください（500文字以内）']],
    [['表示','修正申請→申請中表示','修正申請→理由入力OK'],['表示','申請中→操作不可','承認待ち'],
     ['申請中','承認→表示（修正反映）','承認者操作'],['申請中','差戻→表示（修正申請取消）','差戻し']])

pg_section(lines09, 'PG-INS-APPR-001', '点検実施承認一覧', '点検実施',
    '点検結果修正申請の承認/差戻し一覧。一括操作。差戻し時理由必須。操作後通知メッセージ表示。',
    [['入力','期間(from/to)','String','apprDateFrom/To','',''],['入力','担当班','String','apprTeam','',''],
     ['入力','ステータス','String','apprStatus','',''],['出力','承認待ちリスト','List<ApprovalDto>','approvalList','','']],
    [['検索条件→承認待ち一覧取得','ExecDao.findPendingApprovals()',''],
     ['一括承認: 選択行のapproval_status=承認済に更新','ExecDao.bulkApprove()',''],
     ['一括差戻し: 理由必須→reject_reason保存→status=差戻','ExecDao.bulkReject()',''],
     ['操作結果メッセージをリクエスト属性に設定','','「N件承認しました」']],
    [['apprDateFrom/To','空可、YYYYMMDD',''],['apprTeam','空可',''],
     ['rejectReason','一括差戻し時: 空不可','差戻し理由を入力してください']],
    [['一覧','一括承認→通知','承認→confirmOK'],['一覧','一括差戻→理由→通知','差戻→confirm→理由必須'],
     ['一覧','検索結果表示','検索'],['一覧','全件表示','クリア']])
write_md('09_プログラム設計書_点検計画・実施.md', lines09)

# ===== 10_プログラム設計書_異常報告・対応指示.md =====
lines10 = ['# 10 プログラム設計書 — 異常報告・対応指示', '']

pg_section(lines10, 'PG-INC-LIST-001', '異常報告一覧', '異常報告',
    '異常報告の複合検索（7条件AND）・一覧表示。検索条件保存/呼出、一括ステータス更新、CSV/PDF出力。',
    [['入力','発生日(from/to)','String','incDateFrom/To','',''],['入力','設備種別','String','incEqpType','',''],
     ['入力','異常種別','String','incType','',''],['入力','ステータス','String','incStatus','',''],
     ['入力','重大度','String','incSeverity','',''],['入力','担当班','String','incTeam','',''],
     ['入力','キーワード','String','incKeyword','','部分一致'],['出力','検索結果','List<IncidentDto>','incidentList','','']],
    [['検索条件DTO構築→MyBatis動的SQL','','<where>/<if>'],
     ['DAO.search() 実行（JOIN equipment）','IncidentDao',''],
     ['検索条件保存: セッションにSearchCondition保存','','saveCondition/loadCondition'],
     ['一括ステータス更新: 選択行一括更新','IncidentDao.bulkUpdateStatus()',''],
     ['CSV出力: 結果→CSVストリーム→HttpServletResponse','','Content-Type: text/csv']],
    [['incDateFrom/To','空可、YYYYMMDD、from≦to',''],['incEqpType','空可',''],
     ['incType','空可',''],['incSeverity','空可',''],['incKeyword','最大100文字','']],
    [['表示','検索結果表示','検索'],['表示','条件保存','検索条件保存'],
     ['表示','一括ステータス更新→再表示','実行→confirmOK'],['表示','CSV/PDFダウンロード','出力']])

pg_section(lines10, 'PG-INC-CREATE-001', '異常報告登録', '異常報告',
    '異常報告の新規登録。点検実施からのデータ引継ぎ、類似事例検索（同画面内表示）、ファイル添付。',
    [['入力','発生日時','String','incDateTime','○','点検から引継ぎ'],['入力','発見者','String','finder','○',''],
     ['入力','異常種別','String','incType','○',''],['入力','重大度','String','severity','○',''],
     ['入力','異常部位','String','incPart','○',''],['入力','異常詳細','String','incDetail','○','textarea'],
     ['入力','暫定処置','String','tmpAction','',''],['入力','添付ファイル','FormFile[]','incFiles','','']],
    [['引継ぎデータ表示（点検実施からhidden/セッション経由）','','incident.fromInspection判定'],
     ['類似事例検索: 異常種別＋部位→全文検索→同画面下部に結果表示','IncidentDao.searchSimilar()',''],
     ['登録: Form→DTO→DAO.insert()（報告番号自動採番INC-YYYYMMDD-NNN）','IncidentDao.insert()',''],
     ['添付ファイル保存','','/attachments/inc/{incidentNo}/']],
    [['incDateTime','空不可','発生日時は必須です'],['incType','空不可',''],['severity','空不可',''],
     ['incPart','空不可',''],['incDetail','空不可、最大2000文字',''],
     ['incFiles[*]','jpg/png/pdf, 1枚Max 10MB','']],
    [['表示','類似事例検索→同画面下部表示','類似事例検索'],['表示','登録→一覧','登録'],
     ['表示','一時保存→表示継続','一時保存'],['表示','バリデーションエラー→再表示','登録→NG']])

pg_section(lines10, 'PG-INC-DETAIL-001', '異常報告詳細', '異常報告',
    '異常報告の詳細表示と多段階ステータス遷移。各遷移時に条件付き必須項目が変化。タイムライン表示。',
    [['入力','推定原因','String','cause','調査中→対応中時○','条件付き'],
     ['入力','対応内容','String','counterDetail','対応中→完了時○','条件付き'],
     ['出力','異常報告全データ','IncidentDto','incident','',''],
     ['出力','タイムライン','List<TimelineDto>','timeline','','時系列ソート']],
    [['異常報告ID→全データ取得','IncidentDao.findById()',''],
     ['タイムライン取得（経過記録を時系列ソート）','IncidentDao.getTimeline()',''],
     ['ステータス遷移: 現在statusに応じた遷移先決定','','未了→調査中→対応中→完了→クローズ/CAPA'],
     ['調査→対応: 推定原因must（Validator+Action内追加チェック）','',''],
     ['対応→完了: 対応内容must','',''],
     ['完了→CAPA: 是正処置報告書画面へForward','','Forward: counter.capa']],
    [['cause','調査中→対応中遷移時: 空不可','推定原因を入力してください'],
     ['counterDetail','対応中→完了遷移時: 空不可','対応内容を入力してください']],
    [['未了','調査中','調査開始'],['調査中','対応中','対応開始（推定原因必須）'],
     ['対応中','完了','完了（対応内容必須）'],['完了','クローズ','クローズ'],['完了','是正処置報告書','再発防止策登録']])

pg_section(lines10, 'PG-CTR-CREATE-001', '対応指示登録', '対応指示',
    '対応指示の新規登録。ヘッダ+明細の親子構造、Indexed Propertiesによる動的行管理、担当者ポップアップ選択。全明細完了チェック。',
    [['入力','指示日','String','ctrDate','○',''],['入力','指示者','String','issuer','○',''],
     ['入力','全体期限','String','overallDeadline','',''],['入力','優先度','String','overallPriority','○',''],
     ['入力','明細[{n}].作業内容','String[]','details[{n}].workContent','○','Indexed'],
     ['入力','明細[{n}].担当者','String[]','details[{n}].person','○','popup選択'],
     ['入力','明細[{n}].期限','String[]','details[{n}].deadline','',''],
     ['入力','明細[{n}].優先度','String[]','details[{n}].priority','','']],
    [['動的行操作の判定（addRow/delRow/save）','',''],
     ['行追加: 明細配列サイズ+1→INPUT forward（最下行に空行追加）','','既存行index維持'],
     ['行削除: 指定index除去→残りをindex=0から詰め直し','','配列再構築'],
     ['保存: ヘッダ+全明細をトランザクション保存（指示番号CTR-YYYYMMDD-NNN）','CounterDao',''],
     ['全明細完了チェック: 全details[*].status=完了でヘッダ自動完了','','']],
    [['details[{n}].workContent','空不可','作業内容を入力してください'],
     ['details[{n}].person','空不可','担当者を選択してください'],
     ['details[{n}].deadline','YYYYMMDD、過去日不可','期限は未来日を指定してください'],
     ['詳細行数','1行以上、最大50行','明細は1行以上必要です / 最大50行まで']],
    [['表示','行追加→再表示','＋行追加'],['表示','行削除→再表示','削除'],
     ['表示','担当者ポップアップ→値反映','選択'],['表示','保存→一覧','登録']])

pg_section(lines10, 'PG-CTR-DETAIL-001', '対応指示詳細・完了報告', '対応指示',
    '対応指示の詳細表示と各明細の個別完了報告。完了報告時に実績入力行展開。全明細完了でヘッダ自動完了。使用部品選択→在庫アラート。',
    [['入力','明細[{n}].実績時間','String[]','details[{n}].actualHours','完了時○',''],
     ['入力','明細[{n}].使用部品','String[]','details[{n}].usedPart','','select'],
     ['入力','明細[{n}].使用量','Integer[]','details[{n}].usedQty','',''],
     ['入力','明細[{n}].所見','String[]','details[{n}].note','完了時○','']],
    [['指示ID→全データ取得','CounterDao.findById()',''],
     ['完了報告展開: 指定明細の完了報告フォーム表示','','INPUT forward→JSPで行展開'],
     ['明細完了: details[{n}].status=完了に更新','','実績データ保存→在庫引当'],
     ['全明細完了→header.status=完了に自動更新','','最終明細完了時トリガー'],
     ['使用部品選択→在庫チェック→発注点下回り警告','PartsDao.checkStock()','警告表示のみ']],
    [['actualHours','0より大きい数値','実績作業時間を入力してください'],
     ['usedQty','0より大きい整数',''],['note','空不可','所見を入力してください']],
    [['表示','完了報告展開','完了報告'],['展開','入力→保存→展開閉','保存'],
     ['個別完了','全明細完了→ヘッダ自動完了','最終明細完了時']])

pg_section(lines10, 'PG-CTR-CAPA-001', '是正処置報告書', '是正処置',
    '異常報告から派生する是正処置報告書の登録。なぜなぜ分析5段階、再発防止策、効果確認。',
    [['入力','なぜ①～⑤','String[]','why[1]～why[5]','○','textarea×5'],
     ['入力','再発防止策','String','countermeasure','○',''],['入力','効果確認方法','String','verifyMethod','○',''],
     ['入力','効果確認期限','String','verifyDate','○','YYYYMMDD, 未来日']],
    [['異常報告データ→初期表示','IncidentDao.findById()','引継ぎ'],
     ['なぜなぜ分析5段階入力→保存','','各段階の関係性はアプリ検証なし'],
     ['再発防止策・効果確認登録→承認申請','CapaDao.insert()','']],
    [['why[1]～[5]','空不可','なぜ①～⑤を入力してください'],
     ['countermeasure','空不可、最大2000文字',''],['verifyMethod','空不可、最大1000文字',''],
     ['verifyDate','YYYYMMDD、未来日','効果確認期限には未来日を指定してください']],
    [['表示','入力→登録→異常報告詳細','登録'],['表示','バリデーションエラー→再表示','登録→NG']])
write_md('10_プログラム設計書_異常報告・対応指示.md', lines10)

# ===== 11_プログラム設計書_組織・カレンダー・保守部品.md =====
lines11 = ['# 11 プログラム設計書 — 組織・カレンダー・保守部品', '']

pg_section(lines11, 'PG-ORG-DEPT', '部署マスタ管理', '組織管理',
    '部署マスタの一覧（4階層ツリー表示）、登録・編集、統廃合履歴管理。上位部署のツリーポップアップ選択。過去日異動警告。',
    [['入力','部署名','String','deptName','○',''],['入力','上位部署','String','parentDept','','popup'],
     ['入力','有効開始日','String','startDate','○',''],['入力','有効終了日','String','endDate','',''],
     ['入力','部署種別','String','deptType','○','']],
    [['ツリー表示: 4階層の親子関係を再帰的に構築→階層=インデント幅','DeptDao.findAll()',''],
     ['登録: コード自動採番→INSERT','DeptDao.insert()',''],
     ['過去日異動チェック: 有効開始日＜本日で警告（保存は可）','','']],
    [['deptName','空不可、重複不可',''],['endDate','startDate＜endDate','終了日は開始日より後']],
    [['一覧','編集','部署名クリック'],['一覧','子部署追加','+子部署'],['編集','ツリーポップアップ','上位部署選択']])

pg_section(lines11, 'PG-ORG-EMP', '担当者マスタ管理', '組織管理',
    '担当者マスタの一覧（複合検索）、登録・編集（5セクション大フォーム）。資格ごとの有効期限管理。ログインID重複チェック。パスワードSHA-256ハッシュ化。',
    [['入力','氏名','String','empName','○',''],['入力','フリガナ','String','empKana','','カタカナ'],
     ['入力','部署','String','empDept','○',''],['入力','保有資格[{n}]','String[]','empQual','','checkbox'],
     ['入力','点検員ランク','String','insRank','',''],['入力','認定有効期限','String','insExpire','',''],
     ['入力','ログインID','String','loginId','○','重複不可'],['入力','パスワード','String','password','○','']],
    [['社員番号自動採番（EMP-NNNN）→5セクションForm構築','',''],
     ['資格ごとの有効期限バリデーション（資格チェックあり→認定日+有効期限必須）','','条件付き'],
     ['ログインID重複チェック','EmpDao.countByLoginId()',''],
     ['パスワード: 確認一致→SHA-256ハッシュ化→保存','','']],
    [['empName','空不可',''],['empKana','カタカナのみ','フリガナはカタカナで入力'],
     ['loginId','重複不可','このログインIDは既に使用されています'],
     ['password','8文字以上','パスワードは8文字以上'],['insExpire','未来日','有効期限が切れています']],
    [['表示','5セクション編集',''],['編集','保存→一覧','保存'],['編集','バリデーション→再表示','保存→NG']])

pg_section(lines11, 'PG-CAL', '休日カレンダー管理', 'カレンダー管理',
    '休日カレンダーの月別table表示（色分け）、範囲指定一括登録、重複チェック、振替出勤日設定。',
    [['入力','年度','String','calYear','○',''],['入力','日付範囲(from/to)','String','calDateFrom/To','○',''],
     ['入力','休日種別','String','calType','○',''],['入力','振替休日/出勤日','String','transferFrom/To','','']],
    [['月別カレンダー描画: 日付計算→曜日→週割り→7列×5-6行table','',''],
     ['休日セル色分け: 法定=赤class、会社指定=青class、停止=黄class','','CSSクラス'],
     ['範囲一括登録: from～to全日付INSERT（既存重複→エラー）','CalDao.bulkInsert()',''],
     ['振替設定: 休日→出勤日として登録','','']],
    [['calDateFrom/To','from≦to',''],['calDateFrom/To','既存休日重複不可','指定範囲に既存休日が含まれています'],
     ['transferFrom','休日であること',''],['transferTo','休日でないこと','']],
    [['表示','月別カレンダー','年度選択→表示'],['一覧','登録/編集','日付クリック'],['登録','一括登録→一覧','一括登録']])

pg_section(lines11, 'PG-PRT', '保守部品管理', '保守部品管理',
    '保守部品の一覧・登録・編集、使用実績管理。在庫アラート（発注点下回り）。適用設備のチェックボックスツリー選択。',
    [['入力','部品名','String','prtName','○',''],['入力','部品種別','String','prtType','',''],
     ['入力','適用設備[{n}]','String[]','prtEqp','','checkboxツリー'],
     ['入力','発注点','Integer','orderPoint','',''],['入力','安全在庫数','Integer','safetyStock','',''],
     ['入力','単価','Integer','price','','']],
    [['一覧: 在庫数＜発注点→僅少バッジ（黄）、=0→切れバッジ（赤）','','CSSクラス'],
     ['登録: 適用設備のツリー描画（設備種別→設備名の2階層checkbox）→保存','',''],
     ['使用実績: 点検/修繕→部品使用→在庫自動減算','PartsDao.updateStock()',''],
     ['在庫警告: 使用後在庫≦発注点→アラート表示','','警告のみ、登録は可']],
    [['prtName','空不可',''],['orderPoint','0以上',''],
     ['safetyStock','orderPoint > safetyStock','発注点は安全在庫数より大きく']],
    [['一覧','在庫アラート表示',''],['編集','適用設備ツリー選択',''],
     ['使用実績','在庫自動減算→警告判定',''],['登録/編集','保存→一覧','保存']])
write_md('11_プログラム設計書_組織・カレンダー・保守部品.md', lines11)

print("08-11 MD done!")
