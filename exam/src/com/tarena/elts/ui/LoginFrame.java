package com.tarena.elts.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/** 登录界面 是一个具体窗口框 */
public class LoginFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoginFrame() {
		init();
	}

	/** 初始化界面组件和布局的 */
	private void init() {
		this.setTitle("登录系统");
		JPanel contentPane = createContentPane();
		this.setContentPane(contentPane);
		// 必须先设大小后居中
		setSize(300, 220);
		setLocationRelativeTo(null);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				clientContext.exit(LoginFrame.this);
			}
		});
//		this.addWindowListener(new WindowListener() {
//			public void windowActivated(WindowEvent e) {//活动完成以后
//				// TODO Auto-generated method stub
//			}
//
//			public void windowClosed(WindowEvent e) {//关闭完成以后
//				// TODO Auto-generated method stub
//			}
//
//			public void windowClosing(WindowEvent e) {//正在关闭的时候
//				clientContext.exit(LoginFrame.this);
//			}
//			
//			public void windowDeactivated(WindowEvent e) {//不活动完成以后
//				// TODO Auto-generated method stub
//			}
//
//			public void windowDeiconified(WindowEvent e) {//窗口不最小化
//				// TODO Auto-generated method stub
//			}
//
//			public void windowIconified(WindowEvent e) {//窗口最小化
//				// TODO Auto-generated method stub
//			}
//
//			public void windowOpened(WindowEvent e) {//打开以后
//				// TODO Auto-generated method stub
//			}
//		});
	}

	private JPanel createContentPane() {
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(new EmptyBorder(8, 8, 8, 8));
		p.add(BorderLayout.NORTH, new JLabel("登录考试系统", JLabel.CENTER));
		p.add(BorderLayout.CENTER, createCenterPane());
		p.add(BorderLayout.SOUTH, createBtnPane());
		return p;
	}

	private JPanel createBtnPane() {
		JPanel p = new JPanel(new FlowLayout());
		JButton login = new JButton("Login");
		JButton cancel = new JButton("Cancel");
		p.add(login);
		p.add(cancel);

		getRootPane().setDefaultButton(login);

		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientContext.login();
			}
		});

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientContext.exit(LoginFrame.this);
			}
		});
		return p;
	}

	private JPanel createCenterPane() {
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(new EmptyBorder(8, 0, 0, 0));
		p.add(BorderLayout.NORTH, createIdPwdPane());
		message = new JLabel("", JLabel.CENTER);

		return p;
	}

	private JPanel createIdPwdPane() {
		JPanel p = new JPanel(new GridLayout(2, 1, 0, 6));
		p.add(createIdPane());
		p.add(createPwdPane());
		return p;
	}

	private JPanel createIdPane() {
		JPanel p = new JPanel(new BorderLayout(6, 0));
		p.add(BorderLayout.WEST, new JLabel("编号:"));
		JTextField idField = new JTextField();
		p.add(BorderLayout.CENTER, idField);
		// 将实例变量idField引用到界面组件上
		this.idField = idField;
		return p;
	}

	/** 简单工厂方法, 封装的复杂对象的创建过程, 返回一个对象实例 */
	private JPanel createPwdPane() {
		JPanel p = new JPanel(new BorderLayout(6, 0));
		p.add(BorderLayout.WEST, new JLabel("密码:"));
		JPasswordField pwdField = new JPasswordField();
		pwdField.enableInputMethods(true);
		p.add(BorderLayout.CENTER, pwdField);
		this.pwdField = pwdField;
		return p;
	}

	private JTextField idField;

	private JPasswordField pwdField;

	private JLabel message;

	private ClientContext clientContext;

	public int getUserId() {
		return Integer.parseInt(idField.getText());
	}

	public String getPassword() {
		char[] pwd = pwdField.getPassword();
		return new String(pwd);
	}

	public void showMessage(String message) {
		this.message.setText(message);
	}

	public void setClientContext(ClientContext clientContext) {
		this.clientContext = clientContext;
	}
}
