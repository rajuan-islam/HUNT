package main;

import java.util.ArrayList;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.tiled.TiledMap;

public class Player {
		// THE CONNECTION
		Map map;
		ArrayList<Bullet> bullets;
		ArrayList<Alien> aliens;
		
		// amination and movement content
		Animation runRight, runLeft;
		Image standRight, standLeft;
		Image jumpLowRight, jumpHighRight;
		Image jumpLowLeft, jumpHighLeft;
		int animationFrameDuration=80;
		
		// DEPENDENT CONSTANTS
		float lowLimit, highLimit;
		
		// VARIABLES
		float x, y;
		float vx, vy;
		public float direction;
		public boolean running, freeFall;

		// FREE CONSTANTS
		int playerWidth=50, playerHeight=100;
		float gravity = 0.5f, acceleration=0.5f, friction=0.25f;
		float jump = -15;
		float speed = 5;
		float fireYDifference = 10;
		
		// Audio Clips
		Sound gunFire;
		Sound gunClick;
		
		// DEATH CONTROL AND ANIMATION
		public boolean alive;
		Animation deathShow;
		int deathShowTimer;
	
	public int update(GameContainer gc, StateBasedGame sbg, int delta, int gamePad)
			throws SlickException {
		if( !alive ) {
			deathShow.update(delta);
			if(deathShowTimer>0) deathShowTimer -= delta;
			return 1;
		}
		
		// updating animation frames
		runRight.update(delta);
		runLeft.update(delta);
		
		Input inp = gc.getInput();
		
		/*
		 * 	Y AXIS
		 */
		vy += gravity;
		// JUMP
		if( inp.isKeyPressed(Input.KEY_UP) || (gamePad>-1 && inp.isControlPressed(7,gamePad)) ) {
			// CHECKING IF SOLID GROUND IS UNDERNEATH
			if( !map.free(x,y+playerHeight+vy) || !map.free(x+playerWidth,y+playerHeight+vy) ) {
				vy = jump;
			}
		}
		// FREE FALL
		if( map.free(x,y+playerHeight+vy) && map.free(x+playerWidth,y+playerHeight+vy) ) { // bottom check
			if( map.free(x,y+vy) && map.free(x+playerWidth,y+vy) ) { // top check
				y += vy;
			} else vy = 0;
		} else vy = 0;
		// FREE FALL AND GROUND DETECTION
		// JUST FOR RENDER
		if(map.free(x,y+playerHeight+2) && map.free(x+playerWidth,y+playerHeight+2)) freeFall=true;
		else freeFall=false;
		
		/*
		 *  X AXIS
		 */
		// GO LEFT
		running = false;
		if( inp.isKeyDown(Input.KEY_LEFT) || (gamePad>-1 && inp.isControllerLeft(gamePad)) ) {
			direction = -1;
			running = true;
			if( map.free( x-speed, y ) && map.free( x-speed, y+(playerHeight/2) ) && map.free( x-speed, y+playerHeight ) ) {
				x -= speed;
				// MAP RELATIVE RENDER COORDINATE UPDATE
				if( (x>=lowLimit && x<=highLimit) || (map.tileX>0 && map.tileX<map.AxisX-17) ) {
					map.x += speed;
				}
			}
		}
		// GO RIGHT
		if( inp.isKeyDown(Input.KEY_RIGHT) || (gamePad>-1 && inp.isControllerRight(gamePad)) ) {
			direction = 1;
			running = true;
			if( map.free( x+playerWidth+speed, y ) && map.free( x+playerWidth+speed, y+(playerHeight/2) ) && map.free( x+playerWidth+speed, y+playerHeight ) ) {
				x += speed;
				// MAP RELATIVE RENDER COORDINATE UPDATE
				if( (x>=lowLimit && x<=highLimit) || (map.tileX>0 && map.tileX<map.AxisX-17) ) {
					map.x -= speed;
				}
			}
		}
		
		// FIRE
		if( inp.isKeyPressed(Input.KEY_Q) || (gamePad>-1 && inp.isControlPressed(6,gamePad) ) ) {
			if( HUNT.playerBullet>0 ) {
				HUNT.playerBullet--;
				gunFire.play(1.0f, 0.5f);
				int extraDown;
				if( freeFall ) extraDown=10;
				else extraDown=0;
				if(direction==1) {
					bullets.add( new Bullet( map, aliens, x+playerWidth, y+fireYDifference+extraDown, direction ) );
				} else {
					bullets.add( new Bullet( map, aliens, x, y+fireYDifference+extraDown, direction ) );
				}
			} else {
				gunClick.play(1.0f, 0.4f);
			}
		}
		
		// LEVEL CLEAR CHECK
		if( goGreen() ) {
			return 500;
		}
		
		// NORMAL
		return 0;
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		/*g.setColor(Color.white);
		g.fillRect(x-map.start, y, playerWidth, playerHeight);*/
		if( !alive ) {
			if( deathShowTimer>0 ) {
				deathShow.draw(x-map.start-25, y);
			}
			return;
		}
		
		/*// OLD STYLE
		if( running && vy==0 ) {
			if( direction==1 ) runRight.draw(x-map.start, y);
			else runLeft.draw(x-map.start, y);
		} else {
			if( direction==1 ) standRight.draw(x-map.start, y);
			else standLeft.draw(x-map.start, y);
		}*/
		
		// NEW STYLE
		if( freeFall ) {
			if( vy>=-5 && vy<=5 ) {
				if( direction==1 ) jumpHighRight.draw(x-map.start-25, y+20);
				else jumpHighLeft.draw(x-map.start-25, y+20);
			} else {
				if( direction==1 ) jumpLowRight.draw(x-map.start-25, y+10);
				else jumpLowLeft.draw(x-map.start-25, y+10);
			}
		} else {
			if( running && vy==0 ) {
				if( direction==1 ) runRight.draw(x-map.start, y);
				else runLeft.draw(x-map.start, y);
			} else {
				if( direction==1 ) standRight.draw(x-map.start, y);
				else standLeft.draw(x-map.start, y);
			}
		}
		
		//g.drawString(vy+"", 700, 400);
	}
	
	boolean playerCanMove() {
		// left side collision
		if( !( map.free(x+vx,y) && map.free(x+vx,y+(playerHeight/2)) && map.free(x+vx,y+playerHeight) ) ) {
			return false;
		}
		// right side collision
		if( !( map.free(x+vx+playerWidth,y) && map.free(x+vx+playerWidth,y+(playerHeight/2)) && map.free(x+vx+playerWidth,y+playerHeight) ) ) {
			return false;
		}
		return true;
	}
	
	boolean goGreen() {
		// left side bottom check
		if( map.inGreen(x, y+playerHeight ) ) return true;
		return false;
	}
	
	public Shape getScope() {
		Rectangle scope = new Rectangle( x, y, playerWidth, playerHeight );
		return scope;
	}
	
	public void init() throws SlickException {
		// DEPENDENT CONSTANTS
		lowLimit = 8*50 + 25;
		highLimit = map.AxisX*50 - lowLimit - 50;
		
		// VARIABLES
		x=150; y=100; if( HUNT.backup ) {
			x = Backup.player.x;
			y = Backup.player.y;
		}
		vx=0; vy=0;
		direction = 1;
		running = false;
		freeFall = false;
		
		// player graphics loading
		Image sheet = new Image( "res/player/player sprite.png" ); 
		// all of left
		runRight = new Animation(false);
		for( int row=0; row<2; row++ ) {
			for( int col=0; col<5; col++ ) {
				Image frame = sheet.getSubImage(playerWidth*col, playerHeight*row, playerWidth, playerHeight);
				runRight.addFrame(frame, animationFrameDuration);
			}
		}
		standRight = sheet.getSubImage(0, 0, playerWidth, playerHeight);
		// all of right
		runLeft = new Animation(false);
		for( int row=2; row<4; row++ ) {
			for( int col=0; col<5; col++ ) {
				Image frame = sheet.getSubImage(playerWidth*col, playerHeight*row, playerWidth, playerHeight);
				runLeft.addFrame(frame, animationFrameDuration);
			}
		}
		standLeft = sheet.getSubImage(0, 2*playerHeight, playerWidth, playerHeight);
		
		// JUMP GRAPHICS
		jumpLowRight = new Image( "res/player/jump low.png" );
		jumpHighRight = new Image( "res/player/jump high.png" );
		jumpLowLeft = new Image( "res/player/jump low left.png" );
		jumpHighLeft = new Image( "res/player/jump high left.png" );
		
		// Audio Sources
		gunFire = new Sound( "res/player/gunfire.wav" ); 
		gunClick = new Sound( "res/player/gun click.wav" );
		
		// DEATH CONTROL AND ANIMATION
		sheet = new Image( "res/player/death show.png" );
		deathShow = new Animation(false);
		for( int row=0; row<1; row++ ) {
			for( int col=0; col<6; col++ ) {
				Image frame = sheet.getSubImage(col*100, row*100, 100, 100);
				deathShow.addFrame(frame, 150);
			}
		}
		alive = true;
		deathShowTimer = 900;
	}
	
	public Player( Map map , ArrayList<Alien> aliens, ArrayList<Bullet> bullets ) {
		this.map = map;
		this.bullets = bullets;
		this.aliens = aliens;
		try {
			init();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
