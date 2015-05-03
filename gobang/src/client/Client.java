package client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import comment.Protocol;

public class Client {
	// �������̵Ĵ�С
	  protected static int BOARD_SIZE = 15;
	// ����һ����ά�������䵱���̣�����0�ǿ�λ��1�ǰ��ӣ�2�Ǻ���
	protected int[][] table = new int[BOARD_SIZE][BOARD_SIZE];
	protected static final int NOTHING = 0;
	protected static final int W_PLAYER = 1;
	protected static final int B_PLAYER = 2;
	protected static int CURRENT_PLAYER = W_PLAYER;
	public  boolean FIRST = false;//
	private Socket socket;

	// ��������λͼ�ֱ�������̡����ӡ�����
	BufferedImage bg;
	BufferedImage black;
	BufferedImage white;
	// ������ƶ�ʱ���ѡ���
	BufferedImage selected;
	// �������̿��߶������
	private final int TABLE_WIDTH = 549;
	private final int TABLE_HETGHT = 546;

	// �����������������ֵ����������֮��ı��ʡ�
	private final int RATE = TABLE_WIDTH / BOARD_SIZE;
	// �����������������ֵ����������֮���ƫ�ƾࡣ
	private final int X_OFFSET = 5;
	private final int Y_OFFSET = 6;
	// ��������Ϸ�Ĵ���
	// JFrame f = new JFrame("��������Ϸ������");
	// ��ǰѡ�е������
	private int selectedX = -1;
	private int selectedY = -1;
	// ��������Ϸ���̶�Ӧ��Canvas���
	ChessBoard chessBoard = new ChessBoard();

	JFrame frame = new JFrame();

	public void action() throws Exception {
		init();
		socket = new Socket("127.0.0.1", 8889);
		// �����ͻ���
		// �ͷ��ǳ�--> while
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
		PrintWriter pw = new PrintWriter(osw);
		String nice = "";
		boolean stop = true;
		while (stop) {
			// �����ǳ�
			nice = JOptionPane.showInputDialog("�������ǳ�");
			pw.println(Protocol.NICKNAME + nice + Protocol.NICKNAME);
			pw.flush();
			String line = br.readLine();
			if (line.startsWith(Protocol.NICKNAMEREP)) {
				continue;
			} else {
				stop = false;
			}
		}
		frame.setVisible(true);
		new ClientThread(socket).start();

	}

	public void init() throws Exception {

		bg = ImageIO.read(new File("image/board.jpg"));
		black = ImageIO.read(new File("image/black.gif"));
		white = ImageIO.read(new File("image/white.gif"));
		selected = ImageIO.read(new File("image/selected.gif"));
		// �����ʼ�Ĵ�С
		chessBoard.setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HETGHT));
		// ���û�����¼�������ת����������������ꡣ
		frame.add(chessBoard);
		frame.setSize(TABLE_WIDTH, TABLE_HETGHT);
		// this.setVisible(true);
		chessBoard.addMouseListener(new MouseAdapter() {
			// ���û�����¼�������ת����������������ꡣ
			public void mouseClicked(MouseEvent e) {
				if(FIRST == false){return;}
				int x = (int) ((e.getX() - X_OFFSET) / RATE);
				int y = (int) ((e.getY() - Y_OFFSET) / RATE);
				// �ж�����������Ƿ����Ҫ��
				System.out.println(x + ":" + y);
				
				try {
					PrintWriter oo = new PrintWriter(socket.getOutputStream());
					oo.println(Protocol.XY+x+":"+y+Protocol.XY);
					oo.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				CURRENT_PLAYER = (CURRENT_PLAYER == B_PLAYER) ? W_PLAYER
						: B_PLAYER;
				table[x][y] = CURRENT_PLAYER;
				chessBoard.repaint();
				
				//��ֹ
				FIRST = false;
			}
		});
		// ע�����ͣ����Ч��
		chessBoard.addMouseMotionListener(new MouseMotionAdapter() {
			// ������ƶ�ʱ���ı�ѡ�е������
			public void mouseMoved(MouseEvent e) {
				selectedX = (e.getX() - X_OFFSET) / RATE;
				selectedY = (e.getY() - Y_OFFSET) / RATE;
				chessBoard.repaint();
			}
		});

	}

	class ChessBoard extends JPanel {
		private static final long serialVersionUID = 1L;

		// ��дCanvas��paint������ʵ�ֻ滭
		public void paint(Graphics g) {
			// ����������������
			g.drawImage(bg, 0, 0, null);
			// ����ѡ�е�ĺ��
			if (selectedX >= 0 && selectedY >= 0)
				g.drawImage(selected, selectedX * RATE + X_OFFSET, selectedY
						* RATE + Y_OFFSET, null);

			//if (FIRST == true) {
				// �������飬�������ӡ�
				for (int i = 0; i < BOARD_SIZE; i++) {
					for (int j = 0; j < BOARD_SIZE; j++) {
						// ���ƺ���
						if (table[i][j] == B_PLAYER) {
							g.drawImage(black, i * RATE + X_OFFSET, j * RATE
									+ Y_OFFSET, null);
						}
						// ���ư���
						if (table[i][j] == W_PLAYER) {
							g.drawImage(white, i * RATE + X_OFFSET, j * RATE
									+ Y_OFFSET, null);
						}
					}
				}
			}
		}
	//}

	public static void main(String[] args) throws Exception {
		Client wu = new Client();
		wu.action();
	}

	class ClientThread extends Thread {
		private Socket socket;
		private PrintWriter out;
		private BufferedReader br;

		public ClientThread(Socket socket) throws Exception {
			this.socket = socket;
			this.out = new PrintWriter(socket.getOutputStream());
			this.br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		}

		public void run() {
	

			try {
				String line = "";
				while ((line = br.readLine()) != null) {
					if(line.startsWith(Protocol.FIRST)){
						FIRST = true;
					}else if(line.startsWith(Protocol.XY)){
						System.out.println(line);
						String xy = getRealName(line);
						String[] xys = xy.split(":");
						int x =  Integer.parseInt(xys[0]);
						int y =  Integer.parseInt(xys[1]);
						table[x][y] = CURRENT_PLAYER == B_PLAYER ? W_PLAYER : B_PLAYER;
						chessBoard.repaint();
						FIRST = true;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	private String getRealName(String name){
		return name.substring(2,name.length()-2);
	}
}