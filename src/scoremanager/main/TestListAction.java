package scoremanager.main;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Subject;
import bean.Teacher;
import dao.ClassNumDao;
import dao.SubjectDao;
import tool.Action;

public class TestListAction extends Action {

	@Override
	public void execute(
		HttpServletRequest req, HttpServletResponse res
	) throws Exception {
		ClassNumDao cNumDao = new ClassNumDao();
		SubjectDao sDao = new SubjectDao();
		HttpSession session = req.getSession(true);
		Teacher teacher = (Teacher)session.getAttribute("user");
		LocalDate todayDate = LocalDate.now();
		int year = todayDate.getYear();
		List<Integer> entYearSet = new ArrayList<>();
		List<Integer> numSet = new ArrayList<>();

		List<String> list = cNumDao.filter(teacher.getSchool());
		List<Subject> subjects = sDao.filter(teacher.getSchool());

		for (int i = year - 10; i < year + 10; i++) {
			entYearSet.add(i);
		}

		for (int i = 1; i <= 2; i++) {
			numSet.add(i);
		}

		req.setAttribute("class_num_set", list);
		req.setAttribute("ent_year_set", entYearSet);
		req.setAttribute("subjects", subjects);
		req.setAttribute("num_set", numSet);

		req.getRequestDispatcher("test_list.jsp").forward(req, res);
	}
}