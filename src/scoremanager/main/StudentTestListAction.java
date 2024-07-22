package scoremanager.main;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Student;
import bean.Teacher;
import bean.TestListStudent;
import dao.ClassNumDao;
import dao.StudentDao;
import dao.SubjectDao;
import dao.TestListStudentDao;
import tool.Action;

public class StudentTestListAction extends Action {

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {

		ClassNumDao cNumDao = new ClassNumDao();
		SubjectDao sDao = new SubjectDao();
		HttpSession session = req.getSession(true);
		Teacher teacher = (Teacher) session.getAttribute("user");
		LocalDate todaysDate = LocalDate.now();
		int year = todaysDate.getYear();
		List<Integer> entYearSet = new ArrayList<>();
		List<Integer> numSet = new ArrayList<>();
		Map<String, String> errors = new HashMap<>();
		Student student = null;
		List<TestListStudent> tests = new ArrayList<>();

		String f = req.getParameter("f");
		String studentNo = req.getParameter("f4");
		errors = (HashMap<String, String>)req.getAttribute("errors");

		if (studentNo != null) {
			StudentDao studentDao = new StudentDao();
			student = studentDao.get(studentNo);

			if (student != null) {
				TestListStudentDao testDao = new TestListStudentDao();
				tests = testDao.filter(student);
			}
		} else {
			errors.put("filter", "入学年度とクラスと科目を選択してください");
			req.setAttribute("errors", errors);
			req.getRequestDispatcher("error.jsp").forward(req, res);
		}
		req.setAttribute("student", student);
		req.setAttribute("tests", tests);

		req.getRequestDispatcher("test_list_student.jsp").forward(req, res);
	}

	private boolean existsAllParam(int entYear, String entYearStr, String classNum, String subjectCd) {

		boolean result = false;

		if (entYearStr != null || classNum != null || subjectCd != null) {
			if (entYear != 0 && !classNum.equals("0") && !subjectCd.equals("0")) {
				result = true;
			}
		}
		return result;
	}
}
