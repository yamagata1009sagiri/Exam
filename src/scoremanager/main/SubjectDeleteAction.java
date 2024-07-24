package scoremanager.main;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tool.Action;

public class SubjectDeleteAction extends Action {

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
		//ローカル変数の宣言
		//リクエストパラメータ―の取得
		String cd = req.getParameter("cd");//科目コード
		String name = req.getParameter("name");//科目名
		//レスポンス値をセット
		req.setAttribute("cd", cd);
		req.setAttribute("name", name);
		//JSPへフォワード
		req.getRequestDispatcher("subject_delete.jsp").forward(req, res);
	}
}
