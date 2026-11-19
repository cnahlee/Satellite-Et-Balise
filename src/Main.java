import javax.swing.SwingUtilities;

import demo.ScenarioExemple;
import gui.SimulationWindow;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimulationWindow(ScenarioExemple.creerSimulation()).setVisible(true));
    }
}
