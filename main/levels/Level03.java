package main.levels;

import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import main.Alien;
import main.Backup;
import main.Bullet;
import main.HUNT;
import main.Hud;
import main.Map;
import main.MotionTracker;
import main.Player;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.tiled.TiledMap;

public class Level03 extends BasicGameState {

	Map map;
	Player player;
	ArrayList <Alien> aliens;
	ArrayList <Bullet> bullets;
	Bullet b;
	
	// HUD AND GUI ELEMENTS
	Hud healthHud;
	Sound playerHurtSound, playerDeathSound;
	Sound alienDeathSound;
	//Image hurtFlash; 
	int hurtFlashShow;
	int hurtFlashDuration=500;
	
	// CONTROLLERS
	int gamePad; // index of detected gamepad 
	
	// TRANSACTION FADER MATERIAL
	boolean ready,goodToGo;
	int duration=3000;
	int timer;
	// TRANSACTION CONTROLL RECORDS
	boolean restartLevel;
	boolean nextLevel;
	
	// PAUSE MENU
	Image pauseMenuImage;
	Image qKey, escKey;
	private TrueTypeFont ttf;
	boolean paused;
	
	// SCREENS
	Image levelLoadScreen;
	Image levelClearScreen;
	
	// SCORE TRACK
	int levelScore;
	
	// SOUND
	Music levelStart; boolean levelStartComplete;
	Music levelEnd;
	Sound pauseMenuSound;
	Music backGroundMusic;
	
	// MOTION TRACKER
	MotionTracker motionTracker;
	
	// level 3
	// ALTERNATE ALIENS
		int list[][] = {
				{ 18, 8, 1, 2,	0 },
				{ 26, 8, 1, 2,	0 },
				{ 33, 1, -1, 2, 0 },
				{ 41, 1, 1, 2, 	0 },
				{ 40, 8, 1, 2, 	0 },
				{ 50, 8, -1, 2, 0 },
				
				{ 56, 4, 1, 2, 	30 },
				
				{ 58, 8, -1, 2, 0 },
				{ 72, 8, 1, 2, 	0 },
				{ 78, 8, -1, 2, 0 },
				{ 86, 8, 1, 2,	0 },
				{ 87, 2, 1, 2,	0 },
				{ 99, 8, 1, 2,	0 },
				{ 104, 8, -1, 2,	0 },
				
				{ 121, 8, 1, 2,		2 },
				
				{ 131, 8, -1, 2, 	0 },
				{ 144, 8, 1, 2, 	0 },
				{ 149, 0, -1, 2,	0 },
				{ 157, 8, 1, 2, 	0 },
				
				{ 165, 0, 1, 2, 	1 },
				
				{ 168, 8, -1, 2, 	0 },
				{ 194, 8, 1, 2, 	0 },
				
				{ 202, 0, 1, 2, 	20 },
				
				{ 209, 3, -1, 2, 	0 },
				{ 223, 3, 1, 2, 	0 },
				{ 246, 8, -1, 2, 	0 },
				{ 252, 8, 1, 2, 	0 },
				
				{ 273, 8, 1, 2, 	2 },
				
				{ 285, 8, -1, 2, 	0 },
				{ 301, 8, -1, 2,	0 },
				{ 313, 8, 1, 2,		0 },
				{ 330, 8, -1, 2,	0 },
				{ 336, 8, 1, 2, 	0 },
				{ 366, 8, 1, 2, 	0 },
				
				{ 371, 3, -1, 2, 	20 },
				
				{ 393, 8, 1, 2, 	0 },
				{ 403, 8, -1, 2,	0 },
				{ 414, 8, 1, 2,		0 },
				{ 433, 8, 1, 2,		0 },
				{ 442, 8, -1, 2,	0 },
				{ 455, 8, 1, 2,		0 }
		};
	
	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException { 		
		Input inp = gc.getInput();

		// AT PAUSE MENU
		if( paused ) {
			if( inp.isKeyPressed(Input.KEY_ESCAPE) ) {
				paused = false;
				pauseMenuSound.play();
				if( !levelStartComplete ) levelStart.resume();
				else backGroundMusic.resume();
			} else if( inp.isKeyPressed(Input.KEY_Q) ) {
				levelStart.stop();
				backGroundMusic.stop();
				goodToGo = true;
				paused = false;
			}
			return;
		}
		
		// SOUND
		if( !levelStartComplete && !levelStart.playing() ) {
			levelStartComplete=true;
			backGroundMusic.loop(1f, 0.3f);
		}
		
		// ENTERING FADER
		if( !ready ) {
			timer += delta;
			if( timer>=duration ) {
				// fixing
				timer=duration;
				
				// start everything
				ready = true;
			}
		}
		if( !ready ) return; 
		
		// IF PLAYER IS DEAD THEN JUST TO UPDATE DEATH ANIMATION
		if( !player.alive ) {
			player.update(gc, sbg, delta, delta);
		}
		
		// LEAVING FADER
		if( goodToGo ) {
			timer -= delta;
			if( timer<=0 ) {
				// fixing
				timer = 0;
				
				// LEAVE THIS STATE
				if( restartLevel ) {
					sbg.enterState(getID());
				} else if ( nextLevel ) {
					// go to next level
					sbg.enterState(HUNT.LEVEL04);
				} else {
					sbg.enterState(HUNT.GAMEOVER);
				}
			}
			return;
		}
		
		// PAUSE THE GAME
		if( inp.isKeyPressed(Input.KEY_ESCAPE) ) {
			paused = true;
			if( levelStart.playing() ) levelStart.pause();
			if( backGroundMusic.playing() )backGroundMusic.pause();
			pauseMenuSound.play();
			return;
		}
		
		// PLAYER UPDATE
		int levelStatus = player.update(gc, sbg, delta, gamePad);
		if( levelStatus == 500 ) {
			levelStart.stop();
			backGroundMusic.stop();
			levelEnd.play(1.0f, 0.6f);
			nextLevel = true;
			goodToGo = true;
		}
		
		// MAP CAMERA UPDATE
		map.update(gc, sbg, delta); 
		
		// BULLETS UPDATE AND ALIEN KILL
		for( int i=0; i<bullets.size(); i++ ) {
			int reportIndex = bullets.get(i).update(gc, sbg, delta);
			if( reportIndex>=0 ) {
				// this alien is hurt
				aliens.get(reportIndex).life--;
				// if alien is dead
				if(aliens.get(reportIndex).life==0){
					if( aliens.get(reportIndex).special == 1 ) HUNT.playerLife++;
					else if( aliens.get(reportIndex).special == 2 ) HUNT.playerCurrentHealth=HUNT.playerMaxHealth;
					else if( aliens.get(reportIndex).special >= 10 ) {
						HUNT.playerBullet+=aliens.get(reportIndex).special;
						if( HUNT.playerBullet>100 ) HUNT.playerBullet=100;
					}
					
					HUNT.currentScore += aliens.get(reportIndex).score;
					levelScore += aliens.get(reportIndex).score;
					alienDeathSound.play(1.0f,0.6f);
					aliens.get(reportIndex).alive=false;
				}
				
				bullets.remove(i);
			} else if( reportIndex==-1 ) {
				bullets.remove(i);
			}
		}
		

		// HUD FLASH SHOW
		if( hurtFlashShow > 0 ) hurtFlashShow -= delta;
		if( hurtFlashShow < 0 ) hurtFlashShow = 0;
		
		// ALIENS UPDATE
		for( int i=0; i<aliens.size(); i++ ) {
			int reportIndex = aliens.get(i).update(gc, sbg, delta);
			if( reportIndex >= 0 ) { // player is hurt by the alien
				if(HUNT.playerCurrentHealth>1) {
					hurtFlashShow = hurtFlashDuration;
				} else {
					player.alive=false;
				}
				
				if( HUNT.playerCurrentHealth!=0 ) {
					HUNT.playerCurrentHealth--;
				}
				
				if( HUNT.playerCurrentHealth>0 ) {
					playerHurtSound.play();
				} else {
					playerDeathSound.play();
				}
				
				// LEVEL FAILED
				// RESTARTING LEVEL OR GAME OVER
				if( HUNT.playerCurrentHealth==0 ) {
					if( HUNT.playerLife>0 ) {
						HUNT.playerLife--;
					} 
					
					if(HUNT.playerLife>0) {
						restartLevel = true;
						
						// backing up the current state
						HUNT.backup = true ;
						
						Backup.map = map;
						Backup.aliens = aliens;
						Backup.player = player;
						
						Backup.alienToRemove = i;
					} 
					
					HUNT.currentScore -= levelScore;
					levelStart.stop();
					backGroundMusic.stop();
					goodToGo = true;
				}
			} else if( reportIndex == -100 ) { //
				// time to remove it's corpse
				aliens.remove(i);
				i--;
			}
		}
		
		// HUD UPDATE
		healthHud.update(gc, sbg, delta);
		// motion tracker
		motionTracker.update(gc, sbg, delta);
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		map.render(gc, sbg, g);
		player.render(gc, sbg, g);
		for( int i=0; i<aliens.size(); i++ ) aliens.get(i).render(gc, sbg, g);
		for( int i=0; i<bullets.size(); i++ )  bullets.get(i).render(gc, sbg, g);
		
		// HUD ELEMENTS
		healthHud.render(gc, sbg, g);
		if( hurtFlashShow!=0 ) {
			Color hurtFlash = new Color( 1f, 0f, 0f, 0.3f * (float) ((float)hurtFlashShow/(float)hurtFlashDuration) );
			g.setColor( hurtFlash );
			g.fillRect(0, 0, 800, 600);
		}
		
		// motion tracker
		motionTracker.render(gc, sbg, g);
		
		// PAUSE MENU
		if( paused ) {
			pauseMenuImage.draw( 40, 40 );
			qKey.draw(102,157);
			ttf.drawString(183, 169, "Quit Playing", Color.white);
			escKey.draw(102,239);
			ttf.drawString(183, 250, "Continue Playing", Color.white);
		}
		
		// FADER
		if( !ready || goodToGo ) {
			Color fadeColor = new Color( 0f, 0f, 0f, 1f - (float)( (float)timer/(float)duration ) );
			g.setColor(fadeColor);
			g.fillRect(0, 0, 800, 600);
			g.setColor(Color.white);
		}
		
		// SCREENS ON TOP
		if( !ready ) levelLoadScreen.draw(0,0);
		else if( goodToGo && nextLevel ) levelClearScreen.draw(0,0);
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		bullets = new ArrayList<Bullet>();
		if( !HUNT.backup ) {
			map = new Map( new TiledMap( "res/levels/level03/map 3.tmx" ) , "solid", "green" );
			aliens = new ArrayList <Alien>();
		} else {
			map = Backup.map;
			aliens = Backup.aliens;
		}
		player = new Player( map, aliens, bullets ); 
		
		// STARTING GAMEPLAY CALCULATION
		levelScore = 0;
		HUNT.playerCurrentHealth = HUNT.playerMaxHealth;
		if( HUNT.playerBullet<HUNT.playerInitialBullet ) HUNT.playerBullet=HUNT.playerInitialBullet;
		
		// HUD AND GUI ELEMENTS
		healthHud = new Hud();
		playerHurtSound = new Sound( "res/hud/player hurt.wav" );
		playerDeathSound = new Sound( "res/hud/player death.wav" );
		alienDeathSound = new Sound( "res/hud external/alien scream.wav" );
		hurtFlashShow=0;
		
		// motion tracker
		motionTracker = new MotionTracker(aliens,map,player);
		
		// ADDING ALIENS
		if( !HUNT.alienFree ) {
			if( !HUNT.backup ) {
				for( int i=0; i<list.length; i++ ) {
					int hor = list[i][0];
					int ver = list[i][1];
					int startDirection = list[i][2];
					int life = list[i][3];
					int special = list[i][4];
					int score = 10;
					aliens.add( new Alien( map, player, hor*50, ver*50, startDirection, life, special, score ) );
				}
			} else {
				aliens.remove( Backup.alienToRemove );
				for( int i=0; i<aliens.size(); i++ ) {
					aliens.get(i).player = player;
				}
			}
			
			/*try {
				Scanner alin = new Scanner( new File( "res/levels/level01/alien list for level 01.txt" ) );
				System.out.println( "Alien file loaded successfully" );
				while( alin.hasNextInt() ) {
					int hor = alin.nextInt();
					int ver = alin.nextInt();
					int startDirection = alin.nextInt();
					int life = alin.nextInt();
					int special = alin.nextInt();
					int score = 10;
					aliens.add( new Alien( map, player, hor*50, ver*50, startDirection, life, special, score ) );
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}*/
		}
		
		// demo alien
		//aliens.add( new Alien( map, player, 6*50, 3*50-10, 1 ) );
		
		// GAMEPAD DETECTION
		gamePad = -1;
		int cnt = Controllers.getControllerCount();
		for( int i=0; i<cnt; i++ ) {
			Controller pad = Controllers.getController(i);
			if( pad.getButtonCount()>=10 && pad.getButtonCount()<=20 ) {
				gamePad = i;
				break;
			}
		}
		//gamePad=-1; 
		
		// TRANSACTION SCREEN FADER
		timer=0;
		ready=false;
		goodToGo=false;
		// TRANSACTION CONTROL RECORDS
		restartLevel = false;
		nextLevel = false;
		
		// PAUSE MENU
		pauseMenuImage = new Image( "res/hud/pause menu.png" );
		Font font = new Font( "BankGothic Md BT", Font.BOLD, 30 );
		ttf = new TrueTypeFont( font, true );
		qKey = new Image( "res/keys/q.png" );
		escKey = new Image( "res/keys/esc.png" );
		paused = false;
		
		// SCREENS
		levelLoadScreen = new Image( "res/levels/level03/level 3 screen.png" );
		levelClearScreen = new Image( "res/levels/screens/level clear screen.png" );
		
		// SOUND
		levelStartComplete = false;
		levelStart = new Music( "res/hud external/level start.wav" );
		levelEnd = new Music( "res/hud external/level complete.wav" );
		pauseMenuSound = new Sound( "res/hud external/pause menu sound.wav" );
		backGroundMusic = new Music( "res/levels/level01/level 1 background music.wav" );
		
		// UNIVERSAL
		HUNT.backup = false;
	}
	
	private void reset(GameContainer gc, StateBasedGame sbg) {
		try {
			init(gc,sbg);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getID() {
		return HUNT.LEVEL03;
	}

	public void enter(GameContainer gc , StateBasedGame sbg) {
		try {
			init(gc, sbg);
			levelStart.play(1.0f, 0.5f);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public void leave(GameContainer gc , StateBasedGame sbg) {
		
	}
}
