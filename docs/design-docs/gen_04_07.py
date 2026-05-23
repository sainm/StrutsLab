#!/usr/bin/env python3
"""生成 04_画面設計書_異常報告.xlsx  05_画面設計書_対応指示・是正.xlsx  06_画面設計書_組織・カレンダー・保守部品.xlsx  07_画面設計書_レポート.xlsx"""
import sys; sys.path.insert(0, '.')
from _xlsx_gen import *

def sc(id, name, pg, sub, layout, els, tbl=None, smap=None, rpg=None, tn=None):
    d = []
    d.append(hdr_row(['画面設計書', '', '', '', '', '', '', '']))
    d.append(hdr_row(['文書管理情報', '', '', '', '', '', '', '']))
    for r in [['システムID', 'STRUTSLAB-001', '', '', 'サブシステム名', sub, '', ''],
              ['画面ID', id, '', '', '画面名', name, '', ''],
              ['プログラムID', pg, '', '', '文書番号', f'DOC-SCR-{id}', '', ''],
              ['版数', '1.0', '', '', '作成日', '2026/05/23', '', ''],
              ['作成者', 'システム開発部', '', '', '区分', '新規', '', ''],
              ['アクセス権限', '全ユーザー', '', '', '', '', '', '']]:
        d.append(r)
    d.append(empty_row()); d.append(hdr_row(['画面レイアウト', '', '', '', '', '', '', '']))
    d.append([layout, '', '', '', '', '', '', ''])
    d.append(empty_row()); d.append(hdr_row(['画面要素一覧', '', '', '', '', '', '', '']))
    d.append(hdr_row(['要素ID', '種別', '表示名', '入力値／選択肢', '説明', 'テスト難点タグ', '備考', '']))
    for e in els: d.append(list(e) + [''] * (8 - len(e)))
    if tbl:
        d.append(empty_row()); d.append(hdr_row(['テーブル列定義', '', '', '', '', '', '', '']))
        d.append(hdr_row(['列名', '型', 'ソート', '説明', '', '', '', '']))
        for tc in tbl: d.append(list(tc) + [''] * (8 - len(tc)))
    if smap:
        d.append(empty_row()); d.append(hdr_row(['Struts1マッピング', '', '', '', '', '', '', '']))
        d.append(hdr_row(['項目', '値', '', '', '', '', '', '']))
        for k,v in smap.items(): d.append([k, v, '', '', '', '', '', ''])
    if rpg:
        d.append(empty_row()); d.append(hdr_row(['関連プログラム', '', '', '', '', '', '', '']))
        d.append([', '.join(rpg), '', '', '', '', '', '', ''])
    if tn:
        d.append(empty_row()); d.append(hdr_row(['テスト難点詳細', '', '', '', '', '', '', '']))
        for t in tn: d.append([t, '', '', '', '', '', '', ''])
    return d

# ===== 04_画面設計書_異常報告.xlsx =====
# 異常報告一覧
s11 = sc('SCR-INC-LIST-001', '異常報告一覧', 'PG-INC-LIST-001', '異常報告',
    '【上部】複合検索：発生日（from/to, YYYYMMDD）、設備種別（select）、異常種別（select）、ステータス（select: 全/未了/調査中/対応中/完了/再発防止）、重大度（select: 全/軽微/中/重大/緊急）、担当班（select）、キーワード（text, 部分一致）。検索／クリアボタン。「検索条件保存」「検索条件呼出」ボタン。'
    '【中部】一覧：報告番号、発生日時、設備名、異常種別、重大度（色分け）、ステータス、担当班。ページング。各行にチェックボックス。'
    '【下部】「一括ステータス更新（select→実行）」「CSV出力」「PDF出力」ボタン。',
    [['inc-s-date-from','text','発生日(from)','YYYYMMDD','','',''],
     ['inc-s-date-to','text','発生日(to)','YYYYMMDD','','',''],
     ['inc-s-type','select','設備種別','','','',''],
     ['inc-s-inc-type','select','異常種別','絶縁不良/過熱/振動異常/油漏れ/ガス発生/コロナ/その他','','',''],
     ['inc-s-status','select','ステータス','全/未了/調査中/対応中/完了/再発防止','','',''],
     ['inc-s-severity','select','重大度','全/軽微/中/重大/緊急','','',''],
     ['inc-s-team','select','担当班','','','',''],
     ['inc-s-keyword','text','キーワード','テキスト','部分一致','',''],
     ['inc-btn-search','submit','検索','','','',''],
     ['inc-btn-clear','submit','クリア','','','',''],
     ['inc-btn-save-cond','submit','検索条件保存','','検索条件をセッションに保存','保存→呼出の再現性',''],
     ['inc-btn-load-cond','submit','検索条件呼出','','保存条件を復元','条件復元後の検索結果一致',''],
     ['inc-check-{n}','checkbox','選択','','','',''],
     ['inc-severity-badge-{n}','span','重大度バッジ','軽微/中/重大/緊急','色分け','',''],
     ['inc-status-select','select','一括ステータス更新','調査中/対応中/完了','','一括更新+confirm',''],
     ['inc-btn-bulk-upd','submit','実行','','','',''],
     ['inc-btn-csv','submit','CSV出力','','','',''],
     ['inc-btn-pdf','submit','PDF出力','','','新規ウィンドウ','']],
    [['報告番号','VARCHAR(20)','○','INC-YYYYMMDD-NNN'],
     ['発生日時','TIMESTAMP','○',''],
     ['設備名','VARCHAR(100)','○',''],
     ['異常種別','VARCHAR(10)','○',''],
     ['重大度','VARCHAR(5)','○','軽微/中/重大/緊急'],
     ['ステータス','VARCHAR(10)','○',''],
     ['担当班','VARCHAR(20)','','']],
    {'Action Path':'/inc/list','Action Class':'com.struts-lab.action.inc.IncidentListAction',
     'Form Bean':'com.struts-lab.form.inc.IncidentSearchForm','Forward(SUCCESS)':'inc.list'},
    ['PG-INC-LIST-001'], ['7条件複合検索の全組み合わせ検証困難', '検索条件保存→呼出の再現性', 'PDF出力の新規ウィンドウハンドル'])

# 異常報告登録
s12 = sc('SCR-INC-CREATE-001', '異常報告登録', 'PG-INC-CREATE-001', '異常報告',
    '【ブロック1】発生情報：発生日時（text, 点検実施から引継ぎ）、発見者（text, 引継ぎ）、設備情報（表示, 引継ぎ）、天候（select）、気温（text）'
    '【ブロック2】異常内容：異常種別（select）、重大度（select）、異常部位（text）、異常詳細（textarea）、添付ファイル（file, 最大3枚）'
    '【ブロック3】暫定処置：暫定処置内容（textarea）、処置担当者（select）、処置完了日（text）'
    '【中部】「類似事例検索」ボタン→同画面下部に検索結果テーブル表示（別タブ/別ウィンドウではない）'
    '【下部】「登録」「一時保存」ボタン。',
    [['inc-create-datetime','text','発生日時','YYYYMMDD HH:MM','点検実施から引継ぎ','',''],
     ['inc-create-finder','text','発見者','テキスト','引継ぎ','',''],
     ['inc-create-eqp-info','table','設備情報','表示のみ','引継ぎ','',''],
     ['inc-create-weather','select','天候','晴/曇/雨/雪/雷','','',''],
     ['inc-create-temp','text','気温','数値（℃）','','',''],
     ['inc-create-inc-type','select','異常種別','絶縁不良/過熱/振動異常/油漏れ/ガス発生/コロナ/その他','','必須',''],
     ['inc-create-severity','select','重大度','軽微/中/重大/緊急','','必須',''],
     ['inc-create-part','text','異常部位','テキスト','','必須',''],
     ['inc-create-detail','textarea','異常詳細','テキスト','','必須',''],
     ['inc-create-file-{n}','file','添付ファイル{n}','','異常箇所写真等','',''],
     ['inc-create-tmp-action','textarea','暫定処置内容','','','',''],
     ['inc-create-tmp-person','select','処置担当者','','','',''],
     ['inc-create-tmp-date','text','処置完了日','YYYYMMDD','','',''],
     ['inc-btn-similar','submit','類似事例検索','','同画面下部に結果表示','DOM追加要素の検証','stale element注意'],
     ['inc-similar-table','table','類似事例検索結果','','動的表示エリア','検索結果0件/1件/多数',''],
     ['inc-btn-register','submit','登録','','','',''],
     ['inc-btn-temp-save','submit','一時保存','','','','']],
    None,
    {'Action Path':'/inc/create','Action Class':'com.struts-lab.action.inc.IncidentCreateAction',
     'Form Bean':'com.struts-lab.form.inc.IncidentForm(ValidatorForm)','Forward(SUCCESS)':'inc.list',
     'Forward(INPUT)':'inc.create','Forward(SIMILAR)':'inc.create','Validator':'inc-form-validation'},
    ['PG-INC-CREATE-001'], ['点検実施→異常報告のデータ引継ぎ（hiddenまたはセッション）','類似事例検索のDOM動的追加（同画面内に結果表示）','添付ファイル＋バリデーションの同時処理'])

# 異常報告詳細
s13 = sc('SCR-INC-DETAIL-001', '異常報告詳細', 'PG-INC-DETAIL-001', '異常報告',
    '【上部】3ブロック表示（発生情報／異常内容／暫定処置）。全フィールド表示。添付ファイルダウンロードリンク。'
    '【中部】経過記録タイムライン（table）：日時、処理者、内容、ステータス（登録→調査中→対応中→完了→再発防止）'
    '【下部】ステータス遷移ボタン。現在のステータスに応じて押せるボタンが変わる：'
    '未了→「調査開始」（調査中へ）。調査中→「対応開始」（対応中へ）、このとき「推定原因（textarea）」必須。'
    '対応中→「完了」（完了へ）、このとき「対応内容（textarea）」必須。'
    '完了→「再発防止策登録」（是正処置報告書へ遷移）or「クローズ」。',
    [['inc-dtl-block1','table','発生情報','表示のみ','','',''],
     ['inc-dtl-block2','table','異常内容','表示のみ','','',''],
     ['inc-dtl-block3','table','暫定処置','表示のみ','','',''],
     ['inc-dtl-timeline','table','経過記録タイムライン','登録→調査中→対応中→完了→再発防止','','',''],
     ['inc-dtl-btn-investigate','submit','調査開始','未了時のみ表示','','条件付きボタン表示',''],
     ['inc-dtl-btn-counter','submit','対応開始','調査中時のみ表示','推定原因必須','条件付き必須項目',''],
     ['inc-dtl-cause','textarea','推定原因','調査→対応時に必須','','条件付き必須',''],
     ['inc-dtl-btn-complete','submit','完了','対応中時のみ表示','対応内容必須','条件付き必須項目',''],
     ['inc-dtl-counter-detail','textarea','対応内容','対応→完了時に必須','','条件付き必須',''],
     ['inc-dtl-btn-capa','submit','再発防止策登録','完了時のみ表示','是正処置報告書へ遷移','',''],
     ['inc-dtl-btn-close','submit','クローズ','完了時のみ表示','','','']],
    None,
    {'Action Path':'/inc/detail','Action Class':'com.struts-lab.action.inc.IncidentDetailAction',
     'Form Bean':'com.struts-lab.form.inc.IncidentForm','Forward(SUCCESS)':'inc.detail',
     'Forward(INVESTIGATE)':'inc.detail','Forward(COUNTER)':'inc.detail','Forward(COMPLETE)':'inc.detail',
     'Forward(CAPA)':'counter.capa'},
    ['PG-INC-DETAIL-001','PG-CTR-CAPA-001'],
    ['多段階ステータス遷移（未了→調査中→対応中→完了→再発防止/クローズ）の全パス', '各遷移時の条件付き必須項目（推定原因/対応内容）', 'ボタン表示/非表示の状態別切替', 'タイムラインの時系列ソート検証'])

w04 = XlsxWriter()
w04.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w04.add_sheet('Sheet2', [])
w04.add_sheet('異常報告一覧', s11, col_widths=[20,12,18,30,30,22,22,20])
w04.add_sheet('異常報告登録', s12, col_widths=[20,12,18,30,30,22,22,20])
w04.add_sheet('異常報告詳細', s13, col_widths=[20,12,18,30,30,22,22,20])
w04.save('04_画面設計書_異常報告.xlsx')

# ===== 05_画面設計書_対応指示・是正.xlsx =====
# 対応指示登録
s14 = sc('SCR-CTR-CREATE-001', '対応指示登録', 'PG-CTR-CREATE-001', '対応指示',
    '【ヘッダ部】指示番号（自動採番、表示）、関連異常報告番号（link, 異常報告詳細から引継ぎ）、指示日（text, デフォルト本日）、指示者（text）、全体期限（text, YYYYMMDD）、優先度（select: 高/中/低）'
    '【明細部】テーブル：行番号、作業内容（text）、担当者（text + 「選択」ボタン→ポップアップ→選択→opener値反映）、期限（text, YYYYMMDD）、優先度（select）。「行追加」ボタン→Submit→再描画。「行削除」ボタン。Indexed Properties（name="details[0].workContent"形式）。'
    '【下部】「登録」「一時保存」ボタン。',
    [['ctr-no','text','指示番号','自動採番（表示のみ）','','',''],
     ['ctr-inc-no','link','関連異常報告番号','クリック→異常報告詳細','','',''],
     ['ctr-date','text','指示日','YYYYMMDD','デフォルト本日','',''],
     ['ctr-issuer','text','指示者','テキスト','','必須',''],
     ['ctr-overall-deadline','text','全体期限','YYYYMMDD','','',''],
     ['ctr-overall-priority','select','優先度','高/中/低','','必須',''],
     ['ctr-detail-seq-{n}','text','行番号','表示のみ','1から連番','index再振り検証',''],
     ['ctr-detail-work-{n}','text','作業内容[{n}]','テキスト','','必須','Indexed Properties'],
     ['ctr-detail-person-{n}','text','担当者[{n}]','テキスト','読取専用','ポップアップ選択結果','Stale Element'],
     ['ctr-detail-person-btn-{n}','button','選択','window.open→担当者選択→opener反映','','ウィンドウハンドル切替',''],
     ['ctr-detail-deadline-{n}','text','期限[{n}]','YYYYMMDD','','過去日不可',''],
     ['ctr-detail-priority-{n}','select','優先度[{n}]','高/中/低','','',''],
     ['ctr-detail-del-{n}','submit','削除','','行削除→index再振り','行削除後index変動',''],
     ['ctr-btn-add-row','submit','＋行追加','','Submit→再描画','動的行追加(Submit方式)','index変動→既存要素stale'],
     ['ctr-btn-save','submit','登録','','','',''],
     ['ctr-btn-temp','submit','一時保存','','','','']],
    None,
    {'Action Path':'/counter/create','Action Class':'com.struts-lab.action.counter.CounterCreateAction',
     'Form Bean':'com.struts-lab.form.counter.CounterForm(ValidatorForm)',
     'Forward(SUCCESS)':'counter.list','Forward(INPUT)':'counter.create',
     'Forward(ADD_ROW)':'counter.create','Forward(DEL_ROW)':'counter.create'},
    ['PG-CTR-CREATE-001'],
    ['動的行追加（Submit→再描画）：追加後、既存行要素がstaleになる','Indexed Properties（details[0].workContent）：name属性の深いネスト',
     '行削除後のindex再振り：削除→追加→削除の繰り返しでindex連番が正しいか',
     'ポップアップ→親画面値反映：ウィンドウハンドル切替 + opener.document操作',
     '複数明細＋バリデーションエラー→全明細の入力値維持検証'])

# 対応指示一覧
s15 = sc('SCR-CTR-LIST-001', '対応指示一覧', 'PG-CTR-LIST-001', '対応指示',
    '【上部】複合検索：指示日範囲（from/to）、担当者（select, 明細の担当者含む）、ステータス（select: 全/未了/一部完了/完了）、優先度（select）。検索／クリア。'
    '【中部】一覧：指示番号、指示日、関連異常報告番号、優先度、明細数、完了数/全明細数、全体ステータス。ページング。各行にチェックボックス。'
    '【下部】「一括ステータス更新」「印刷用表示（window.open）」「CSV出力」ボタン。',
    [['ctr-s-date-from','text','指示日(from)','YYYYMMDD','','',''],
     ['ctr-s-date-to','text','指示日(to)','YYYYMMDD','','',''],
     ['ctr-s-person','select','担当者','','明細担当者含む','',''],
     ['ctr-s-status','select','ステータス','全/未了/一部完了/完了','','',''],
     ['ctr-s-priority','select','優先度','全/高/中/低','','',''],
     ['ctr-check-{n}','checkbox','選択','','','',''],
     ['ctr-complete-ratio-{n}','text','完了数/全明細数','例: 3/5','','',''],
     ['ctr-btn-bulk-upd','submit','一括ステータス更新','','','confirm',''],
     ['ctr-btn-print','button','印刷用表示','window.open(印刷用URL)','別ウィンドウ','新規ウィンドウハンドル',''],
     ['ctr-btn-csv','submit','CSV出力','','','','']],
    [['指示番号','VARCHAR(20)','○',''],['指示日','DATE','○',''],['関連異常報告番号','VARCHAR(20)','',''],
     ['優先度','VARCHAR(5)','○',''],['明細数','INT','',''],['完了/全明細','VARCHAR(10)','',''],
     ['ステータス','VARCHAR(10)','○','']],
    {'Action Path':'/counter/list','Action Class':'com.struts-lab.action.counter.CounterListAction',
     'Form Bean':'com.struts-lab.form.counter.CounterSearchForm','Forward(SUCCESS)':'counter.list',
     'Forward(PRINT)':'(新規ウィンドウ)'},
    ['PG-CTR-LIST-001'], ['印刷用別ウィンドウのハンドル切替と内容検証','完了数/全明細数の表示更新検証'])

# 対応指示詳細・完了報告
s16 = sc('SCR-CTR-DETAIL-001', '対応指示詳細・完了報告', 'PG-CTR-DETAIL-001', '対応指示',
    '【上部】指示ヘッダ情報（表示）。【中部】明細一覧テーブル：各行に行番号、作業内容、担当者、期限、優先度、ステータス（未了/完了）、「完了報告」ボタン。'
    '完了報告押下時→行の下に「実績作業時間（text, 時間）」「使用部品（select, 部品マスタから）」「使用量（text）」「所見（textarea）」入力行が展開表示。'
    '全明細が完了になるとヘッダが自動で「完了」になる。'
    '【下部】「保存」「戻る」ボタン。',
    [['ctr-dtl-header','table','指示ヘッダ情報','表示のみ','','',''],
     ['ctr-dtl-detail-seq-{n}','text','行番号','表示','','',''],
     ['ctr-dtl-detail-work-{n}','text','作業内容','表示','','',''],
     ['ctr-dtl-detail-person-{n}','text','担当者','表示','','',''],
     ['ctr-dtl-detail-status-{n}','text','ステータス','未了/完了','','',''],
     ['ctr-dtl-btn-complete-{n}','submit','完了報告','','押下→入力行展開','DOM追加要素','Stale Element'],
     ['ctr-dtl-actual-hours-{n}','text','実績作業時間','数値（時間）','完了報告時必須','条件付き必須',''],
     ['ctr-dtl-used-part-{n}','select','使用部品','部品マスタから選択','','',''],
     ['ctr-dtl-used-qty-{n}','text','使用量','数値','','在庫超えアラート',''],
     ['ctr-dtl-note-{n}','textarea','所見','テキスト','完了報告時必須','条件付き必須',''],
     ['ctr-dtl-header-status','text','全体ステータス','自動計算','全明細完了→自動「完了」','自動完了検出',''],
     ['ctr-dtl-btn-save','submit','保存','','','',''],
     ['ctr-dtl-btn-back','submit','戻る','','一覧へ','','']],
    None,
    {'Action Path':'/counter/detail','Action Class':'com.struts-lab.action.counter.CounterDetailAction',
     'Form Bean':'com.struts-lab.form.counter.CounterDetailForm','Forward(SUCCESS)':'counter.list',
     'Forward(INPUT)':'counter.detail','Forward(COMPLETE)':'counter.detail'},
    ['PG-CTR-DETAIL-001'], ['明細個別の完了報告→DOM展開の検証','全明細完了→ヘッダ自動完了の検出トリガー','使用部品選択→在庫超えアラートの条件付き表示'])

# 是正処置報告書
s17 = sc('SCR-CTR-CAPA-001', '是正処置報告書', 'PG-CTR-CAPA-001', '是正処置',
    '【上部】異常報告からの引継ぎ情報（表示）。【中部】なぜなぜ分析：5段階の原因分析入力（各段 textarea）。'
    '段階ごとに「次へ」で掘り下げ。第5段階で根本原因確定。【下部】再発防止策（textarea）、効果確認方法（textarea）、'
    '効果確認期限（text, YYYYMMDD）。【最終】「承認申請」ボタン→承認フローへ。',
    [['capa-inc-info','table','異常報告引継情報','表示のみ','','',''],
     ['capa-why1','textarea','なぜ①','直接原因','','必須',''],
     ['capa-why2','textarea','なぜ②','①への掘り下げ','','必須',''],
     ['capa-why3','textarea','なぜ③','②への掘り下げ','','必須',''],
     ['capa-why4','textarea','なぜ④','③への掘り下げ','','必須',''],
     ['capa-why5','textarea','なぜ⑤（根本原因）','④への掘り下げ','','必須',''],
     ['capa-countermeasure','textarea','再発防止策','','','必須',''],
     ['capa-verify-method','textarea','効果確認方法','','','必須',''],
     ['capa-verify-date','text','効果確認期限','YYYYMMDD','','必須、未来日',''],
     ['capa-btn-submit','submit','承認申請','','','','']],
    None,
    {'Action Path':'/counter/capa/create','Action Class':'com.struts-lab.action.counter.CapaAction',
     'Form Bean':'com.struts-lab.form.counter.CapaForm(ValidatorForm)','Forward(SUCCESS)':'counter.list',
     'Forward(INPUT)':'counter.capa'},
    ['PG-CTR-CAPA-001'], ['なぜなぜ分析5段階：各段の入力値が次段にどう影響するか','全textareaの必須バリデーション','異常報告→是正処置のデータ引継ぎ'])

w05 = XlsxWriter()
w05.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w05.add_sheet('Sheet2', [])
w05.add_sheet('対応指示登録', s14, col_widths=[22,12,20,30,30,22,22,20])
w05.add_sheet('対応指示一覧', s15, col_widths=[20,12,18,30,30,22,22,20])
w05.add_sheet('対応指示詳細・完了報告', s16, col_widths=[22,12,20,30,30,22,22,20])
w05.add_sheet('是正処置報告書', s17, col_widths=[22,12,20,30,30,22,22,20])
w05.save('05_画面設計書_対応指示・是正.xlsx')

# ===== 06_画面設計書_組織・カレンダー・保守部品.xlsx =====

# 部署マスタ一覧
s19 = sc('SCR-ORG-DEPT-001', '部署マスタ一覧', 'PG-ORG-DEPT-001', '組織管理',
    '【上部】検索：部署コード（text）、部署名（text, 部分一致）。【中部】部署ツリー：table形式の4階層ツリー（本社→支社→営業所→出張所）。'
    '階層インデントはセル内の空白文字またはCSSで表現。各行に「編集」「子部署追加」リンク。'
    '【下部】「新規部署（最上位）」「CSV出力」「統廃合履歴表示」ボタン。',
    [['dept-s-code','text','部署コード','','','',''],
     ['dept-s-name','text','部署名','部分一致','','',''],
     ['dept-tree-row-{n}','link','部署名(ツリー)','インデント付き','クリック→編集へ','階層インデント検証',''],
     ['dept-tree-level-{n}','text','階層レベル','(インデント幅)','','',''],
     ['dept-btn-add-child-{n}','link','+子部署追加','','','',''],
     ['dept-btn-new-top','submit','新規部署（最上位）','','','',''],
     ['dept-btn-csv','submit','CSV出力','','','',''],
     ['dept-btn-history','submit','統廃合履歴','','履歴画面へ遷移','','']],
    [['部署コード','VARCHAR(10)','○',''],['部署名','VARCHAR(100)','○',''],['上位部署','VARCHAR(10)','',''],
     ['階層レベル','INT','','1-4'],['有効開始日','DATE','',''],['有効終了日','DATE','','']],
    {'Action Path':'/org/dept/list','Action Class':'com.struts-lab.action.org.DeptListAction',
     'Form Bean':'com.struts-lab.form.org.DeptSearchForm','Forward(SUCCESS)':'org.dept.list',
     'Forward(EDIT)':'org.dept.edit'},
    ['PG-ORG-DEPT-001','PG-ORG-DEPT-002'], ['4階層ツリーのインデント検証','階層別の追加/編集画面遷移'])

# 部署マスタ登録・編集
s20 = sc('SCR-ORG-DEPT-002', '部署マスタ登録・編集', 'PG-ORG-DEPT-002', '組織管理',
    '【上部】部署コード（自動採番/表示）、部署名（text, 必須）、上位部署（text + 「選択」ボタン→ツリーポップアップ→選択→値反映）。'
    '有効期間：開始日（text, YYYYMMDD）、終了日（text, YYYYMMDD）。終了日＜開始日不可。過去日に遡った異動の場合、警告表示。'
    '部署種別（select: 本社/支社/営業所/出張所）。住所（text）、電話番号（text）。「保存」「キャンセル」。',
    [['dept-code','text','部署コード','自動採番','表示のみ','',''],
     ['dept-name','text','部署名','','必須','',''],
     ['dept-parent','text','上位部署','読取専用','','ポップアップ選択',''],
     ['dept-parent-btn','button','選択','window.open→ツリーポップアップ','','ウィンドウハンドル',''],
     ['dept-start-date','text','有効開始日','YYYYMMDD','','必須',''],
     ['dept-end-date','text','有効終了日','YYYYMMDD','','過去日チェック',''],
     ['dept-type','select','部署種別','本社/支社/営業所/出張所','','必須',''],
     ['dept-address','text','住所','','','',''],
     ['dept-tel','text','電話番号','','','',''],
     ['dept-btn-save','submit','保存','','','',''],
     ['dept-btn-cancel','submit','キャンセル','','一覧へ','','']],
    None,
    {'Action Path':'/org/dept/save','Action Class':'com.struts-lab.action.org.DeptSaveAction',
     'Form Bean':'com.struts-lab.form.org.DeptForm(ValidatorForm)','Forward(SUCCESS)':'org.dept.list',
     'Forward(INPUT)':'org.dept.edit'},
    ['PG-ORG-DEPT-002'], ['過去日に遡った部署異動の警告バリデーション','ツリーポップアップからの選択値反映'])

# 担当者マスタ一覧
s21 = sc('SCR-ORG-EMP-001', '担当者マスタ一覧', 'PG-ORG-EMP-001', '組織管理',
    '【上部】複合検索：部署（select, ツリー風flat）、職位（select）、技能資格（select, 複数選択可のチェックボックス群）、入社年（from/to）。検索／クリア。'
    '【中部】一覧：社員番号、氏名、部署名、職位、保有資格（カンマ区切り）、認定有効期限。有効期限切れ行は赤背景。ページング。'
    '【下部】「CSV出力」「一括ロック/解除」「新規登録」ボタン。',
    [['emp-s-dept','select','部署','','','',''],
     ['emp-s-position','select','職位','係長/主任/一般','','',''],
     ['emp-s-qual-{n}','checkbox','資格{n}','電気主任技術者/電気工事士/…','複数選択可','',''],
     ['emp-s-year-from','text','入社年(from)','YYYY','','',''],
     ['emp-s-year-to','text','入社年(to)','YYYY','','',''],
     ['emp-expired-{n}','text','有効期限切れ行','赤背景','','CSSクラス検証',''],
     ['emp-btn-lock','submit','一括ロック/解除','','','',''],
     ['emp-btn-csv','submit','CSV出力','','','',''],
     ['emp-btn-new','submit','新規登録','','','','']],
    [['社員番号','VARCHAR(10)','○',''],['氏名','VARCHAR(100)','○',''],['部署名','VARCHAR(100)','',''],
     ['職位','VARCHAR(10)','',''],['保有資格','VARCHAR(200)','',''],['認定有効期限','DATE','','']],
    {'Action Path':'/org/emp/list','Action Class':'com.struts-lab.action.org.EmpListAction',
     'Form Bean':'com.struts-lab.form.org.EmpSearchForm','Forward(SUCCESS)':'org.emp.list',
     'Forward(EDIT)':'org.emp.edit'},
    ['PG-ORG-EMP-001','PG-ORG-EMP-002'], ['複数チェックボックス資格選択のAND/OR検索','有効期限切れのCSSクラス検証','一括ロック/解除のconfirm'])

# 担当者マスタ登録・編集
s22 = sc('SCR-ORG-EMP-002', '担当者マスタ登録・編集', 'PG-ORG-EMP-002', '組織管理',
    '【5セクション大フォーム】'
    '①基本情報：社員番号（自動採番）、氏名（text,必須）、フリガナ（text）、生年月日（text, YYYYMMDD）、入社年月（text, YYYYMM）'
    '②所属・職位：部署（select）、職位（select）、配属日（text, YYYYMMDD）'
    '③保有資格・技能：資格選択（複数checkbox）、資格認定日（text, YYYYMMDD）×資格ごと、認定有効期限（text）×資格ごと'
    '④点検員認定：点検員ランク（select: A/B/C/無）、認定日（text）、有効期限（text, 期限切れバリデーション）'
    '⑤アカウント情報：ログインID（text, 必須）、パスワード（password）、パスワード確認（password）'
    '【下部】「保存」「キャンセル」。',
    [['emp-code','text','社員番号','自動採番','表示のみ','',''],
     ['emp-name','text','氏名','','必須','',''],
     ['emp-kana','text','フリガナ','','','カタカナバリデーション',''],
     ['emp-birth','text','生年月日','YYYYMMDD','','',''],
     ['emp-join-date','text','入社年月','YYYYMM','','',''],
     ['emp-dept','select','部署','','必須','',''],
     ['emp-position','select','職位','係長/主任/一般','','',''],
     ['emp-assign-date','text','配属日','YYYYMMDD','','',''],
     ['emp-qual-check-{n}','checkbox','資格{n}','電気主任技術者/電気工事士/…','','',''],
     ['emp-qual-date-{n}','text','認定日','YYYYMMDD','資格ごと','',''],
     ['emp-qual-expire-{n}','text','有効期限','YYYYMMDD','期限切れバリデーション','条件付きバリデーション',''],
     ['emp-ins-rank','select','点検員ランク','A/B/C/無','','',''],
     ['emp-ins-date','text','点検員認定日','YYYYMMDD','','',''],
     ['emp-ins-expire','text','有効期限','YYYYMMDD','期限切れ不可','',''],
     ['emp-login-id','text','ログインID','','','必須、重複不可',''],
     ['emp-password','password','パスワード','','','必須、長さ制限',''],
     ['emp-password2','password','パスワード確認','','','一致バリデーション',''],
     ['emp-btn-save','submit','保存','','','',''],
     ['emp-btn-cancel','submit','キャンセル','','','','']],
    None,
    {'Action Path':'/org/emp/save','Action Class':'com.struts-lab.action.org.EmpSaveAction',
     'Form Bean':'com.struts-lab.form.org.EmpForm(ValidatorForm)','Forward(SUCCESS)':'org.emp.list',
     'Forward(INPUT)':'org.emp.edit'},
    ['PG-ORG-EMP-002'], ['5セクション大フォームのバリデーション全セクション一括','資格ごとの有効期限バリデーション（n個の条件付き必須）',
     'パスワード確認の一致バリデーション','ログインID重複チェック'])

# 休日カレンダー一覧
s23 = sc('SCR-CAL-LIST-001', '休日カレンダー一覧', 'PG-CAL-LIST-001', 'カレンダー管理',
    '【上部】年度選択（select）。「表示」ボタン。【中部】月別カレンダー（table形式）：行=週、列=日～土。'
    '休日セルは種別で色分け（法定休日=赤、会社指定休日=青、点検停止日=黄）。日付クリック→登録/編集画面へ。'
    '【下部】「一括設定（曜日指定）」「CSV出力」。',
    [['cal-year','select','年度','2025/2026/2027','','',''],
     ['cal-btn-show','submit','表示','','','',''],
     ['cal-cell-{m}-{d}','link','日付セル','色分け','クリック→登録/編集','CSSクラス検証(色)',''],
     ['cal-btn-bulk','submit','一括設定','','曜日指定で一括登録画面へ','',''],
     ['cal-btn-csv','submit','CSV出力','','','','']],
    None,
    {'Action Path':'/cal/list','Action Class':'com.struts-lab.action.cal.CalendarListAction',
     'Form Bean':'com.struts-lab.form.cal.CalendarForm','Forward(SUCCESS)':'cal.list',
     'Forward(EDIT)':'cal.edit'},
    ['PG-CAL-LIST-001','PG-CAL-REG-001'], ['月別カレンダーのセル色検証','年度切替のデータ範囲検証'])

# 休日登録・編集
s24 = sc('SCR-CAL-REG-001', '休日登録・編集', 'PG-CAL-REG-001', 'カレンダー管理',
    '【上部】日付範囲指定：開始日（text, YYYYMMDD）～終了日（text, YYYYMMDD）、休日種別（select: 法定休日/会社指定休日/点検停止日）、休日名称（text）。'
    '「一括登録」ボタン→重複チェック→登録。振替出勤日設定：休日指定→振替出勤日（text, YYYYMMDD）→「設定」。'
    '【下部】「削除」「戻る」。',
    [['cal-date-from','text','開始日','YYYYMMDD','','必須',''],
     ['cal-date-to','text','終了日','YYYYMMDD','','必須、from≦to',''],
     ['cal-type','select','休日種別','法定休日/会社指定休日/点検停止日','','必須',''],
     ['cal-name','text','休日名称','テキスト','','',''],
     ['cal-btn-bulk-reg','submit','一括登録','','重複チェック→登録','重複バリデーション',''],
     ['cal-transfer-from','text','振替元休日','YYYYMMDD','','',''],
     ['cal-transfer-to','text','振替出勤日','YYYYMMDD','','',''],
     ['cal-btn-transfer','submit','振替設定','','','',''],
     ['cal-btn-delete','submit','削除','','','confirm',''],
     ['cal-btn-back','submit','戻る','','','','']],
    None,
    {'Action Path':'/cal/save','Action Class':'com.struts-lab.action.cal.CalendarSaveAction',
     'Form Bean':'com.struts-lab.form.cal.CalendarRegForm(ValidatorForm)','Forward(SUCCESS)':'cal.list',
     'Forward(INPUT)':'cal.edit'},
    ['PG-CAL-REG-001'], ['範囲指定一括登録の重複チェック（既存休日との重複）','計画停止期間と通常休日の重複チェック'])

# 保守部品一覧
s25 = sc('SCR-PRT-LIST-001', '保守部品一覧', 'PG-PRT-LIST-001', '保守部品管理',
    '【上部】複合検索：設備種別（select）、部品種別（select）、在庫ステータス（select: 全/充足/僅少/在庫切れ）、キーワード（text）。'
    '【中部】一覧：部品コード、部品名、設備種別、在庫数、発注点、安全在庫数、在庫ステータス（バッジ色分け）。'
    '在庫切れ=赤、僅少=黄。ページング。【下部】「CSV出力」「新規登録」。',
    [['prt-s-eqp-type','select','設備種別','','','',''],
     ['prt-s-part-type','select','部品種別','','','',''],
     ['prt-s-stock-status','select','在庫ステータス','全/充足/僅少/在庫切れ','','',''],
     ['prt-s-keyword','text','キーワード','','','',''],
     ['prt-stock-badge-{n}','span','在庫バッジ','充足/僅少/在庫切れ','色分け','CSSクラス',''],
     ['prt-btn-csv','submit','CSV出力','','','',''],
     ['prt-btn-new','submit','新規登録','','','','']],
    [['部品コード','VARCHAR(10)','○',''],['部品名','VARCHAR(100)','○',''],['設備種別','VARCHAR(10)','',''],
     ['在庫数','INT','',''],['発注点','INT','',''],['安全在庫数','INT','',''],['在庫ステータス','VARCHAR(5)','','']],
    {'Action Path':'/parts/list','Action Class':'com.struts-lab.action.parts.PartsListAction',
     'Form Bean':'com.struts-lab.form.parts.PartsSearchForm','Forward(SUCCESS)':'parts.list'},
    ['PG-PRT-LIST-001','PG-PRT-REG-001'], ['在庫バッジの色分け（CSSクラス検証）','在庫僅少/切れの境界値テスト'])

# 保守部品登録・編集
s26 = sc('SCR-PRT-REG-001', '保守部品登録・編集', 'PG-PRT-REG-001', '保守部品管理',
    '【上部】部品コード（自動採番）、部品名（text,必須）、部品種別（select）、単位（select）。'
    '【中部】適用設備（チェックボックスツリー：設備種別→設備名の2階層、複数選択可）。'
    '【下部】発注点（text）、安全在庫数（text）、単価（text）、仕入先（text）、備考（textarea）、添付ファイル（file, 仕様書PDF）。'
    '「保存」「キャンセル」。',
    [['prt-code','text','部品コード','自動採番','','',''],
     ['prt-name','text','部品名','','必須','',''],
     ['prt-type','select','部品種別','ガスケット/ボルト/絶縁油/…','','',''],
     ['prt-unit','select','単位','個/式/m/kg/L','','',''],
     ['prt-eqp-check-{n}','checkbox','適用設備','設備名','ツリー階層インデント','',''],
     ['prt-order-point','text','発注点','数値','','',''],
     ['prt-safety-stock','text','安全在庫数','数値','','発注点＞安全在庫数',''],
     ['prt-price','text','単価（円）','数値','','',''],
     ['prt-supplier','text','仕入先','テキスト','','',''],
     ['prt-note','textarea','備考','','','',''],
     ['prt-file','file','添付ファイル','仕様書PDF等','','',''],
     ['prt-btn-save','submit','保存','','','',''],
     ['prt-btn-cancel','submit','キャンセル','','','','']],
    None,
    {'Action Path':'/parts/save','Action Class':'com.struts-lab.action.parts.PartsSaveAction',
     'Form Bean':'com.struts-lab.form.parts.PartsForm(ValidatorForm)','Forward(SUCCESS)':'parts.list',
     'Forward(INPUT)':'parts.edit'},
    ['PG-PRT-REG-001'], ['チェックボックスツリーの階層表示検証','発注点＞安全在庫数のバリデーション'])

# 部品使用実績一覧
s27 = sc('SCR-PRT-USAGE-001', '部品使用実績一覧', 'PG-PRT-USAGE-001', '保守部品管理',
    '【上部】検索：期間（from/to）、設備種別（select）、部品（select）。検索／クリア。'
    '【中部】一覧：使用日、部品名、設備名、使用量、使用前在庫、使用後在庫、使用目的（点検/修繕）、担当者。'
    '在庫が発注点下回った行は警告表示。ページング。【下部】「CSV出力」。',
    [['usage-s-date-from','text','使用日(from)','YYYYMMDD','','',''],
     ['usage-s-date-to','text','使用日(to)','YYYYMMDD','','',''],
     ['usage-s-eqp-type','select','設備種別','','','',''],
     ['usage-s-part','select','部品','','','',''],
     ['usage-warning-{n}','text','在庫警告行','発注点下回り','','CSSクラス','']],
    [['使用日','DATE','○',''],['部品名','VARCHAR(100)','',''],['設備名','VARCHAR(100)','',''],
     ['使用量','INT','',''],['使用前在庫','INT','',''],['使用後在庫','INT','',''],
     ['使用目的','VARCHAR(5)','','点検/修繕'],['担当者','VARCHAR(50)','','']],
    {'Action Path':'/parts/usage','Action Class':'com.struts-lab.action.parts.PartsUsageAction',
     'Form Bean':'com.struts-lab.form.parts.PartsUsageSearchForm','Forward(SUCCESS)':'parts.usage'},
    ['PG-PRT-USAGE-001'], ['在庫自動引当表示（使用後在庫が発注点下回る場合の警告）'])

w06 = XlsxWriter()
w06.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w06.add_sheet('Sheet2', [])
w06.add_sheet('部署マスタ一覧', s19, col_widths=[20,12,18,30,30,22,22,20])
w06.add_sheet('部署マスタ登録編集', s20, col_widths=[20,12,18,30,30,22,22,20])
w06.add_sheet('担当者マスタ一覧', s21, col_widths=[20,12,18,30,30,22,22,20])
w06.add_sheet('担当者マスタ登録編集', s22, col_widths=[22,12,18,30,30,22,22,20])
w06.add_sheet('休日カレンダー一覧', s23, col_widths=[20,12,18,30,30,22,22,20])
w06.add_sheet('休日登録編集', s24, col_widths=[20,12,18,30,30,22,22,20])
w06.add_sheet('保守部品一覧', s25, col_widths=[20,12,18,30,30,22,22,20])
w06.add_sheet('保守部品登録編集', s26, col_widths=[20,12,18,30,30,22,22,20])
w06.add_sheet('部品使用実績一覧', s27, col_widths=[20,12,18,30,30,22,22,20])
w06.save('06_画面設計書_組織・カレンダー・保守部品.xlsx')

# ===== 07_画面設計書_レポート.xlsx =====
s18 = sc('SCR-RPT-SUMMARY-001', '総合レポート', 'PG-RPT-SUMMARY-001', 'レポート',
    '【上部】期間指定：集計年月（from/to, YYYYMM）、設備種別（select, 全/個別）、担当班（select, 全/個別）。「表示」ボタン。'
    '【中部①】点検実施率推移：table形式の月別グラフ。行=設備種別、列=月（from～toの全月）。セル内に実施率%表示。'
    '目標値（95%）未満のセルは赤背景。【中部②】異常発生傾向：月別×異常種別のクロス集計表。'
    '【中部③】設備別異常件数ランキング：上位10件のテーブル。'
    '【下部】「CSV出力」「印刷用表示（window.open）」。',
    [['rpt-date-from','text','集計年月(from)','YYYYMM','','',''],
     ['rpt-date-to','text','集計年月(to)','YYYYMM','','',''],
     ['rpt-eqp-type','select','設備種別','全/変圧器/遮断器/…','','',''],
     ['rpt-team','select','担当班','全/保守1班/…','','',''],
     ['rpt-btn-show','submit','表示','','','',''],
     ['rpt-rate-cell-{行}-{月}','text','実施率セル','%表示','目標値未満=赤','動的カラム(月数可変)',''],
     ['rpt-cross-cell-{行}-{列}','text','クロス集計セル','件数','','',''],
     ['rpt-ranking-{n}','text','ランキング行','設備名/件数','','',''],
     ['rpt-btn-csv','submit','CSV出力','','','',''],
     ['rpt-btn-print','button','印刷用表示','window.open','','新規ウィンドウ','']],
    [['月', '動的', '', 'from～toの全月'], ['設備名/異常種別', 'VARCHAR(100)', '', '']],
    {'Action Path':'/report/summary','Action Class':'com.struts-lab.action.report.SummaryReportAction',
     'Form Bean':'com.struts-lab.form.report.ReportForm','Forward(SUCCESS)':'report.summary'},
    ['PG-RPT-SUMMARY-001'],
    ['動的カラム（期間により月数可変）のヘッダ/データ対応検証','実施率の色分け検証（CSS）','クロス集計の数値正確性','印刷用別ウィンドウ'])

w07 = XlsxWriter()
w07.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w07.add_sheet('Sheet2', [])
w07.add_sheet('総合レポート', s18, col_widths=[22,12,20,30,30,22,22,20])
w07.save('07_画面設計書_レポート.xlsx')

print("Done: 04-07")
