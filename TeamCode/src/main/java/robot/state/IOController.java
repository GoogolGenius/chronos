package robot.state;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import robot.state.function.HandoffInitializing;
import robot.state.function.HandoffReady;
import robot.state.function.Idle;
import robot.state.function.Pickup;
import robot.state.function.BucketPlacement;
import robot.state.function.RungPlacement;
import robot.system.subsystem.Intake;
import robot.system.subsystem.Outtake;

public class IOController {
    public enum State {
        IDLE,
        PICKUP,
        HANDOFF_INITIALIZING,
        HANDOFF_READY,
        BUCKET_PLACEMENT,
        RUNG_PLACEMENT,
    }

    private State state;
    private Intake intake;
    private Outtake outtake;
    private OpMode opMode;
    private GamepadToggle gamepadToggle;
    private Gamepad currentGamepad2 = new Gamepad();
    private Gamepad previousGamepad2 = new Gamepad();

    public IOController(Intake intake, Outtake outtake, OpMode opMode) {
        this.state = State.IDLE; // For testing right now
        this.intake = intake;
        this.outtake = outtake;
        this.opMode = opMode;
        this.gamepadToggle = new GamepadToggle(opMode.gamepad2);
    }

    public State getState() { return state; }

    public void setState(State state) { this.state = state; }

    public Intake getIntake() { return intake; }

    public Outtake getOuttake() { return outtake; }

    public Gamepad getCurrentGamepad2() { return currentGamepad2; }

    public Gamepad getPreviousGamepad2() { return previousGamepad2; }

    public GamepadToggle getGamepadToggle() { return gamepadToggle; }

    public OpMode getOpMode() { return opMode; }

    public boolean isGamepadButtonPressed(boolean current, boolean previous) {
        return current && !previous;
    }

    public void loop() {
        try {
            previousGamepad2.copy(currentGamepad2);
            currentGamepad2.copy(opMode.gamepad2);
        } catch (Exception ignored) {}

        // Update toggle states
        gamepadToggle.update();

//          // Example usage of toggle states
//        if (gamepadToggle.getToggleState(GamepadToggle.Button.A)) {
//            intake.extend();
//            intake.twistVertical();
//        } else {
//            intake.retract();
//            intake.twistHorizontal();
//        }

        switch (state) {
            case IDLE:
                Idle.run(this);
                break;
            case PICKUP:
                 Pickup.run(this);
                break;
            case HANDOFF_INITIALIZING:
                HandoffInitializing.run(this);
                break;
            case HANDOFF_READY:
                 HandoffReady.run(this);
                break;
            case BUCKET_PLACEMENT:
                 BucketPlacement.run(this);
                break;
            case RUNG_PLACEMENT:
                 RungPlacement.run(this);
                break;
        }
    }
}
