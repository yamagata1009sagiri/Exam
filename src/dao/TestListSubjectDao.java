package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.School;
import bean.Subject;
import bean.TestListSubject;

public class TestListSubjectDao extends Dao {

	private String baseSql = "SELECT ST.ent_year as st_ent_year, ST.no as st_no, ST.name as st_name, "
			+ "ST.class_num as st_class_num, T.no as t_no, T.point as t_point "
			+ "FROM student ST left outer join (test T inner join subject SJ on T.subject_cd = SJ.cd) "
			+ "on ST.no = T.student_no ";

	private List<TestListSubject> postFilter(ResultSet rSet) throws Exception {

		List<TestListSubject> list = new ArrayList<>();
		TestListSubject test = new TestListSubject();

		String currentStudentNo = null;
		while (rSet.next()) {
			String studentNo = rSet.getString("st_no");

			if (currentStudentNo == null) {
				currentStudentNo = studentNo;
				test.setStudentNo(studentNo);
				test.setEntYear(rSet.getInt("st_ent_year"));
				test.setClassNum(rSet.getString("st_class_num"));
				test.setStudentName(rSet.getString("st_name"));
			} else if (!studentNo.equals(currentStudentNo)) {
				list.add(test);
				test = new TestListSubject();
				currentStudentNo = studentNo;
				test.setStudentNo(studentNo);
				test.setEntYear(rSet.getInt("st_ent_year"));
				test.setClassNum(rSet.getString("st_class_num"));
				test.setStudentName(rSet.getString("st_name"));
			}

			int num = rSet.getInt("t_no");
			int point = rSet.getInt("t_point");
			test.putPoint(num, point);
		}
		if (currentStudentNo != null) {
			list.add(test);
		}
		return list;
	}

	public List<TestListSubject> filter(
		int entYear, String classNum, Subject subject, School school) throws Exception {

		Connection connection = getConnection();
		PreparedStatement statement = null;
		List<TestListSubject> list = new ArrayList<>();
		ResultSet rSet = null;

		String condition = "and T.subject_cd=? where ST.ent_year = ? "
				+ "and ST.class_num = ? and ST.school_cd = ? and ST.is_attend = true ";
		String order = "order by ST.no asc, T.no asc";

		try {
			statement = connection.prepareStatement(baseSql + condition + order);
			statement.setString(1, subject.getCd());
			statement.setInt(2, entYear);
			statement.setString(3, classNum);
			statement.setString(4, school.getCd());
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