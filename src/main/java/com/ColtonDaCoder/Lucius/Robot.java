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
  
  public static DriverStation dStation;
  private UsbCamera camera;

  private XboxController controller;

  private Lift s_Lift;
  private Turret s_Turret;


  public static enum LiftStates {
    lift, lower, hold, free
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

    liftState = LiftStates.free;
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
    return Math.abs(input) > 0.08 ? input : 0;
  }

}
