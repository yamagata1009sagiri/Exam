package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.School;
import bean.Student;
import bean.Subject;
import bean.Test;

public class TestDao extends Dao {

	private String baseSql = "SELECT SJ.cd as sj_cd, SJ.name as sj_name, ST.no as st_no, ST.name as st_name, "
			+ "ST.ent_year as st_ent_year, ST.class_num as st_class_num, ST.is_attend as st_is_attend, "
			+ "T.no as t_no, coalesce(T.point, -1) as t_point "
			+ "FROM student ST left outer join (test T inner join subject SJ on T.subject_cd = SJ.cd) "
			+ "on ST.no = T.student_no ";

	public Test get(Student student, Subject subject, School school, int no) throws Exception {

		Test test = new Test();
		Connection connection = getConnection();
		PreparedStatement statement = null;

		try {
			statement = connection.prepareStatement(
				"select * from test where student_no = ? and subject_cd = ? and school_cd = ? and no = ?");
			statement.setString(1, student.getNo());
			statement.setString(2, subject.getCd());
			statement.setString(3, school.getCd());
			statement.setInt(4, no);
			ResultSet rSet = statement.executeQuery();

			if (rSet.next()) {
				test.setStudent(student);
				test.setSubject(subject);
				test.setSchool(school);
				test.setNo(no);
				test.setPoint(rSet.getInt("point"));
			} else {
				test = null;
			}
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
		return test;
	}

	private List<Test> postFilter(ResultSet rSet, School school) throws Exception {

		List<Test> list = new ArrayList<>();

		while (rSet.next()) {
			Subject subject = new Subject();
			subject.setCd(rSet.getString("sj_cd"));
			subject.setName(rSet.getString("sj_name"));
			subject.setSchool(school);

			Student student = new Student();
			student.setNo(rSet.getString("st_no"));
			student.setName(rSet.getString("st_name"));
			student.setEntYear(rSet.getInt("st_ent_year"));
			student.setClassNum(rSet.getString("st_class_num"));
			student.setAttend(rSet.getBoolean("st_is_attend"));
			student.setSchool(school);

			Test test = new Test();
			test.setStudent(student);
			test.setClassNum(rSet.getString("st_class_num"));
			test.setSubject(subject);
			test.setSchool(school);
			test.setNo(rSet.getInt("t_no"));
			test.setPoint(rSet.getInt("t_point"));

			list.add(test);
		}
		return list;
	}

	public List<Test> filter(int entYear, String classNum, Subject subject, int num, School school
			) throws Exception {

		List<Test> list = new ArrayList<>();
		Connection connection = getConnection();
		PreparedStatement statement = null;
		ResultSet rSet = null;

		String condition = "and T.subject_cd = ? and T.no = ? "
				+ "where ST.ent_year = ? and ST.class_num = ? and ST.school_cd = ? and ST.is_attend = true";
		String order = "order by ST.no asc";

		try {
			statement = connection.prepareStatement(baseSql + condition + order);
			statement.setString(1, subject.getCd());
			statement.setInt(2, num);
			statement.setInt(3, entYear);
			statement.setString(4, classNum);
			statement.setString(5, school.getCd());
			rSet = statement.executeQuery();
			list = postFilter(rSet, school);
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

	public boolean save(List<Test> list) throws Exception {

		Connection connection = getConnection();
		boolean canCommit = true;

		try {
			connection.setAutoCommit(false);
			for (Test test : list) {
				canCommit = save(test, connection);
				if (!canCommit) {
					break;
				}
			}

			if (canCommit) {
				connection.commit();
			} else {
				throw new Exception();
			}
		} catch (SQLException sqle) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				throw e;
			}
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (SQLException sqle) {
					throw sqle;
				}
			}
		}
		return canCommit;
	}

	private boolean save(Test test, Connection connection) throws Exception {

		PreparedStatement statement = null;
		int count = 0;

		try {
			Test old = get(test.getStudent(), test.getSubject(), test.getSchool(), test.getNo());
			if (old == null) {
				statement = connection.prepareStatement(
					"insert into test(point, no, student_no, subject_cd, school_cd, class_num) "
					+ "values(?, ?, ?, ?, ?, ?)");
				statement.setInt(1, test.getPoint());
				statement.setInt(2, test.getNo());
				statement.setString(3, test.getStudent().getNo());
				statement.setString(4, test.getSubject().getCd());
				statement.setString(5, test.getSchool().getCd());
				statement.setString(6, test.getStudent().getClassNum());
			} else {
				statement = connection.prepareStatement(
						"update test set point = ? where no = ? and student_no = ? and subject_cd = ? and school_cd = ?");
				statement.setInt(1, test.getPoint());
				statement.setInt(2, test.getNo());
				statement.setString(3, test.getStudent().getNo());
				statement.setString(4, test.getSubject().getCd());
				statement.setString(5, test.getSchool().getCd());
			}
			count = statement.executeUpdate();
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
		}
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean delete(List<Test> list) throws Exception {

		Connection connection = getConnection();
		boolean canCommit = true;

		try {
			connection.setAutoCommit(false);
			for (Test test : list) {
				canCommit = delete(test, connection);
				if (!canCommit) {
					break;
				}
			}

			if (canCommit) {
				connection.commit();
			} else {
				throw new Exception();
			}
		} catch (SQLException sqle) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				throw e;
			}
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (SQLException sqle) {
					throw sqle;
				}
			}
		}
		return canCommit;
	}

	private boolean delete(Test test, Connection connection) throws Exception {

		PreparedStatement statement = null;
		int count = 0;

		try {
			statement = connection.prepareStatement(
					"delete from test where no = ? and student_no = ? and subject_cd = ? and school_cd = ?");
			statement.setInt(1, test.getNo());
			statement.setString(2, test.getStudent().getNo());
			statement.setString(3, test.getSubject().getCd());
			statement.setString(4, test.getSchool().getCd());
			count = statement.executeUpdate();
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
		}
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}
}