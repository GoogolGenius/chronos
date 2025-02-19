package robot.opmode.auto;

import org.opencv.core.MatOfPoint;
import org.openftc.easyopencv.OpenCvPipeline;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Rect;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class SampleDetectionPipeline extends OpenCvPipeline {
    private Mat hsvMat = new Mat();
    private Mat mask = new Mat();

    public enum SpecimenColor {
        RED, BLUE, NONE
    }

    private SpecimenColor detectedColor = SpecimenColor.NONE;
    private Point specimenPosition = new Point();

    @Override
    public Mat processFrame(Mat input) {
        // Convert to hsv
        Imgproc.cvtColor(input, hsvMat, Imgproc.COLOR_RGB2HSV);

        Scalar lowerRed1 = new Scalar(0, 100, 100);
        Scalar upperRed1 = new Scalar(10, 255, 255);
        Scalar lowerRed2 = new Scalar(170, 100, 100);
        Scalar upperRed2 = new Scalar(180, 255, 255);
        Scalar lowerBlue = new Scalar(100, 150, 100);
        Scalar upperBlue = new Scalar(130, 255, 255);

        // thresholding
        Mat redMask1 = new Mat();
        Mat redMask2 = new Mat();
        Mat blueMask = new Mat();
        Core.inRange(hsvMat, lowerRed1, upperRed1, redMask1);
        Core.inRange(hsvMat, lowerRed2, upperRed2, redMask2);
        Core.inRange(hsvMat, lowerBlue, upperBlue, blueMask);

        // Combine red masks (in hsv, red is a separated band, not continuous)
        Core.addWeighted(redMask1, 1.0, redMask2, 1.0, 0.0, mask);

        // Find contours
        Rect redRect = findLargestContour(mask);
        Rect blueRect = findLargestContour(blueMask);

        // positioning
        if (redRect != null) {
            detectedColor = SpecimenColor.RED;
            specimenPosition = new Point(redRect.x + redRect.width / 2, redRect.y + redRect.height / 2);
        } else if (blueRect != null) {
            detectedColor = SpecimenColor.BLUE;
            specimenPosition = new Point(blueRect.x + blueRect.width / 2, blueRect.y + blueRect.height / 2);
        } else {
            detectedColor = SpecimenColor.NONE;
        }

        // draw bounding-boxes
        if (redRect != null) Imgproc.rectangle(input, redRect, new Scalar(255, 0, 0), 2);
        if (blueRect != null) Imgproc.rectangle(input, blueRect, new Scalar(0, 0, 255), 2);

        return input;
    }

    private Rect findLargestContour(Mat mask) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Rect largestRect = null;
        double maxArea = 0;

        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            double area = rect.width * rect.height;
            if (area > maxArea) {
                maxArea = area;
                largestRect = rect;
            }
        }
        return largestRect;
    }

    public SpecimenColor getDetectedColor() {
        return detectedColor;
    }

    public Point getSpecimenPosition() {
        return specimenPosition;
    }
}
