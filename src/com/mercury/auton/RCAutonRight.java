package com.mercury.auton;

import org.usfirst.frc.team1089.robot.Elevator;
import org.usfirst.frc.team1089.robot.Ports;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RCAutonRight extends Auton {

	private enum State{
	    PREP_DRIVE_1, DRIVE_1, DROP, PREP_DRIVE_2, DRIVE_2, DONE
	  }
	  
	  private State state;
	  private long lastTime = 0;
	  private CANTalon armL, armR;
	  
	  public RCAutonRight (RobotDrive drive, Gyro gyro, Encoder leftFront, Encoder rightRear, CANTalon l, CANTalon r){
	    super(drive, gyro, leftFront, rightRear);
	    
	    armL = l;
	    armR = r;
	  }
	  
	  public void tick (){
	    switch (state){
	      case PREP_DRIVE_1:
	        lastTime = System.currentTimeMillis(); 
	        state = State.DRIVE_1;
	        break;
	      case DRIVE_1:
	        drive.mecanumDrive_Cartesian(0, 0.3, 0, gyro.getAngle());
	        if (right.getDistance() < -4 || left.getDistance() < -4){ //TODO check these values
	          drive.mecanumDrive_Cartesian(0, 0, 0, gyro.getAngle());
	          state = State.DROP;
	          lastTime = 0;
	        }
	        
	        break;
	      case DROP:
	        if (armL.getPosition() < (Ports.ARM_UP + Ports.ARM_DOWN) / 2){ //if left arm is all the way up
	          armL.set(-.50);    // drop arm fast
	        }
	        else if (armL.getPosition() > (Ports.ARM_UP + Ports.ARM_DOWN) / 2 && armL.getPosition() < Ports.ARM_DOWN) { // if left arm is down
	          armL.set(-0.3); //slow movement of arm
	        }
	        
	        else
	          armL.set(0.0); // stop arm
	        
	        if (armR.getPosition() < (Ports.P_ARM_UP + Ports.P_ARM_DOWN) / 2){ //if right arm is all the way up
	          armR.set(-.50);    // drop arm fast
	        }
	        else if (armR.getPosition() > (Ports.P_ARM_UP + Ports.P_ARM_DOWN) / 2 && armR.getPosition() < Ports.P_ARM_DOWN) { // if left arm is down
	          armR.set(-0.3); //slow movement of arm
	        }
	        else
	          armR.set(0.0); // stop arm
	        
	        if (armL.getPosition() > Ports.ARM_DOWN && armR.getPosition() > Ports.P_ARM_DOWN){ // if both arms are down
	        	state = State.PREP_DRIVE_2;
	        	armR.set(0.0);
	        	armL.set(0.0);
	        }
	        break;
	      case PREP_DRIVE_2:
	        lastTime = System.currentTimeMillis();
	        state = State.DRIVE_2;
	        break;
	      case DRIVE_2:
	    	
	        drive.mecanumDrive_Cartesian(0, -0.5, 0, gyro.getAngle());
	        if (right.getDistance() > 70 || left.getDistance() > 70){ //TODO check these values
	          drive.mecanumDrive_Cartesian(0, 0, 0, gyro.getAngle());
	          state = State.DONE;
	          lastTime = 0;
	        }
	        break;
	      default:
	    	//  drive.mecanumDrive_Cartesian(0, 0, 0, gyro.getAngle());
	        break;
	    }
	  }
	  
	  public void start(){
	    state = State.PREP_DRIVE_1;
	    
	    left.reset(); //reset both encoders
	    right.reset();
	  }
	  
	  public void output(){
	    SmartDashboard.putString("Auton State", "" + state);
	  }
	/* private enum State {
		RESET, DROP, FORE, LIFT, DONE
	}

	private State		state;
	private long		last;
	private Elevator	lift;
	private CANTalon	arm;
	private CANTalon	parm;
	

	public RCAutonRight(RobotDrive drive2, Gyro g, Encoder l, Encoder r,
			Elevator e, CANTalon autonArm, CANTalon arm2) {
		super(drive2, g, l, r);

		parm = arm2;
		arm = autonArm;
		lift = e;
	
	}

	@Override
	public void tick() {
		switch (state) {
		case RESET:
			arm.set(.7);
			parm.set(.7);
			//SmartDashboard.putString("auto STATE", "RESET");
			if (arm.getPosition() < Ports.ARM_DOWN && parm.getPosition() < Ports.P_ARM_DOWN) {
				state = State.FORE;
				arm.set(0);
				parm.set(0);

				left.reset();
				right.reset();
			}
			break;
		case DROP:
			arm.set(-.5);
			if (arm.getPosition() > 463){
				arm.set(-0.2);
			}
			parm.set(-.5);
			if (parm.getPosition() > 423){
				arm.set(-0.2);
			}
			
			SmartDashboard.putString("auto STATE", "DROP");
			if (arm.getPosition() > Ports.ARM_DOWN) {
				state = State.DONE;
				//TODO
				arm.set(0);
				parm.set(0);

				left.reset();
				right.reset();
			}
			break;
		case FORE:
			SmartDashboard.putString("auto STATE", "FORE");
			/*if (right.getDistance() > 120 || left.getDistance() > 120) {
				drive.mecanumDrive_Cartesian(0, 0, 0, gyro.getAngle());
				last = System.currentTimeMillis();
			} else if (right.getDistance() <= 120 || left.getDistance() <= 120){
				drive.mecanumDrive_Cartesian(0, -.8, 0, gyro.getAngle());
			}
			
			if (right.getDistance() < 200000) {
				drive.mecanumDrive_Cartesian(0, .9, 0, gyro.getAngle());
			}
			state = State.DROP; 
			break;
		case LIFT:
			SmartDashboard.putString("auto STATE", "LIFT");
			if (System.currentTimeMillis() - last > 500) {
				state = State.DONE;

				left.reset();
				right.reset();
				// arm.set(Ports.AUTO_FORE * -1);
			}
			
			break;
		case DONE:
			break;
		}
	}

	@Override
	public void start() {
		state = State.RESET;
		last = System.currentTimeMillis();

		left.reset();
		right.reset();
	}

	@Override
	public void output() {

	}
	*/

	
	

}
