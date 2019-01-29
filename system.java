import java.util.ArrayList;

class Gravity extends Thread{
	Map map;
	ArrayList<Chara> enemies;
	Chara mine;
	double acceleration = 0.0065;
	boolean boo = true;

	Gravity(Map map,ArrayList<Chara> enemies,Chara mine){
		this.map = map;
		this.enemies = enemies;
		this.mine = mine;
	}
	public void run(){
		while(boo){
			for(int i=0;i<enemies.size();i++){
				int min = enemies.get(i).curr_panel_y * 30-60;
				for(int j=enemies.get(i).curr_panel_y;j<map.map.length;j++){
					min += 30;
					if(map.map[j][enemies.get(i).curr_panel_x] != ' '
                        &&map.map[j][enemies.get(i).curr_panel_x] !='C'
                        &&map.map[j][enemies.get(i).curr_panel_x] !='H') break;
				} 
				if(map.map[enemies.get(i).curr_panel_y+(int)(enemies.get(i).yvelocity/Math.abs(enemies.get(i).yvelocity))][enemies.get(i).curr_panel_x] == ' '
					||map.map[enemies.get(i).curr_panel_y+(int)(enemies.get(i).yvelocity/Math.abs(enemies.get(i).yvelocity))][enemies.get(i).curr_panel_x] == 'C'
					||map.map[enemies.get(i).curr_panel_y+(int)(enemies.get(i).yvelocity/Math.abs(enemies.get(i).yvelocity))][enemies.get(i).curr_panel_x] == 'H'
					||enemies.get(i).curr_y%30!=0){//落下時の運動
					if(enemies.get(i).curr_y + enemies.get(i).yvelocity < min){
						enemies.get(i).curr_y += enemies.get(i).yvelocity;
						enemies.get(i).yvelocity += acceleration;
					}else{
						enemies.get(i).curr_y = min;
						enemies.get(i).yvelocity = 0;
					}
				}else{
					enemies.get(i).yvelocity = 0;
				}
				if(map.map[enemies.get(i).curr_panel_y+1][enemies.get(i).curr_panel_x] == 'D') enemies.remove(i);
			}
			mine.curr_y += mine.yvelocity;
			mine.yvelocity += acceleration;
			if(mine.yvelocity > 2){
				mine.yvelocity = 2;
			}
			if((map.map[mine.curr_panel_y+(int)(mine.yvelocity/Math.abs(mine.yvelocity))][mine.curr_panel_x] != ' '
                &&map.map[mine.curr_panel_y+(int)(mine.yvelocity/Math.abs(mine.yvelocity))][mine.curr_panel_x] != 'C'
                &&map.map[mine.curr_panel_y+(int)(mine.yvelocity/Math.abs(mine.yvelocity))][mine.curr_panel_x] != 'H'
				&&map.map[mine.curr_panel_y+(int)(mine.yvelocity/Math.abs(mine.yvelocity))][mine.curr_panel_x] != 'T'
                &&(mine.curr_y-(int)(mine.yvelocity/Math.abs(mine.yvelocity)))%30>28)
				||(map.map[mine.curr_panel_y+1][mine.curr_panel_x] == 'T'&&mine.yvelocity>0)){
				if(mine.yvelocity<=0 && map.map[mine.curr_panel_y-1][mine.curr_panel_x] == 'B'){
					map.map[mine.curr_panel_y-1][mine.curr_panel_x] = ' ';
				}
				mine.yvelocity = 0;
				mine.curr_y = mine.curr_panel_y*30;
			}
			if(mine.xvelocity<0){
				if(map.map[mine.curr_panel_y][(int)(mine.curr_x+mine.xvelocity)/30] == ' '
                ||map.map[mine.curr_panel_y][(int)(mine.curr_x+mine.xvelocity)/30] == 'C'
                ||map.map[mine.curr_panel_y][(int)(mine.curr_x+mine.xvelocity)/30] == 'H'
				||map.map[mine.curr_panel_y][(int)(mine.curr_x+mine.xvelocity)/30] == 'T'){
					if(map.map[mine.curr_panel_y+(int)(mine.yvelocity/Math.abs(mine.yvelocity))][(int)(mine.curr_x+mine.xvelocity)/30] != ' '
                    &&map.map[mine.curr_panel_y+(int)(mine.yvelocity/Math.abs(mine.yvelocity))][(int)(mine.curr_x+mine.xvelocity)/30] != 'C'
                    &&map.map[mine.curr_panel_y+(int)(mine.yvelocity/Math.abs(mine.yvelocity))][(int)(mine.curr_x+mine.xvelocity)/30] != 'H'
					&&map.map[mine.curr_panel_y+(int)(mine.yvelocity/Math.abs(mine.yvelocity))][(int)(mine.curr_x+mine.xvelocity)/30] != 'T'
					&&mine.yvelocity != 0){
						if(mine.yvelocity<0 && map.map[mine.curr_panel_y-1][mine.curr_panel_x] == 'B'){
							map.map[mine.curr_panel_y-1][mine.curr_panel_x] = ' ';
						}
						mine.curr_y = mine.curr_panel_y*30; 
						mine.yvelocity = 0;
					}
					mine.curr_x += mine.xvelocity;
				}else{
					mine.curr_x = mine.curr_panel_x*30; 
					mine.xvelocity = 0;
				}
			}else if(mine.xvelocity>0){
				if(map.map[mine.curr_panel_y][(int)(mine.curr_x+mine.xvelocity+30)/30] == ' '
				||map.map[mine.curr_panel_y][(int)(mine.curr_x+mine.xvelocity+30)/30] == 'C'
				||map.map[mine.curr_panel_y][(int)(mine.curr_x+mine.xvelocity+30)/30] == 'H'
				||map.map[mine.curr_panel_y][(int)(mine.curr_x+mine.xvelocity+30)/30] == 'T'){
					if(map.map[mine.curr_panel_y+(int)(mine.yvelocity/Math.abs(mine.yvelocity))][(int)(mine.curr_x+mine.xvelocity+30)/30] != ' '
					&&map.map[mine.curr_panel_y+(int)(mine.yvelocity/Math.abs(mine.yvelocity))][(int)(mine.curr_x+mine.xvelocity+30)/30] != 'C'
					&&map.map[mine.curr_panel_y+(int)(mine.yvelocity/Math.abs(mine.yvelocity))][(int)(mine.curr_x+mine.xvelocity+30)/30] != 'H'
					&&map.map[mine.curr_panel_y+(int)(mine.yvelocity/Math.abs(mine.yvelocity))][(int)(mine.curr_x+mine.xvelocity+30)/30] != 'T'
					&&mine.yvelocity != 0){
						if(mine.yvelocity<0 && map.map[mine.curr_panel_y-1][mine.curr_panel_x] == 'B'){
							map.map[mine.curr_panel_y-1][mine.curr_panel_x] = ' ';
						}
						mine.curr_y = mine.curr_panel_y*30; 
						mine.yvelocity = 0;
					}
					mine.curr_x += mine.xvelocity;
				}else{
					mine.curr_x = mine.curr_panel_x*30; 
					mine.xvelocity = 0;
				}
			}

			//摩擦の処理
			if(mine.xvelocity != 0 && map.map[mine.curr_panel_y+1][mine.curr_panel_x] != ' ' && map.map[mine.curr_panel_y+1][mine.curr_panel_x] != 'C' 
			&& map.map[mine.curr_panel_y+1][mine.curr_panel_x] != 'H'){
				mine.xvelocity -= mine.xvelocity/Math.abs(mine.xvelocity) * 0.0008;
				if(Math.abs(mine.xvelocity) < Math.abs(mine.xvelocity - mine.xvelocity/Math.abs(mine.xvelocity) * 0.01)) mine.xvelocity = 0;
			}

			try{
				sleep(1);
			}catch(InterruptedException e){}
		}
	}
}	


class GameScore extends Thread{
	int getCoin; //Coin*100
	int death; //death*-1500
	float time = 0; //要調整
	int killScore; //killScore = 100*strength**2
	float restBlock;
	Chara mine;
	int index = 0;
	boolean boo = true;
	int untilScore = 10000;

	GameScore(Chara mine){
		this.getCoin = 0;
		this.death = 0;
		this.time = 0;
		this.restBlock = 5;
		this.mine = mine;
	}

	public void run(){
		while(boo){
			time += 0.01;
			if(index%25 == 0){
				untilScore -= 10;
			}
			if(index%10 == 0){
				mine.charaMove();
			}
			index++;
			try{
				sleep(10);
			}catch(InterruptedException e){}
		}
	}

	public int getScore(){
		return untilScore+getCoin*100+death*(-1500)+killScore*100;
	}
}
