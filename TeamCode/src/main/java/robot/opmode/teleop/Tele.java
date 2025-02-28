package robot.opmode.teleop;

import android.os.SystemClock;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
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
    public static double target0 = 0;
    public static double target1 = 0;

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

//        ioController.loop();

        hardware.intakeRotateL.setPosition(target0); // Go to Tele tab in FTC Dashboard and adjust the value.
        // Then go to Intake.java and change the target position in the corresponding methods.
        hardware.intakeTwist.setPosition(target1);  // Do the same thing here to change the twist for the horizontal and vertical.
        // When complete, UNCOMMENT `ioController.loop();` above these lines for the state machine to work
        // You can now delete `hardware.intakeRotateL.setPosition(target0);` and `hardware.intakeTwist.setPosition(target1);`


        telemetry.addData("Status", "Run Time: %s", runtime.toString());
        telemetry.addData("State", ioController.getState());
        telemetry.addData("outtake isRungPlacement", outtake.isRungPlacement());
        telemetry.addData("outtake rotate pos state", outtake.getRotatePosition());
        telemetry.addData("outtake Pincer pos", hardware.outtakePincer.getPosition());
        telemetry.addData("outtake twist pos", hardware.outtakeTwist.getPosition());
        telemetry.addData("Intake Position", hardware.intake.getCurrentPosition());
        telemetry.addData("Intake Power", hardware.intake.getPower());
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
