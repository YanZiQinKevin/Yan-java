package com.tarena.elts.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.tarena.elts.entity.User;

/** 主菜单界面 */
public class MenuFrame extends JFrame {

	public MenuFrame() {
		init();
	}

	private void init() {
		setTitle("达内科技在线测评");
		setSize(600, 400);
		setContentPane(createContentPane());
		setLocationRelativeTo(null);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				clientContext.exit(MenuFrame.this);
			}
		});
	}

	private JPanel createContentPane() {
		JPanel pane = new JPanel(new BorderLayout());

		ImageIcon icon = new ImageIcon(this.getClass().getResource("title.png"));

		// MenuFrame.class.getResource("title.png");

		pane.add(BorderLayout.NORTH, new JLabel(icon));

		pane.add(BorderLayout.CENTER, createMenuPane());

		pane.add(BorderLayout.SOUTH,
				new JLabel("达内科技--版权所有 盗版必究", JLabel.RIGHT));

		return pane;
	}

	private JPanel createMenuPane() {
		JPanel pane = new JPanel(new BorderLayout());
		// 务必将info 引用到界面控件对象
		info = new JLabel("XXX 同学您好!", JLabel.CENTER);

		pane.add(BorderLayout.NORTH, info);
		pane.add(BorderLayout.CENTER, createBtnPane());

		return pane;
	}

	private JPanel createBtnPane() {
		JPanel pane = new JPanel(new FlowLayout());
		JButton start = createImgBtn("exam.png", "开始");
		JButton result = createImgBtn("result.png", "分数");
		JButton msg = createImgBtn("message.png", "考试规则");
		JButton exit = createImgBtn("exit.png", "离开");

		pane.add(start);
		pane.add(result);
		pane.add(msg);
		pane.add(exit);
		
		getRootPane().setDefaultButton(start);
		
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientContext.start();
			}
		});
		
//		msg.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent e) {
//				clientContext.showMsg();
//			}
//		});
		
		msg.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				clientContext.showMsg();
			}
		});

		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientContext.exit(MenuFrame.this);
			}
		});

		return pane;
	}

	// 创建图片按钮的方法
	private JButton createImgBtn(String img, String txt) {
		ImageIcon ico = new ImageIcon(this.getClass().getResource(img));

		JButton button = new JButton(txt, ico);
		// button.setIcon(ico);
		// 设置文本相对于图标的垂直位置
		button.setVerticalTextPosition(JButton.BOTTOM);
		// 设置文本相对于图标的水平位置
		button.setHorizontalTextPosition(JButton.CENTER);

		return button;
	}

	private ClientContext clientContext;

	private JLabel info;

	public void updateView(User user) {
		info.setText(user + "同学你好");
	}

	public void setClientContext(ClientContext clientContext) {
		this.clientContext = clientContext;
	}


//	public void alertMsg(String str) {
//		JOptionPane.showMessageDialog(MenuFrame.this, str);
//	}
public static void main(String[] arge){
	new MenuFrame();
}
}
