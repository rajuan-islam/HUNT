package main;

import java.awt.Font;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.tiled.TiledMap;

public class Hud {
	
	// RESOURCE
	Image hud;
	Image healthBar[];
	Animation life;
	private TrueTypeFont ttf;
	Image scoreLabel;
	Image bulletRound, bullet2;
	
	// SCORE POSITION
	final int startPosition = 750;
	final int stepSize = 33;
	int xPosition;
	
	// BULLET
	int rounds;
	
	// DEBUG
	int x, y;
	
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		xPosition = startPosition - stepSize*step( HUNT.currentScore );
		
		// BULLET
		rounds = HUNT.playerBullet/3;
		if( HUNT.playerBullet%3 > 0 ) rounds++;
	}
	
	int step( int num ) {
		int cnt=0;
		while( num>9 ) {
			num /= 10;
			cnt++;
		}
		return cnt;
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		// HUD STYLE 1
		hud.draw(15,15);
		healthBar[ HUNT.playerMaxHealth - HUNT.playerCurrentHealth ].draw( 100+15, 0+15 );
		
		// LIFE
		for( int i=0; i<HUNT.playerLife; i++ ) {
			life.draw( 15 + 23 + i*50, 121 );
		}
		
		// BULLET 2
		for( int i=0; i<rounds; i++ ) {
			bullet2.draw( 15 + 100 + 5 + 5*i, 75 );
		}
		
		//-------------------------------------------------------------------------------
		
		/*// HUD STYLE 2
		hud.draw(15,15+45);
		healthBar[ HUNT.playerMaxHealth - HUNT.playerCurrentHealth ].draw( 100+15, 0+15+45 );
		for( int i=0; i<HUNT.playerLife; i++ ) {
			life.draw( 100+15 + i*50 + 5, 55+15+45 );
		}
		
		// BULLET
		for( int i=0; i<rounds; i++ ) {
			bullet2.draw( 15 + 5*i, 15 );
		}*/
		
		// SCORING
		scoreLabel.draw(645,10);
		ttf.drawString(xPosition+5, 58+5, HUNT.currentScore+"", Color.black);
		ttf.drawString(xPosition, 58, HUNT.currentScore+"", Color.white);
	}
	
	public void init() throws SlickException {
		hud = new Image( "res/hud/hud.png" );
		bulletRound = new Image( "res/hud/bullet round.png" );
		bullet2 = new Image( "res/hud/bullet 2.png" );
		
		// BULLET
		rounds = HUNT.playerBullet/3;
		if( HUNT.playerBullet%3 > 0 ) rounds++;
		
		// making the healthbar
		healthBar = new Image[10];
		Image sheet = new Image( "res/hud/health bar sheet.png" );
		int ind=0;
		for( int row=0; row<3; row++ ) {
			for( int col=0; col<2; col++ ) {
				healthBar[ind++] = sheet.getSubImage(col*200, row*50, 200, 50);
			}
		}
		
		// life ball animation
		life = new Animation(true);
		sheet = new Image( "res/hud/ball sheet.png" );
		for( int row=0; row<4; row++ ){
			for( int col=0; col<8; col++ ){
				Image frame = sheet.getSubImage(col*40, row*40, 40, 40);
				life.addFrame(frame, 65);
			}
		}
		
		// FONT
		Font font = new Font( "BankGothic Md BT", Font.BOLD, 40 );
		ttf = new TrueTypeFont( font, true );
		
		// SCORE LABEL
		scoreLabel = new Image( "res/hud/score small.png" );
		xPosition = startPosition - stepSize*step( HUNT.currentScore );
	}
	
	public Hud() {
		try {
			init();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
