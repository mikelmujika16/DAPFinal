package ui;

import model.FlightModel;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.viewer.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

public class FlightDashboard extends JFrame {
    private JLabel currentAltitudeLabel;
    private JLabel currentVelocityLabel;
    private JLabel maxAltitudeLabel;
    private JLabel avgAltitudeLabel;
    private JLabel avgVelocityLabel;
    private JLabel updateCountLabel;
    private JLabel lastUpdateLabel;

    private JPanel chartsPanel;
    private JXMapViewer mapViewer;
    private WaypointPainter<Waypoint> waypointPainter;

    public FlightDashboard(String title) {
        super(title);

        // Configuración de la ventana
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        chartsPanel = new JPanel(new GridLayout(2, 2));
        tabbedPane.addTab("Gráficas", chartsPanel);
        tabbedPane.addTab("Información", createInfoPanel());
        tabbedPane.addTab("Mapa", createMapPanel());

        add(tabbedPane, BorderLayout.CENTER);
        setSize(1200, 800);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        currentAltitudeLabel = new JLabel("Altitud Actual: -");
        currentVelocityLabel = new JLabel("Velocidad Actual: -");
        maxAltitudeLabel = new JLabel("Altitud Máxima: -");
        avgAltitudeLabel = new JLabel("Altitud Promedio: -");
        avgVelocityLabel = new JLabel("Velocidad Promedio: -");
        updateCountLabel = new JLabel("Número de Actualizaciones: 0");
        lastUpdateLabel = new JLabel("Última Actualización: -");

        panel.add(currentAltitudeLabel);
        panel.add(currentVelocityLabel);
        panel.add(maxAltitudeLabel);
        panel.add(avgAltitudeLabel);
        panel.add(avgVelocityLabel);
        panel.add(updateCountLabel);
        panel.add(lastUpdateLabel);

        return panel;
    }

    private JPanel createMapPanel() {
        mapViewer = new JXMapViewer();
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Set the focus
        mapViewer.setZoom(10);
        mapViewer.setAddressLocation(new GeoPosition(0, 0));

        waypointPainter = new WaypointPainter<>();
        mapViewer.setOverlayPainter(waypointPainter);

        JPanel mapPanel = new JPanel(new BorderLayout());
        mapPanel.add(mapViewer, BorderLayout.CENTER);
        return mapPanel;
    }

    public void updateView(double epochTime, double altitude, double velocity, double verticalSpeed, double latitude, double longitude, FlightModel model) {
        String formattedTime = new SimpleDateFormat("HH:mm:ss").format(new Date((long) (epochTime * 1000)));

        // Actualizar etiquetas
        currentAltitudeLabel.setText("Altitud Actual: " + altitude + " m");
        currentVelocityLabel.setText("Velocidad Actual: " + velocity + " m/s");
        maxAltitudeLabel.setText("Altitud Máxima: " + model.getMaxAltitude() + " m");
        avgAltitudeLabel.setText("Altitud Promedio: " + model.getAverageAltitude() + " m");
        avgVelocityLabel.setText("Velocidad Promedio: " + model.getAverageVelocity() + " m/s");
        updateCountLabel.setText("Número de Actualizaciones: " + model.getAltitudeSeries().getItemCount());
        lastUpdateLabel.setText("Última Actualización: " + formattedTime);

        // Actualizar gráficos
        chartsPanel.removeAll();
        chartsPanel.add(createChartPanel("Altitud vs Tiempo", model.getAltitudeSeries(), "Punto de Muestra", "Altitud (m)"));
        chartsPanel.add(createChartPanel("Velocidad vs Tiempo", model.getVelocitySeries(), "Punto de Muestra", "Velocidad (m/s)"));
        chartsPanel.add(createChartPanel("Velocidad Vertical", model.getVerticalSpeedSeries(), "Punto de Muestra", "Velocidad Vertical (m/s)"));
        chartsPanel.add(createSpeedDistributionChart(model)); // Gráfico de pastel
        chartsPanel.revalidate();
        chartsPanel.repaint();

        // Actualizar mapa
        GeoPosition geoPosition = new GeoPosition(latitude, longitude);
        mapViewer.setAddressLocation(geoPosition);
        Waypoint waypoint = new DefaultWaypoint(geoPosition);
        waypointPainter.setWaypoints(Collections.singleton(waypoint));
        mapViewer.repaint();
    }

    private JPanel createChartPanel(String title, XYSeries series, String xLabel, String yLabel) {
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, dataset);
        return new ChartPanel(chart);
    }

    private JPanel createSpeedDistributionChart(FlightModel model) {
        JFreeChart chart = ChartFactory.createPieChart(
                "Distribución de Velocidad",
                model.getSpeedDistributionDataset(),
                true, true, false
        );
        return new ChartPanel(chart);
    }

    public void updateTitle(String icao24) {
        setTitle(getTitle() + " - " + icao24);
    }
}