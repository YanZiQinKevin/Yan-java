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
public class AiClient {
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
	public int[] cp;
	
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

	public AiClient() throws Exception {
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
					
					computer();
					int q=cp[0];
					int r=cp[1];

					out.println(Protocol.XY + q + "," + r + Protocol.XY);
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
		new AiClient();
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
	public void computer(){ //计算机走棋
		 cp=new int [2];
			cp[0]=0;
			cp[1]=0;
		int maxblack,maxHong,maxtemp,max=0;
		for(int i=1;i<18;i++){
		for(int j=1;j<18;j++){
		if(chessExist(i,j)){//判断是否可以下子
		maxblack=CheckMax(i,j,1);
		maxHong=CheckMax(i,j,2);
		maxtemp=Math.max(maxblack,maxHong);
		if(maxtemp>max){
		max=maxtemp;
		cp[0]=i;
		cp[1]=j;
		}
		}
		}
		}
	}
	public int CheckMax(int x,int y,int person){//判断某一点四个方向上棋子的最大值
		int maxnum=0;
		int up,down,left,right,align;
		left=right=x;
		up=down=y;

		do{
		up++;
		}
		while(up>=0&&table[x][up]==person);

		do{
		down--;
		}
		while(down<15&&table[x][down]==person);

		if((up-down)>maxnum){
		maxnum=up-down;
		}

		do{
		right++;
		}
		while(right<15&&table[align=right][y]==person);

		do{
		left--;
		}
		while(left>=0&&table[align=left][y]==person);

		if((right-left)>maxnum){
		maxnum=right-left;
		}

		left=right=x;
		down=up=y;
		do{
		right++;
		down++;
		}
		while(right<15&&down<19&&table[align=right][down]==person);

		do{
		left--;
		up--;
		}
		while(left>=0&&up>=0&&table[align=left][up]==person);

		if((right-left)>maxnum){
		maxnum=right-left;
		}

		left=right=x;
		down=up=y;
		do{
		right++;
		down--;
		}
		while(right<15&&down>=0&&table[align=right][down]==person);

		do{
		left--;
		up++;
		}
		while(left>=0&&up<15&&table[align=left][up]==person);

		if((right-left)>maxnum){
		maxnum=right-left;
		}
		return maxnum;
		}

	public boolean chessExist(int i,int j){//判断该格是否有棋子
		if(table[i][j]==0)
		return true;
		else
		return false;
		
		}
}