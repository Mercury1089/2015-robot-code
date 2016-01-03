package com.mercury.auton;

import org.usfirst.frc.team1089.robot.Claw;
import org.usfirst.frc.team1089.robot.Elevator;
import org.usfirst.frc.team1089.robot.Claw.POS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.RobotDrive;

public class RCToteAuton extends Auton {

	private enum State {
		CHECK, CLOSE, LIFT, MOVE, DONE
	}

	private State		state;
	private long		last;
	private Elevator	lift;
	private Claw		claw;

	public RCToteAuton(RobotDrive drive2, Gyro g, Encoder l, Encoder r,
			Elevator e, Claw c) {
		super(drive2, g, l, r);
	}

	@Override
	public void tick() {
		switch(state){
		case CHECK:
			if(claw.isHome()){
				claw.setClaw(true);
				last = System.currentTimeMillis();
				state = State.CLOSE;
			}
			break;
			
		case CLOSE:
			if(System.currentTimeMillis() - last > 100){
				lift.setSpeed(.4);
				state = State.LIFT;
			}
			break;
		
		case LIFT:
			if(lift.getHeight() >= 4){
				lift.setSpeed(0);
				left.reset();
				right.reset();
				state = State.MOVE;
			}
			break;
			
		case MOVE:
			drive.mecanumDrive_Cartesian(0, -.5, 0, gyro.getAngle());
			if (right.getDistance() > 136 || left.getDistance() > 136) {
				state = State.DONE;
				drive.drive(0, 0);
			}
			break;
			
		case DONE:
			drive.drive(0, 0);
			break;
		}
	}

	@Override
	public void start() {
		claw.setHands(POS.DWN_HRZ);
		state = State.CHECK;
	}

	@Override
	public void output() {
		
	}

}
