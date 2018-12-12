import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList; // ArrayList    
import java.io.*;
import javax.imageio.ImageIO;

public class NuperArio{
    public static void main(String[] args) {
	JFrame fr = new JFrame("Nuper Ario Java");
	
	fr.setSize(900,660);
	fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	fr.getContentPane().setBackground(new Color(200, 255, 255));
	
	Scenepanel panel = new Scenepanel();
	panel.setOpaque(false);
	fr.add(panel);
	fr.setVisible(true);
    }
}

class Scenepanel extends JPanel implements KeyListener{
	Map map;
	Chara mine;
    ArrayList<Image> images = new ArrayList<Image>();
	ArrayList<Chara> enemies = new ArrayList<Chara>();
	int mapx = 200,mapy = 20;

    Scenepanel(){
	/* スタート画面 */

		try {
			// 画像の読み込み
			images.add(ImageIO.read(new File("./graphics/enemy1.gif")));
			images.add(ImageIO.read(new File("./graphics/enemy2.gif")));
			images.add(ImageIO.read(new File("./graphics/enemy3.gif")));
			images.add(ImageIO.read(new File("./graphics/enemy4.gif")));
			images.add(ImageIO.read(new File("./graphics/enemy5.gif")));
			images.add(ImageIO.read(new File("./graphics/enemy6.gif")));
			images.add(ImageIO.read(new File("./graphics/enemy7.gif")));
			images.add(ImageIO.read(new File("./graphics/enemy8.gif")));
			images.add(ImageIO.read(new File("./graphics/enemy9.gif")));
			images.add(ImageIO.read(new File("./graphics/enemy0.gif")));
			images.add(ImageIO.read(new File("./graphics/weapon1.gif")));
			images.add(ImageIO.read(new File("./graphics/weapon2.gif")));
			images.add(ImageIO.read(new File("./graphics/weapon3.gif")));
			images.add(ImageIO.read(new File("./graphics/weapon4.gif")));
			images.add(ImageIO.read(new File("./graphics/weapon5.gif")));
			images.add(ImageIO.read(new File("./graphics/weapon6.gif")));
			images.add(ImageIO.read(new File("./graphics/weapon7.gif")));
			images.add(ImageIO.read(new File("./graphics/weapon8.gif")));
			images.add(ImageIO.read(new File("./graphics/weapon9.gif")));
			images.add(ImageIO.read(new File("./graphics/weapon0.gif")));
			images.add(ImageIO.read(new File("./graphics/goal.gif")));
			images.add(ImageIO.read(new File("./graphics/block.gif")));
			images.add(ImageIO.read(new File("./graphics/wall.gif")));
		} catch (IOException e) {
			System.err.println("ファイルの読み込みに失敗しました．");
		}

		//マップの生成
		map = new Map(mapx,mapy);
		//マップデータ１の読み込み
		try {
			File map1File = new File("./mapdata/map1.txt");
			FileReader fr = new FileReader(map1File);
			int ch;
			for(int y=0;y<mapy;y++){
				for(int x=0;x<mapx;x++){
					ch = fr.read();
					map.setObject(x,y,(char)ch);
				}
			}
			fr.close();
		} catch (IOException e) {
			System.err.println("ファイルの読み込みに失敗しました．");
		}

    }

    public void paintComponent(Graphics g){
		for(int j=0;j<map.map.length;j++){
			for(int i=0;i<map.map[0].length;i++){
				switch(map.map[j][i]){
					case 'B':g.drawImage(images.get(0),30*i,30*j,30,30,this);
					case 'W':g.drawImage(images.get(1),30*i,30*j,30,30,this);
					case 'G':g.drawImage(images.get(2),30*i,30*j,30,30,this);
				}
			}
		}
	}

    @Override
	public void keyPressed(KeyEvent e) {
	int key = e.getKeyCode();
	int dir = -1;
	switch(key){
        case KeyEvent.VK_LEFT: dir = 0; break;// left
        case KeyEvent.VK_RIGHT: dir = 1; break;// right
		case KeyEvent.VK_UP: dir = 2; break;// jump
		case KeyEvent.VK_SPACE: swingWeapon(); break;
		case KeyEvent.VK_Z: searchWeapon(); break; //pick weapon
	}
	//	if(dir>=0) mine.charamove(dir);
	repaint();
	}
	
	void searchWeapon(){
		if(typeof(map.map[mine.curr_panel_y][mine.curr_panel_x])==int){//どうやってint型判定すればいい？
			if(mine.strength < (int)map.map[mine.curr_panel_y][mine.curr_panel_x]){
				mine.strength = (int)map.map[mine.curr_panel_y][mine.curr_panel_x];
			}
		}
	}
	void swingWeapon(){
		if(map.map[mine.curr_panel_y][mine.curr_panel_x+1]=='E'){
			for(int i=0;i<enemies.size();i++){
				if(enemies.get(i).curr_panel_y == mine.curr_panel_y
				&& enemies.get(i).curr_panel_x == mine.curr_panel_x+1
				&& enemies.get(i).strength <= mine.strength){
					enemies.remove(i);
				}
			}
		}
		if(map.map[mine.curr_panel_y][mine.curr_panel_x+1]=='B'){
			map.map[mine.curr_panel_y][mine.curr_panel_x+1]=' ';
		}
	}

    // KeyListenerのメソッドkeyReleased
    @Override
	public void keyReleased(KeyEvent e) { }

    // KeyListenerのメソッドkeyTyped
    @Override
	public void keyTyped(KeyEvent e) { }
}

class Map{
    char[][] map;
    /*
      スペース：移動可能オブジェクト
      W：破壊不可能オブジェクト
      B：破壊可能オブジェクト
	  G：ゴール
	*/

    Map(int x,int y){
		map = new char[y][x];
    }

    void setObject(int x,int y,char s){
		map[y][x] = s;
	}
}

class Chara{
    public int strength;
    public int curr_x;
    public int curr_y;
    public int curr_panel_x;
    public int curr_panel_y;

    //インスタンスを生成した時点で座標、1キャラの種類(整数値)を保持する
    Chara(int x, int y, int i){
	this.strength = i;
	this.curr_x = x;
	this.curr_y = y;
    }

    public void pickWeapon(int i){
	this.strength = i;
	}
}