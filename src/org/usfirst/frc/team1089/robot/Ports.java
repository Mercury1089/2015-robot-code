package org.usfirst.frc.team1089.robot;

public class Ports {
	public static final int		DELAY		= 100;

	public static final double	AUTO_FORE	= -1; 

	public static final double	ARM_DOWN	= 718;//value must be lower than all the way down
	public static final double	ARM_UP		= 690; //value must be greater than all the way up
	public static final double P_ARM_DOWN   = 412; //value must be lower than all the way down
	public static final double P_ARM_UP   =	395;	//value must be greater than all the way up
			

	public static final double	P			= 2.5;
	public static final int		I			= 0;
	public static final int		D			= 0; 

	public static class Analog {
		public static final int	GYRO		= 0;
		public static final int	LEFT_W_LOC	= 2;
		public static final int	RIGHT_W_LOC	= 1;
	}

	public static class Digital {
		public final static int	ENCODER_REAR_LEFT_1		= 6;
		public final static int	ENCODER_REAR_LEFT_2		= 7;
		public final static int	ENCODER_REAR_RIGHT_1	= 2;
		public final static int	ENCODER_REAR_RIGHT_2	= 3;
		public final static int	ENCODER_FRONT_LEFT_1	= 0;
		public final static int	ENCODER_FRONT_LEFT_2	= 1;
		public final static int	ENCODER_FRONT_RIGHT_1	= 8;
		public final static int	ENCODER_FRONT_RIGHT_2	= 9;
		public static final int	ENCODER_LIFT_1			= 4;
		public static final int	ENCODER_LIFT_2			= 5;

		public static final int	LIFT_INDEX				= 10;
	}

	public static class PWM {
		public static final int	DRIVE_FRONT_LEFT	= 2;
		public static final int	DRIVE_FRONT_RIGHT	= 3;
		public static final int	DRIVE_REAR_LEFT		= 1;
		public static final int	DRIVE_REAR_RIGHT	= 0;

		public static final int	ELEVATOR			= 5;
		public static final int	WRISTS_LEFT			= 6;
		public static final int	WRISTS_RIGHT		= 7;

		public static final int	AUTO_ARM			= 4;
	}

	public static class USB {
		public static final int	LEFT_STICK	= 0;
		public static final int	RIGHT_STICK	= 1;

		public static final int	GAMEPAD		= 2;
	}

	public static class PCM {
		public static final int	AUTO_1		= 2;
		public static final int	AUTO_2		= 4;

		public static final int	CLAW_1		= 3;
		public static final int	CLAW_2		= 5;
		public static final int	LIFT_1		= 0;
		public static final int	LIFT_2		= 1;

		public static final int	STOPPER_1	= 7;
		public static final int	STOPPER_2	= 6;
	}
}