package robot.opmode.auto;

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
import com.qualcomm.robotcore.util.ElapsedTime;

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

    private final Pose startPose = new Pose(8, 44, Math.toRadians(0));
    private final Pose moveToOnePose = new Pose(40, 34, Math.toRadians(0));
    private final Pose moveToOneControl = new Pose(4, 34, Math.toRadians(0));
    private final Pose strafeToOnePose = new Pose(60, 26, Math.toRadians(0));
    private final Pose pushOnePose = new Pose(25, 26, Math.toRadians(0));
    private final Pose moveToTwoPose = new Pose(60, 16, Math.toRadians(0));
    private final Pose moveToTwoControl = new Pose(60, 34, Math.toRadians(0));
    private final Pose pushTwoPose = new Pose(25, 16, Math.toRadians(0));
    private final Pose moveToThreePose = new Pose(60, 12, Math.toRadians(0));
    private final Pose moveToThreeControl = new Pose(60, 25, Math.toRadians(0));
    private final Pose pushThreePose = new Pose(25, 12, Math.toRadians(0));
    private final Pose moveFromWallPose = new Pose(14, 16, Math.toRadians(0));
    private final Pose pickupPose = new Pose(12, 36, Math.toRadians(0));
    private final Pose placeOnePose = new Pose(37, 71, Math.toRadians(0));
    private final Pose placeOneControl = new Pose(14, 71, Math.toRadians(0));
    private final Pose placeTwoPose = new Pose(37, 70, Math.toRadians(0));
    private final Pose placeThreePose = new Pose(37, 69, Math.toRadians(0));
    private final Pose placeFourPose = new Pose(37, 68, Math.toRadians(0));
    private final Pose parkPose = new Pose(12, 26, Math.toRadians(0));

    // PathChain for cases 0-7
    private PathChain pushConePathChain, pushThree;

    // Remaining individual paths
    private Path placeOne, pickupTwo, placeTwo, pickupThree, placeThree, pickupFour, placeFour, park;

    public void buildPaths() {
        // Create a PathChain for cases 0-7 using pathBuilder
        pushConePathChain = follower.pathBuilder()
                // First path: moveToOne (case 0)
                .addPath(new BezierCurve(new Point(startPose), new Point(moveToOneControl), new Point(moveToOnePose)))
                .setLinearHeadingInterpolation(startPose.getHeading(), moveToOnePose.getHeading())

                // Second path: strafeToOne (case 1-2)
                .addPath(new BezierLine(new Point(moveToOnePose), new Point(strafeToOnePose)))
                .setLinearHeadingInterpolation(moveToOnePose.getHeading(), strafeToOnePose.getHeading())

                // Third path: pushOne (case 2-3)
                .addPath(new BezierLine(new Point(strafeToOnePose), new Point(pushOnePose)))
                .setLinearHeadingInterpolation(strafeToOnePose.getHeading(), pushOnePose.getHeading())

                // Fourth path: moveToTwo (case 3-4)
                .addPath(new BezierCurve(new Point(pushOnePose), new Point(moveToTwoControl), new Point(moveToTwoPose)))
                .setLinearHeadingInterpolation(pushOnePose.getHeading(), moveToTwoPose.getHeading())

                // Fifth path: pushTwo (case 4-5)
                .addPath(new BezierLine(new Point(moveToTwoPose), new Point(pushTwoPose)))
                .setLinearHeadingInterpolation(moveToTwoPose.getHeading(), pushTwoPose.getHeading())

                // Sixth path: moveToThree (case 5-7)
                .addPath(new BezierCurve(new Point(pushTwoPose), new Point(moveToThreeControl), new Point(moveToThreePose)))
                .setLinearHeadingInterpolation(pushTwoPose.getHeading(), moveToThreePose.getHeading())

                // Set a reasonable timeout for the entire chain (adjust as needed)
                .setPathEndTimeoutConstraint(15.0)
                .build();


        // Define the remaining individual paths normally
        pushThree = follower.pathBuilder()
                .addPath(new BezierLine(new Point(moveToThreePose), new Point(pushThreePose)))
                .setLinearHeadingInterpolation(moveToThreePose.getHeading(), pushThreePose.getHeading())

                .setPathEndTimeoutConstraint(15.0)
                .build();


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
        outtake.rotate(outtake.getRotatePosition());
        outtake.setToTargetPosition(outtake.getLevel());
        outtake.twistHorizontal();
        outtake.pincerOpen();
        switch (pathState) {
            case 0:
                // Follow the entire pushConePathChain as one sequence
                follower.followPath(pushConePathChain, true);
                setPathState(9); // Skip to case 9 after pushConePathChain is complete
                break;
            case 8:
                if(!follower.isBusy()) {
                    follower.followPath(pushThree, 0.5, false);
                    setPathState(10);
                }
            case 9:
                outtake.pincerClose();
                outtake.twistInverseHorizontal();
                outtake.setRotatePosition(Outtake.OuttakeRotate.WALL);
                outtake.setLevel(Outtake.OuttakeLevel.GROUND);
                outtake.linkageBackward();
                if(!follower.isBusy()) {
                    follower.followPath(placeOne, false);
                    setPathState(10);
                }
                break;
            case 10:
                outtake.pincerOpen();
                outtake.twistHorizontal();
                outtake.setRotatePosition(Outtake.OuttakeRotate.SUBMERSIBLE_RIGHT);
                outtake.setLevel(Outtake.OuttakeLevel.HIGH_RUNG);
                outtake.linkageForward();
                if(!follower.isBusy()) {
                    follower.followPath(pickupTwo, false);
                    setPathState(11);
                }
                break;
            case 11:
                outtake.pincerClose();
                outtake.twistInverseHorizontal();
                outtake.setRotatePosition(Outtake.OuttakeRotate.WALL);
                outtake.setLevel(Outtake.OuttakeLevel.WALL);
                outtake.linkageBackward();
                if(!follower.isBusy()) {
                    follower.followPath(placeTwo, false);
                    setPathState(12);
                }
                break;
            case 12:
                outtake.pincerOpen();
                outtake.twistHorizontal();
                outtake.setRotatePosition(Outtake.OuttakeRotate.SUBMERSIBLE_RIGHT);
                outtake.setLevel(Outtake.OuttakeLevel.HIGH_RUNG);
                outtake.linkageForward();
                if(!follower.isBusy()) {
                    follower.followPath(pickupThree, false);
                    setPathState(13);
                }
                break;
            case 13:
                outtake.pincerClose();
                outtake.twistInverseHorizontal();
                outtake.setRotatePosition(Outtake.OuttakeRotate.WALL);
                outtake.setLevel(Outtake.OuttakeLevel.WALL);
                outtake.linkageBackward();
                if(!follower.isBusy()) {
                    follower.followPath(placeThree, false);
                    setPathState(14);
                }
                break;
            case 14:
                outtake.pincerOpen();
                outtake.twistHorizontal();
                outtake.setRotatePosition(Outtake.OuttakeRotate.SUBMERSIBLE_RIGHT);
                outtake.setLevel(Outtake.OuttakeLevel.HIGH_RUNG);
                outtake.linkageForward();
                if(!follower.isBusy()) {
                    follower.followPath(pickupFour, false);
                    setPathState(15);
                }
                break;
            case 15:
                outtake.pincerClose();
                outtake.twistInverseHorizontal();
                outtake.setRotatePosition(Outtake.OuttakeRotate.WALL);
                outtake.setLevel(Outtake.OuttakeLevel.WALL);
                outtake.linkageBackward();
                if(!follower.isBusy()) {
                    follower.followPath(placeFour, false);
                    setPathState(16);
                }
                break;
            case 16:
                outtake.pincerOpen();
                outtake.twistHorizontal();
                outtake.setRotatePosition(Outtake.OuttakeRotate.SUBMERSIBLE_RIGHT);
                outtake.setLevel(Outtake.OuttakeLevel.HIGH_RUNG);
                outtake.linkageForward();
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
        intake.retract();
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