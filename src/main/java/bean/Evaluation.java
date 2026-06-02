package bean;

import java.util.Date;

public class Evaluation {
    private int id;
    private int studentId;
    private int topicId;
    private int teacherId;
    private String selfComment;
    private String peerComment;
    private Integer score;
    private Date createdAt;

    private String studentName;
    private String topicTitle;
    private String teacherName;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getTopicId() { return topicId; }
    public void setTopicId(int topicId) { this.topicId = topicId; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public String getSelfComment() { return selfComment; }
    public void setSelfComment(String selfComment) { this.selfComment = selfComment; }

    public String getPeerComment() { return peerComment; }
    public void setPeerComment(String peerComment) { this.peerComment = peerComment; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
}
