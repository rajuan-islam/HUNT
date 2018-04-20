package main;

import java.util.ArrayList;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.tiled.TiledMap;

public class MotionTracker {
	
	// material
	Image tracker;
	Animation dot;
	
	// contact
	ArrayList <Alien> aliens;
	Map map;
	Player player;
	
	// variables 
	int tx=15-8, ty=473;
	int start=850, end=850+800;
	
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		Input inp = gc.getInput();
		
		start = (int) (map.start + (800*player.direction));
		end = start + 800;
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		tracker.draw(tx+8, ty);
		//dot.draw( tx + 100, ty + 80 );
		
		for( int i=0; i<aliens.size(); i++ ) {
			Alien al = aliens.get(i);
			if( al.x>=start && al.x<=end ) {
				dot.draw( ((float)(al.x-start)*150f)/800f + tx, ((float)(al.y)*112f)/600f + ty );
			}
		}
	}
	
	public void init() throws SlickException {
		tracker = new Image( "res/motion tracker/tracker.png" );
		
		Image dotpic = new Image( "res/motion tracker/dot.png" );
		Image blankDot = new Image( "res/motion tracker/blank dot.png" );
		dot = new Animation(true);
		dot.addFrame(dotpic, 600);
		dot.addFrame(blankDot, 300);
	}
	
	public MotionTracker(ArrayList <Alien> aliens, Map map, Player player) {
		try {
			init();
			this.aliens = aliens;
			this.map = map;
			this.player = player;
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
