package main;

import java.awt.Font;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.tiled.TiledMap;

public class GameOver extends BasicGameState {
	
	// RESOURCES
	Image gameOver;
	Image newHighScore;
	
	// TRANSACTION FADER MATERIAL
	boolean ready,goodToGo;
	int duration=3000;
	int timer;
	
	// PROMP MATERIAL
	boolean prompt;
	private TrueTypeFont ttf;
	int promptDuration = 500;
	int promptTimer;
	int fadeDirection;
	
	// SOUND
	Music gameOverVoice;
	
	// HIGH SCORE CALCULATION
	int place;

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		// ENTERING FADER
		if( !ready ) {
			timer += delta;
			if( timer>=duration ) {
				// fixing
				timer=duration;
				
				// start everything
				ready = true;
				prompt = true;
			}
		}
		if( !ready ) return;
		
		// LEAVING FADER
		if( goodToGo ) {
			timer -= delta;
			if( timer<=0 ) {
				// fixing
				timer = 0;
				
				// LEAVE THIS STATE
				sbg.enterState(HUNT.MENU);
			}
			return;
		}
		
		// INPUT TO LEAVE
		Input inp = gc.getInput();
		if( inp.isKeyDown(Input.KEY_ENTER) ) {
			goodToGo = true;
			prompt = false;
		}
		
		// PROMPT FADER CHANGE
		if( prompt ) {
			promptTimer += fadeDirection*delta;
			if( promptTimer>=promptDuration ) {
				fadeDirection = -1;
				promptTimer = promptDuration;
			} else if( promptTimer<=0 ) {
				fadeDirection = 1;
				promptTimer = 0;
			}
		}
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		gameOver.draw(0,0);
		
		if( ready && place>0 ) {
			newHighScore.draw(0,0);
			ttf.drawString(12, 315, "Place: " + place );
			ttf.drawString(12, 345, "Score: " + HUNT.currentScore );
		}
		
		// FADER
		if( !ready || goodToGo ) {
			Color fadeColor = new Color( 0f, 0f, 0f, 1f - (float)( (float)timer/(float)duration ) );
			g.setColor(fadeColor);
			g.fillRect(0, 0, 800, 600);
			g.setColor(Color.white);
		}
		
		// PROMPTING
		if(prompt) {
			Color promptColor = new Color( 1f, 1f, 1f, 1f * (float)( (float)promptTimer/(float)promptDuration ) );
			ttf.drawString(163, 438, "Press Enter To Continue", promptColor );
		}
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		if( HUNT.gameWin ) gameOver = new Image( "res/game over/game win.jpg" );
		else gameOver = new Image( "res/game over/game over.jpg" );
		
		newHighScore = new Image( "res/game over/new high score.png" );
		
		// TRANSACTION SCREEN FADER
		timer=0;
		ready=false;
		goodToGo=false;
		
		// PROMPT FADER
		Font font = new Font( "BankGothic Md BT", Font.BOLD, 30 );
		ttf = new TrueTypeFont( font, true );
		promptTimer = 0;
		fadeDirection = 1;
		prompt = false;
		
		// SOUND
		if( HUNT.gameWin ) gameOverVoice = new Music( "res/game over/game win sound.wav" );
		else gameOverVoice = new Music( "res/game over/game over sound.wav" );
		
		// HIGH SCORE CALCULATION
		place = 0;
		for( int i=0; i<10; i++ ) {
			if( HUNT.currentScore > HUNT.top10score[i] ) {
				place = i+1;
				for( int j=9; j>i; j-- ) {
					HUNT.top10score[j] = HUNT.top10score[j-1];
				}
				HUNT.top10score[i] = HUNT.currentScore;
				break;
			}
		}
		
		// RESETTING STATES
		HUNT.gameWin = false;
	}

	@Override
	public int getID() {
		return HUNT.GAMEOVER;
	}
	
	public void enter(GameContainer gc , StateBasedGame sbg) {
		try {
			init(gc, sbg);
			gameOverVoice.play();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public void leave(GameContainer gc , StateBasedGame sbg) {
		
	}

}
