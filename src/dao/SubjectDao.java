package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.School;
import bean.Subject;

public class SubjectDao extends Dao{

	public Subject get(String cd, School school) throws Exception {

		Subject subject = new Subject();
		Connection connection = getConnection();
		PreparedStatement statement = null;

		try {
			statement = connection.prepareStatement(
				"SELECT * FROM SUBJECT WHERE CD = ? AND SCHOOL_CD = ?");
			statement.setString(1, cd);
			statement.setString(2, school.getCd());
			ResultSet rSet = statement.executeQuery();

			if (rSet.next()) {
				subject.setCd(rSet.getString("cd"));
				subject.setName(rSet.getString("name"));
				subject.setSchool(school);
			} else {
				subject = null;
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
		return subject;
	}

	public List<Subject> filter(School school) throws Exception {

		List<Subject> list = new ArrayList<>();
		Connection connection = getConnection();
		PreparedStatement statement = null;

		try {
			statement = connection.prepareStatement(
				"SELECT * FROM SUBJECT WHERE SCHOOL_CD = ? ORDER BY CD");
			statement.setString(1, school.getCd());
			ResultSet rSet = statement.executeQuery();

			while (rSet.next()) {
				Subject subject = new Subject();
				subject.setCd(rSet.getString("cd"));
				subject.setName(rSet.getString("name"));
				list.add(subject);
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
		return list;
	}

	public boolean save(Subject subject) throws Exception {
		return true;
	}

	public boolean delete(Subject subject) throws Exception {
		return true;
	}
}