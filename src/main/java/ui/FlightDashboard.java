package ui;

import model.FlightModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
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

    public FlightDashboard(String title) {
        super(title);

        // Configuración de la ventana
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        chartsPanel = new JPanel(new GridLayout(2, 2));
        tabbedPane.addTab("Gráficas", chartsPanel);
        tabbedPane.addTab("Información", createInfoPanel());

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
}
