package scoremanager.main;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Student;
import dao.StudentDao;
import tool.Action;

public class StudentDeleteExecuteAction extends Action {

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {

		String no = "";
		Map<String, String> errors = new HashMap<>();
		StudentDao dao = new StudentDao();
		Student students = null;

		no = req.getParameter("no");
		students = dao.get(no);

		if (students == null) {
			errors.put("no", "学生が存在していません");
			req.setAttribute("errors", errors);
			req.getRequestDispatcher("student_delete.jsp").forward(req, res);
		} else {
			req.setAttribute("no", no);
			dao.delete(no);
			req.getRequestDispatcher("student_delete_done.jsp").forward(req, res);
		}


	}
}