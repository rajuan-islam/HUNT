package main;

import java.util.ArrayList;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.tiled.TiledMap;

public class Bullet { 
	// CONNECTION
	ArrayList<Alien> aliens;
	Map map;
	
	// VARIABLES
	float x, y;
	float direction;
	float covered;
	
	// CONSTANTS
	float speed=6;
	float lifeTime=100;
	
	// DRAWING CONSTANTS
	float radius=5;
	Image fireball;
	
	public int update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		// LIFETIME END
		if( covered>=lifeTime ) return -1; 
		
		// COLLISION DETECTION
		if( !map.free( x+speed*direction, y ) ) return -1;
		
		// ALIEN KILL
		for( int i=0; i<aliens.size(); i++ ) {
			if( aliens.get(i).contains(x, y) && aliens.get(i).alive ) return i;
		}
		
		// IF ALL OK THEN PROCEED
		x += speed*direction;
		covered += speed;
		
		// EVERYTHING IS FINE
		return -500;
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		if( map.start<=x && x<=map.start+800 ) {
			//g.setColor(Color.blue);
			//g.fillOval( x-radius-map.start, y-radius, radius*2, radius*2 );
			fireball.draw( x-radius-map.start, y-radius );
		}
	}
	
	public void init() throws SlickException {
		covered=0;
		fireball = new Image( "res/player/fireball.png" );
	}
	
	public Bullet( Map map, ArrayList<Alien> aliens , float x, float y, float direction ) {
		this.map = map;
		this.aliens = aliens;
		
		this.x = x;
		this.y = y;
		this.direction = direction;
		
		try {
			init();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
