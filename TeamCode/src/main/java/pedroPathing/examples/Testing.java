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
    private final Pose moveToOnePose = new Pose(64, 34, Math.toRadians(0));
    private final Pose moveToOneControl = new Pose(8, 34, Math.toRadians(0));
    private final Pose strafeToOnePose = new Pose(64, 26, Math.toRadians(0));
    private final Pose pushOnePose = new Pose(12, 26, Math.toRadians(0));
    private final Pose moveToTwoPose = new Pose(62, 20, Math.toRadians(0));
    private final Pose moveToTwoControl = new Pose(66, 34, Math.toRadians(0));
    private final Pose pushTwoPose = new Pose(12, 20, Math.toRadians(0));
    private final Pose moveToThreePose = new Pose(62, 18, Math.toRadians(0));
    private final Pose moveToThreeControl = new Pose(65, 25, Math.toRadians(0));
    private final Pose pushThreePose = new Pose(10, 20, Math.toRadians(0));
    private final Pose moveFromWallPose = new Pose(14, 16, Math.toRadians(0));
    private final Pose pickupPose = new Pose(10, 45, Math.toRadians(0));
    private final Pose placeOnePose = new Pose(37, 70, Math.toRadians(0));
    private final Pose placeOneControl = new Pose(14, 76, Math.toRadians(0));
    private final Pose placeTwoPose = new Pose(37, 68, Math.toRadians(0));
    private final Pose placeThreePose = new Pose(37, 66, Math.toRadians(0));
    private final Pose placeFourPose = new Pose(37, 64, Math.toRadians(0));
    private final Pose parkPose = new Pose(8, 26, Math.toRadians(0));

    private Path moveToOne, strafeToOne, pushOne, moveToTwo, pushTwo, moveToThree, angleForThree, pushThree, moveFromWall, placeOne, pickupTwo, placeTwo, pickupThree, placeThree, pickupFour, placeFour, park;

    public void buildPaths() {
        moveToOne = new Path(new BezierCurve(new Point(startPose), new Point(moveToOneControl), new Point(moveToOnePose)));
        moveToOne.setLinearHeadingInterpolation(startPose.getHeading(), moveToOnePose.getHeading());

        strafeToOne = new Path(new BezierLine(new Point(moveToOnePose), new Point(strafeToOnePose)));
        strafeToOne.setLinearHeadingInterpolation(moveToOnePose.getHeading(), strafeToOnePose.getHeading());

        pushOne = new Path(new BezierLine(new Point(strafeToOnePose), new Point(pushOnePose)));
        pushOne.setLinearHeadingInterpolation(strafeToOnePose.getHeading(), pushOnePose.getHeading());

        moveToTwo = new Path(new BezierCurve(new Point(pushOnePose), new Point(moveToTwoControl), new Point(moveToTwoPose)));
        moveToTwo.setLinearHeadingInterpolation(pushOnePose.getHeading(), moveToTwoPose.getHeading());

        pushTwo = new Path(new BezierLine(new Point(moveToTwoPose), new Point(pushTwoPose)));
        pushTwo.setLinearHeadingInterpolation(moveToTwoPose.getHeading(), pushTwoPose.getHeading());

        moveToThree = new Path(new BezierCurve(new Point(pushTwoPose), new Point(moveToThreeControl), new Point(moveToThreePose)));
        moveToThree.setLinearHeadingInterpolation(pushTwoPose.getHeading(), moveToThreePose.getHeading());

        pushThree = new Path(new BezierLine(new Point(moveToThreePose), new Point(pushThreePose)));
        pushThree.setLinearHeadingInterpolation(moveToThreePose.getHeading(), pushThreePose.getHeading());

        moveFromWall = new Path(new BezierLine(new Point(pushThreePose), new Point(moveFromWallPose)));
        moveFromWall.setLinearHeadingInterpolation(pushThreePose.getHeading(), moveFromWallPose.getHeading());

        placeOne = new Path(new BezierCurve(new Point(pushThreePose), new Point(placeOneControl), new Point(placeOnePose)));
        placeOne.setLinearHeadingInterpolation(pushThreePose.getHeading(), placeOnePose.getHeading());

        pickupTwo = new Path(new BezierLine(new Point(placeOnePose), new Point(pickupPose)));
        pickupTwo.setLinearHeadingInterpolation(placeOnePose.getHeading(), pickupPose.getHeading());

        placeTwo = new Path(new BezierLine(new Point(pickupPose), new Point(placeTwoPose)));
        placeTwo.setLinearHeadingInterpolation(pickupPose.getHeading(), placeTwoPose.getHeading());

        pickupThree = new Path(new BezierLine(new Point(placeTwoPose), new Point(pickupPose)));
        pickupThree.setLinearHeadingInterpolation(placeTwoPose.getHeading(), pickupPose.getHeading());

        placeThree = new Path(new BezierLine(new Point(pickupPose), new Point(placeThreePose)));
        placeThree.setLinearHeadingInterpolation(pickupPose.getHeading(), placeThreePose.getHeading());

        pickupFour = new Path(new BezierLine(new Point(placeThreePose), new Point(pickupPose)));
        pickupFour.setLinearHeadingInterpolation(placeThreePose.getHeading(), pickupPose.getHeading());

        placeFour = new Path(new BezierLine(new Point(pickupPose), new Point(placeFourPose)));
        placeFour.setLinearHeadingInterpolation(pickupPose.getHeading(), placeFourPose.getHeading());

        park = new Path(new BezierLine(new Point(placeFourPose), new Point(parkPose)));
        park.setLinearHeadingInterpolation(placeFourPose.getHeading(), parkPose.getHeading());

    }


    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(moveToOne, true);
                setPathState(1);
                break;
            case 1:
                if(!follower.isBusy()) {
                    follower.followPath(strafeToOne, false);
                    setPathState(2);
                    }
                break;
            case 2:
                if(!follower.isBusy()) {
                    follower.followPath(pushOne, false);
                    setPathState(3);
                }
                break;
            case 3:
                if(!follower.isBusy()) {
                    follower.followPath(moveToTwo, false);
                    setPathState(4);
                }
                break;
            case 4:
                if(!follower.isBusy()) {
                    follower.followPath(pushTwo, false);
                    setPathState(5);
                }
                break;
            case 5:
                if(!follower.isBusy()) {
                    follower.followPath(moveToThree, false);
                    setPathState(7);
                }
                break;
            case 7:
                if(!follower.isBusy()) {
                    follower.followPath(pushThree, false);
                    setPathState(9);
                }
                break;
//            case 8:
//                if(!follower.isBusy()) {
//                    follower.followPath(moveFromWall, false);
//                    setPathState(9);
//                }
//                break;
            case 9:
                if(!follower.isBusy()) {
                    follower.followPath(placeOne, false);
                    setPathState(10);
                }
                break;
            case 10:
                if(!follower.isBusy()) {
                    follower.followPath(pickupTwo, false);
                    setPathState(11);
                }
                break;
            case 11:
                if(!follower.isBusy()) {
                    follower.followPath(placeTwo, false);
                    setPathState(12);
                }
                break;
            case 12:
                if(!follower.isBusy()) {
                    follower.followPath(pickupThree, false);
                    setPathState(13);
                }
                break;
            case 13:
                if(!follower.isBusy()) {
                    follower.followPath(placeThree, false);
                    setPathState(14);
                }
                break;
            case 14:
                if(!follower.isBusy()) {
                    follower.followPath(pickupFour, false);
                    setPathState(15);
                }
                break;
            case 15:
                if(!follower.isBusy()) {
                    follower.followPath(placeFour, false);
                    setPathState(16);
                }
                break;
            case 16:
                if(!follower.isBusy()) {
                    follower.followPath(park, false);
                }
                break;
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