package main;

import controller.FlightController;
import model.FlightModel;
import ui.FlightDashboard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MainScreen extends JFrame {
    public MainScreen() {
        setTitle("Pantalla Principal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Crear panel para la imagen y el label
        JPanel imagePanel = new JPanel(new BorderLayout());

        // Añadir label encima de la imagen
        JLabel titleLabel = new JLabel("FlightCharts", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        imagePanel.add(titleLabel, BorderLayout.NORTH);

        // Añadir imagen redimensionada
        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResource("/flight.jpg"));
            Image scaledImage = originalImage.getScaledInstance(650, 400, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imagePanel.add(imageLabel, BorderLayout.CENTER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        add(imagePanel, BorderLayout.CENTER);

        // Añadir panel de botones
        JPanel buttonPanel = new JPanel();
        JButton trackFlightButton = new JButton("Rastrear Vuelo");
        JButton exitButton = new JButton("Salir");

        buttonPanel.add(trackFlightButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Acción del botón "Rastrear Vuelo"
        trackFlightButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                FlightModel model = new FlightModel();
                FlightDashboard view = new FlightDashboard("Controlador de Vuelo");
                FlightController controller = new FlightController(model, view);

                view.setVisible(true);

                // Obtener y mostrar vuelos recientes
                controller.fetchRecentFlights();
            });
        });

        // Acción del botón "Salir"
        exitButton.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainScreen mainScreen = new MainScreen();
            mainScreen.setVisible(true);
        });
    }
}