package com.ColtonDaCoder.Lucius.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ColtonDaCoder.Lucius.Robot.LiftStates;
import com.ColtonDaCoder.Lucius.Robot;
import static com.ColtonDaCoder.Lucius.Robot.*;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class  Lift extends SubsystemBase{
    
    private XboxController controller;
    
    private TalonSRX liftMotor;
    private TalonSRX liftSlave;

    private double input;
    private double power = 0;

    public TalonSRXConfiguration liftMotorSRXconfig;
    public TalonSRXConfiguration liftSlaveSRXconfig;


    public DigitalInput highLimit;
    public DigitalInput lowLimit;

    public Lift(XboxController controller){
        this.controller = controller;
        liftMotor = new TalonSRX(1);
        liftSlave = new TalonSRX(2);
        liftSlave.setInverted(true);
        liftSlave.follow(liftMotor);

        liftMotor.configFactoryDefault();
        liftMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);

        highLimit = new DigitalInput(9);
        lowLimit = new DigitalInput(8);

        liftMotorSRXconfig = new TalonSRXConfiguration();
        liftSlaveSRXconfig = new TalonSRXConfiguration();

        liftMotorSRXconfig.continuousCurrentLimit = 35;
        liftMotorSRXconfig.peakCurrentLimit = 60;
        liftMotorSRXconfig.peakCurrentDuration = 100;
        liftMotor.configAllSettings(liftMotorSRXconfig);

        liftSlaveSRXconfig.continuousCurrentLimit = 35;
        liftSlaveSRXconfig.peakCurrentLimit = 60;
        liftSlaveSRXconfig.peakCurrentDuration = 100;
        liftSlave.configAllSettings(liftSlaveSRXconfig);
        liftMotor.setSelectedSensorPosition(0);
    }


    @Override
    public void periodic(){        

        SmartDashboard.putNumber("lift position ", liftMotor.getSelectedSensorPosition());

        boolean highBool = !highLimit.get();
        boolean lowBool = !lowLimit.get();
        input = deadband(-controller.getRawAxis(5)) * 0.5;
        switch(liftState){
            case lift:
                if(stateChange(input < 0, LiftStates.lower))  break;
                if(stateChange(highBool, LiftStates.hold))  break;
                power = input;
            break;
            case lower:
                if(stateChange(input > 0, LiftStates.lift))  break;
                if(stateChange(lowBool, LiftStates.free))  break;
                power = input * 0.2;
            break;
            case hold:
                if(stateChange((Turret.getZero() && input < 0), LiftStates.lower))  break;
                power = highBool ? 0.05 : input;
            break;
            case free:
                power = 0;
                if(stateChange(input > 0, LiftStates.lift))  break;
                liftState = input > 0 ? LiftStates.lift : LiftStates.free;
            break;
        }
        liftMotor.set(ControlMode.PercentOutput, power);
        SmartDashboard.putString("lift state ", liftState.toString());
        SmartDashboard.putNumber("lift input ", input);
        SmartDashboard.putNumber("lift power ", power);
    }

    private boolean stateChange(Boolean condition, LiftStates state){
        if(condition){
            liftState = state;
            return true;
        }
        return false;
    }

}