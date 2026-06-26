package bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 成绩评定：导师自评 40% + 评阅评分 20% + 答辩成绩 40%。
 * 保留旧 score/comment 字段，作为导师自评的兼容来源。
 */
public class Evaluation {
    private int id;
    private int studentId;
    private int topicId;
    private int teacherId;
    private Integer score;
    private String comment;
    private Integer advisorScore;
    private String advisorComment;
    private Integer reviewerTeacherId;
    private Integer reviewerScore;
    private String reviewerComment;
    private Integer defenseScore;
    private String defenseComment;
    private BigDecimal finalScore;
    private Date createdAt;
    private Date updatedAt;

    private String studentName;
    private String studentNo;
    private String className;
    private String topicTitle;
    private String teacherName;
    private String reviewerTeacherName;
    private List<EvaluationTeacherScore> reviewerScores = new ArrayList<EvaluationTeacherScore>();
    private List<EvaluationTeacherScore> defenseScores = new ArrayList<EvaluationTeacherScore>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getTopicId() { return topicId; }
    public void setTopicId(int topicId) { this.topicId = topicId; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Integer getAdvisorScore() { return advisorScore; }
    public void setAdvisorScore(Integer advisorScore) { this.advisorScore = advisorScore; }

    public String getAdvisorComment() { return advisorComment; }
    public void setAdvisorComment(String advisorComment) { this.advisorComment = advisorComment; }

    public Integer getReviewerTeacherId() { return reviewerTeacherId; }
    public void setReviewerTeacherId(Integer reviewerTeacherId) { this.reviewerTeacherId = reviewerTeacherId; }

    public Integer getReviewerScore() { return reviewerScore; }
    public void setReviewerScore(Integer reviewerScore) { this.reviewerScore = reviewerScore; }

    public String getReviewerComment() { return reviewerComment; }
    public void setReviewerComment(String reviewerComment) { this.reviewerComment = reviewerComment; }

    public Integer getDefenseScore() { return defenseScore; }
    public void setDefenseScore(Integer defenseScore) { this.defenseScore = defenseScore; }

    public String getDefenseComment() { return defenseComment; }
    public void setDefenseComment(String defenseComment) { this.defenseComment = defenseComment; }

    public BigDecimal getFinalScore() { return finalScore; }
    public void setFinalScore(BigDecimal finalScore) { this.finalScore = finalScore; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getReviewerTeacherName() { return reviewerTeacherName; }
    public void setReviewerTeacherName(String reviewerTeacherName) { this.reviewerTeacherName = reviewerTeacherName; }

    public List<EvaluationTeacherScore> getReviewerScores() { return reviewerScores; }
    public void setReviewerScores(List<EvaluationTeacherScore> reviewerScores) { this.reviewerScores = reviewerScores; }

    public List<EvaluationTeacherScore> getDefenseScores() { return defenseScores; }
    public void setDefenseScores(List<EvaluationTeacherScore> defenseScores) { this.defenseScores = defenseScores; }
}
