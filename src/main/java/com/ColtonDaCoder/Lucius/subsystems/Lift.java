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
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class  Lift extends SubsystemBase{
    
    private XboxController controller;
    
    private TalonSRX liftMotor;
    private TalonSRX liftSlave;

    private double input;
    private double power = 0;
    private double position = 0;

    public TalonSRXConfiguration liftMotorSRXconfig;
    public TalonSRXConfiguration liftSlaveSRXconfig;


    public int highTarget = 41030;
    public int lowTarget = 200;

    public Lift(XboxController controller){
        this.controller = controller;
        liftMotor = new TalonSRX(20);
        liftSlave = new TalonSRX(1);
        liftMotor.configFactoryDefault();
        liftSlave.configFactoryDefault();
        liftMotor.setSensorPhase(false);

        liftMotor.setInverted(true);
        liftSlave.setInverted(false);

        liftSlave.follow(liftMotor);

        liftMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        liftMotorSRXconfig = new TalonSRXConfiguration();
        liftSlaveSRXconfig = new TalonSRXConfiguration();

        liftMotorSRXconfig.continuousCurrentLimit = 35;
        liftMotorSRXconfig.peakCurrentLimit = 60;
        liftMotorSRXconfig.peakCurrentDuration = 100;
        liftMotorSRXconfig.forwardSoftLimitThreshold  = highTarget;
        liftMotorSRXconfig.reverseSoftLimitThreshold = lowTarget;
        liftMotorSRXconfig.slot0.kP = 1;
        liftMotorSRXconfig.slot0.kI = 0;
        liftMotorSRXconfig.slot0.kD = 0;
        liftMotor.configAllSettings(liftMotorSRXconfig);
        liftMotor.configForwardSoftLimitEnable(true);
        liftMotor.configReverseSoftLimitEnable(false);
        liftSlaveSRXconfig.continuousCurrentLimit = 35;
        liftSlaveSRXconfig.peakCurrentLimit = 60;
        liftSlaveSRXconfig.peakCurrentDuration = 100;
        liftSlave.configAllSettings(liftSlaveSRXconfig);
        liftMotor.setSelectedSensorPosition(0);
    }

    @Override
    public void periodic(){  
        input = deadband(-controller.getRawAxis(5)) * 0.5;
        //liftMotor.set(ControlMode.PercentOutput, input);
        switch(liftState){
            case control:
                if(stateChange(nearHigh() && !(input < 0), LiftStates.hold)) break;
                if(controller.getRawButton(1)){
                    input = 0.05;
                }
                power = input;
            break;
            case hold:
                if(stateChange((Turret.getZero() && input < 0) || !nearHigh(), LiftStates.control))  break;
                power = 0.08;
            break;
        }

        liftMotor.set(ControlMode.PercentOutput, power);
        SmartDashboard.putNumber("lift current ", liftMotor.getSupplyCurrent());
        SmartDashboard.putString("lift state ", liftState.toString());
        SmartDashboard.putNumber("lift input ", input);
        SmartDashboard.putNumber("lift power ", power);
        SmartDashboard.putNumber("lift position ", liftMotor.getSelectedSensorPosition());
        SmartDashboard.putNumber("lift target ", position);
    }

    private boolean nearLow(){
        return Math.abs(liftMotor.getSelectedSensorPosition() - lowTarget) < 500;
    }

    private boolean nearHigh(){
        return Math.abs(liftMotor.getSelectedSensorPosition() - highTarget) < 200;
    }

    private boolean stateChange(Boolean condition, LiftStates state){
        if(condition){
            liftState = state;
            return true;
        }
        return false;
    }

}