#!/usr/bin/env python3
"""Generate all 16 Markdown design docs."""

import os

OUT = '/home/lh/source/StrutsLab/docs/design-docs'

def write_md(filename, lines):
    path = os.path.join(OUT, filename)
    with open(path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines) + '\n')
    print(f'  -> {filename}')

def hdr(lines, level=2):
    """Section header with lines."""
    out = []
    for line in lines:
        out.append(f'{"#" * level} {line}')
        out.append('')
    return out

def table(headers, rows):
    """Markdown table."""
    out = []
    out.append('| ' + ' | '.join(headers) + ' |')
    out.append('| ' + ' | '.join(['---'] * len(headers)) + ' |')
    for row in rows:
        # Pad row to match header count
        padded = list(row) + [''] * (len(headers) - len(row))
        out.append('| ' + ' | '.join(str(c) for c in padded) + ' |')
    out.append('')
    return out

def kv_table(pairs):
    """Key-value as 2-col wide table."""
    headers = ['項目', '値', '', '', '', '']
    rows = []
    for k, v in pairs:
        rows.append([k, v, '', '', '', ''])
    return table(headers, rows)

def divider():
    return ['---', '']

# ===== 01_システム概要書.md =====
lines = []
lines.append('# 01 システム概要書')
lines.append('')
lines += hdr(['文書管理情報'], 2)
lines += kv_table([
    ('システムID', 'STRUTSLAB-001'),
    ('システム名', '電力設備巡視点検管理システム'),
    ('サブシステム名', '設備巡視点検'),
    ('開発言語', 'Java 1.8 / Struts 1.3 / MyBatis 3.5'),
    ('データベース', 'H2 Database（ファイルモード）'),
    ('文書番号', 'SYS-OVERVIEW-001'),
    ('版数', '1.0'),
    ('作成日', '2026/05/23'),
    ('作成者', 'システム開発部'),
])
lines += hdr(['システムアーキテクチャ'], 2)
lines += table(
    ['レイヤー', '採用技術', 'バージョン', '役割'],
    [
        ['プレゼンテーション層', 'Struts 1.3', '1.3.10', 'ActionServlet, Action, ActionForm, JSP'],
        ['プレゼンテーション層', 'Tiles', '1.3.10', '画面レイアウト共通化（header/body/footer/menu）'],
        ['プレゼンテーション層', 'JSP + カスタムタグ', '-', 'スクリプトレット主体、独自タグライブラリ'],
        ['ビジネスロジック層', 'Actionクラス', '-', 'Struts1 Action + DispatchAction'],
        ['データアクセス層', 'MyBatis', '3.5.x', 'SQLマッピング、Mapper.xml'],
        ['データベース', 'H2 Database', '1.4.200', 'ファイルモード（~/struts-lab-db）'],
        ['バリデーション', 'Validator Plug-in', '1.3.10', 'validation.xml, validation-rules.xml'],
        ['ビルドツール', 'Maven', '3.x', 'war パッケージ → Tomcat 8.x にデプロイ'],
        ['サーブレットコンテナ', 'Apache Tomcat', '8.5.x', 'Java 1.8 対応'],
    ]
)

lines += hdr(['機能モジュール一覧'], 2)
lines += table(
    ['モジュール名', '略称', '対象画面数', '主なテスト難点'],
    [
        ['マスタ管理', 'MASTER', '4', '多段検索、ツリー選択、親子設定、CSV出力'],
        ['点検計画・実施', 'INSPECT', '7', 'Wizard、ネストチェックリスト、写真添付、条件付きバリデーション、承認フロー'],
        ['異常報告', 'INCIDENT', '3', 'ステータス遷移、条件付き必須項目、類似事例検索'],
        ['対応指示・是正', 'COUNTER', '4', '動的行追加、Nested Properties、ポップアップ選択、全明細完了チェック'],
        ['組織・要員管理', 'ORG', '4', '階層ツリー、資格期限チェック、多セクションフォーム'],
        ['カレンダー・休日', 'CALENDAR', '2', 'カレンダー表示、一括登録、重複チェック'],
        ['保守部品管理', 'PARTS', '3', 'チェックボックスツリー、在庫アラート、数量自動引当'],
        ['レポート', 'REPORT', '1', 'クロス集計、テーブル形式グラフ、期間指定'],
    ]
)

lines += hdr(['画面一覧（全27画面）'], 2)
lines += table(
    ['No.', 'モジュール', '画面ID', '画面名', '種別', '主要Struts1要素', 'テスト難点カテゴリ'],
    [
        ['1', 'MASTER', 'SCR-MST-EQP-001', '設備マスタ一覧', '一覧・検索', 'Action, ActionForm, ページング', '多段検索、一括操作、CSV'],
        ['2', 'MASTER', 'SCR-MST-EQP-002', '設備マスタ登録・編集', '登録・編集', 'ActionForm, Validator, ポップアップ', '条件付きバリデーション、Popup→親画面'],
        ['3', 'MASTER', 'SCR-MST-CHK-001', '点検項目マスタ一覧', '一覧・検索', 'Action, ActionForm', 'テンプレートコピー、並び順変更'],
        ['4', 'MASTER', 'SCR-MST-CHK-002', '点検項目マスタ登録・編集', '登録・編集', 'DispatchAction, 動的行追加', '3階層ツリー編集、一括項目追加'],
        ['5', 'INSPECT', 'SCR-INS-PLAN-001', '年間点検計画一覧', '一覧・検索', 'Action, ActionForm', 'マトリクス表示（動的カラム）'],
        ['6', 'INSPECT', 'SCR-INS-PLAN-002', '点検計画登録（Wizard）', '登録', 'ActionForm, Wizard 4ステップ', 'マルチステップ、途中離脱→再開'],
        ['7', 'INSPECT', 'SCR-INS-DAILY-001', '点検実施一覧（当日）', '一覧', 'Action, ActionForm', 'ステータスバッジ、直接遷移'],
        ['8', 'INSPECT', 'SCR-INS-EXEC-001', '点検実施入力', '入力', 'ActionForm, FileUpload, Validator', 'ネストチェックリスト、条件付き必須、写真'],
        ['9', 'INSPECT', 'SCR-INS-EXEC-002', '点検実施詳細・修正', '表示・編集', 'ActionForm, Validator', '修正申請→承認フロー'],
        ['10', 'INSPECT', 'SCR-INS-APPR-001', '点検実施承認一覧', '一覧', 'Action, 一括操作', '一括承認/差戻し'],
        ['11', 'INCIDENT', 'SCR-INC-LIST-001', '異常報告一覧', '一覧・検索', 'Action, ActionForm', '7条件複合検索、条件保存'],
        ['12', 'INCIDENT', 'SCR-INC-CREATE-001', '異常報告登録', '登録', 'ActionForm, FileUpload', '点検データ引継ぎ、類似事例検索'],
        ['13', 'INCIDENT', 'SCR-INC-DETAIL-001', '異常報告詳細', '詳細・遷移', 'ActionForm, LookupDispatchAction', '多段階ステータス遷移'],
        ['14', 'COUNTER', 'SCR-CTR-CREATE-001', '対応指示登録', '登録', 'ActionForm, Indexed Properties, ポップアップ', '動的行追加(Submit)、index再振り'],
        ['15', 'COUNTER', 'SCR-CTR-LIST-001', '対応指示一覧', '一覧・検索', 'Action, ActionForm', '複合検索、印刷用別ウィンドウ'],
        ['16', 'COUNTER', 'SCR-CTR-DETAIL-001', '対応指示詳細・完了報告', '詳細・更新', 'ActionForm, Validator', '明細個別更新、全明細完了自動検出'],
        ['17', 'COUNTER', 'SCR-CTR-CAPA-001', '是正処置報告書', '登録', 'ActionForm, Validator', 'なぜなぜ分析5段階'],
        ['18', 'REPORT', 'SCR-RPT-SUMMARY-001', '総合レポート', '表示', 'Action, ActionForm', 'クロス集計、テーブル形式グラフ'],
        ['19', 'ORG', 'SCR-ORG-DEPT-001', '部署マスタ一覧', '一覧', 'Action, ActionForm', '4階層ツリー表示'],
        ['20', 'ORG', 'SCR-ORG-DEPT-002', '部署マスタ登録・編集', '登録・編集', 'ActionForm, Validator', 'ツリーポップアップ、過去日バリデーション'],
        ['21', 'ORG', 'SCR-ORG-EMP-001', '担当者マスタ一覧', '一覧・検索', 'Action, ActionForm', '複合検索、資格期限アラート'],
        ['22', 'ORG', 'SCR-ORG-EMP-002', '担当者マスタ登録・編集', '登録・編集', 'ActionForm, Validator', '5セクション大フォーム、認定期限'],
        ['23', 'CALENDAR', 'SCR-CAL-LIST-001', '休日カレンダー一覧', '一覧', 'Action, ActionForm', '月別カレンダー表示、色分け'],
        ['24', 'CALENDAR', 'SCR-CAL-REG-001', '休日登録・編集', '登録・編集', 'ActionForm, Validator', '範囲指定一括登録、重複チェック'],
        ['25', 'PARTS', 'SCR-PRT-LIST-001', '保守部品一覧', '一覧・検索', 'Action, ActionForm', '在庫アラート表示'],
        ['26', 'PARTS', 'SCR-PRT-REG-001', '保守部品登録・編集', '登録・編集', 'ActionForm, Validator, FileUpload', 'チェックボックスツリー'],
        ['27', 'PARTS', 'SCR-PRT-USAGE-001', '部品使用実績一覧', '一覧・検索', 'Action, ActionForm', '在庫自動引当表示'],
    ]
)

lines += hdr(['Struts1 設定ファイル構成'], 2)
lines += table(
    ['ファイル', '配置パス', '役割'],
    [
        ['web.xml', '/WEB-INF/web.xml', 'ActionServlet, Tiles, Validator 設定'],
        ['struts-config.xml', '/WEB-INF/struts-config.xml', 'Actionマッピング, FormBean, Forward, Plug-in'],
        ['tiles-defs.xml', '/WEB-INF/tiles-defs.xml', 'Tilesレイアウト定義'],
        ['validation.xml', '/WEB-INF/validation.xml', 'Validator検証ルール'],
        ['validation-rules.xml', '/WEB-INF/validation-rules.xml', 'Validator基本ルール定義'],
        ['mybatis-config.xml', 'クラスパス直下', 'MyBatis設定, データソース, Mapper登録'],
        ['ApplicationResources_ja.properties', 'クラスパス直下', '日本語メッセージリソース'],
        ['*.tld', '/WEB-INF/tld/', 'カスタムタグ定義（設備ツリー、日付ピッカー）'],
    ]
)

write_md('01_システム概要書.md', lines)

# ===== 02_画面設計書_マスタ管理.md =====
lines02 = []
lines02.append('# 02 画面設計書 — マスタ管理')
lines02.append('')

# 設備マスタ一覧
lines02 += hdr(['設備マスタ一覧 (SCR-MST-EQP-001)'], 2)
lines02 += kv_table([
    ('画面ID', 'SCR-MST-EQP-001'), ('画面名', '設備マスタ一覧'),
    ('プログラムID', 'PG-MST-EQP-001'), ('サブシステム', 'マスタ管理'),
])
lines02 += hdr(['画面レイアウト'], 3)
lines02.append('【上部】検索条件：設備種別（select）、電圧階級（select）、設置年（text, 年範囲）、保全ランク（select）、担当部署（text）。検索／クリアボタン。')
lines02.append('【下部】一覧テーブル：設備コード、設備名、設備種別、電圧階級、設置年月、保全ランク、担当部署。ページング。一覧下部に「CSV出力」「選択削除」「新規登録」ボタン。')
lines02.append('')
lines02 += hdr(['画面要素一覧'], 3)
lines02 += table(
    ['要素ID', '種別', '表示名', '入力値／選択肢', 'テスト難点タグ'],
    [
        ['eqp-search-type', 'select', '設備種別', '変圧器／遮断器／開閉器／ケーブル／母線／保護継電器／計器用変成器', '複合検索（5条件AND）'],
        ['eqp-search-voltage', 'select', '電圧階級', '66kV／154kV／275kV／500kV', ''],
        ['eqp-search-year-from', 'text', '設置年（から）', 'YYYY', '過去日バリデーション'],
        ['eqp-search-year-to', 'text', '設置年（まで）', 'YYYY', 'from > to バリデーション'],
        ['eqp-search-rank', 'select', '保全ランク', 'S／A／B／C', ''],
        ['eqp-search-dept', 'text', '担当部署', 'テキスト（部分一致）', ''],
        ['eqp-select-all', 'checkbox', '全選択', '', '全選択スコープ（ページ内のみ）'],
        ['eqp-check-{n}', 'checkbox', '選択', '', 'index付きname属性'],
        ['eqp-code-{n}', 'link', '設備コード', 'クリック→編集画面へ', ''],
        ['eqp-btn-csv', 'submit', 'CSV出力', '', 'Content-Disposition検証'],
        ['eqp-btn-delete', 'submit', '選択削除', '', 'window.confirm OK/キャンセル'],
        ['eqp-btn-new', 'submit', '新規登録', '', ''],
        ['eqp-paging', 'link', 'ページング', '前へ 1 2 3 … 次へ', 'ページ跨ぎ全選択不可'],
    ]
)
lines02 += hdr(['Struts1マッピング'], 3)
lines02 += kv_table([
    ('Action Path', '/mst/eqp/list'),
    ('Action Class', 'com.struts-lab.action.mst.EqpListAction'),
    ('Form Bean', 'com.struts-lab.form.mst.EqpSearchForm'),
    ('Forward (SUCCESS)', 'eqp.list'),
])
lines02 += hdr(['テスト難点'], 3)
lines02.append('- 多段検索（5条件AND）：全組み合わせの検索結果検証が必要')
lines02.append('- 全選択チェックボックス：ページ内のみ全選択、他ページには影響しないことの検証')
lines02.append('- CSV出力：検索条件に応じた出力内容の検証')
lines02.append('- 選択削除：confirmダイアログのOK/キャンセル両方テスト')
lines02.append('')

lines02 += divider()

# 設備マスタ登録・編集
lines02 += hdr(['設備マスタ登録・編集 (SCR-MST-EQP-002)'], 2)
lines02 += kv_table([
    ('画面ID', 'SCR-MST-EQP-002'), ('画面名', '設備マスタ登録・編集'),
    ('プログラムID', 'PG-MST-EQP-002'), ('サブシステム', 'マスタ管理'),
])
lines02 += hdr(['画面レイアウト'], 3)
lines02.append('5セクション大フォーム。セクション間をアンカーリンクで移動可能。')
lines02.append('【基本情報】設備コード（自動採番）、設備名、設備種別（select）、設備ステータス')
lines02.append('【電気仕様】電圧階級、定格容量、定格電流、周波数')
lines02.append('【設置場所】親設備選択（text + ポップアップ）、設置年月、住所、経度緯度')
lines02.append('【保全区分】保全ランク、点検周期、直近点検日、次回点検予定日')
lines02.append('【備考】備考（textarea）、添付ファイル')
lines02.append('')
lines02 += hdr(['画面要素一覧'], 3)
lines02 += table(
    ['要素ID', '種別', '表示名', 'テスト難点タグ'],
    [
        ['eqp-name', 'text', '設備名', '必須'],
        ['eqp-type', 'select', '設備種別', '必須'],
        ['eqp-status', 'select', '設備ステータス（運用中/停止中/廃止）', '廃止→親設備不可'],
        ['eqp-parent', 'text', '親設備（読取専用）', 'ポップアップ選択結果反映（Stale Element注意）'],
        ['eqp-parent-btn', 'button', '選択（window.open）', 'ウィンドウハンドル切替、opener値反映'],
        ['eqp-rank', 'select', '保全ランク（S/A/B/C）', '必須'],
        ['eqp-interval', 'text', '点検周期（月）', '1～120'],
        ['eqp-file', 'file', '添付ファイル', 'ファイルサイズ/形式制限'],
        ['eqp-btn-save', 'submit', '登録／更新', 'POST→バリデーション→遷移'],
        ['eqp-btn-back', 'submit', '戻る', ''],
    ]
)
lines02 += hdr(['Struts1マッピング'], 3)
lines02 += kv_table([
    ('Action Path', '/mst/eqp/save'),
    ('Action Class', 'com.struts-lab.action.mst.EqpSaveAction (extends DispatchAction)'),
    ('Form Bean', 'com.struts-lab.form.mst.EqpForm (ValidatorForm)'),
    ('Forward (SUCCESS)', 'eqp.list'),
    ('Forward (INPUT)', 'eqp.edit'),
    ('Validator', 'eqp-form-validation'),
])
lines02 += hdr(['テスト難点'], 3)
lines02.append('- 親設備選択ポップアップ：ウィンドウハンドル切替 → 検索 → 選択 → opener値反映の全フロー')
lines02.append('- 5セクション大フォーム：アンカーリンク移動でフォーム値が維持されること')
lines02.append('- 条件付きバリデーション：廃止設備は親設備に選択不可')
lines02.append('- 添付ファイル＋同時バリデーション：ファイル選択状態でエラー時のファイル維持確認')
lines02.append('')

lines02 += divider()

# 点検項目マスタ一覧
lines02 += hdr(['点検項目マスタ一覧 (SCR-MST-CHK-001)'], 2)
lines02 += kv_table([
    ('画面ID', 'SCR-MST-CHK-001'), ('画面名', '点検項目マスタ一覧'),
    ('プログラムID', 'PG-MST-CHK-001'), ('サブシステム', 'マスタ管理'),
])
lines02 += hdr(['画面レイアウト'], 3)
lines02.append('【上部】検索：設備種別×点検種別（日常/定期/精密）。検索／クリア。')
lines02.append('【中部】一覧：テンプレート名、設備種別、点検種別、項目数、最終更新日。ソート可能。各行に「編集」「コピー」「削除」ボタン。')
lines02.append('【下部】「新規テンプレート」「並び順変更（▲/▼）」ボタン。')
lines02.append('')
lines02 += hdr(['テスト難点'], 3)
lines02.append('- 並び順変更：▲/▼ボタン押下後の要素index変動検証（stale element）')
lines02.append('- コピー→名称重複バリデーションの確認')
lines02.append('')

lines02 += divider()

# 点検項目マスタ登録・編集
lines02 += hdr(['点検項目マスタ登録・編集 (SCR-MST-CHK-002)'], 2)
lines02 += kv_table([
    ('画面ID', 'SCR-MST-CHK-002'), ('画面名', '点検項目マスタ登録・編集'),
    ('プログラムID', 'PG-MST-CHK-002'), ('サブシステム', 'マスタ管理'),
])
lines02 += hdr(['画面レイアウト'], 3)
lines02.append('【上部】テンプレート基本情報。【中部】ツリー編集：大分類→中分類→個別項目の3階層。各行に「追加」「削除」ボタン（Submit→再描画）。')
lines02.append('各項目に項目名、判定基準（○/×/△）、正常範囲、単位。')
lines02.append('【下部】「一括項目追加」「保存」「キャンセル」ボタン。')
lines02.append('')
lines02 += hdr(['テスト難点'], 3)
lines02.append('- 3階層ツリー動的行追加：大分類→中分類→項目の順序でindexが変動')
lines02.append('- 行削除後のindex再振り：削除→追加の繰り返しでindex連番が維持されるか')
lines02.append('- 一括項目追加の別画面遷移→戻る：追加データがフォームに保持されているか')
lines02.append('')

write_md('02_画面設計書_マスタ管理.md', lines02)

print("02 done, continuing...")

# For efficiency, let me write the remaining MD files in a more compact style
# 03_画面設計書_点検計画・実施.md

md03 = []
md03.append('# 03 画面設計書 — 点検計画・実施')
md03.append('')

screens_03 = [
    ('年間点検計画一覧', 'SCR-INS-PLAN-001', 'PG-INS-PLAN-001',
     '年度×設備×月のマトリクス表示。行=設備、列=月（4月～3月、12カラム）。セル内「計画:N回 実績:N回」。未達セル赤背景、超過セル黄背景。「CSV出力」「計画ロック/解除」ボタン。',
     ['動的カラム（12ヶ月固定）のヘッダ/データ対応検証', 'セル色のCSSクラス検証', '計画ロック後の編集不可検証']),
    ('点検計画登録（Wizard）', 'SCR-INS-PLAN-002', 'PG-INS-PLAN-002',
     '4ステップWizard：①対象設備選択 → ②点検項目テンプレート選択 → ③日程・担当者設定 → ④確認・登録。ステップ間データはhiddenで持ち回り。各ステップに「戻る」「進む」。確認画面に「一時保存」「確定」。',
     ['4ステップのデータ持ち回り（hidden→再POST）', '「戻る」→修正→「進む」ループの全パス網羅', 'ステップ3まで進んでステップ1不備発覚→戻る→修正→再確認', '一時保存→セッション切れ→再開不可']),
    ('点検実施一覧（当日）', 'SCR-INS-DAILY-001', 'PG-INS-DAILY-001',
     '対象日（デフォルト本日）、担当者選択。一覧：設備名、点検種別、予定時刻、ステータスバッジ（未了=灰/一部完了=黄/完了=緑）、担当者。各行クリック→実施入力画面へ直接遷移。',
     ['ステータスバッジの色分け検証（CSS class）', '一覧→実施入力直接遷移でパラメータ引継ぎ']),
    ('点検実施入力', 'SCR-INS-EXEC-001', 'PG-INS-EXEC-001',
     '3ブロック構成。①設備基本情報（表示のみ）。②チェックリスト：大分類→中分類→項目のネストテーブル。各項目に判定radio（○/×/△）、実測値、所見。判定×→実測値+所見必須、△→所見必須。写真添付（最大5枚/項目）。③総合判定、総合所見。総合判定「異常あり」→「異常報告へ」ボタン表示。',
     ['ネストテーブル：trの親子関係がDOM上フラット', '条件付きバリデーションの組み合わせ爆発（全項目×3状態=3^N通り）', '写真添付＋同時バリデーション', '「異常報告へ」ボタンの条件表示/非表示']),
    ('点検実施詳細・修正', 'SCR-INS-EXEC-002', 'PG-INS-EXEC-002',
     '実施済み点検結果の全ブロック表示（読取専用）。「修正申請」ボタン→修正理由（textarea, 必須）→承認申請。申請後は承認されるまで再修正不可。',
     ['修正申請→承認→再表示フローの全パス', '修正理由バリデーション（申請時のみ必須）', '承認済み後の修正不可検証']),
    ('点検実施承認一覧', 'SCR-INS-APPR-001', 'PG-INS-APPR-001',
     '検索（期間/担当班/ステータス）。一覧にチェックボックス。「一括承認」「一括差戻し」ボタン。差戻し時は理由（textarea）必須。操作後通知メッセージ表示。',
     ['一括承認：confirm OK→全件処理', '一括差戻し：confirm OK→理由必須→理由未入力でバリデーション', '操作後通知メッセージ表示', 'ページ跨ぎ選択不可']),
]

for name, sid, pid, layout, tests in screens_03:
    md03 += hdr([f'{name} ({sid})'], 2)
    md03 += kv_table([('画面ID', sid), ('画面名', name), ('プログラムID', pid), ('サブシステム', '点検計画・実施')])
    md03 += hdr(['画面レイアウト'], 3)
    md03.append(layout)
    md03.append('')
    md03 += hdr(['テスト難点'], 3)
    for t in tests:
        md03.append(f'- {t}')
    md03.append('')
    md03 += divider()

write_md('03_画面設計書_点検計画・実施.md', md03)

# 04_画面設計書_異常報告.md
md04 = []
md04.append('# 04 画面設計書 — 異常報告')
md04.append('')

sc04 = [
    ('異常報告一覧', 'SCR-INC-LIST-001', 'PG-INC-LIST-001',
     '7条件複合検索（発生日×設備種別×異常種別×ステータス×重大度×担当班×キーワード）。検索条件保存/呼出機能。一覧：報告番号、発生日時、設備名、異常種別、重大度（色分け）、ステータス。ページング。「一括ステータス更新」「CSV出力」「PDF出力」ボタン。',
     ['7条件複合検索の全組み合わせ検証困難', '検索条件保存→呼出の再現性', 'PDF出力の新規ウィンドウハンドル']),
    ('異常報告登録', 'SCR-INC-CREATE-001', 'PG-INC-CREATE-001',
     '3ブロック構成。①発生情報：点検実施からデータ引継ぎ。②異常内容：異常種別、重大度、異常部位、異常詳細、添付ファイル（最大3枚）。③暫定処置。「類似事例検索」ボタン→同画面下部に結果表示（DOM動的追加）。「登録」「一時保存」ボタン。',
     ['点検実施→異常報告のデータ引継ぎ（hidden/セッション）', '類似事例検索のDOM動的追加（同画面内）', '添付ファイル＋バリデーション同時処理']),
    ('異常報告詳細', 'SCR-INC-DETAIL-001', 'PG-INC-DETAIL-001',
     '3ブロック表示＋経過記録タイムライン。多段階ステータス遷移：未了→調査中（調査開始）→対応中（対応開始、推定原因必須）→完了（完了、対応内容必須）→クローズ/再発防止策登録。現在ステータスにより押せるボタンが変化。',
     ['多段階ステータス遷移（5段階）の全パス網羅', '各遷移時の条件付き必須項目（推定原因/対応内容）', 'ボタン表示/非表示の状態別切替', 'タイムライン時系列ソート検証']),
]

for name, sid, pid, layout, tests in sc04:
    md04 += hdr([f'{name} ({sid})'], 2)
    md04 += kv_table([('画面ID', sid), ('画面名', name), ('プログラムID', pid), ('サブシステム', '異常報告')])
    md04 += hdr(['画面レイアウト'], 3)
    md04.append(layout)
    md04.append('')
    md04 += hdr(['テスト難点'], 3)
    for t in tests:
        md04.append(f'- {t}')
    md04.append('')
    md04 += divider()

write_md('04_画面設計書_異常報告.md', md04)

# 05_画面設計書_対応指示・是正.md
md05 = []
md05.append('# 05 画面設計書 — 対応指示・是正')
md05.append('')

sc05 = [
    ('対応指示登録', 'SCR-CTR-CREATE-001', 'PG-CTR-CREATE-001',
     'ヘッダ＋N明細の親子構造。明細はIndexed Properties（details[0].workContent形式）。各行：作業内容、担当者（text + ポップアップ選択）、期限、優先度。「行追加」ボタン→Submit→再描画。「行削除」ボタン→index再振り。',
     ['動的行追加（Submit→再描画）：追加後既存行要素がstale', 'Indexed Propertiesのname属性深いネスト', '行削除後index再振り：削除→追加→削除の繰り返し', 'ポップアップ→親画面値反映：ウィンドウハンドル切替+opener操作', '複数明細＋バリデーションエラー→全明細入力値維持']),
    ('対応指示一覧', 'SCR-CTR-LIST-001', 'PG-CTR-LIST-001',
     '複合検索（指示日範囲×担当者×ステータス×優先度）。一覧：指示番号、指示日、関連異常報告番号、明細数、完了/全明細数、全体ステータス。ページング。「一括ステータス更新」「印刷用表示（window.open）」「CSV出力」ボタン。',
     ['印刷用別ウィンドウのハンドル切替と内容検証', '完了数/全明細数の表示更新']),
    ('対応指示詳細・完了報告', 'SCR-CTR-DETAIL-001', 'PG-CTR-DETAIL-001',
     '指示ヘッダ情報（表示）＋明細一覧。各行に「完了報告」ボタン→実績作業時間、使用部品、使用量、所見の入力行が展開。全明細完了→ヘッダ自動「完了」。使用部品選択→在庫超えアラート。',
     ['明細個別完了報告→DOM展開', '全明細完了→ヘッダ自動完了トリガー検出', '使用部品選択→在庫超えアラートの条件付き表示']),
    ('是正処置報告書', 'SCR-CTR-CAPA-001', 'PG-CTR-CAPA-001',
     '異常報告からの引継ぎ情報（表示）。なぜなぜ分析5段階（各textarea）。再発防止策、効果確認方法、効果確認期限。「承認申請」ボタン。',
     ['なぜなぜ分析5段階：各段の入力値検証', '全textarea必須バリデーション', '異常報告→是正処置のデータ引継ぎ']),
]

for name, sid, pid, layout, tests in sc05:
    md05 += hdr([f'{name} ({sid})'], 2)
    md05 += kv_table([('画面ID', sid), ('画面名', name), ('プログラムID', pid), ('サブシステム', '対応指示・是正')])
    md05 += hdr(['画面レイアウト'], 3)
    md05.append(layout)
    md05.append('')
    md05 += hdr(['テスト難点'], 3)
    for t in tests:
        md05.append(f'- {t}')
    md05.append('')
    md05 += divider()

write_md('05_画面設計書_対応指示・是正.md', md05)

# 06_画面設計書_組織・カレンダー・保守部品.md
md06 = []
md06.append('# 06 画面設計書 — 組織・カレンダー・保守部品')
md06.append('')

sc06 = [
    ('部署マスタ一覧', 'SCR-ORG-DEPT-001',
     '4階層ツリー表示（本社→支社→営業所→出張所）。table形式、インデントで階層表現。各行に「編集」「子部署追加」。統廃合履歴表示。',
     ['4階層ツリーのインデント検証', '階層別の追加/編集画面遷移']),
    ('部署マスタ登録・編集', 'SCR-ORG-DEPT-002',
     '上位部署選択（ツリーポップアップ→値反映）。有効期間設定（開始日/終了日）。過去日異動時警告。部署種別（select: 本社/支社/営業所/出張所）。',
     ['過去日に遡った部署異動の警告バリデーション', 'ツリーポップアップからの選択値反映']),
    ('担当者マスタ一覧', 'SCR-ORG-EMP-001',
     '複合検索（部署×職位×技能資格（複数checkbox）×入社年）。一覧：社員番号、氏名、部署、職位、保有資格、認定有効期限。期限切れ行は赤背景。一括ロック/解除。',
     ['複数チェックボックス資格選択のAND/OR検索', '有効期限切れCSSクラス検証', '一括ロック/解除のconfirm']),
    ('担当者マスタ登録・編集', 'SCR-ORG-EMP-002',
     '5セクション大フォーム：①基本情報 ②所属・職位 ③保有資格・技能（資格ごとに認定日・有効期限） ④点検員認定 ⑤アカウント情報。パスワード確認一致チェック。ログインID重複チェック。',
     ['5セクション大フォーム一括バリデーション', '資格ごと有効期限バリデーション（n個の条件付き必須）', 'パスワード確認一致', 'ログインID重複']),
    ('休日カレンダー一覧', 'SCR-CAL-LIST-001',
     '年度選択→月別カレンダー表示（table形式、7列×5-6行）。休日種別で色分け（法定=赤、会社指定=青、点検停止日=黄）。日付クリック→登録/編集へ。',
     ['月別カレンダーのセル色検証', '年度切替のデータ範囲']),
    ('休日登録・編集', 'SCR-CAL-REG-001',
     '日付範囲指定（from/to）→一括登録。重複チェック。振替出勤日設定。休日種別選択（法定/会社指定/点検停止日）。',
     ['範囲指定一括登録の重複チェック', '計画停止期間と通常休日の重複']),
    ('保守部品一覧', 'SCR-PRT-LIST-001',
     '複合検索（設備種別×部品種別×在庫ステータス×キーワード）。在庫バッジ（充足=緑/僅少=黄/在庫切れ=赤）。',
     ['在庫バッジ色分け（CSSクラス）', '在庫僅少/切れ境界値']),
    ('保守部品登録・編集', 'SCR-PRT-REG-001',
     '適用設備選択（チェックボックスツリー：設備種別→設備名の2階層、複数選択可）。発注点、安全在庫数（発注点＞安全在庫数バリデーション）。添付ファイル（仕様書PDF）。',
     ['チェックボックスツリー階層表示', '発注点＞安全在庫数バリデーション']),
    ('部品使用実績一覧', 'SCR-PRT-USAGE-001',
     '期間×設備種別×部品で検索。一覧：使用日、部品名、設備名、使用量、使用前在庫、使用後在庫、使用目的。在庫が発注点下回り→警告表示。',
     ['在庫自動引当表示（使用後在庫≦発注点で警告）']),
]

for name, sid, layout, tests in sc06:
    md06 += hdr([f'{name} ({sid})'], 2)
    md06 += kv_table([('画面ID', sid), ('画面名', name), ('サブシステム', '組織・カレンダー・保守部品')])
    md06 += hdr(['画面レイアウト'], 3)
    md06.append(layout)
    md06.append('')
    md06 += hdr(['テスト難点'], 3)
    for t in tests:
        md06.append(f'- {t}')
    md06.append('')
    md06 += divider()

write_md('06_画面設計書_組織・カレンダー・保守部品.md', md06)

# 07_画面設計書_レポート.md
md07 = []
md07.append('# 07 画面設計書 — レポート')
md07.append('')
md07 += hdr(['総合レポート (SCR-RPT-SUMMARY-001)'], 2)
md07 += kv_table([('画面ID', 'SCR-RPT-SUMMARY-001'), ('画面名', '総合レポート'), ('プログラムID', 'PG-RPT-SUMMARY-001')])
md07 += hdr(['画面レイアウト'], 3)
md07.append('【上部】期間指定（from/to, YYYYMM）、設備種別、担当班。「表示」ボタン。')
md07.append('【中部①】点検実施率推移：table形式月別グラフ（動的カラム）。目標値(95%)未満は赤背景。')
md07.append('【中部②】異常発生傾向：月別×異常種別クロス集計。')
md07.append('【中部③】設備別異常件数ランキング（上位10件）。')
md07.append('【下部】「CSV出力」「印刷用表示（window.open）」')
md07.append('')
md07 += hdr(['テスト難点'], 3)
md07.append('- 動的カラム（期間により月数可変）のヘッダ/データ対応検証')
md07.append('- 実施率の色分け検証（CSS）')
md07.append('- クロス集計の数値正確性')
md07.append('- 印刷用別ウィンドウの内容検証')
md07.append('')
write_md('07_画面設計書_レポート.md', md07)

print("02-07 done")

# ===== 12_画面遷移設計書.md =====
md12 = []
md12.append('# 12 画面遷移設計書')
md12.append('')
md12 += kv_table([
    ('システムID', 'STRUTSLAB-001'), ('システム名', '電力設備巡視点検管理システム'),
    ('文書番号', 'DOC-TRANS-001'), ('版数', '1.0'), ('作成日', '2026/05/23'),
])

md12 += hdr(['画面遷移一覧（全66経路）'], 2)
md12 += table(
    ['モジュール', '遷移元', '操作', '遷移先', '種別', 'テスト経路ID'],
    [
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
        ['マスタ','設備マスタ一覧','設備コードクリック','設備マスタ編集','画面遷移','T102'],
        ['マスタ','設備マスタ一覧','検索','同一画面(検索結果)','同一画面','T103'],
        ['マスタ','設備マスタ一覧','選択削除→confirmOK','同一画面(削除後)','同一画面','T104'],
        ['マスタ','設備マスタ登録/編集','親設備選択','親設備ポップアップ','ポップアップ','T105'],
        ['マスタ','設備マスタ登録/編集','保存→成功','設備マスタ一覧','画面遷移','T106'],
        ['マスタ','設備マスタ登録/編集','保存→失敗','同一画面(エラー)','同一画面','T107'],
        ['マスタ','点検項目マスタ一覧','コピー','同一画面(コピー後)','同一画面','T110'],
        ['マスタ','点検項目マスタ一覧','並び順変更▲/▼','同一画面(並替後)','同一画面','T111'],
        ['点検','点検計画登録Wizard','Step1→進む','Step2','同一画面','T203'],
        ['点検','点検計画登録Wizard','Step2→戻る','Step1','同一画面','T204'],
        ['点検','点検計画登録Wizard','Step2→進む','Step3','同一画面','T205'],
        ['点検','点検計画登録Wizard','Step3→戻る','Step2','同一画面','T206'],
        ['点検','点検計画登録Wizard','Step3→進む','Step4(確認)','同一画面','T207'],
        ['点検','点検計画登録Wizard','Step4→戻る','Step3','同一画面','T208'],
        ['点検','点検計画登録Wizard','Step4→確定','年間点検計画一覧','画面遷移','T209'],
        ['点検','点検計画登録Wizard','Step4→一時保存','Step4(保存完了)','同一画面','T210'],
        ['点検','点検実施一覧(当日)','行クリック','点検実施入力','画面遷移','T211'],
        ['点検','点検実施入力','異常報告へ(異常あり)','異常報告登録','画面遷移','T213'],
        ['点検','点検実施詳細','修正申請','同一画面(申請中)','同一画面','T214'],
        ['点検','点検実施承認一覧','一括承認→confirmOK','同一画面(通知)','同一画面','T215'],
        ['点検','点検実施承認一覧','一括差戻→理由入力','同一画面(通知)','同一画面','T216'],
        ['異常','異常報告一覧','検索条件保存','同一画面','同一画面','T302'],
        ['異常','異常報告一覧','一括ステータス更新','同一画面(更新後)','同一画面','T303'],
        ['異常','異常報告登録','類似事例検索','同一画面(結果表示)','同一画面','T304'],
        ['異常','異常報告詳細','調査開始(未了→調査中)','同一画面(調査中)','同一画面','T306'],
        ['異常','異常報告詳細','対応開始(調査中→対応中)','同一画面(対応中)','同一画面','T307'],
        ['異常','異常報告詳細','完了(対応中→完了)','同一画面(完了)','同一画面','T308'],
        ['異常','異常報告詳細','クローズ','異常報告一覧','画面遷移','T309'],
        ['異常','異常報告詳細','再発防止策登録','是正処置報告書','画面遷移','T310'],
        ['対応','対応指示登録','行追加','同一画面(行追加後)','同一画面','T401'],
        ['対応','対応指示登録','行削除','同一画面(行削除後)','同一画面','T402'],
        ['対応','対応指示登録','担当者選択→popup→反映','同一画面','同一画面','T403'],
        ['対応','対応指示登録','登録→成功','対応指示一覧','画面遷移','T404'],
        ['対応','対応指示一覧','印刷用表示','印刷用別ウィンドウ','新規ウィンドウ','T405'],
        ['対応','対応指示詳細','完了報告(明細)','同一画面(展開)','同一画面','T407'],
        ['対応','対応指示詳細','保存→全明細完了','同一画面(自動完了)','同一画面','T408'],
        ['対応','是正処置報告書','承認申請','異常報告詳細','画面遷移','T409'],
        ['組織','部署マスタ一覧','部署クリック','部署マスタ編集','画面遷移','T501'],
        ['組織','部署マスタ登録/編集','上位部署選択→popup','同一画面','同一画面','T503'],
        ['組織','担当者マスタ一覧','一括ロック/解除','同一画面(更新後)','同一画面','T504'],
        ['カレンダー','休日カレンダー一覧','日付クリック','休日登録/編集','画面遷移','T601'],
        ['カレンダー','休日登録/編集','範囲指定一括登録','休日カレンダー一覧','画面遷移','T602'],
        ['保守','保守部品一覧','新規/編集','保守部品登録/編集','画面遷移','T701'],
        ['レポート','総合レポート','期間指定→表示','同一画面(集計表示)','同一画面','T801'],
        ['レポート','総合レポート','印刷用表示','印刷用別ウィンドウ','新規ウィンドウ','T802'],
    ]
)

write_md('12_画面遷移設計書.md', md12)

# ===== 13_DB設計書.md =====
md13 = []
md13.append('# 13 データベース設計書')
md13.append('')
md13 += kv_table([
    ('システムID', 'STRUTSLAB-001'), ('DBMS', 'H2 Database 1.4.200（ファイルモード）'),
    ('文書番号', 'DOC-DB-001'), ('版数', '1.0'), ('文字コード', 'UTF-8'),
])

tables_md = {
    'equipment (設備マスタ)': [
        ['equipment_code','VARCHAR(10)','PK','設備コード（TR-0001）'],
        ['equipment_name','VARCHAR(100)','NOT NULL','設備名'],
        ['equipment_type','VARCHAR(10)','NOT NULL','設備種別'],
        ['voltage_level','VARCHAR(10)','','電圧階級'],
        ['parent_equipment_code','VARCHAR(10)','FK→equipment','親設備（自己参照）'],
        ['maintenance_rank','CHAR(1)','NOT NULL','保全ランク（S/A/B/C）'],
        ['status','VARCHAR(5)','NOT NULL','運用中/停止中/廃止'],
    ],
    'inspection_template (点検項目テンプレート)': [
        ['template_id','INT','PK AUTO','テンプレートID'],
        ['template_name','VARCHAR(100)','NOT NULL','テンプレート名'],
        ['equipment_type','VARCHAR(10)','NOT NULL','対象設備種別'],
        ['inspection_kind','VARCHAR(5)','NOT NULL','日常/定期/精密'],
    ],
    'inspection_items (点検項目・3階層)': [
        ['item_id','INT','PK AUTO','項目ID'],
        ['template_id','INT','FK→inspection_template','所属テンプレート'],
        ['parent_item_id','INT','FK→inspection_items','親項目（自己参照）'],
        ['item_level','INT','NOT NULL','1=大分類/2=中分類/3=個別'],
        ['item_name','VARCHAR(200)','NOT NULL','項目名'],
        ['judge_criteria','VARCHAR(5)','','判定基準'],
    ],
    'inspection_plans (点検計画)': [
        ['plan_id','INT','PK AUTO','計画ID'],
        ['fiscal_year','VARCHAR(4)','NOT NULL','年度'],
        ['equipment_code','VARCHAR(10)','FK→equipment','対象設備'],
        ['template_id','INT','FK→inspection_template','テンプレート'],
        ['planned_date','VARCHAR(8)','NOT NULL','点検予定日'],
        ['team_code','VARCHAR(10)','','担当班'],
        ['status','VARCHAR(10)','NOT NULL','予定/実施済/中止/延期'],
        ['is_locked','BOOLEAN','NOT NULL','計画ロック'],
    ],
    'inspection_results (点検実施結果ヘッダ)': [
        ['result_id','INT','PK AUTO','結果ID'],
        ['plan_id','INT','FK→inspection_plans','計画ID'],
        ['executed_date','VARCHAR(8)','NOT NULL','実施日'],
        ['executed_by','VARCHAR(10)','FK→employees','実施者'],
        ['summary_judge','VARCHAR(5)','','NORMAL/ABNORMAL/WATCH'],
        ['approval_status','VARCHAR(5)','','申請中/承認済/差戻'],
    ],
    'inspection_items_results (点検項目結果明細)': [
        ['result_item_id','INT','PK AUTO',''],
        ['result_id','INT','FK→inspection_results',''],
        ['item_id','INT','FK→inspection_items','点検項目ID'],
        ['judge','CHAR(1)','NOT NULL','○/×/△'],
        ['measured_value','VARCHAR(50)','','実測値'],
        ['note','TEXT','','所見'],
    ],
    'incidents (異常報告)': [
        ['incident_no','VARCHAR(20)','PK','INC-YYYYMMDD-NNN'],
        ['result_id','INT','FK→inspection_results','関連点検結果'],
        ['equipment_code','VARCHAR(10)','FK→equipment','対象設備'],
        ['incident_type','VARCHAR(10)','NOT NULL','異常種別'],
        ['severity','VARCHAR(5)','NOT NULL','軽微/中/重大/緊急'],
        ['status','VARCHAR(10)','NOT NULL','未了/調査中/対応中/完了/再発防止/クローズ'],
        ['cause','TEXT','','推定原因'],
        ['counter_detail','TEXT','','対応内容'],
    ],
    'incident_timeline (異常報告経過記録)': [
        ['timeline_id','INT','PK AUTO',''],
        ['incident_no','VARCHAR(20)','FK→incidents',''],
        ['action_datetime','TIMESTAMP','NOT NULL','処理日時'],
        ['action_content','TEXT','NOT NULL','処理内容'],
        ['status_from','VARCHAR(10)','','遷移元'],
        ['status_to','VARCHAR(10)','','遷移先'],
    ],
    'counter_orders (対応指示ヘッダ)': [
        ['order_no','VARCHAR(20)','PK','CTR-YYYYMMDD-NNN'],
        ['incident_no','VARCHAR(20)','FK→incidents','関連異常報告'],
        ['order_date','VARCHAR(8)','NOT NULL','指示日'],
        ['issuer','VARCHAR(50)','NOT NULL','指示者'],
        ['status','VARCHAR(5)','NOT NULL','未了/処理中/完了'],
    ],
    'counter_order_details (対応指示明細)': [
        ['detail_id','INT','PK AUTO',''],
        ['order_no','VARCHAR(20)','FK→counter_orders',''],
        ['seq_no','INT','NOT NULL','行番号'],
        ['work_content','VARCHAR(500)','NOT NULL','作業内容'],
        ['person_code','VARCHAR(10)','FK→employees','担当者'],
        ['status','VARCHAR(3)','NOT NULL','未了/完了'],
        ['actual_hours','DECIMAL(5,1)','','実績作業時間'],
        ['used_quantity','INT','','使用量'],
    ],
    'capa_reports (是正処置報告書)': [
        ['capa_id','INT','PK AUTO',''],
        ['incident_no','VARCHAR(20)','FK→incidents',''],
        ['why1~5','TEXT','NOT NULL','なぜなぜ分析'],
        ['countermeasure','TEXT','NOT NULL','再発防止策'],
        ['verify_deadline','VARCHAR(8)','NOT NULL','効果確認期限'],
    ],
    'departments (部署マスタ)': [
        ['dept_code','VARCHAR(10)','PK','部署コード'],
        ['dept_name','VARCHAR(100)','NOT NULL','部署名'],
        ['parent_dept_code','VARCHAR(10)','FK→departments','上位部署（自己参照）'],
        ['dept_level','INT','NOT NULL','階層（1-4）'],
        ['dept_type','VARCHAR(5)','NOT NULL','本社/支社/営業所/出張所'],
    ],
    'employees (担当者マスタ)': [
        ['emp_no','VARCHAR(10)','PK','EMP-NNNN'],
        ['name','VARCHAR(100)','NOT NULL','氏名'],
        ['dept_code','VARCHAR(10)','FK→departments','所属部署'],
        ['login_id','VARCHAR(50)','NOT NULL UNIQUE','ログインID'],
        ['password_hash','VARCHAR(64)','NOT NULL','SHA-256'],
        ['inspection_rank','CHAR(1)','','点検員ランク'],
        ['inspection_cert_expire','VARCHAR(8)','','認定有効期限'],
    ],
    'employee_qualifications (担当者保有資格)': [
        ['id','INT','PK AUTO',''],
        ['emp_no','VARCHAR(10)','FK→employees',''],
        ['qualification_code','VARCHAR(10)','NOT NULL','資格コード'],
        ['expire_date','VARCHAR(8)','','有効期限'],
    ],
    'holidays (休日マスタ)': [
        ['holiday_id','INT','PK AUTO',''],
        ['holiday_date','VARCHAR(8)','NOT NULL UNIQUE','日付'],
        ['holiday_type','VARCHAR(5)','NOT NULL','法定/会社指定/点検停止'],
        ['is_transfer','BOOLEAN','NOT NULL','振替設定有無'],
        ['transfer_date','VARCHAR(8)','','振替出勤日'],
    ],
    'parts (保守部品マスタ)': [
        ['part_code','VARCHAR(10)','PK','部品コード'],
        ['part_name','VARCHAR(100)','NOT NULL','部品名'],
        ['order_point','INT','','発注点'],
        ['safety_stock','INT','','安全在庫数'],
        ['current_stock','INT','NOT NULL','現在庫数'],
    ],
    'part_usages (部品使用実績)': [
        ['usage_id','INT','PK AUTO',''],
        ['part_code','VARCHAR(10)','FK→parts',''],
        ['equipment_code','VARCHAR(10)','FK→equipment','使用設備'],
        ['quantity','INT','NOT NULL','使用量'],
        ['stock_before','INT','NOT NULL','使用前在庫'],
        ['stock_after','INT','NOT NULL','使用後在庫'],
        ['purpose','VARCHAR(3)','NOT NULL','点検/修繕'],
    ],
}

for tname, cols in tables_md.items():
    md13 += hdr([tname], 3)
    md13 += table(['カラム名', '型', '制約', '説明'], cols)

md13 += hdr(['主要リレーション'], 2)
rels_md = [
    'equipment.parent_equipment_code → equipment.equipment_code（自己参照）',
    'inspection_items.template_id → inspection_template.template_id',
    'inspection_items.parent_item_id → inspection_items.item_id（自己参照: 階層）',
    'inspection_plans.equipment_code → equipment.equipment_code',
    'inspection_results.plan_id → inspection_plans.plan_id',
    'inspection_items_results.result_id → inspection_results.result_id',
    'incidents.result_id → inspection_results.result_id',
    'incidents.equipment_code → equipment.equipment_code',
    'incident_timeline.incident_no → incidents.incident_no',
    'counter_orders.incident_no → incidents.incident_no',
    'counter_order_details.order_no → counter_orders.order_no',
    'counter_order_details.person_code → employees.emp_no',
    'capa_reports.incident_no → incidents.incident_no',
    'departments.parent_dept_code → departments.dept_code（自己参照）',
    'employees.dept_code → departments.dept_code',
    'part_usages.part_code → parts.part_code',
    'part_usages.order_no → counter_orders.order_no',
]
for r in rels_md:
    md13.append(f'- {r}')
md13.append('')

write_md('13_DB設計書.md', md13)

# ===== 14_共通部品設計書.md =====
md14 = []
md14.append('# 14 共通部品設計書')
md14.append('')
md14 += kv_table([('システムID', 'STRUTSLAB-001'), ('文書番号', 'DOC-COMMON-001'), ('版数', '1.0')])

md14 += hdr(['Tilesレイアウト定義'], 2)
md14 += table(
    ['テンプレート名', '継承', '属性', '説明'],
    [
        ['baseLayout', '(root)', 'header, menu, body, footer', '全画面共通ベース'],
        ['masterLayout', 'baseLayout', 'body=menuLayout(menu,content)', 'マスタ管理用'],
        ['wizardLayout', 'baseLayout', 'body=wizardContent', 'Wizard画面用'],
        ['listLayout', 'baseLayout', 'body=searchArea+listArea', '一覧画面用'],
        ['inputLayout', 'baseLayout', 'body=formArea', '入力画面用'],
        ['popupLayout', '(root)', 'header, body', 'ポップアップ用'],
        ['printLayout', '(root)', 'body', '印刷用（装飾なし）'],
    ]
)

md14 += hdr(['共通JSPインクルード'], 2)
md14 += table(
    ['ファイル名', 'パス', '役割'],
    [
        ['header.jsp', '/WEB-INF/jsp/common/header.jsp', 'タイトルバー、ログインユーザー表示'],
        ['menu.jsp', '/WEB-INF/jsp/common/menu.jsp', 'モジュール別メニューリンク'],
        ['footer.jsp', '/WEB-INF/jsp/common/footer.jsp', 'コピーライト'],
        ['paging.jsp', '/WEB-INF/jsp/common/paging.jsp', 'ページング共通部品'],
        ['errorMessages.jsp', '/WEB-INF/jsp/common/errorMessages.jsp', '<html:errors/>表示'],
        ['confirmDialog.jsp', '/WEB-INF/jsp/common/confirmDialog.jsp', 'window.confirm用JS'],
    ]
)

md14 += hdr(['カスタムタグ一覧'], 2)
md14 += table(
    ['タグ名', 'TLD', 'Javaクラス', '機能'],
    [
        ['eqp:treeSelect', 'eqp-tree.tld', 'EqpTreeSelectTag', '設備ツリー選択（select）'],
        ['date:picker', 'date-picker.tld', 'DatePickerTag', '日付入力補助（text+カレンダーpopup）'],
        ['app:sectionHeader', 'app-common.tld', 'SectionHeaderTag', 'セクション見出し（アンカー付き）'],
        ['app:statusBadge', 'app-common.tld', 'StatusBadgeTag', 'ステータスバッジ（色付きspan）'],
        ['app:inspectionChecklist', 'app-common.tld', 'InspectionChecklistTag', '点検チェックリスト3階層テーブル'],
        ['app:indexedRow', 'app-common.tld', 'IndexedRowTag', '動的行（Indexed Properties反復）'],
        ['app:timeline', 'app-common.tld', 'TimelineTag', '経過記録タイムライン表示'],
    ]
)

md14 += hdr(['MyBatis Mapper一覧'], 2)
md14 += table(
    ['Mapper XML', 'Javaインタフェース', '担当テーブル'],
    [
        ['EquipmentMapper.xml', 'EquipmentDao.java', 'equipment'],
        ['InspectionTemplateMapper.xml', 'InspectionTemplateDao.java', 'inspection_template, inspection_items'],
        ['InspectionPlanMapper.xml', 'InspectionPlanDao.java', 'inspection_plans'],
        ['InspectionResultMapper.xml', 'InspectionResultDao.java', 'inspection_results, inspection_items_results'],
        ['IncidentMapper.xml', 'IncidentDao.java', 'incidents, incident_timeline, incident_attachments'],
        ['CounterOrderMapper.xml', 'CounterOrderDao.java', 'counter_orders, counter_order_details'],
        ['CapaMapper.xml', 'CapaDao.java', 'capa_reports'],
        ['DeptMapper.xml', 'DeptDao.java', 'departments'],
        ['EmployeeMapper.xml', 'EmployeeDao.java', 'employees, employee_qualifications'],
        ['CalendarMapper.xml', 'CalendarDao.java', 'holidays'],
        ['PartsMapper.xml', 'PartsDao.java', 'parts, part_equipment_relations, part_usages'],
    ]
)

write_md('14_共通部品設計書.md', md14)

# ===== 15_エラーコード一覧.md =====
md15 = []
md15.append('# 15 エラーコード一覧')
md15.append('')
md15 += kv_table([('システムID', 'STRUTSLAB-001'), ('文書番号', 'DOC-ERR-001'), ('版数', '1.0')])

md15 += hdr(['業務エラー'], 2)
md15 += table(
    ['コード', '区分', 'メッセージ（ja）', '発生条件'],
    [
        ['ERR-BIZ-001','重複','同じ設備コードが既に存在します。','設備コード重複'],
        ['ERR-BIZ-002','重複','同じログインIDが既に使用されています。','ログインID重複'],
        ['ERR-BIZ-003','重複','指定範囲に既存の休日が含まれています。','休日重複'],
        ['ERR-BIZ-006','状態不正','廃止済み設備は親設備に指定できません。','廃止設備を親に指定'],
        ['ERR-BIZ-007','状態不正','承認済みの点検結果は修正できません。','承認済みデータ修正試行'],
        ['ERR-BIZ-008','状態不正','計画がロックされています。','ロック済み計画編集'],
        ['ERR-BIZ-009','在庫不足','使用量が現在庫を超えています。','在庫超え使用'],
        ['ERR-BIZ-010','期限切れ','認定有効期限が切れています。','期限切れ資格'],
        ['ERR-BIZ-011','データ不存在','保存された検索条件が見つかりません。','セッション切れ'],
        ['ERR-BIZ-012','操作不正','全明細完了していないため完了できません。','未了明細あり'],
        ['ERR-BIZ-013','操作不正','差戻し理由が入力されていません。','理由未入力'],
        ['ERR-BIZ-014','過去日','過去日は指定できません。','過去日指定'],
    ]
)

md15 += hdr(['バリデーションエラー'], 2)
md15 += table(
    ['コード', '区分', 'メッセージ（ja）', '発生条件'],
    [
        ['ERR-VAL-001','必須','{0}は必須です。','required'],
        ['ERR-VAL-002','最大長','{0}は{1}文字以内で入力してください。','maxlength'],
        ['ERR-VAL-003','数値範囲','{0}は{1}～{2}の範囲で入力してください。','intRange'],
        ['ERR-VAL-004','形式','{0}は{1}の形式で入力してください。','date'],
        ['ERR-VAL-005','形式','フリガナはカタカナで入力してください。','mask'],
        ['ERR-VAL-006','一致','パスワードが一致しません。','confirm'],
        ['ERR-VAL-007','最小長','パスワードは8文字以上で入力してください。','minlength'],
        ['ERR-VAL-008','条件付き必須','判定×の場合、実測値は必須です。','conditional'],
        ['ERR-VAL-009','条件付き必須','判定×/△の場合、所見は必須です。','conditional'],
        ['ERR-VAL-010','条件付き必須','推定原因は必須です。','conditional'],
        ['ERR-VAL-011','条件付き必須','対応内容は必須です。','conditional'],
        ['ERR-VAL-014','ファイル','ファイルサイズは{0}MB以下にしてください。','file size'],
        ['ERR-VAL-015','ファイル','ファイル形式は{0}のみ許可されています。','file ext'],
        ['ERR-VAL-018','行数','明細は1行以上必要です。','min rows'],
        ['ERR-VAL-019','行数','明細は最大50行までです。','max rows'],
    ]
)

md15 += hdr(['システムエラー'], 2)
md15 += table(
    ['コード', '区分', 'メッセージ（ja）', 'HTTP'],
    [
        ['ERR-SYS-001','DB接続','システムエラーが発生しました。管理者に連絡してください。','500'],
        ['ERR-SYS-002','ファイルIO','ファイルの読み書きに失敗しました。','500'],
        ['ERR-SYS-003','認証','ログインに失敗しました。IDとパスワードを確認してください。','200'],
        ['ERR-SYS-004','セッション','セッションが切れました。再度ログインしてください。','200'],
        ['ERR-SYS-005','権限','この操作の権限がありません。','200'],
        ['ERR-SYS-006','404','ページが見つかりません。','404'],
        ['ERR-SYS-007','500','予期せぬエラーが発生しました。','500'],
    ]
)

write_md('15_エラーコード一覧.md', md15)

# ===== 16_UIテスト難点マップ.md =====
md16 = []
md16.append('# 16 UIテスト難点マップ')
md16.append('')
md16 += kv_table([('システムID', 'STRUTSLAB-001'), ('文書番号', 'DOC-TEST-001'), ('版数', '1.0')])

md16 += hdr(['テスト難点カテゴリ'], 2)
md16 += table(
    ['ID', 'カテゴリ', '説明', 'Selenium困難性'],
    [
        ['C01','Stale Element','動的行追加/削除（Submit→再描画）で既存要素参照が無効化','高'],
        ['C02','Indexed Properties','name属性がdetails[{n}].field形式','高'],
        ['C03','Wizard','マルチステップのデータ持ち回りと「戻る→修正→進む」','高'],
        ['C04','条件付きバリデーション','状態により必須項目が変化','高'],
        ['C05','Popup→親画面反映','window.open→選択→opener値反映','中'],
        ['C06','File Upload','添付ファイル＋同時バリデーション','中'],
        ['C07','一括操作+confirm','window.confirm OK/キャンセル両方テスト','低'],
        ['C08','動的カラム','期間によりカラム数/内容変動','中'],
        ['C09','ネストテーブル','大分類rowspan→中分類→項目の階層テーブル','中'],
        ['C10','印刷用別ウィンドウ','window.open→別ウィンドウ内容検証','中'],
        ['C11','CSS色分け','ステータスバッジ/セル色クラス検証','低'],
        ['C12','ページング複合','ページ跨ぎ操作＋全選択＋状態変動','中'],
        ['C13','CSV出力','ダウンロードファイル内容検証','低'],
        ['C14','セッション切れ','セッションタイムアウト後挙動','中'],
        ['C15','多段階ステータス遷移','未了→調査中→対応中→完了→再発防止','高'],
    ]
)

md16 += hdr(['画面×テスト難点マトリクス（◎=高度難点, ○=難点あり）'], 2)
md16 += table(
    ['No.', '画面名', 'C01', 'C02', 'C03', 'C04', 'C05', 'C06', 'C07', 'C08', 'C09', 'C10', 'C11', 'C12', 'C13', 'C14', 'C15'],
    [
        ['1','設備マスタ一覧','','','','','','','○','','','','○','○','○','',''],
        ['2','設備マスタ登録/編集','','','','○','○','○','','','','','','','','',''],
        ['3','点検項目マスタ一覧','○','','','','','','○','','','','○','','','',''],
        ['4','点検項目マスタ登録/編集','○','○','','','','','','','○','','','','','',''],
        ['5','年間点検計画一覧','','','','','','','','○','','','○','','','',''],
        ['6','点検計画登録(Wizard)','','','◎','○','','','','','','','','','','○',''],
        ['7','点検実施一覧(当日)','','','','','','','','','','','○','','','',''],
        ['8','点検実施入力','','','','◎','','○','','','○','','','','','',''],
        ['9','点検実施詳細/修正','','','','○','','','○','','','','','','','',''],
        ['10','点検実施承認一覧','','','','○','','','○','','','','','○','','',''],
        ['11','異常報告一覧','','','','','','','○','','','','','○','○','○',''],
        ['12','異常報告登録','','','','','','○','','','','','','','','',''],
        ['13','異常報告詳細','','','','◎','','','','','','','','','','','◎'],
        ['14','対応指示登録','◎','◎','','','○','','','','','','','','','',''],
        ['15','対応指示一覧','','','','','','','○','','','','○','','○','○','',''],
        ['16','対応指示詳細/完了','○','○','','○','','','','','','','','','','',''],
        ['17','是正処置報告書','','','','','','','','','','','','','','',''],
        ['18','総合レポート','','','','','','','','','','','○','','','',''],
        ['20','部署マスタ登録/編集','','','','','○','','','','','','','','','',''],
        ['22','担当者マスタ登録/編集','','','','','','','','','','','','','','',''],
        ['23','休日カレンダー一覧','','','','','','','','○','','','○','','','',''],
        ['26','保守部品登録/編集','','','','','','○','','','','','','','','',''],
    ]
)

md16.append('')
md16.append('**凡例**: ◎ = 特に高度なテスト難点（組み合わせ爆発・複合要因）、○ = 難点あり')
md16.append('')

write_md('16_UIテスト難点マップ.md', md16)

print("All MD files generated!")
