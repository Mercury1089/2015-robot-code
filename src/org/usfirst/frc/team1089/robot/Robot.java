package org.usfirst.frc.team1089.robot;

import java.util.Arrays;

import org.usfirst.frc.team1089.robot.Claw.POS;

import com.mercury.auton.Auton;
import com.mercury.auton.MoveAuton;
import com.mercury.auton.NoAuton;
import com.mercury.auton.RCAutonRight;
import com.mercury.auton.RCToteAuton;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.ControlMode;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	private RobotDrive		drive;
	private Joystick		rightStick, leftStick, gamepad;
	private Encoder			rre, rle, fle, fre;
	private Gyro			gyro;

	private Claw			claw;
	private Elevator		lift;

	private SendableChooser	auton;

	private boolean[]		btnPrev;
	private boolean[]		btn;

	private CANTalon		autonArm;
	private CANTalon		arm2;

	public void robotInit() {
		drive = new RobotDrive(Ports.PWM.DRIVE_FRONT_LEFT,
				Ports.PWM.DRIVE_REAR_LEFT, Ports.PWM.DRIVE_FRONT_RIGHT,
				Ports.PWM.DRIVE_REAR_RIGHT);
		drive.setInvertedMotor(MotorType.kFrontRight, true);
		drive.setInvertedMotor(MotorType.kRearRight, true);

		rightStick = new Joystick(Ports.USB.RIGHT_STICK);
		leftStick = new Joystick(Ports.USB.LEFT_STICK);
		gamepad = new Joystick(Ports.USB.GAMEPAD);

		rre = new Encoder(Ports.Digital.ENCODER_REAR_RIGHT_1,
				Ports.Digital.ENCODER_REAR_RIGHT_2, false, EncodingType.k4X);
		rle = new Encoder(Ports.Digital.ENCODER_REAR_LEFT_1,
				Ports.Digital.ENCODER_REAR_LEFT_2, true, EncodingType.k4X);
		fre = new Encoder(Ports.Digital.ENCODER_FRONT_RIGHT_1,
				Ports.Digital.ENCODER_FRONT_RIGHT_2, false, EncodingType.k4X);
		fle = new Encoder(Ports.Digital.ENCODER_FRONT_LEFT_1,
				Ports.Digital.ENCODER_FRONT_LEFT_2, true, EncodingType.k4X);

		rre.setDistancePerPulse(6 * Math.PI / 360);
		rle.setDistancePerPulse(6 * Math.PI / 360);
		fre.setDistancePerPulse(6 * Math.PI / 360);
		fle.setDistancePerPulse(6 * Math.PI / 360);

		rre.setSamplesToAverage(127);
		rle.setSamplesToAverage(127);
		fre.setSamplesToAverage(127);
		fle.setSamplesToAverage(127);

		gyro = new Gyro(Ports.Analog.GYRO);
		gyro.setSensitivity((1.1 * 5 / 3.38) / 1000);

		claw = new Claw();
		claw.setHands(POS.DWN_VRT);

		lift = new Elevator();

		autonArm = new CANTalon(2);
		arm2 = new CANTalon(1);

		autonArm.changeControlMode(ControlMode.PercentVbus);
		arm2.changeControlMode(ControlMode.PercentVbus);
		
		autonArm.setFeedbackDevice(FeedbackDevice.AnalogPot);
		arm2.setFeedbackDevice(FeedbackDevice.AnalogPot);

		auton = new SendableChooser();
		auton.addDefault("Move", new MoveAuton(drive, gyro, fle, rre, lift,
				claw));
		//auton.addObject("RC Right", new RCAutonRight(drive, gyro, fle, rre,
		//		lift, autonArm, arm2));
		auton.addObject("RC Right", new RCAutonRight(drive, gyro, fle, rre, autonArm, arm2));
		/*auton.addObject("RC Left", new RCAutonLeft(drive, gyro, fle, rre, lift,
				autonArm, arm2));*/

		auton.addObject("none", new NoAuton(drive, gyro, fle, rre));
		auton.addObject("RC Tote", new RCToteAuton(drive, gyro, fle, rre, lift,
				claw));

		SmartDashboard.putData("Auton", auton);
	}

	public void autonomousPeriodic() {
		((Auton) auton.getSelected()).tick();
		tick();
	}
	
	public void teleopInit(){
		autonArm.changeControlMode(ControlMode.PercentVbus);
		arm2.changeControlMode(ControlMode.PercentVbus);
	}

	public void teleopPeriodic() {
		btn = new boolean[11];
		for (int i = 1; i <= 10; i++) {
			btn[i] = gamepad.getRawButton(i);
		}
		double scale = 0.8;
		double x = scale * deadZone(rightStick.getRawAxis(0));
		double y = scale * deadZone(rightStick.getRawAxis(1));
		double r = scale * deadZone(leftStick.getRawAxis(0));

		drive.mecanumDrive_Cartesian(x, y, r, 0);

		if (rightStick.getRawButton(8) && autonArm.getPosition() > 384) {
			autonArm.set(.99);
			//arm2.set(.3);
		}else if (rightStick.getRawButton(9) && autonArm.getPosition() < 423) {
			autonArm.set(-.3);
			//arm2.set(-.3);
		}else{
			autonArm.set(0);
			//arm2.set(0);
		}
	
		//New Arm WITH BOTH POTS working
		
		if (rightStick.getRawButton(8))
		{
			if (autonArm.getPosition() < Ports.ARM_DOWN) {
				if (autonArm.getPosition() > (Ports.ARM_DOWN + Ports.ARM_UP) / 2) {
					autonArm.set(-.2);
				}
				else{
					autonArm.set(-.7);
				}
				
			}
			else
				autonArm.set(0);
			
			if(arm2.getPosition() < Ports.P_ARM_DOWN ) {
				if (arm2.getPosition() > (Ports.P_ARM_UP + Ports.P_ARM_DOWN) / 2) {
					arm2.set(-.2);
				}
				else{
				    arm2.set(-.7);
				}
			}
			else
			    arm2.set(0);
		}
		else if(rightStick.getRawButton(9))
		{
			if(autonArm.getPosition() > Ports.ARM_UP)
			{
				if (autonArm.getPosition() > (Ports.ARM_UP + Ports.ARM_DOWN) / 2)
					autonArm.set(.7);
				else
					autonArm.set(.3);
			}
				else
					autonArm.set(0);
			
			if(arm2.getPosition() > Ports.P_ARM_UP)
			{
				if (arm2.getPosition() > (Ports.P_ARM_DOWN + Ports.P_ARM_UP) / 2 )
					arm2.set(.7);
				else
					arm2.set(.3);
			}
			else
			    arm2.set(0);
		}
		else
		{
			autonArm.set(0);
			arm2.set(0);
		}
		//end new arm process

		if (button(5)) {
			claw.setLift(!claw.isUp());
		}

		if (button(6)) {
			claw.setClaw(!claw.isIn());
		}

		if (button(10)) {
			claw.setSingle(!claw.isSingle());
		}

		if (button(8)) {
			claw.setManual(!claw.isManual());
		}

		if (rightStick.getRawButton(6)) {
			gyro.reset();
		}

		if (gamepad.getRawAxis(2) > 0.9) {
			lift.setSafetyScale(false);
		} else {
			lift.setSafetyScale(true);
		}

		if (gamepad.getRawAxis(3) > 0.9) {
			lift.toTote();
		}

		if (button(1)) {
			claw.setHands(POS.DWN_HRZ);
		} else if (button(2)) {
			claw.setHands(POS.DWN_VRT);
		} else if (button(4)) {
			claw.setHands(POS.UP);
		}
		claw.rotateHands(deadZone(gamepad.getRawAxis(5)));
		if (!lift.goingToTote()) {
			lift.setSpeed(deadZone(-gamepad.getRawAxis(1)));
		}

		tick();
		debug();
		btnPrev = Arrays.copyOf(btn, 11);
	}

	public void tick() {
		lift.tick();
		claw.tick();
	}

	private double deadZone(double rawAxis) {
		return Math.abs(rawAxis) > .2 ? rawAxis : 0;
	}

	public void disabledPeriodic() {
		drive.drive(0, 0);
		claw.stop();
		lift.stop();
		debug();

		if (rightStick.getRawButton(6)) {
			gyro.initGyro();
		}
	}

	public void autonomousInit() {
		((Auton) auton.getSelected()).start();
	}

	public void disabledInit() {

	}

	public boolean button(int i) {
		return btn[i] && !btnPrev[i];
	}

	public void debug() {
		if (rre.getRate() < 200) {
			SmartDashboard.putNumber("rr", rre.getRate());
			SmartDashboard.putNumber("rrn", rre.getRate());
		}
		if (rle.getRate() < 200) {
			SmartDashboard.putNumber("rl", rle.getRate());
			SmartDashboard.putNumber("rln", rle.getRate());
		}
		if (fre.getRate() < 200) {
			SmartDashboard.putNumber("fr", fre.getRate());
			SmartDashboard.putNumber("frn", fre.getRate());
		}
		if (fle.getRate() < 200) {
			SmartDashboard.putNumber("fl", fle.getRate());
			SmartDashboard.putNumber("fln", fle.getRate());
		}

		SmartDashboard.putNumber("fle dist", fle.getDistance());
		SmartDashboard.putNumber("rre dist", rre.getDistance());

		SmartDashboard.putNumber("Gyro", gyro.getAngle());

		lift.debug();
		claw.debug();
		
		SmartDashboard.putNumber("POT TALON L POSITION", autonArm.getPosition());
		SmartDashboard.putNumber("POT TALON R POSITION", arm2.getPosition());
	}
}
