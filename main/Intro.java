package main;

import java.awt.Font;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class Intro extends BasicGameState {

	// TIMERS
	private long time;
	private long blink;
	private long delay;
	private long loadTime;
	
	// FLAGS
	private boolean done;
	private boolean ready;
	private boolean loading;
	private boolean lock;
	
	// IMPORT
	private Music music, start, hunt; 
	// IMAGES FOR ALL THE SCENES
	Image image;
	Image startImage;
	Image aust;
	Image secondScene;
	Image slickImage;
	Image darkImage;
	Image huntImage;
	
	// PROMP MATERIAL
	boolean prompt;
	private TrueTypeFont ttf;
	int promptDuration = 500;
	int promptTimer;
	int fadeDirection;
	
	// final fader
	float alpha;
	
	// flying shader
	Image flyShade;
	int flyX;
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		// TIMERS
		time = 0;
		blink = 0;
		delay = 500;
		loadTime = 3000;
		
		// FLAGS
		done = false;
		ready = false;
		prompt = false;
		loading = false;
		lock = false;
		
		// LOADING MUSIC
		start = new Music( "res/intro/start.wav" );
		hunt = new Music( "res/intro/hunting.wav" );
		music = start;
		
		// IMAGES FOR ALL THE SCENES
		darkImage = new Image( "res/intro/dark.jpg" );
		aust = new Image( "res/intro/aust 0.jpg" );
		startImage = new Image( "res/intro/aust.jpg" );	
		secondScene = new Image( "res/intro/project.jpg" );
		slickImage = new Image( "res/intro/slick.jpg" );
		huntImage = new Image( "res/intro/hunt 3.jpg" );
		image = aust;
		
		// flying shader
		flyShade = new Image( "res/intro/fly shade.png" );
		flyX = -200;
		
		// PROMPT FADER
		Font font = new Font( "BankGothic Md BT", Font.BOLD, 30 );
		ttf = new TrueTypeFont( font, true );
		promptTimer = 0;
		fadeDirection = 1;
		prompt = false;
		
		// final fader
		alpha = 0;
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		image.draw(0,0);
		if( image==secondScene ){
			flyShade.draw(flyX,0);
		}
		if( alpha>0.0 ) {
			Color shadeColor = new Color( 0f, 0f, 0f, alpha );
			g.setColor(shadeColor);
			g.fillRect(0, 0, 800, 600);
			g.setColor(Color.white);
		}

		if(prompt) {
			Color promptColor = new Color( 1f, 1f, 1f, 1f * (float)( (float)promptTimer/(float)promptDuration ) );
			ttf.drawString(278, 550, "Press Enter", promptColor );
		}
		
		if(loading) ttf.drawString(600, 550, "loading...", Color.white );
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		Input inp = gc.getInput();
		if( time < 200000 ) time += delta;
		if( loading ) loadTime -= delta;
		
		if( !done ) {
			if( time >= 8700 ) {
				image = huntImage;
				done = true;
			}
			else if( time >= 7200 ) {
				image = darkImage;
			} else if( time>= 4500 ) {
				image = slickImage;
			} else if( time >= 2200 ) {
				image = secondScene;
			} else if( time >= 500 ) {
				image = startImage;
			}
		} else if( !ready && time >= 14000+1500 ) {
			ready = true;
			prompt = true;
		}
		
		// fly shader
		if( time>=2200 && time<3500 ){
			float travel = 1000f * (float)( (float)(time-2200)/1300f );
			flyX = -200 + (int)travel;
		}
		
		// ALL FADER CONTROL
		// scene 1
		if( time<=1000 ) alpha = 1f - (float)time/1000f;
		else if( time>=1700 && time<2200 ) alpha = (float)(time-1700)/500f;
		// scene 2
		else if( time>=2200 && time<=2400 ) alpha = 1f - (float)(time-2200)/200f;
		else if( time>=4300 && time<4500 ) alpha = (float)(time-4300)/200f;
		// slick scene
		else if( time>=4500 && time<=4700 ) alpha = 1f - (float)(time-4500)/200f;
		else alpha=0;
		// final fader
		if( loadTime <= 1000 ) {
			alpha = 1f - (float)( (float)loadTime/1000f );
		}
		
		if( inp.isKeyPressed(Input.KEY_ENTER) ) {
			if( !lock && ready ) {
				if(music.playing()) music.fade(3000, 0, true);
				loading = true;
				prompt = false;
				lock = true;
			} else inp.clearKeyPressedRecord();
		}
			
		if( !lock && ready ) {
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
		
		if( !music.playing() ) {
			if(!done) music.play();
			else {
				music = hunt;
				music.loop();
			}
		}
		
		if( loadTime<=0 ) {
			// TRANSITION AREA
			sbg.enterState(HUNT.MENU);
		}
	}

	@Override
	public int getID() {
		return HUNT.INTRO;
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
