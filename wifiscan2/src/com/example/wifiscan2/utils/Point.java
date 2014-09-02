package com.example.wifiscan2.utils;

public class Point {
	public int x;
	public int y;
	public Point() {
		this(0,0);
	}
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "[x=" + x + ", y=" + y + "]";
	}
	
}
