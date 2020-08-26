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
    private boolean automatic = true;
    private double power = 0;
    private double position = 0;

    public TalonSRXConfiguration liftMotorSRXconfig;
    public TalonSRXConfiguration liftSlaveSRXconfig;


    public double highTarget = 1000;
    public double lowTarget = 100;

    public Lift(XboxController controller){
        this.controller = controller;
        liftMotor = new TalonSRX(1);
        liftSlave = new TalonSRX(2);
        liftMotor.configFactoryDefault();
        liftSlave.configFactoryDefault();
        boolean phase = false;
        liftMotor.setSensorPhase(phase);
        liftSlave.setSensorPhase(phase);
        liftSlave.setInverted(true);
        liftSlave.follow(liftMotor);

        liftMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        liftMotorSRXconfig = new TalonSRXConfiguration();
        liftSlaveSRXconfig = new TalonSRXConfiguration();

        liftMotorSRXconfig.continuousCurrentLimit = 35;
        liftMotorSRXconfig.peakCurrentLimit = 60;
        liftMotorSRXconfig.peakCurrentDuration = 100;
        liftMotorSRXconfig.forwardSoftLimitThreshold  = 100;
        liftMotorSRXconfig.reverseSoftLimitThreshold = 0;
        liftMotorSRXconfig.slot0.kP = 0.01;
        liftMotorSRXconfig.slot0.kI = 0;
        liftMotorSRXconfig.slot0.kD = 0;
        liftMotor.configAllSettings(liftMotorSRXconfig);
        liftMotor.configForwardSoftLimitEnable(false);
        liftMotor.configReverseSoftLimitEnable(true);


        liftSlaveSRXconfig.continuousCurrentLimit = 35;
        liftSlaveSRXconfig.peakCurrentLimit = 60;
        liftSlaveSRXconfig.peakCurrentDuration = 100;
        liftSlave.configAllSettings(liftSlaveSRXconfig);
        liftMotor.setSelectedSensorPosition(0);
    }

    @Override
    public void periodic(){  
        input = deadband(-controller.getRawAxis(5)) * 0.5;
        switch(liftState){
            case lift:
                if(stateChange(input < 0 && !automatic, LiftStates.lower))  break;
                if(stateChange(controller.getRawButton(1), LiftStates.lower)) break;
                if(stateChange(nearHigh(), LiftStates.hold))  break;
                position = highTarget;
                power = input;
            break;
            case lower:
                if(stateChange(input > 0 && !automatic, LiftStates.lift))  break;
                if(stateChange(controller.getRawButton(4), LiftStates.lift))  break;
                if(stateChange(nearLow(), LiftStates.free)) break;
                position = lowTarget;
                power = input * 0.2;
            break;
            case hold:
                position = highTarget;
                if(stateChange((Turret.getZero() && input < 0 && !automatic), LiftStates.lower))  break;
                if(stateChange((Turret.getZero() && controller.getRawButton(1)), LiftStates.lower))  break;
            break;
            case free:
                power = 0;
                if(stateChange(input > 0 && !automatic, LiftStates.lift))  break;
                if(stateChange(controller.getRawButton(4), LiftStates.lift)) break;
            break;
        }
        if(controller.getRawButton(3)){
            automatic = !automatic;
        }
        if(!automatic || liftState == LiftStates.free){
            liftMotor.set(ControlMode.PercentOutput, power);
        }else{
            liftMotor.set(ControlMode.Position, position);  
        }
        SmartDashboard.putString("lift state ", liftState.toString());
        SmartDashboard.putNumber("lift input ", input);
        SmartDashboard.putNumber("lift power ", power);
        SmartDashboard.putNumber("lift position ", liftMotor.getSelectedSensorPosition());

    }


    private boolean nearLow(){
        return Math.abs(liftMotor.getSelectedSensorPosition() - lowTarget) < 100;
    }
    private boolean nearHigh(){
        return Math.abs(liftMotor.getSelectedSensorPosition() - highTarget) < 100;
    }

    private void manual(){

    }

    private boolean stateChange(Boolean condition, LiftStates state){
        if(condition){
            liftState = state;
            return true;
        }
        return false;
    }

}