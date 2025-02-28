package robot.system.subsystem;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import robot.config.Hardware;
import robot.config.PID;
import robot.system.System;

@Config
public class Outtake extends System {
    public enum OuttakeLevel {
        GROUND(-10),
        WALL(175), // pickup specimen from wall
        LOW_RUNG(500),
        LOW_BASKET(1000),
        HIGH_RUNG(750),
        HIGH_BASKET(1470); // 1470 is max extension

        private final int value;

        OuttakeLevel(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum OuttakeRotate {
        TRANSFER(0),
        SUBMERSIBLE(-1150), //-1300
        SUBMERSIBLE_RIGHT(-1050),
        WALL(2500);

        private final int value;

        OuttakeRotate(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static double target = 0;
    public static double Kp = 0.0035;
    public static double Ki = 0;
    public static double Kd = 0;
    private PID pid = new PID(Kp, Ki, Kd);
    private PID pidRotate = new PID(0.00075, 0, 0);
    private OuttakeLevel level = OuttakeLevel.GROUND;
    private OuttakeRotate rotatePosition = OuttakeRotate.TRANSFER;
    private boolean isRungPlacement = false;
    private boolean isPincerToggled = false;

    public Outtake(Hardware hardware, Telemetry telemetry) { super(hardware, telemetry); }

    public void setRungPlacement(boolean rungPlacement) { isRungPlacement = rungPlacement; }

    public boolean isRungPlacement() { return isRungPlacement; }

    public void setPincerToggled(boolean pincerToggled) { isPincerToggled = pincerToggled; }

    public boolean isPincerToggled() { return isPincerToggled; }

    public OuttakeLevel getLevel() {
        return level;
    }

    public void setLevel(OuttakeLevel level) { this.level = level; }

    public void setToTargetPosition(OuttakeLevel level) {
        this.level = level;
        int targetPosition = level.getValue();
        int currentPosition = hardware.getOuttakeCurrentPosition();
        double power = pid.out(targetPosition, currentPosition);

        if (level == OuttakeLevel.WALL) {
            power = new PID(0.0075, 0, 0).out(targetPosition, currentPosition);
        }

        hardware.outtakeL.setPower(power);
        hardware.outtakeR.setPower(power);
    }

    public boolean isAtTargetPosition() {
        int targetPosition = level.getValue();
        int currentPosition = hardware.getOuttakeCurrentPosition();

        return Math.abs(targetPosition - currentPosition) < 30;
    }

    public void pincerOpen() {
        double targetPosition = 0.4;
        hardware.outtakePincer.setPosition(targetPosition);
    }

    public void pincerClose() {
        double targetPosition = 0;
        hardware.outtakePincer.setPosition(targetPosition);
    }

    public void twistHorizontal() {
        double targetPosition = 0.325; //0.35;
        hardware.outtakeTwist.setPosition(targetPosition);
    }

    public void twistInverseHorizontal() {
        double targetPosition = 1.0; // 0.98; // servo box faces up at transfer
        hardware.outtakeTwist.setPosition(targetPosition);
    }

    public void twistVertical() {
        double targetPosition = 0.15;
        hardware.outtakeTwist.setPosition(targetPosition);
    }

    public OuttakeRotate getRotatePosition() { return rotatePosition; }

    public void setRotatePosition(OuttakeRotate position) { rotatePosition = position; }

    public void rotate(OuttakeRotate position) {
        rotatePosition = position;
        int targetPosition = position.getValue();
        int currentPosition = hardware.getOuttakeRotatePosition();
        double power = pidRotate.out(targetPosition, currentPosition);
//        hardware.outtakeRotateR.setPower(power); // broken servo
        hardware.outtakeRotateL.setPower(-power);
    }

    public void rotate(int position) {
        int targetPosition = position;
        int currentPosition = hardware.getOuttakeRotatePosition();
        double power = pidRotate.out(targetPosition, currentPosition);
        hardware.outtakeRotateL.setPower(power);
    }

    public boolean isRotated() {
        return Math.abs(hardware.getOuttakeRotatePosition() - rotatePosition.getValue()) < 50;
    }

    public boolean isRotateTransferPosition() {
        return hardware.getOuttakeRotatePosition() == OuttakeRotate.TRANSFER.getValue();
    }

    public boolean isRotateSubmersiblePosition() {
        return hardware.getOuttakeRotatePosition() == OuttakeRotate.SUBMERSIBLE.getValue();
    }

    public boolean isRotateWallPosition() {
        return hardware.getOuttakeRotatePosition() == OuttakeRotate.WALL.getValue();
    }

    public void linkageForward() {
        double targetPosition = 0.9;
        hardware.linkageL.setPosition(targetPosition);
//        hardware.linkageR.setPosition(targetPosition);
    }

    public void linkageTransfer() {
        double targetPosition = 0.675;
        hardware.linkageL.setPosition(targetPosition);
    }

    public void linkageBackward() {
        double targetPosition = 0.5;
//        hardware.linkageL.setPosition(targetPosition);
        hardware.linkageL.setPosition(targetPosition);
    }
}
