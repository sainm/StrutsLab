#!/usr/bin/env python3
"""生成 01_システム概要書.xlsx"""
import sys
sys.path.insert(0, '.')
from _xlsx_gen import *

data = []

# === 文書管理情報 ===
data.append(hdr_row(['文書管理情報', '', '', '', '', '', '', '']))
data.extend(DOC_INFO_8)
data[2][5] = 'SYS-OVERVIEW-001'  # 文書番号

data.append(empty_row())
data.append(hdr_row(['システムアーキテクチャ', '', '', '', '', '', '', '']))
data.append(hdr_row(['レイヤー', '採用技術', 'バージョン', '役割', '', '', '', '']))
data.append(['プレゼンテーション層', 'Struts 1.3', '1.3.10', 'ActionServlet, Action, ActionForm, JSP', '', '', '', ''])
data.append(['プレゼンテーション層', 'Tiles', '1.3.10', '画面レイアウト共通化（header/body/footer/menu）', '', '', '', ''])
data.append(['プレゼンテーション層', 'JSP + カスタムタグ', '', 'スクリプトレット主体、独自タグライブラリ', '', '', '', ''])
data.append(['ビジネスロジック層', 'Actionクラス', '', 'Struts1 Action + DispatchAction', '', '', '', ''])
data.append(['データアクセス層', 'MyBatis', '3.5.x', 'SQLマッピング、Mapper.xml', '', '', '', ''])
data.append(['データベース', 'H2 Database', '1.4.200', 'ファイルモード（~/struts-lab-db）', '', '', '', ''])
data.append(['バリデーション', 'Validator Plug-in', '1.3.10', 'validation.xml, validation-rules.xml', '', '', '', ''])
data.append(['ビルドツール', 'Maven', '3.x', 'war パッケージ → Tomcat 8.x にデプロイ', '', '', '', ''])
data.append(['サーブレットコンテナ', 'Apache Tomcat', '8.5.x', 'Java 1.8 対応', '', '', '', ''])

data.append(empty_row())
data.append(hdr_row(['機能モジュール一覧', '', '', '', '', '', '', '']))
data.append(hdr_row(['モジュール名', '略称', '対象画面数', '主なテスト難点', '', '', '', '']))
data.append(['マスタ管理', 'MASTER', '4', '多段検索、ツリー選択、親子設定、CSV出力', '', '', '', ''])
data.append(['点検計画・実施', 'INSPECT', '7', 'Wizard、ネストチェックリスト、写真添付、条件付きバリデーション、承認フロー', '', '', '', ''])
data.append(['異常報告', 'INCIDENT', '3', 'ステータス遷移、条件付き必須項目、類似事例検索', '', '', '', ''])
data.append(['対応指示・是正', 'COUNTER', '4', '動的行追加、Nested Properties、ポップアップ選択、全明細完了チェック', '', '', '', ''])
data.append(['組織・要員管理', 'ORG', '4', '階層ツリー、資格期限チェック、多セクションフォーム', '', '', '', ''])
data.append(['カレンダー・休日', 'CALENDAR', '2', 'カレンダー表示、一括登録、重複チェック', '', '', '', ''])
data.append(['保守部品管理', 'PARTS', '3', 'チェックボックスツリー、在庫アラート、数量自動引当', '', '', '', ''])
data.append(['レポート', 'REPORT', '1', 'クロス集計、テーブル形式グラフ、期間指定', '', '', '', ''])

data.append(empty_row())
data.append(hdr_row(['画面一覧（全27画面）', '', '', '', '', '', '', '']))
data.append(hdr_row(['No.', 'モジュール', '画面ID', '画面名', '種別', '主要Struts1要素', 'テスト難点カテゴリ', '']))
screens = [
    ['1', 'MASTER', 'SCR-MST-EQP-001', '設備マスタ一覧', '一覧・検索', 'Action, ActionForm, ページング', '多段検索、一括操作、CSV出力'],
    ['2', 'MASTER', 'SCR-MST-EQP-002', '設備マスタ登録・編集', '登録・編集', 'ActionForm, Validator, ポップアップ', '条件付きバリデーション、ポップアップ選択→親画面反映'],
    ['3', 'MASTER', 'SCR-MST-CHK-001', '点検項目マスタ一覧', '一覧・検索', 'Action, ActionForm', 'テンプレートコピー、並び順変更'],
    ['4', 'MASTER', 'SCR-MST-CHK-002', '点検項目マスタ登録・編集', '登録・編集', 'DispatchAction, 動的行追加', '3階層ツリー編集、一括項目追加'],
    ['5', 'INSPECT', 'SCR-INS-PLAN-001', '年間点検計画一覧', '一覧・検索', 'Action, ActionForm', 'マトリクス表示（動的カラム）、月別対比'],
    ['6', 'INSPECT', 'SCR-INS-PLAN-002', '点検計画登録（Wizard）', '登録', 'ActionForm, Wizard 4ステップ', 'マルチステップデータ保持、途中離脱→再開'],
    ['7', 'INSPECT', 'SCR-INS-DAILY-001', '点検実施一覧（当日）', '一覧', 'Action, ActionForm', 'ステータスバッジ、一覧からの直接遷移'],
    ['8', 'INSPECT', 'SCR-INS-EXEC-001', '点検実施入力', '入力', 'ActionForm, FileUpload, Validator', 'ネストチェックリスト、条件付き必須、写真添付'],
    ['9', 'INSPECT', 'SCR-INS-EXEC-002', '点検実施詳細・修正', '表示・編集', 'ActionForm, Validator', '修正申請→承認フロー、修正理由必須'],
    ['10', 'INSPECT', 'SCR-INS-APPR-001', '点検実施承認一覧', '一覧', 'Action, 一括操作', '一括承認/差戻し、差戻し理由必須'],
    ['11', 'INCIDENT', 'SCR-INC-LIST-001', '異常報告一覧', '一覧・検索', 'Action, ActionForm, ページング', '複合検索、一括ステータス更新、検索条件保存'],
    ['12', 'INCIDENT', 'SCR-INC-CREATE-001', '異常報告登録', '登録', 'ActionForm, FileUpload, Validator', '点検データ引継ぎ、類似事例検索結果表示'],
    ['13', 'INCIDENT', 'SCR-INC-DETAIL-001', '異常報告詳細', '詳細・遷移', 'ActionForm, LookupDispatchAction', '多段階ステータス遷移、条件付き必須項目'],
    ['14', 'COUNTER', 'SCR-CTR-CREATE-001', '対応指示登録', '登録', 'ActionForm, Nested/Indexed Properties, ポップアップ', '動的行追加（Submit方式）、ポップアップ→親画面、index再振り'],
    ['15', 'COUNTER', 'SCR-CTR-LIST-001', '対応指示一覧', '一覧・検索', 'Action, ActionForm', '複合検索、印刷用別ウィンドウ、一括ステータス更新'],
    ['16', 'COUNTER', 'SCR-CTR-DETAIL-001', '対応指示詳細・完了報告', '詳細・更新', 'ActionForm, Validator', '明細個別更新、全明細完了自動検出、実績入力'],
    ['17', 'COUNTER', 'SCR-CTR-CAPA-001', '是正処置報告書', '登録', 'ActionForm, Validator', 'なぜなぜ分析5段階、状態別項目増減'],
    ['18', 'REPORT', 'SCR-RPT-SUMMARY-001', '総合レポート', '表示', 'Action, ActionForm', 'クロス集計、テーブル形式グラフ、CSV/印刷'],
    ['19', 'ORG', 'SCR-ORG-DEPT-001', '部署マスタ一覧', '一覧', 'Action, ActionForm', '4階層ツリー表示、統廃合履歴'],
    ['20', 'ORG', 'SCR-ORG-DEPT-002', '部署マスタ登録・編集', '登録・編集', 'ActionForm, Validator', 'ツリーポップアップ選択、過去日バリデーション'],
    ['21', 'ORG', 'SCR-ORG-EMP-001', '担当者マスタ一覧', '一覧・検索', 'Action, ActionForm', '複合検索、資格期限アラート'],
    ['22', 'ORG', 'SCR-ORG-EMP-002', '担当者マスタ登録・編集', '登録・編集', 'ActionForm, Validator', '5セクション大フォーム、認定期限バリデーション'],
    ['23', 'CALENDAR', 'SCR-CAL-LIST-001', '休日カレンダー一覧', '一覧', 'Action, ActionForm', '月別カレンダー表示、色分け'],
    ['24', 'CALENDAR', 'SCR-CAL-REG-001', '休日登録・編集', '登録・編集', 'ActionForm, Validator', '範囲指定一括登録、重複チェック、振替設定'],
    ['25', 'PARTS', 'SCR-PRT-LIST-001', '保守部品一覧', '一覧・検索', 'Action, ActionForm', '在庫アラート表示'],
    ['26', 'PARTS', 'SCR-PRT-REG-001', '保守部品登録・編集', '登録・編集', 'ActionForm, Validator, FileUpload', 'チェックボックスツリー、仕様書添付'],
    ['27', 'PARTS', 'SCR-PRT-USAGE-001', '部品使用実績一覧', '一覧・検索', 'Action, ActionForm', '在庫自動引当表示、アラート'],
]
for s in screens:
    data.append(s)

data.append(empty_row())
data.append(hdr_row(['Struts1 設定ファイル構成', '', '', '', '', '', '', '']))
data.append(hdr_row(['ファイル', '配置パス', '役割', '', '', '', '', '']))
data.append(['web.xml', '/WEB-INF/web.xml', 'ActionServlet, Tiles, Validator 設定', '', '', '', '', ''])
data.append(['struts-config.xml', '/WEB-INF/struts-config.xml', 'Actionマッピング, FormBean, Forward, Plug-in', '', '', '', '', ''])
data.append(['tiles-defs.xml', '/WEB-INF/tiles-defs.xml', 'Tilesレイアウト定義', '', '', '', '', ''])
data.append(['validation.xml', '/WEB-INF/validation.xml', 'Validator検証ルール', '', '', '', '', ''])
data.append(['validation-rules.xml', '/WEB-INF/validation-rules.xml', 'Validator基本ルール定義', '', '', '', '', ''])
data.append(['mybatis-config.xml', 'クラスパス直下', 'MyBatis設定, データソース, Mapper登録', '', '', '', '', ''])
data.append(['ApplicationResources_ja.properties', 'クラスパス直下', '日本語メッセージリソース', '', '', '', '', ''])
data.append(['*.tld', '/WEB-INF/tld/', 'カスタムタグ定義（設備ツリー、日付ピッカー）', '', '', '', '', ''])

w = make_standard_workbook('システム概要書', data)
w.save('01_システム概要書.xlsx')
