package com.mercury.auton;

import org.usfirst.frc.team1089.robot.Elevator;
import org.usfirst.frc.team1089.robot.Ports;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RCAutonLeft extends Auton {
	private enum State {
		RESET, DROP, FORE, LIFT, DONE
	}

	private State		state;
	private long		last;
	private Elevator	lift;
	private CANTalon	arm;
	private CANTalon	parm;

	public RCAutonLeft(RobotDrive drive2, Gyro g, Encoder l, Encoder r,
			Elevator e, CANTalon autonArm, CANTalon arm2) {
		super(drive2, g, l, r);

		arm = autonArm;
		parm = arm2;
		lift = e;
	}

	@Override
	public void tick() {
		switch (state) {
		case RESET:
			lift.setSpeed(0);
			state = State.DROP;
			left.reset();
			right.reset();
			arm.set(Ports.ARM_DOWN);
			parm.set(2);
			break;
		case DROP:
			arm.set(Ports.ARM_DOWN);
			parm.set(2);
			SmartDashboard.putString("auto STATE", "DROP");
			if (System.currentTimeMillis() - last > 150) {
				state = State.FORE;
				arm.set(-0.1);

				left.reset();
				right.reset();
			}
			break;
		case FORE:
			SmartDashboard.putString("auto STATE", "FORE");
			if (right.getDistance() > 100 || left.getDistance() > 100) {
				state = State.LIFT;
				drive.drive(0, 0);
				last = System.currentTimeMillis();
			} else {
				drive.mecanumDrive_Cartesian(0, -.8, 0, gyro.getAngle());
			}
			break;
		case LIFT:
			SmartDashboard.putString("auto STATE", "LIFT");
			if (System.currentTimeMillis() - last > 500) {
				state = State.DONE;

				left.reset();
				right.reset();
			}
			break;
		case DONE:
			break;
		}
	}

	@Override
	public void start() {
		state = State.DROP;
		last = System.currentTimeMillis();

		left.reset();
		right.reset();
	}

	@Override
	public void output() {

	}

}
