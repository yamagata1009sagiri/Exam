package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.Student;
import bean.TestListStudent;

public class TestListStudentDao extends Dao {

	private List<TestListStudent> postFilter(ResultSet rSet) throws Exception {
		List<TestListStudent> list = new ArrayList<>();

		while (rSet.next()) {
			TestListStudent test = new TestListStudent();
			test.setNum(rSet.getInt("t_no"));
			test.setPoint(rSet.getInt("t_point"));
			test.setSubjectCd(rSet.getString("sj_cd"));
			test.setSubjectName(rSet.getString("sj_name"));

			list.add(test);
		}
		return list;
	}

	public List<TestListStudent> filter(Student student) throws Exception {

		List<TestListStudent> list = new ArrayList<>();
		Connection connection = getConnection();
		PreparedStatement statement = null;
		ResultSet rSet = null;

		try {
			statement = connection.prepareStatement(
				"SELECT SJ.name as sj_name, SJ.cd as sj_cd, T.no as t_no, T.point as t_point "
				+ "FROM student ST inner join (test T inner join subject SJ on T.subject_cd=SJ.cd) "
				+ "on ST.no=T.student_no where ST.no=? order by SJ.cd asc, T.no asc");
			statement.setString(1, student.getNo());
			rSet = statement.executeQuery();
			list = postFilter(rSet);
		} catch (Exception e) {
			throw e;
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException sqle) {
					throw sqle;
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException sqle) {
					throw sqle;
				}
			}
		}
		return list;
	}
}