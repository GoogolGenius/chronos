package system;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import config.Hardware;

public abstract class System {
    protected Hardware hardware;
    protected Telemetry telemetry;

    public System(Hardware hardware, Telemetry telemetry) {
        this.hardware = hardware;
        this.telemetry = telemetry;
    }

    public abstract void run();

    protected boolean gamepadButtonPressed(boolean current, boolean previous) {
        return current && !previous;
    }
}
