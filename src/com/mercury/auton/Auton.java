package com.mercury.auton;


import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.RobotDrive;

public abstract class Auton {
	protected static RobotDrive	drive;
	protected static Gyro		gyro;
	protected static Encoder	right, left;

	public Auton(RobotDrive drive2, Gyro g, Encoder l, Encoder r) {
		drive = drive2;
		gyro = g;
		left = l;
		right = r;
	}

	public abstract void tick();

	public abstract void start();

	public abstract void output();
}
