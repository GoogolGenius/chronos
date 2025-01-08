package opmode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import config.Hardware;
import system.subsystem.Drive;
import system.subsystem.Intake;
import system.subsystem.Outtake;

@TeleOp(name = "Iterative TeleOp", group = "Iterative OpMode")
public class IterativeTeleOp extends OpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    private Hardware hardware;
    private Drive drive;
    private Intake intake;
    private Outtake outtake;
    private final Gamepad currentGamepad2 = new Gamepad();
    private final Gamepad previousGamepad2 = new Gamepad();

    @Override
    public void init() {
        hardware = new Hardware(hardwareMap);

        drive = new Drive(hardware, telemetry, gamepad1);
        intake = new Intake(hardware, telemetry, currentGamepad2, previousGamepad2);
        outtake = new Outtake(hardware, telemetry, currentGamepad2, previousGamepad2);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void start() {
        runtime.reset();
    }

    @Override
    public void loop() {
        copyGamepadState();

        drive.run();
        intake.run();
        outtake.run(intake.isRetracted());

        telemetry.addData("Status", "Run Time: %s", runtime.toString());
        telemetry.update();
    }

    private void copyGamepadState() {
        try {
            previousGamepad2.copy(currentGamepad2);
            currentGamepad2.copy(gamepad2);
        } catch (Exception e) {
            telemetry.addData("Error", "Gamepad copy failed: %s", e.getMessage());
            telemetry.update();
        }
    }

    @Override
    public void stop() {
        // Perform any cleanup if needed
    }
}
