# StrutsLab 実装タスク一覧

> 参照: `docs/superpowers/plans/2026-05-23-strutslab-implementation.md`
> 設計書: `docs/design-docs/`

## 全体進捗

| Phase | タスク | 内容 | 状況 | 備考 |
|---|---|---|---|---|
| 0 | Task 0 | プロジェクト骨格 (Maven, web.xml, struts-config, Tiles, Validator, MyBatis, i18n) | ⬜ | pom.xml, 全設定XML |
| 1 | Task 1 | DBスキーマ + MyBatis Session (schema.sql, seed.sql, 18テーブル, 11Mapper) | ⬜ | H2ファイルモード |
| 2 | Task 2 | 共通インフラ (Tiles 7レイアウト, 共通JSP 7, CSS, login.jsp, menu.jsp) | ⬜ | 2005年風スタイル |
| 3 | Task 3 | ログイン認証 (LoginAction, LogoutAction, LoginForm, SHA-256) | ⬜ | セッション管理 |
| 4 | Task 4 | カスタムタグ 7種 (設備ツリー, 日付, セクション, バッジ, チェックリスト, 動的行, タイムライン) | ⬜ | TagSupport継承 |
| 5 | Task 5 | マスタ管理: 設備マスタ一覧/登録編集 (画面1-2) | ⬜ | EqpListAction, EqpSaveAction, 5セクションフォーム |
| 6 | Task 6 | マスタ管理: 点検項目一覧/登録編集 (画面3-4) | ⬜ | 3階層ツリー, 動的行追加/削除 |
| 7 | Task 7 | 点検計画: 年間計画一覧 + Wizard 4ステップ (画面5-6) | ⬜ | マトリクス表示, hidden持ち回り |
| 8 | Task 8 | 点検実施: 当日一覧/実施入力/詳細修正/承認一覧 (画面7-10) | ⬜ | ネストチェックリスト, 条件付き必須, 写真添付 |
| 9 | Task 9 | 異常報告: 一覧/登録/詳細 (画面11-13) | ⬜ | 7条件検索, 類似事例, 多段階ステータス遷移 |
| 10 | Task 10 | 対応指示・是正: 登録/一覧/詳細完了/CAPA (画面14-17) | ⬜ | Indexed Properties, なぜなぜ分析5段階 |
| 11 | Task 11 | 組織/カレンダー/部品/レポート (画面18-27) | ⬜ | 4階層ツリー, カレンダー, 在庫管理 |
| 12 | Task 12 | バリデーション + エラーメッセージ統合 | ⬜ | validation.xml, ApplicationResources_ja |
| 13 | Task 13 | DB初期化 + 最終統合テスト | ⬜ | DbInitializer, tomcat7:run |

## 画面一覧 (27画面)

| No. | 画面ID | 画面名 | Task | 状況 |
|---|---|---|---|---|
| 1 | SCR-MST-EQP-001 | 設備マスタ一覧 | 5 | ⬜ |
| 2 | SCR-MST-EQP-002 | 設備マスタ登録・編集 | 5 | ⬜ |
| 3 | SCR-MST-CHK-001 | 点検項目マスタ一覧 | 6 | ⬜ |
| 4 | SCR-MST-CHK-002 | 点検項目マスタ登録・編集 | 6 | ⬜ |
| 5 | SCR-INS-PLAN-001 | 年間点検計画一覧 | 7 | ⬜ |
| 6 | SCR-INS-PLAN-002 | 点検計画登録（Wizard 4step） | 7 | ⬜ |
| 7 | SCR-INS-DAILY-001 | 点検実施一覧（当日） | 8 | ⬜ |
| 8 | SCR-INS-EXEC-001 | 点検実施入力 | 8 | ⬜ |
| 9 | SCR-INS-EXEC-002 | 点検実施詳細・修正 | 8 | ⬜ |
| 10 | SCR-INS-APPR-001 | 点検実施承認一覧 | 8 | ⬜ |
| 11 | SCR-INC-LIST-001 | 異常報告一覧 | 9 | ⬜ |
| 12 | SCR-INC-CREATE-001 | 異常報告登録 | 9 | ⬜ |
| 13 | SCR-INC-DETAIL-001 | 異常報告詳細 | 9 | ⬜ |
| 14 | SCR-CTR-CREATE-001 | 対応指示登録 | 10 | ⬜ |
| 15 | SCR-CTR-LIST-001 | 対応指示一覧 | 10 | ⬜ |
| 16 | SCR-CTR-DETAIL-001 | 対応指示詳細・完了報告 | 10 | ⬜ |
| 17 | SCR-CTR-CAPA-001 | 是正処置報告書 | 10 | ⬜ |
| 18 | SCR-RPT-SUMMARY-001 | 総合レポート | 11 | ⬜ |
| 19 | SCR-ORG-DEPT-001 | 部署マスタ一覧 | 11 | ⬜ |
| 20 | SCR-ORG-DEPT-002 | 部署マスタ登録・編集 | 11 | ⬜ |
| 21 | SCR-ORG-EMP-001 | 担当者マスタ一覧 | 11 | ⬜ |
| 22 | SCR-ORG-EMP-002 | 担当者マスタ登録・編集 | 11 | ⬜ |
| 23 | SCR-CAL-LIST-001 | 休日カレンダー一覧 | 11 | ⬜ |
| 24 | SCR-CAL-REG-001 | 休日登録・編集 | 11 | ⬜ |
| 25 | SCR-PRT-LIST-001 | 保守部品一覧 | 11 | ⬜ |
| 26 | SCR-PRT-REG-001 | 保守部品登録・編集 | 11 | ⬜ |
| 27 | SCR-PRT-USAGE-001 | 部品使用実績一覧 | 11 | ⬜ |

## DBテーブル (18テーブル)

| テーブル | 説明 | Task | 状況 |
|---|---|---|---|
| equipment | 設備マスタ | 1 | ⬜ |
| inspection_template | 点検項目テンプレート | 1 | ⬜ |
| inspection_items | 点検項目（3階層） | 1 | ⬜ |
| inspection_plans | 点検計画 | 1 | ⬜ |
| inspection_results | 点検実施結果ヘッダ | 1 | ⬜ |
| inspection_items_results | 点検項目結果明細 | 1 | ⬜ |
| inspection_photos | 点検写真 | 1 | ⬜ |
| incidents | 異常報告 | 1 | ⬜ |
| incident_timeline | 異常報告経過記録 | 1 | ⬜ |
| incident_attachments | 異常報告添付 | 1 | ⬜ |
| counter_orders | 対応指示ヘッダ | 1 | ⬜ |
| counter_order_details | 対応指示明細 | 1 | ⬜ |
| capa_reports | 是正処置報告書 | 1 | ⬜ |
| departments | 部署マスタ | 1 | ⬜ |
| employees | 担当者マスタ | 1 | ⬜ |
| employee_qualifications | 担当者保有資格 | 1 | ⬜ |
| holidays | 休日マスタ | 1 | ⬜ |
| parts | 保守部品マスタ | 1 | ⬜ |
| part_equipment_relations | 部品・適用設備 | 1 | ⬜ |
| part_usages | 部品使用実績 | 1 | ⬜ |

## 凡例

- ⬜ = 未着手
- 🔄 = 進行中
- ✅ = 完了
- ❌ = ブロック中
