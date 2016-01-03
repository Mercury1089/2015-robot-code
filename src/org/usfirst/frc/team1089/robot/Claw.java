package org.usfirst.frc.team1089.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Talon;

public class Claw {
	private Talon				wristsLeft, wristsRight;
	private DoubleSolenoid		lift, claw;
	private long				liftStart, clawStart;
	private AnalogInput			leftLoc, rightLoc;
	private boolean				single;

	private double				leftGoal, rightGoal;
	private double				MAX					= 4, MIN = 1;

	private STATE				state;
	private boolean				manual;
	private boolean				isUp;
	private long				prev;

	private final static double	DWN_HRZ_CONST_LEFT	= 2.252;
	private final static double	DWN_HRZ_CONST_RIGHT	= 2.680;

	private final static double	DWN_VRT_DIFF_LEFT	= 0.367;
	private final static double	DWN_VRT_DIFF_RIGHT	= 0.374;

	private final static double	UP_HRZ_DIFF_LEFT	= 0.317;
	private final static double	UP_HRZ_DIFF_RIGHT	= 0.330;

	public static enum POS {
		UP, DWN_HRZ, DWN_VRT
	}

	private static enum STATE {
		WAIT, SOLENOID, STOP, MOVE
	}

	public Claw() {
		wristsLeft = new Talon(Ports.PWM.WRISTS_LEFT);
		wristsRight = new Talon(Ports.PWM.WRISTS_RIGHT);
		lift = new DoubleSolenoid(Ports.PCM.LIFT_1, Ports.PCM.LIFT_2);
		claw = new DoubleSolenoid(Ports.PCM.CLAW_1, Ports.PCM.CLAW_2);
		leftLoc = new AnalogInput(Ports.Analog.LEFT_W_LOC);
		rightLoc = new AnalogInput(Ports.Analog.RIGHT_W_LOC);
	}

	public void tick() {
		double speed = 0.6;
		double eps = 0.015;
		if (state == STATE.WAIT) {
			if (System.currentTimeMillis() - prev > 600)
				state = STATE.MOVE;
		} else if (state == STATE.MOVE && !manual) {
			if (leftLoc.getVoltage() > leftGoal + eps) {
				wristsLeft.set(-speed * 1.07);
			} else if (leftLoc.getVoltage() < leftGoal - eps) {
				wristsLeft.set((speed) * 1.07);
			} else {
				wristsLeft.set(0);
			}

			if (rightLoc.getVoltage() > rightGoal + eps) {
				wristsRight.set(-speed);
			} else if (rightLoc.getVoltage() < rightGoal - eps) {
				wristsRight.set((speed));
			} else {
				wristsRight.set(0);
			}

			if (rightLoc.getVoltage() > rightGoal + eps
					&& rightLoc.getVoltage() < rightGoal - eps) {
				state = STATE.STOP;
			}
		} else if (state == STATE.STOP) {
			if (rightLoc.getVoltage() > rightGoal + eps * 2
					|| rightLoc.getVoltage() < rightGoal - eps * 2) {
				state = STATE.MOVE;
			}
		}
	}

	public void setLift(boolean up) {
		if (System.currentTimeMillis() - liftStart < Ports.DELAY)
			return;
		isUp = up;
		lift.set(up ? Value.kForward : Value.kReverse);
		if (up) {
			state = STATE.WAIT;
			prev = System.currentTimeMillis();
			setHands(POS.UP);
			state = STATE.WAIT;
		} else {
			setHands(POS.DWN_HRZ);
		}
		liftStart = System.currentTimeMillis();
	}

	public void setClaw(boolean in) {
		if (System.currentTimeMillis() - clawStart < Ports.DELAY)
			return;

		claw.set(in ? Value.kReverse : Value.kForward);
		clawStart = System.currentTimeMillis();
	}

	public void rotateHands(double speed) {
		if (!manual) {
			return;
		}

		int sgn = (int) Math.signum(speed);
		speed *= 1;

		if ((sgn == 1 && leftLoc.getVoltage() < MAX)
				|| (sgn == -1 && leftLoc.getVoltage() > MIN) || sgn == 0) {
			wristsLeft.set(speed);
		} else {
			wristsLeft.set(0);
		}
		if ((sgn == 1 && rightLoc.getVoltage() < MAX)
				|| (sgn == -1 && rightLoc.getVoltage() > MIN) || sgn == 0) {
			if (!single) {
				wristsRight.set(speed);
			} else {
				wristsRight.set(0);
			}
		} else {
			wristsRight.set(0);
		}
	}

	public void setManual(boolean m) {
		manual = m;

	}

	public boolean isManual() {
		return manual;
	}

	public void setHands(POS p) {
		state = STATE.MOVE;
		switch (p) {
		case UP:
			if (!isUp()) {
				return;
			}
			leftGoal = DWN_HRZ_CONST_LEFT - UP_HRZ_DIFF_LEFT;
			rightGoal = DWN_HRZ_CONST_RIGHT - UP_HRZ_DIFF_RIGHT;
			state = STATE.WAIT;
			break;
		case DWN_HRZ:
			if (isUp()) {
				return;
			}
			leftGoal = DWN_HRZ_CONST_LEFT;
			rightGoal = DWN_HRZ_CONST_RIGHT;
			break;
		case DWN_VRT:
			if (isUp()) {
				return;
			}
			leftGoal = DWN_HRZ_CONST_LEFT - DWN_VRT_DIFF_LEFT;
			rightGoal = DWN_HRZ_CONST_RIGHT - DWN_VRT_DIFF_RIGHT;
			break;
		}
	}

	public double getLeftHandState() {
		return wristsLeft.get();
	}

	public double getRightHandState() {
		return wristsRight.get();
	}

	public boolean isIn() {
		return claw.get() == Value.kReverse;
	}

	public boolean isUp() {
		return isUp;
	}

	public double getLeftLoc() {
		return leftLoc.getVoltage();
	}

	public double getRightLoc() {
		return rightLoc.getVoltage();
	}

	public boolean isSingle() {
		return single;
	}

	public void setSingle(boolean s) {
		single = s;
	}

	public boolean isHome() {
		return Math.abs(leftLoc.getVoltage() - leftGoal) < .05
				&& Math.abs(rightLoc.getVoltage() - rightGoal) < .05;
	}

	public void stop() {
		rotateHands(0);
	}

	public void debug() {
		SmartDashboard.putBoolean("manual", manual);
		SmartDashboard.putBoolean("single", single);

		SmartDashboard.putNumber("Left Wrist", getLeftLoc());
		SmartDashboard.putNumber("Right Wrist", getRightLoc());

		SmartDashboard.putNumber("Left goal", leftGoal);
		SmartDashboard.putNumber("Right goal", rightGoal);

		SmartDashboard.putBoolean("is up", isUp());
		SmartDashboard.putBoolean("is in", isIn());
	}
}