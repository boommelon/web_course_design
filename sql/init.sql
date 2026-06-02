-- 毕业设计管理系统数据库初始化脚本
-- 使用示例:
-- mysql --default-character-set=utf8mb4 -uroot -p -e "source E:/Desktop/web_course_design/sql/init.sql;"

DROP DATABASE IF EXISTS graduation_design;
CREATE DATABASE graduation_design DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE graduation_design;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    password VARCHAR(64) NOT NULL COMMENT 'MD5加密后的密码',
    name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    role ENUM('admin','teacher','student') NOT NULL COMMENT '角色',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE topics (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '课题名称',
    description TEXT COMMENT '课题描述',
    teacher_id INT NOT NULL COMMENT '指导教师ID',
    max_students INT DEFAULT 1 COMMENT '最大可选人数',
    selected_count INT DEFAULT 0 COMMENT '已选人数',
    status ENUM('open','closed') DEFAULT 'closed' COMMENT '课题开放状态',
    review_status ENUM('pending','approved','rejected') DEFAULT 'pending' COMMENT '管理员审核状态',
    review_comment TEXT COMMENT '审核意见',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);

CREATE TABLE topic_selections (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    topic_id INT NOT NULL,
    reason TEXT COMMENT '申请理由',
    round_no INT DEFAULT 1 COMMENT '选题轮次',
    status ENUM('pending','approved','rejected') DEFAULT 'pending',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (topic_id) REFERENCES topics(id)
);

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

CREATE TABLE system_settings (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(20) NOT NULL,
    setting_label VARCHAR(100) NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

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

CREATE TABLE evaluations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    topic_id INT NOT NULL,
    teacher_id INT NOT NULL,
    self_comment TEXT COMMENT '教师自评意见',
    peer_comment TEXT COMMENT '互评意见',
    score INT COMMENT '综合成绩',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_evaluation_student (student_id),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (topic_id) REFERENCES topics(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);

CREATE TABLE defense_scores (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    topic_id INT NOT NULL,
    teacher_id INT NOT NULL,
    score INT NOT NULL,
    comment TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_defense_student (student_id),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (topic_id) REFERENCES topics(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);

CREATE TABLE announcements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告内容',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 密码说明:
-- admin123 = 0192023a7bbd73250516f069df18b500
-- 123456   = e10adc3949ba59abbe56e057f20f883e

INSERT INTO users (username, password, name, role, email, phone) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '系统管理员', 'admin', 'admin@example.com', '13800000000'),
('teacher01', 'e10adc3949ba59abbe56e057f20f883e', '张老师', 'teacher', 'zhang@example.com', '13800000001'),
('teacher02', 'e10adc3949ba59abbe56e057f20f883e', '李老师', 'teacher', 'li@example.com', '13800000002'),
('student01', 'e10adc3949ba59abbe56e057f20f883e', '王同学', 'student', 'wang@example.com', '13900000001'),
('student02', 'e10adc3949ba59abbe56e057f20f883e', '赵同学', 'student', 'zhao@example.com', '13900000002'),
('student03', 'e10adc3949ba59abbe56e057f20f883e', '刘同学', 'student', 'liu@example.com', '13900000003');

INSERT INTO topics (title, description, teacher_id, max_students, selected_count, status, review_status, review_comment) VALUES
('基于Web的图书管理系统', '设计并实现在线图书管理系统，支持图书增删改查和借阅管理。', 2, 2, 1, 'open', 'approved', '初始化示例课题'),
('学生成绩分析平台', '开发学生成绩数据分析平台，支持成绩录入、统计和可视化展示。', 2, 1, 0, 'open', 'approved', '初始化示例课题'),
('在线考试系统设计与实现', '实现支持自动组卷、在线答题和自动评分的考试系统。', 3, 2, 0, 'open', 'approved', '初始化示例课题'),
('校园二手交易平台', '实现面向校园的二手物品发布、检索和交易管理功能。', 3, 1, 0, 'open', 'pending', '等待管理员审核');

INSERT INTO topic_selections (student_id, topic_id, reason, round_no, status) VALUES
(4, 1, '对Web系统开发和数据库设计感兴趣。', 1, 'approved'),
(5, 2, '希望完成数据统计分析方向的毕业设计。', 1, 'pending');

INSERT INTO documents (student_id, topic_id, reviewer_id, type, file_path, file_name, content, score, feedback, status) VALUES
(4, 1, 2, 'proposal', NULL, NULL, '开题报告摘要示例。', 86, '选题依据和研究内容基本完整。', 'reviewed'),
(4, 1, NULL, 'midterm', NULL, NULL, '中期检查报告摘要示例。', NULL, NULL, 'submitted');

INSERT INTO evaluations (student_id, topic_id, teacher_id, self_comment, peer_comment, score) VALUES
(4, 1, 2, '指导过程按计划推进，学生能够按时完成阶段任务。', '互评认为课题工作量适中，系统实现较完整。', 88);

INSERT INTO defense_scores (student_id, topic_id, teacher_id, score, comment) VALUES
(4, 1, 2, 90, '答辩表达清晰，系统演示完整。');

INSERT INTO system_settings (setting_key, setting_value, setting_label) VALUES
('topic_publish_open', 'true', '教师出题开放'),
('student_selection_open', 'true', '学生选题开放'),
('document_upload_open', 'true', '学生文档上传开放'),
('selection_round', '1', '当前选题轮次'),
('project_closed', 'false', '答辩结束与后期归档');

INSERT INTO tasks (teacher_id, title, content, deadline) VALUES
(2, '开题报告提交任务', '完成开题报告并在系统中上传附件。', '2026-06-20'),
(2, '中期检查任务', '整理中期检查材料并提交中期报告。', '2026-07-10');

INSERT INTO announcements (title, content, is_top) VALUES
('毕业设计工作安排通知', '请各位同学在规定时间内完成选题、材料提交和答辩准备。', 1),
('关于提交开题报告的通知', '选题通过的同学请按时提交开题报告。', 0);
