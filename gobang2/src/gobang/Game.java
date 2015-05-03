package gobang;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import java.awt.geom.*;
import java.awt.event.*;

class PaintPanel extends JPanel {
	JDialog labHeiQi = new JDialog();
    
	JLabel jb1 = new JLabel("你胜利");
    
	JDialog labHongQi = new JDialog();

	JLabel jb2 = new JLabel("你输了");

	public int[][] map;
	public int[] cp;
	public int isSuccess = 0;
	int selectedX = -1, selectedY = -1;
	ArrayList Items = new ArrayList();

	public PaintPanel() {
		setLayout(new BorderLayout());
		ButtonPanel buttonPanel = new ButtonPanel();
		add(buttonPanel, BorderLayout.SOUTH);
		addMouseListener(new MouseHandler());
		map = new int[19][19];
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				map[i][j] = -1;
			}
		}
		cp = new int[2];
		cp[0] = -1;
		cp[1] = -1;
	}

	public void lab(int k) {

		labHeiQi.setLayout(new BorderLayout());
		 labHeiQi.setSize(100, 100); 
		 labHeiQi.setLocationRelativeTo(null);
		 labHeiQi.toFront();
		labHeiQi.add("Center", jb1);
		//labHeiQi.pack();
       
		
		labHongQi.setLayout(new BorderLayout());
		labHongQi.setSize(100, 100);
		labHongQi.setLocationRelativeTo(null);
		labHongQi.toFront();
		labHongQi.add("Center", jb2);
		
		//labHongQi.pack();

		if (k == 1) {

			labHeiQi.setVisible(true);
		} else {

			if (k == 2)
				labHongQi.setVisible(true);
		}
	}

	public void paintComponent(Graphics g) {
		int startX = 50;
		int startY = 50;
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		for (int i = 0; i < 19; i++) {
			g2.draw(new Line2D.Double(startX, startY + 20 * i, startX + 360,
					startY + 20 * i));
			g2.draw(new Line2D.Double(startX + 20 * i, startY, startX + 20 * i,
					startY + 360));
		}
		for (int i = 0; i < Items.size(); i++) {
			int k = (i + 2) % 2;
			if (k == 0) {
				g2.setColor(Color.BLACK);
				if (isSuccess == 1) {
					lab(1);
				}
			} else {
				g2.setColor(Color.RED);
				if (isSuccess == 2) {
					lab(2);
				}
			}
			g2.fill((Ellipse2D) Items.get(i));
		}
	}

	void myRepaint() {
		repaint();
	}

	void reset() {
		isSuccess = 0;
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				map[i][j] = -1;
			}
		}
	}

	class MouseHandler extends MouseAdapter {
		public void mouseMoved(MouseEvent e) {
//			
			selectedX = (e.getX() - 50) / 20;
			selectedY = (e.getY() -50) / 20;
			 System.out.println(selectedY + " " + selectedX);
			
			 repaint();
		}
		
		
		public void mousePressed(MouseEvent event) {
			int x = event.getX();
			int y = event.getY();
			x = (x - 50) / 20;
			y = (y - 50) / 20;
			if (chessExist(x, y) && isSuccess == 0) {
				paintItem(x, y);
				map[x][y] = 1;
				isSuccess = isWin(x, y, 1);
				repaint();
				
				if (isSuccess == 0) {
					repaint();
					
					computer();
					int q = cp[0];
					int r = cp[1];
					paintItem(q, r);
					map[q][r] = 2;
					isSuccess = isWin(q, r, 2);

					
					repaint();
				}
			}
		}

		public void paintItem(int x, int y) {
			x = x * 20 + 50;
			y = y * 20 + 50;
			if (x > 30 && x < 430 && y > 30 && y < 430) {
				int X = x / 20;
				int Y = y / 20;
				int centerX = X * 20 + 10;
				int centerY = Y * 20 + 10;
				Ellipse2D ellipse = new Ellipse2D.Double();
				ellipse.setFrameFromCenter(centerX, centerY, centerX + 8,
						centerY + 8);
				Items.add(ellipse);
			}
		}

		public void computer() {
			int maxblack, maxHong, maxtemp, max = 0;
			for (int i = 1; i < 18; i++) {
				for (int j = 1; j < 18; j++) {
	
					if (chessExist(i, j)) {
						maxblack = CheckMax(i, j, 1);
						maxHong = CheckMax(i, j, 2);
						maxtemp = Math.max(maxblack, maxHong);
						if (maxtemp > max) {
							max = maxtemp;
							cp[0] = i;
							cp[1] = j;
						}
					}
				}
			}
		}

		public int CheckMax(int x, int y, int person) {
			int maxnum = 0;
			int up, down, left, right, align;
			left = right = x;
			up = down = y;

			do {
				up++;
			} while (up >= 0 && map[x][up] == person);

			
			
			do {
				down--;
			} while (down < 19 && map[x][down] == person);

			if ((up - down) > maxnum) {
				maxnum = up - down;
			}
  
			do {
				right++;
			} while (right < 19 && map[align = right][y] == person);
             
			do {
				left--;
			} while (left >= 0 && map[align = left][y] == person);

			if ((right - left) > maxnum) {
				maxnum = right - left;
			}

			left = right = x;
			down = up = y;
			do {
				right++;
				down++;
			} while (right < 19 && down < 19
					&& map[align = right][down] == person);

			do {
				left--;
				up--;
			} while (left >= 0 && up >= 0 && map[align = left][up] == person);

			if ((right - left) > maxnum) {
				maxnum = right - left;
			}

			left = right = x;
			down = up = y;
			do {
				right++;
				down--;
			} while (right < 19 && down >= 0
					&& map[align = right][down] == person);

			do {
				left--;
				up++;
			} while (left >= 0 && up < 19 && map[align = left][up] == person);

			if ((right - left) > maxnum) {
				maxnum = right - left;
			}
			return maxnum;
		}

		public int isWin(int i1, int j1, int a) {// i行j列
			// 保存原有行和列到原始位置
			System.out.println(i1+" "+j1);
			int i = i1;
			int j = j1;
			int c = 0;
			int d = 0;
			int x = 1;
			String w=null;
			if (a == 1) {
				w = "黑棋";
			} else if(a==2){
				w = "白棋";
			}

		
			while (map[i][j] == a) {
				i--;// 行数-1向上
				if (i == -1) {
					break;
				}
			}
			// 2回退
			i++;
			while (map[i][j] == a) {
				c++;
				i++;
				if (i == 19) {
					break;
				}

			}
			System.out.println("上下"+c);
			if (c >= 5) {
				JOptionPane.showMessageDialog(null, w + "胜利");
				System.out.println("恭喜" + a + "获得胜利");
				return a;

			}
			// -----左右方向end
			i = i1;
			j = j1;
			while (map[i][j] == a) {
				j--;// 列数-1向上
				if (j == -1) {
					break;
				}
			}
			// 2回退
			j++;
			while (map[i][j] == a) {
				d++;
				j++;
				if (d == 19) {
					break;
				}

			}
			System.out.println("左右"+d);
			if (d >= 5) {
				JOptionPane.showMessageDialog(null, w + "胜利");
				System.out.println("恭喜" + a + "获得胜利");
				return a;

			}

			// 右上，左下
			i = i1;
			j = j1;
			d = 0;
			while (map[i][j] == a) {
				j--;// 列数-1向上
				i--;
				if (j == -1 || i == -1) {
					break;
				}
			}
			// 2回退
			j++;
			i++;
			while (map[i][j] == a) {
				d++;
				j++;
				i++;
				if (d == 19) {
					break;
				}

			}
			System.out.println("右上左下"+d);
			if (d >= 5) {
				JOptionPane.showMessageDialog(null, w + "胜利");
				System.out.println("恭喜" + a + "获得胜利");
				return a;

			}
			// 左上，右下
			i = i1;
			j = j1;
			d = 0;
			while (map[i][j] == a) {
				j--;// 列数-1向上
				i++;
				if (j == -1 || i == -1) {
					break;
				}
			}
			// 2回退
			j++;
			i--;
			while (map[i][j] == a) {
				d++;
				j++;
				i--;
				if (d == 19) {
					break;
				}

			}
			System.out.println("右下左上"+d);
			if (d >= 5) {
				JOptionPane.showMessageDialog(null, w + "胜利");
				System.out.println("恭喜" + a + "获得胜利");
				return a;

			}

			return 0;
		}

		public boolean chessExist(int i, int j) {
			if (map[i][j] == -1)
				return true;
			else
				return false;
		}
	}

	class ButtonPanel extends JPanel {
		public ButtonPanel() {
			JButton reset = new JButton("新游戏");
			add(reset);
			JButton quit = new JButton("退出");
			add(quit);

			ResetEvent listenerR = new ResetEvent();
			reset.addMouseListener(listenerR);

			QuitEvent listenerQ = new QuitEvent();
			quit.addMouseListener(listenerQ);
		}

		class QuitEvent extends MouseAdapter {
			public void mouseClicked(MouseEvent event) {
				System.exit(1);
			}
		}

		class ResetEvent extends MouseAdapter {
			public void mouseClicked(MouseEvent event) {
				Items.clear();
				myRepaint();
				reset();
			}
		}
	}
}

class GameFrame extends JFrame {
	public GameFrame() {
		setTitle("释然子钦");
		setSize(500, 600);
		setLocationRelativeTo(null);
		setResizable(false);
		PaintPanel panel = new PaintPanel();
		getContentPane().add(panel);
	}
}

public class Game {
	public static void main(String[] args) {
		GameFrame frame = new GameFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);
	}
}