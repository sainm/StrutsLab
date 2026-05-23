-- Sample departments (4-level hierarchy)
INSERT INTO departments (dept_code, dept_name, parent_dept_code, dept_level, dept_type, start_date) VALUES
('HONSHA', '本社', NULL, 1, '本社', '20000101'),
('TOHOKU', '東北支社', 'HONSHA', 2, '支社', '20000101'),
('KANTO', '関東支社', 'HONSHA', 2, '支社', '20000101'),
('TOHOKU-MIYAGI', '宮城営業所', 'TOHOKU', 3, '営業所', '20000101'),
('TOHOKU-FUKUSHIMA', '福島営業所', 'TOHOKU', 3, '営業所', '20000101'),
('KANTO-TOKYO', '東京営業所', 'KANTO', 3, '営業所', '20000101'),
('MIYAGI-SENDAI', '仙台出張所', 'TOHOKU-MIYAGI', 4, '出張所', '20100101'),
('TOKYO-SHINJUKU', '新宿出張所', 'KANTO-TOKYO', 4, '出張所', '20100101');

-- Sample employees
INSERT INTO employees (emp_no, name, name_kana, dept_code, position, login_id, password_hash, inspection_rank) VALUES
('EMP-0001', '山田 太郎', 'ヤマダ タロウ', 'TOHOKU-MIYAGI', '係長', 'yamada', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'A'),
('EMP-0002', '佐藤 花子', 'サトウ ハナコ', 'KANTO-TOKYO', '主任', 'sato', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'B'),
('EMP-0003', '鈴木 一郎', 'スズキ イチロウ', 'TOHOKU-FUKUSHIMA', '一般', 'suzuki', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'C');

-- Sample equipment
INSERT INTO equipment (equipment_code, equipment_name, equipment_type, voltage_level, maintenance_rank, status) VALUES
('TR-0001', '南変電所 #1 変圧器', '変圧器', '154kV', 'S', '運用中'),
('TR-0002', '南変電所 #2 変圧器', '変圧器', '154kV', 'A', '運用中'),
('CB-0001', '北変電所 #1 遮断器', '遮断器', '275kV', 'S', '運用中'),
('CB-0002', '北変電所 #2 遮断器', '遮断器', '275kV', 'B', '運用中'),
('DS-0001', '東開閉所 #1 開閉器', '開閉器', '66kV', 'A', '運用中'),
('CB-0003', '西変電所 #3 遮断器', '遮断器', '154kV', 'C', '廃止'),
('TR-0003', '南変電所 #3 変圧器', '変圧器', '154kV', 'A', '停止中'),
('CBL-0001', '南-北線 ケーブル', 'ケーブル', '275kV', 'S', '運用中'),
('RY-0001', '北変電所 保護継電器', '保護継電器', '275kV', 'S', '運用中'),
('VT-0001', '東開閉所 計器用変成器', '計器用変成器', '66kV', 'B', '運用中');

-- Sample inspection template
INSERT INTO inspection_template (template_name, equipment_type, inspection_kind, sort_order) VALUES
('変圧器 定期点検', '変圧器', '定期', 1),
('遮断器 定期点検', '遮断器', '定期', 2),
('変圧器 日常点検', '変圧器', '日常', 3);

-- Sample inspection items (3 levels for template 1)
INSERT INTO inspection_items (item_id, template_id, parent_item_id, item_level, item_name, judge_criteria, normal_range, unit, sort_order) VALUES
(1, 1, NULL, 1, '外観点検', NULL, NULL, NULL, 1),
(2, 1, NULL, 1, '絶縁油点検', NULL, NULL, NULL, 2),
(3, 1, NULL, 1, '電気的特性', NULL, NULL, NULL, 3),
(4, 1, 1, 2, 'ブッシング部', NULL, NULL, NULL, 1),
(5, 1, 1, 2, '本体外観', NULL, NULL, NULL, 2),
(6, 1, 4, 3, 'き裂の有無', 'ONLY_O', NULL, NULL, 1),
(7, 1, 4, 3, '汚損状況', 'O_X_TRI', NULL, NULL, 2),
(8, 1, 5, 3, '塗装剥離', 'O_X_TRI', NULL, NULL, 1),
(9, 1, 5, 3, '腐食の有無', 'O_X_TRI', NULL, NULL, 2),
(10, 1, 5, 3, '漏油の有無', 'ONLY_O', NULL, NULL, 3),
(11, 1, 2, 2, '油面レベル', NULL, NULL, NULL, 1),
(12, 1, 2, 2, '油性状', NULL, NULL, NULL, 2),
(13, 1, 11, 3, '油面計指示値', 'O_X_TRI', '50～90', '%', 1),
(14, 1, 11, 3, '油漏れ痕跡', 'ONLY_O', NULL, NULL, 2),
(15, 1, 12, 3, '絶縁油耐圧試験', 'O_X_TRI', '30以上', 'kV/2.5mm', 1),
(16, 1, 12, 3, '油中ガス分析', 'O_X_TRI', NULL, NULL, 2),
(17, 1, 3, 2, '絶縁抵抗', NULL, NULL, NULL, 1),
(18, 1, 3, 2, '巻線抵抗', NULL, NULL, NULL, 2),
(19, 1, 17, 3, '一次-二次間', 'O_X_TRI', '1000以上', 'MΩ', 1),
(20, 1, 17, 3, '一次-接地間', 'O_X_TRI', '1000以上', 'MΩ', 2),
(21, 1, 18, 3, '一次巻線', 'O_X_TRI', NULL, 'Ω', 1),
(22, 1, 18, 3, '二次巻線', 'O_X_TRI', NULL, 'Ω', 2);

-- Sample parts
INSERT INTO parts (part_code, part_name, part_type, unit, order_point, safety_stock, current_stock, unit_price, supplier) VALUES
('P-GSK-001', 'ガスケット A型', 'ガスケット', '個', 10, 5, 25, 500, '株式会社 電材商事'),
('P-BLT-001', '六角ボルト M12x50', 'ボルト', '個', 50, 20, 200, 50, '株式会社 電材商事'),
('P-OIL-001', '絶縁油 高圧用', '絶縁油', 'L', 100, 50, 500, 300, '日本絶縁油株式会社'),
('P-BSH-001', 'ブッシング 66kV用', 'ブッシング', '式', 2, 1, 5, 150000, '高圧機器工業株式会社');

-- Sample holiday
INSERT INTO holidays (holiday_date, holiday_type, holiday_name, is_transfer) VALUES
('20260101', '法定休日', '元日', FALSE),
('20260112', '法定休日', '成人の日', FALSE),
('20260211', '法定休日', '建国記念の日', FALSE),
('20260429', '法定休日', '昭和の日', FALSE),
('20260503', '法定休日', '憲法記念日', FALSE),
('20260504', '法定休日', 'みどりの日', FALSE),
('20260505', '法定休日', 'こどもの日', FALSE),
('20261231', '会社指定休日', '年末休暇', FALSE);
