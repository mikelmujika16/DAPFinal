package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.axis.ValueAxis;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FlightDashboard extends JFrame {

    private XYSeries longitudeSeries;
    private XYSeries latitudeSeries;
    private XYSeries velocitySeries;
    private XYSeries verticalRateSeries;
    private XYSeries headingSeries;

    private List<Double[]> historicalData;

    public FlightDashboard(String title) {
        super(title);

        longitudeSeries = new XYSeries("Longitude");
        latitudeSeries = new XYSeries("Latitude");
        velocitySeries = new XYSeries("Velocity");
        verticalRateSeries = new XYSeries("Vertical Rate");
        headingSeries = new XYSeries("Heading");

        historicalData = new ArrayList<>();

        setLayout(new GridLayout(2, 3));

        add(createChartPanel("Longitude", longitudeSeries));
        add(createChartPanel("Latitude", latitudeSeries));
        add(createChartPanel("Velocity", velocitySeries));
        add(createChartPanel("Vertical Rate", verticalRateSeries));
        add(createChartPanel("Heading", headingSeries));

        setSize(1200, 800);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private JPanel createChartPanel(String title, XYSeries series) {
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "Time",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);

        // Verificar y configurar el eje de rango como NumberAxis
        if (plot.getRangeAxis() instanceof org.jfree.chart.axis.NumberAxis) {
            org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
            rangeAxis.setAutoRangeIncludesZero(false); // Evita incluir 0 automáticamente en el rango
        }

        return new ChartPanel(chart);
    }

    public void updateData(List<Double[]> flightData) {
        int startIndex = historicalData.size();

        for (int i = 0; i < flightData.size(); i++) {
            Double[] data = flightData.get(i);
            longitudeSeries.add(startIndex + i, data[0]);
            latitudeSeries.add(startIndex + i, data[1]);
            velocitySeries.add(startIndex + i, data[2]);
            verticalRateSeries.add(startIndex + i, data[3]);
            headingSeries.add(startIndex + i, data[4]);
        }

        historicalData.addAll(flightData);
    }

    public List<Double[]> getHistoricalData() {
        return historicalData;
    }
}
