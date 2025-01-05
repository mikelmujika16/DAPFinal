package org.example;

import org.opensky.api.OpenSkyApi;
import org.opensky.model.OpenSkyStates;
import org.opensky.model.StateVector;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class OpenSkyAPIExample {

    public static void main(String[] args) throws IOException {

        // Instancia de OpenSkyApi (sin credenciales para datos públicos)
        OpenSkyApi api = new OpenSkyApi("Mugica", "zUCH39eY");

        // Primera llamada a la API para obtener los vuelos disponibles
        AtomicReference<OpenSkyStates> os = new AtomicReference<>(api.getStates(0, null, null));

        // Mostrar los vuelos disponibles
        System.out.println("Available flights:");
        for (StateVector s : os.get().getStates()) {
            System.out.println("ICAO24: " + s.getIcao24() + ", Origin Country: " + s.getOriginCountry());
        }

        // Pedir al usuario que escoja un vuelo por su ICAO24
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the ICAO24 of the flight you want to track: ");
        String selectedIcao24 = scanner.nextLine();

        // Crear y mostrar el panel de control de gráficos
        SwingUtilities.invokeLater(() -> {
            FlightDashboard dashboard = new FlightDashboard("Flight Dashboard");
            dashboard.setVisible(true);

            // Bucle para obtener y mostrar los datos del vuelo seleccionado cada 5 segundos
            new Thread(() -> {
                while (true) {
                    try {
                        os.set(api.getStates(0, new String[]{selectedIcao24}, null));
                        List<Double[]> flightData = new ArrayList<>();
                        for (StateVector s : os.get().getStates()) {
                            flightData.add(new Double[]{
                                    s.getLongitude(), s.getLatitude(), s.getVelocity(),
                                    s.getVerticalRate(), s.getHeading()
                            });
                        }
                        dashboard.updateData(flightData);
                        Thread.sleep(5000);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        });
    }
}