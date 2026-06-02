package bean;

import java.util.Date;

/**
 * 课题实体类
 * 对应数据库topics表
 */
public class Topic {
    private int id;
    private String title;       // 课题名称
    private String description; // 课题描述
    private int teacherId;      // 指导教师ID
    private String teacherName; // 教师姓名（关联查询用）
    private int maxStudents;    // 最大可选人数
    private int selectedCount;  // 已选人数
    private String status;      // 状态: open/closed
    private String reviewStatus; // 审核状态: pending/approved/rejected
    private String reviewComment; // 审核意见
    private Date createdAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public int getMaxStudents() { return maxStudents; }
    public void setMaxStudents(int maxStudents) { this.maxStudents = maxStudents; }

    public int getSelectedCount() { return selectedCount; }
    public void setSelectedCount(int selectedCount) { this.selectedCount = selectedCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }

    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
