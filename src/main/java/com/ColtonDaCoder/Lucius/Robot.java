/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.ColtonDaCoder.Lucius;

import com.ColtonDaCoder.Lucius.subsystems.Lift;
import com.ColtonDaCoder.Lucius.subsystems.Turret;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandScheduler;


public class Robot extends TimedRobot {
  
  /**
   * NOTE TO FUTURE TESTING COLTON:
   *  --check turret to zero deadband       (line 75)
   * 
   *  --check soft limits for lift          (line 55)
   * 
   *  --check targets for lift              (line 33)
   * 
   *  --check target to position deadband   (line 120)
   * 
   *  --pid for lift and turret             (line 58 and 38)
   * 
   *  --check power for hold while manual   (line 95)
   */

  public static DriverStation dStation;
  private UsbCamera camera;

  private XboxController controller;

  private Lift s_Lift;
  private Turret s_Turret;


  public static enum LiftStates {
    control, hold
  };

  public static LiftStates liftState;

  @Override
  public void robotInit() {
    dStation = DriverStation.getInstance();

    camera = CameraServer.getInstance().startAutomaticCapture("Video", 0);
    camera.setResolution(320, 240);
     
    camera.setFPS(18);
    controller = new XboxController(0);
    s_Lift = new Lift(controller);
    s_Turret = new Turret(controller);

    liftState = LiftStates.control;
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }



  @Override
  public void teleopInit() {
  }

  @Override
  public void teleopPeriodic() {
  }

  public static double deadband(double input){
    return Math.abs(input) > 0.1 ? input : 0;
  }

}
