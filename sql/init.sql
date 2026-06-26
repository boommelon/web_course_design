-- ============================================================
-- 毕业设计管理系统 数据库初始化脚本
-- 业务核心：教师出题 -> 专业负责人审题 -> 学生三志愿选题
--           -> 专业负责人两轮确认 -> 强制分配 -> 资料上传 -> 评分
-- 设计原则：志愿(selection_*) 与 最终分配(final_assignments) 严格分离
--           一人一题、一题一人 由 final_assignments 的唯一约束兜底
-- ============================================================

DROP DATABASE IF EXISTS graduation_design;
CREATE DATABASE graduation_design DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE graduation_design;

-- ------------------------------------------------------------
-- 用户表：四种角色 admin / director / teacher / student
-- director(专业负责人) 与 admin(教务管理员) 严格区分
-- college + major 是后续专业范围控制的基础
-- ------------------------------------------------------------
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    password VARCHAR(64) NOT NULL COMMENT 'MD5加密后的密码',
    name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    role ENUM('admin','director','teacher','student') NOT NULL COMMENT '角色',
    college VARCHAR(100) COMMENT '所属学院(admin 可空)',
    major VARCHAR(100) COMMENT '所属专业(director/student 必填)',
    student_no VARCHAR(30) COMMENT '学号(学生必填)',
    class_name VARCHAR(50) COMMENT '班级(学生必填)',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    status TINYINT DEFAULT 1 COMMENT '是否启用 1启用 0停用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- 题目表：一题一人模型
-- status: draft 草稿 / pending 待审 / approved 审核通过(可选)
--         rejected 退回 / assigned 已最终分配
-- college + major 决定哪些学生 / 哪个专业负责人能看到
-- 审核通过后教师不能再修改(由 Controller + status 校验保证)
-- ------------------------------------------------------------
CREATE TABLE topics (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '课题名称',
    description TEXT COMMENT '课题描述',
    teacher_id INT NOT NULL COMMENT '出题教师ID',
    college VARCHAR(100) NOT NULL COMMENT '题目所属学院',
    major VARCHAR(100) NOT NULL COMMENT '题目所属专业',
    status ENUM('draft','pending','approved','rejected','assigned') NOT NULL DEFAULT 'pending' COMMENT '题目状态',
    review_comment TEXT COMMENT '专业负责人审核意见',
    reviewer_id INT COMMENT '审核人(专业负责人)ID',
    review_time DATETIME COMMENT '审核时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id),
    FOREIGN KEY (reviewer_id) REFERENCES users(id)
);

-- ------------------------------------------------------------
-- 选题申请批次：一个学生一轮一条
-- round: 1 第一轮 / 2 第二轮
-- 同一学生同一轮只能有一个申请批次 -> UNIQUE(student_id, round)
-- ------------------------------------------------------------
CREATE TABLE selection_applications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL COMMENT '学生ID',
    round INT NOT NULL COMMENT '轮次 1 或 2',
    status ENUM('submitted','confirmed','rejected','expired') NOT NULL DEFAULT 'submitted' COMMENT '批次状态',
    submit_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_student_round (student_id, round),
    FOREIGN KEY (student_id) REFERENCES users(id)
);

-- ------------------------------------------------------------
-- 志愿明细：一个申请批次最多 3 条(choice_rank 1/2/3)
-- 同一批次不能重复选同一题目 -> UNIQUE(application_id, topic_id)
-- 同一批次志愿顺序不能重复     -> UNIQUE(application_id, choice_rank)
-- status: pending 待确认 / selected 被选中 / not_selected 未选中
-- ------------------------------------------------------------
CREATE TABLE selection_choices (
    id INT PRIMARY KEY AUTO_INCREMENT,
    application_id INT NOT NULL COMMENT '所属申请批次',
    student_id INT NOT NULL COMMENT '冗余学生ID 便于查询',
    topic_id INT NOT NULL COMMENT '志愿题目',
    choice_rank INT NOT NULL COMMENT '志愿顺序 1/2/3',
    round INT NOT NULL COMMENT '冗余轮次 便于按题目按轮统计意向人数',
    status ENUM('pending','selected','not_selected') NOT NULL DEFAULT 'pending' COMMENT '志愿状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_app_rank (application_id, choice_rank),
    UNIQUE KEY uk_app_topic (application_id, topic_id),
    FOREIGN KEY (application_id) REFERENCES selection_applications(id),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (topic_id) REFERENCES topics(id)
);

-- ------------------------------------------------------------
-- 最终分配表：唯一权威的"一人一题"结果
-- UNIQUE(student_id) -> 一个学生最终只能有一个题目
-- UNIQUE(topic_id)   -> 一个题目最终只能分配给一个学生
-- source: round1 / round2 / manual(强制分配)
-- ------------------------------------------------------------
CREATE TABLE final_assignments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL COMMENT '学生ID',
    topic_id INT NOT NULL COMMENT '最终题目ID',
    source ENUM('round1','round2','manual') NOT NULL COMMENT '分配来源',
    choice_rank INT COMMENT '若来源于志愿 记录第几志愿',
    confirmed_by INT NOT NULL COMMENT '专业负责人ID',
    confirm_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    confirm_comment VARCHAR(255) COMMENT '确认说明',
    UNIQUE KEY uk_final_student (student_id),
    UNIQUE KEY uk_final_topic (topic_id),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (topic_id) REFERENCES topics(id),
    FOREIGN KEY (confirmed_by) REFERENCES users(id)
);

-- ------------------------------------------------------------
-- 资料文档：必须基于 final_assignments(没有最终题目不能上传)
-- ------------------------------------------------------------
CREATE TABLE documents (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    topic_id INT NOT NULL,
    reviewer_id INT COMMENT '审核教师ID',
    type ENUM('proposal','midterm','final','source') NOT NULL COMMENT '文档类型',
    file_path VARCHAR(500) COMMENT '文件路径',
    file_name VARCHAR(255) COMMENT '原始文件名',
    content TEXT COMMENT '文档内容摘要',
    score INT COMMENT '评分',
    feedback TEXT COMMENT '教师反馈',
    status ENUM('submitted','reviewed','rejected') DEFAULT 'submitted',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (topic_id) REFERENCES topics(id),
    FOREIGN KEY (reviewer_id) REFERENCES users(id)
);

-- ------------------------------------------------------------
-- 成绩评定：导师自评 40% + 评阅评分 20% + 答辩成绩 40%
-- 保留 score/comment 旧字段，兼容旧的单一导师评分页面数据
-- 一个学生一条
-- ------------------------------------------------------------
CREATE TABLE evaluations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    topic_id INT NOT NULL,
    teacher_id INT NOT NULL COMMENT '指导教师',
    score INT COMMENT '旧字段：导师自评成绩',
    comment TEXT COMMENT '旧字段：导师评语',
    advisor_score INT COMMENT '导师自评成绩',
    advisor_comment TEXT COMMENT '导师评语',
    reviewer_teacher_id INT COMMENT '评阅教师ID',
    reviewer_score INT COMMENT '评阅成绩',
    reviewer_comment TEXT COMMENT '评阅评语',
    defense_score INT COMMENT '答辩成绩',
    defense_comment TEXT COMMENT '答辩评语',
    final_score DECIMAL(5,2) COMMENT '最终成绩',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_evaluation_student (student_id),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (topic_id) REFERENCES topics(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id),
    FOREIGN KEY (reviewer_teacher_id) REFERENCES users(id)
);

-- ------------------------------------------------------------
-- 评阅/答辩教师分配与评分明细
-- reviewer: 互评教师，slot_no 固定为 1
-- defense: 答辩教师，slot_no 为 1/2/3，三位教师评分取平均作为答辩分
-- ------------------------------------------------------------
CREATE TABLE evaluation_teacher_scores (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    topic_id INT NOT NULL,
    teacher_id INT NOT NULL COMMENT '评分教师',
    score_type ENUM('reviewer','defense') NOT NULL COMMENT '评分类型',
    slot_no INT NOT NULL DEFAULT 1 COMMENT '同类型第几位教师',
    score INT COMMENT '评分',
    comment TEXT COMMENT '评语',
    assigned_by INT NOT NULL COMMENT '分配人(专业负责人)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_eval_score_slot (student_id, score_type, slot_no),
    UNIQUE KEY uk_eval_score_teacher (student_id, score_type, teacher_id),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (topic_id) REFERENCES topics(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id),
    FOREIGN KEY (assigned_by) REFERENCES users(id)
);

-- ------------------------------------------------------------
-- 系统阶段开关 / 流程状态
-- ------------------------------------------------------------
CREATE TABLE system_settings (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(20) NOT NULL,
    setting_label VARCHAR(100) NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- 非核心模块(锦上添花)：公告 / 任务 / 教师资源 / 模板 / 答疑
-- ------------------------------------------------------------
CREATE TABLE tasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    teacher_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    deadline DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);

CREATE TABLE teacher_resources (
    id INT PRIMARY KEY AUTO_INCREMENT,
    teacher_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);

CREATE TABLE file_templates (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    teacher_id INT NOT NULL,
    topic_id INT,
    question TEXT NOT NULL,
    answer TEXT,
    status ENUM('pending','answered') DEFAULT 'pending',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    answered_at DATETIME,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id),
    FOREIGN KEY (topic_id) REFERENCES topics(id)
);

CREATE TABLE announcements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告内容',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 初始化数据
-- 演示场景：计算机学院 软件工程 + 计算机科学与技术 两个专业
-- 密码统一为 123456 (MD5: e10adc3949ba59abbe56e057f20f883e)
-- admin 密码 admin123 (MD5: 0192023a7bbd73250516f069df18b500)
-- ============================================================

-- 教务管理员(不绑定专业)
INSERT INTO users (username, password, name, role, college, major, student_no, class_name, email, phone) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '教务管理员', 'admin', NULL, NULL, NULL, NULL, 'admin@example.com', '13800000000');

-- 专业负责人(每个专业一个)
INSERT INTO users (username, password, name, role, college, major, student_no, class_name, email, phone) VALUES
('director_se', 'e10adc3949ba59abbe56e057f20f883e', '软工负责人(周主任)', 'director', '计算机学院', '软件工程', NULL, NULL, 'dir_se@example.com', '13800001001'),
('director_cs', 'e10adc3949ba59abbe56e057f20f883e', '计科负责人(吴主任)', 'director', '计算机学院', '计算机科学与技术', NULL, NULL, 'dir_cs@example.com', '13800001002');

-- 教师
INSERT INTO users (username, password, name, role, college, major, student_no, class_name, email, phone) VALUES
('teacher01', 'e10adc3949ba59abbe56e057f20f883e', '张老师', 'teacher', '计算机学院', '软件工程', NULL, NULL, 'zhang@example.com', '13800002001'),
('teacher02', 'e10adc3949ba59abbe56e057f20f883e', '李老师', 'teacher', '计算机学院', '软件工程', NULL, NULL, 'li@example.com', '13800002002'),
('teacher03', 'e10adc3949ba59abbe56e057f20f883e', '陈老师', 'teacher', '计算机学院', '计算机科学与技术', NULL, NULL, 'chen@example.com', '13800002003'),
('teacher04', 'e10adc3949ba59abbe56e057f20f883e', '钱老师', 'teacher', '计算机学院', '软件工程', NULL, NULL, 'qian@example.com', '13800002004'),
('teacher05', 'e10adc3949ba59abbe56e057f20f883e', '郑老师', 'teacher', '计算机学院', '软件工程', NULL, NULL, 'zheng@example.com', '13800002005'),
('teacher06', 'e10adc3949ba59abbe56e057f20f883e', '冯老师', 'teacher', '计算机学院', '计算机科学与技术', NULL, NULL, 'feng@example.com', '13800002006'),
('teacher07', 'e10adc3949ba59abbe56e057f20f883e', '蒋老师', 'teacher', '计算机学院', '计算机科学与技术', NULL, NULL, 'jiang@example.com', '13800002007');

-- 学生(软件工程 4 人)
INSERT INTO users (username, password, name, role, college, major, student_no, class_name, email, phone) VALUES
('student01', 'e10adc3949ba59abbe56e057f20f883e', '王同学', 'student', '计算机学院', '软件工程', '2021001', '软工2101', 'wang@example.com', '13900000001'),
('student02', 'e10adc3949ba59abbe56e057f20f883e', '赵同学', 'student', '计算机学院', '软件工程', '2021002', '软工2101', 'zhao@example.com', '13900000002'),
('student03', 'e10adc3949ba59abbe56e057f20f883e', '刘同学', 'student', '计算机学院', '软件工程', '2021003', '软工2101', 'liu@example.com', '13900000003'),
('student04', 'e10adc3949ba59abbe56e057f20f883e', '孙同学', 'student', '计算机学院', '软件工程', '2021004', '软工2102', 'sun@example.com', '13900000004');

-- 学生(计算机科学与技术 2 人)
INSERT INTO users (username, password, name, role, college, major, student_no, class_name, email, phone) VALUES
('student05', 'e10adc3949ba59abbe56e057f20f883e', '周同学', 'student', '计算机学院', '计算机科学与技术', '2021101', '计科2101', 'zhou@example.com', '13900000005'),
('student06', 'e10adc3949ba59abbe56e057f20f883e', '吴同学', 'student', '计算机学院', '计算机科学与技术', '2021102', '计科2101', 'wu@example.com', '13900000006');

-- 题目：软件工程专业(teacher_id 4=张, 5=李)
INSERT INTO topics (title, description, teacher_id, college, major, status, review_comment, reviewer_id, review_time) VALUES
('基于Web的图书管理系统', '设计并实现在线图书管理系统，支持图书增删改查和借阅管理。', 4, '计算机学院', '软件工程', 'approved', '选题合适，通过。', 2, NOW()),
('学生成绩分析平台', '开发学生成绩数据分析平台，支持成绩录入、统计和可视化展示。', 4, '计算机学院', '软件工程', 'approved', '通过。', 2, NOW()),
('在线考试系统设计与实现', '实现支持自动组卷、在线答题和自动评分的考试系统。', 5, '计算机学院', '软件工程', 'approved', '通过。', 2, NOW()),
('校园二手交易平台', '实现面向校园的二手物品发布、检索和交易管理功能。', 5, '计算机学院', '软件工程', 'pending', NULL, NULL, NULL);

-- 题目：计算机科学与技术专业(teacher_id 6=陈)
INSERT INTO topics (title, description, teacher_id, college, major, status, review_comment, reviewer_id, review_time) VALUES
('分布式爬虫系统设计', '设计并实现可扩展的分布式网络爬虫系统。', 6, '计算机学院', '计算机科学与技术', 'approved', '通过。', 3, NOW()),
('基于深度学习的图像分类', '研究并实现常见卷积神经网络在图像分类任务上的应用。', 6, '计算机学院', '计算机科学与技术', 'pending', NULL, NULL, NULL);

-- 系统阶段开关：初始停在第一轮选题阶段，便于演示完整流程
INSERT INTO system_settings (setting_key, setting_value, setting_label) VALUES
('topic_submit_open',     'false', '教师出题开放'),
('topic_review_open',     'false', '专业负责人审题开放'),
('selection_open',        'true',  '学生选题开放'),
('current_round',         '1',     '当前选题轮次(1/2)'),
('confirm_open',          'false', '专业负责人选题确认开放'),
('manual_assign_open',    'false', '强制分配开放'),
('document_upload_open',  'false', '资料上传开放'),
('grade_open',            'false', '成绩评定开放'),
('project_closed',        'false', '项目归档(关闭全部阶段)');

-- 公告(非核心)
INSERT INTO announcements (title, content, is_top) VALUES
('毕业设计工作安排通知', '请各位同学在规定时间内完成选题、材料提交和答辩准备。', 1),
('关于第一轮选题的通知', '第一轮选题已开放，请各位同学填报 1-3 个志愿。', 0);
