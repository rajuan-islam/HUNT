package main;

import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class Menu extends BasicGameState {

	// EVERYTHING ON THE BACKGROUND
	Image backGround;
	Image waitMessage;
	Image huntLogo, menuLogo;
	Sound click, over;
	Music backGroundMusic;
	
	// SCREENS
	Image scoreWindow, helpWindow, creditWindow, window;
	boolean windowShow, scoreWindowShow, helpWindowShow, creditWindowShow;
	
	// FOR BUTTONS
	Image play, playNormal, playGlow;
	Image score, scoreNormal, scoreGlow;
	Image help, helpNormal, helpGlow;
	Image credit, creditNormal, creditGlow;
	Image exit, exitNormal, exitGlow;
	Image back, backNormal, backGlow;
	
	// CONSTANTS
	float buttonWidth=300, buttonHeight=80;
	float allX = 476, allBackX = 42;
	float playY=55, scoreY=155, helpY=255, creditY=355, exitY=455, backY=455; 
	
	// VARIABLES
	float playX, scoreX, helpX, creditX, exitX, backX;
	
	// TIMERS AND FLAGS
	boolean go=false;
	int wait=1000;
	boolean shutDown=false;
	long time=0, start=500, gap=300;
	
	// APPEARANCE ANIMATION
	boolean ready;
	boolean menuLogoShow;
	boolean huntLogoShow;
	boolean playShow;
	boolean scoreShow;
	boolean helpShow;
	boolean creditShow;
	boolean exitShow;
	
	// CURSOR
	Animation mouseCursor;
	float x, y;
	
	// PRINTING MATERIAL
	private TrueTypeFont ttf;
	
	// SCORE RECORD SECTION
	void writeData() {
		try {
			PrintWriter wri = new PrintWriter( "DO NOT OPEN.txt", "UTF-8" );
			for( int i=0; i<10; i++ ) {
				wri.println( HUNT.top10score[i] );
			}
			wri.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		// BACKGROUND MUSIC
		if( !backGroundMusic.playing() ) backGroundMusic.loop(1, 0.4f);
		
		Input inp = gc.getInput();
		x = inp.getMouseX();
		y = inp.getMouseY();
		
		// debug
		//if( inp.isMousePressed(Input.MOUSE_LEFT_BUTTON) ) System.out.println( "X "+x+" Y "+y );
		float clickVolume=0.7f;
		
		if(time<20000) time += delta;
		if( !ready ) {
			if( time>=start+gap*6 ) {
				huntLogoShow=true;
				menuLogoShow=true;
				ready=true;
				click.play(1,clickVolume);
			} else if( time>=start+gap*4 ) {
				if(!exitShow) click.play(1,clickVolume);
				exitShow=true;
			} else if( time>=start+gap*3 ) {
				if( !creditShow ) click.play(1,clickVolume);
				creditShow=true;
			} else if( time>=start+gap*2 ) {
				if( !helpShow ) click.play(1,clickVolume);
				helpShow=true;
			} else if( time>=start+gap ) {
				if( !scoreShow ) click.play(1,clickVolume);
				scoreShow=true;
			} else if( time>=start ) {
				if( !playShow ) click.play(1,clickVolume);
				playShow=true;
			}
		}
			
		if( !ready ) return;
		
		if( windowShow ) {
			if( x>=allBackX && x<=allBackX+buttonWidth && y>=backY && y<=backY+buttonHeight ) {
				if( back!=backGlow ) {
					backX -= 25;
					back = backGlow;
					over.play();
				}
				if( inp.isMousePressed(Input.MOUSE_LEFT_BUTTON) && !go ) {
					click.play(1,clickVolume);
					back = backNormal;
					backX = allBackX;
					
					windowShow = false;
				}
			} else {
				back = backNormal;
				backX = allBackX;
			}
			
			return;
		}
		
		// TRANSACTION
		if( go ) wait -= delta;
		if( wait<=0 ) {
			if( shutDown ) {
				writeData();
				gc.exit();
			} else {
				// START GAMEPLAY
				HUNT.backup = false;
				HUNT.gameWin = false;
				HUNT.playerCurrentHealth = HUNT.playerMaxHealth;
				HUNT.playerLife = 2;
				HUNT.currentScore = 0;
				HUNT.playerBullet = 60;
				sbg.enterState(HUNT.COMIC);
			}
		}
		
		// PLAY BUTTON
		if( x>=allX && x<=allX+buttonWidth && y>=playY && y<=playY+buttonHeight ) {
			if( play!=playGlow ) {
				playX -= 25;
				play = playGlow;
				over.play();
			}
			if( inp.isMousePressed(Input.MOUSE_LEFT_BUTTON) && !go ) {
				click.play(1,clickVolume);
				go = true;
				backGroundMusic.fade(wait, 0, true);
			}
		} else {
			play = playNormal;
			playX = allX;
		}
		
		// SCORE BUTTON
		if( x>=allX && x<=allX+buttonWidth && y>=scoreY && y<=scoreY+buttonHeight ) {
			if( score!=scoreGlow ) {
				scoreX -= 25;
				score = scoreGlow;
				over.play();
			}
			if( inp.isMousePressed(Input.MOUSE_LEFT_BUTTON) && !go ) {
				click.play(1,clickVolume);
				score = scoreNormal;
				scoreX = allX;
				
				windowShow = true;
				window = scoreWindow;
				back = backNormal;
				backX = allBackX;
			}
		} else {
			score = scoreNormal;
			scoreX = allX;
		}
		
		// HELP BUTTON
		if( x>=allX && x<=allX+buttonWidth && y>=helpY && y<=helpY+buttonHeight ) {
			if( help!=helpGlow ) {
				helpX -= 25;
				help = helpGlow;
				over.play();
			}
			if( inp.isMousePressed(Input.MOUSE_LEFT_BUTTON) && !go ) {
				click.play(1,clickVolume);
				help = helpNormal;
				helpX = allX;
				
				windowShow = true;
				window = helpWindow;
				back = backNormal;
				backX = allBackX;
			}
		} else {
			help = helpNormal;
			helpX = allX;
		}
		
		// CREDIT BUTTON
		if( x>=allX && x<=allX+buttonWidth && y>=creditY && y<=creditY+buttonHeight ) {
			if( credit!=creditGlow ) {
				creditX -= 25;
				credit = creditGlow;
				over.play();
			}
			if( inp.isMousePressed(Input.MOUSE_LEFT_BUTTON) && !go ) {
				click.play(1,clickVolume);
				credit = creditNormal;
				creditX = allX;
				
				windowShow = true;
				window = creditWindow;
				back = backNormal;
				backX = allBackX;
			}
		} else {
			credit = creditNormal;
			creditX = allX;
		}
		
		// EXIT BUTTON
		if( x>=allX && x<=allX+buttonWidth && y>=exitY && y<=exitY+buttonHeight ) {
			if( exit!=exitGlow ) {
				exitX -= 25;
				exit = exitGlow;
				over.play();
			}
			if( inp.isMousePressed(Input.MOUSE_LEFT_BUTTON) && !go ) {
				click.play(1,clickVolume);
				// action
				go = true;
				backGroundMusic.fade(wait, 0, true);
				shutDown = true;
			}
		} else {
			exit = exitNormal;
			exitX = allX;
		}
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		backGround.draw(0,0);
		
		if(playShow) play.draw(playX,playY);
		if(scoreShow) score.draw(scoreX,scoreY);
		if(helpShow) help.draw(helpX,helpY);
		if(creditShow) credit.draw(creditX,creditY);
		if(exitShow) exit.draw(exitX,exitY);
		
		if(huntLogoShow) huntLogo.draw(10, 400);
		if(menuLogoShow) menuLogo.draw(10,10);
		
		if( go ) waitMessage.draw(200,200);
		
		// SCREENS
		if( windowShow ) {
			window.draw(40, 40);
			back.draw(backX,backY);
			
			if( window == scoreWindow ) {
				ttf.drawString(105, 120, "Top 10 Scores:" );
				//ttf.drawString(105, 150, "Top 10 Scores:" );
				for( int i=0; i<10; i++ ) {
					ttf.drawString(105, 150 + i*30, (i+1) + ". " + HUNT.top10score[i] );
				}
			}
		}
		
		// drawing mouse cursor on top of everything
		mouseCursor.draw(x, y);
		
		// fader on top of everything
		if( time<=3000 ) {
			Color shadeColor = new Color( 0f, 0f, 0f, 1f - ( (float)time/3000f ) );
			g.setColor(shadeColor);
			g.fillRect(0, 0, 800, 600);
			g.setColor(Color.white);
		}
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		// SCREENS FLAGS
		windowShow=false;
		scoreWindowShow=false;
		helpWindowShow=false;
		creditWindowShow=false;
		
		// TIMERS AND FLAGS
		go=false;
		wait=1000;
		shutDown=false;
		time=0;
		start=500;
		gap=300;
		
		// APPEARANCE ANIMATION
		ready=false;
		menuLogoShow=false;
		huntLogoShow=false;
		playShow=false;
		scoreShow=false;
		helpShow=false;
		creditShow=false;
		exitShow=false;
		
		// SETTING THE CURSOR
		// SETTING BLANK CURSOR
		Image cursor = new Image( "res/main menu/blankCursor.png" );
		gc.setMouseCursor(cursor, 0, 0);
		// LOADING IMAGE FOR CUSTOM CARSOR
		Image cursorSheet = new Image( "res/main menu/cursor sheet.png" );
		mouseCursor = new Animation(true);
		for( int i=0; i<3; i++ ) {
			for( int j=0; j<3; j++ ) {
				Image cursorFrame = cursorSheet.getSubImage(j*60, i*60, 60, 60);
				mouseCursor.addFrame(cursorFrame, 65);
			}
		}
		
		// BASICS
		backGround = new Image( "res/main menu/menu.jpg" );
		waitMessage = new Image( "res/main menu/waitMessage.png" );
		huntLogo = new Image( "res/main menu/huntLogo.png" );
		menuLogo = new Image( "res/main menu/menuLogo.png" );
		click = new Sound( "res/main menu/click.wav" );
		over = new Sound( "res/main menu/over.wav" );
		
		// SCREENS
		scoreWindow = new Image( "res/main menu/scoreWindow.png" );
		helpWindow = new Image( "res/main menu/helpWindow.png" );
		creditWindow = new Image( "res/main menu/creditWindow.png" );
		
		// BUTTONS
		playX = scoreX = helpX = creditX = exitX = allX;
		backX = allBackX;
		
		// NORMAL BUTTONS
		playNormal = new Image( "res/main menu/button/playNormal.png" ); play=playNormal;
		scoreNormal = new Image( "res/main menu/button/scoreNormal.png" ); score=scoreNormal;
		helpNormal = new Image( "res/main menu/button/helpNormal.png" ); help=helpNormal;
		creditNormal = new Image( "res/main menu/button/creditNormal.png" ); credit=creditNormal;
		exitNormal = new Image( "res/main menu/button/exitNormal.png" ); exit=exitNormal;
		backNormal = new Image( "res/main menu/button/backNormal.png" ); back=backNormal;
				
		// GLOW BUTTONS
		playGlow = new Image( "res/main menu/button/playGlow.png" );
		scoreGlow = new Image( "res/main menu/button/scoreGlow.png" );
		helpGlow = new Image( "res/main menu/button/helpGlow.png" );
		creditGlow = new Image( "res/main menu/button/creditGlow.png" );
		exitGlow = new Image( "res/main menu/button/exitGlow.png" );
		backGlow = new Image( "res/main menu/button/backGlow.png" );
		
		// PRINTING MATERIAL
		Font font = new Font( "BankGothic Md BT", Font.BOLD, 30 );
		ttf = new TrueTypeFont( font, true );
		
		// STARTING THE BACKGROUND MUSIC
		backGroundMusic = new Music( "res/main menu/backGroundMusic.wav" );
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return HUNT.MENU;
	}
	
	public void enter(GameContainer gc , StateBasedGame sbg) {
		try {
			init(gc, sbg);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public void leave(GameContainer gc , StateBasedGame sbg) {
		
	}

}
