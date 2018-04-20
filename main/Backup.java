package main;

import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import main.Alien;
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

public class Backup {
	public static Map map;
	public static ArrayList<Alien> aliens;
	public static Player player;
	
	public static int alienToRemove;
}
