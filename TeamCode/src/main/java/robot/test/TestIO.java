package robot.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import robot.config.Hardware;

@TeleOp(name = "Test I/O", group = "Testing")
public class TestIO extends OpMode {
    Hardware hardware;
    private int testIndex = 0;
    private final String[] deviceNames = {
            "Outtake Motors",
            "Outtake Rotate CRServos",
            "Outtake Twist Servo",
            "Outtake Pincer Servo",
            "Linkage Servos",
            "Intake Motor",
            "Intake Rotate CRServos",
            "Intake Twist Servo",
            "Intake Pincer Servo"
    };

    @Override
    public void init() {
        hardware = new Hardware(hardwareMap);
        telemetry.addLine("Initialization Complete");
        telemetry.addLine("Press Dpad Up/Down to switch device");
        telemetry.addLine("Use Gamepad buttons/joystick to test.");
    }

    @Override
    public void loop() {
        telemetry.addLine("Testing: " + deviceNames[testIndex]);

        switch (testIndex) {
            case 0: // Outtake motors
                double outtakePower = gamepad1.right_stick_y;
                hardware.outtakeL.setPower(outtakePower);
                hardware.outtakeR.setPower(outtakePower);
                telemetry.addData("OuttakeL Encoder", hardware.outtakeL.getCurrentPosition());
                telemetry.addData("OuttakeR Encoder", hardware.outtakeR.getCurrentPosition());
                break;

            case 1: // Outtake Rotate CRServos
                double rotatePower = gamepad1.right_trigger - gamepad1.left_trigger;
                hardware.outtakeRotateL.setPower(rotatePower);
                hardware.outtakeRotateR.setPower(-rotatePower);
                telemetry.addData("Outtake Rotate Encoder", hardware.getOuttakeRotatePosition());
                break;

            case 2: // Outtake Twist Servo
                if (gamepad1.a) hardware.outtakeTwist.setPosition(1.0);
                if (gamepad1.b) hardware.outtakeTwist.setPosition(0.0);
                telemetry.addData("Outtake Twist Position", hardware.outtakeTwist.getPosition());
                break;

            case 3: // Outtake Pincer Servo
                if (gamepad1.x) hardware.outtakePincer.setPosition(1.0);
                if (gamepad1.y) hardware.outtakePincer.setPosition(0.0);
                telemetry.addData("Outtake Pincer Position", hardware.outtakePincer.getPosition());
                break;

            case 4: // Linkage Servos
                if (gamepad1.a) hardware.linkageL.setPosition(1.0);
                if (gamepad1.b) hardware.linkageL.setPosition(0.0);
                if (gamepad1.x) hardware.linkageR.setPosition(1.0);
                if (gamepad1.y) hardware.linkageR.setPosition(0.0);
                telemetry.addData("LinkageL Position", hardware.linkageL.getPosition());
                telemetry.addData("LinkageR Position", hardware.linkageR.getPosition());
                break;

            case 5: // Intake Motor
                double intakePower = gamepad1.right_stick_y;
                hardware.intake.setPower(intakePower);
                telemetry.addData("Intake Motor Encoder", hardware.intake.getCurrentPosition());
                break;

            case 6: // Intake Rotate CRServos
                double intakeRotatePower = gamepad1.right_trigger - gamepad1.left_trigger;
//                hardware.intakeRotateL.setPower(intakeRotatePower);
//                hardware.intakeRotateR.setPower(-intakeRotatePower);
                break;

            case 7: // Intake Twist Servo
                if (gamepad1.a) hardware.intakeTwist.setPosition(1.0);
                if (gamepad1.b) hardware.intakeTwist.setPosition(0.0);
                telemetry.addData("Intake Twist Position", hardware.intakeTwist.getPosition());
                break;

            case 8: // Intake Pincer Servo
                if (gamepad1.x) hardware.intakePincer.setPosition(1.0);
                if (gamepad1.y) hardware.intakePincer.setPosition(0.0);
                telemetry.addData("Intake Pincer Position", hardware.intakePincer.getPosition());
                break;
        }

        // Nav through devices
        if (gamepad1.dpad_up) {
            testIndex = (testIndex + 1) % deviceNames.length;
            sleep(300); // Debounce
        } else if (gamepad1.dpad_down) {
            testIndex = (testIndex - 1 + deviceNames.length) % deviceNames.length;
            sleep(300);
        }

        telemetry.update();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
