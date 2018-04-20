package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import main.levels.Level01;
import main.levels.Level02;
import main.levels.Level03;
import main.levels.Level04;
import main.levels.Level05;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class HUNT extends StateBasedGame {

	public HUNT() {
		super("H    U    N    T");
	}

	public static void main(String[] args) {
		try {
			AppGameContainer agc = new AppGameContainer( new HUNT() );
			
			agc.setIcon("res/hud external/hunt-game-icon.png");
			agc.setDisplayMode(800, 600, true);
			agc.setTargetFrameRate(60);
			agc.setShowFPS(false);
			agc.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	// CONTROL
	public static boolean alienFree=false;
	
	// UNIVERSAL INFORMATIONS
	public static int playerMaxHealth=5;
	public static int playerCurrentHealth=5;
	public static int playerLife=2;
	public static int currentScore=0;
	public static int highScore=0;
	public static int playerBullet=120;
	public static int playerInitialBullet=40;
	
	// INTER LEVEL DATA
	public static boolean backup = false;
	public static boolean gameWin = false;
	
	// LEVEL NAMES
	public static final int INTRO = 0;
	public static final int MENU = 1;
	
	public static final int LEVEL01 = 2;
	public static final int LEVEL02 = 3;
	public static final int LEVEL03 = 4;
	public static final int LEVEL04 = 5;
	public static final int LEVEL05 = 6;
	
	public static final int GAMEOVER = 500;
	public static final int COMIC = 600;
	
	// SCORE LIST
	public static int top10score[] = new int[15];

	@Override
	public void initStatesList(GameContainer gc) throws SlickException {
		addState( new Intro() );
		addState( new Menu() );
		
		addState( new Level01() );
		addState( new Level02() );
		addState( new Level03() );
		addState( new Level04() );
		addState( new Level05() );
		
		addState( new GameOver() );
		addState( new Comic() );
		
		// TOP 10 SCORE READING
		readData();
		
		alienFree = false;
		enterState( INTRO );
	}
	
	void readData() {
		try {
			Scanner sin = new Scanner( new File( "DO NOT OPEN.txt" ) );
			for( int i=0; i<10; i++ ) {
				top10score[i] = sin.nextInt();
			}
			sin.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println( "problem reading scores" );
		}
	}

}
