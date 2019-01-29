import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;
import javax.imageio.ImageIO;

public class NuperArio{
    public static void main(String[] args) {
	JFrame fr = new JFrame("Nuper Ario Java");
	
	fr.setSize(900,690);
	fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	fr.getContentPane().setBackground(new Color(200, 255, 255));
	
	Gamepanel panel = new Gamepanel();
	panel.setOpaque(false);
	fr.add(panel);
	fr.setVisible(true);
	}
}

class Gamepanel extends JPanel implements KeyListener,Runnable{
	Map map;
	Chara mine = new Chara(5850,540,0,2,map,0,0,bullets,0);
    ArrayList<Image> images = new ArrayList<Image>();
	ArrayList<Chara> enemies = new ArrayList<Chara>();
	ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	static ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	ArrayList<EnemyB> enemies2 = new ArrayList<EnemyB>();
	ArrayList<WarpMachine> warps = new ArrayList<WarpMachine>();
	ArrayList<MineBlock> blocks = new ArrayList<MineBlock>();
	ArrayList<Laser> laser = new ArrayList<Laser>(); 

	int mapx,mapy;
	int mode = 0;
	int curr_mode = 2;
	Thread th;
	GameScore gs;
	Gravity gr;
	boolean boo = true;
	int gra = 0;
	boolean br = false;
	int life = 5;
	boolean flag = true;
	boolean swing = false;

	int nowgs;
	float nowtime;
	float restBlock;
	int defaultlife = 5;

	int cooltime = 0;

    Gamepanel(){
		setFocusable(true);
		addKeyListener(this);
		requestFocus();

		try {
			images.add(ImageIO.read(new File("./graphics/mine.png")));
			images.add(ImageIO.read(new File("./graphics/objects.png")));
			images.add(ImageIO.read(new File("./graphics/enemies.png")));
			images.add(ImageIO.read(new File("./graphics/bullets.png")));
			images.add(ImageIO.read(new File("./graphics/enemies2.png")));
			images.add(ImageIO.read(new File("./graphics/mineblock.png")));
			images.add(ImageIO.read(new File("./graphics/mine2.png")));
		} catch (IOException e) {
			System.err.println("ファイルの読み込みに失敗しました．");
		}
    }

    public void paintComponent(Graphics g){
		if(mode == 1||mode == 2||mode == 3){
			//mapの描画
					/*(デバッグ用)各Charaのカレントパネルを表示させる
					if(true){
						g.setColor(new Color(255,0,0));
						g.drawRect(mine.curr_panel_x*30,mine.curr_panel_y*30,30,30);
					}*/
			int st,wx = 0,wy = 0,ex = 0,ey = 0,bx = 0,by = 0,gra = 0;
			if(mine.curr_panel_x<=15){
				for(int j=0;j<20;j++){
					for(int i=0;i<30;i++){
						switch(map.map[j+1][i]){
							case 'B':g.drawImage(images.get(1),30*i,30*j,30*i+30,30*j+30,90,0,120,30,this); break;
							case 'W':g.drawImage(images.get(1),30*i,30*j,30*i+30,30*j+30,120,0,150,30,this); break;
							case 'G':g.drawImage(images.get(1),30*i,30*j,30*i+30,30*j+30,60,90,90,120,this); break;
							case 'C':g.drawImage(images.get(1),30*i,30*j,30*i+30,30*j+30,0,0,30,30,this); break;
							case 'S':g.drawImage(images.get(1),30*i,30*j,30*i+30,30*j+30,60,30,90,60,this); break;
							case 'F':g.drawImage(images.get(1),30*i,30*j,30*i+30,30*j+30,30,30,60,60,this); break;
							case 'T':g.drawImage(images.get(1),30*i,30*j,30*i+30,30*j+30,0,30,30,60,this); break;
							case 'H':g.drawImage(images.get(1),30*i,30*j,30*i+30,30*j+30,90,90,120,120,this); break;
							case 'M':
							for(int k=0;k<blocks.size();k++){
								if(blocks.get(k).curr_panel_x == i && blocks.get(k).curr_panel_y == j+1){
									life = blocks.get(k).life;
									if(life == 3){
										g.drawImage(images.get(5),30*i,30*j,30*i+30,30*j+30,0,0,30,30,this);
									}else if(life > 1){
										g.drawImage(images.get(5),30*i,30*j,30*i+30,30*j+30,30,0,60,30,this);
									}else if(life == 1){
										g.drawImage(images.get(5),30*i,30*j,30*i+30,30*j+30,60,0,90,30,this);
									}
									break;
								}
							}
							break;
						}
					}
				}
				for(int i=0;i<enemies.size();i++){
					if(enemies.get(i).curr_x<=900){
						gra = enemies.get(i).gra/50; 
						switch(enemies.get(i).strength){
							case 1: ex = 0; ey = 0; break;
							case 2: if(enemies.get(i).vector == -1){ ex = 30 + gra%2*30; ey = 0;}else{ ex = 90 + gra%2*30; ey = 0;} break;
							case 3: ex = 150 + gra%2*30; ey = 0; break;
							case 4: ex = 0 + (gra/5)%4*30; ey = 30; break;
							case 5: ex = 120; ey = 30; break;
							case 6: ex = 150 + gra%2*30; ey = 30; break;
							case 7: ex = 0 + (gra/5)%2*30; ey = 60; break;
							case 10: ex = 210; ey = 30; break;
						}
						if(enemies.get(i).cooltime > 1000 || (int)(enemies.get(i).cooltime/100) % 3 != 0){
							g.drawImage(images.get(2),(int)enemies.get(i).curr_x,(int)enemies.get(i).curr_y-30,
							(int)enemies.get(i).curr_x+30,(int)enemies.get(i).curr_y,ex,ey,ex+30,ey+30,this);
						}
						enemies.get(i).gra++;
					}
				}	
				for(int i=0;i<enemies2.size();i++){
					if(enemies2.get(i).curr_x<=900){
						gra = enemies2.get(i).gra/50; 
						switch(enemies2.get(i).strength){
							case 7: ex = 0 + gra%2*60; ey = 60; break;
							case 8: if(enemies2.get(i).vector == -1){ ex = 0; ey = 120;}else{ ex = 60; ey = 120;} break;
							case 9: if(enemies2.get(i).vector == -1){ ex = 0; ey = 0;}else{ ex = 60; ey = 0;} break;
						}
						if(enemies2.get(i).cooltime > 1000 || (int)(enemies2.get(i).cooltime/100) % 3 != 0){
							g.drawImage(images.get(4),(int)enemies2.get(i).curr_x,(int)enemies2.get(i).curr_y-30,
							(int)enemies2.get(i).curr_x+60,(int)enemies2.get(i).curr_y+30,ex,ey,ex+60,ey+60,this);
						}
						if(enemies2.get(i).strength == 8){
							if(enemies2.get(i).life == 1){
								g.drawImage(images.get(4),(int)enemies2.get(i).sub_x,(int)enemies2.get(i).sub_y-30,
								(int)enemies2.get(i).sub_x+60,(int)enemies2.get(i).sub_y+30,ex,ey,ex+60,ey+60,this);
								g.drawImage(images.get(4),(int)enemies2.get(i).subsub_x,(int)enemies2.get(i).subsub_y-30,
								(int)enemies2.get(i).subsub_x+60,(int)enemies2.get(i).subsub_y+30,ex,ey,ex+60,ey+60,this);		
							}
						}
						enemies2.get(i).gra++;
					}
				}	
				for(int i=0;i<weapons.size();i++){
					if(weapons.get(i).curr_x<=900){
						st = weapons.get(i).strength;
						if(st <= 2){
							wx = 60 + 30*st;
							wy = 30;
						}else if(st == 6){
							g.drawImage(images.get(1),weapons.get(i).curr_x,weapons.get(i).curr_y-30,
							weapons.get(i).curr_x+30,weapons.get(i).curr_y,0,0,30,30,this);	
							continue;
						}else if(st < 6){
							wx = 30*(st-3);
							wy = 60;
						}else if(st < 9){
							wx = 30*(st-4);
							wy = 60;
						}else{
							wx = 30*(st-9);
							wy = 90;
						}
						g.drawImage(images.get(1),weapons.get(i).curr_x,weapons.get(i).curr_y-30,
						weapons.get(i).curr_x+30,weapons.get(i).curr_y,wx,wy,wx+30,wy+30,this);
					}
				}	
				for(int i=0;i<bullets.size();i++){
					if(bullets.get(i).curr_x<=900){
						gra = bullets.get(i).gra/50; 
						switch(bullets.get(i).kind){
							case 1: ex = 0; ey = 0; break;
							case 2: ex = 30; ey = 0; break;
							case 3: ex = 60 + gra%2*30; ey = 0; break;
							case 4: ex = 0 + gra%2*30; ey = 30; break;
							case 5: ex = 60 + gra%2*30; ey = 30; break;
						}
						g.drawImage(images.get(3),(int)bullets.get(i).curr_x,(int)bullets.get(i).curr_y-30,
						(int)bullets.get(i).curr_x+30,(int)bullets.get(i).curr_y,ex,ey,ex+30,ey+30,this);
						bullets.get(i).gra++;
					}
				}	
				for(int i=0;i<warps.size();i++){
					g.drawImage(images.get(1),warps.get(i).from_x*30,warps.get(i).from_y*30-30,
					warps.get(i).from_x*30+30,warps.get(i).from_y*30,120,90,150,120,this);
					g.drawImage(images.get(1),warps.get(i).to_x*30,warps.get(i).to_y*30-30,
					warps.get(i).to_x*30+30,warps.get(i).to_y*30,120,90,150,120,this);
				}	
				if(mine.cooltime > 2000 || (int)(mine.cooltime/100) % 3 != 0){
					minedraw((int)mine.curr_x,(int)mine.curr_y-30,g);					
				}
				if(false){
					g.setColor(new Color(255,0,0));
					g.drawRect((int)mine.curr_panel_x*30,((int)mine.curr_panel_y-1)*30,30,30);
				}	
			}else if(mine.curr_x>=(mapx-15)*30){
				for(int i=0;i<laser.size();i++){
					if(laser.get(i).cooltime < 1000){
						g.setColor(Color.gray);
						if(laser.get(i).value == 'x'){
							g.fillRect(0,(laser.get(i).pos-1)*30,900,laser.get(i).width*30);
						}else{
							g.fillRect(laser.get(i).pos*30-5080,0,laser.get(i).width*30,660);
						}
					}
				}
				for(int j=0;j<20;j++){
					for(int i=-1;i<30;i++){
						switch(map.map[j+1][i+mapx-30]){
							case 'B':g.drawImage(images.get(1),30*i+20,30*j,30*i+50,30*j+30,90,0,120,30,this); break;
							case 'W':g.drawImage(images.get(1),30*i+20,30*j,30*i+50,30*j+30,120,0,150,30,this); break;
							case 'G':g.drawImage(images.get(1),30*i+20,30*j,30*i+50,30*j+30,60,90,90,120,this); break;
							case 'C':g.drawImage(images.get(1),30*i+20,30*j,30*i+50,30*j+30,0,0,30,30,this); break;
							case 'S':g.drawImage(images.get(1),30*i+20,30*j,30*i+50,30*j+30,60,30,90,60,this); break;
							case 'F':g.drawImage(images.get(1),30*i+20,30*j,30*i+50,30*j+30,30,30,60,60,this); break;
							case 'T':g.drawImage(images.get(1),30*i+20,30*j,30*i+50,30*j+30,0,30,30,60,this); break;
							case 'H':g.drawImage(images.get(1),30*i+20,30*j,30*i+50,30*j+30,90,90,120,120,this); break;
							case 'M':
							for(int k=0;k<blocks.size();k++){
								if(blocks.get(k).curr_panel_x == i+mapx-30 && blocks.get(k).curr_panel_y == j+1){
									life = blocks.get(k).life;
									if(life == 3){
										g.drawImage(images.get(5),30*i+20,30*j,30*i+50,30*j+30,0,0,30,30,this);
									}else if(life > 1){
										g.drawImage(images.get(5),30*i+20,30*j,30*i+50,30*j+30,30,0,60,30,this);
									}else if(life == 1){
										g.drawImage(images.get(5),30*i+20,30*j,30*i+50,30*j+30,60,0,90,30,this);
									}
									break;
								}
							}
							break;
						}
					}
				}
				for(int i=0;i<enemies.size();i++){
					if(enemies.get(i).curr_x>mapx*30-900){
						gra = enemies.get(i).gra/50; 
						switch(enemies.get(i).strength){
							case 1: ex = 0; ey = 0; break;
							case 2: if(enemies.get(i).vector == -1){ ex = 30 + gra%2*30; ey = 0;}else{ ex = 90 + gra%2*30; ey = 0;} break;
							case 3: ex = 150 + gra%2*30; ey = 0; break;
							case 4: ex = 0 + (gra/5)%4*30; ey = 30; break;
							case 5: ex = 120; ey = 30; break;
							case 6: ex = 150 + gra%2*30; ey = 30; break;
							case 7: ex = 0 + (gra/5)%2*30; ey = 60; break;
							case 10: ex = 210; ey = 30; break;
						}
						if(enemies.get(i).cooltime > 1000 || (int)(enemies.get(i).cooltime/100) % 3 != 0){
							g.drawImage(images.get(2),(int)enemies.get(i).curr_x-5080,(int)enemies.get(i).curr_y-30,
							(int)enemies.get(i).curr_x-5050,(int)enemies.get(i).curr_y,ex,ey,ex+30,ey+30,this);
						}
						enemies.get(i).gra++;
					}
				}	
				for(int i=0;i<enemies2.size();i++){
					if(enemies2.get(i).curr_x>mapx*30-900){
						gra = enemies2.get(i).gra/50; 
						switch(enemies2.get(i).strength){
							case 7: ex = 0 + gra%2*60; ey = 60; break;
							case 8: if(enemies2.get(i).vector == -1){ ex = 0; ey = 120;}else{ ex = 60; ey = 120;} break;
							case 9: if(enemies2.get(i).vector == -1){ ex = 0; ey = 0;}else{ ex = 60; ey = 0;} break;
						}
						if(enemies2.get(i).cooltime > 1000 || (int)(enemies2.get(i).cooltime/100) % 3 != 0){
							g.drawImage(images.get(4),(int)enemies2.get(i).curr_x-5080,(int)enemies2.get(i).curr_y-30,
							(int)enemies2.get(i).curr_x-5020,(int)enemies2.get(i).curr_y+30,ex,ey,ex+60,ey+60,this);
						}
						enemies2.get(i).gra++;
					}
				}	
				for(int i=0;i<weapons.size();i++){
					if(weapons.get(i).curr_x>mapx*30-900){
						st = weapons.get(i).strength;
						if(st <= 2){
							wx = 60 + 30*st;
							wy = 30;
						}else if(st == 6){
							g.drawImage(images.get(1),weapons.get(i).curr_x-5080,weapons.get(i).curr_y-30,
							weapons.get(i).curr_x-5050,weapons.get(i).curr_y,0,0,30,30,this);	
							continue;
						}else if(st < 6){
							wx = 30*(st-3);
							wy = 60;
						}else if(st < 9){
							wx = 30*(st-4);
							wy = 60;
						}else{
							wx = 30*(st-9);
							wy = 90;
						}
						g.drawImage(images.get(1),weapons.get(i).curr_x-5080,weapons.get(i).curr_y-30,
						weapons.get(i).curr_x-5050,weapons.get(i).curr_y,wx,wy,wx+30,wy+30,this);
					}
				}	
				for(int i=0;i<bullets.size();i++){
					if(bullets.get(i).curr_x>mapx*30-900){
						gra = bullets.get(i).gra/50; 
						switch(bullets.get(i).kind){
							case 1: ex = 0; ey = 0; break;
							case 2: ex = 30; ey = 0; break;
							case 3: ex = 60 + gra%2*30; ey = 0; break;
							case 4: ex = 0 + gra%2*30; ey = 30; break;
							case 5: ex = 60 + gra%2*30; ey = 30; break;
						}
						g.drawImage(images.get(3),(int)bullets.get(i).curr_x-5080,(int)bullets.get(i).curr_y-30,
						(int)bullets.get(i).curr_x-5050,(int)bullets.get(i).curr_y,ex,ey,ex+30,ey+30,this);
						bullets.get(i).gra++;
					}
				}
				for(int i=0;i<warps.size();i++){
					g.drawImage(images.get(1),warps.get(i).from_x*30-5080,warps.get(i).from_y*30-30,
					warps.get(i).from_x*30-5050,warps.get(i).from_y*30,120,90,150,120,this);
					g.drawImage(images.get(1),warps.get(i).to_x*30-5080,warps.get(i).to_y*30-30,
					warps.get(i).to_x*30-5050,warps.get(i).to_y*30,120,90,150,120,this);
				}	
				if(mine.cooltime > 2000 || (int)(mine.cooltime/100) % 3 != 0){
					minedraw((int)mine.curr_x-(mapx-30)*30+20,(int)mine.curr_y-30,g);
				}
				for(int i=0;i<laser.size();i++){
					if(laser.get(i).cooltime >= 1000){
						g.setColor(Color.yellow);
						if(laser.get(i).value == 'x'){
							g.fillRect(0,(laser.get(i).pos-1)*30,900,laser.get(i).width*30);
						}else{
							g.fillRect(laser.get(i).pos*30-5080,0,laser.get(i).width*30,660);
						}
					}
				}
				if(false){
					g.setColor(new Color(255,0,0));
					g.drawRect((int)mine.curr_panel_x*30-(mapx-30)*30+20,((int)mine.curr_panel_y-1)*30,30,30);
				}	
			}else{
				for(int i=0;i<laser.size();i++){
					if(laser.get(i).cooltime < 1000){
						g.setColor(Color.gray);
						if(laser.get(i).value == 'x'){
							g.fillRect(0,(laser.get(i).pos-1)*30,900,laser.get(i).width*30);
						}else{
							g.fillRect(laser.get(i).pos*30-(int)mine.curr_x+465,0,laser.get(i).width*30,660);
						}
					}
				}
				for(int j=0;j<20;j++){
					for(int i=mine.curr_panel_x-1;i<mine.curr_panel_x+30;i++){
						switch(map.map[j+1][i-15]){
							case 'B':g.drawImage(images.get(1),30*i-(int)mine.curr_x+15,30*j,30*i-(int)mine.curr_x+45,30*j+30,90,0,120,30,this); break;
							case 'W':g.drawImage(images.get(1),30*i-(int)mine.curr_x+15,30*j,30*i-(int)mine.curr_x+45,30*j+30,120,0,150,30,this); break;
							case 'G':g.drawImage(images.get(1),30*i-(int)mine.curr_x+15,30*j,30*i-(int)mine.curr_x+45,30*j+30,60,90,90,120,this); break;
							case 'C':g.drawImage(images.get(1),30*i-(int)mine.curr_x+15,30*j,30*i-(int)mine.curr_x+45,30*j+30,0,0,30,30,this); break;
							case 'S':g.drawImage(images.get(1),30*i-(int)mine.curr_x+15,30*j,30*i-(int)mine.curr_x+45,30*j+30,60,30,90,60,this); break;
							case 'F':g.drawImage(images.get(1),30*i-(int)mine.curr_x+15,30*j,30*i-(int)mine.curr_x+45,30*j+30,30,30,60,60,this); break;
							case 'T':g.drawImage(images.get(1),30*i-(int)mine.curr_x+15,30*j,30*i-(int)mine.curr_x+45,30*j+30,0,30,30,60,this); break;
							case 'H':g.drawImage(images.get(1),30*i-(int)mine.curr_x+15,30*j,30*i-(int)mine.curr_x+45,30*j+30,90,90,120,120,this); break;
							case 'M':
							for(int k=0;k<blocks.size();k++){
								if(blocks.get(k).curr_panel_x == i-15 && blocks.get(k).curr_panel_y == j+1){
									life = blocks.get(k).life;
									if(life == 3){
										g.drawImage(images.get(5),30*i-(int)mine.curr_x+15,30*j,30*i-(int)mine.curr_x+45,30*j+30,0,0,30,30,this);
									}else if(life > 1){
										g.drawImage(images.get(5),30*i-(int)mine.curr_x+15,30*j,30*i-(int)mine.curr_x+45,30*j+30,30,0,60,30,this);
									}else if(life == 1){
										g.drawImage(images.get(5),30*i-(int)mine.curr_x+15,30*j,30*i-(int)mine.curr_x+45,30*j+30,60,0,90,30,this);
									}
									break;
								}
							}
							break;
						}
					}
				}
				for(int i=0;i<enemies.size();i++){
					if(enemies.get(i).curr_x>mine.curr_x-500&&enemies.get(i).curr_x<mine.curr_x+500){
						gra = enemies.get(i).gra/50; 
						switch(enemies.get(i).strength){
							case 1: ex = 0; ey = 0; break;
							case 2: if(enemies.get(i).vector == -1){ ex = 30 + gra%2*30; ey = 0;}else{ ex = 90 + gra%2*30; ey = 0;} break;
							case 3: ex = 150 + gra%2*30; ey = 0; break;
							case 4: ex = 0 + (gra/5)%4*30; ey = 30; break;
							case 5: ex = 120; ey = 30; break;
							case 6: ex = 150 + gra%2*30; ey = 30; break;
							case 7: ex = 0 + (gra/5)%2*30; ey = 60; break;
							case 10: ex = 210; ey = 30; break;
						}
						if(enemies.get(i).cooltime > 1000 || (int)(enemies.get(i).cooltime/100) % 3 != 0){
							g.drawImage(images.get(2),(int)enemies.get(i).curr_x-(int)mine.curr_x+465,(int)enemies.get(i).curr_y-30,
							(int)enemies.get(i).curr_x-(int)mine.curr_x+495,(int)enemies.get(i).curr_y,ex,ey,ex+30,ey+30,this);
						}
						enemies.get(i).gra++;
					}
				}	
				for(int i=0;i<enemies2.size();i++){
					if(enemies2.get(i).curr_x>mine.curr_x-2000&&enemies2.get(i).curr_x<mine.curr_x+2000){
						gra = enemies2.get(i).gra/50; 
						switch(enemies2.get(i).strength){
							case 7: ex = 0 + gra%2*60; ey = 60; break;
							case 8: if(enemies2.get(i).vector == -1){ ex = 0; ey = 120;}else{ ex = 60; ey = 120;} break;
							case 9: if(enemies2.get(i).vector == -1){ ex = 0; ey = 0;}else{ ex = 60; ey = 0;} break;
						}
						if(enemies2.get(i).cooltime > 1000 || (int)(enemies2.get(i).cooltime/100) % 3 != 0){
							g.drawImage(images.get(4),(int)enemies2.get(i).curr_x-(int)mine.curr_x+465,(int)enemies2.get(i).curr_y-30,
							(int)enemies2.get(i).curr_x-(int)mine.curr_x+525,(int)enemies2.get(i).curr_y+30,ex,ey,ex+60,ey+60,this);
						}
						if(enemies2.get(i).strength == 8){
							if(enemies2.get(i).life == 1){
								g.drawImage(images.get(4),(int)enemies2.get(i).sub_x-(int)mine.curr_x+465,(int)enemies2.get(i).sub_y-30,
								(int)enemies2.get(i).sub_x-(int)mine.curr_x+525,(int)enemies2.get(i).sub_y+30,ex,ey,ex+60,ey+60,this);
								g.drawImage(images.get(4),(int)enemies2.get(i).subsub_x-(int)mine.curr_x+465,(int)enemies2.get(i).subsub_y-30,
								(int)enemies2.get(i).subsub_x-(int)mine.curr_x+525,(int)enemies2.get(i).subsub_y+30,ex,ey,ex+60,ey+60,this);		
							}
						}
						enemies2.get(i).gra++;
					}
				}	
				for(int i=0;i<weapons.size();i++){
					if(weapons.get(i).curr_x>mine.curr_x-500&&weapons.get(i).curr_x<mine.curr_x+500){
						st = weapons.get(i).strength;
						if(st <= 2){
							wx = 60 + 30*st;
							wy = 30;
						}else if(st == 6){
							g.drawImage(images.get(1),weapons.get(i).curr_x-(int)mine.curr_x+465,weapons.get(i).curr_y-30,
							weapons.get(i).curr_x-(int)mine.curr_x+495,weapons.get(i).curr_y,0,0,30,30,this);
							continue;
						}else if(st < 6){
							wx = 30*(st-3);
							wy = 60;
						}else if(st < 9){
							wx = 30*(st-4);
							wy = 60;
						}else{
							wx = 30*(st-9);
							wy = 90;
						}
						g.drawImage(images.get(1),weapons.get(i).curr_x-(int)mine.curr_x+465,weapons.get(i).curr_y-30,
						weapons.get(i).curr_x-(int)mine.curr_x+495,weapons.get(i).curr_y,wx,wy,wx+30,wy+30,this);
					}
				}
				for(int i=0;i<bullets.size();i++){
					if(bullets.get(i).curr_x>mine.curr_x-500&&bullets.get(i).curr_x<mine.curr_x+500){
						gra = bullets.get(i).gra/50; 
						switch(bullets.get(i).kind){
							case 1: ex = 0; ey = 0; break;
							case 2: ex = 30; ey = 0; break;
							case 4: ex = 0 + gra%2*30; ey = 30; break;
							case 5: ex = 60 + gra%2*30; ey = 30; break;
						}
						g.drawImage(images.get(3),(int)bullets.get(i).curr_x-(int)mine.curr_x+465,(int)bullets.get(i).curr_y-30,
						(int)bullets.get(i).curr_x-(int)mine.curr_x+495,(int)bullets.get(i).curr_y,ex,ey,ex+30,ey+30,this);
						bullets.get(i).gra++;
					}
				}	
				for(int i=0;i<warps.size();i++){
					g.drawImage(images.get(1),warps.get(i).from_x*30-(int)mine.curr_x+465,warps.get(i).from_y*30-30,
					warps.get(i).from_x*30-(int)mine.curr_x+495,warps.get(i).from_y*30,120,90,150,120,this);				
					g.drawImage(images.get(1),warps.get(i).to_x*30-(int)mine.curr_x+465,warps.get(i).to_y*30-30,
					warps.get(i).to_x*30-(int)mine.curr_x+495,warps.get(i).to_y*30,120,90,150,120,this);				
				}	
				if(mine.cooltime > 2000 || (int)(mine.cooltime/100) % 3 != 0){
					minedraw(465,(int)mine.curr_y-30,g);
				}
				for(int i=0;i<laser.size();i++){
					if(laser.get(i).cooltime >= 1000){
						g.setColor(Color.yellow);
						if(laser.get(i).value == 'x'){
							g.fillRect(0,(laser.get(i).pos-1)*30,900,laser.get(i).width*30);
						}else{
							g.fillRect(laser.get(i).pos*30-(int)mine.curr_x+465,0,laser.get(i).width*30,660);
						}
					}
				}
				if(false){
					g.setColor(new Color(255,0,0));
					g.drawRect(450-(((int)mine.curr_x-15)%30)+30,((int)mine.curr_panel_y-1)*30,30,30);
				}
			}
			//UIの描画
			//HP、経過時間、取得コイン、総スコアの描画
			
			g.setColor(Color.black);		
			Font fo = new Font("ＭＳ Ｐゴシック",Font.BOLD,30);
			g.setFont(fo);
			g.drawString(String.format("%.2f",gs.time),780,27);
			String str = "武器 :    "+mine.curr_w.name+"  攻撃力 : "+String.valueOf(mine.curr_w.strength);
			g.drawString(str,5,630);
			st = mine.curr_w.strength;
			if(st <= 2){
				wx = 60 + 30*st;
				wy = 30;
			}else if(st < 6){
				wx = 30*(st-3);
				wy = 60;
			}else if(st < 9 && st != 6){
				wx = 30*(st-4);
				wy = 60;
			}else{
				wx = 30*(st-9);
				wy = 90;
			}
			if(st != 0 && st != 6){
				g.drawImage(images.get(1),98,602,128,632,wx,wy,wx+30,wy+30,this);
			}
			g.drawString(String.valueOf(gs.getScore()),7,25);
			g.drawImage(images.get(1),605,602,635,632,90,0,120,30,this);
			String rb = " × "+String.format("%.1f",gs.restBlock);
			g.drawString(rb,635,628);
			g.drawImage(images.get(1),740,602,770,632,30,0,60,30,this);
			rb = " × "+String.valueOf(mine.life);
			g.drawString(rb,770,628);
		}else if(mode == 0){
			Font fo = new Font("ＭＳ Ｐゴシック",Font.BOLD,90);
			g.setFont(fo);
			g.drawString("PUSH ANY KEY",150,350);
		}else if(mode == 6){
			Font fo = new Font("ＭＳ Ｐゴシック",Font.BOLD,100);
			g.setFont(fo);
			g.drawString("GAME OVER",150,350);
		}else if(mode == 5){
			Font fo = new Font("ＭＳ Ｐゴシック",Font.BOLD,100);
			g.setFont(fo);
			g.drawString("NEXT STAGE...",100,350);
		}else if(mode == 4){
			Font fo = new Font("ＭＳ Ｐゴシック",Font.BOLD,100);
			g.setFont(fo);
			g.drawString("GAME CLEAR!!",100,250);
			g.drawString(String.valueOf(nowgs),100,350);
		}
	}

    @Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(mode == 1||mode == 2||mode == 3){
			switch(key){
				case KeyEvent.VK_LEFT : mine.isLeft = true;  break;// left
				case KeyEvent.VK_RIGHT: mine.isRight = true; break;// right
				case KeyEvent.VK_UP   : mine.isUp = true;    break;// jump
				case KeyEvent.VK_DOWN : mine.isDown = true;  break;
				case KeyEvent.VK_Z    : swingWeapon();       break;
				case KeyEvent.VK_C    : searchWeapon();      break;//pick weapon
				case KeyEvent.VK_X    : putBlock();          break;//putBlock
			}
		}else if(mode == 0){
			mine.life = defaultlife;
			nextMode();
		}else if(mode == 6){
			if(key == KeyEvent.VK_ENTER){
				mode = 0;
				mine.curr_w = new Weapon(0,0,0,"折れそうなシャベル");
				defaultlife = 5;
			}else{
				mode = curr_mode-1;
				mine.life = defaultlife;
				nextMode();
			}
		}else if(mode == 5){
			mode = curr_mode;
			defaultlife = mine.life;
			nextMode();
		}else if(mode == 4){
			if(key == KeyEvent.VK_ENTER){
				mode = 0;
				defaultlife = 5;
			}
		}
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) { 
		int key = e.getKeyCode();
		switch(key){
			case KeyEvent.VK_LEFT : mine.isLeft = false;  break;// left
			case KeyEvent.VK_RIGHT: mine.isRight = false; break;// right
			case KeyEvent.VK_UP   : mine.isUp = false;    break;// jump
			case KeyEvent.VK_DOWN : mine.isDown = false;  break;// jump
		}
	}
	@Override
	public void run() {
		while (this.boo) {
			mine.setPanel();
			for(int i=0;i<enemies.size();i++){
				enemies.get(i).autoMove(mine);
				enemies.get(i).setPanel();
				enemies.get(i).cooltime++;
			}
			for(int i=0;i<enemies2.size();i++){
				enemies2.get(i).autoMove(mine,enemies);
				enemies2.get(i).cooltime++;
			}
			for(int i=0;i<bullets.size();i++){
				if(bullets.get(i)!=null){
					bullets.get(i).movebullet();
					if(bullets.get(i).cooltime == 3000){
						bullets.remove(i);
					}
				}
			}
			for(int i=0;i<laser.size();i++){
				laser.get(i).cooltime++;
				if(laser.get(i).cooltime == 3000){
					laser.remove(i);
				}
			}
			mine.cooltime++;
			if(touchJudge()) gameover();
			if(map.map[mine.curr_panel_y][mine.curr_panel_x] == 'C'){
				map.map[mine.curr_panel_y][mine.curr_panel_x] = ' ';
				gs.getCoin++;
			} 
			if(map.map[mine.curr_panel_y][mine.curr_panel_x] == 'H'){
				map.map[mine.curr_panel_y][mine.curr_panel_x] = ' ';
				mine.life++;
			} 
			if(map.map[mine.curr_panel_y+1][mine.curr_panel_x] == 'S'){
				mine.yvelocity = -2.4;
			}
			if(map.map[mine.curr_panel_y][mine.curr_panel_x+1] == 'G'){
				if(flag){
					stageClear();
				}
			}
			if(mine.curr_x > 4560 && mode == 3 && cooltime % 3000 == 0){
				if(enemies.size() == 1){
					int life = enemies.get(0).life;
					if(life < 7){//x:152-196,y:1-19
						int rnd,width;
						if(life > 5){
							width = 1;
						}else{
							width = 2;
						}
						for(int i=0;i<2*width+5;i++){
							rnd = (int)(Math.random()*(45-width))+153;
							laser.add(new Laser('y',rnd,width));
							rnd = (int)(Math.random()*(19-width))+1;
							laser.add(new Laser('x',rnd,width));
						}
					}
				}
			}
			cooltime++;
			mine.gra++;
			repaint();
			sleep(1);
		}
	}

	public void sleep(int time){
		try { Thread.sleep(time); }
		catch (InterruptedException e) {}
	}

	void searchWeapon(){
		for(int i=0;i<weapons.size();i++){
			if(weapons.get(i).currpanelX() == mine.curr_panel_x
				&&weapons.get(i).currpanelY() == mine.curr_panel_y
				&&mine.curr_w.strength < weapons.get(i).strength){
				mine.curr_w = weapons.get(i);
				if(weapons.get(i).strength == 7){
					mine.life += 3;
				}
				weapons.remove(i);
			}
		}
		for(int i=0;i<warps.size();i++){
			warps.get(i).warp(mine);
		}
	}

	void swingWeapon(){
		int weaponv = 1,weapony = 1;
		swing = true;
		mine.gra = 0;
		switch(mine.curr_w.strength){
			case 6: weaponv = 2; break;
			case 7: weaponv = 2; break;
			case 8: weaponv = 2; weapony = 2; break;
			case 9: weaponv = 3; weapony = 2; break;
			case 10: weaponv = 3; weapony = 2; break;
		}
		for(int i=0;i<enemies.size();i++){
			if(enemies.get(i).cooltime > 1000){	
				for(int j=1;j<=weaponv;j++){
					if(enemies.get(i).curr_panel_x == mine.curr_panel_x + j*mine.vector
					&& enemies.get(i).curr_panel_y == mine.curr_panel_y
					&& enemies.get(i).strength <= mine.curr_w.strength){
						enemydamage(i);			
					}
					if(weapony == 2){
						if(enemies.get(i).curr_panel_x == mine.curr_panel_x + j*mine.vector
						&& enemies.get(i).curr_panel_y == mine.curr_panel_y+1
						&& enemies.get(i).strength <= mine.curr_w.strength){
							enemydamage(i);
						}
						if(enemies.get(i).curr_panel_x == mine.curr_panel_x + j*mine.vector
						&& enemies.get(i).curr_panel_y == mine.curr_panel_y-1
						&& enemies.get(i).strength <= mine.curr_w.strength){
							enemydamage(i);
						}
					}
				}
			}
		}
		for(int i=0;i<enemies2.size();i++){
			if(enemies2.get(i).cooltime > 1000){	
				for(int j=1;j<=weaponv;j++){	
					if((int)(enemies2.get(i).curr_x+15)/30 == mine.curr_panel_x + j*mine.vector
					|| (int)(enemies2.get(i).curr_x+45)/30 == mine.curr_panel_x + j*mine.vector){
						if((int)(enemies2.get(i).curr_y+15)/30 == mine.curr_panel_y
						|| (int)(enemies2.get(i).curr_y+45)/30 == mine.curr_panel_y){
							enemydamage2(i);
							break;				
						}
					}
					if(weapony == 2){
						if((int)(enemies2.get(i).curr_x+15)/30 == mine.curr_panel_x + j*mine.vector
						|| (int)(enemies2.get(i).curr_x+45)/30 == mine.curr_panel_x + j*mine.vector){
							if((int)(enemies2.get(i).curr_y+15)/30 == mine.curr_panel_y+1
							|| (int)(enemies2.get(i).curr_y+45)/30 == mine.curr_panel_y+1){
								enemydamage2(i);
								break;				
							}
						}
						if((int)(enemies2.get(i).curr_x+15)/30 == mine.curr_panel_x + j*mine.vector
						|| (int)(enemies2.get(i).curr_x+45)/30 == mine.curr_panel_x + j*mine.vector){
							if((int)(enemies2.get(i).curr_y+15)/30 == mine.curr_panel_y-1
							|| (int)(enemies2.get(i).curr_y+45)/30 == mine.curr_panel_y-1){
								enemydamage2(i);
								break;				
							}
						}
					}
				}
			}
		}
		for(int i=0;i<bullets.size();i++){
			for(int j=1;j<=weaponv;j++){
				if((int)(bullets.get(i).curr_x+15)/30 == mine.curr_panel_x + j*mine.vector
				&& (int)(bullets.get(i).curr_y+15)/30 == mine.curr_panel_y){
					bullets.remove(i);
					break;
				}
				if(weapony == 2){
					if((int)(bullets.get(i).curr_x+15)/30 == mine.curr_panel_x + j*mine.vector
					&& (int)(bullets.get(i).curr_y+15)/30 == mine.curr_panel_y+1){
						bullets.remove(i);
						break;
					}	
					if((int)(bullets.get(i).curr_x+15)/30 == mine.curr_panel_x + j*mine.vector
					&& (int)(bullets.get(i).curr_y+15)/30 == mine.curr_panel_y-1){
						bullets.remove(i);
						break;
					}	
				}						
			}
		}
		if(map.map[mine.curr_panel_y][mine.curr_panel_x+mine.vector]=='B'){
			map.map[mine.curr_panel_y][mine.curr_panel_x+mine.vector]=' ';
			gs.restBlock += 0.2;
		}
		if(map.map[mine.curr_panel_y][mine.curr_panel_x+mine.vector]=='M'){
			map.map[mine.curr_panel_y][mine.curr_panel_x+mine.vector]=' ';
			for(int i=0;i<blocks.size();i++){
				if(blocks.get(i).curr_panel_x == mine.curr_panel_x+mine.vector 
				&& blocks.get(i).curr_panel_y == mine.curr_panel_y){
					life = blocks.get(i).life;
					blocks.remove(i);
				}
			}
			gs.restBlock += 0.2*life;
		}
	}

	void enemydamage(int i){
		if(mine.curr_w.strength <= 5){
			enemies.get(i).life--;
		}else{
			enemies.get(i).life -= 2;
		}
		enemies.get(i).cooltime = 0;
		if(enemies.get(i).life <= 0){
			if(enemies.get(i).strength == 3){
				gs.killScore += 1;
			}else if(enemies.get(i).strength != 7){
				gs.killScore += (int)Math.pow(enemies.get(i).strength,2);
			}
			if((mode == 1 && enemies.get(i).strength == 4)||(mode == 3 && enemies.get(i).strength == 10)) flag = true;
			enemies.remove(i);					
		}else if(enemies.get(i).strength == 10){
			mine.curr_x = 4560;
			mine.curr_y = 570;
			mine.setPanel();
		}
	}

	void enemydamage2(int i){
		if(enemies2.get(i).strength <= mine.curr_w.strength){
			if(mine.curr_w.strength == 10){
				enemies2.get(i).life-= 2;
			}else{
				enemies2.get(i).life--;
			}
			enemies2.get(i).cooltime = 0;
			enemies2.get(i).move();
			if(enemies2.get(i).life <= 0){
				gs.killScore += (int)Math.pow(enemies2.get(i).strength,2);
				if(mode == 2 && enemies2.get(i).strength == 7) flag = true;
				enemies2.remove(i);	
			}
		}
	}

	void putBlock(){
		if(gs.restBlock >= 0.9){
			int pm = (int)((mine.curr_x-mine.curr_panel_x*30)/Math.abs(mine.curr_x-mine.curr_panel_x*30));
			if(mine.curr_x%30==0||pm!=mine.vector){
				if(map.map[mine.curr_panel_y][mine.curr_panel_x+mine.vector] == ' '){
					map.map[mine.curr_panel_y][mine.curr_panel_x+mine.vector] = 'M';
					blocks.add(new MineBlock(mine.curr_panel_x+mine.vector,mine.curr_panel_y));
					gs.restBlock--;
				}
			}else{
				if(map.map[mine.curr_panel_y][mine.curr_panel_x+mine.vector+pm] == ' '){
					map.map[mine.curr_panel_y][mine.curr_panel_x+mine.vector+pm] = 'M';
					blocks.add(new MineBlock(mine.curr_panel_x+mine.vector+pm,mine.curr_panel_y));
					gs.restBlock--;
				}
			}
		}
	}

	void minedraw(int x,int y,Graphics g){
		int sy=0,dy=0,wx=0,wy=0,index=0;
		switch(mine.curr_w.strength){
			case 0: sy = 0; dy = 0; wx = 60; wy = 30; break;
			case 1: sy = 0; dy = 30; wx = 60; wy = 30; break;
			case 2: sy = 0; dy = 60; wx = 60; wy = 30; break;
			case 3: sy = 0; dy = 90; wx = 60; wy = 30; break;
			case 4: sy = 0; dy = 120; wx = 60; wy = 30; break;
			case 5: sy = 0; dy = 150; wx = 60; wy = 30; break;
			case 6: sy = 30; dy = 180; wx = 90; wy = 30; break;
			case 7: sy = 60; dy = 210; wx = 90; wy = 30; break;
			case 8: sy = 90; dy = 240; wx = 90; wy = 90; break;
			case 9: sy = 120; dy = 330; wx = 120; wy = 90; break; 
			case 10: sy = 150; dy = 420; wx = 120; wy = 90; break;
		}
		if(wy == 90){
			index = 1;
		}
		if(swing){
			g.drawImage(images.get(6),x+15-wx/2+(wx/2-15)*mine.vector,y-30*index,x+15+wx/2+(wx/2-15)*mine.vector,y+30+index*30,(int)(wx*2.5)-((2-mine.gra/50%3)*2+1)*wx/2*mine.vector,dy,(int)(wx*3.5)-((2-mine.gra/50%3)*2+1)*wx/2*mine.vector,dy+wy,this);
			if(mine.gra > 149){
				swing = false;
			}
		}else{
			g.drawImage(images.get(0),x,y,x+30,y+30,75-((2-mine.gra/200%3)*2+1)*15*mine.vector,sy,105-((2-mine.gra/200%3)*2+1)*15*mine.vector,sy+30,this);
		}

	}

    // KeyListenerのメソッドkeyTyped
    @Override
	public void keyTyped(KeyEvent e) { }

	//当たり判定部：全ての敵のパネル座標と自分のパネル座標を参照して一致していた場合、true
	synchronized boolean touchJudge(){
		if(gs.time <= 0){
			return true;
		}
		if(map.map[mine.curr_panel_y+1][mine.curr_panel_x] == 'D'){
			return true;
		}
		if(mine.cooltime > 2000){
			if(map.map[mine.curr_panel_y+1][mine.curr_panel_x] == 'F'){
				mine.cooltime = 0;
				mine.life--;
				gs.death++;
				if(mine.life <= 0){
					return true;
				}
			}
			for(int i=0;i<enemies.size();i++){
				if(enemies.get(i) != null&&enemies.get(i).curr_panel_x==mine.curr_panel_x
				&&enemies.get(i).curr_panel_y==mine.curr_panel_y){
					mine.cooltime = 0;
					mine.life--;
					gs.death++;
					if(mine.life <= 0){
						return true;
					}
					break;
				}
			}
			for(int i=0;i<enemies2.size();i++){
				if(enemies2.get(i) != null){
					if((int)(enemies2.get(i).curr_x+15)/30==mine.curr_panel_x
					||(int)(enemies2.get(i).curr_x+45)/30==mine.curr_panel_x){
						if((int)(enemies2.get(i).curr_y+15)/30==mine.curr_panel_y
						||(int)(enemies2.get(i).curr_y+45)/30==mine.curr_panel_y){
							mine.cooltime = 0;
							mine.life--;
							gs.death++;
							if(mine.life <= 0){
								return true;
							}
							break;
						}
					}
				}
			}
			for(int i=0;i<bullets.size();i++){
				for(int j=0;j<blocks.size();j++){
					if((int)(bullets.get(i).curr_x+15)/30==blocks.get(j).curr_panel_x
					&&(int)(bullets.get(i).curr_y+15)/30==blocks.get(j).curr_panel_y){
						bullets.remove(i);
						blocks.get(j).life--;
						if(blocks.get(j).life == 0){
							map.map[blocks.get(j).curr_panel_y][blocks.get(j).curr_panel_x] = ' ';
							blocks.remove(j);
						}
						break;
					}
				}
				if((int)(bullets.get(i).curr_x+15)/30==mine.curr_panel_x
				&&(int)(bullets.get(i).curr_y+15)/30==mine.curr_panel_y){
					mine.cooltime = 0;
					mine.life--;
					gs.death++;
					if(mine.life <= 0){
						return true;
					}
					break;
				}
			}
			for(int i=0;i<laser.size();i++){
				if(laser.get(i).cooltime > 1000){
					if(laser.get(i).value == 'x'){
						for(int j=0;j<laser.get(i).width;j++){
							if(mine.curr_panel_y == laser.get(i).pos + j){
								mine.cooltime = 0;
								mine.life--;
								gs.death++;
								if(mine.life <= 0){
									return true;
								}
								br = true;
								break;
							} 							
						}
						if(br){
							br = false;
							break;
						}
					}else if(laser.get(i).value == 'y'){
						for(int j=0;j<laser.get(i).width;j++){
							if(mine.curr_panel_x == laser.get(i).pos + j){
								mine.cooltime = 0;
								mine.life--;
								gs.death++;
								if(mine.life <= 0){
									return true;													
								}
								br = true;
								break;
							} 
						}
						if(br){
							br = false;
							break;
						}
					}
				}
			}
		}
		return false;
	}

	synchronized void gameover(){
		//画面を切り替えてゲームオーバーの表記、ボタンを押したらタイトルに戻る
		this.boo = false;
		gr.boo = false;
		gs.boo = false;
		this.nowgs = nowgs - 1000;
		this.restBlock = gs.restBlock;
		mine.yvelocity = 0;
		mine.xvelocity = 0;
		mine.cooltime = 2000;
		curr_mode = mode;
		mode = 6;
	}

	synchronized void stageClear(){
		this.boo = false;
		gr.boo = false;
		gs.boo = false;
		this.nowgs = gs.getScore();
		this.restBlock = gs.restBlock;
		curr_mode = mode;
		if(mode == 3){
			mode = 4;
		}else{
			mode = 5;
		}
	}

	synchronized void nextMode(){
		mode++;
		if(mode == 1){
			//マップ1の生成
			mapx = 200;mapy = 20;
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
			for(int i=0;i<mapx;i++){
				map.setObject(i,mapy,'D');
			}
			weapons = new ArrayList<Weapon>();
			enemies = new ArrayList<Chara>();
			bullets = new ArrayList<Bullet>();
			warps = new ArrayList<WarpMachine>();
			enemies2 = new ArrayList<EnemyB>();
			laser = new ArrayList<Laser>();
			blocks = new ArrayList<MineBlock>();

			mine.curr_x = 30;
			mine.curr_y = 570;
			mine.setPanel();
			mine.map = map;
			this.map = map;
			flag = false;

			/*
			enemiesの作成(x,y,strength,life,map,movespeed,movecount)
			strengthの値によって敵の種類が決定する
			1000/movespeedによって移動時間間隔が確定、movecountによって方向転換間隔を確定
					*/
			
			enemies.add(new Chara(210,450,1,1,map,50,200,bullets,0));
			enemies.add(new Chara(810,360,1,1,map,70,200,bullets,0));
			enemies.add(new Chara(780,360,1,1,map,70,200,bullets,0));
			enemies.add(new Chara(1650,450,2,1,map,100,40,bullets,0));
			enemies.add(new Chara(1680,390,2,1,map,100,60,bullets,0));
			enemies.add(new Chara(1620,390,2,1,map,100,50,bullets,0));
			enemies.add(new Chara(1680,390,2,1,map,100,60,bullets,0));
			for(int i=0;i<6;i++){
				enemies.add(new Chara(3230+i*15,480,3,1,map,50,200,bullets,gra));
				enemies.add(new Chara(2585+i*15,210,3,1,map,50,200,bullets,gra));
				enemies.add(new Chara(2590+i*15,390,3,1,map,50,200,bullets,gra));
			}
			for(int i=0;i<12;i++){
				enemies.add(new Chara(5100+i*10,540,3,1,map,50,200,bullets,gra));
				enemies.add(new Chara(5100+i*10,180,3,1,map,50,200,bullets,gra));
				enemies.add(new Chara(5100+i*10,270,3,1,map,50,200,bullets,gra));
				gra++;
				if(gra == 2) gra = 0;
			}

			enemies.add(new Chara(5550,540,4,3,map,1,0,bullets,0));
			/*
			weaponsの作成(x,y,strength,name)

			strengthの値によって武器の種類が決定する
			*/
			weapons.add(new Weapon(450,570,1,"くたびれたピッケル"));
			weapons.add(new Weapon(1050,300,2,"職人のフライパン"));
			weapons.add(new Weapon(900,570,3,"木刀：洞爺湖"));
			weapons.add(new Weapon(4170,90,4,"あの頃の思ひ出"));
			weapons.add(new Weapon(5880,480,5,"キング・オブ・政宗"));
			weapons.add(new Weapon(2190,60,6,"ダーク・ブリンガー"));

			//Gravityスレッドの起動
			gr = new Gravity(map,enemies,mine);
			gr.start();
			//GameScoreスレッドの起動
			gs = new GameScore(mine);
			gs.start();
			//timeベースのスレッドの起動
			this.boo = true;
			th = new Thread(this);
			th.start();
		}else if(mode == 2){
			mapx = 200;mapy = 20;
			map = new Map(mapx,mapy);
			try {
				File map1File = new File("./mapdata/map2.txt");
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
			for(int i=0;i<mapx;i++){
				map.setObject(i,mapy,'D');
			}
			weapons = new ArrayList<Weapon>();
			enemies = new ArrayList<Chara>();
			enemies2 = new ArrayList<EnemyB>();
			bullets = new ArrayList<Bullet>();
			warps = new ArrayList<WarpMachine>();
			laser = new ArrayList<Laser>();
			blocks = new ArrayList<MineBlock>();

			mine.curr_x = 30;
			mine.curr_y = 570;
			mine.setPanel();
			mine.map = map;
			this.map = map;
			flag = false;

			//enemiesの作成(x,y,strength,life,map,movespeed,movecount)
			enemies.add(new Chara(690,210,4,1,map,1,0,bullets,0));
			enemies.add(new Chara(1050,480,4,1,map,1,0,bullets,0));
			enemies.add(new Chara(720,210,5,3,map,1,0,bullets,0));
			enemies.add(new Chara(720,360,5,3,map,1,0,bullets,0));
			enemies.add(new Chara(1080,150,2,1,map,100,70,bullets,0));
			enemies.add(new Chara(1110,150,2,1,map,100,60,bullets,0));
			enemies.add(new Chara(1140,150,2,1,map,100,50,bullets,0));

			enemies.add(new Chara(2670,210,5,3,map,1,0,bullets,0));
			enemies.add(new Chara(2670,370,5,3,map,1,0,bullets,0));
			enemies.add(new Chara(2970,210,5,3,map,1,0,bullets,0));
			enemies.add(new Chara(2820,570,6,1,map,1,0,bullets,0));
			enemies.add(new Chara(3390,370,6,1,map,1,0,bullets,0));
			enemies.add(new Chara(3540,450,2,1,map,100,200,bullets,0));
			enemies.add(new Chara(3510,450,2,1,map,100,200,bullets,0));
			enemies.add(new Chara(3390,450,2,1,map,100,200,bullets,0));
			enemies.add(new Chara(3420,450,2,1,map,100,200,bullets,0));
			enemies.add(new Chara(3480,450,2,1,map,100,200,bullets,0));
			enemies.add(new Chara(3270,210,4,1,map,1,0,bullets,0));
			enemies.add(new Chara(3270,570,4,1,map,1,0,bullets,0));
			enemies.add(new Chara(3270,90,5,3,map,1,0,bullets,0));
			enemies.add(new Chara(2970,450,1,1,map,30,200,bullets,0));
			enemies.add(new Chara(3570,90,6,1,map,30,200,bullets,0));
			enemies.add(new Chara(3570,210,5,3,map,1,0,bullets,0));
			enemies.add(new Chara(3570,370,5,3,map,1,0,bullets,0));

			enemies2.add(new EnemyB(5160,360,7,map,bullets));

			weapons.add(new Weapon(600,210,5,"宝刀マサムーネ"));
			weapons.add(new Weapon(2100,60,6,"シャドウ・ブレイカー"));
			weapons.add(new Weapon(2820,510,7,"天明の七支刀"));
			weapons.add(new Weapon(4290,210,8,"雷切"));

			//縦4横10
			warps.add(new WarpMachine(83,3,95,3));
			warps.add(new WarpMachine(83,7,85,3));
			warps.add(new WarpMachine(83,11,85,7));
			warps.add(new WarpMachine(83,15,95,7));
			warps.add(new WarpMachine(93,7,95,15));
			warps.add(new WarpMachine(103,15,85,15));
			warps.add(new WarpMachine(93,11,105,11));
			warps.add(new WarpMachine(85,11,95,11));
			warps.add(new WarpMachine(103,7,85,19));
			warps.add(new WarpMachine(93,19,137,19));
			warps.add(new WarpMachine(103,19,105,15));
			warps.add(new WarpMachine(123,15,115,19));
			warps.add(new WarpMachine(123,13,137,11));
			warps.add(new WarpMachine(123,19,105,19));
			warps.add(new WarpMachine(113,19,137,15));
			warps.add(new WarpMachine(102,11,105,3));
			warps.add(new WarpMachine(102,9,105,7));
			warps.add(new WarpMachine(113,3,115,7));
			warps.add(new WarpMachine(123,7,137,7));
			warps.add(new WarpMachine(123,3,137,3));
			warps.add(new WarpMachine(111,7,115,11));
			warps.add(new WarpMachine(123,11,115,3));

			//Gravityスレッドの起動
			gr = new Gravity(map,enemies,mine);
			gr.start();
			//GameScoreスレッドの起動
			gs = new GameScore(mine);
			gs.untilScore = nowgs;
			gs.restBlock = restBlock;
			gs.start();
			th = new Thread(this);
			this.boo = true;
			th.start();
		}else if(mode == 3){
			mapx = 200;mapy = 20;
			map = new Map(mapx,mapy);
			try {
				File map1File = new File("./mapdata/map3.txt");
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
			for(int i=0;i<mapx;i++){
				map.setObject(i,mapy,'D');
			}
			weapons = new ArrayList<Weapon>();
			enemies = new ArrayList<Chara>();
			bullets = new ArrayList<Bullet>();
			warps = new ArrayList<WarpMachine>();
			enemies2 = new ArrayList<EnemyB>();
			laser = new ArrayList<Laser>();
			blocks = new ArrayList<MineBlock>();

			mine.curr_x = 30;
			mine.curr_y = 570;
			mine.setPanel();
			mine.map = map;
			this.map = map;
			flag = false;

			enemies2.add(new EnemyB(1170,420,8,map,bullets));

			enemies2.add(new EnemyB(3660,270,9,map,bullets));
			enemies2.add(new EnemyB(3210,540,9,map,bullets));
			enemies2.add(new EnemyB(4050,450,9,map,bullets));
			enemies2.add(new EnemyB(4380,540,9,map,bullets));

			enemies.add(new Chara(5880,270,10,10,map,1,0,bullets,0));

			weapons.add(new Weapon(30,480,8,"迅雷"));
			weapons.add(new Weapon(2850,480,9,"爆炎滅龍剣"));
			weapons.add(new Weapon(4500,120,10,"約束された勝利の〇"));

			//Gravityスレッドの起動
			gr = new Gravity(map,enemies,mine);
			gr.start();
			//GameScoreスレッドの起動
			gs = new GameScore(mine);
			gs.untilScore = nowgs;
			gs.restBlock = restBlock;
			gs.start();
			th = new Thread(this);
			this.boo = true;
			th.start();
		}
	}
}

class Map{
	char[][] map;
	/*
	  スペース：移動可能オブジェクト
	  W：破壊不可能オブジェクト
	  B：破壊可能オブジェクト
	  G：ゴール
	  C：コイン
	  F：炎
	*/

	Map(int x,int y){
		map = new char[y+2][x];
		for(int i=0;i<x;i++){
			map[0][i] = 'W';
		}
	}

	void setObject(int x,int y,char s){
		map[y+1][x] = s;
	}
}	
