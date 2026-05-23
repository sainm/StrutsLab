#!/usr/bin/env python3
"""生成 03_画面設計書_点検計画・実施.xlsx"""
import sys; sys.path.insert(0, '.')
from _xlsx_gen import *

def sc(id, name, pg, layout, els, tbl=None, smap=None, rpg=None, tn=None):
    d = []
    d.append(hdr_row(['画面設計書', '', '', '', '', '', '', '']))
    d.append(hdr_row(['文書管理情報', '', '', '', '', '', '', '']))
    for r in [['システムID', 'STRUTSLAB-001', '', '', 'サブシステム名', '点検計画・実施', '', ''],
              ['画面ID', id, '', '', '画面名', name, '', ''],
              ['プログラムID', pg, '', '', '文書番号', f'DOC-SCR-{id}', '', ''],
              ['版数', '1.0', '', '', '作成日', '2026/05/23', '', ''],
              ['作成者', 'システム開発部', '', '', '区分', '新規', '', ''],
              ['アクセス権限', '全ユーザー', '', '', '', '', '', '']]:
        d.append(r)
    d.append(empty_row())
    d.append(hdr_row(['画面レイアウト', '', '', '', '', '', '', '']))
    d.append([layout, '', '', '', '', '', '', ''])
    d.append(empty_row())
    d.append(hdr_row(['画面要素一覧', '', '', '', '', '', '', '']))
    d.append(hdr_row(['要素ID', '種別', '表示名', '入力値／選択肢', '説明', 'テスト難点タグ', '備考', '']))
    for e in els:
        d.append(list(e) + [''] * (8 - len(e)))
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

# 年間点検計画一覧
s5 = sc('SCR-INS-PLAN-001', '年間点検計画一覧', 'PG-INS-PLAN-001',
    '【上部】年度選択（select）、設備種別（select）、担当班（select）。「表示」ボタン。'
    '【中部】マトリクス表：行=設備名、列=4月～3月（12カラム）。セル内に「計画:3回 実績:2回」形式。'
    '計画未達セルは赤背景、計画超過セルは黄背景。'
    '【下部】「CSV出力」「計画ロック/解除」ボタン。',
    [['plan-year', 'select', '年度', '2025／2026／2027', '', '', ''],
     ['plan-type', 'select', '設備種別', '変圧器／遮断器／…', '', '', ''],
     ['plan-team', 'select', '担当班', '保守1班／保守2班／…', '', '', ''],
     ['plan-btn-show', 'submit', '表示', '', '', '', ''],
     ['plan-cell-{行}-{月}', 'text', 'マトリクスセル', '計画:数字 実績:数字', '', '動的カラム（12ヶ月）', '2月は日数変動'],
     ['plan-btn-csv', 'submit', 'CSV出力', '', '', '', ''],
     ['plan-btn-lock', 'submit', '計画ロック/解除', '', '', 'window.confirm', '']],
    [['設備名', 'VARCHAR(100)', '', ''], ['4月～3月', '動的(12列)', '', 'マトリクスセル']],
    {'Action Path': '/ins/plan/yearly', 'Action Class': 'com.struts-lab.action.ins.YearlyPlanAction',
     'Form Bean': 'com.struts-lab.form.ins.YearlyPlanForm', 'Forward (SUCCESS)': 'ins.plan.yearly'},
    ['PG-INS-PLAN-001'], ['動的カラム（年度により月数=12固定だが行数可変）', 'セル色のクラス属性検証', '計画ロック後のセル編集不可検証'])

# 点検計画登録（Wizard）
s6 = sc('SCR-INS-PLAN-002', '点検計画登録（Wizard）', 'PG-INS-PLAN-002',
    '【Wizardステップ表示】①対象設備選択 → ②点検項目テンプレート選択 → ③日程・担当者設定 → ④確認・登録'
    '【各ステップ共通】「戻る」「進む」ボタン。確認画面のみ「一時保存」「確定」ボタン。'
    'ステップ間のデータ引継ぎは hidden フィールドで行う。'
    'ステップ1：設備ツリー（select, 複数選択不可→次へ）、設備情報表示ブロック'
    'ステップ2：テンプレート選択（radio）、選択済みテンプレートの項目数表示'
    'ステップ3：点検予定日（text, YYYYMMDD）、担当班（select）、担当者（select, 班に連動）、備考（textarea）'
    'ステップ4：全入力内容の確認表示（table形式）。「一時保存」「確定」ボタン。',
    [['wiz-step-indicator', 'text', 'ステップ表示', '①→②→③→④（現在地ハイライト）', '', '', ''],
     ['wiz-eqp-select', 'select', '対象設備', '設備一覧から選択', 'ステップ1', '必須', ''],
     ['wiz-btn-next-1', 'submit', '進む', '', 'ステップ2へ', 'hidden値のバリデーション', ''],
     ['wiz-tmpl-radio', 'radio', 'テンプレート選択', 'テンプレート名', 'ステップ2', '必須', ''],
     ['wiz-btn-back-2', 'submit', '戻る', '', 'ステップ1へ', '前画面データ保持検証', ''],
     ['wiz-btn-next-2', 'submit', '進む', '', 'ステップ3へ', '', ''],
     ['wiz-date', 'text', '点検予定日', 'YYYYMMDD', 'ステップ3', '必須、未来日', ''],
     ['wiz-team', 'select', '担当班', '保守1班／…', 'ステップ3', '必須', ''],
     ['wiz-person', 'select', '担当者', '班に連動', 'ステップ3', '必須', '班×担当者の連動'],
     ['wiz-note', 'textarea', '備考', '', 'ステップ3', '', ''],
     ['wiz-btn-back-3', 'submit', '戻る', '', 'ステップ2へ', '', ''],
     ['wiz-btn-next-3', 'submit', '進む', '', 'ステップ4（確認）へ', '', ''],
     ['wiz-confirm-table', 'table', '確認表示', '全入力内容', 'ステップ4', '', ''],
     ['wiz-btn-temp-save', 'submit', '一時保存', '', '下書き保存', 'セッションに保存', ''],
     ['wiz-btn-commit', 'submit', '確定', '', '本登録', '', ''],
     ['wiz-btn-back-4', 'submit', '戻る', '', 'ステップ3へ修正', '戻る→修正→進むloop', '']],
    None,
    {'Action Path': '/ins/plan/register', 'Action Class': 'com.struts-lab.action.ins.PlanWizardAction',
     'Form Bean': 'com.struts-lab.form.ins.PlanWizardForm (ValidatorForm)',
     'Forward (STEP1)': 'ins.plan.wiz1', 'Forward (STEP2)': 'ins.plan.wiz2',
     'Forward (STEP3)': 'ins.plan.wiz3', 'Forward (CONFIRM)': 'ins.plan.confirm',
     'Forward (SUCCESS)': 'ins.plan.yearly'},
    ['PG-INS-PLAN-002'],
    ['4ステップWizardのデータ持ち回り（hidden→再POST）検証', '「戻る」→修正→「進む」ループの全パス網羅',
     'ステップ3→ステップ1不備発覚→戻る→修正→再確認フロー', '一時保存→セッション切れ→再開不可のハンドリング'])

# 点検実施一覧（当日）
s7 = sc('SCR-INS-DAILY-001', '点検実施一覧（当日）', 'PG-INS-DAILY-001',
    '【上部】対象日（text, YYYYMMDD, デフォルト本日）、担当者（select）。「表示」ボタン。'
    '【中部】一覧テーブル：設備名、点検種別、予定時刻、ステータスバッジ（未了=灰／一部完了=黄／完了=緑）、担当者。'
    'ステータスでフィルタ可能。各行クリック→実施入力画面へ直接遷移。',
    [['daily-date', 'text', '対象日', 'YYYYMMDD（デフォルト本日）', '', '', ''],
     ['daily-person', 'select', '担当者', '', '', '', ''],
     ['daily-btn-show', 'submit', '表示', '', '', '', ''],
     ['daily-status-filter', 'select', 'ステータスフィルタ', '全部／未了／一部完了／完了', '', '', ''],
     ['daily-status-badge-{n}', 'span', 'ステータスバッジ', '未了/一部完了/完了', '', 'CSSクラス検証', ''],
     ['daily-row-{n}', 'link', '行全体', 'クリック→実施入力へ', '', '', '']],
    [['設備名', 'VARCHAR(100)', '', ''], ['点検種別', 'VARCHAR(10)', '', ''],
     ['予定時刻', 'VARCHAR(5)', '', 'HH:MM'], ['ステータス', 'VARCHAR(10)', '', ''],
     ['担当者', 'VARCHAR(50)', '', '']],
    {'Action Path': '/ins/daily', 'Action Class': 'com.struts-lab.action.ins.DailyListAction',
     'Form Bean': 'com.struts-lab.form.ins.DailyForm', 'Forward (SUCCESS)': 'ins.daily.list',
     'Forward (EXEC)': 'ins.exec.input'},
    ['PG-INS-DAILY-001', 'PG-INS-EXEC-001'],
    ['ステータスバッジの色分け検証（CSS class）', '一覧→実施入力直接遷移でパラメータ引継ぎ検証'])

# 点検実施入力
s8 = sc('SCR-INS-EXEC-001', '点検実施入力', 'PG-INS-EXEC-001',
    '【ブロック1】設備基本情報（表示のみ）：設備コード、設備名、設備種別、電圧階級、設置場所、前回点検日'
    '【ブロック2】チェックリスト：大分類見出し→中分類見出し→個別項目のネストテーブル。'
    '各項目に「判定（radio: ○/×/△）」「実測値（text）」「所見（textarea）」。'
    '判定「×」の場合、実測値と所見が必須（Validator条件付き）。△の場合、所見が必須。'
    '写真添付ボタン（各項目ごと、最大5枚/項目、file input）。'
    '【ブロック3】総合判定（radio: 正常/異常あり/要観察）、総合所見（textarea）、次回点検推奨日（text）'
    '総合判定「異常あり」→「異常報告へ」ボタン表示。「保存」ボタン。',
    [['exec-eqp-info', 'table', '設備基本情報', '表示のみ', '', '', ''],
     ['exec-cat1-{n}', 'text', '大分類見出し', '表示のみ', '例: 外観点検', '', 'ネストテーブル（rowspan）'],
     ['exec-cat2-{n}-{m}', 'text', '中分類見出し', '表示のみ', '例: ブッシング部', '', 'ネストテーブル'],
     ['exec-judge-{i}', 'radio', '判定', '○／×／△', '', '条件付きバリデーション', '×→実測値+所見必須'],
     ['exec-value-{i}', 'text', '実測値', '数値', '判定×のとき必須', '条件付き必須', ''],
     ['exec-note-{i}', 'textarea', '所見', 'テキスト', '判定×/△のとき必須', '条件付き必須', ''],
     ['exec-photo-{i}-{j}', 'file', '写真{1-5}', '', '', 'ファイル添付', ''],
     ['exec-summary-judge', 'radio', '総合判定', '正常／異常あり／要観察', '', '必須', ''],
     ['exec-summary-note', 'textarea', '総合所見', '', '', '', ''],
     ['exec-next-date', 'text', '次回点検推奨日', 'YYYYMMDD', '', '', ''],
     ['exec-btn-incident', 'submit', '異常報告へ', '総合判定=異常あり時のみ表示', '異常報告登録画面へ遷移', '条件付きボタン表示', 'データ引継ぎ'],
     ['exec-btn-save', 'submit', '保存', '', '', '', '']],
    None,
    {'Action Path': '/ins/exec/input', 'Action Class': 'com.struts-lab.action.ins.ExecInputAction',
     'Form Bean': 'com.struts-lab.form.ins.ExecForm (ValidatorForm)', 'Forward (SUCCESS)': 'ins.daily.list',
     'Forward (INPUT)': 'ins.exec.input', 'Forward (INCIDENT)': 'inc.create',
     'Validator': 'exec-form-validation'},
    ['PG-INS-EXEC-001', 'PG-INS-EXEC-002', 'PG-INC-CREATE-001'],
    ['ネストテーブル（大分類rowspan→中分類→項目）：trの親子関係がDOM上フラット', '条件付きバリデーションの組み合わせ爆発：全項目×(○/×/△) = 3^N通り',
     '写真添付＋同時バリデーション：ファイル選択状態でエラー→再描画後もファイル選択維持？', '「異常報告へ」ボタンの条件表示：総合判定変更で表示/非表示切替'])

# 点検実施詳細・修正
s9 = sc('SCR-INS-EXEC-002', '点検実施詳細・修正', 'PG-INS-EXEC-002',
    '【上部】実施済み点検結果の表示（全ブロック読み取り専用）。'
    '【下部】「修正申請」ボタン→修正理由入力ダイアログ→承認者に申請。'
    '修正理由（textarea, 必須）がないと申請できない。'
    '申請後は「申請済み」表示、承認されるまで再修正不可。',
    [['exec-detail-all', 'table', '実施結果表示', '読取専用', '', '', ''],
     ['exec-mod-btn', 'submit', '修正申請', '', '', '', ''],
     ['exec-mod-reason', 'textarea', '修正理由', '', '', '必須（修正申請時）', ''],
     ['exec-mod-status', 'text', '申請ステータス', '申請中/承認済/差戻', '表示のみ', '', '']],
    None,
    {'Action Path': '/ins/exec/detail', 'Action Class': 'com.struts-lab.action.ins.ExecDetailAction',
     'Form Bean': 'com.struts-lab.form.ins.ExecForm', 'Forward (SUCCESS)': 'ins.exec.detail',
     'Forward (MODIFY)': 'ins.exec.modify', 'Forward (APPROVED)': 'ins.exec.detail'},
    ['PG-INS-EXEC-002', 'PG-INS-APPR-001'],
    ['修正申請→承認→再表示フローの全パス', '修正理由バリデーション（申請時のみ必須）', '承認済み後の修正不可検証'])

# 点検実施承認一覧
s10 = sc('SCR-INS-APPR-001', '点検実施承認一覧', 'PG-INS-APPR-001',
    '【上部】検索：期間（from/to）、担当班（select）、ステータス（select: 申請中/承認済/差戻）。'
    '【中部】一覧：申請日時、設備名、申請者、修正理由（一部表示）、ステータス。各行にチェックボックス。'
    '【下部】「一括承認」「一括差戻し」ボタン。差戻し時は差戻し理由（textarea）必須。'
    '操作後は画面上部に「N件承認しました」等の通知メッセージ表示。',
    [['appr-date-from', 'text', '申請日（from）', 'YYYYMMDD', '', '', ''],
     ['appr-date-to', 'text', '申請日（to）', 'YYYYMMDD', '', '', ''],
     ['appr-team', 'select', '担当班', '', '', '', ''],
     ['appr-status', 'select', 'ステータス', '全部／申請中／承認済／差戻', '', '', ''],
     ['appr-check-{n}', 'checkbox', '選択', '', '', '', ''],
     ['appr-btn-approve', 'submit', '一括承認', '', '', 'window.confirm', ''],
     ['appr-btn-reject', 'submit', '一括差戻し', '', '差戻し理由必須', 'window.confirm + textarea必須', ''],
     ['appr-reject-reason', 'textarea', '差戻し理由', '', '', '一括差戻し時必須', ''],
     ['appr-msg', 'text', '通知メッセージ', 'N件承認しました', '操作結果表示', '', '']],
    [['申請日時', 'TIMESTAMP', '', ''], ['設備名', 'VARCHAR(100)', '', ''], ['申請者', 'VARCHAR(50)', '', ''],
     ['修正理由', 'VARCHAR(200)', '', '一部表示'], ['ステータス', 'VARCHAR(10)', '', '']],
    {'Action Path': '/ins/approval/list', 'Action Class': 'com.struts-lab.action.ins.ApprovalListAction',
     'Form Bean': 'com.struts-lab.form.ins.ApprovalForm', 'Forward (SUCCESS)': 'ins.appr.list'},
    ['PG-INS-APPR-001'],
    ['一括承認：confirm OK→全件処理', '一括差戻し：confirm OK→理由必須→理由未入力でバリデーション',
     '一括処理後の通知メッセージ表示検証', 'ページ跨ぎの選択不可確認'])

w = XlsxWriter()
w.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5,12,30,15,15,8,20])
w.add_sheet('Sheet2', [])
w.add_sheet('年間点検計画一覧', s5, col_widths=[20,12,18,30,30,22,22,20])
w.add_sheet('点検計画登録Wizard', s6, col_widths=[20,12,18,30,30,22,22,20])
w.add_sheet('点検実施一覧当日', s7, col_widths=[20,12,18,30,30,22,22,20])
w.add_sheet('点検実施入力', s8, col_widths=[20,12,18,30,30,22,22,20])
w.add_sheet('点検実施詳細修正', s9, col_widths=[20,12,18,30,30,22,22,20])
w.add_sheet('点検実施承認一覧', s10, col_widths=[20,12,18,30,30,22,22,20])
w.save('03_画面設計書_点検計画・実施.xlsx')
