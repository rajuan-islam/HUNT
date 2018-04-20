package main;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.tiled.TiledMap;

public class Alien {
	public Map map;
	public Player player;
	
	// VARIABLES
	float x, y;
	float direction;
	float timeBeforeAttack;
	
	// CONSTANTS
	float alienWidth=50, alienHeight=100;
	int animationFrameDuration=100;
	float speed=4;
	
	// PROPERTIES
	public int life;
	public int special;
	public int score;
	
	// ANIMATIONS
	Animation runRight, runLeft;
	
	// DEATH CONTROL AND ANIMATION
	public boolean alive;
	Animation deathShow;
	int deathShowTimer;
	
	public int update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException { 
		if( !alive ) {
			deathShow.update(delta);
			deathShowTimer -= delta;
			if( deathShowTimer<=0 ) {
				return -100;
			}
			return -500;
		}
		
		// animation frame update
		runRight.update(delta);
		runLeft.update(delta);
		
		// COLLISION DETECTION AND MOVEMENT CONTROLL		
		if( direction>0 ) {
			if( map.free(x+speed*direction+alienWidth,y+alienHeight) && !map.free(x+speed*direction+alienWidth,y+alienHeight+1) ) { // right bottom check
				x += speed*direction;
			} else {
				direction = -direction;
			}
		} else if( direction<0 ) {
			if( map.free(x+speed*direction,y+alienHeight) && !map.free(x+speed*direction,y+alienHeight+1) ) { // left bottom check
				x += speed*direction;
			} else {
				direction = -direction;
			}
		}
		
		// ATTACKING THE PLAYER
		if( timeBeforeAttack>0 ) timeBeforeAttack -= delta;
		if( timeBeforeAttack<0 ) timeBeforeAttack = 0;
		Rectangle scope = new Rectangle( x, y, alienWidth, alienHeight );
		if( player.alive && timeBeforeAttack==0 && scope.intersects(player.getScope()) ) {
			timeBeforeAttack = 500;
			return 0;
		}
		
		// EVERYTHING IS FINE
		return -500;
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException { 
		// DRAWING THE ALIEN ON THE SCREEN
		if( (map.start<=x && x<=map.start+800) || (map.start<=x+alienWidth && x+alienWidth<=map.start+800) ) {
			//g.setColor(Color.red);
			//g.fillRect(x-map.start, y, alienWidth, alienHeight );
			if( !alive ) {
				deathShow.draw(x-map.start,y+4);
				return;
			}
			
			if( direction==1 ) {
				runRight.draw(x-map.start-50,y);
			} else {
				runLeft.draw(x-map.start,y);
			}
		}
	}
	
	public boolean contains( float px, float py ) {
		if( px>=x && px<=x+alienWidth && py>=y && py<=y+alienHeight ) return true;
		return false;
	}
	
	public void init() throws SlickException {
		// ANIMATION LOADING
		Image sheet = new Image( "res/alien/alienSprite.png" );
		runRight = new Animation( false );
		for( int row=0; row<2; row++ ) {
			for( int col=0; col<5; col++ ) {
				if( row==1 && col>2 ) break;
				Image frame = sheet.getSubImage(col*100, row*100, 100, 100);
				runRight.addFrame(frame, animationFrameDuration);
			}
		}
		runLeft = new Animation( false );
		for( int row=2; row<4; row++ ) {
			for( int col=0; col<5; col++ ) {
				if( row==3 && col>2 ) break;
				Image frame = sheet.getSubImage(col*100, row*100, 100, 100);
				runLeft.addFrame(frame, animationFrameDuration);
			}
		}
		
		// DEATH CONTROL AND ANIMATION
		sheet = new Image( "res/alien/explosion.png" );
		deathShow = new Animation(false);
		for( int row=0; row<3; row++ ) {
			for( int col=0; col<5; col++ ) {
				Image frame = sheet.getSubImage(col*96, row*96, 96, 96);
				deathShow.addFrame(frame, 100);
			}
		}
		alive = true;
		deathShowTimer = 1500;
		
		timeBeforeAttack = 500;
	}
	
	public Alien( Map map, Player player, float x, float y, float direction, int life, int special, int score ) {
		// CONNECTING
		this.map = map;
		this.player = player;
		
		// properties
		this.life = life;
		this.special = special;
		this.score = score;
		
		// SETTING UP THE ALIENS STANDING SPACE
		this.x = x;
		while( map.free(x, y+alienHeight) ) y++; y--;
		this.y = y;
		
		this.direction=direction;
		
		try {
			init();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
}
