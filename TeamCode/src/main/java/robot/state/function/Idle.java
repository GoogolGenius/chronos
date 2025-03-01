package robot.state.function;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import robot.state.GamepadToggle;
import robot.state.IOController;
import robot.state.StateFunction;
import robot.system.subsystem.Intake;
import robot.system.subsystem.Outtake;

public class Idle implements StateFunction {
    public static void run(IOController ioController) {
        Outtake outtake = ioController.getOuttake();
        Intake intake = ioController.getIntake();

        outtake.setToTargetPosition(outtake.getLevel());
        outtake.rotate(outtake.getRotatePosition());

        outtake.linkageBackward();
        outtake.twistHorizontal();
        outtake.pincerOpen();
        outtake.setLevel(Outtake.OuttakeLevel.WALL);

        if (outtake.getLevel() == Outtake.OuttakeLevel.WALL
                && outtake.isAtTargetPosition()) {
            outtake.setRotatePosition(Outtake.OuttakeRotate.WALL);
        }

        if (ioController.getCurrentGamepad2().dpad_up
                && !ioController.getPreviousGamepad2().dpad_up
                && (outtake.getLevel() == Outtake.OuttakeLevel.WALL)) {
            ioController.setState(IOController.State.RUNG_PLACEMENT);
        }

//        outtake.setLevel(Outtake.OuttakeLevel.GROUND);

        if (ioController.getGamepadToggle().getToggleState(GamepadToggle.Button.A)) {
            intake.extend();

            if (intake.isExtended()) {
//                ioController.setState(IOController.State.PICKUP);

            }
        } else {
            intake.retract();
        }

//        if (!ioController.getGamepadToggle().getToggleState(GamepadToggle.Button.A)) {
//            intake.retract();
//
////            if (intake.isRetracted()) {
////                ioController.setState(IOController.State.HANDOFF_INITIALIZING);
////            }
//
////            return;
//        }

        if (ioController.getGamepadToggle().getToggleState(GamepadToggle.Button.B)) {
            intake.twistHorizontal();
        } else {
            intake.twistVertical();
        }

        if (ioController.getGamepadToggle().getToggleState(GamepadToggle.Button.X)) {
            //intake.rotateDown();
            intake.pincerOpen();
        } else {
            //intake.rotateUp();
            intake.pincerClose();

//            int powerMilliSeconds = 500;
//            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//
//            // Schedule to run after x milliseconds
//            scheduler.schedule(
//                    () -> intake.rotateUp(),
//                    powerMilliSeconds,
//                    TimeUnit.MILLISECONDS
//            );
//
//            scheduler.shutdown();
        }

        if (ioController.getGamepadToggle().getToggleState(GamepadToggle.Button.DPAD_DOWN)) {
            intake.rotateDown();
        } else {
            intake.rotateUp();
        }

        // Manual override
        if (ioController.getGamepadToggle().getToggleState(GamepadToggle.Button.RIGHT_BUMPER)) {
            ioController.setState(IOController.State.MANUAL_OVERRIDE);
        }
    }
}
