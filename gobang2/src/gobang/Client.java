package gobang;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 客户端
 * 
 * @author soft01
 * 
 */
public class Client {
	// 棋盘
	BufferedImage bg;
	// 黑子
	BufferedImage black;
	// 白子
	BufferedImage white;
	// 选择
	BufferedImage selected;
	// 窗口
	JFrame frame = new JFrame();
	// 画版
	ChessBoard board = new ChessBoard();

	int selectedX = -1, selectedY = -1;
	private int RATE = 535 / 15;
	private int X_OFFSET = 5;
	private int Y_OFFSET = 5;

	private int[][] table = new int[15][15];

	private final int NOTHING = 0;
	private final int W = 1;
	private final int B = 2;
	// 创建当前玩家的棋子的颜色
	private int Current = W;
	Socket socket = null;
	boolean stop = false;

	class ChessBoard extends JPanel {

		private static final long serialVersionUID = 1L;

		public void paint(Graphics g) {
			g.drawImage(bg, 0, 0, null);
			g.drawImage(selected, selectedX * RATE + X_OFFSET, selectedY * RATE
					+ Y_OFFSET, null);
			// 所有玩家落子的位置保存在数组
			for (int i = 0; i < 15; i++) {
				for (int j = 0; j < 15; j++) {
					if (table[i][j] == B) {
						g.drawImage(black, i * RATE + X_OFFSET, j * RATE
								+ Y_OFFSET, null);
					}
					if (table[i][j] == W) {
						g.drawImage(white, i * RATE + X_OFFSET, j * RATE
								+ Y_OFFSET, null);
					}
				}
			}
		}
	}

	public Client() throws Exception {
		// 初始化棋盘
		bg = ImageIO.read(new File("images/board.jpg"));
		black = ImageIO.read(new File("images/black.gif"));
		white = ImageIO.read(new File("images/white.gif"));
		selected = ImageIO.read(new File("images/selected.gif"));

		board.setPreferredSize(new Dimension(535, 536));
		frame.add(board);

		frame.setSize(549, 566);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		board.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {

				selectedX = (e.getX() - X_OFFSET) / RATE;
				selectedY = (e.getY() - Y_OFFSET) / RATE;
				// System.out.println(selectedY + " " + selectedX);
				board.repaint();
			}
		});

		board.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println(stop);

				if (stop == false) {
					return;
				}
				System.out.println("###");
				int x = (e.getX() - X_OFFSET) / RATE;
				int y = (e.getY() - Y_OFFSET) / RATE;
				if (table[x][y] != NOTHING) {
					JOptionPane.showMessageDialog(null, "GUN!");
					return;
				}
				table[x][y] = Current;
				board.repaint();
				stop = false;

				// 发坐标
				PrintWriter out = null;
				try {
					out = new PrintWriter(socket.getOutputStream());

					out.println(Protocol.XY + x + "," + y + Protocol.XY);
					out.flush();
					if (isWin(x, y, table[x][y]) == true) {
						JOptionPane.showMessageDialog(null, "再来一局");

						out = new PrintWriter(socket.getOutputStream());
						out.println(Protocol.XY + (-1) + "," + (-1)
								+ Protocol.XY);
						out.flush();

						for (int i = 0; i < 15; i++) {
							Arrays.fill(table[i], 0);
						}
                         
						board.repaint();
                   return;
					}

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		});

		socket = new Socket("127.0.0.1", 8889);
		BufferedReader br = null;
		PrintWriter pw = null;

		boolean stop = true;
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		pw = new PrintWriter(socket.getOutputStream());
		while (stop) {
			String nice = JOptionPane.showInputDialog("昵称");
			pw.println(Protocol.NICK + nice + Protocol.NICK);
			pw.flush();
			String line = br.readLine();
			if (line.startsWith(Protocol.NICKREP)) {
				continue;
			} else {

				stop = false;
			}
		}
		new ClientThread(socket).start();

	}

	// 线程内部类
	class ClientThread extends Thread {
		private Socket s;

		public ClientThread(Socket s) {
			this.s = s;
		}

		@Override
		public void run() {
			super.run();
			PrintWriter out = null;
			BufferedReader br = null;
			try {
				out = new PrintWriter(s.getOutputStream());
				br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String input = "";
				while ((input = br.readLine()) != null) {
					if (input.startsWith(Protocol.FIRST)) {
						Current = B;
						stop = true;
						System.out.println(1 + "" + stop);
					} else if (input.startsWith(Protocol.XY)) {
                    
						input = getRealName(input);
						String[] ij = input.split(",");
						int i = Integer.parseInt(ij[0]);
						int j = Integer.parseInt(ij[1]);
						
						
						if (i == (-1)) {

							JOptionPane.showMessageDialog(null, "你输了");
							JOptionPane.showMessageDialog(null, "重新开始，输者先行");
							for (int i1 = 0; i1 < 15; i1++) {
								Arrays.fill(table[i1], 0);
							}

							board.repaint();
							continue;
						}
						
						
						
						table[i][j] = Current == B ? W : B;
						
						board.repaint();
						stop = true;
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String getRealName(String name) {
		return name.substring(2, name.length() - 2);
	}

	public static void main(String[] arge) throws Exception {
		new Client();
	}

	public boolean isWin(int i1, int j1, int a) {// i行j列
		// 保存原有行和列到原始位置
		int i = i1;
		int j = j1;
		int c = 0;
		int d = 0;
		int x = 1;
		String w;
		if (a == 1) {
			w = "白棋";
		} else {
			w = "黑棋";
		}

		// c==5.win
		// ------左右方向start
		// up
		while (table[i][j] == a) {
			i--;// 行数-1向上
			if (i == -1) {
				break;
			}
		}
		// 2回退
		i++;
		while (table[i][j] == a) {
			c++;
			i++;
			if (i == 15) {
				break;
			}

		}
		if (c >= 5) {
			JOptionPane.showMessageDialog(null, w + "胜利");
			System.out.println("恭喜" + a + "获得胜利");
			return true;

		}
		// -----左右方向end
		i = i1;
		j = j1;
		while (table[i][j] == a) {
			j--;// 列数-1向上
			if (j == -1) {
				break;
			}
		}
		// 2回退
		j++;
		while (table[i][j] == a) {
			d++;
			j++;
			if (d == 15) {
				break;
			}

		}
		if (d >= 5) {
			JOptionPane.showMessageDialog(null, w + "胜利");
			System.out.println("恭喜" + a + "获得胜利");
			return true;

		}

		// 右上，左下
		i = i1;
		j = j1;
		d = 0;
		while (table[i][j] == a) {
			j--;// 列数-1向上
			i--;
			if (j == -1 || i == -1) {
				break;
			}
		}
		// 2回退
		j++;
		i++;
		while (table[i][j] == a) {
			d++;
			j++;
			i++;
			if (d == 15) {
				break;
			}

		}
		if (d >= 5) {
			JOptionPane.showMessageDialog(null, w + "胜利");
			System.out.println("恭喜" + a + "获得胜利");
			return true;

		}

		// 左上，右下
		i = i1;
		j = j1;
		d = 0;
		while (table[i][j] == a) {
			j--;// 列数-1向上
			i++;
			if (j == -1 || i == -1) {
				break;
			}
		}
		// 2回退
		j++;
		i--;
		while (table[i][j] == a) {
			d++;
			j++;
			i--;
			if (d == 15) {
				break;
			}

		}
		if (d >= 5) {
			JOptionPane.showMessageDialog(null, w + "胜利");
			System.out.println("恭喜" + a + "获得胜利");
			return true;

		}

		return false;
	}

}