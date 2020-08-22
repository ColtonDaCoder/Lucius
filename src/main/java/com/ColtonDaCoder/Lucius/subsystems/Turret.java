package com.ColtonDaCoder.Lucius.subsystems;

import com.ColtonDaCoder.Lucius.Robot.LiftStates;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import static com.ColtonDaCoder.Lucius.Robot.*;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {
 
  private XboxController controller;

  private TalonFX turret;

  public static boolean zero = true;

  public Turret(XboxController controller) {
    this.controller = controller;
    turret = new TalonFX(0);
  }

  @Override
  public void periodic() {
    turret.set(ControlMode.PercentOutput, -controller.getRawAxis(0) * 0.1);
    /*

    //checks if turret at zero
    if(Math.abs(turret.getSelectedSensorPosition()) < 10){
      zero = true;
    }

    switch(liftState){
      case hold:
        //while button 3, target zero
        //otherwise just go based on controller
        if(controller.getRawButton(3)){
          turret.set(ControlMode.Position, 0);
        } else {
          turret.set(ControlMode.PercentOutput, controller.getRawAxis(0));
        }
      break;
      default:
        turret.set(ControlMode.PercentOutput, 0);
      break;
    }
    
    */
  }

  /**
   * @return is turret zeroed
   */
  public static boolean getZero(){
    return zero;
  }

}
