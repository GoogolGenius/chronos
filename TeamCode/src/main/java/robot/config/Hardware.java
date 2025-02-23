package robot.config;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Arrays;
import java.util.List;

public class Hardware {
    public DcMotor fL;
    public DcMotor fR;
    public DcMotor bL;
    public DcMotor bR;
    public List<DcMotor> driveMotors;
    public DcMotor outtakeL;
    public DcMotor outtakeR;
    public List<DcMotor> outtakeMotors;
    public CRServo outtakeRotateL;
    public CRServo outtakeRotateR;
    public Servo outtakeTwist;
    public Servo outtakePincer;
    public Servo linkageL;
    public Servo linkageR;
    public DcMotor intake;
    public Servo intakeRotateL;
    public Servo intakeRotateR;
    public Servo intakeTwist;
    public Servo intakePincer;

    public Hardware(HardwareMap hardwareMap) {
        fR = hardwareMap.get(DcMotor.class, "fR");
        fL = hardwareMap.get(DcMotor.class, "fL");
        bR = hardwareMap.get(DcMotor.class, "bR");
        bL = hardwareMap.get(DcMotor.class, "bL");
        driveMotors = Arrays.asList(fR, fL, bR, bL);
        outtakeL = hardwareMap.get(DcMotor.class, "outtakeL");
        outtakeR = hardwareMap.get(DcMotor.class, "outtakeR");
        outtakeMotors = Arrays.asList(outtakeL, outtakeR);
        outtakeRotateL = hardwareMap.get(CRServo.class, "outtakeRotateL");
        outtakeRotateR = hardwareMap.get(CRServo.class, "outtakeRotateR");
        outtakeTwist = hardwareMap.get(Servo.class, "outtakeTwist");
        outtakePincer = hardwareMap.get(Servo.class, "outtakePincer");
        linkageL = hardwareMap.get(Servo.class, "linkageL");
        linkageR = hardwareMap.get(Servo.class, "linkageR");
        intake = hardwareMap.get(DcMotor.class, "intake");
        intakeRotateL = hardwareMap.get(Servo.class, "intakeRotateL");
        intakeRotateR = hardwareMap.get(Servo.class, "intakeRotateR");
        intakeTwist = hardwareMap.get(Servo.class, "intakeTwist");
        intakePincer = hardwareMap.get(Servo.class, "intakePincer");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        bL.setDirection(DcMotor.Direction.REVERSE);
        bR.setDirection(DcMotor.Direction.FORWARD);
        fL.setDirection(DcMotor.Direction.REVERSE);
        fR.setDirection(DcMotor.Direction.FORWARD);

        outtakeL.setDirection(DcMotor.Direction.REVERSE);
        outtakeR.setDirection(DcMotor.Direction.REVERSE);
        intake.setDirection(DcMotor.Direction.REVERSE);

     //   bR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        outtakeR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        outtakeL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outtakeR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outtakeL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public int getOuttakeRotatePosition() {
        // The through-bore encoder for the servos is attached to this motor port
        return -bR.getCurrentPosition();
    }

    public int getIntakeCurrentPosition() {
        return intake.getCurrentPosition(); // encoder reads opposite
    }

    public int getOuttakeCurrentPosition() {
        return outtakeL.getCurrentPosition();
    }

    public void moveForward(double power) {
        bL.setPower(power);
        bR.setPower(power);
        fL.setPower(power);
        fR.setPower(power);
    }

    public void moveBackward(double power) {
        bL.setPower(-power);
        bR.setPower(-power);
        fL.setPower(-power);
        fR.setPower(-power);
    }

    public void strafeLeft(double power) {
        bL.setPower(power);
        bR.setPower(-power);
        fL.setPower(power);
        fR.setPower(-power);
    }

    public void strafeRight(double power) {
        bL.setPower(-power);
        bR.setPower(power);
        fL.setPower(-power);
        fR.setPower(power);
    }

    public final void rotateLeft(double power) {
        bL.setPower(-power);
        bR.setPower(power);
        fL.setPower(-power);
        fR.setPower(power);
    }

    public final void rotateRight(double power) {
        fR.setPower(-power);
        bR.setPower(-power);
        fL.setPower(power);
        bL.setPower(power);
    }

    public final void stopDrive() {
        fR.setPower(0);
        bR.setPower(0);
        fL.setPower(0);
        bL.setPower(0);
    }
}
