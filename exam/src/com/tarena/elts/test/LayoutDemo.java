package com.tarena.elts.test;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class LayoutDemo {
  public static void main(String[] args) {
    JFrame frame = new JFrame("布局管理");
    JPanel content = new JPanel(new BorderLayout());
    JPanel bottom = new JPanel(new BorderLayout());
    JPanel btnPanel = new JPanel(new FlowLayout());
    JButton ok = new JButton("OK");
    JButton cancel = new JButton("Cancel");
    btnPanel.add(ok);
    btnPanel.add(cancel);
    bottom.add(BorderLayout.EAST, btnPanel);
    content.add(BorderLayout.SOUTH, bottom);
    frame.setContentPane(content);
    frame.setSize(380, 300);
    frame.setVisible(true);
  }
}





