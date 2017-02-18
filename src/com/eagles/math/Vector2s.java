package com.eagles.math;

public class Vector2s {
	
	public short X,Y;
	
	public Vector2s(short X, short Y) {
		this.X = X;
		this.Y = Y;
	}
	
	public Vector2s add(Vector2s val) {
		
		this.X += val.X;
		this.Y += val.Y;
		
		return this;
		
	}
	
	public Vector2s sub(Vector2s val) {
		
		this.X -= val.X;
		this.Y -= val.Y;
		
		return this;
		
	}
	
	public Vector2s mul(Vector2s val) {
		
		this.Y *= val.Y;
		this.X *= val.X;
		
		
		return this;
		
	}
	
	public Vector2s div(Vector2s val) {
		
		this.Y /= val.Y;
		this.X /= val.X;
		
		return this;
		
	}
	
	public boolean equals(Vector2s val) {
		
		return this.Y == val.Y && this.X == val.X;
		
	}
	
	public double mag() {
		
		return Math.sqrt(this.Y*this.Y + this.X*this.X);
		
	}
	
	public double angleYX() {
		return Math.atan((this.Y / this.X));
	}
	
	public void reset() {
		this.X = 0;
		this.Y = 0;
	}

}
