package com.mercury.auton;

import org.usfirst.frc.team1089.robot.Elevator;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.RobotDrive;

public class RCFAST extends Auton {
	private CANTalon	left;
	private CANTalon	right;

	public RCFAST(RobotDrive drive2, Gyro g, Encoder l, Encoder r, Elevator e,
			CANTalon autonArm, CANTalon arm2) {
		super(drive2, g, l, r);
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void output() {
		// TODO Auto-generated method stub

	}

}
