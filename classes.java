import java.util.ArrayList;

class Chara{
	Map map;
    public int strength;
    public float curr_x;
    public float curr_y;
    public int curr_panel_x;
	public int curr_panel_y;
	public int movespeed;
	public double xvelocity = 0;
	public double yvelocity = 0;
	public int life;
	public int cooltime = 2000;

	public int vector = 1; //向いてる方向
	public int movecount = 0;
	public int vectorchange;

	//mine用のメソッド
	public boolean isLeft = false;
	public boolean isRight = false;
    public boolean isUp = false;
    public boolean isDown = false;
	public Weapon curr_w = new Weapon(0,0,0,"折れそうなシャベル");//new Weapon(0,0,10,"管理者権限");
	public ArrayList<Bullet> bullets;

	//Bullet用のメソッド
	public int angle = 0;
	public int delta;

	public int gra;

    //インスタンスを生成した時点で座標、1キャラの種類(整数値)を保持する
    Chara(int x, int y, int strength,int life,Map map,int speed,int vectorchange,ArrayList<Bullet> bullets,int gra){
		this.curr_x = x;
		this.curr_y = y;
		this.strength = strength;
		this.movespeed = speed;
		this.map = map;
		this.life = life;
		this.vectorchange = vectorchange;
		this.bullets = bullets;
		this.gra = gra;
		setPanel();
    }

	public void charaMove(){
		double velocity = 0.4;
		if(isLeft){
			vector = -1;
			if((curr_x%30!=0||map.map[curr_panel_y][curr_panel_x-1]==' '
                ||map.map[curr_panel_y][curr_panel_x-1]=='C'
                ||map.map[curr_panel_y][curr_panel_x+1]=='H'
				||map.map[curr_panel_y][curr_panel_x-1]=='T') && xvelocity >velocity*-1) {
					if(xvelocity > 0) {xvelocity = 0;}
					else if(xvelocity == 0) { xvelocity = -0.12;}
					else{xvelocity -= velocity*0.22;}
			}else if(xvelocity <= velocity*-1){xvelocity = velocity*-1;}
		}
		if(isRight){
			vector =  1;
			if((curr_x%30!=0||map.map[curr_panel_y][curr_panel_x+1]==' '
                ||map.map[curr_panel_y][curr_panel_x+1]=='C'
                ||map.map[curr_panel_y][curr_panel_x+1]=='H'
				||map.map[curr_panel_y][curr_panel_x+1]=='T') && xvelocity <velocity) {
				if(xvelocity < 0) {xvelocity = 0;}
				else if(xvelocity == 0){xvelocity = 0.12;}
				else{xvelocity += velocity*0.22;}
			}else if(xvelocity >= velocity){xvelocity = velocity;}
		}
        if(isUp){
			if((curr_y%30==0)&&(map.map[curr_panel_y+1][curr_panel_x]!=' '
                &&map.map[curr_panel_y+1][curr_panel_x]!='C'
                &&map.map[curr_panel_y+1][curr_panel_x]!='H')) yvelocity = -1.1;//jump
        }
        if(isDown){
            if(map.map[curr_panel_y+1][curr_panel_x] == 'T') this.curr_y += 16;        
        }
		setPanel();
	}

	void setPanel(){
		//curr_x,curr_yの値からcurr_panel_x,curr_panel_yを計算する。
		this.curr_panel_x = ((int)curr_x+15) / 30 ;
		this.curr_panel_y = ((int)curr_y+15) / 30 ;
	}

	void autoMove(Chara mine){
		if(cooltime % (int)(1000/movespeed) == 0 && vectorchange != 0){
			if(map.map[curr_panel_y][curr_panel_x+vector] == ' '
                ||map.map[curr_panel_y][curr_panel_x+vector] =='C'
                ||map.map[curr_panel_y][curr_panel_x+vector] =='H'||(curr_x-5)/30==curr_x/30){
				curr_x += vector * 3;
				movecount++;
				if(movecount == vectorchange){
					vector *= -1;
					movecount = 0;
				}
			}else if(map.map[curr_panel_y][curr_panel_x+vector] != ' '
                &&map.map[curr_panel_y][curr_panel_x+vector] !='C'
                &&map.map[curr_panel_y][curr_panel_x+vector] !='H'){
				vector *= -1;
				movecount = 0;
			}
		}
		mkBullets(mine);			
	}
	
	void mkBullets(Chara mine){
		int kind = 0,pitch = 0;
		float tlsp = 0;
		Bullet bul;
		switch(strength){
			case 4  : tlsp = (float)0.1; kind = 1; pitch = 300; delta = 80; break;
			case 6  : tlsp = (float)0.1; kind = 2; pitch = 500; delta = 45; break;
			case 7  : tlsp = (float)0.1; kind = 1; pitch = 500; delta = 45; break;
			case 10 : tlsp = (float)0.2; kind = 5; pitch = 100; delta = 10; break;
		}
		//クールタイムとmineとの距離に応じてbulletsにオブジェクトを追加する
		if(kind != 0 && pitch != 0){
			if(cooltime % pitch == 0 &&(Math.abs(mine.curr_x-curr_x)<350 
			|| (mine.curr_x > 4560 && strength == 10))){
				switch(strength){
					case 4:
					this.angle += delta;
					bul = new Bullet(tlsp,angle,kind);
					bul.curr_x = this.curr_x;
					bul.curr_y = this.curr_y;
					bullets.add(bul);
					break;
					case 6:
					this.angle += delta;
					bul = new Bullet(tlsp,angle,kind);
					bul.curr_x = this.curr_x;
					bul.curr_y = this.curr_y;
					bullets.add(bul);
					bul = new Bullet(tlsp,angle+45,kind);
					bul.curr_x = this.curr_x;
					bul.curr_y = this.curr_y;
					bullets.add(bul);
					bul = new Bullet(tlsp,angle+90,kind);
					bul.curr_x = this.curr_x;
					bul.curr_y = this.curr_y;
					bullets.add(bul);
					break;
					case 7:
					this.angle += delta;
					bul = new Bullet(tlsp,angle,kind);
					bul.curr_x = this.curr_x;
					bul.curr_y = this.curr_y;
					bullets.add(bul);
					bul = new Bullet(tlsp,angle+90,kind);
					bul.curr_x = this.curr_x;
					bul.curr_y = this.curr_y;
					bullets.add(bul);
					bul = new Bullet(tlsp,angle+180,kind);
					bul.curr_x = this.curr_x;
					bul.curr_y = this.curr_y;
					bullets.add(bul);
					bul = new Bullet(tlsp,angle+270,kind);
					bul.curr_x = this.curr_x;
					bul.curr_y = this.curr_y;
					bullets.add(bul);
					break;
					case 10:
					this.angle += delta;
					bul = new Bullet(tlsp,angle+5,4);
					bul.curr_x = this.curr_x;
					bul.curr_y = this.curr_y;
					bullets.add(bul);
					bul = new Bullet(tlsp,angle+185,4);
					bul.curr_x = this.curr_x;
					bul.curr_y = this.curr_y;
					bullets.add(bul);
					if(cooltime % (pitch * 5) == 0){
						tlsp *= 2;
						bul = new Bullet(tlsp,0,5);
						bul.curr_x = this.curr_x;
						bul.curr_y = this.curr_y;
						bullets.add(bul);					
						bul = new Bullet(tlsp,90,5);
						bul.curr_x = this.curr_x;
						bul.curr_y = this.curr_y;
						bullets.add(bul);					
						bul = new Bullet(tlsp,180,5);
						bul.curr_x = this.curr_x;
						bul.curr_y = this.curr_y;
						bullets.add(bul);
						bul = new Bullet(tlsp,270,5);
						bul.curr_x = this.curr_x;
						bul.curr_y = this.curr_y;
						bullets.add(bul);	
					}
					if(cooltime % (pitch * 5) == 100){
						bul = new Bullet(tlsp,45,5);
						bul.curr_x = this.curr_x;
						bul.curr_y = this.curr_y;
						bullets.add(bul);					
						bul = new Bullet(tlsp,135,5);
						bul.curr_x = this.curr_x;
						bul.curr_y = this.curr_y;
						bullets.add(bul);					
						bul = new Bullet(tlsp,225,5);
						bul.curr_x = this.curr_x;
						bul.curr_y = this.curr_y;
						bullets.add(bul);
						bul = new Bullet(tlsp,315,5);
						bul.curr_x = this.curr_x;
						bul.curr_y = this.curr_y;
						bullets.add(bul);	
					}
				}
			}
		}			
	}
}

class EnemyB{//7の堕天使、9のケルベロス、8の魔女はこれに該当. 大きさが4ブロック分の敵のクラス
	int strength;
	float curr_x;
	float curr_y;
	Map map;
	ArrayList<Bullet> bullets;
	int life;
	int gra;
	int vector = 1;
	int cooltime;
	int prev_rnd = 0;

	int angle = 0;
	float deltax;
	boolean movable;
	int movecount;

	float sub_x,sub_y,subsub_x,subsub_y;

	public EnemyB(int x, int y, int strength,Map map,ArrayList<Bullet> bullets){
		this.curr_x = x;
		this.curr_y = y;
		this.strength = strength;
		this.map = map;
		this.bullets = bullets;
		this.cooltime = 2000;
		switch(strength){
			case 7: this.life = 5; deltax = 3;break;
			case 8: this.life = 3; break;
			case 9: this.life = 3; break;
		}
	}
	void autoMove(Chara mine,ArrayList<Chara> enemies){
		if((this.curr_x - mine.curr_x)*vector > 0){
			vector *= -1;
		}
		if(movable){
			switch(strength){
				case 7:
				movecount--;
				curr_x += deltax;
				if(movecount==0){
					movable = false;
				}
				break;
				case 8:
				if(cooltime %200 == 0){
					int rnd = (int)(Math.random()*8);
					while(rnd == prev_rnd){
						rnd = (int)(Math.random()*8);
					}
					switch(rnd){
						case 0: curr_x = 1170; curr_y = 420; break;
						case 1: curr_x = 1380; curr_y = 270; break;
						case 2: curr_x = 720; curr_y = 300; break;
						case 3: curr_x = 1350; curr_y = 360; break;
						case 4: curr_x = 1350; curr_y = 540; break;
						case 5: curr_x = 1050; curr_y = 540; break;
						case 6: curr_x = 690; curr_y = 390; break;
						case 7: curr_x = 690; curr_y = 540; break;
					}
					prev_rnd = rnd;
					movecount--;
					if(life == 1 && movecount == 2){
						sub_x = curr_x;
						sub_y = curr_y;
					}else if(life == 1 && movecount == 1){
						subsub_x = curr_x;
						subsub_y = curr_y;
					}
					if(movecount==0){
						movable = false;
					}
				}
				break;
			}
		}
		mkBullets(mine,enemies);
	}
	void mkBullets(Chara mine,ArrayList<Chara> enemies){
		int kind = 0,pitch = 0;
		Bullet bul;
		float tlsp = 0,x = curr_x - mine.curr_x,y = curr_y - mine.curr_y;
		switch(strength){
			case 7  : tlsp = (float)0.2; kind = 1; pitch = 300; break;
			case 8  : tlsp = (float)0.65; kind = 4; pitch = 200; break;
			case 9  : tlsp = (float)0.3; kind = 2; pitch = 700; this.angle = 90+(270-(int)(Math.asin(y/Math.sqrt(x*x+y*y))*180/Math.PI))*vector; break;
		}
		//クールタイムとmineとの距離に応じてbulletsにオブジェクトを追加する
		if(kind != 0 && pitch != 0){
			if(cooltime % pitch == 0 &&
			(Math.abs(mine.curr_x-curr_x)<350||(mine.curr_x > 600 && mine.curr_x < 1690 && strength == 8))){
				switch(strength){
					case 7:
					bul = new Bullet(tlsp,angle,kind);
					bul.curr_x = this.curr_x;
					bul.curr_y = this.curr_y;
					bullets.add(bul);
					bul = new Bullet(tlsp,angle+15,kind);
					bul.curr_x = this.curr_x;
					bul.curr_y = this.curr_y;
					bullets.add(bul);
					if(angle % 900 == 0){
						enemies.add(new Chara((int)this.curr_x-30,(int)this.curr_y,7,1,map,1,0,bullets,0));
						enemies.add(new Chara((int)this.curr_x+60,(int)this.curr_y,7,1,map,1,0,bullets,0));
					}
					angle += 30;
					break;
					case 8:
					bul = new Bullet(tlsp/5,angle+45,kind);
					bul.curr_x = this.curr_x+15*(vector+1);
					bul.curr_y = this.curr_y;
					bullets.add(bul);
					if(life == 1){
						bul = new Bullet(tlsp/5,angle+45,kind);
						bul.curr_x = this.sub_x+15*(vector+1);
						bul.curr_y = this.sub_y;
						bullets.add(bul);
						bul = new Bullet(tlsp/5,angle+45,kind);
						bul.curr_x = this.subsub_x+15*(vector+1);
						bul.curr_y = this.subsub_y;
						bullets.add(bul);
					}
					if(cooltime % (pitch * 5) == 0){
						bul = new Bullet(tlsp,angle,kind);
						bul.curr_x = this.curr_x+15*(vector+1);
						bul.curr_y = this.curr_y;
						bullets.add(bul);
						bul = new Bullet(tlsp,angle+90,kind);
						bul.curr_x = this.curr_x+15*(vector+1);
						bul.curr_y = this.curr_y;
						bullets.add(bul);
						bul = new Bullet(tlsp,angle+180,kind);
						bul.curr_x = this.curr_x+15*(vector+1);
						bul.curr_y = this.curr_y;
						bullets.add(bul);
						bul = new Bullet(tlsp,angle+270,kind);
						bul.curr_x = this.curr_x+15*(vector+1);
						bul.curr_y = this.curr_y;
						bullets.add(bul);
						if(life == 1){
							bul = new Bullet(tlsp,angle,kind);
							bul.curr_x = this.sub_x+15*(vector+1);
							bul.curr_y = this.sub_y;
							bullets.add(bul);
							bul = new Bullet(tlsp,angle+90,kind);
							bul.curr_x = this.sub_x+15*(vector+1);
							bul.curr_y = this.sub_y;
							bullets.add(bul);
							bul = new Bullet(tlsp,angle+180,kind);
							bul.curr_x = this.sub_x+15*(vector+1);
							bul.curr_y = this.sub_y;
							bullets.add(bul);
							bul = new Bullet(tlsp,angle+270,kind);
							bul.curr_x = this.sub_x+15*(vector+1);
							bul.curr_y = this.sub_y;
							bullets.add(bul);
							bul = new Bullet(tlsp,angle,kind);
							bul.curr_x = this.subsub_x+15*(vector+1);
							bul.curr_y = this.subsub_y;
							bullets.add(bul);
							bul = new Bullet(tlsp,angle+90,kind);
							bul.curr_x = this.subsub_x+15*(vector+1);
							bul.curr_y = this.subsub_y;
							bullets.add(bul);
							bul = new Bullet(tlsp,angle+180,kind);
							bul.curr_x = this.subsub_x+15*(vector+1);
							bul.curr_y = this.subsub_y;
							bullets.add(bul);
							bul = new Bullet(tlsp,angle+270,kind);
							bul.curr_x = this.subsub_x+15*(vector+1);
							bul.curr_y = this.subsub_y;
							bullets.add(bul);		
						}
					}
					angle += 15;
					break;
					case 9:
					bul = new Bullet(tlsp,angle-10,kind);
					bul.curr_x = this.curr_x+15*(vector+1);
					bul.curr_y = this.curr_y;
					bullets.add(bul);
					bul = new Bullet(tlsp,angle,kind);
					bul.curr_x = this.curr_x+15*(vector+1);
					bul.curr_y = this.curr_y;
					bullets.add(bul);				
					bul = new Bullet(tlsp,angle+10,kind);
					bul.curr_x = this.curr_x+15*(vector+1);
					bul.curr_y = this.curr_y;
					bullets.add(bul);
					break;
				}
			}
		}			
	}

	void move(){
		switch(strength){
			case 7: 
			movable = true;
			deltax *= -1.2;
			movecount = 50;
			break;
			case 8:
			movable = true;
			movecount = 7;
		}
	}
}

class Weapon{
	int strength;
	int curr_x;
	int curr_y;
	String name;
	Weapon(int x,int y,int strength,String name){
		this.strength = strength;
		this.curr_x = x;
		this.curr_y = y;
		this.name = name;
	}
	int currpanelX(){
		return curr_x/30;
	}
	int currpanelY(){
		return curr_y/30;
	}
}

class Bullet{
	float curr_x;
	float curr_y;
	float toolupspeed;
	float angle;
	int kind;
	int cooltime = 0;

	int gra = 0;

	Bullet(float toolupspeed,int angle,int kind){
		this.toolupspeed = toolupspeed;
		this.angle = angle;
		this.kind = kind;
	}

	void movebullet(){
		curr_x += toolupspeed*Math.cos(angle/180*Math.PI);
		curr_y += toolupspeed*Math.sin(angle/180*Math.PI);
		cooltime++;
	}
}

class WarpMachine{
	int from_x;
	int from_y;
	int to_x;
	int to_y;
	public WarpMachine(int x1,int y1,int x2,int y2){
		from_x = x1;
		from_y = y1;
		to_x = x2;
		to_y = y2;
	}
	void warp(Chara mine){
		if(mine.curr_panel_x == from_x && mine.curr_panel_y == from_y){
			synchronized(this){
				mine.xvelocity = 0;
				mine.yvelocity = 0;
				mine.curr_x = to_x*30;
				mine.curr_y = to_y*30;
				mine.setPanel();
			}
		}else if(mine.curr_panel_x == to_x && mine.curr_panel_y == to_y){
			synchronized(this){
				mine.xvelocity = 0;
				mine.yvelocity = 0;
				mine.curr_x = from_x*30;
				mine.curr_y = from_y*30;
				mine.setPanel();
			}
		}
	}
}

class MineBlock{
	int curr_panel_x;
	int curr_panel_y;
	int life;
	MineBlock(int x,int y){
		curr_panel_x = x;
		curr_panel_y = y;
		life = 3;
	}
}

class Laser{
	char value;
	int pos;
	int width;
	int cooltime;
	Laser(char value,int pos,int width){
		this.value = value; //x or y
		this.pos = pos;
		this.width = width;
		cooltime = 0;
	}
}