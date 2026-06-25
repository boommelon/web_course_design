package util;

/**
 * 系统阶段开关 key 与角色常量集中维护，避免散落在各 Controller。
 * 与 system_settings 表的 setting_key 对应。
 */
public final class Stage {

    private Stage() {}

    // 角色
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_DIRECTOR = "director";
    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_STUDENT = "student";

    // 阶段开关 key
    public static final String TOPIC_SUBMIT_OPEN = "topic_submit_open";       // 教师出题
    public static final String TOPIC_REVIEW_OPEN = "topic_review_open";       // 专业负责人审题
    public static final String SELECTION_OPEN = "selection_open";             // 学生选题
    public static final String CURRENT_ROUND = "current_round";               // 当前轮次 1/2
    public static final String CONFIRM_OPEN = "confirm_open";                 // 专业负责人确认
    public static final String MANUAL_ASSIGN_OPEN = "manual_assign_open";     // 强制分配
    public static final String DOCUMENT_UPLOAD_OPEN = "document_upload_open"; // 资料上传
    public static final String GRADE_OPEN = "grade_open";                     // 成绩评定
    public static final String PROJECT_CLOSED = "project_closed";             // 项目归档
}
