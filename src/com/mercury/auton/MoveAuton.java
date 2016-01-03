package com.mercury.auton;

import org.usfirst.frc.team1089.robot.Claw;
import org.usfirst.frc.team1089.robot.Elevator;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.RobotDrive;

public class MoveAuton extends Auton {
	private enum State {
		RESET, MOVE, DONE
	}

	private State		state;
	private Elevator	lift;
	private Claw		claw;

	public MoveAuton(RobotDrive drive, Gyro g, Encoder left, Encoder right,
			Elevator e, Claw c) {
		super(drive, g, left, right);

		lift = e;
		claw = c;
	}

	@Override
	public void tick() {
		switch (state) {
		case RESET:
			if (!lift.isBottom()) {
				lift.setSpeed(-0.2);
			} else {
				lift.setSpeed(0);
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
			break;
		}
	}

	@Override
	public void start() {
		state = State.MOVE;

		left.reset();
		right.reset();
		claw.setClaw(false);
		claw.setLift(false);
		claw.setHands(Claw.POS.DWN_HRZ);
	}

	@Override
	public void output() {

	}

}
