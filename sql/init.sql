-- 毕业设计管理系统 数据库初始化脚本
-- 使用方法: mysql --default-character-set=utf8mb4 -uroot -pzk406521 -e "source sql/init.sql"

DROP DATABASE IF EXISTS graduation_design;
CREATE DATABASE graduation_design DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE graduation_design;

-- 用户表（管理员、教师、学生共用一张表，通过role字段区分）
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

-- 课题表
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

-- 选题申请表
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

-- 文档表（开题报告、中期检查、终稿）
CREATE TABLE documents (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    topic_id INT NOT NULL,
    reviewer_id INT COMMENT '审核教师ID',
    type ENUM('proposal','midterm','final') NOT NULL COMMENT '文档类型',
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

-- 系统流程开关表
CREATE TABLE system_settings (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(20) NOT NULL,
    setting_label VARCHAR(100) NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 教师发布的阶段任务
CREATE TABLE tasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    teacher_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    deadline DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);

-- 教师上传的参考资料
CREATE TABLE teacher_resources (
    id INT PRIMARY KEY AUTO_INCREMENT,
    teacher_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);

-- 管理员维护的文件模板
CREATE TABLE file_templates (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 学生提问与教师答疑
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

-- 答辩成绩
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

-- 公告表
CREATE TABLE announcements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告内容',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶 1是0否',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ========== 插入测试数据 ==========
-- 密码说明: admin123的MD5值为0192023a7bbd73250516f069df18b500
--           123456的MD5值为e10adc3949ba59abbe56e057f20f883e

INSERT INTO users (username, password, name, role, email) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '系统管理员', 'admin', 'admin@example.com'),
('teacher01', 'e10adc3949ba59abbe56e057f20f883e', '张老师', 'teacher', 'zhang@example.com'),
('teacher02', 'e10adc3949ba59abbe56e057f20f883e', '李老师', 'teacher', 'li@example.com'),
('student01', 'e10adc3949ba59abbe56e057f20f883e', '王同学', 'student', 'wang@example.com'),
('student02', 'e10adc3949ba59abbe56e057f20f883e', '赵同学', 'student', 'zhao@example.com'),
('student03', 'e10adc3949ba59abbe56e057f20f883e', '刘同学', 'student', 'liu@example.com');

INSERT INTO topics (title, description, teacher_id, max_students, status, review_status, review_comment) VALUES
('基于Web的图书管理系统', '设计并实现一个在线图书管理系统，支持图书的增删改查和借阅管理。', 2, 2, 'open', 'approved', '初始化示例课题'),
('学生成绩分析平台', '开发一个学生成绩数据分析平台，支持成绩录入、统计和可视化展示。', 2, 1, 'open', 'approved', '初始化示例课题'),
('在线考试系统设计与实现', '实现一个支持自动组卷、在线答题和自动评分的考试系统。', 3, 2, 'open', 'approved', '初始化示例课题');

INSERT INTO system_settings (setting_key, setting_value, setting_label) VALUES
('topic_publish_open', 'true', '教师出题开放'),
('student_selection_open', 'true', '学生选题开放'),
('document_upload_open', 'true', '学生文档上传开放'),
('selection_round', '1', '当前选题轮次');

INSERT INTO tasks (teacher_id, title, content, deadline) VALUES
(2, '开题报告提交任务', '完成开题报告并在系统中上传附件。', '2026-06-20'),
(2, '中期检查任务', '整理中期检查材料并提交中期报告。', '2026-07-10');

INSERT INTO announcements (title, content, is_top) VALUES
('2024届毕业设计工作安排通知', '请各位同学在规定时间内完成选题，逾期将无法参加本届毕业设计。', 1),
('关于提交开题报告的通知', '选题通过的同学请在两周内提交开题报告。', 0);
