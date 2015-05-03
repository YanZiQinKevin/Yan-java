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

/** �ͻ�������������ͻ��������Ļ��� */
public class ClientContext implements Serializable {
	private static final long serialVersionUID = 1L;

	// private LoginFrame loginFrame = new LoginFrame();

	// ����ǰ���ڻش����Ŀ
	private QuestionInfo currentQuestionInfo;

	private ExamInfo examInfo;

	private WelcomeWindow welcomeWindow;

	/**
	 * private LoginFrame loginFrame;
	 * 
	 * public void setLoginFrame(LoginFrame loginFrame){ this.loginFrame =
	 * loginFrame; } ���������ע��
	 */
	private LoginFrame loginFrame;

	/** ����ע��(IOC) ����ע�����loginFrameʵ�� */
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
	 * ��ʾ����Ŀ�ʼ���棬����ʾ��½���� show()���������У�����������loginFrame(ʵ������)���þ���Ľ������
	 * ��������õĻ�����˶���Ϊ�գ�����ֿ�ָ���쳣 ���������������������setLoginFrame()ע�룬ע������������ʵ��
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
	 * �˷�����login��ť���� ��½ϵͳ�������߼� 1. �ӵ�½�������û���Id & Pwd 2. ����ҵ��ģ�͵�login������ɵ�½���� 3.
	 * ���ݵ�½���������ɹ��͸��²˵����棬��ʾ�û���Ϣ�� �رյ�½���棬�򿪲˵����� 4. �����½ʧ�ܾ��ڵ�½��������ʾʧ����Ϣ
	 */
	public void login() {
		try {
			int id = loginFrame.getUserId();
			String pwd = loginFrame.getPassword();
			User user = examService.login(id, pwd);
			menuFrame.updateView(user);// ���²˵�����
			loginFrame.setVisible(false);
			menuFrame.setVisible(true);
		} catch (IdOrPwdException e) {
			e.printStackTrace();
			loginFrame.showMessage("��½ʧ�ܣ�" + e.getMessage());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			loginFrame.showMessage("ID������������" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			loginFrame.showMessage("��½ʧ�ܣ�" + e.getMessage());
		}
	}

	/**
	 * �˳�ϵͳ �����߼�
	 * 
	 * @param source
	 *            �������һ�������˳�. ��һ�����ڵ�����
	 */
	public void exit(JFrame source) {
		// Confirm ȷ��
		// Dialog �Ի���
		int val = JOptionPane.showConfirmDialog(source, "�뿪��");
		if (val == JOptionPane.YES_OPTION) {
			System.exit(0);// ������ǰJava����
		}
	}

	/**
	 * ��ʼ����
	 * 
	 */
	public void start() {
		try {
			// ����ҵ��㿪ʼ����
			examInfo = examService.start();
			// ȡ�õ�һ����, ������ʾ����
			currentQuestionInfo = examService.getQuestion(0);
			// ���¿��Խ���
			examFrame.updateView(examInfo, currentQuestionInfo);
			// �رղ˵�,
			menuFrame.setVisible(false);
			// �򿪿��Խ���
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
			// ȡ�õ�ǰ�����
			int index = currentQuestionInfo.getQuestionIndex();
			// ���浱ǰ������û��𰸵�ҵ���
			List<Integer> userAnswers = examFrame.getUserAnswers();
			examService.saveUserAnswers(index, userAnswers);
			// ȡ����һ��Ŀ
			QuestionInfo questionInfo = examService.getQuestion(index + 1);
			currentQuestionInfo = questionInfo;
			// ���¿��Խ���, ��ʾ��һ��Ŀ
			examFrame.updateView(examInfo, questionInfo);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(examFrame, e.getMessage());
		}
	}

	public void prev() {
		try {
			// ȡ�õ�ǰ�����
			int index = currentQuestionInfo.getQuestionIndex();
			// ���浱ǰ������û��𰸵�ҵ���
			List<Integer> userAnswers = examFrame.getUserAnswers();
			examService.saveUserAnswers(index, userAnswers);
			// ȡ����һ��Ŀ
			QuestionInfo questionInfo = examService.getQuestion(index - 1);
			currentQuestionInfo = questionInfo;
			// ���¿��Խ���, ��ʾ��һ��Ŀ
			examFrame.updateView(examInfo, questionInfo);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(examFrame, e.getMessage());
		}
	}

	/**
	 * ����
	 */
	public void send() {
		int val = JOptionPane.showConfirmDialog(examFrame, "����?");
		if (val == JOptionPane.YES_OPTION) {
			gameOver();
		}
	}

	public void gameOver() {
		try {
			// ��ȡ�����û���
			List<Integer> ans = examFrame.getUserAnswers();
			int index = currentQuestionInfo.getQuestionIndex();
			// �����û���
			examService.saveUserAnswers(index, ans);
			// ���Խ���, �õ�����
			int score = examService.examOver();
			// ��ʾ����
			JOptionPane.showMessageDialog(examFrame, "����:" + score);
			// �رտ��Խ���
			examFrame.setVisible(false);
			// ���ز˵�����
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
			JOptionPane.showMessageDialog(menuFrame, "����:" + score);

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(menuFrame, e.getMessage());
		}
	}

	

	private void center(Window win) {
		// Toolkit toolkit = Toolkit.getDefaultToolkit();
		// // toolkit ����ǰ��ͼϵͳ�Ĺ��߷�������
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
