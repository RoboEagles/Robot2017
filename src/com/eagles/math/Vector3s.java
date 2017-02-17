package com.eagles.math;

public class Vector3s extends Vector2s {
	
	public short Z;
	
	public Vector3s(short X, short Y, short Z) {
		super(X,Y);
		this.Z = Z;
	}
	
	public Vector3s() {
		this((short) 0,(short) 0,(short) 0);
	}
	
	public double mag() {
		return Math.sqrt(this.X*this.X + this.Y*this.Y + this.Z*this.Z);
	}
	
	public Vector3s add(Vector3s val) {
		
		this.X += val.X;
		this.Y += val.Y;
		this.Z += val.Z;
		
		return this;
		
	}
	
	public Vector3s sub(Vector3s val) {
		
		this.X -= val.X;
		this.Y -= val.Y;
		this.Z -= val.Z;
		
		return this;
		
	}

	public Vector3s mul(Vector3s val) {
		
		this.X *= val.X;
		this.Y *= val.Y;
		this.Z *= val.Z;
		
		return this;
		
	}

	public Vector3s div(Vector3s val) {
		
		this.X /= val.X;
		this.Y /= val.Y;
		this.Z /= val.Z;
		
		return this;
		
	}
	
	public boolean equals(Vector3s val) {
		return this.X==val.X && this.Y==val.Y && this.Z==val.Z;
	}
	
	public void reset() {
		this.X = 0;
		this.Y = 0;
		this.Z = 0;
	}


}