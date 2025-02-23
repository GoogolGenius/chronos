package pedroPathing.constants;


import com.pedropathing.localization.*;
import com.pedropathing.localization.constants.*;

public class LConstants {
    static {
        ThreeWheelConstants.forwardTicksToInches = .00065;
        ThreeWheelConstants.strafeTicksToInches = .0005;
        ThreeWheelConstants.turnTicksToInches = .0005;
        ThreeWheelConstants.leftY = 6.6;
        ThreeWheelConstants.rightY = -6.6;
        ThreeWheelConstants.strafeX = 3.3;
        ThreeWheelConstants.leftEncoder_HardwareMapName = "fR";
        ThreeWheelConstants.rightEncoder_HardwareMapName = "bL";
        ThreeWheelConstants.strafeEncoder_HardwareMapName = "fL";
        ThreeWheelConstants.leftEncoderDirection = Encoder.REVERSE;
        ThreeWheelConstants.rightEncoderDirection = Encoder.FORWARD;
        ThreeWheelConstants.strafeEncoderDirection = Encoder.FORWARD;
    }
}
