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
    private Hardware hardware;
    private Drive drive;
    private Intake intake;
    private Outtake outtake;
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    private int pathState;

    private final Pose startPose = new Pose(8, 60, Math.toRadians(0));
    private final Pose placePreloadPose = new Pose(37, 69, Math.toRadians(0));
    private final Pose moveToOnePose = new Pose(60, 36, Math.toRadians(0));
    private final Pose moveToOneControl = new Pose(22, 36, Math.toRadians(0));
    private final Pose strafeToOnePose = new Pose(60, 28, Math.toRadians(0));
    private final Pose pushOnePose = new Pose(25, 28, Math.toRadians(0));
    private final Pose moveToTwoPose = new Pose(60, 20, Math.toRadians(0));
    private final Pose moveToTwoControl = new Pose(60, 34, Math.toRadians(0));
    private final Pose pushTwoPose = new Pose(11, 20, Math.toRadians(0));
    private final Pose moveToThreePose = new Pose(60, 15, Math.toRadians(0));
    private final Pose moveToThreeControl = new Pose(60, 25, Math.toRadians(0));
    private final Pose pushThreePose = new Pose(11, 15, Math.toRadians(0));
    private final Pose pickupPose = new Pose(11, 34, Math.toRadians(0));
    private final Pose placeOnePose = new Pose(37, 71, Math.toRadians(0));
    private final Pose placeOneControl = new Pose(14, 71, Math.toRadians(0));
    private final Pose placeTwoPose = new Pose(38, 70, Math.toRadians(0));
    private final Pose placeThreePose = new Pose(37, 72, Math.toRadians(0));
    private final Pose placeFourPose = new Pose(37, 68, Math.toRadians(0));
    private final Pose pickupControl = new Pose(26, 34, Math.toRadians(0));
    private final Pose parkPose = new Pose(12, 26, Math.toRadians(0));

    // PathChain for cases 0-6
    private PathChain pushSamplePathChain, pushTwo;

    // Remaining individual paths
    private Path placePreload, placeOne, pickupTwo, placeTwo, pickupThree, placeThree, pickupFour, placeFour, park;

    public void buildPaths() {

        placePreload = new Path(new BezierLine(new Point(startPose), new Point(placePreloadPose)));
        placePreload.setLinearHeadingInterpolation(startPose.getHeading(), placePreloadPose.getHeading());

        pushSamplePathChain = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(placePreloadPose), new Point(moveToOneControl), new Point(moveToOnePose)))
                .setLinearHeadingInterpolation(placePreloadPose.getHeading(), moveToOnePose.getHeading())

                // Second path: strafeToOne (case 1-2)
                .addPath(new BezierLine(new Point(moveToOnePose), new Point(strafeToOnePose)))
                .setLinearHeadingInterpolation(moveToOnePose.getHeading(), strafeToOnePose.getHeading())

                // Third path: pushOne (case 2-3)
                .addPath(new BezierLine(new Point(strafeToOnePose), new Point(pushOnePose)))
                .setLinearHeadingInterpolation(strafeToOnePose.getHeading(), pushOnePose.getHeading())

                // Fourth path: moveToTwo (case 3-4)
                .addPath(new BezierCurve(new Point(pushOnePose), new Point(moveToTwoControl), new Point(moveToTwoPose)))
                .setLinearHeadingInterpolation(pushOnePose.getHeading(), moveToTwoPose.getHeading())

//                // Fifth path: pushTwo (case 4-5)
//                .addPath(new BezierLine(new Point(moveToTwoPose), new Point(pushTwoPose)))
//                .setLinearHeadingInterpolation(moveToTwoPose.getHeading(), pushTwoPose.getHeading())

                // Sixth path: moveToThree (case 5-7)
                //.addPath(new BezierCurve(new Point(pushTwoPose), new Point(moveToThreeControl), new Point(moveToThreePose)))
                //.setLinearHeadingInterpolation(pushTwoPose.getHeading(), moveToThreePose.getHeading())

                // Set a reasonable timeout for the entire chain (adjust as needed)
                .setPathEndTimeoutConstraint(15.0)
                .build();


        // Define the remaining individual paths normally
        pushTwo = follower.pathBuilder()

                .addPath(new BezierLine(new Point(moveToTwoPose), new Point(pushTwoPose)))
                .setLinearHeadingInterpolation(moveToTwoPose.getHeading(), pushTwoPose.getHeading())

                .setPathEndTimeoutConstraint(15.0)
                .build();


        placeOne = new Path(new BezierCurve(new Point(pushThreePose), new Point(placeOneControl), new Point(placeOnePose)));
        placeOne.setLinearHeadingInterpolation(pushThreePose.getHeading(), placeOnePose.getHeading());

        pickupTwo = new Path(new BezierCurve(new Point(placeOnePose), new Point(pickupControl), new Point(pickupPose)));
        pickupTwo.setLinearHeadingInterpolation(placeOnePose.getHeading(), pickupPose.getHeading());

        placeTwo = new Path(new BezierLine(new Point(pickupPose), new Point(placeTwoPose)));
        placeTwo.setLinearHeadingInterpolation(pickupPose.getHeading(), placeTwoPose.getHeading());

        pickupThree = new Path(new BezierCurve(new Point(placeTwoPose), new Point(pickupControl), new Point(pickupPose)));
        pickupThree.setLinearHeadingInterpolation(placeTwoPose.getHeading(), pickupPose.getHeading());

        placeThree = new Path(new BezierLine(new Point(pickupPose), new Point(placeThreePose)));
        placeThree.setLinearHeadingInterpolation(pickupPose.getHeading(), placeThreePose.getHeading());

        pickupFour = new Path(new BezierCurve(new Point(placeThreePose), new Point(pickupControl), new Point(pickupPose)));
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
        intake.retract();
        boolean reachedDestination = !follower.isBusy();

        switch (pathState) {


            case 0:
                follower.followPath(placePreload, true);

                // preps for preload specimen placement
                outtake.setLevel(Outtake.OuttakeLevel.HIGH_RUNG);
                outtake.setRotatePosition(Outtake.OuttakeRotate.SUBMERSIBLE);
                outtake.linkageForward();
                outtake.twistHorizontal();

                outtake.pincerClose();

                setPathState(7);
                break;


            case 7:
                if(reachedDestination) {

                    // places preload
                    outtake.setLevel(Outtake.OuttakeLevel.GROUND);
                    outtake.setRotatePosition(Outtake.OuttakeRotate.WALL);
                    outtake.linkageBackward();
                    outtake.twistInverseHorizontal();

                    outtake.pincerOpen();

                    // pushes all of the samples
                    follower.followPath(pushSamplePathChain, false);
                    setPathState(8);
                }
                break;


            case 8:
                if(reachedDestination) {

                    // preps for first specimen pickup
                    outtake.setLevel(Outtake.OuttakeLevel.WALL);
                    outtake.setRotatePosition(Outtake.OuttakeRotate.WALL);
                    outtake.linkageBackward();
                    outtake.twistInverseHorizontal();

                    outtake.pincerOpen();

                    // pushes final block and moves to pick up first specimen
                    follower.followPath(pushTwo, 0.85, false);
                    setPathState(9);
                }
                break;


            case 9:
                if(reachedDestination) {
                    // picks up first specimen
                    outtake.pincerClose();

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // preps for first specimen placement
                    outtake.setLevel(Outtake.OuttakeLevel.HIGH_RUNG);
                    outtake.setRotatePosition(Outtake.OuttakeRotate.SUBMERSIBLE);
                    outtake.linkageForward();
                    outtake.twistHorizontal();

                    // moves to place first specimen
                    follower.followPath(placeOne, false);
                    setPathState(10);
                }
                break;


            case 10:
                if(reachedDestination) {

                    // places first specimen and preps for second specimen pickup
                    outtake.setLevel(Outtake.OuttakeLevel.WALL);
                    outtake.setRotatePosition(Outtake.OuttakeRotate.WALL);
                    outtake.linkageBackward();
                    outtake.twistInverseHorizontal();

                    outtake.pincerOpen();

                    // moves to pick up second specimen
                    follower.followPath(pickupTwo, false);
                    setPathState(11);
                }
                break;


            case 11:
                if(reachedDestination) {

                    // picks up second specimen
                    outtake.pincerClose();

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // preps for second specimen placement
                    outtake.setLevel(Outtake.OuttakeLevel.HIGH_RUNG);
                    outtake.setRotatePosition(Outtake.OuttakeRotate.SUBMERSIBLE);
                    outtake.linkageForward();
                    outtake.twistHorizontal();

                    // moves to place second specimen
                    follower.followPath(placeTwo, false);
                    setPathState(12);
                }
                break;


            case 12:
                if(reachedDestination) {

                    // places second specimen and preps for third specimen pickup
                    outtake.setLevel(Outtake.OuttakeLevel.WALL);
                    outtake.setRotatePosition(Outtake.OuttakeRotate.WALL);
                    outtake.linkageBackward();
                    outtake.twistInverseHorizontal();

                    outtake.pincerOpen();

                    // moves to pick up third specimen
                    follower.followPath(pickupThree, false);
                    setPathState(13);
                }
                break;


            case 13:
                if(reachedDestination) {

                    // picks up third specimen
                    outtake.pincerClose();

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // preps for third specimen placement
                    outtake.setLevel(Outtake.OuttakeLevel.HIGH_RUNG);
                    outtake.setRotatePosition(Outtake.OuttakeRotate.SUBMERSIBLE);
                    outtake.linkageForward();
                    outtake.twistHorizontal();

                    // moves to place third specimen
                    follower.followPath(placeThree, false);
                    setPathState(16);
                    /** skip placing the fourth non-preload specimen **/
                }
                break;
            case 14:
                if(reachedDestination) {

                    // places third specimen and preps for fourth specimen pickup
                    outtake.setLevel(Outtake.OuttakeLevel.WALL);
                    outtake.setRotatePosition(Outtake.OuttakeRotate.WALL);
                    outtake.linkageBackward();
                    outtake.twistInverseHorizontal();

                    outtake.pincerOpen();

                    // moves to pick up fourth specimen
                    follower.followPath(pickupFour, false);
                    setPathState(15);
                }
                break;


            case 15:
                if(reachedDestination) {

                    // picks up fourth specimen
                    outtake.pincerClose();

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // preps for fourth specimen placement
                    outtake.setLevel(Outtake.OuttakeLevel.HIGH_RUNG);
                    outtake.setRotatePosition(Outtake.OuttakeRotate.SUBMERSIBLE);
                    outtake.linkageForward();
                    outtake.twistHorizontal();

                    // moves to place fourth specimen
                    follower.followPath(placeFour, false);
                    setPathState(16);
                }
                break;


            case 16:
                if(reachedDestination) {

                    // places fourth specimen
                    outtake.setLevel(Outtake.OuttakeLevel.GROUND);
                    outtake.setRotatePosition(Outtake.OuttakeRotate.WALL);
                    outtake.linkageBackward();
                    outtake.twistInverseHorizontal();

                    outtake.pincerOpen();

                    // moves to park
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