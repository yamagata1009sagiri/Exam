// 成績登録の処理サーブレット

package scoremanager.main;

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
import bean.Test;
import dao.ClassNumDao;
import dao.StudentDao;
import dao.SubjectDao;
import dao.TestDao;
import tool.Action;

public class TestRegistExecuteAction extends Action {

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
		//ローカル変数の宣言 1
		HttpSession session = req.getSession(true);//セッション
		Teacher teacher = (Teacher)session.getAttribute("user");// ログインユーザーを取得

		SubjectDao subDao = new SubjectDao();// 科目Dao
		StudentDao stuDao = new StudentDao(); // 学生Dao
		ClassNumDao cNumDao = new ClassNumDao();// クラス番号Dao
		School school = teacher.getSchool(); // 学校

		Map<String, String> errors = new HashMap<>();//エラーメッセージ
		Map<String, String> inPoint = new HashMap<>(); //得点の入れ箱
		List<Test> scoreList = new ArrayList<>(); //成績のリスト
		List<Test> delList = new ArrayList<>(); //削除のリスト

		//リクエストパラメータ―の取得 2
		String subjectCd = req.getParameter("subject_cd");
		int num = Integer.parseInt(req.getParameter("num"));
		String[] studentNoSet = req.getParameterValues("student_no_set[]");

		//DBからデータ取得 3
		List<String> list = cNumDao.filter(school);
		Subject subjects= subDao.get(subjectCd, school);

		//ビジネスロジック 4
		// 全件走査
		for (String studentNo : studentNoSet) {
			Test test = new Test(); //Testインスタンスの初期化

			String pointStr = null; //得点の文字

			// 得点_学生番号の取得
			pointStr = req.getParameter("point_" + studentNo);
			// 得点用のmapに学生番号と文字型得点を格納
			inPoint.put(studentNo, pointStr);

			// 文字型得点に中身がなければ再度操作させる
			if (pointStr.isEmpty()) {
				continue;
			}

			// 得点
			int point = 0;

			try {
				point = Integer.parseInt(pointStr);
			} catch (NumberFormatException e) {
				// 数字以外の入力の場合、エラーメッセージ
				errors.put(studentNo, "整数以外入力できません");
				break;
			}

			// 点数が0～100でなかった場合、エラーメッセージ
			if (point < 0 || 100 < point) {
				errors.put(studentNo, "0～100の範囲で入力してください");
				break;
			}

			// 成績に値をセット
			test.setNo(num);
			test.setPoint(point);
			test.setSchool(school);
			test.setStudent(stuDao.get(studentNo));
			test.setSubject(subjects);

			// 削除フラグ
			boolean del = false;
			//入力された値にdelete学生が有った場合、フラグを立てる
			if (req.getParameter("delete_" + studentNo) != null){
				del = true;
			}

			// フラグ確認
			if (del) {
				delList.add(test);
			} else {
				scoreList.add(test);
			}
		}

		//レスポンス値をセット
		//JSPへフォワード
		if(errors.size() > 0){//エラーがあった場合
			// リクエスト属性をセット
			req.setAttribute("errors", errors);
			req.getRequestDispatcher("TestRegist.action").forward(req, res);
		} else {// 正常時
			// リクエスト用に得点mapをセット
			req.setAttribute("input_points", inPoint);

			TestDao testDao = new TestDao(); //インスタンス初期化
			// 成績リクエストに保存
			testDao.save(scoreList);
			// 削除リストから削除
			testDao.delete(delList);

			// 登録を続ける場合
			if (req.getParameter("continue") != null){
				//完了のメッセージ
				req.setAttribute("done", "登録が完了しました");
				// 入力画面にフォワード
				req.getRequestDispatcher("test_regist.jsp").forward(req, res);
			} else {
				// 完了ページにリダイレクト
				req.getRequestDispatcher("test_regist_done.jsp").forward(req, res);
			}
		}
	}
}
