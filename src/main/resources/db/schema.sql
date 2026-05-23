-- Equipment Master
CREATE TABLE equipment (
    equipment_code VARCHAR(10) PRIMARY KEY,
    equipment_name VARCHAR(100) NOT NULL,
    equipment_type VARCHAR(10) NOT NULL,
    voltage_level VARCHAR(10),
    rated_capacity INT,
    rated_current INT,
    frequency VARCHAR(5),
    parent_equipment_code VARCHAR(10),
    install_date VARCHAR(6),
    location_address VARCHAR(200),
    coordinates VARCHAR(50),
    maintenance_rank CHAR(1) NOT NULL,
    inspection_interval INT,
    last_inspection_date VARCHAR(8),
    next_inspection_date VARCHAR(8),
    status VARCHAR(5) NOT NULL DEFAULT '運用中',
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inspection Template
CREATE TABLE inspection_template (
    template_id INT AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(100) NOT NULL,
    equipment_type VARCHAR(10) NOT NULL,
    inspection_kind VARCHAR(5) NOT NULL,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inspection Items (3-level hierarchy)
CREATE TABLE inspection_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    template_id INT NOT NULL,
    parent_item_id INT,
    item_level INT NOT NULL,
    item_name VARCHAR(200) NOT NULL,
    judge_criteria VARCHAR(10),
    normal_range VARCHAR(100),
    unit VARCHAR(20),
    sort_order INT DEFAULT 0
);

-- Inspection Plans
CREATE TABLE inspection_plans (
    plan_id INT AUTO_INCREMENT PRIMARY KEY,
    fiscal_year VARCHAR(4) NOT NULL,
    equipment_code VARCHAR(10) NOT NULL,
    template_id INT,
    planned_date VARCHAR(8) NOT NULL,
    team_code VARCHAR(10),
    person_code VARCHAR(10),
    status VARCHAR(10) NOT NULL DEFAULT '予定',
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inspection Results (header)
CREATE TABLE inspection_results (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    plan_id INT NOT NULL,
    executed_date VARCHAR(8) NOT NULL,
    executed_by VARCHAR(10),
    summary_judge VARCHAR(5),
    summary_note TEXT,
    next_recommended_date VARCHAR(8),
    approval_status VARCHAR(5),
    reject_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inspection Item Results (detail)
CREATE TABLE inspection_items_results (
    result_item_id INT AUTO_INCREMENT PRIMARY KEY,
    result_id INT NOT NULL,
    item_id INT NOT NULL,
    judge CHAR(1) NOT NULL,
    measured_value VARCHAR(50),
    note TEXT
);

-- Inspection Photos
CREATE TABLE inspection_photos (
    photo_id INT AUTO_INCREMENT PRIMARY KEY,
    result_item_id INT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    original_name VARCHAR(200),
    file_size BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Incidents
CREATE TABLE incidents (
    incident_no VARCHAR(20) PRIMARY KEY,
    result_id INT,
    incident_datetime TIMESTAMP NOT NULL,
    finder VARCHAR(50) NOT NULL,
    equipment_code VARCHAR(10) NOT NULL,
    weather VARCHAR(5),
    temperature INT,
    incident_type VARCHAR(10) NOT NULL,
    severity VARCHAR(5) NOT NULL,
    incident_part VARCHAR(200) NOT NULL,
    incident_detail TEXT NOT NULL,
    tmp_action TEXT,
    tmp_action_person VARCHAR(10),
    tmp_action_date VARCHAR(8),
    cause TEXT,
    counter_detail TEXT,
    status VARCHAR(10) NOT NULL DEFAULT '未了',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Incident Timeline
CREATE TABLE incident_timeline (
    timeline_id INT AUTO_INCREMENT PRIMARY KEY,
    incident_no VARCHAR(20) NOT NULL,
    action_datetime TIMESTAMP NOT NULL,
    action_user VARCHAR(50) NOT NULL,
    action_content TEXT NOT NULL,
    status_from VARCHAR(10),
    status_to VARCHAR(10)
);

-- Incident Attachments
CREATE TABLE incident_attachments (
    attachment_id INT AUTO_INCREMENT PRIMARY KEY,
    incident_no VARCHAR(20) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    original_name VARCHAR(200),
    file_size BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Counter Orders (header)
CREATE TABLE counter_orders (
    order_no VARCHAR(20) PRIMARY KEY,
    incident_no VARCHAR(20),
    order_date VARCHAR(8) NOT NULL,
    issuer VARCHAR(50) NOT NULL,
    overall_deadline VARCHAR(8),
    overall_priority VARCHAR(3) NOT NULL,
    status VARCHAR(5) NOT NULL DEFAULT '未了',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Counter Order Details
CREATE TABLE counter_order_details (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(20) NOT NULL,
    seq_no INT NOT NULL,
    work_content VARCHAR(500) NOT NULL,
    person_code VARCHAR(10),
    deadline VARCHAR(8),
    priority VARCHAR(3),
    status VARCHAR(3) NOT NULL DEFAULT '未了',
    actual_hours DECIMAL(5,1),
    used_part_code VARCHAR(10),
    used_quantity INT,
    note TEXT
);

-- CAPA Reports
CREATE TABLE capa_reports (
    capa_id INT AUTO_INCREMENT PRIMARY KEY,
    incident_no VARCHAR(20) NOT NULL,
    why1 TEXT NOT NULL,
    why2 TEXT NOT NULL,
    why3 TEXT NOT NULL,
    why4 TEXT NOT NULL,
    why5 TEXT NOT NULL,
    countermeasure TEXT NOT NULL,
    verify_method TEXT NOT NULL,
    verify_deadline VARCHAR(8) NOT NULL,
    status VARCHAR(5) NOT NULL DEFAULT '申請中',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Departments (hierarchical)
CREATE TABLE departments (
    dept_code VARCHAR(10) PRIMARY KEY,
    dept_name VARCHAR(100) NOT NULL,
    parent_dept_code VARCHAR(10),
    dept_level INT NOT NULL,
    dept_type VARCHAR(5) NOT NULL,
    start_date VARCHAR(8) NOT NULL,
    end_date VARCHAR(8),
    address VARCHAR(200),
    tel VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Employees
CREATE TABLE employees (
    emp_no VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    name_kana VARCHAR(100),
    birth_date VARCHAR(8),
    join_date VARCHAR(6),
    dept_code VARCHAR(10),
    position VARCHAR(10),
    assign_date VARCHAR(8),
    inspection_rank CHAR(1),
    inspection_cert_date VARCHAR(8),
    inspection_cert_expire VARCHAR(8),
    login_id VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Employee Qualifications
CREATE TABLE employee_qualifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    emp_no VARCHAR(10) NOT NULL,
    qualification_code VARCHAR(10) NOT NULL,
    cert_date VARCHAR(8),
    expire_date VARCHAR(8)
);

-- Holidays
CREATE TABLE holidays (
    holiday_id INT AUTO_INCREMENT PRIMARY KEY,
    holiday_date VARCHAR(8) NOT NULL UNIQUE,
    holiday_type VARCHAR(10) NOT NULL,
    holiday_name VARCHAR(100),
    is_transfer BOOLEAN NOT NULL DEFAULT FALSE,
    transfer_date VARCHAR(8)
);

-- Parts
CREATE TABLE parts (
    part_code VARCHAR(10) PRIMARY KEY,
    part_name VARCHAR(100) NOT NULL,
    part_type VARCHAR(10),
    unit VARCHAR(5),
    order_point INT,
    safety_stock INT,
    current_stock INT NOT NULL DEFAULT 0,
    unit_price INT,
    supplier VARCHAR(100),
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Part-Equipment Relations
CREATE TABLE part_equipment_relations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    part_code VARCHAR(10) NOT NULL,
    equipment_type VARCHAR(10),
    equipment_code VARCHAR(10)
);

-- Part Usages
CREATE TABLE part_usages (
    usage_id INT AUTO_INCREMENT PRIMARY KEY,
    part_code VARCHAR(10) NOT NULL,
    equipment_code VARCHAR(10),
    usage_date VARCHAR(8) NOT NULL,
    quantity INT NOT NULL,
    stock_before INT NOT NULL,
    stock_after INT NOT NULL,
    purpose VARCHAR(3) NOT NULL,
    used_by VARCHAR(10),
    order_no VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- Foreign Key Constraints
-- ========================
ALTER TABLE equipment ADD CONSTRAINT fk_eqp_parent FOREIGN KEY (parent_equipment_code) REFERENCES equipment(equipment_code);
ALTER TABLE inspection_items ADD CONSTRAINT fk_item_template FOREIGN KEY (template_id) REFERENCES inspection_template(template_id);
ALTER TABLE inspection_items ADD CONSTRAINT fk_item_parent FOREIGN KEY (parent_item_id) REFERENCES inspection_items(item_id);
ALTER TABLE inspection_plans ADD CONSTRAINT fk_plan_equipment FOREIGN KEY (equipment_code) REFERENCES equipment(equipment_code);
ALTER TABLE inspection_plans ADD CONSTRAINT fk_plan_template FOREIGN KEY (template_id) REFERENCES inspection_template(template_id);
ALTER TABLE inspection_plans ADD CONSTRAINT fk_plan_person FOREIGN KEY (person_code) REFERENCES employees(emp_no);
ALTER TABLE inspection_results ADD CONSTRAINT fk_res_plan FOREIGN KEY (plan_id) REFERENCES inspection_plans(plan_id);
ALTER TABLE inspection_results ADD CONSTRAINT fk_res_person FOREIGN KEY (executed_by) REFERENCES employees(emp_no);
ALTER TABLE inspection_items_results ADD CONSTRAINT fk_iir_result FOREIGN KEY (result_id) REFERENCES inspection_results(result_id);
ALTER TABLE inspection_items_results ADD CONSTRAINT fk_iir_item FOREIGN KEY (item_id) REFERENCES inspection_items(item_id);
ALTER TABLE inspection_photos ADD CONSTRAINT fk_photo_item FOREIGN KEY (result_item_id) REFERENCES inspection_items_results(result_item_id);
ALTER TABLE incidents ADD CONSTRAINT fk_inc_result FOREIGN KEY (result_id) REFERENCES inspection_results(result_id);
ALTER TABLE incidents ADD CONSTRAINT fk_inc_equipment FOREIGN KEY (equipment_code) REFERENCES equipment(equipment_code);
ALTER TABLE incidents ADD CONSTRAINT fk_inc_person FOREIGN KEY (tmp_action_person) REFERENCES employees(emp_no);
ALTER TABLE incident_timeline ADD CONSTRAINT fk_tl_inc FOREIGN KEY (incident_no) REFERENCES incidents(incident_no);
ALTER TABLE incident_attachments ADD CONSTRAINT fk_att_inc FOREIGN KEY (incident_no) REFERENCES incidents(incident_no);
ALTER TABLE counter_orders ADD CONSTRAINT fk_ctr_inc FOREIGN KEY (incident_no) REFERENCES incidents(incident_no);
ALTER TABLE counter_order_details ADD CONSTRAINT fk_ctrd_order FOREIGN KEY (order_no) REFERENCES counter_orders(order_no);
ALTER TABLE counter_order_details ADD CONSTRAINT fk_ctrd_person FOREIGN KEY (person_code) REFERENCES employees(emp_no);
ALTER TABLE counter_order_details ADD CONSTRAINT fk_ctrd_part FOREIGN KEY (used_part_code) REFERENCES parts(part_code);
ALTER TABLE capa_reports ADD CONSTRAINT fk_capa_inc FOREIGN KEY (incident_no) REFERENCES incidents(incident_no);
ALTER TABLE departments ADD CONSTRAINT fk_dept_parent FOREIGN KEY (parent_dept_code) REFERENCES departments(dept_code);
ALTER TABLE employees ADD CONSTRAINT fk_emp_dept FOREIGN KEY (dept_code) REFERENCES departments(dept_code);
ALTER TABLE employee_qualifications ADD CONSTRAINT fk_qual_emp FOREIGN KEY (emp_no) REFERENCES employees(emp_no);
ALTER TABLE part_equipment_relations ADD CONSTRAINT fk_per_part FOREIGN KEY (part_code) REFERENCES parts(part_code);
ALTER TABLE part_equipment_relations ADD CONSTRAINT fk_per_eqp FOREIGN KEY (equipment_code) REFERENCES equipment(equipment_code);
ALTER TABLE part_usages ADD CONSTRAINT fk_usage_part FOREIGN KEY (part_code) REFERENCES parts(part_code);
ALTER TABLE part_usages ADD CONSTRAINT fk_usage_eqp FOREIGN KEY (equipment_code) REFERENCES equipment(equipment_code);
ALTER TABLE part_usages ADD CONSTRAINT fk_usage_person FOREIGN KEY (used_by) REFERENCES employees(emp_no);
ALTER TABLE part_usages ADD CONSTRAINT fk_usage_order FOREIGN KEY (order_no) REFERENCES counter_orders(order_no);

-- ========================
-- Indexes
-- ========================
CREATE INDEX idx_eqp_type ON equipment(equipment_type);
CREATE INDEX idx_eqp_rank ON equipment(maintenance_rank);
CREATE INDEX idx_eqp_status ON equipment(status);
CREATE INDEX idx_item_tmpl ON inspection_items(template_id);
CREATE INDEX idx_plan_year ON inspection_plans(fiscal_year);
CREATE INDEX idx_plan_date ON inspection_plans(planned_date);
CREATE INDEX idx_res_date ON inspection_results(executed_date);
CREATE INDEX idx_res_approval ON inspection_results(approval_status);
CREATE INDEX idx_inc_status ON incidents(status);
CREATE INDEX idx_inc_date ON incidents(incident_datetime);
CREATE INDEX idx_inc_type ON incidents(incident_type);
CREATE INDEX idx_inc_severity ON incidents(severity);
CREATE INDEX idx_ctr_status ON counter_orders(status);
CREATE INDEX idx_dept_parent ON departments(parent_dept_code);
CREATE INDEX idx_emp_dept ON employees(dept_code);
CREATE INDEX idx_emp_login ON employees(login_id);
CREATE INDEX idx_hol_date ON holidays(holiday_date);
CREATE INDEX idx_hol_type ON holidays(holiday_type);
CREATE INDEX idx_part_type ON parts(part_type);
CREATE INDEX idx_usage_date ON part_usages(usage_date);
CREATE INDEX idx_usage_part ON part_usages(part_code);
