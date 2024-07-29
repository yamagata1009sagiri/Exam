// 成績登録のサーブレット

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
import bean.Test;
import dao.ClassNumDao;
import dao.SubjectDao;
import dao.TestDao;
import tool.Action;

public class TestRegistAction extends Action {

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
		//ローカル変数の宣言 1
		Subject subject = null;

		// Daoを初期化
		TestDao testDao = new TestDao();
		SubjectDao subDao = new SubjectDao();
		ClassNumDao cNumDao = new ClassNumDao();

		HttpSession session = req.getSession(true);//セッション
		Teacher teacher = (Teacher)session.getAttribute("user");//ログインユーザー
		School school = teacher.getSchool();
		List<Test> tests = null;
		Map<String, String> errors = new HashMap<>();// エラーメッセージ

		LocalDate todaysDate = LocalDate.now();// LcalDateインスタンスを取得
		int year = todaysDate.getYear();// 現在の年を取得
		List<Integer> entYearSet = new ArrayList<>();// 入学年度のリストを初期化
		List<Integer> numSet = new ArrayList<>();// テストの回数リストを初期化


		//リクエストパラメータ―の取得 2
		String entYearStr=req.getParameter("f1");// 入力された入学年度
		String classNum = req.getParameter("f2");//入力されたクラス番号
		String subjectCd=req.getParameter("f3");//入力された科目コード
		String numStr=req.getParameter("f4");//入力された回数


		// 入学年度
		int entYear = 0;
		if (entYearStr != null) {
			// 数値に変換
			entYear = Integer.parseInt(entYearStr);
		}

		//回数
		int num = 0;
		if (numStr != null) {
			// 数値に変換
			num = Integer.parseInt(numStr);
		}

		//DBからデータ取得 3
		List<String> list = cNumDao.filter(teacher.getSchool());
		List<Subject> subjects= subDao.filter(teacher.getSchool());

		//ビジネスロジック 4
		// 10年後の年をリストに追加
		for (int i = year - 10; i < year + 10; i++) {
			entYearSet.add(i);
		}

		// 二回分のテスト回数の追加
		for (int i = 1; i <= 2; i++) {
			numSet.add(i);
		}

		//DBへデータ保存 5
		//なし
		//レスポンス値をセット 6
		// リクエストに入学年度をセット
		req.setAttribute("f1", entYear);
		// リクエストにクラス番号をセット
		req.setAttribute("f2", classNum);
		// リクエストに科目番号をセット
		req.setAttribute("f3", subjectCd);
		// リクエストに回数をセット
		req.setAttribute("f4", num);
		req.setAttribute("num_set", numSet);
		// リクエストに科目リストをセット
		req.setAttribute("subjects", subjects);
		// リクエストにデータをセット
		req.setAttribute("class_num_set", list);
		req.setAttribute("ent_year_set", entYearSet);

		// 各項目に値があった場合と無い場合の処理
		if (entYearStr != null && classNum != null && subjectCd != null && numStr != null) {
			if (entYear != 0 && !classNum.equals("0") && !subjectCd.equals("0") && num != 0) {
				// 科目のインスタンスを取得
				subject = subDao.get(subjectCd, school);
				// 成績の取得
				tests = testDao.filter(entYear, classNum, subject, num, school);
				// リクエストに回数をセット設定
				req.setAttribute("num", num);
				// リクエストに科目を設定
				req.setAttribute("subject", subject);
				// リクエストに成績を設定
				req.setAttribute("tests", tests);
			} else {
				// エラーメッセージ処理
				errors.put("filter", "入学年度とクラスと科目と回数を選択してください");
				req.setAttribute("errors", errors);
			}
		}

		//JSPへフォワード 7 (成績一覧へ)
		req.getRequestDispatcher("test_regist.jsp").forward(req, res);
	}
}
