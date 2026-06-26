package dao;

import bean.Evaluation;
import bean.EvaluationTeacherScore;
import util.SQLHelper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 成绩评定数据访问。
 * 新模型使用 advisor/reviewer/defense/final 字段，旧 score/comment 映射为导师自评。
 */
public class EvaluationDao {

    private static final String SELECT_FROM_ASSIGNMENTS =
            "SELECT e.id, fa.student_id, fa.topic_id, t.teacher_id, "
          + "e.score, e.comment, "
          + "COALESCE(e.advisor_score, e.score) AS advisor_score, "
          + "COALESCE(e.advisor_comment, e.comment) AS advisor_comment, "
          + "e.reviewer_teacher_id, e.reviewer_score, e.reviewer_comment, "
          + "e.defense_score, e.defense_comment, e.final_score, e.created_at, e.updated_at, "
          + "s.name AS student_name, s.student_no, s.class_name, "
          + "t.title AS topic_title, te.name AS teacher_name, rte.name AS reviewer_teacher_name "
          + "FROM final_assignments fa "
          + "JOIN users s ON fa.student_id=s.id "
          + "JOIN topics t ON fa.topic_id=t.id "
          + "JOIN users te ON t.teacher_id=te.id "
          + "LEFT JOIN evaluations e ON e.student_id=fa.student_id "
          + "LEFT JOIN users rte ON e.reviewer_teacher_id=rte.id ";

    private static final String SELECT_FROM_EVALUATIONS =
            "SELECT e.id, e.student_id, e.topic_id, e.teacher_id, "
          + "e.score, e.comment, "
          + "COALESCE(e.advisor_score, e.score) AS advisor_score, "
          + "COALESCE(e.advisor_comment, e.comment) AS advisor_comment, "
          + "e.reviewer_teacher_id, e.reviewer_score, e.reviewer_comment, "
          + "e.defense_score, e.defense_comment, e.final_score, e.created_at, e.updated_at, "
          + "s.name AS student_name, s.student_no, s.class_name, "
          + "t.title AS topic_title, te.name AS teacher_name, rte.name AS reviewer_teacher_name "
          + "FROM evaluations e "
          + "JOIN users s ON e.student_id=s.id "
          + "JOIN topics t ON e.topic_id=t.id "
          + "JOIN users te ON e.teacher_id=te.id "
          + "LEFT JOIN users rte ON e.reviewer_teacher_id=rte.id ";

    public List<Evaluation> findAdvisorByTeacher(int teacherId) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(
                SELECT_FROM_ASSIGNMENTS + "WHERE t.teacher_id=? ORDER BY fa.id DESC", teacherId);
        try {
            return toList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public List<Evaluation> findReviewerByTeacher(int teacherId) throws SQLException {
        return findByAssignedScoreTeacher(teacherId, "reviewer");
    }

    public List<Evaluation> findDefenseByTeacher(int teacherId) throws SQLException {
        return findByAssignedScoreTeacher(teacherId, "defense");
    }

    /** 兼容旧调用：返回该教师作为导师或评阅教师关联的成绩。 */
    public List<Evaluation> findByTeacher(int teacherId) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(
                SELECT_FROM_ASSIGNMENTS + "WHERE t.teacher_id=? OR e.reviewer_teacher_id=? ORDER BY fa.id DESC",
                teacherId, teacherId);
        try {
            return toList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public List<Evaluation> findByMajor(String college, String major) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(
                SELECT_FROM_ASSIGNMENTS + "WHERE s.college=? AND s.major=? ORDER BY fa.id DESC",
                college, major);
        try {
            List<Evaluation> list = toList(rs);
            attachTeacherScores(list);
            return list;
        } finally {
            SQLHelper.close(rs);
        }
    }

    public List<Evaluation> findAll() throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(SELECT_FROM_ASSIGNMENTS + "ORDER BY fa.id DESC");
        try {
            return toList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public Evaluation findByStudent(int studentId) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(
                SELECT_FROM_ASSIGNMENTS + "WHERE fa.student_id=?", studentId);
        try {
            if (rs.next()) {
                Evaluation evaluation = row(rs);
                attachTeacherScores(evaluation);
                return evaluation;
            }
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

    public boolean saveAdvisorScore(int studentId, int teacherId, int score, String comment) throws SQLException {
        validateScore(score);
        if (!isAdvisorForStudent(studentId, teacherId)) {
            return false;
        }
        String sql = "INSERT INTO evaluations(student_id,topic_id,teacher_id,score,comment,advisor_score,advisor_comment) "
                + "SELECT fa.student_id, fa.topic_id, t.teacher_id, ?, ?, ?, ? "
                + "FROM final_assignments fa JOIN topics t ON fa.topic_id=t.id "
                + "WHERE fa.student_id=? AND t.teacher_id=? "
                + "ON DUPLICATE KEY UPDATE topic_id=VALUES(topic_id), teacher_id=VALUES(teacher_id), "
                + "score=VALUES(score), comment=VALUES(comment), "
                + "advisor_score=VALUES(advisor_score), advisor_comment=VALUES(advisor_comment), updated_at=NOW()";
        SQLHelper.executeUpdate(sql, score, comment, score, comment, studentId, teacherId);
        updateFinalScore(studentId);
        return true;
    }

    /** 旧接口兼容：保存为导师自评。 */
    public void save(int studentId, int topicId, int teacherId, int score, String comment) throws SQLException {
        saveAdvisorScore(studentId, teacherId, score, comment);
    }

    public boolean assignReviewer(int studentId, int reviewerTeacherId, int directorId, String college, String major)
            throws SQLException {
        boolean ok = assignScoreTeacher(studentId, reviewerTeacherId, "reviewer", 1, directorId, college, major);
        if (ok) {
            syncSummaryFromTeacherScores(studentId);
        }
        return ok;
    }

    public boolean assignDefenseTeachers(int studentId, int teacherId1, int teacherId2, int teacherId3,
                                         int directorId, String college, String major) throws SQLException {
        if (teacherId1 == teacherId2 || teacherId1 == teacherId3 || teacherId2 == teacherId3) {
            return false;
        }
        if (!canAssignScoreTeacher(studentId, teacherId1, college, major)
                || !canAssignScoreTeacher(studentId, teacherId2, college, major)
                || !canAssignScoreTeacher(studentId, teacherId3, college, major)) {
            return false;
        }
        assignScoreTeacherInternal(studentId, teacherId1, "defense", 1, directorId);
        assignScoreTeacherInternal(studentId, teacherId2, "defense", 2, directorId);
        assignScoreTeacherInternal(studentId, teacherId3, "defense", 3, directorId);
        syncSummaryFromTeacherScores(studentId);
        return true;
    }

    public boolean saveReviewerScore(int studentId, int reviewerTeacherId, int score, String comment)
            throws SQLException {
        return saveTeacherScore(studentId, reviewerTeacherId, "reviewer", score, comment);
    }

    public boolean saveDefenseTeacherScore(int studentId, int teacherId, int score, String comment)
            throws SQLException {
        return saveTeacherScore(studentId, teacherId, "defense", score, comment);
    }

    public boolean saveDefenseScore(int studentId, int score, String comment, String college, String major)
            throws SQLException {
        validateScore(score);
        if (!isStudentAssignedInMajor(studentId, college, major)) {
            return false;
        }
        String sql = "INSERT INTO evaluations(student_id,topic_id,teacher_id,defense_score,defense_comment) "
                + "SELECT fa.student_id, fa.topic_id, t.teacher_id, ?, ? "
                + "FROM final_assignments fa JOIN users s ON fa.student_id=s.id JOIN topics t ON fa.topic_id=t.id "
                + "WHERE fa.student_id=? AND s.college=? AND s.major=? "
                + "ON DUPLICATE KEY UPDATE topic_id=VALUES(topic_id), teacher_id=VALUES(teacher_id), "
                + "defense_score=VALUES(defense_score), defense_comment=VALUES(defense_comment), updated_at=NOW()";
        SQLHelper.executeUpdate(sql, score, comment, studentId, college, major);
        updateFinalScore(studentId);
        return true;
    }

    public int count() throws SQLException {
        ResultSet rs = SQLHelper.executeQuery("SELECT COUNT(*) FROM evaluations");
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    private void validateScore(int score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("成绩必须在 0-100 之间");
        }
    }

    private void updateFinalScore(int studentId) throws SQLException {
        String sql = "UPDATE evaluations SET final_score = CASE "
                + "WHEN COALESCE(advisor_score, score) IS NOT NULL "
                + "AND reviewer_score IS NOT NULL AND defense_score IS NOT NULL "
                + "THEN ROUND(COALESCE(advisor_score, score) * 0.4 + reviewer_score * 0.2 + defense_score * 0.4, 2) "
                + "ELSE NULL END, updated_at=NOW() WHERE student_id=?";
        SQLHelper.executeUpdate(sql, studentId);
    }

    private List<Evaluation> findByAssignedScoreTeacher(int teacherId, String scoreType) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(
                SELECT_FROM_ASSIGNMENTS
                        + "WHERE EXISTS (SELECT 1 FROM evaluation_teacher_scores ets "
                        + "WHERE ets.student_id=fa.student_id AND ets.teacher_id=? AND ets.score_type=?) "
                        + "ORDER BY fa.id DESC",
                teacherId, scoreType);
        try {
            List<Evaluation> list = toList(rs);
            attachTeacherScores(list);
            return list;
        } finally {
            SQLHelper.close(rs);
        }
    }

    private boolean assignScoreTeacher(int studentId, int teacherId, String scoreType, int slotNo, int directorId,
                                       String college, String major) throws SQLException {
        if (!canAssignScoreTeacher(studentId, teacherId, college, major)) {
            return false;
        }
        assignScoreTeacherInternal(studentId, teacherId, scoreType, slotNo, directorId);
        return true;
    }

    private boolean canAssignScoreTeacher(int studentId, int teacherId, String college, String major) throws SQLException {
        if (!isStudentAssignedInMajor(studentId, college, major)) {
            return false;
        }
        if (!isTeacherInMajor(teacherId, college, major)) {
            return false;
        }
        return !isAdvisorForStudent(studentId, teacherId);
    }

    private void assignScoreTeacherInternal(int studentId, int teacherId, String scoreType, int slotNo, int directorId)
            throws SQLException {
        String sql = "INSERT INTO evaluation_teacher_scores(student_id,topic_id,teacher_id,score_type,slot_no,assigned_by) "
                + "SELECT fa.student_id, fa.topic_id, ?, ?, ?, ? FROM final_assignments fa WHERE fa.student_id=? "
                + "ON DUPLICATE KEY UPDATE "
                + "score=IF(teacher_id <=> VALUES(teacher_id), score, NULL), "
                + "comment=IF(teacher_id <=> VALUES(teacher_id), comment, NULL), "
                + "teacher_id=VALUES(teacher_id), topic_id=VALUES(topic_id), assigned_by=VALUES(assigned_by), updated_at=NOW()";
        SQLHelper.executeUpdate(sql, teacherId, scoreType, slotNo, directorId, studentId);
    }

    private boolean saveTeacherScore(int studentId, int teacherId, String scoreType, int score, String comment)
            throws SQLException {
        validateScore(score);
        String sql = "UPDATE evaluation_teacher_scores SET score=?, comment=?, updated_at=NOW() "
                + "WHERE student_id=? AND teacher_id=? AND score_type=?";
        int rows = SQLHelper.executeUpdate(sql, score, comment, studentId, teacherId, scoreType);
        if (rows == 0) {
            return false;
        }
        syncSummaryFromTeacherScores(studentId);
        return true;
    }

    private void syncSummaryFromTeacherScores(int studentId) throws SQLException {
        String sql = "UPDATE evaluations e SET "
                + "reviewer_teacher_id=(SELECT teacher_id FROM evaluation_teacher_scores WHERE student_id=e.student_id AND score_type='reviewer' AND slot_no=1 LIMIT 1), "
                + "reviewer_score=(SELECT AVG(score) FROM evaluation_teacher_scores WHERE student_id=e.student_id AND score_type='reviewer' AND score IS NOT NULL), "
                + "reviewer_comment=(SELECT comment FROM evaluation_teacher_scores WHERE student_id=e.student_id AND score_type='reviewer' AND slot_no=1 LIMIT 1), "
                + "defense_score=(SELECT CASE WHEN COUNT(score)=3 THEN ROUND(AVG(score), 0) ELSE NULL END FROM evaluation_teacher_scores WHERE student_id=e.student_id AND score_type='defense'), "
                + "defense_comment=(SELECT GROUP_CONCAT(CONCAT(ts.name, ': ', COALESCE(ets.comment, '')) ORDER BY ets.slot_no SEPARATOR '\\n') "
                + "FROM evaluation_teacher_scores ets JOIN users ts ON ets.teacher_id=ts.id WHERE ets.student_id=e.student_id AND ets.score_type='defense' AND ets.score IS NOT NULL), "
                + "updated_at=NOW() WHERE e.student_id=?";
        ensureEvaluationRow(studentId);
        SQLHelper.executeUpdate(sql, studentId);
        updateFinalScore(studentId);
    }

    private void ensureEvaluationRow(int studentId) throws SQLException {
        String sql = "INSERT INTO evaluations(student_id,topic_id,teacher_id) "
                + "SELECT fa.student_id, fa.topic_id, t.teacher_id "
                + "FROM final_assignments fa JOIN topics t ON fa.topic_id=t.id "
                + "WHERE fa.student_id=? "
                + "ON DUPLICATE KEY UPDATE topic_id=VALUES(topic_id), teacher_id=VALUES(teacher_id), updated_at=NOW()";
        SQLHelper.executeUpdate(sql, studentId);
    }

    private boolean isAdvisorForStudent(int studentId, int teacherId) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(
                "SELECT COUNT(*) FROM final_assignments fa JOIN topics t ON fa.topic_id=t.id "
                        + "WHERE fa.student_id=? AND t.teacher_id=?",
                studentId, teacherId);
        try {
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            SQLHelper.close(rs);
        }
    }

    private boolean isReviewerForStudent(int studentId, int teacherId) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(
                "SELECT COUNT(*) FROM evaluation_teacher_scores WHERE student_id=? AND teacher_id=? AND score_type='reviewer'",
                studentId, teacherId);
        try {
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            SQLHelper.close(rs);
        }
    }

    private void attachTeacherScores(Evaluation evaluation) throws SQLException {
        List<Evaluation> list = new ArrayList<Evaluation>();
        list.add(evaluation);
        attachTeacherScores(list);
    }

    private void attachTeacherScores(List<Evaluation> evaluations) throws SQLException {
        if (evaluations == null || evaluations.isEmpty()) {
            return;
        }
        Map<Integer, Evaluation> byStudent = new HashMap<Integer, Evaluation>();
        StringBuilder ids = new StringBuilder();
        for (int i = 0; i < evaluations.size(); i++) {
            Evaluation evaluation = evaluations.get(i);
            byStudent.put(Integer.valueOf(evaluation.getStudentId()), evaluation);
            if (i > 0) {
                ids.append(',');
            }
            ids.append(evaluation.getStudentId());
        }
        ResultSet rs = SQLHelper.executeQuery(
                "SELECT ets.*, s.name AS student_name, s.student_no, t.title AS topic_title, "
                        + "advisor.name AS advisor_name, teacher.name AS teacher_name "
                        + "FROM evaluation_teacher_scores ets "
                        + "JOIN users s ON ets.student_id=s.id "
                        + "JOIN topics t ON ets.topic_id=t.id "
                        + "JOIN users advisor ON t.teacher_id=advisor.id "
                        + "JOIN users teacher ON ets.teacher_id=teacher.id "
                        + "WHERE ets.student_id IN (" + ids.toString() + ") ORDER BY ets.score_type, ets.slot_no");
        try {
            while (rs.next()) {
                EvaluationTeacherScore score = teacherScoreRow(rs);
                Evaluation evaluation = byStudent.get(Integer.valueOf(score.getStudentId()));
                if (evaluation != null) {
                    if ("reviewer".equals(score.getScoreType())) {
                        evaluation.getReviewerScores().add(score);
                    } else if ("defense".equals(score.getScoreType())) {
                        evaluation.getDefenseScores().add(score);
                    }
                }
            }
        } finally {
            SQLHelper.close(rs);
        }
    }

    private EvaluationTeacherScore teacherScoreRow(ResultSet rs) throws SQLException {
        EvaluationTeacherScore score = new EvaluationTeacherScore();
        score.setId(rs.getInt("id"));
        score.setStudentId(rs.getInt("student_id"));
        score.setTopicId(rs.getInt("topic_id"));
        score.setTeacherId(rs.getInt("teacher_id"));
        score.setScoreType(rs.getString("score_type"));
        score.setSlotNo(rs.getInt("slot_no"));
        score.setScore(getNullableInt(rs, "score"));
        score.setComment(rs.getString("comment"));
        score.setAssignedBy(rs.getInt("assigned_by"));
        score.setCreatedAt(rs.getTimestamp("created_at"));
        score.setUpdatedAt(rs.getTimestamp("updated_at"));
        score.setStudentName(rs.getString("student_name"));
        score.setStudentNo(rs.getString("student_no"));
        score.setTopicTitle(rs.getString("topic_title"));
        score.setAdvisorName(rs.getString("advisor_name"));
        score.setTeacherName(rs.getString("teacher_name"));
        return score;
    }

    private boolean isStudentAssignedInMajor(int studentId, String college, String major) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(
                "SELECT COUNT(*) FROM final_assignments fa JOIN users s ON fa.student_id=s.id "
                        + "WHERE fa.student_id=? AND s.college=? AND s.major=?",
                studentId, college, major);
        try {
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            SQLHelper.close(rs);
        }
    }

    private boolean isTeacherInMajor(int teacherId, String college, String major) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(
                "SELECT COUNT(*) FROM users WHERE id=? AND role='teacher' AND college=? AND major=? AND status=1",
                teacherId, college, major);
        try {
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            SQLHelper.close(rs);
        }
    }

    private List<Evaluation> toList(ResultSet rs) throws SQLException {
        List<Evaluation> list = new ArrayList<Evaluation>();
        while (rs.next()) {
            list.add(row(rs));
        }
        return list;
    }

    private Evaluation row(ResultSet rs) throws SQLException {
        Evaluation e = new Evaluation();
        e.setId(getNullableInt(rs, "id", 0));
        e.setStudentId(rs.getInt("student_id"));
        e.setTopicId(rs.getInt("topic_id"));
        e.setTeacherId(rs.getInt("teacher_id"));
        e.setScore(getNullableInt(rs, "score"));
        e.setComment(rs.getString("comment"));
        e.setAdvisorScore(getNullableInt(rs, "advisor_score"));
        e.setAdvisorComment(rs.getString("advisor_comment"));
        e.setReviewerTeacherId(getNullableInt(rs, "reviewer_teacher_id"));
        e.setReviewerScore(getNullableInt(rs, "reviewer_score"));
        e.setReviewerComment(rs.getString("reviewer_comment"));
        e.setDefenseScore(getNullableInt(rs, "defense_score"));
        e.setDefenseComment(rs.getString("defense_comment"));
        BigDecimal finalScore = rs.getBigDecimal("final_score");
        if (!rs.wasNull()) {
            e.setFinalScore(finalScore);
        }
        e.setCreatedAt(rs.getTimestamp("created_at"));
        e.setUpdatedAt(rs.getTimestamp("updated_at"));
        e.setStudentName(rs.getString("student_name"));
        e.setStudentNo(rs.getString("student_no"));
        e.setClassName(rs.getString("class_name"));
        e.setTopicTitle(rs.getString("topic_title"));
        e.setTeacherName(rs.getString("teacher_name"));
        e.setReviewerTeacherName(rs.getString("reviewer_teacher_name"));
        return e;
    }

    private Integer getNullableInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : Integer.valueOf(value);
    }

    private int getNullableInt(ResultSet rs, String column, int def) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? def : value;
    }
}
