package com.tarena.elts.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.tarena.elts.entity.EntityContext;
import com.tarena.elts.entity.ExamInfo;
import com.tarena.elts.entity.QuestionInfo;
import com.tarena.elts.entity.User;
import com.tarena.elts.service.ExamService;
import com.tarena.elts.service.IdOrPwdException;

/** 客户界面控制器：客户端上下文环境 */
public class ClientContext implements Serializable {
	private static final long serialVersionUID = 1L;

	// private LoginFrame loginFrame = new LoginFrame();

	// 代表当前正在回答的题目
	private QuestionInfo currentQuestionInfo;

	private ExamInfo examInfo;

	private WelcomeWindow welcomeWindow;

	/**
	 * private LoginFrame loginFrame;
	 * 
	 * public void setLoginFrame(LoginFrame loginFrame){ this.loginFrame =
	 * loginFrame; } 对象的依赖注入
	 */
	private LoginFrame loginFrame;

	/** 依赖注入(IOC) 这里注入的是loginFrame实例 */
	public void setLoginFrame(LoginFrame loginFrame) {
		this.loginFrame = loginFrame;
	}

	private ExamService examService;

	public void setExamService(ExamService examService) {
		this.examService = examService;
	}

	private MenuFrame menuFrame;

	public void setMenuFrame(MenuFrame menuFrame) {
		this.menuFrame = menuFrame;
	}
	private MsgFrame msgFrame;

	public void setMsgFrame(MsgFrame msgFrame) {
		this.msgFrame = msgFrame;
	}

	private ExamFrame examFrame;

	public void setExamFrame(ExamFrame examFrame) {
		this.examFrame = examFrame;
	}

	public void setWelcomeWindow(WelcomeWindow welcomeWindow) {
		this.welcomeWindow = welcomeWindow;
	}

	/**
	 * 显示软件的开始界面，先显示登陆界面 show()方法的运行，必须依赖于loginFrame(实例变量)引用具体的界面对象
	 * 如果不引用的话，则此对象为空，会出现空指针异常 这个界面对象必须依赖方法setLoginFrame()注入，注入这个界面对象实例
	 */
	public void show() {
		welcomeWindow.setVisible(true);
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				welcomeWindow.setVisible(false);
				loginFrame.setVisible(true);
				timer.cancel();
			}
		}, 2000);
	}

	/**
	 * 此方法被login按钮调用 登陆系统，控制逻辑 1. 从登陆界面获得用户的Id & Pwd 2. 调用业务模型的login方法完成登陆功能 3.
	 * 根据登陆结果，如果成功就更新菜单界面，显示用户信息， 关闭登陆界面，打开菜单界面 4. 如果登陆失败就在登陆界面上显示失败信息
	 */
	public void login() {
		try {
			int id = loginFrame.getUserId();
			String pwd = loginFrame.getPassword();
			User user = examService.login(id, pwd);
			menuFrame.updateView(user);// 更新菜单界面
			loginFrame.setVisible(false);
			menuFrame.setVisible(true);
		} catch (IdOrPwdException e) {
			e.printStackTrace();
			loginFrame.showMessage("登陆失败：" + e.getMessage());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			loginFrame.showMessage("ID必须是整数：" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			loginFrame.showMessage("登陆失败：" + e.getMessage());
		}
	}

	/**
	 * 退出系统 控制逻辑
	 * 
	 * @param source
	 *            代表从哪一个界面退出. 是一个窗口的引用
	 */
	public void exit(JFrame source) {
		// Confirm 确认
		// Dialog 对话框
		int val = JOptionPane.showConfirmDialog(source, "离开吗？");
		if (val == JOptionPane.YES_OPTION) {
			System.exit(0);// 结束当前Java进程
		}
	}

	/**
	 * 开始考试
	 * 
	 */
	public void start() {
		try {
			// 访问业务层开始考试
			examInfo = examService.start();
			// 取得第一道题, 用于显示考题
			currentQuestionInfo = examService.getQuestion(0);
			// 更新考试界面
			examFrame.updateView(examInfo, currentQuestionInfo);
			// 关闭菜单,
			menuFrame.setVisible(false);
			// 打开考试界面
			center(examFrame);
			examFrame.setVisible(true);
			startTimer();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(menuFrame, e.getMessage());
		}
	}

	public void next() {
		try {
			// 取得当前的题号
			int index = currentQuestionInfo.getQuestionIndex();
			// 保存当前考题的用户答案到业务层
			List<Integer> userAnswers = examFrame.getUserAnswers();
			examService.saveUserAnswers(index, userAnswers);
			// 取得下一题目
			QuestionInfo questionInfo = examService.getQuestion(index + 1);
			currentQuestionInfo = questionInfo;
			// 更新考试界面, 显示下一题目
			examFrame.updateView(examInfo, questionInfo);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(examFrame, e.getMessage());
		}
	}

	public void prev() {
		try {
			// 取得当前的题号
			int index = currentQuestionInfo.getQuestionIndex();
			// 保存当前考题的用户答案到业务层
			List<Integer> userAnswers = examFrame.getUserAnswers();
			examService.saveUserAnswers(index, userAnswers);
			// 取得下一题目
			QuestionInfo questionInfo = examService.getQuestion(index - 1);
			currentQuestionInfo = questionInfo;
			// 更新考试界面, 显示上一题目
			examFrame.updateView(examInfo, questionInfo);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(examFrame, e.getMessage());
		}
	}

	/**
	 * 交卷
	 */
	public void send() {
		int val = JOptionPane.showConfirmDialog(examFrame, "交卷?");
		if (val == JOptionPane.YES_OPTION) {
			gameOver();
		}
	}

	public void gameOver() {
		try {
			// 获取最后的用户答案
			List<Integer> ans = examFrame.getUserAnswers();
			int index = currentQuestionInfo.getQuestionIndex();
			// 保存用户答案
			examService.saveUserAnswers(index, ans);
			// 考试结束, 得到分数
			int score = examService.examOver();
			// 显示分数
			JOptionPane.showMessageDialog(examFrame, "分数:" + score);
			// 关闭考试界面
			examFrame.setVisible(false);
			// 返回菜单界面
			menuFrame.setVisible(true);
			timer.cancel();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(examFrame, e.getMessage());
		}
	}
	
	private Timer timer = new Timer();

	private void startTimer() {
		final long end = System.currentTimeMillis() + examInfo.getTimeLimit()
				* 1000 * 60;
		timer.schedule(new TimerTask() {
			public void run() {
				long now = System.currentTimeMillis();
				long show = end - now;
				long h = show / 1000 / 60 / 60;
				long m = show / 1000 / 60 % 60;
				long s = show / 1000 % 60;
				examFrame.showTime(h, m, s);
			}
		}, 0, 1000);
		timer.schedule(new TimerTask() {
			public void run() {
				gameOver();
			}
		}, new Date(end));
	}

	public void result() {
		try {

			int score = examService.examOver();
			JOptionPane.showMessageDialog(menuFrame, "分数:" + score);

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(menuFrame, e.getMessage());
		}
	}

	

	private void center(Window win) {
		// Toolkit toolkit = Toolkit.getDefaultToolkit();
		// // toolkit 代表当前绘图系统的工具方法集合
		// Dimension screen = toolkit.getScreenSize();
		// int w = win.getWidth();
		// int h = win.getHeight();
		// int x = (screen.width - w) / 2;
		// int y = (screen.height - h) / 2;
		// win.setLocation(x, y);
	}

	public void showMsg() {
		String file = examService.getMsg();
		msgFrame.showMsg(file);
		msgFrame.setVisible(true);
	}

//	public void showMsg() {
//		String str = examService.getMsg();
//		menuFrame.alertMsg(str);
//	}

}
