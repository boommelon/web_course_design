package bean;

import java.util.Date;
import java.util.List;

/**
 * 选题申请批次：一个学生一轮一条。
 * status: submitted / confirmed / rejected / expired。
 */
public class SelectionApplication {
    private int id;
    private int studentId;
    private int round;
    private String status;
    private Date submitTime;

    // 关联展示字段
    private String studentName;
    private String studentNo;
    private String className;
    private List<SelectionChoice> choices;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getRound() { return round; }
    public void setRound(int round) { this.round = round; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getSubmitTime() { return submitTime; }
    public void setSubmitTime(Date submitTime) { this.submitTime = submitTime; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public List<SelectionChoice> getChoices() { return choices; }
    public void setChoices(List<SelectionChoice> choices) { this.choices = choices; }
}
