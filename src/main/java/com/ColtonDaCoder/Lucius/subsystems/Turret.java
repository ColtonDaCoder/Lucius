package com.ColtonDaCoder.Lucius.subsystems;

import com.ColtonDaCoder.Lucius.Robot.LiftStates;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.MotorCommutation;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;

import static com.ColtonDaCoder.Lucius.Robot.*;

import edu.wpi.first.hal.AccumulatorResult;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {

  private XboxController controller;

  private static TalonFX turret;

  public static boolean zero = true;

  public TalonFXConfiguration turretFXConfig;

  public Turret(XboxController controller) {
    this.controller = controller;
    turret = new TalonFX(0);
    turretFXConfig = new TalonFXConfiguration();
    SupplyCurrentLimitConfiguration turretSupplyLimit = new SupplyCurrentLimitConfiguration(true, 25, 30, 0.1);
    turretFXConfig.slot0.kP = 0.001;
    turretFXConfig.slot0.kI = 0.0001;
    turretFXConfig.slot0.kD = 0;
    turretFXConfig.slot0.kF = 0;
    turretFXConfig.supplyCurrLimit = turretSupplyLimit;
    turretFXConfig.motorCommutation = MotorCommutation.Trapezoidal;
    turretFXConfig.initializationStrategy = SensorInitializationStrategy.BootToZero;
    turret.configAllSettings(turretFXConfig);
    turret.setSelectedSensorPosition(0);
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("turret position ", turret.getSelectedSensorPosition());
    double input = controller.getRawAxis(0);
    //-controller.getTriggerAxis(Hand.kLeft) + controller.getTriggerAxis(Hand.kRight)
    switch(liftState){
      case control:
        turret.set(ControlMode.PercentOutput, 0);
      break;
      case hold:
        //while button 2, target zero
        //otherwise just go based on controller
        if(controller.getRawButton(2) && !getZero()){
          SmartDashboard.putNumber("turret input ", controller.getButtonCount());
          turret.set(ControlMode.Position, 0);
        } else {
          turret.set(ControlMode.PercentOutput, input * 0.1);
        }
      break;
    }
  }

  /**
   * @return is turret zeroed
   */
  public static boolean getZero(){
    return Math.abs(turret.getSelectedSensorPosition()) < 500 ? true : false;
  }

}
