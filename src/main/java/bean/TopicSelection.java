package bean;

import java.util.Date;

/**
 * 选题申请实体类
 * 对应数据库topic_selections表
 */
public class TopicSelection {
    private int id;
    private int studentId;
    private int topicId;
    private String reason;      // 申请理由
    private int roundNo;        // 选题轮次
    private String status;      // 状态: pending/approved/rejected
    private Date createdAt;
    // 下面是关联查询用的字段
    private String studentName;
    private String topicTitle;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getTopicId() { return topicId; }
    public void setTopicId(int topicId) { this.topicId = topicId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public int getRoundNo() { return roundNo; }
    public void setRoundNo(int roundNo) { this.roundNo = roundNo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }
}
