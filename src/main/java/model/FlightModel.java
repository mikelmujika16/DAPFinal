package model;

import opensky.StateVector;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;

import java.util.ArrayList;
import java.util.List;

public class FlightModel {
    private XYSeries altitudeSeries = new XYSeries("Altitud (m)");
    private XYSeries velocitySeries = new XYSeries("Velocidad (m/s)");
    private XYSeries verticalSpeedSeries = new XYSeries("Velocidad Vertical (m/s)");
    private List<StateVector> recentFlights = new ArrayList<>();


    private double maxAltitude = 0;
    private double totalAltitude = 0;
    private double totalVelocity = 0;
    private int updateCount = 0;

    public void addData(double altitude, double velocity, double verticalSpeed) {
        altitudeSeries.add(updateCount, altitude);
        velocitySeries.add(updateCount, velocity);
        verticalSpeedSeries.add(updateCount, verticalSpeed);

        if (altitude > maxAltitude) {
            maxAltitude = altitude;
        }
        totalAltitude += altitude;
        totalVelocity += velocity;
        updateCount++;
    }

    public XYSeries getAltitudeSeries() {
        return altitudeSeries;
    }

    public XYSeries getVelocitySeries() {
        return velocitySeries;
    }

    public XYSeries getVerticalSpeedSeries() {
        return verticalSpeedSeries;
    }

    public DefaultPieDataset getSpeedDistributionDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        double lowSpeed = 0, mediumSpeed = 0, highSpeed = 0;

        for (int i = 0; i < velocitySeries.getItemCount(); i++) {
            double speed = velocitySeries.getY(i).doubleValue();
            if (speed < 200) {
                lowSpeed++;
            } else if (speed < 400) {
                mediumSpeed++;
            } else {
                highSpeed++;
            }
        }

        if (lowSpeed + mediumSpeed + highSpeed == 0) {
            dataset.setValue("Sin Datos", 100);
        } else {
            dataset.setValue("Baja (<200 m/s)", lowSpeed);
            dataset.setValue("Media (200-400 m/s)", mediumSpeed);
            dataset.setValue("Alta (>400 m/s)", highSpeed);
        }

        return dataset;
    }

    public double getMaxAltitude() {
        return maxAltitude;
    }

    public double getAverageAltitude() {
        return totalAltitude / updateCount;
    }

    public double getAverageVelocity() {
        return totalVelocity / updateCount;
    }

    public void setRecentFlights(List<StateVector> flights) {
        recentFlights = flights;
    }

    public List<StateVector> getRecentFlights() {
        return recentFlights;
    }
}
