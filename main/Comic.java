package main;

import java.awt.Font;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.tiled.TiledMap;

public class Comic extends BasicGameState {
	
	// IMAGES
	Image sc1, sc2, sc3, sc4;
	float x1, x2, y3;
	boolean p1, p2, p3;
	int s1, s2, s3;
	
	// variables
	int state;
	
	// TRANSACTION FADER MATERIAL
	boolean ready,goodToGo;
	int duration=3000;
	int timer;
	
	// MUSIC
	Music music;

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		Input inp = gc.getInput();
		
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
		if( !ready ){
			if( inp.isKeyPressed( Input.KEY_ENTER ) );
			return;
		}
		
		// page change
		if( p1 && x1>-800 ) {
			s1 += delta;
			x1 = (-800f)*(s1*1f)/(duration*0.5f);
		}
		if( p2 && x2<800 ) {
			s2 += delta;
			x2 = (800f)*(s2*1f)/(duration*0.5f);
		}
		if( p3 && y3>-800 ) {
			s3 += delta;
			y3 = (-800f)*(s3*1f)/(duration*0.5f);
		}
		
		// page change control
		if( inp.isKeyPressed( Input.KEY_ENTER ) ) {
			if( state==1 ) {
				p1 = true;
				state = 2;
				s1 = 0;
			} else if( state==2 ){
				p2 = true;
				state = 3;
				s2 = 0;
			} else if( state==3 ){
				p3 = true;
				state = 4;
				s3 = 0;
			} else if( state==4 ){
				goodToGo = true;
				state = 5;
			}
		}
		
		// LEAVING FADER
		if( goodToGo ) {
				timer -= delta;
			if( timer<=0 ) {
				// fixing
				timer = 0;
				
				// LEAVE THIS STATE
				sbg.enterState(HUNT.LEVEL01);
			}
			return;
		}
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		sc4.draw( 0, 0 );
		sc3.draw( 0, y3 );
		sc2.draw( x2, 0 );
		sc1.draw( x1, 0 );
		
		// FADER
		if( !ready || goodToGo ) {
			Color fadeColor = new Color( 0f, 0f, 0f, 1f - (float)( (float)timer/(float)duration ) );
			g.setColor(fadeColor);
			g.fillRect(0, 0, 800, 600);
			g.setColor(Color.white);
		}
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		// IMAGES
		sc1 = new Image( "res/comic/scene 1.png" );
		sc2 = new Image( "res/comic/scene 2.png" );
		sc3 = new Image( "res/comic/scene 3.png" );
		sc4 = new Image( "res/comic/scene 4.png" );
		
		// VARIABLES
		x1 = x2 = y3 = 0;
		p1 = p2 = p3 = false;
		state = 1;
		
		// MUSIC
		music = new Music( "res/comic/comic music.wav" );
		music.loop();
		
		// TRANSACTION SCREEN FADER
		timer=0;
		ready=false;
		goodToGo=false;
	}

	@Override
	public int getID() {
		return HUNT.COMIC;
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
