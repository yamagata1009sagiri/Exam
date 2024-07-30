package scoremanager.main;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.StudentDao;
import tool.Action;

public class StudentDeleteExecuteAction extends Action {

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {

		String no = "";
		StudentDao dao = new StudentDao();

		no = req.getParameter("no");

		dao.delete(no);
		req.getRequestDispatcher("student_delete_done.jsp").forward(req, res);
	}
}