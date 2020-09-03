package com.ColtonDaCoder.Lucius.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.MotorCommutation;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
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
    
    private TalonFX liftMotor;
    private TalonFX liftSlave;

    private double input;
    private double power = 0;
    private double position = 0;
    private boolean hold = false;

    public TalonFXConfiguration liftMotorFXconfig;
    public TalonFXConfiguration liftSlaveFXconfig;


    public int highTarget = -200;
    public int lowTarget = -970830;

    public Lift(XboxController controller){
        this.controller = controller;
        liftMotor = new TalonFX(2);
        liftSlave = new TalonFX(3);
        liftMotor.configFactoryDefault();
        liftSlave.configFactoryDefault();
        liftMotor.setSensorPhase(false);

        liftMotor.setInverted(true);
        liftSlave.setInverted(false);

        liftSlave.follow(liftMotor);

        liftMotor.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);

        liftMotorFXconfig = new TalonFXConfiguration();
        liftSlaveFXconfig = new TalonFXConfiguration();

        SupplyCurrentLimitConfiguration liftSupplyLimit = new SupplyCurrentLimitConfiguration(
           true, 
            25, 
            30, 
            0.1);        
       
        liftMotorFXconfig.supplyCurrLimit = liftSupplyLimit;
        liftMotorFXconfig.forwardSoftLimitThreshold  = highTarget;
        liftMotorFXconfig.reverseSoftLimitThreshold = lowTarget;
        liftMotorFXconfig.slot0.kP = 1;
        liftMotorFXconfig.slot0.kI = 0;
        liftMotorFXconfig.slot0.kD = 0;
        liftMotor.configAllSettings(liftMotorFXconfig);
        liftMotor.setNeutralMode(NeutralMode.Brake);

        liftMotor.configReverseSoftLimitEnable(false);


        liftMotor.configForwardSoftLimitEnable(true);

        liftSlaveFXconfig.supplyCurrLimit = liftSupplyLimit;
        liftSlave.configAllSettings(liftSlaveFXconfig);
        liftSlave.setNeutralMode(NeutralMode.Brake);
        liftMotor.setSelectedSensorPosition(0);
    }
    @Override
    public void periodic(){  
        input = deadband(-controller.getRawAxis(5) * 0.25);
        //liftMotor.set(ControlMode.PercentOutput, input);
        switch(liftState){
            case control:
                //if(stateChange((input > 0), LiftStates.hold)) break;
                if(stateChange(controller.getRawButton(3), LiftStates.hold)) break;
                if(nearLow() && input < 0){
                    input = 0;
                }
                if(controller.getRawButton(1)){
                    input = -0.09;
                }
                power = -input;
            break;
            case hold:
                if(stateChange((Turret.getZero() && input < 0) || controller.getRawButton(3), LiftStates.control))  break;
                power = -0.07;
            break;
        }

        liftMotor.set(ControlMode.PercentOutput, power);
        SmartDashboard.putBoolean("lift hold ", hold);

        SmartDashboard.putNumber("lift current ", liftMotor.getSupplyCurrent());
        SmartDashboard.putString("lift state ", liftState.toString());
        SmartDashboard.putNumber("lift input ", input);
        SmartDashboard.putNumber("lift power ", power);
        SmartDashboard.putNumber("lift position ", liftMotor.getSelectedSensorPosition());
        SmartDashboard.putNumber("lift target ", position);
    }

    private boolean nearLow(){
        return Math.abs(liftMotor.getSelectedSensorPosition() - highTarget) < 9000;
    }

    private boolean nearHigh(){
        return Math.abs(liftMotor.getSelectedSensorPosition() - lowTarget) < 9000;
    }

    private boolean stateChange(Boolean condition, LiftStates state){
        if(condition){
            liftState = state;
            return true;
        }
        return false;
    }

}