package gui;

import javax.swing.Timer;
import data.Simulation;

/** One Swing timer tick advances the actual model by one simulation step. */
public final class SimulationAnimation {
    private final Simulation simulation;
    private final Runnable refresh;
    private final Timer timer;

    public SimulationAnimation(Simulation simulation, Runnable refresh) {
        this.simulation = simulation;
        this.refresh = refresh;
        timer = new Timer(1000, event -> step());
    }

    public void start() { timer.start(); }
    public void pause() { timer.stop(); }
    public boolean isRunning() { return timer.isRunning(); }

    public void step() {
        simulation.avancer();
        refresh.run();
    }

    /** Number of model steps per real second; each model step still represents one second. */
    public void setSpeed(int stepsPerSecond) {
        if (stepsPerSecond < 1 || stepsPerSecond > 10) {
            throw new IllegalArgumentException("Speed must be between 1 and 10");
        }
        timer.setDelay(1000 / stepsPerSecond);
        timer.setInitialDelay(timer.getDelay());
    }
}
