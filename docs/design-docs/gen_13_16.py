#!/usr/bin/env python3
"""生成 13_DB設計書.xlsx  14_共通部品設計書.xlsx  15_エラーコード一覧.xlsx  16_UIテスト難点マップ.xlsx"""
import sys; sys.path.insert(0, '.')
from _xlsx_gen import *

# ===== 13_DB設計書.xlsx =====
db_data = []
db_data.append(hdr_row(['データベース設計書', '', '', '', '', '', '', '']))
db_data.append(hdr_row(['文書管理情報', '', '', '', '', '', '', '']))
for r in [['システムID', 'STRUTSLAB-001', '', '', 'DBMS', 'H2 Database 1.4.200（ファイルモード）', '', ''],
          ['文書番号', 'DOC-DB-001', '', '', '版数', '1.0', '', ''],
          ['作成日', '2026/05/23', '', '', '作成者', 'システム開発部', '', ''],
          ['文字コード', 'UTF-8', '', '', '接続モード', 'ファイル（./struts-lab-db）', '', '']]:
    db_data.append(r)
db_data.append(empty_row())

tables = {
    'equipment': {
        'desc': '設備マスタ',
        'cols': [
            ['equipment_code','VARCHAR(10)','PK','設備コード（例: TR-0001）'],
            ['equipment_name','VARCHAR(100)','NOT NULL','設備名'],
            ['equipment_type','VARCHAR(10)','NOT NULL','設備種別（変圧器/遮断器/開閉器/ケーブル/母線/保護継電器/計器用変成器）'],
            ['voltage_level','VARCHAR(10)','','電圧階級（66kV/154kV/275kV/500kV）'],
            ['rated_capacity','INT','','定格容量（MVA）'],
            ['rated_current','INT','','定格電流（A）'],
            ['frequency','VARCHAR(5)','','周波数（50Hz/60Hz）'],
            ['parent_equipment_code','VARCHAR(10)','FK→equipment','親設備コード（自己参照）'],
            ['install_date','VARCHAR(6)','','設置年月（YYYYMM）'],
            ['location_address','VARCHAR(200)','','設置場所住所'],
            ['coordinates','VARCHAR(50)','','経度緯度（例: 35.6812,139.7671）'],
            ['maintenance_rank','CHAR(1)','NOT NULL','保全ランク（S/A/B/C）'],
            ['inspection_interval','INT','','点検周期（月）'],
            ['last_inspection_date','VARCHAR(8)','','直近点検日（YYYYMMDD）'],
            ['next_inspection_date','VARCHAR(8)','','次回点検予定日（YYYYMMDD）'],
            ['status','VARCHAR(5)','NOT NULL','ステータス（運用中/停止中/廃止）'],
            ['note','TEXT','','備考'],
            ['created_at','TIMESTAMP','NOT NULL','登録日時'],
            ['updated_at','TIMESTAMP','NOT NULL','更新日時'],
        ],
        'idx': ['idx_eqp_type ON (equipment_type)','idx_eqp_parent ON (parent_equipment_code)','idx_eqp_rank ON (maintenance_rank)','idx_eqp_status ON (status)']
    },
    'inspection_template': {
        'desc': '点検項目テンプレート',
        'cols': [
            ['template_id','INT','PK AUTO_INCREMENT','テンプレートID'],
            ['template_name','VARCHAR(100)','NOT NULL','テンプレート名'],
            ['equipment_type','VARCHAR(10)','NOT NULL','対象設備種別'],
            ['inspection_kind','VARCHAR(5)','NOT NULL','点検種別（日常/定期/精密）'],
            ['sort_order','INT','','表示順'],
            ['created_at','TIMESTAMP','NOT NULL',''],
            ['updated_at','TIMESTAMP','NOT NULL',''],
        ],
        'idx': ['idx_itmpl_type_kind ON (equipment_type, inspection_kind)']
    },
    'inspection_items': {
        'desc': '点検項目（大分類・中分類・個別項目の3階層）',
        'cols': [
            ['item_id','INT','PK AUTO_INCREMENT','項目ID'],
            ['template_id','INT','FK→inspection_template','所属テンプレート'],
            ['parent_item_id','INT','FK→inspection_items','親項目ID（NULL=大分類）'],
            ['item_level','INT','NOT NULL','階層レベル（1=大分類/2=中分類/3=個別項目）'],
            ['item_name','VARCHAR(200)','NOT NULL','項目名'],
            ['judge_criteria','VARCHAR(5)','','判定基準（ONLY_O/O_X_TRI）'],
            ['normal_range','VARCHAR(100)','','正常範囲'],
            ['unit','VARCHAR(20)','','単位'],
            ['sort_order','INT','','表示順'],
        ],
        'idx': ['idx_item_tmpl ON (template_id)','idx_item_parent ON (parent_item_id)']
    },
    'inspection_plans': {
        'desc': '点検計画',
        'cols': [
            ['plan_id','INT','PK AUTO_INCREMENT','計画ID'],
            ['fiscal_year','VARCHAR(4)','NOT NULL','年度'],
            ['equipment_code','VARCHAR(10)','FK→equipment','対象設備'],
            ['template_id','INT','FK→inspection_template','点検項目テンプレート'],
            ['planned_date','VARCHAR(8)','NOT NULL','点検予定日（YYYYMMDD）'],
            ['team_code','VARCHAR(10)','','担当班コード'],
            ['person_code','VARCHAR(10)','FK→employees','担当者'],
            ['status','VARCHAR(10)','NOT NULL','予定/実施済/中止/延期'],
            ['is_locked','BOOLEAN','NOT NULL DEFAULT FALSE','計画ロック'],
            ['note','TEXT','','備考'],
            ['created_at','TIMESTAMP','NOT NULL',''],
            ['updated_at','TIMESTAMP','NOT NULL',''],
        ],
        'idx': ['idx_plan_year ON (fiscal_year)','idx_plan_eqp ON (equipment_code)','idx_plan_date ON (planned_date)']
    },
    'inspection_results': {
        'desc': '点検実施結果（ヘッダ）',
        'cols': [
            ['result_id','INT','PK AUTO_INCREMENT','結果ID'],
            ['plan_id','INT','FK→inspection_plans','点検計画ID'],
            ['executed_date','VARCHAR(8)','NOT NULL','実施日（YYYYMMDD）'],
            ['executed_by','VARCHAR(10)','FK→employees','実施者'],
            ['summary_judge','VARCHAR(5)','','総合判定（NORMAL/ABNORMAL/WATCH）'],
            ['summary_note','TEXT','','総合所見'],
            ['next_recommended_date','VARCHAR(8)','','次回点検推奨日'],
            ['approval_status','VARCHAR(5)','','承認ステータス（申請中/承認済/差戻）'],
            ['created_at','TIMESTAMP','NOT NULL',''],
            ['updated_at','TIMESTAMP','NOT NULL',''],
        ],
        'idx': ['idx_res_plan ON (plan_id)','idx_res_date ON (executed_date)','idx_res_approval ON (approval_status)']
    },
    'inspection_items_results': {
        'desc': '点検項目実施結果（明細）',
        'cols': [
            ['result_item_id','INT','PK AUTO_INCREMENT',''],
            ['result_id','INT','FK→inspection_results',''],
            ['item_id','INT','FK→inspection_items','点検項目ID'],
            ['judge','CHAR(1)','NOT NULL','判定（○/×/△）'],
            ['measured_value','VARCHAR(50)','','実測値'],
            ['note','TEXT','','所見'],
        ],
        'idx': ['idx_iir_result ON (result_id)']
    },
    'inspection_photos': {
        'desc': '点検写真',
        'cols': [
            ['photo_id','INT','PK AUTO_INCREMENT',''],
            ['result_item_id','INT','FK→inspection_items_results',''],
            ['file_path','VARCHAR(500)','NOT NULL','ファイルパス'],
            ['original_name','VARCHAR(200)','','元ファイル名'],
            ['file_size','BIGINT','','バイト数'],
            ['created_at','TIMESTAMP','NOT NULL',''],
        ],
        'idx': ['idx_photo_item ON (result_item_id)']
    },
    'incidents': {
        'desc': '異常報告',
        'cols': [
            ['incident_no','VARCHAR(20)','PK','報告番号（INC-YYYYMMDD-NNN）'],
            ['result_id','INT','FK→inspection_results','関連点検結果（点検から起票時）'],
            ['incident_datetime','TIMESTAMP','NOT NULL','発生日時'],
            ['finder','VARCHAR(50)','NOT NULL','発見者'],
            ['equipment_code','VARCHAR(10)','FK→equipment','対象設備'],
            ['weather','VARCHAR(5)','','天候（晴/曇/雨/雪/雷）'],
            ['temperature','INT','','気温（℃）'],
            ['incident_type','VARCHAR(10)','NOT NULL','異常種別'],
            ['severity','VARCHAR(5)','NOT NULL','重大度（軽微/中/重大/緊急）'],
            ['incident_part','VARCHAR(200)','NOT NULL','異常部位'],
            ['incident_detail','TEXT','NOT NULL','異常詳細'],
            ['tmp_action','TEXT','','暫定処置内容'],
            ['tmp_action_person','VARCHAR(10)','FK→employees','処置担当者'],
            ['tmp_action_date','VARCHAR(8)','','処置完了日'],
            ['cause','TEXT','','推定原因'],
            ['counter_detail','TEXT','','対応内容'],
            ['status','VARCHAR(10)','NOT NULL DEFAULT 未了','未了/調査中/対応中/完了/再発防止/クローズ'],
            ['created_at','TIMESTAMP','NOT NULL',''],
            ['updated_at','TIMESTAMP','NOT NULL',''],
        ],
        'idx': ['idx_inc_status ON (status)','idx_inc_date ON (incident_datetime)','idx_inc_eqp ON (equipment_code)','idx_inc_type ON (incident_type)','idx_inc_severity ON (severity)']
    },
    'incident_timeline': {
        'desc': '異常報告経過記録',
        'cols': [
            ['timeline_id','INT','PK AUTO_INCREMENT',''],
            ['incident_no','VARCHAR(20)','FK→incidents',''],
            ['action_datetime','TIMESTAMP','NOT NULL','処理日時'],
            ['action_user','VARCHAR(50)','NOT NULL','処理者'],
            ['action_content','TEXT','NOT NULL','処理内容'],
            ['status_from','VARCHAR(10)','','遷移元ステータス'],
            ['status_to','VARCHAR(10)','','遷移先ステータス'],
        ],
        'idx': ['idx_tl_inc ON (incident_no)','idx_tl_date ON (action_datetime)']
    },
    'incident_attachments': {
        'desc': '異常報告添付ファイル',
        'cols': [
            ['attachment_id','INT','PK AUTO_INCREMENT',''],
            ['incident_no','VARCHAR(20)','FK→incidents',''],
            ['file_path','VARCHAR(500)','NOT NULL',''],
            ['original_name','VARCHAR(200)','',''],
            ['file_size','BIGINT','',''],
            ['created_at','TIMESTAMP','NOT NULL',''],
        ],
        'idx': ['idx_att_inc ON (incident_no)']
    },
    'counter_orders': {
        'desc': '対応指示（ヘッダ）',
        'cols': [
            ['order_no','VARCHAR(20)','PK','指示番号（CTR-YYYYMMDD-NNN）'],
            ['incident_no','VARCHAR(20)','FK→incidents','関連異常報告番号'],
            ['order_date','VARCHAR(8)','NOT NULL','指示日'],
            ['issuer','VARCHAR(50)','NOT NULL','指示者'],
            ['overall_deadline','VARCHAR(8)','','全体期限'],
            ['overall_priority','VARCHAR(3)','NOT NULL','優先度（高/中/低）'],
            ['status','VARCHAR(5)','NOT NULL DEFAULT 未了','未了/処理中/完了'],
            ['created_at','TIMESTAMP','NOT NULL',''],
            ['updated_at','TIMESTAMP','NOT NULL',''],
        ],
        'idx': ['idx_ctr_status ON (status)','idx_ctr_inc ON (incident_no)']
    },
    'counter_order_details': {
        'desc': '対応指示明細',
        'cols': [
            ['detail_id','INT','PK AUTO_INCREMENT',''],
            ['order_no','VARCHAR(20)','FK→counter_orders',''],
            ['seq_no','INT','NOT NULL','行番号'],
            ['work_content','VARCHAR(500)','NOT NULL','作業内容'],
            ['person_code','VARCHAR(10)','FK→employees','担当者'],
            ['deadline','VARCHAR(8)','','期限（YYYYMMDD）'],
            ['priority','VARCHAR(3)','','優先度（高/中/低）'],
            ['status','VARCHAR(3)','NOT NULL DEFAULT 未了','未了/完了'],
            ['actual_hours','DECIMAL(5,1)','','実績作業時間'],
            ['used_part_code','VARCHAR(10)','FK→parts','使用部品'],
            ['used_quantity','INT','','使用量'],
            ['note','TEXT','','所見'],
        ],
        'idx': ['idx_ctrd_order ON (order_no)']
    },
    'capa_reports': {
        'desc': '是正処置報告書',
        'cols': [
            ['capa_id','INT','PK AUTO_INCREMENT',''],
            ['incident_no','VARCHAR(20)','FK→incidents',''],
            ['why1','TEXT','NOT NULL','なぜ①'],
            ['why2','TEXT','NOT NULL','なぜ②'],
            ['why3','TEXT','NOT NULL','なぜ③'],
            ['why4','TEXT','NOT NULL','なぜ④'],
            ['why5','TEXT','NOT NULL','なぜ⑤（根本原因）'],
            ['countermeasure','TEXT','NOT NULL','再発防止策'],
            ['verify_method','TEXT','NOT NULL','効果確認方法'],
            ['verify_deadline','VARCHAR(8)','NOT NULL','効果確認期限'],
            ['status','VARCHAR(5)','NOT NULL DEFAULT 申請中','申請中/承認済/差戻'],
            ['created_at','TIMESTAMP','NOT NULL',''],
            ['updated_at','TIMESTAMP','NOT NULL',''],
        ],
        'idx': ['idx_capa_inc ON (incident_no)']
    },
    'departments': {
        'desc': '部署マスタ（階層構造）',
        'cols': [
            ['dept_code','VARCHAR(10)','PK','部署コード'],
            ['dept_name','VARCHAR(100)','NOT NULL','部署名'],
            ['parent_dept_code','VARCHAR(10)','FK→departments','上位部署コード'],
            ['dept_level','INT','NOT NULL','階層レベル（1-4）'],
            ['dept_type','VARCHAR(5)','NOT NULL','本社/支社/営業所/出張所'],
            ['start_date','VARCHAR(8)','NOT NULL','有効開始日'],
            ['end_date','VARCHAR(8)','','有効終了日'],
            ['address','VARCHAR(200)','','住所'],
            ['tel','VARCHAR(20)','','電話番号'],
            ['created_at','TIMESTAMP','NOT NULL',''],
            ['updated_at','TIMESTAMP','NOT NULL',''],
        ],
        'idx': ['idx_dept_parent ON (parent_dept_code)','idx_dept_level ON (dept_level)']
    },
    'employees': {
        'desc': '担当者マスタ',
        'cols': [
            ['emp_no','VARCHAR(10)','PK','社員番号（EMP-NNNN）'],
            ['name','VARCHAR(100)','NOT NULL','氏名'],
            ['name_kana','VARCHAR(100)','','フリガナ'],
            ['birth_date','VARCHAR(8)','','生年月日'],
            ['join_date','VARCHAR(6)','','入社年月'],
            ['dept_code','VARCHAR(10)','FK→departments','所属部署'],
            ['position','VARCHAR(10)','','職位'],
            ['assign_date','VARCHAR(8)','','配属日'],
            ['inspection_rank','CHAR(1)','','点検員ランク（A/B/C）'],
            ['inspection_cert_date','VARCHAR(8)','','点検員認定日'],
            ['inspection_cert_expire','VARCHAR(8)','','認定有効期限'],
            ['login_id','VARCHAR(50)','NOT NULL UNIQUE','ログインID'],
            ['password_hash','VARCHAR(64)','NOT NULL','パスワード（SHA-256）'],
            ['is_locked','BOOLEAN','NOT NULL DEFAULT FALSE','アカウントロック'],
            ['created_at','TIMESTAMP','NOT NULL',''],
            ['updated_at','TIMESTAMP','NOT NULL',''],
        ],
        'idx': ['idx_emp_dept ON (dept_code)','idx_emp_name ON (name)','idx_emp_login ON (login_id)']
    },
    'employee_qualifications': {
        'desc': '担当者保有資格',
        'cols': [
            ['id','INT','PK AUTO_INCREMENT',''],
            ['emp_no','VARCHAR(10)','FK→employees',''],
            ['qualification_code','VARCHAR(10)','NOT NULL','資格コード'],
            ['cert_date','VARCHAR(8)','','認定日'],
            ['expire_date','VARCHAR(8)','','有効期限'],
        ],
        'idx': ['idx_qual_emp ON (emp_no)']
    },
    'holidays': {
        'desc': '休日マスタ',
        'cols': [
            ['holiday_id','INT','PK AUTO_INCREMENT',''],
            ['holiday_date','VARCHAR(8)','NOT NULL','日付（YYYYMMDD）'],
            ['holiday_type','VARCHAR(5)','NOT NULL','法定休日/会社指定休日/点検停止日'],
            ['holiday_name','VARCHAR(100)','','休日名称'],
            ['is_transfer','BOOLEAN','NOT NULL DEFAULT FALSE','振替出勤設定あり'],
            ['transfer_date','VARCHAR(8)','','振替出勤日'],
        ],
        'idx': ['idx_hol_date UNIQUE (holiday_date)','idx_hol_type ON (holiday_type)']
    },
    'parts': {
        'desc': '保守部品マスタ',
        'cols': [
            ['part_code','VARCHAR(10)','PK','部品コード'],
            ['part_name','VARCHAR(100)','NOT NULL','部品名'],
            ['part_type','VARCHAR(10)','','部品種別'],
            ['unit','VARCHAR(5)','','単位（個/式/m/kg/L）'],
            ['order_point','INT','','発注点'],
            ['safety_stock','INT','','安全在庫数'],
            ['current_stock','INT','NOT NULL DEFAULT 0','現在庫数'],
            ['unit_price','INT','','単価（円）'],
            ['supplier','VARCHAR(100)','','仕入先'],
            ['note','TEXT','','備考'],
            ['created_at','TIMESTAMP','NOT NULL',''],
            ['updated_at','TIMESTAMP','NOT NULL',''],
        ],
        'idx': ['idx_part_type ON (part_type)']
    },
    'part_equipment_relations': {
        'desc': '部品・適用設備関連',
        'cols': [
            ['id','INT','PK AUTO_INCREMENT',''],
            ['part_code','VARCHAR(10)','FK→parts',''],
            ['equipment_type','VARCHAR(10)','','適用設備種別'],
            ['equipment_code','VARCHAR(10)','FK→equipment','適用設備（NULL=設備種別全体）'],
        ],
        'idx': ['idx_per_part ON (part_code)','idx_per_eqp ON (equipment_code)']
    },
    'part_usages': {
        'desc': '部品使用実績',
        'cols': [
            ['usage_id','INT','PK AUTO_INCREMENT',''],
            ['part_code','VARCHAR(10)','FK→parts',''],
            ['equipment_code','VARCHAR(10)','FK→equipment','使用設備'],
            ['usage_date','VARCHAR(8)','NOT NULL','使用日'],
            ['quantity','INT','NOT NULL','使用量'],
            ['stock_before','INT','NOT NULL','使用前在庫'],
            ['stock_after','INT','NOT NULL','使用後在庫'],
            ['purpose','VARCHAR(3)','NOT NULL','使用目的（点検/修繕）'],
            ['used_by','VARCHAR(10)','FK→employees','使用者'],
            ['order_no','VARCHAR(20)','FK→counter_orders','関連対応指示'],
            ['created_at','TIMESTAMP','NOT NULL',''],
        ],
        'idx': ['idx_usage_date ON (usage_date)','idx_usage_part ON (part_code)','idx_usage_eqp ON (equipment_code)']
    },
}

for tname, tdef in tables.items():
    db_data.append(hdr_row([f'テーブル: {tname}', '', '', '', '', '', '', '']))
    db_data.append([f'説明: {tdef["desc"]}', '', '', '', '', '', '', ''])
    db_data.append(hdr_row(['カラム名', '型', '制約', '説明', '', '', '', '']))
    for col in tdef['cols']:
        db_data.append(list(col) + [''] * (8 - len(col)))
    db_data.append(hdr_row(['インデックス:', ', '.join(tdef['idx']), '', '', '', '', '', '']))
    db_data.append(empty_row())

# リレーション概要
db_data.append(hdr_row(['リレーション概要', '', '', '', '', '', '', '']))
rels = [
    'equipment.parent_equipment_code → equipment.equipment_code（自己参照: 親子関係）',
    'inspection_items.template_id → inspection_template.template_id',
    'inspection_items.parent_item_id → inspection_items.item_id（自己参照: 階層）',
    'inspection_plans.equipment_code → equipment.equipment_code',
    'inspection_plans.template_id → inspection_template.template_id',
    'inspection_plans.person_code → employees.emp_no',
    'inspection_results.plan_id → inspection_plans.plan_id',
    'inspection_results.executed_by → employees.emp_no',
    'inspection_items_results.result_id → inspection_results.result_id',
    'inspection_items_results.item_id → inspection_items.item_id',
    'inspection_photos.result_item_id → inspection_items_results.result_item_id',
    'incidents.result_id → inspection_results.result_id（点検から起票時）',
    'incidents.equipment_code → equipment.equipment_code',
    'incident_timeline.incident_no → incidents.incident_no',
    'incident_attachments.incident_no → incidents.incident_no',
    'counter_orders.incident_no → incidents.incident_no',
    'counter_order_details.order_no → counter_orders.order_no',
    'counter_order_details.person_code → employees.emp_no',
    'counter_order_details.used_part_code → parts.part_code',
    'capa_reports.incident_no → incidents.incident_no',
    'departments.parent_dept_code → departments.dept_code（自己参照: 階層）',
    'employees.dept_code → departments.dept_code',
    'employee_qualifications.emp_no → employees.emp_no',
    'part_equipment_relations.part_code → parts.part_code',
    'part_equipment_relations.equipment_code → equipment.equipment_code',
    'part_usages.part_code → parts.part_code',
    'part_usages.equipment_code → equipment.equipment_code',
    'part_usages.order_no → counter_orders.order_no',
    'part_usages.used_by → employees.emp_no',
]
for r in rels:
    db_data.append([r, '', '', '', '', '', '', ''])

w13 = make_standard_workbook('DB設計書', db_data)
w13.save('13_DB設計書.xlsx')

# ===== 14_共通部品設計書.xlsx =====
common_data = []
common_data.append(hdr_row(['共通部品設計書', '', '', '', '', '', '', '']))
common_data.append(hdr_row(['文書管理情報', '', '', '', '', '', '', '']))
for r in [['システムID', 'STRUTSLAB-001', '', '', '文書番号', 'DOC-COMMON-001', '', ''],
          ['版数', '1.0', '', '', '作成日', '2026/05/23', '', ''],
          ['作成者', 'システム開発部', '', '', '区分', '新規', '', '']]:
    common_data.append(r)
common_data.append(empty_row())

# Tilesレイアウト
common_data.append(hdr_row(['Tilesレイアウト定義', '', '', '', '', '', '', '']))
common_data.append(hdr_row(['テンプレート名', '継承', '属性', '説明', '', '', '', '']))
tiles = [
    ['baseLayout', '(root)', 'header, menu, body, footer', '全画面共通ベースレイアウト'],
    ['masterLayout', 'baseLayout', 'body=menuLayout(menu,content)', 'マスタ管理用（左メニュー）'],
    ['wizardLayout', 'baseLayout', 'body=wizardContent', 'Wizard画面用（ステップ表示付き）'],
    ['listLayout', 'baseLayout', 'body=searchArea+listArea', '一覧画面用（検索＋一覧）'],
    ['inputLayout', 'baseLayout', 'body=formArea', '入力画面用（大フォーム）'],
    ['popupLayout', '(root)', 'header, body', 'ポップアップ画面用（メニューなし）'],
    ['printLayout', '(root)', 'body', '印刷画面用（装飾なし）'],
]
for t in tiles:
    common_data.append(list(t) + [''] * (8 - len(t)))
common_data.append(empty_row())

# 共通JSPインクルード
common_data.append(hdr_row(['共通JSPインクルード', '', '', '', '', '', '', '']))
common_data.append(hdr_row(['ファイル名', 'パス', '役割', '使用画面', '', '', '', '']))
jsp_includes = [
    ['header.jsp', '/WEB-INF/jsp/common/header.jsp', 'タイトルバー、ログインユーザー表示', '全画面'],
    ['menu.jsp', '/WEB-INF/jsp/common/menu.jsp', 'モジュール別メニューリンク', '全画面（popup除く）'],
    ['footer.jsp', '/WEB-INF/jsp/common/footer.jsp', 'コピーライト', '全画面'],
    ['paging.jsp', '/WEB-INF/jsp/common/paging.jsp', 'ページング共通部品', '一覧画面'],
    ['searchForm.jsp', '/WEB-INF/jsp/common/searchForm.jsp', '検索条件エリア', '一覧画面'],
    ['errorMessages.jsp', '/WEB-INF/jsp/common/errorMessages.jsp', '<html:errors/>表示', '入力画面'],
    ['confirmDialog.jsp', '/WEB-INF/jsp/common/confirmDialog.jsp', 'window.confirm用JS（最低限）', '削除/一括操作'],
]
for jsp in jsp_includes:
    common_data.append(list(jsp) + [''] * (8 - len(jsp)))
common_data.append(empty_row())

# カスタムタグ
common_data.append(hdr_row(['カスタムタグ一覧', '', '', '', '', '', '', '']))
common_data.append(hdr_row(['タグ名', 'TLDファイル', 'Javaクラス', '機能', '属性', '使用画面', '', '']))
tags = [
    ['eqp:treeSelect', 'eqp-tree.tld', 'EqpTreeSelectTag', '設備ツリー選択（selectボックス）', 'name, property, equipmentType, level', '設備選択画面'],
    ['date:picker', 'date-picker.tld', 'DatePickerTag', '日付入力補助（text+カレンダーpopup）', 'name, property, format(YYYYMMDD), pastDisabled', '全入力画面'],
    ['app:sectionHeader', 'app-common.tld', 'SectionHeaderTag', 'セクション見出し（アンカー付き）', 'title, anchorId', '大フォーム画面'],
    ['app:statusBadge', 'app-common.tld', 'StatusBadgeTag', 'ステータスバッジ（色付きspan）', 'status, type', '一覧画面'],
    ['app:inspectionChecklist', 'app-common.tld', 'InspectionChecklistTag', '点検チェックリスト（3階層テーブル）', 'name, items, nestedPath', '点検実施入力'],
    ['app:indexedRow', 'app-common.tld', 'IndexedRowTag', '動的行（Indexed Properties反復）', 'name, indexed, collection', '対応指示登録'],
    ['app:timeline', 'app-common.tld', 'TimelineTag', '経過記録タイムライン表示', 'timeline, incidentNo', '異常報告詳細'],
]
for t in tags:
    common_data.append(list(t) + [''] * (8 - len(t)))
common_data.append(empty_row())

# Validatorルール
common_data.append(hdr_row(['Validator定義（validation.xml）', '', '', '', '', '', '', '']))
common_data.append(hdr_row(['Form名', '項目', 'ルール', 'パラメータ', 'エラーメッセージキー', '', '', '']))
vals = [
    ['eqpForm', 'eqpName', 'required', '', 'errors.required'],
    ['eqpForm', 'eqpType', 'required', '', 'errors.required'],
    ['eqpForm', 'eqpName', 'maxlength', '100', 'errors.maxlength'],
    ['eqpForm', 'eqpInterval', 'intRange', 'min=1,max=120', 'errors.range'],
    ['checkItemForm', 'tmplName', 'required', '', 'errors.required'],
    ['execForm', 'execJudge[{i}]', 'required', '', 'errors.required'],
    ['incidentForm', 'incidentType', 'required', '', 'errors.required'],
    ['incidentForm', 'severity', 'required', '', 'errors.required'],
    ['incidentForm', 'incidentPart', 'required', '', 'errors.required'],
    ['incidentForm', 'incidentDetail', 'required', '', 'errors.required'],
    ['incidentForm', 'incidentDetail', 'maxlength', '2000', 'errors.maxlength'],
    ['counterForm', 'details[{n}].workContent', 'required', '', 'errors.required'],
    ['counterForm', 'details[{n}].person', 'required', '', 'errors.required'],
    ['empForm', 'empName', 'required', '', 'errors.required'],
    ['empForm', 'empKana', 'mask', '^[ァ-ヴー\s]+$', 'errors.katakana'],
    ['empForm', 'password', 'minlength', '8', 'errors.minlength'],
    ['empForm', 'loginId', 'required', '', 'errors.required'],
    ['calForm', 'calDateFrom', 'required', '', 'errors.required'],
    ['calForm', 'calDateTo', 'required', '', 'errors.required'],
    ['capaForm', 'why[1]', 'required', '', 'errors.required'],
    ['capaForm', 'why[2]', 'required', '', 'errors.required'],
    ['capaForm', 'why[3]', 'required', '', 'errors.required'],
    ['capaForm', 'why[4]', 'required', '', 'errors.required'],
    ['capaForm', 'why[5]', 'required', '', 'errors.required'],
    ['capaForm', 'countermeasure', 'required', '', 'errors.required'],
]
for v in vals:
    common_data.append(list(v) + [''] * (8 - len(v)))

common_data.append(empty_row())
common_data.append(hdr_row(['MyBatis Mapper一覧', '', '', '', '', '', '', '']))
common_data.append(hdr_row(['Mapperファイル', 'Javaインタフェース', '担当テーブル', '', '', '', '', '']))
mappers = [
    ['EquipmentMapper.xml', 'EquipmentDao.java', 'equipment'],
    ['InspectionTemplateMapper.xml', 'InspectionTemplateDao.java', 'inspection_template, inspection_items'],
    ['InspectionPlanMapper.xml', 'InspectionPlanDao.java', 'inspection_plans'],
    ['InspectionResultMapper.xml', 'InspectionResultDao.java', 'inspection_results, inspection_items_results, inspection_photos'],
    ['IncidentMapper.xml', 'IncidentDao.java', 'incidents, incident_timeline, incident_attachments'],
    ['CounterOrderMapper.xml', 'CounterOrderDao.java', 'counter_orders, counter_order_details'],
    ['CapaMapper.xml', 'CapaDao.java', 'capa_reports'],
    ['DeptMapper.xml', 'DeptDao.java', 'departments'],
    ['EmployeeMapper.xml', 'EmployeeDao.java', 'employees, employee_qualifications'],
    ['CalendarMapper.xml', 'CalendarDao.java', 'holidays'],
    ['PartsMapper.xml', 'PartsDao.java', 'parts, part_equipment_relations, part_usages'],
]
for m in mappers:
    common_data.append(list(m) + [''] * (8 - len(m)))

w14 = make_standard_workbook('共通部品設計書', common_data)
w14.save('14_共通部品設計書.xlsx')

# ===== 15_エラーコード一覧.xlsx =====
err_data = []
err_data.append(hdr_row(['エラーコード一覧', '', '', '', '', '', '', '']))
err_data.append(hdr_row(['文書管理情報', '', '', '', '', '', '', '']))
for r in [['システムID', 'STRUTSLAB-001', '', '', '文書番号', 'DOC-ERR-001', '', ''],
          ['版数', '1.0', '', '', '作成日', '2026/05/23', '', ''],
          ['作成者', 'システム開発部', '', '', '', '', '', '']]:
    err_data.append(r)
err_data.append(empty_row())
err_data.append(hdr_row(['業務エラー', '', '', '', '', '', '', '']))
err_data.append(hdr_row(['エラーコード', '区分', 'メッセージ（ja）', '発生条件', '画面', 'HTTPステータス', '', '']))
biz_errs = [
    ['ERR-BIZ-001','重複','同じ設備コードが既に存在します。','設備コード重複登録','設備マスタ登録','200', ''],
    ['ERR-BIZ-002','重複','同じログインIDが既に使用されています。','ログインID重複','担当者マスタ登録','200', ''],
    ['ERR-BIZ-003','重複','指定範囲に既存の休日が含まれています。','休日重複','休日一括登録','200', ''],
    ['ERR-BIZ-004','データ不存在','指定された設備が見つかりません。','設備コード不在','設備マスタ編集','200', ''],
    ['ERR-BIZ-005','データ不存在','指定された点検計画が見つかりません。','計画ID不在','点検実施入力','200', ''],
    ['ERR-BIZ-006','状態不正','廃止済み設備は親設備に指定できません。','廃止設備を親に指定','設備マスタ登録','200', ''],
    ['ERR-BIZ-007','状態不正','承認済みの点検結果は修正できません。','承認済みデータの修正試行','点検実施修正','200', ''],
    ['ERR-BIZ-008','状態不正','計画がロックされています。','ロック済み計画の編集','年間点検計画','200', ''],
    ['ERR-BIZ-009','在庫不足','使用量が現在庫を超えています。','在庫超え使用','部品使用実績','200', ''],
    ['ERR-BIZ-010','期限切れ','認定有効期限が切れています。','期限切れ資格','担当者マスタ','200', ''],
    ['ERR-BIZ-011','データ不存在','保存された検索条件が見つかりません。','セッション切れ','異常報告一覧','200', ''],
    ['ERR-BIZ-012','操作不正','全明細が完了していないため、指示全体を完了できません。','未了明細あり','対応指示詳細','200', ''],
    ['ERR-BIZ-013','操作不正','差戻し理由が入力されていません。','理由未入力','点検実施承認一覧','200', ''],
    ['ERR-BIZ-014','過去日','過去日は指定できません。','過去日指定','対応指示登録/休日登録','200', ''],
]
for e in biz_errs:
    err_data.append(list(e) + [''] * (8 - len(e)))
err_data.append(empty_row())
err_data.append(hdr_row(['バリデーションエラー', '', '', '', '', '', '', '']))
err_data.append(hdr_row(['エラーコード', '区分', 'メッセージ（ja）', '発生条件', '画面', '', '', '']))
val_errs = [
    ['ERR-VAL-001','必須','{0}は必須です。','required','全入力画面', '', ''],
    ['ERR-VAL-002','最大長','{0}は{1}文字以内で入力してください。','maxlength','全入力画面', '', ''],
    ['ERR-VAL-003','数値範囲','{0}は{1}～{2}の範囲で入力してください。','intRange','数値入力画面', '', ''],
    ['ERR-VAL-004','形式','{0}は{1}の形式で入力してください。','date(YRYYMMDD)','日付入力画面', '', ''],
    ['ERR-VAL-005','形式','フリガナはカタカナで入力してください。','mask(カタカナ)','担当者マスタ', '', ''],
    ['ERR-VAL-006','一致','パスワードが一致しません。','confirm(password)','担当者マスタ', '', ''],
    ['ERR-VAL-007','最小長','パスワードは8文字以上で入力してください。','minlength(8)','担当者マスタ', '', ''],
    ['ERR-VAL-008','条件付き必須','判定が「×」の場合、実測値は必須です。','conditional','点検実施入力', '', ''],
    ['ERR-VAL-009','条件付き必須','判定が「×/△」の場合、所見は必須です。','conditional','点検実施入力', '', ''],
    ['ERR-VAL-010','条件付き必須','推定原因は必須です。','conditional','異常報告詳細', '', ''],
    ['ERR-VAL-011','条件付き必須','対応内容は必須です。','conditional','異常報告詳細', '', ''],
    ['ERR-VAL-012','条件付き必須','修正理由は必須です。','conditional','点検実施修正', '', ''],
    ['ERR-VAL-013','条件付き必須','差戻し理由は必須です。','conditional','点検実施承認一覧', '', ''],
    ['ERR-VAL-014','ファイル','ファイルサイズは{0}MB以下にしてください。','file size','ファイル添付画面', '', ''],
    ['ERR-VAL-015','ファイル','ファイル形式は{0}のみ許可されています。','file ext','ファイル添付画面', '', ''],
    ['ERR-VAL-016','将来日','未来日を指定してください。','future date','点検計画/休日', '', ''],
    ['ERR-VAL-017','範囲','{0}は{1}以上{2}以下で入力してください。','range(date)','日付範囲指定', '', ''],
    ['ERR-VAL-018','行数','明細は1行以上必要です。','min rows','対応指示登録', '', ''],
    ['ERR-VAL-019','行数','明細は最大50行までです。','max rows','対応指示登録', '', ''],
]
for e in val_errs:
    err_data.append(list(e) + [''] * (8 - len(e)))
err_data.append(empty_row())
err_data.append(hdr_row(['システムエラー', '', '', '', '', '', '', '']))
err_data.append(hdr_row(['エラーコード', '区分', 'メッセージ（ja）', '発生条件', 'HTTPステータス', '', '', '']))
sys_errs = [
    ['ERR-SYS-001','DB接続','システムエラーが発生しました。管理者に連絡してください。','DB接続失敗','500', '', ''],
    ['ERR-SYS-002','ファイルIO','ファイルの読み書きに失敗しました。','添付ファイル操作失敗','500', '', ''],
    ['ERR-SYS-003','認証','ログインに失敗しました。IDとパスワードを確認してください。','認証失敗','200', '', ''],
    ['ERR-SYS-004','セッション','セッションが切れました。再度ログインしてください。','セッションタイムアウト','200', '', ''],
    ['ERR-SYS-005','権限','この操作の権限がありません。','権限不足','200', '', ''],
    ['ERR-SYS-006','404','ページが見つかりません。','URL不正','404', '', ''],
    ['ERR-SYS-007','500','予期せぬエラーが発生しました。','未ハンドル例外','500', '', ''],
]
for e in sys_errs:
    err_data.append(list(e) + [''] * (8 - len(e)))

w15 = make_standard_workbook('エラーコード一覧', err_data)
w15.save('15_エラーコード一覧.xlsx')

# ===== 16_UIテスト難点マップ.xlsx =====
test_data = []
test_data.append(hdr_row(['UIテスト難点マップ', '', '', '', '', '', '', '']))
test_data.append(hdr_row(['文書管理情報', '', '', '', '', '', '', '']))
for r in [['システムID', 'STRUTSLAB-001', '', '', '文書番号', 'DOC-TEST-001', '', ''],
          ['版数', '1.0', '', '', '作成日', '2026/05/23', '', ''],
          ['作成者', 'システム開発部', '', '', '目的', '全画面×テスト難点カテゴリのマトリクス', '', '']]:
    test_data.append(r)
test_data.append(empty_row())

# テスト難点カテゴリ説明
test_data.append(hdr_row(['テスト難点カテゴリ', '', '', '', '', '', '', '']))
test_data.append(hdr_row(['カテゴリID', '難点カテゴリ', '説明', 'Seleniumでの困難性', '', '', '', '']))
cats = [
    ['C01','Stale Element','動的行追加/削除（Submit→再描画）で既存要素の参照が無効になる','高: index再振りにより要素再特定が必要'],
    ['C02','Indexed Properties','name属性が details[{n}].field 形式','高: 深いネストと動的indexのロケーターが複雑'],
    ['C03','Wizard','マルチステップのデータ持ち回りと「戻る→修正→進む」','高: 全ステップパスの組み合わせ爆発'],
    ['C04','条件付きバリデーション','状態によって必須項目が変わる','高: 全状態×全項目の組み合わせ検証'],
    ['C05','Popup→親画面反映','window.open→選択→opener値反映','中: ウィンドウハンドル切替'],
    ['C06','File Upload','添付ファイル＋同時バリデーション','中: ファイル選択状態でエラー時のファイル維持確認'],
    ['C07','一括操作+confirm','window.confirmのOK/キャンセル両方テスト','低: alert/confirmハンドリングはSelenium標準'],
    ['C08','動的カラム','期間によりカラム数/内容が変動','中: ヘッダとデータの対応検証が必要'],
    ['C09','ネストテーブル','大分類rowspan→中分類→項目の階層テーブル','中: DOM上フラット、trの親子関係がない'],
    ['C10','印刷用別ウィンドウ','window.open→別ウィンドウの内容検証','中: ハンドル切替＋別DOM検証'],
    ['C11','CSS色分け','ステータスバッジ/セル色のクラス検証','低: CSSクラス名のassertion'],
    ['C12','ページング複合','ページ跨ぎ操作＋全選択＋状態変動','中: ページ遷移後の状態維持確認'],
    ['C13','CSV出力','ダウンロードファイルの内容検証','低: HTTPレスポンス＋ファイルパース'],
    ['C14','セッション切れ','セッションタイムアウト後の挙動','中: タイムアウト待機が必要'],
    ['C15','多段階ステータス遷移','未了→調査中→対応中→完了→再発防止の全パス','高: 全遷移パス＋条件付き必須の組み合わせ'],
]
for c in cats:
    test_data.append(list(c) + [''] * (8 - len(c)))
test_data.append(empty_row())

# マトリクス
test_data.append(hdr_row(['画面×テスト難点マトリクス', '', '', '', '', '', '', '']))
test_data.append(hdr_row(['No.', '画面名', 'C01 Stale', 'C02 Indexed', 'C03 Wizard', 'C04 条件付', 'C05 Popup', 'C06 File', 'C07 Confirm', 'C08 動的列', 'C09 ネスト', 'C10 印刷', 'C11 CSS', 'C12 Paging', 'C13 CSV', 'C14 Session', 'C15 状態遷移']))

matrix = [
    ['1','設備マスタ一覧','','','','','','','○','','','','○','○','○','',''],
    ['2','設備マスタ登録/編集','','','','○','○','○','','','','','','','','',''],
    ['3','点検項目マスタ一覧','○','','','','','','○','','','','○','','','',''],
    ['4','点検項目マスタ登録/編集','○','○','','','','','','','○','','','','','',''],
    ['5','年間点検計画一覧','','','','','','','','○','','','○','','','',''],
    ['6','点検計画登録(Wizard)','','','○','○','','','','','','','','','','○',''],
    ['7','点検実施一覧(当日)','','','','','','','','','','','○','','','',''],
    ['8','点検実施入力','','','','○','','○','','','○','','','','','',''],
    ['9','点検実施詳細/修正','','','','○','','','○','','','','','','','',''],
    ['10','点検実施承認一覧','','','','○','','','○','','','','','○','','',''],
    ['11','異常報告一覧','','','','','','','○','','','','','○','○','○',''],
    ['12','異常報告登録','','','','','','○','','','','','','','','',''],
    ['13','異常報告詳細','','','','○','','','','','','','','','','','○'],
    ['14','対応指示登録','○','○','','','○','','','','','','','','','',''],
    ['15','対応指示一覧','','','','','','','○','','','','○','','○','○','',''],
    ['16','対応指示詳細/完了','○','○','','○','','','','','','','','','','',''],
    ['17','是正処置報告書','','','','','','','','','','','','','','','',''],
    ['18','総合レポート','','','','','','','','','','','○','','','',''],
    ['19','部署マスタ一覧','','','','','','','','','','','','','','',''],
    ['20','部署マスタ登録/編集','','','','','○','','','','','','','','','',''],
    ['21','担当者マスタ一覧','','','','','','','','','','','','','○','',''],
    ['22','担当者マスタ登録/編集','','','','','','','','','','','','','','',''],
    ['23','休日カレンダー一覧','','','','','','','','○','','','○','','','',''],
    ['24','休日登録/編集','','','','','','','','','','','','','','',''],
    ['25','保守部品一覧','','','','','','','','','','','','','','',''],
    ['26','保守部品登録/編集','','','','','','○','','','','','','','','',''],
    ['27','部品使用実績一覧','','','','','','','','','','','','','','',''],
]
for m in matrix:
    test_data.append(list(m) + [''] * (16 - len(m)))

w16 = make_standard_workbook('UIテスト難点マップ', test_data)
w16.save('16_UIテスト難点マップ.xlsx')

print("Done: 13-16")
