package scoremanager.main;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tool.Action;

public class SubjectCreateAction extends Action{

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
		//ローカル変数の宣言
		//なし

		//リクエストパラメータ―の取得
		//なし

		//DBへデータ保存
		//なし

		//レスポンス値をセット
		//なし

		//JSPへフォワード
		req.getRequestDispatcher("subject_create.jsp").forward(req, res);
	}
}
