package robot.opmode.teleop;

import android.os.SystemClock;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import robot.config.Hardware;
import robot.state.GamepadToggle;
import robot.state.IOController;
import robot.system.subsystem.Drive;
import robot.system.subsystem.Intake;
import robot.system.subsystem.Outtake;

@Config
@TeleOp(name = "Tele", group = "Iterative OpMode")
public class Tele extends OpMode {
    public static double target = 0;
    private final ElapsedTime runtime = new ElapsedTime();
    private Hardware hardware;
    private IOController ioController;
    private Drive drive;
    private Intake intake;
    private Outtake outtake;

    @Override
    public void init() {
        hardware = new Hardware(hardwareMap);

        drive = new Drive(hardware, telemetry, gamepad1);

        intake = new Intake(hardware, telemetry);
        outtake = new Outtake(hardware, telemetry);
        ioController = new IOController(intake, outtake, this);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void start() {
        runtime.reset();
    }

    @Override
    public void loop() {
        drive.run();
//        try {
//            ioController.getPreviousGamepad2().copy(ioController.getCurrentGamepad2());
//            ioController.getCurrentGamepad2().copy(this.gamepad2);
//        } catch (Exception ignored) {}
//
//        Placement.run(ioController);

        ioController.loop();
//        hardware.outtakeRotateL.setPower(0.25);
//        hardware.outtakeRotateR.setPower(0.25);
//
//        double Kp = 0.001;
//        double Ki = 0;
//        double Kd = 0;
//        double integral = 0;
//        double previousError = 0;
//        ElapsedTime timer = new ElapsedTime();
//
//        int reference = 0;
//        int currentPosition = hardware.getOuttakeRotatePosition();
//
//        double error = reference - currentPosition;
//        double derivative = (error - previousError) / timer.seconds();
//        integral += error * timer.seconds();
//
//        double _o = (Kp * error) + (Ki * integral) + (Kd * derivative);
//        previousError = error;
//        timer.reset();
//
//        _o = Range.clip(_o, 0.5, 1);
//        double power = pidRotate.out(reference, currentPosition);
//        hardware.outtakeRotateR.setPower(-1);

//          outtake.rotate(0);

//          hardware.outtakeRotateR.setPower(1);
//
        hardware.linkageR.setPosition(target); // R backward 0.5
        hardware.linkageL.setPosition(target); // L forward 0.5
//        outtake.rotate(Outtake.OuttakeRotate.TRANSFER);

//        hardware.outtakePincer.setPosition(1);
//        hardware.outtakeTwist.setPosition(0.5);

//        outtake.linkageForward();
//        SystemClock.sleep(2000);
//        outtake.linkageBackward();

//        outtake.twistInverseHorizontal();
//        outtake.twistHorizontal();


        telemetry.addData("outtake isRungPlacement", outtake.isRungPlacement());
        telemetry.addData("outtake rotate pos state", outtake.getRotatePosition());
        telemetry.addData("outtake Pincer pos", hardware.outtakePincer.getPosition());
        telemetry.addData("outtake twist pos", hardware.outtakeTwist.getPosition());
        telemetry.addData("Status", "Run Time: %s", runtime.toString());
        telemetry.addData("State", ioController.getState());
        telemetry.addData("Intake Position", hardware.intake.getCurrentPosition());
        telemetry.addData("Intake Power", hardware.intake.getPower());
//        telemetry.addData("Intake RotateL power", hardware.intakeRotateL.getPower());
//        telemetry.addData("Intake RotateR power", hardware.intakeRotateR.getPower());
        telemetry.addData("Rotate Down", intake.isRotateDown());
        telemetry.addData("linkageR Pos", hardware.linkageR.getPosition());
        telemetry.addData("linkageL Pos", hardware.linkageL.getPosition());
        telemetry.addData("OuttakeRotatePos", hardware.getOuttakeRotatePosition());
        telemetry.addData("OuttakeRotateL Power", hardware.outtakeRotateL.getPower());
        telemetry.addData("OuttakeRotateR Power", hardware.outtakeRotateR.getPower());
        telemetry.addData("OuttakeL Pos", hardware.outtakeL.getCurrentPosition());
        telemetry.addData("Outtake Level", outtake.getLevel());
        telemetry.addData("Outtake Power L", hardware.outtakeL.getPower());
        telemetry.addData("Outtake Power R", hardware.outtakeR.getPower());
        telemetry.addData("Gamepad x toggle", ioController.getGamepadToggle().getToggleState(GamepadToggle.Button.X));
        telemetry.update();
    }
}
