package com.tarena.elts.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.LineBorder;

/** иафа */
public class WelcomeWindow extends JWindow {

  private static final long serialVersionUID = -2970626103134592561L;
  public WelcomeWindow() {
    init();
  }
  private void init(){
    setSize(430, 300);
    JPanel pane = new JPanel(new BorderLayout());
    ImageIcon ico = new ImageIcon(
        getClass().getResource("welcome.png"));
    JLabel l = new JLabel(ico);
    pane.add(BorderLayout.CENTER, l);
    pane.setBorder(new LineBorder(Color.GRAY));
    setContentPane(pane);
    setLocationRelativeTo(null);
  }
  public static void main(String[] args) {
    WelcomeWindow w = new WelcomeWindow();
    w.setVisible(true);
  }
}




