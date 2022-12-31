package com.mrboomdev.platformer.entity.data;

public class PlayerConfigData {
	public String name = "Unknown character";
	public String tag = "A custom character";
	public int hp = 100;
	public int speed = 10;
	public int shield = 10;
	public int attack = 10;
	public int[] size;
	public Bones bones;
	
	class Bones {
		public Bone head;
		public Bone body;
		public Bone arm;
		public Bone leg;
	}
	
	class Bone {
		public int[] from_texture;
		public int[] position;
		public int[] size;
	}
}