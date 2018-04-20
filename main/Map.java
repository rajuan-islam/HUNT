package main;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.tiled.TiledMap;

public class Map {
	// LOAD
		TiledMap map;
		
		// VARIABLES
		float x, start;
		int tileX;
		
		// DEPENDENT CONSTANTS;
		int solid;
		int green;
		int AxisX;
		
		public boolean free( float x, float y ) {
			int tx = (int) (x / 50);
			int ty = (int) (y / 50);
			if( ty<0 ) return false;
			if( tx<0 || tx>AxisX*50 ) return false;
			if( map.getTileId(tx, ty, solid)==0 ) return true;
			return false;
		}
		
		public boolean inGreen( float x, float y ) {
			int tx = (int) (x / 50);
			int ty = (int) (y / 50);
			if( ty<0 ) return false;
			if( tx<0 || tx>AxisX*50 ) return false;
			if( map.getTileId(tx, ty, green)==0 ) return false;
			return true;
		}
		
		public void update(GameContainer gc, StateBasedGame sbg, int delta)
				throws SlickException {
			if( x<-50 ) {
				x += 50;
				tileX++;
			} else if( x>=0 ) {
				x -= 50;
				tileX--;
			}
			start = tileX*50 - x;			
		}
		
		public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
				throws SlickException {
			map.render((int)x, 0, tileX, 0, 18, 12);
			
			/*// DEBUG
			g.setColor(Color.white);
			g.drawString("X "+x, 650, 50);
			g.drawString("Start "+start, 650, 80);
			g.drawString("TileX "+tileX, 650, 110);*/
		}
		
		public void init() throws SlickException {
			// VARIABLES
			x=-50; start=50;
			tileX=0;
			
			// DEPENDENT CONSTANTS
			AxisX = map.getWidth();			
		}
		
		public Map( TiledMap map, String solidLevelName, String greenLevelName ) {
			this.map = map;
			solid = map.getLayerIndex(solidLevelName);
			green = map.getLayerIndex(greenLevelName);
			try {
				init();
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
}
