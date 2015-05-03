package com.tarena.elts.ui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/** 主菜单界面 */
public class MsgFrame extends JFrame {
	private JTextArea ta ;
	private ClientContext clientContext;
	public MsgFrame() {
		init();
	}

	private void init() {
		setSize(600, 400);
		setContentPane(createContentPane());
		setLocationRelativeTo(null);
	}

	private JScrollPane createContentPane() {
		JScrollPane jsp = new JScrollPane();
		ta = new JTextArea();
		jsp.add(ta);
		jsp.getViewport().add(ta);
		return jsp;
	}

	public void setClientContext(ClientContext clientContext) {
		this.clientContext = clientContext;
	}

	public void showMsg(String file) {
		ta.setText(file);
		ta.setLineWrap(true);// 允许折行显示
		ta.setEditable(false);// 不能够编辑内容
	}
}
