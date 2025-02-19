package robot.opmode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Point;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import robot.config.Hardware;
import robot.system.subsystem.Intake;
import robot.system.subsystem.Outtake;

@Autonomous(name = "Auto Vision", group = "Iterative OpMode")
public class AutoVision extends OpMode {
    private OpenCvCamera camera;
    private SampleDetectionPipeline pipeline;
    private Hardware hardware;
    private Intake intake;
    private Outtake outtake;

    @Override
    public void init() {
        hardware = new Hardware(hardwareMap);

        intake = new Intake(hardware, telemetry);
        outtake = new Outtake(hardware, telemetry);

        pipeline = new SampleDetectionPipeline();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        camera.setPipeline(pipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                // Usually this is where you'll want to start streaming from the camera (see section 4)
                camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
            }
            @Override
            public void onError(int errorCode)
            {
                /*
                 * This will be called if the camera could not be opened
                 */
            }
        });

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        SampleDetectionPipeline.SpecimenColor color = pipeline.getDetectedColor();
        Point specimenPos = pipeline.getSpecimenPosition();

        if (color == SampleDetectionPipeline.SpecimenColor.RED) {
            telemetry.addData("Detected:", "Red Sample");
            moveToPosition(specimenPos);
        } else if (color == SampleDetectionPipeline.SpecimenColor.BLUE) {
            telemetry.addData("Detected:", "Blue Sample");
            moveToPosition(specimenPos);
        } else {
            telemetry.addData("Detected:", "None");
        }
        
        telemetry.update();
    }

    private void moveToPosition(Point position) {
        double targetX = position.x;
        double targetY = position.y;

        if (targetX < 320) { // viewport is divided in half, trying to move to the center
            // Move left
        } else {
            // Move right
        }

        if (targetY < 240) {
            // Move forward
        } else {
            // Move backward
        }
    }
}
