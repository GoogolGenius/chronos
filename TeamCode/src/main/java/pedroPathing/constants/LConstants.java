package pedroPathing.constants;

import com.pedropathing.localization.*;
import com.pedropathing.localization.constants.*;

public class LConstants {
    static {
        DriveEncoderConstants.forwardTicksToInches = 0.6177;
        DriveEncoderConstants.strafeTicksToInches = 4.4885;
        DriveEncoderConstants.turnTicksToInches = 0.0405;

        DriveEncoderConstants.robot_Width = 16;
        DriveEncoderConstants.robot_Length = 16;

        DriveEncoderConstants.leftFrontEncoderDirection = Encoder.FORWARD;
        DriveEncoderConstants.leftRearEncoderDirection = Encoder.REVERSE;
        DriveEncoderConstants.rightFrontEncoderDirection = Encoder.REVERSE;
        DriveEncoderConstants.rightRearEncoderDirection = Encoder.FORWARD;
    }
}
