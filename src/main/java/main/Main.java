package main;

import controller.FlightController;
import model.FlightModel;
import ui.FlightDashboard;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlightModel model = new FlightModel();
            FlightDashboard view = new FlightDashboard("Controlador de Vuelo");
            FlightController controller = new FlightController(model, view);

            view.setVisible(true);

            // Obtener y mostrar vuelos recientes
            controller.fetchRecentFlights();
        });
    }
}
