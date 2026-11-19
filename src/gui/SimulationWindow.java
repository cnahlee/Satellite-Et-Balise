package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.Simulation;

/** The single application window, using the same Simulation as its panel and timer. */
public final class SimulationWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private final Simulation simulation;
    private final SimulationPanel scene;
    private final SimulationAnimation animation;
    private final JButton startButton = new JButton("Start");
    private final JButton pauseButton = new JButton("Pause");
    private final JButton stepButton = new JButton("Next step");
    private final JLabel simulationTime = new JLabel();

    public SimulationWindow(Simulation simulation) {
        super("Satellites et balises");
        this.simulation = simulation;
        scene = new SimulationPanel(simulation);
        animation = new SimulationAnimation(simulation, this::refresh);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel heading = new JLabel("Satellites et balises | Schematic orbits | Positions in metres");
        heading.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        add(heading, BorderLayout.NORTH);
        add(scene, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        controls.add(startButton);
        controls.add(pauseButton);
        controls.add(stepButton);
        controls.add(new JLabel("Speed"));
        JComboBox<Integer> speed = new JComboBox<>(new Integer[] {1, 2, 5, 10});
        speed.addActionListener(event -> animation.setSpeed((Integer) speed.getSelectedItem()));
        controls.add(speed);
        controls.add(simulationTime);
        add(controls, BorderLayout.SOUTH);

        startButton.addActionListener(event -> {
            animation.start();
            refresh();
        });
        pauseButton.addActionListener(event -> {
            animation.pause();
            refresh();
        });
        stepButton.addActionListener(event -> animation.step());
        refresh();
        setMinimumSize(new Dimension(960, 720));
        pack();
        setLocationRelativeTo(null);
    }

    private void refresh() {
        scene.refresh();
        boolean running = animation.isRunning();
        startButton.setEnabled(!running);
        pauseButton.setEnabled(running);
        stepButton.setEnabled(!running);
        simulationTime.setText((running ? "Running" : "Paused")
                + " | Step " + simulation.getNumeroPas() + " | t = " + simulation.getNumeroPas() + " s");
    }

    @Override
    public void dispose() {
        animation.pause();
        super.dispose();
    }
}
