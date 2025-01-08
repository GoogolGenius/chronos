package system.subsystem;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import config.Hardware;
import config.PID;
import system.System;

public class Intake extends System {
    private enum IntakeState { RETRACTING, EXTENDING, IDLE }
    private final Gamepad currentGamepad2;
    private final Gamepad previousGamepad2;
    private final PID pid;
    private static final int RETRACT_POSITION = 0;
    private static final double PINCER_OPEN = 0.9;
    private static final double PINCER_CLOSE = 0.6;
    private IntakeState state = IntakeState.IDLE;
    private boolean pincerToggle = false;
    private boolean isRetracted = false;

    public Intake(Hardware hardware, Telemetry telemetry, Gamepad currentGamepad2, Gamepad previousGamepad2) {
        super(hardware, telemetry);

        this.currentGamepad2 = currentGamepad2;
        this.previousGamepad2 = previousGamepad2;
        this.pid = new PID(0.01, 0.0, 0.0);
    }

    public void run() {
        updateState();
        controlPincer();
        controlExtension();
        updateTelemetry();
    }

    private void updateState() {
        if (gamepadButtonPressed(currentGamepad2.right_bumper, previousGamepad2.right_bumper)) {
            state = IntakeState.RETRACTING;
        } else if (Math.abs(currentGamepad2.right_stick_y) > 0.1) {
            state = IntakeState.EXTENDING;
        } else {
            state = IntakeState.IDLE;
        }

        if (gamepadButtonPressed(currentGamepad2.x, previousGamepad2.x)) {
            pincerToggle = !pincerToggle;
        }
    }

    private void controlExtension() {
        int currentPos = hardware.intake.getCurrentPosition();
        double power;
        switch (state) {
            case RETRACTING:
                hardware.intakeTwist.setPosition(1);
                hardware.intakeRotateL.setPosition(0.5);
                power = pid.out(RETRACT_POSITION, currentPos);
                break;

            case EXTENDING:
                power = -currentGamepad2.right_stick_y;
                break;

            case IDLE:
            default:
                power = 0;
                break;
        }
        hardware.intake.setPower(power);

        if (state == IntakeState.RETRACTING && isFullyRetracted()) {
            isRetracted = true;
            state = IntakeState.IDLE;
        }
    }

    private void controlPincer() {
        double position = pincerToggle ? PINCER_CLOSE : PINCER_OPEN;
        hardware.intakePincer.setPosition(position);
    }

    private void updateTelemetry() {
        telemetry.addData("Intake Position", hardware.intake.getCurrentPosition());
        telemetry.addData("State", state);
        telemetry.addData("Pincer State", pincerToggle ? "Closed" : "Open");
        telemetry.addData("Is Retracted", isRetracted);
    }

    private boolean isFullyRetracted() {
        return Math.abs(hardware.intake.getCurrentPosition() - RETRACT_POSITION) < 1;
    }

    public boolean isRetracted() {
        return isRetracted;
    }
}
