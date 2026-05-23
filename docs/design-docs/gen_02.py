#!/usr/bin/env python3
"""生成 02_画面設計書_マスタ管理.xlsx"""
import sys; sys.path.insert(0, '.')
from _xlsx_gen import *

def screen_design_sheet(screen_id, screen_name, pg_id, layout_desc, elements, table_cols=None, struts_map=None, related_pgs=None, test_notes=None):
    """Build a screen design sheet."""
    d = []
    d.append(hdr_row(['画面設計書', '', '', '', '', '', '', '']))
    d.append(hdr_row(['文書管理情報', '', '', '', '', '', '', '']))
    info = [
        ['システムID', 'STRUTSLAB-001', '', '', 'サブシステム名', 'マスタ管理', '', ''],
        ['画面ID', screen_id, '', '', '画面名', screen_name, '', ''],
        ['プログラムID', pg_id, '', '', '文書番号', f'DOC-SCR-{screen_id}', '', ''],
        ['版数', '1.0', '', '', '作成日', '2026/05/23', '', ''],
        ['作成者', 'システム開発部', '', '', '区分', '新規', '', ''],
        ['アクセス権限', '全ユーザー（保守管理者）', '', '', '', '', '', ''],
    ]
    d.extend(info)
    d.append(empty_row())
    d.append(hdr_row(['画面レイアウト', '', '', '', '', '', '', '']))
    d.append([layout_desc, '', '', '', '', '', '', ''])
    d.append(empty_row())
    d.append(hdr_row(['画面要素一覧', '', '', '', '', '', '', '']))
    d.append(hdr_row(['要素ID', '種別', '表示名', '入力値／選択肢', '説明', 'テスト難点タグ', '備考', '']))
    for e in elements:
        d.append(list(e) + [''] * (8 - len(e)))
    if table_cols:
        d.append(empty_row())
        d.append(hdr_row(['テーブル列定義', '', '', '', '', '', '', '']))
        d.append(hdr_row(['列名', '型', 'ソート', '説明', '', '', '', '']))
        for tc in table_cols:
            d.append(list(tc) + [''] * (8 - len(tc)))
    if struts_map:
        d.append(empty_row())
        d.append(hdr_row(['Struts1マッピング', '', '', '', '', '', '', '']))
        d.append(hdr_row(['項目', '値', '', '', '', '', '', '']))
        for k, v in struts_map.items():
            d.append([k, v, '', '', '', '', '', ''])
    if related_pgs:
        d.append(empty_row())
        d.append(hdr_row(['関連プログラム', '', '', '', '', '', '', '']))
        d.append([', '.join(related_pgs), '', '', '', '', '', '', ''])
    if test_notes:
        d.append(empty_row())
        d.append(hdr_row(['テスト難点詳細', '', '', '', '', '', '', '']))
        for tn in test_notes:
            d.append([tn, '', '', '', '', '', '', ''])
    return d

# ============ Sheet 3: 設備マスタ一覧 ============
eqp_list = screen_design_sheet(
    'SCR-MST-EQP-001', '設備マスタ一覧', 'PG-MST-EQP-001',
    '【上部】検索条件：設備種別（select）、電圧階級（select）、設置年（text, 年範囲）、保全ランク（select）、担当部署（text）。検索／クリアボタン。'
    '【下部】一覧テーブル：設備コード、設備名、設備種別、電圧階級、設置年月、保全ランク、担当部署。ページング（前へ/次へ）。'
    '一覧下部に「CSV出力」「選択削除」「新規登録」ボタン。各行にチェックボックス（全選択チェックボックス付き）。'
    '新規登録→設備マスタ登録画面へ遷移。設備コードクリック→編集画面へ遷移。',
    [
        ['eqp-search-type', 'select', '設備種別', '変圧器／遮断器／開閉器／ケーブル／母線／保護継電器／計器用変成器', '設備種別プルダウン', '複合検索（5条件AND）', ''],
        ['eqp-search-voltage', 'select', '電圧階級', '66kV／154kV／275kV／500kV', '電圧階級プルダウン', '', ''],
        ['eqp-search-year-from', 'text', '設置年（から）', 'YYYY（例: 2000）', '範囲検索開始年', '過去日バリデーション', ''],
        ['eqp-search-year-to', 'text', '設置年（まで）', 'YYYY（例: 2020）', '範囲検索終了年', 'from > to のバリデーション', ''],
        ['eqp-search-rank', 'select', '保全ランク', 'S／A／B／C', '重要度ランク', '', ''],
        ['eqp-search-dept', 'text', '担当部署', 'テキスト（部分一致）', '部署名で部分一致検索', '', ''],
        ['eqp-btn-search', 'submit', '検索', '', '条件で検索実行', 'POST→検索結果再描画', ''],
        ['eqp-btn-clear', 'submit', 'クリア', '', '検索条件全クリア→全件表示', '', ''],
        ['eqp-select-all', 'checkbox', '全選択', '', '一覧全件選択（当該ページのみ）', '全選択のスコープ検証（ページ内のみ）', '全ページ選択ではない'],
        ['eqp-check-{n}', 'checkbox', '選択', '', '行選択チェックボックス', 'index付きname属性', ''],
        ['eqp-code-{n}', 'link', '設備コード', 'クリック→編集画面へ', '設備コードリンク', '', ''],
        ['eqp-btn-csv', 'submit', 'CSV出力', '', '検索結果をCSVダウンロード', 'Content-Disposition検証', ''],
        ['eqp-btn-delete', 'submit', '選択削除', '', 'チェック行を削除', 'window.confirm→OK/キャンセル', '削除後ページ再計算'],
        ['eqp-btn-new', 'submit', '新規登録', '', '設備マスタ登録画面へ遷移', '', ''],
        ['eqp-paging', 'link', 'ページング', '前へ 1 2 3 … 次へ', 'ページングリンク', 'ページ跨ぎの全選択不可', ''],
    ],
    [
        ['設備コード', 'VARCHAR(10)', '○', '例: TR-0001'],
        ['設備名', 'VARCHAR(100)', '○', '例: 南変電所 #1 変圧器'],
        ['設備種別', 'VARCHAR(10)', '○', '変圧器/遮断器/…'],
        ['電圧階級', 'VARCHAR(10)', '○', '66kV/154kV/…'],
        ['設置年月', 'VARCHAR(6)', '○', 'YYYYMM'],
        ['保全ランク', 'CHAR(1)', '○', 'S/A/B/C'],
        ['担当部署', 'VARCHAR(50)', '○', ''],
    ],
    {
        'Action Path': '/mst/eqp/list',
        'Action Class': 'com.struts-lab.action.mst.EqpListAction',
        'Form Bean': 'com.struts-lab.form.mst.EqpSearchForm',
        'Forward (SUCCESS)': 'eqp.list',
        'Forward (EDIT)': 'eqp.edit',
        'Forward (NEW)': 'eqp.new',
    },
    ['PG-MST-EQP-001', 'PG-MST-EQP-002'],
    ['多段検索（5条件AND）：全組み合わせの検索結果検証が必要', '全選択チェックボックス：ページ内のみ全選択、他ページには影響しないことの検証', 'CSV出力：検索条件に応じた出力内容の検証', '選択削除：confirmダイアログのOK/キャンセル両方テスト'],
)

# ============ Sheet 4: 設備マスタ登録・編集 ============
eqp_edit = screen_design_sheet(
    'SCR-MST-EQP-002', '設備マスタ登録・編集', 'PG-MST-EQP-002',
    '【上部】タイトル「設備マスタ登録」（または編集）。5セクションに分かれた大フォーム。'
    'セクション間をアンカーリンクで移動可能（ページ内ジャンプ）。'
    '【基本情報】設備コード（自動採番/表示のみ）、設備名（text）、設備種別（select）、設備ステータス（select: 運用中/停止中/廃止）'
    '【電気仕様】電圧階級（select）、定格容量（text, MVA）、定格電流（text, A）、周波数（select: 50/60Hz）'
    '【設置場所】親設備選択（text + 「選択」ボタン→ポップアップ→選択値反映）、設置年月（text, YYYYMM）、設置場所住所（text）、経度緯度（text）'
    '【保全区分】保全ランク（select）、点検周期（text, 月数）、直近点検日（text, YYYYMMDD）、次回点検予定日（text, YYYYMMDD）'
    '【備考】備考（textarea）、添付ファイル（file, 仕様書PDF等）'
    '【下部】「登録」または「更新」ボタン、「戻る」ボタン（一覧に戻る）',
    [
        ['eqp-code', 'text', '設備コード', '自動採番（表示のみ）', '編集時のみ表示', '', '新規時は非表示'],
        ['eqp-name', 'text', '設備名', 'テキスト', '設備名称', '必須', 'max 100文字'],
        ['eqp-type', 'select', '設備種別', '変圧器／遮断器／開閉器／ケーブル／母線／保護継電器／計器用変成器', '設備種別', '必須', ''],
        ['eqp-status', 'select', '設備ステータス', '運用中／停止中／廃止', '稼働状態', '必須', '廃止→親設備不可'],
        ['eqp-voltage', 'select', '電圧階級', '66kV／154kV／275kV／500kV', '', '必須', ''],
        ['eqp-capacity', 'text', '定格容量', '数値（MVA）', '', '0より大きい', ''],
        ['eqp-current', 'text', '定格電流', '数値（A）', '', '0より大きい', ''],
        ['eqp-freq', 'select', '周波数', '50Hz／60Hz', '', '必須', ''],
        ['eqp-parent', 'text', '親設備', '設備コード＋設備名', '読取専用', 'ポップアップ選択結果反映', 'Stale Element注意'],
        ['eqp-parent-btn', 'button', '選択', 'window.open(…)', '親設備選択ポップアップ', 'ウィンドウハンドル切替', 'opener.documentで値反映'],
        ['eqp-install-date', 'text', '設置年月', 'YYYYMM', '', 'YYYYMM形式', ''],
        ['eqp-address', 'text', '設置場所住所', 'テキスト', '', '', ''],
        ['eqp-coord', 'text', '経度緯度', '例: 35.6812,139.7671', '', 'カンマ区切り形式', ''],
        ['eqp-rank', 'select', '保全ランク', 'S／A／B／C', '重要度', '必須', ''],
        ['eqp-interval', 'text', '点検周期（月）', '数値', '点検間隔', '1～120', ''],
        ['eqp-last-date', 'text', '直近点検日', 'YYYYMMDD', '', '', ''],
        ['eqp-next-date', 'text', '次回点検予定日', 'YYYYMMDD', '自動計算（直近＋周期）', '未来日であること', ''],
        ['eqp-note', 'textarea', '備考', 'テキスト', '', '', 'max 1000文字'],
        ['eqp-file', 'file', '添付ファイル', '', '仕様書等', 'ファイルサイズ/形式制限', ''],
        ['eqp-btn-save', 'submit', '登録／更新', '', '保存実行', 'POST→バリデーション→遷移', ''],
        ['eqp-btn-back', 'submit', '戻る', '', '一覧画面に戻る', '', ''],
    ],
    None,
    {
        'Action Path': '/mst/eqp/save',
        'Action Class': 'com.struts-lab.action.mst.EqpSaveAction (extends DispatchAction)',
        'Form Bean': 'com.struts-lab.form.mst.EqpForm',
        'Forward (SUCCESS)': 'eqp.list',
        'Forward (INPUT)': 'eqp.edit',
        'Forward (ERROR)': 'eqp.edit',
        'Validator': 'eqp-form-validation',
    },
    ['PG-MST-EQP-001', 'PG-MST-EQP-002'],
    ['親設備選択ポップアップ：ウィンドウハンドル切替 → 検索 → 選択 → opener 値反映の全フロー', '5セクション大フォーム：アンカーリンク移動でフォーム値が維持されること', '条件付きバリデーション：廃止設備は親設備に選択不可', '添付ファイル＋同時バリデーション：ファイル選択状態でバリデーションエラー時のファイル維持確認'],
)

# ============ Sheet 5: 点検項目マスタ一覧 ============
chk_list = screen_design_sheet(
    'SCR-MST-CHK-001', '点検項目マスタ一覧', 'PG-MST-CHK-001',
    '【上部】検索：設備種別（select）×点検種別（select: 日常/定期/精密）。検索／クリアボタン。'
    '【中部】一覧テーブル：テンプレート名、設備種別、点検種別、項目数、最終更新日。ソート可能。'
    '各行に「編集」「コピー」「削除」ボタン。'
    '【下部】「新規テンプレート」「並び順変更」ボタン。',
    [
        ['chk-search-type', 'select', '設備種別', '変圧器／遮断器／…', '設備種別フィルタ', '', ''],
        ['chk-search-kind', 'select', '点検種別', '日常点検／定期点検／精密点検', '点検種別フィルタ', '', ''],
        ['chk-btn-search', 'submit', '検索', '', '', '', ''],
        ['chk-btn-clear', 'submit', 'クリア', '', '', '', ''],
        ['chk-tmpl-name-{n}', 'link', 'テンプレート名', 'クリック→編集へ', '', '', ''],
        ['chk-btn-copy-{n}', 'submit', 'コピー', '', 'テンプレート複製', 'コピー後名称重複チェック', ''],
        ['chk-btn-order-up-{n}', 'submit', '▲', '', '並び順を上へ', 'index変化の検証', ''],
        ['chk-btn-order-down-{n}', 'submit', '▼', '', '並び順を下へ', 'index変化の検証', ''],
        ['chk-btn-new', 'submit', '新規テンプレート', '', '登録画面へ遷移', '', ''],
    ],
    [
        ['テンプレート名', 'VARCHAR(100)', '○', ''],
        ['設備種別', 'VARCHAR(10)', '○', ''],
        ['点検種別', 'VARCHAR(10)', '○', '日常/定期/精密'],
        ['項目数', 'INT', '○', '大分類以下の全項目数'],
        ['最終更新日', 'DATE', '○', ''],
    ],
    {
        'Action Path': '/mst/chkitem/list',
        'Action Class': 'com.struts-lab.action.mst.CheckItemListAction',
        'Form Bean': 'com.struts-lab.form.mst.CheckItemSearchForm',
        'Forward (SUCCESS)': 'chkitem.list',
        'Forward (EDIT)': 'chkitem.edit',
        'Forward (COPY)': 'chkitem.edit',
    },
    ['PG-MST-CHK-001', 'PG-MST-CHK-002'],
    ['並び順変更：▲/▼ボタン押下後の要素index変動検証（stale element）', 'コピー→名称重複バリデーションの確認'],
)

# ============ Sheet 6: 点検項目マスタ登録・編集 ============
chk_edit = screen_design_sheet(
    'SCR-MST-CHK-002', '点検項目マスタ登録・編集', 'PG-MST-CHK-002',
    '【上部】テンプレート基本情報：テンプレート名（text）、設備種別（select）、点検種別（select）'
    '【中部】ツリー編集エリア：大分類→中分類→個別項目の3階層。階層ごとに「追加」ボタン。'
    '各行に項目名（text）、判定基準（select: ○/×/△で判定）、正常範囲（text）、単位（text）。'
    '行削除ボタン（Submit→再描画）。'
    '【下部】「一括項目追加」ボタン→行数指定画面に遷移→戻って一括追加。'
    '「保存」「キャンセル」ボタン。',
    [
        ['chk-tmpl-name', 'text', 'テンプレート名', 'テキスト', '', '必須', ''],
        ['chk-tmpl-eqp-type', 'select', '設備種別', '変圧器／遮断器／…', '', '必須', ''],
        ['chk-tmpl-kind', 'select', '点検種別', '日常／定期／精密', '', '必須', ''],
        ['chk-cat1-name-{n}', 'text', '大分類名', 'テキスト', '例: 外観点検', '', ''],
        ['chk-btn-add-cat1', 'submit', '＋大分類追加', '', '大分類行追加→再描画', '動的行追加（Submit方式）', 'index再振り'],
        ['chk-cat2-name-{n}-{m}', 'text', '中分類名', 'テキスト', '例: ブッシング部', '', ''],
        ['chk-btn-add-cat2-{n}', 'submit', '＋中分類追加', '', '中分類行追加', 'Nested index', ''],
        ['chk-item-name-{i}', 'text', '項目名', 'テキスト', '例: き裂の有無', '必須', ''],
        ['chk-item-criteria-{i}', 'select', '判定基準', '○のみ／○×△', '', '', ''],
        ['chk-item-range-{i}', 'text', '正常範囲', 'テキスト', '例: 0.1～5.0', '', ''],
        ['chk-item-unit-{i}', 'text', '単位', 'テキスト', '例: mm', '', ''],
        ['chk-btn-del-{i}', 'submit', '削除', '', '項目削除', '行削除後index再振り', ''],
        ['chk-btn-bulk-add', 'submit', '一括項目追加', '', '行数指定画面へ遷移', 'Wizard的な別画面遷移', '戻り値の保持'],
        ['chk-btn-save', 'submit', '保存', '', '', '', ''],
        ['chk-btn-cancel', 'submit', 'キャンセル', '', '一覧に戻る', '', ''],
    ],
    None,
    {
        'Action Path': '/mst/chkitem/save',
        'Action Class': 'com.struts-lab.action.mst.CheckItemSaveAction',
        'Form Bean': 'com.struts-lab.form.mst.CheckItemForm (ValidatorForm)',
        'Forward (SUCCESS)': 'chkitem.list',
        'Forward (INPUT)': 'chkitem.edit',
        'Validator': 'chkitem-form-validation',
    },
    ['PG-MST-CHK-001', 'PG-MST-CHK-002'],
    ['3階層ツリー動的行追加：大分類追加→中分類追加→項目追加の順序でindexが変動', '行削除後のindex再振り：削除→追加の繰り返しでindex連番が維持されるか', '一括項目追加の別画面遷移→戻る：追加データがフォームに保持されているか'],
)

# ============ Assemble workbook ============
w = XlsxWriter()
w.add_sheet('改訂履歴', REVISION_HISTORY, col_widths=[5, 12, 30, 15, 15, 8, 20])
w.add_sheet('Sheet2', [])
w.add_sheet('設備マスタ一覧', eqp_list, col_widths=[20, 12, 18, 30, 30, 22, 22, 20])
w.add_sheet('設備マスタ登録・編集', eqp_edit, col_widths=[20, 12, 18, 30, 30, 22, 22, 20])
w.add_sheet('点検項目マスタ一覧', chk_list, col_widths=[20, 12, 18, 30, 30, 22, 22, 20])
w.add_sheet('点検項目マスタ登録・編集', chk_edit, col_widths=[20, 12, 18, 30, 30, 22, 22, 20])
w.save('02_画面設計書_マスタ管理.xlsx')
