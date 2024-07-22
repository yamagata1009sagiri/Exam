package scoremanager.main;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.School;
import bean.Subject;
import bean.Teacher;
import bean.TestListSubject;
import dao.ClassNumDao;
import dao.SubjectDao;
import dao.TestListSubjectDao;
import tool.Action;

public class SubjectTestListAction extends Action {

	@Override
	public void execute(
		HttpServletRequest req, HttpServletResponse res
	) throws Exception {
		ClassNumDao cNumDao = new ClassNumDao();
		SubjectDao sDao = new SubjectDao();
		HttpSession session = req.getSession(true);
		Teacher teacher = (Teacher)session.getAttribute("user");
		LocalDate todaysDate = LocalDate.now();
		int year = todaysDate.getYear();
		List<Integer> entYearSet = new ArrayList<>();
		List<Integer> numSet = new ArrayList<>();
		int entYear = 0;
		String entYearStr = null;
		String classNum = null;
		String subjectCd = null;
		Map<String, String> errors = new HashMap<>();
		List<TestListSubject> tests = new ArrayList<>();
		TestListSubjectDao testDao = new TestListSubjectDao();
		SubjectDao subjectDao = new SubjectDao();
		Subject subject = null;

		entYearStr = req.getParameter("f1");
		classNum = req.getParameter("f2");
		subjectCd = req.getParameter("f3");
		if (entYearStr != null) {
			entYear = Integer.parseInt(entYearStr);
		}

		List<String> list = cNumDao.filter(teacher.getSchool());
		List<Subject> subjects = sDao.filter(teacher.getSchool());

		for (int i = year - 10; i < year + 10; i++) {
			entYearSet.add(i);
		}

		for (int i = 1; i <= 2; i++) {
			numSet.add(i);
		}

		req.setAttribute("f1", entYear);
		req.setAttribute("f2", classNum);
		req.setAttribute("f3", subjectCd);
		req.setAttribute("class_num_set", list);
		req.setAttribute("ent_year_set", entYearSet);
		req.setAttribute("subjects", subjects);
		req.setAttribute("num_set", numSet);

		if (entYearStr != null || classNum != null || subjectCd != null) {
			if (entYear != 0 && !classNum.equals("0") && !subjectCd.equals("0")) {
				School school = teacher.getSchool();
				subject = subjectDao.get(subjectCd, school);
				tests = testDao.filter(entYear, classNum, subject, school);

				req.setAttribute("subject", subject);
				req.setAttribute("tests", tests);
			} else {
				errors.put("filter", "入学年度とクラスと科目を選択してください");
				req.setAttribute("errors", errors);
			}
		}
		req.getRequestDispatcher("test_list_subject.jsp").forward(req, res);
	}
}