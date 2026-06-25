package bean;

import java.util.Date;

/**
 * 最终分配：一人一题、一题一人的权威结果。
 * source: round1 / round2 / manual。
 */
public class FinalAssignment {
    private int id;
    private int studentId;
    private int topicId;
    private String source;
    private Integer choiceRank;
    private int confirmedBy;
    private Date confirmTime;
    private String confirmComment;

    // 关联展示字段
    private String studentName;
    private String studentNo;
    private String className;
    private String major;
    private String topicTitle;
    private int teacherId;
    private String teacherName;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getTopicId() { return topicId; }
    public void setTopicId(int topicId) { this.topicId = topicId; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Integer getChoiceRank() { return choiceRank; }
    public void setChoiceRank(Integer choiceRank) { this.choiceRank = choiceRank; }

    public int getConfirmedBy() { return confirmedBy; }
    public void setConfirmedBy(int confirmedBy) { this.confirmedBy = confirmedBy; }

    public Date getConfirmTime() { return confirmTime; }
    public void setConfirmTime(Date confirmTime) { this.confirmTime = confirmTime; }

    public String getConfirmComment() { return confirmComment; }
    public void setConfirmComment(String confirmComment) { this.confirmComment = confirmComment; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
}
