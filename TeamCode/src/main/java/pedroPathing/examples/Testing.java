package pedroPathing.examples;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import  com.qualcomm.robotcore.eventloop.opmode.OpMode;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import robot.config.Hardware;
import robot.state.IOController;
import robot.system.subsystem.Drive;
import robot.system.subsystem.Intake;
import robot.system.subsystem.Outtake;


@Autonomous(name = "Testing", group = "Real Auto")
public class Testing extends OpMode {
    //added by Alex, code from tele file
    private Hardware hardware;
    private IOController ioController;
    private Drive drive;
    private Intake intake;
    private Outtake outtake;
    //after this is the regular odo code initializing variable stuff (I don't actually know how this works)
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    private int pathState;

    private final Pose startPose = new Pose(8, 60, Math.toRadians(0));
    private final Pose placePreloadPose = new Pose(41, 70, Math.toRadians(0));
    private final Pose parkPose = new Pose(8, 8, Math.toRadians(0));

    private Path scorePreload, park;
    private PathChain placePreload;

    public void buildPaths() {
        scorePreload = new Path(new BezierLine(new Point(startPose), new Point(placePreloadPose)));
        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), placePreloadPose.getHeading());

        park = new Path(new BezierLine(new Point(placePreloadPose), new Point(parkPose)));
        park.setLinearHeadingInterpolation(placePreloadPose.getHeading(), parkPose.getHeading());
        //taking paths into a chain
        placePreload = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(placePreloadPose))) // path 1
                .setLinearHeadingInterpolation(startPose.getHeading(), placePreloadPose.getHeading())
                .addPath(new BezierLine(new Point(placePreloadPose), new Point(parkPose))) // path 2
                .setLinearHeadingInterpolation(placePreloadPose.getHeading(), parkPose.getHeading())
                .build();
    }


    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    public void autonomousPathUpdate() {
//        outtake.rotate(outtake.getRotatePosition());
//        outtake.setToTargetPosition(outtake.getLevel());
        switch (pathState) {
            case 0:
//                outtake.pincerClose();
                follower.followPath(scorePreload, true);
                setPathState(1);
                break;
//            case 1:
//                outtake.setLevel(Outtake.OuttakeLevel.HIGH_RUNG);
//                if(outtake.isAtTargetPosition()) {
//                    outtake.twistHorizontal();
//                    outtake.setRotatePosition(Outtake.OuttakeRotate.SUBMERSIBLE_RIGHT);
//                    outtake.linkageForward();
//                    if(outtake.isRotateSubmersiblePosition()) {
//                        outtake.pincerOpen();
//                        outtake.setLevel(Outtake.OuttakeLevel.WALL);
//                        outtake.linkageBackward();
//                    }
//                }
//                if(!follower.isBusy()) {
//                    setPathState(2);
//                }
//            case 1:
//
//                //here is new code
//                if(!follower.isBusy()) {
//                    follower.followPath(park, true);
//                }
//                break;
        }
    }

    @Override
    public void loop() {

        // These loop the movements of the robot
        follower.update();
        autonomousPathUpdate();

        // Feedback to Driver Hub
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }

    /** This method is called once at the init of the OpMode. **/
    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        hardware = new Hardware(hardwareMap);
        intake = new Intake(hardware, telemetry);
        outtake = new Outtake(hardware, telemetry);

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        buildPaths();
    }

    /** This method is called continuously after Init while waiting for "play". **/
    @Override
    public void init_loop() {}

    /** This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system **/
    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    /** We do not use this because everything should automatically disable **/
    @Override
    public void stop() {
    }
}