package scoremanager.main;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Subject;
import bean.Teacher;
import dao.SubjectDao;
import tool.Action;

public class SubjectCreateExecuteAction extends Action{

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
		//ローカル変数の宣言 1
		String cd = "";
		String name = "";
		Subject subject = null;
		SubjectDao subDao = new SubjectDao();
		Map<String, String> errors = new HashMap<>();
		HttpSession session = req.getSession();//セッション
		Teacher teacher = (Teacher)session.getAttribute("user");// ログインユーザーを取得

		//リクエストパラメータ―の取得 2
		cd = req.getParameter("cd");//科目コード
		name = req.getParameter("name");//科目名

		//ビジネスロジック 4
    	if (cd.length() != 3) {
			errors.put("cd", "3文字で入力してください"); // 入力された文字が3文字以外だった場合
    	} else {
    		// 科目コードを元に科目情報を取得
			subject = subDao.get(cd, teacher.getSchool());
			if (subject != null) {// 科目が存在していた場合はエラー
				errors.put("cd", "科目コードが重複しています");
			}
		}

    	//エラーがあったかどうかで手順5~7の内容が分岐
    	//レスポンス値をセット 6
    	//JSPへフォワード 7
		//DBへデータ保存 5
		if (errors.isEmpty()) {// 学生が未登録だった場合
			// インスタンスを初期化
			subject = new Subject();
			// インスタンスに値をセット
			subject.setCd(cd);
			subject.setName(name);
			subject.setSchool(teacher.getSchool());
			// 保存
			subDao.save(subject);
			req.getRequestDispatcher("subject_create_done.jsp").forward(req, res);
		} else {
			// エラー時の処理
			req.setAttribute("errors", errors);
			req.setAttribute("cd", cd);
			req.setAttribute("name", name);
			req.getRequestDispatcher("subject_create.jsp").forward(req, res);
		}

	}
}