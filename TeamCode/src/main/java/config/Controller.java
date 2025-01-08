package config;

import com.qualcomm.robotcore.hardware.Gamepad;

import system.subsystem.Outtake.OuttakeLevel;

public class Controller {
    public Gamepad currentGamepad2;
    public Gamepad previousGamepad2;
    public double outtakePower = 0;
    public OuttakeLevel outtakeLevel = OuttakeLevel.GROUND;

    public Controller() {
        currentGamepad2 = new Gamepad();
        previousGamepad2 = new Gamepad();
    }

    public void update() {
        try {
            previousGamepad2.copy(currentGamepad2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (currentGamepad2.dpad_up && !previousGamepad2.dpad_up) {
            switch (outtakeLevel) {
                case GROUND:
                    outtakeLevel = OuttakeLevel.LOW;
                    break;
                case LOW:
                    outtakeLevel = OuttakeLevel.HIGH;
                    break;
            }
        }

        else if (currentGamepad2.dpad_down && !previousGamepad2.dpad_down) {
            switch (outtakeLevel) {
                case HIGH:
                    outtakeLevel = OuttakeLevel.LOW;
                    break;
                case LOW:
                    outtakeLevel = OuttakeLevel.GROUND;
                    break;
            }

        }

        if (Math.abs(currentGamepad2.right_stick_y) > 0.1) {
            outtakePower = -0.75 * currentGamepad2.right_stick_y;
        } else {
            outtakePower = 0;
        }


    }
}
