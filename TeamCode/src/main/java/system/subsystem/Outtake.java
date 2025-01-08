package system.subsystem;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import config.Hardware;
import config.PID;
import system.System;

public class Outtake extends System {
    public enum OuttakeLevel { GROUND(0), LOW(100), HIGH(200);
        private final int value;
        OuttakeLevel(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    private enum ManipulatorState { IDLE, WAITING, ROTATING, TWISTING }
    private final Gamepad currentGamepad2;
    private final Gamepad previousGamepad2;
    private final PID pid;
    private OuttakeLevel level = OuttakeLevel.GROUND;
    private ManipulatorState manipulatorState = ManipulatorState.IDLE;
    private boolean manipulatorToggle = false;
    private ElapsedTime manipulatorTimer = new ElapsedTime();
    private static final double PINCER_OPEN = 0.5;
    private static final double PINCER_CLOSE = 0.0;

    public Outtake(Hardware hardware, Telemetry telemetry, Gamepad currentGamepad2, Gamepad previousGamepad2) {
        super(hardware, telemetry);
        this.currentGamepad2 = currentGamepad2;
        this.previousGamepad2 = previousGamepad2;
        this.pid = new PID(0.005, 0.0, 0.0);
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Outtake does not have a run() method without parameters.");
    }

    public void run(boolean isIntakeRetracted) {
        updateLevel();
        controlExtension();
        if (isIntakeRetracted) {
            controlManipulator();
        }
    }

    private void updateLevel() {
        if (gamepadButtonPressed(currentGamepad2.dpad_up, previousGamepad2.dpad_up)) {
            if (level == OuttakeLevel.GROUND) {
                level = OuttakeLevel.LOW;
            } else if (level == OuttakeLevel.LOW) {
                level = OuttakeLevel.HIGH;
            } // If already at HIGH, it stays at HIGH.
        } else if (gamepadButtonPressed(currentGamepad2.dpad_down, previousGamepad2.dpad_down)) {
            if (level == OuttakeLevel.HIGH) {
                level = OuttakeLevel.LOW;
            } else if (level == OuttakeLevel.LOW) {
                level = OuttakeLevel.GROUND;
            } // If already at GROUND, it stays at GROUND.
        }
    }

    private void controlExtension() {
        int target = level.getValue();
        hardware.outtakeL.setPower(pid.out(target, hardware.outtakeL.getCurrentPosition()));
        hardware.outtakeR.setPower(pid.out(target, hardware.outtakeR.getCurrentPosition()));
    }

    private void controlManipulator() {
        switch (manipulatorState) {
            case IDLE:
                if (manipulatorToggle) {
                    hardware.outtakePincer.setPosition(PINCER_CLOSE);
                    manipulatorTimer.reset();
                    manipulatorState = ManipulatorState.WAITING;
                }
                break;

            case WAITING:
                if (manipulatorTimer.milliseconds() >= 2000) {
                    hardware.outtakeRotateR.setPosition(1);
                    hardware.outtakeRotateL.setPosition(1);
                    manipulatorState = ManipulatorState.TWISTING;
                }
                break;

            case TWISTING:
                hardware.outtakeTwist.setPosition(1);
                manipulatorState = ManipulatorState.IDLE;
                break;
        }
    }
}
