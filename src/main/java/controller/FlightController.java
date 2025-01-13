package controller;

import model.FlightModel;
import opensky.OpenSkyApi;
import opensky.OpenSkyStates;
import opensky.StateVector;
import ui.FlightDashboard;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class FlightController {
    private FlightModel model;
    private FlightDashboard view;
    private OpenSkyApi api;
    private boolean done;

    public FlightController(FlightModel model, FlightDashboard view) {
        done = false;
        this.model = model;
        this.view = view;
        this.api = new OpenSkyApi("Mugica", "zUCH39eY"); // Reemplaza con tus credenciales
    }

    public void fetchRecentFlights() {
        try {
            OpenSkyStates states = api.getStates(0, null, null); // Obtener todos los vuelos
            List<StateVector> flights = (List<StateVector>) states.getStates();
            if (flights != null && !flights.isEmpty()) {
                List<String> flightDescriptions = flights.stream()
                        .map(flight -> String.format("ICAO24: %s, Callsign: %s, País: %s",
                                flight.getIcao24(),
                                flight.getCallsign() != null ? flight.getCallsign().trim() : "N/A",
                                flight.getOriginCountry()))
                        .collect(Collectors.toList());

                String selectedIcao24 = showFlightSelectionDialog(flightDescriptions, flights);
                if (selectedIcao24 != null) {
                    startFlightMonitoring(selectedIcao24);
                } else {
                    JOptionPane.showMessageDialog(null, "No se seleccionó ningún vuelo.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se encontraron vuelos disponibles.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener los vuelos: " + e.getMessage());
        }
    }

    private String showFlightSelectionDialog(List<String> flightDescriptions, List<StateVector> flights) {
        JList<String> flightList = new JList<>(flightDescriptions.toArray(new String[0]));
        flightList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Selecciona un vuelo o ingresa un código ICAO24 manualmente:"), BorderLayout.NORTH);
        panel.add(new JScrollPane(flightList), BorderLayout.CENTER);

        JTextField manualInput = new JTextField();
        panel.add(manualInput, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(
                null, panel, "Vuelos Disponibles", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (!manualInput.getText().trim().isEmpty()) {
                return manualInput.getText().trim();
            } else if (!flightList.isSelectionEmpty()) {
                int selectedIndex = flightList.getSelectedIndex();
                return flights.get(selectedIndex).getIcao24();
            }
        }
        return null;
    }

    public void startFlightMonitoring(String icao24) {
        new Thread(() -> {
            while (true) {
                try {
                    OpenSkyStates states = api.getStates(0, new String[]{icao24}, null);
                    List<StateVector> stateVectors = (List<StateVector>) states.getStates();

                    if (stateVectors != null && !stateVectors.isEmpty()) {
                        StateVector state = stateVectors.get(0);
                        if (!done) {
                            String originCountry = state.getOriginCountry() != null ? state.getOriginCountry() : "N/A";
                            view.updateTitle(icao24 + " (" + originCountry + ")");
                            done = true;
                        }
                        double epochTime = state.getLastContact();
                        double altitude = state.getGeoAltitude() != null ? state.getGeoAltitude() : 0.0;
                        double velocity = state.getVelocity() != null ? state.getVelocity() : 0.0;
                        double verticalSpeed = state.getVerticalRate() != null ? state.getVerticalRate() : 0.0;
                        double latitude = state.getLatitude() != null ? state.getLatitude() : 0.0;
                        double longitude = state.getLongitude() != null ? state.getLongitude() : 0.0;

                        model.addData(altitude, velocity, verticalSpeed);
                        view.updateView(epochTime, altitude, velocity, verticalSpeed, latitude, longitude, model);

                        System.out.printf("Tiempo: %.1f, Altitud: %.2f m, Velocidad: %.2f m/s, Velocidad Vertical: %.2f m/s, Latitud: %.5f, Longitud: %.5f%n",
                                epochTime, altitude, velocity, verticalSpeed, latitude, longitude);
                    } else {
                        System.out.println("No se encontraron datos para el ICAO24: " + icao24);
                    }

                    Thread.sleep(10000);
                } catch (Exception e) {
                    System.err.println("Error al obtener datos: " + e.getMessage());
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }).start();
    }
}
