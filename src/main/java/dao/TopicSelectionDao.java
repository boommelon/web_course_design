package dao;

import bean.TopicSelection;
import util.SQLHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

 



public class TopicSelectionDao {

    
    private String baseSql = "SELECT ts.*, u.name AS student_name, t.title AS topic_title "
            + "FROM topic_selections ts "
            + "JOIN users u ON ts.student_id = u.id "
            + "JOIN topics t ON ts.topic_id = t.id ";

     


    public List<TopicSelection> findByStudent(int studentId) throws SQLException {
        String sql = baseSql + "WHERE ts.student_id = ? ORDER BY ts.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql, studentId);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

     


    public List<TopicSelection> findByTeacher(int teacherId) throws SQLException {
        String sql = baseSql + "WHERE t.teacher_id = ? ORDER BY ts.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql, teacherId);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

     


    public List<TopicSelection> findAll() throws SQLException {
        String sql = baseSql + "ORDER BY ts.round_no DESC, ts.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

     


    public List<TopicSelection> findPendingByTeacher(int teacherId) throws SQLException {
        String sql = baseSql + "WHERE t.teacher_id = ? AND ts.status = 'pending' ORDER BY ts.id";
        ResultSet rs = SQLHelper.executeQuery(sql, teacherId);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

     


    public List<TopicSelection> findApprovedByTeacher(int teacherId) throws SQLException {
        String sql = baseSql + "WHERE t.teacher_id = ? AND ts.status = 'approved' ORDER BY ts.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql, teacherId);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

     


    public void insert(int studentId, int topicId, String reason) throws SQLException {
        insert(studentId, topicId, reason, 1, "pending");
    }

     


    public void insert(int studentId, int topicId, String reason, int roundNo, String status) throws SQLException {
        String sql = "INSERT INTO topic_selections(student_id, topic_id, reason, round_no, status) VALUES(?,?,?,?,?)";
        SQLHelper.executeUpdate(sql, studentId, topicId, reason, roundNo, status);
    }

     



    public boolean confirmRoundSelections(int studentId, List<Integer> topicIds, String reason, int roundNo) throws SQLException {
        Set<Integer> uniqueTopicIds = new LinkedHashSet<Integer>();
        if (topicIds != null) {
            for (Integer topicId : topicIds) {
                if (topicId != null && topicId.intValue() > 0) {
                    uniqueTopicIds.add(topicId);
                }
            }
        }
        if (uniqueTopicIds.isEmpty()) {
            return false;
        }

        Connection conn = SQLHelper.getConnection();
        try {
            conn.setAutoCommit(false);

            if (hasApprovedSelection(conn, studentId) || hasSelectionInRound(conn, studentId, roundNo)) {
                conn.rollback();
                return false;
            }

            int approvedTopicId = 0;
            for (Integer topicId : uniqueTopicIds) {
                String status = "rejected";
                String savedReason = reason;

                if (approvedTopicId == 0 && occupyTopicQuota(conn, topicId.intValue())) {
                    status = "approved";
                    approvedTopicId = topicId.intValue();
                } else if (approvedTopicId > 0) {
                    savedReason = appendReasonNote(reason, "已确认其他课题，本志愿自动失效");
                } else {
                    savedReason = appendReasonNote(reason, "课题已满或暂不可选，系统自动驳回");
                }

                insert(conn, studentId, topicId.intValue(), savedReason, roundNo, status);
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

     


    public boolean approvePendingSelection(int selectionId) throws SQLException {
        return approvePendingSelection(selectionId, null);
    }

     


    public boolean approvePendingSelection(int selectionId, Integer teacherId) throws SQLException {
        Connection conn = SQLHelper.getConnection();
        try {
            conn.setAutoCommit(false);

            SelectionLock lock = lockSelection(conn, selectionId);
            if (lock == null
                    || !"pending".equals(lock.status)
                    || (teacherId != null && lock.teacherId != teacherId.intValue())
                    || hasApprovedSelection(conn, lock.studentId)) {
                conn.rollback();
                return false;
            }

            if (!occupyTopicQuota(conn, lock.topicId)) {
                conn.rollback();
                return false;
            }

            updateStatus(conn, selectionId, "approved");
            rejectOtherPendingSelections(conn, lock.studentId, selectionId);
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

     


    public boolean forceAssign(int studentId, int topicId, String reason, int roundNo) throws SQLException {
        Connection conn = SQLHelper.getConnection();
        try {
            conn.setAutoCommit(false);

            if (hasApprovedSelection(conn, studentId) || !occupyTopicQuota(conn, topicId)) {
                conn.rollback();
                return false;
            }

            rejectOtherPendingSelections(conn, studentId, 0);
            insert(conn, studentId, topicId, reason, roundNo, "approved");
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

     


    public void updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE topic_selections SET status=? WHERE id=?";
        SQLHelper.executeUpdate(sql, status, id);
    }

     


    public int updateStatus(int id, String status, int teacherId) throws SQLException {
        String sql = "UPDATE topic_selections ts JOIN topics t ON ts.topic_id=t.id "
                + "SET ts.status=? WHERE ts.id=? AND t.teacher_id=?";
        return SQLHelper.executeUpdate(sql, status, id, teacherId);
    }

     


    public TopicSelection findById(int id) throws SQLException {
        String sql = baseSql + "WHERE ts.id = ?";
        ResultSet rs = SQLHelper.executeQuery(sql, id);
        try {
            if (rs.next()) {
                return rowToSelection(rs);
            }
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

     


    public boolean isStudentOfTeacher(int studentId, int teacherId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM topic_selections ts JOIN topics t ON ts.topic_id=t.id "
                + "WHERE ts.student_id=? AND t.teacher_id=? AND ts.status='approved'";
        ResultSet rs = SQLHelper.executeQuery(sql, studentId, teacherId);
        try {
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            SQLHelper.close(rs);
        }
    }

     


    public boolean hasActiveSelection(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM topic_selections WHERE student_id=? AND status IN ('pending','approved')";
        ResultSet rs = SQLHelper.executeQuery(sql, studentId);
        try {
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            SQLHelper.close(rs);
        }
    }

     


    public boolean hasApprovedSelection(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM topic_selections WHERE student_id=? AND status='approved'";
        ResultSet rs = SQLHelper.executeQuery(sql, studentId);
        try {
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            SQLHelper.close(rs);
        }
    }

     


    public boolean hasSelectionInRound(int studentId, int roundNo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM topic_selections WHERE student_id=? AND round_no=?";
        ResultSet rs = SQLHelper.executeQuery(sql, studentId, roundNo);
        try {
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            SQLHelper.close(rs);
        }
    }

     


    public TopicSelection findApprovedByStudent(int studentId) throws SQLException {
        String sql = baseSql + "WHERE ts.student_id = ? AND ts.status = 'approved' ORDER BY ts.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql, studentId);
        try {
            if (rs.next()) {
                return rowToSelection(rs);
            }
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

    private void insert(Connection conn, int studentId, int topicId, String reason, int roundNo, String status)
            throws SQLException {
        String sql = "INSERT INTO topic_selections(student_id, topic_id, reason, round_no, status) VALUES(?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.setInt(1, studentId);
            ps.setInt(2, topicId);
            ps.setString(3, reason);
            ps.setInt(4, roundNo);
            ps.setString(5, status);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private boolean occupyTopicQuota(Connection conn, int topicId) throws SQLException {
        String sql = "UPDATE topics "
                + "SET status=IF(selected_count + 1 >= max_students, 'closed', status), "
                + "selected_count=selected_count + 1 "
                + "WHERE id=? AND status='open' AND review_status='approved' AND selected_count < max_students";
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.setInt(1, topicId);
            return ps.executeUpdate() > 0;
        } finally {
            ps.close();
        }
    }

    private boolean hasApprovedSelection(Connection conn, int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM topic_selections WHERE student_id=? AND status='approved'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = null;
        try {
            ps.setInt(1, studentId);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            if (rs != null) {
                rs.close();
            }
            ps.close();
        }
    }

    private boolean hasSelectionInRound(Connection conn, int studentId, int roundNo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM topic_selections WHERE student_id=? AND round_no=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = null;
        try {
            ps.setInt(1, studentId);
            ps.setInt(2, roundNo);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            if (rs != null) {
                rs.close();
            }
            ps.close();
        }
    }

    private SelectionLock lockSelection(Connection conn, int selectionId) throws SQLException {
        String sql = "SELECT ts.student_id, ts.topic_id, ts.status, t.teacher_id "
                + "FROM topic_selections ts JOIN topics t ON ts.topic_id=t.id "
                + "WHERE ts.id=? FOR UPDATE";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = null;
        try {
            ps.setInt(1, selectionId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }
            SelectionLock lock = new SelectionLock();
            lock.studentId = rs.getInt("student_id");
            lock.topicId = rs.getInt("topic_id");
            lock.status = rs.getString("status");
            lock.teacherId = rs.getInt("teacher_id");
            return lock;
        } finally {
            if (rs != null) {
                rs.close();
            }
            ps.close();
        }
    }

    private void updateStatus(Connection conn, int selectionId, String status) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE topic_selections SET status=? WHERE id=?");
        try {
            ps.setString(1, status);
            ps.setInt(2, selectionId);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private void rejectOtherPendingSelections(Connection conn, int studentId, int keepSelectionId) throws SQLException {
        String sql = "UPDATE topic_selections SET status='rejected' "
                + "WHERE student_id=? AND status='pending' AND id<>?";
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.setInt(1, studentId);
            ps.setInt(2, keepSelectionId);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private String appendReasonNote(String reason, String note) {
        if (reason == null || reason.trim().length() == 0) {
            return note;
        }
        return reason + "（" + note + "）";
    }

    private static class SelectionLock {
        private int studentId;
        private int topicId;
        private int teacherId;
        private String status;
    }

     


    public int countByStatus(String status) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery("SELECT COUNT(*) FROM topic_selections WHERE status=?", status);
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    private List<TopicSelection> resultSetToList(ResultSet rs) throws SQLException {
        List<TopicSelection> list = new ArrayList<TopicSelection>();
        while (rs.next()) {
            list.add(rowToSelection(rs));
        }
        return list;
    }

    private TopicSelection rowToSelection(ResultSet rs) throws SQLException {
        TopicSelection sel = new TopicSelection();
        sel.setId(rs.getInt("id"));
        sel.setStudentId(rs.getInt("student_id"));
        sel.setTopicId(rs.getInt("topic_id"));
        sel.setReason(rs.getString("reason"));
        sel.setRoundNo(rs.getInt("round_no"));
        sel.setStatus(rs.getString("status"));
        sel.setCreatedAt(rs.getTimestamp("created_at"));
        sel.setStudentName(rs.getString("student_name"));
        sel.setTopicTitle(rs.getString("topic_title"));
        return sel;
    }
}
