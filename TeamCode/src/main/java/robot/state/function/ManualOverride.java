package robot.state.function;

import com.qualcomm.robotcore.hardware.DcMotor;

import robot.state.GamepadToggle;
import robot.state.IOController;
import robot.state.StateFunction;

public class ManualOverride implements StateFunction {
    public static void run(IOController ioController) {
        if (Math.abs(ioController.getOpMode().gamepad2.right_stick_y) > 0.1) {
            ioController.getOuttake().rotate(0.75 * -ioController.getOpMode().gamepad2.right_stick_y);
        } else {
            ioController.getOuttake().rotate(0.0);
        }

        if (ioController.getCurrentGamepad2().right_stick_button
                && !ioController.getPreviousGamepad2().right_stick_button) {
            ioController.getOuttake().hardware.bR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            ioController.getOuttake().hardware.bR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        if (!ioController.getGamepadToggle().getToggleState(GamepadToggle.Button.RIGHT_BUMPER)) {
            ioController.setState(IOController.State.IDLE);
        }
    }
}
