package scoremanager.main;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tool.Action;

public class StudentDeleteAction extends Action {

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
		//ローカル変数の宣言
		//リクエストパラメータ―の取得
		String no = req.getParameter("no");
		String name = req.getParameter("name");
		//レスポンス値をセット
		req.setAttribute("no", no);
		req.setAttribute("name", name);
		//JSPへフォワード
		req.getRequestDispatcher("student_delete.jsp").forward(req, res);
	}
}