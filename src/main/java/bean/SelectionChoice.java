package bean;

import java.util.Date;

/**
 * 志愿明细：一个申请批次最多 3 条。
 * choice_rank 1/2/3；status: pending / selected / not_selected。
 */
public class SelectionChoice {
    private int id;
    private int applicationId;
    private int studentId;
    private int topicId;
    private int choiceRank;
    private int round;
    private String status;
    private Date createdAt;

    // 关联展示字段
    private String studentName;
    private String studentNo;
    private String className;
    private String topicTitle;
    private int teacherId;
    private String teacherName;
    private boolean studentAssigned; // 该学生是否已被最终分配
    private boolean topicAssigned;   // 该题目是否已被最终分配

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getTopicId() { return topicId; }
    public void setTopicId(int topicId) { this.topicId = topicId; }

    public int getChoiceRank() { return choiceRank; }
    public void setChoiceRank(int choiceRank) { this.choiceRank = choiceRank; }

    public int getRound() { return round; }
    public void setRound(int round) { this.round = round; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public boolean isStudentAssigned() { return studentAssigned; }
    public void setStudentAssigned(boolean studentAssigned) { this.studentAssigned = studentAssigned; }

    public boolean isTopicAssigned() { return topicAssigned; }
    public void setTopicAssigned(boolean topicAssigned) { this.topicAssigned = topicAssigned; }
}
