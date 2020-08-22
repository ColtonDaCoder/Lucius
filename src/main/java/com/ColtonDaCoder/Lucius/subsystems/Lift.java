package com.ColtonDaCoder.Lucius.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ColtonDaCoder.Lucius.Robot.LiftStates;
import static com.ColtonDaCoder.Lucius.Robot.*;

import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class  Lift extends SubsystemBase{
    
    private XboxController controller;
    
    private TalonSRX liftMotor;
    private TalonSRX liftSlave;

    private PWM highLimit;
    private PWM lowLimit; 

    private double input;
    private double power = 0;

    public Lift(XboxController controller){
        this.controller = controller;
        liftMotor = new TalonSRX(1);
        liftSlave = new TalonSRX(2);
        liftSlave.follow(liftMotor);

        highLimit = new PWM(0);
        lowLimit = new PWM(1);
    }


    @Override
    public void periodic(){
        liftSlave.set(ControlMode.PercentOutput, controller.getRawAxis(1));
        liftMotor.set(ControlMode.PercentOutput, controller.getRawAxis(1));

        /*

        boolean highBool = true;
        boolean lowBool = true;
        input = deadband(controller.getRawAxis(5));
        switch(liftState){
            case lift:
                liftState = highBool ? LiftStates.hold : LiftStates.lift;
                power = input;
            break;
            case lower:
                liftState = lowBool ? LiftStates.free : LiftStates.lower;
                power = input;
            break;
            case hold:
                power = 0.05;
                liftState = (Turret.getZero() && input < 0) ? LiftStates.lower : LiftStates.hold;
            break;
            case free:
                power = 0;
                liftState = input > 0 ? LiftStates.lift : LiftStates.free;
            break;
        }
        liftMotor.set(ControlMode.PercentOutput, power);

        */
    }

}