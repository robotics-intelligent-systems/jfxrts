import org.apache.commons.math3.linear.*;

import java.util.Arrays;
import java.util.Random;

/**
 * Simple Vector Autoregression (VAR) example in Java
 * for predicting temperature and humidity.
 * Requires Apache Commons Math library:
 * https://commons.apache.org/proper/commons-math/
 */
public class WeatherVAR {

    // Fit a VAR(1) model: Y_t = A * Y_{t-1} + c
    public static RealMatrix fitVAR(RealMatrix Y) {
        int n = Y.getRowDimension(); // time points
        int k = Y.getColumnDimension(); // variables

        // Prepare lagged matrix (Y_{t-1})
        double[][] laggedData = new double[n - 1][k];
        double[][] currentData = new double[n - 1][k];

        for (int t = 1; t < n; t++) {
            laggedData[t - 1] = Y.getRow(t - 1);
            currentData[t - 1] = Y.getRow(t);
        }

        RealMatrix Ylag = new Array2DRowRealMatrix(laggedData);
        RealMatrix Ycurr = new Array2DRowRealMatrix(currentData);

        // Estimate A = (Ylag' * Ylag)^(-1) * Ylag' * Ycurr
        RealMatrix XtX = Ylag.transpose().multiply(Ylag);
        RealMatrix XtY = Ylag.transpose().multiply(Ycurr);

        try {
            RealMatrix A = new LUDecomposition(XtX).getSolver().getInverse().multiply(XtY);
            return A;
        } catch (Exception e) {
            throw new RuntimeException("Matrix inversion failed. Possibly singular matrix.", e);
        }
    }

    // Predict next step using VAR(1)
    public static double[] predictNext(RealMatrix A, double[] lastObs) {
        RealMatrix lastVec = new Array2DRowRealMatrix(lastObs);
        RealMatrix prediction = A.transpose().multiply(lastVec);
        return prediction.getColumn(0);
    }

    // Generate synthetic weather data (temperature, humidity)
    public static RealMatrix generateWeatherData(int days) {
        Random rand = new Random(42);
        double[][] data = new double[days][2];
        double temp = 20.0;
        double hum = 60.0;

        for (int i = 0; i < days; i++) {
            temp += rand.nextGaussian(); // random walk
            hum += rand.nextGaussian() * 0.5;
            data[i][0] = temp;
            data[i][1] = hum;
        }
        return new Array2DRowRealMatrix(data);
    }

    public static void main(String[] args) {
        try {
            // Step 1: Generate synthetic data
            RealMatrix weatherData = generateWeatherData(30);
            System.out.println("Historical Weather Data (Temp, Humidity):");
            for (double[] row : weatherData.getData()) {
                System.out.printf("%.2f\t%.2f%n", row[0], row[1]);
            }

            // Step 2: Fit VAR(1) model
            RealMatrix A = fitVAR(weatherData);
            System.out.println("\nEstimated VAR(1) Coefficient Matrix A:");
            System.out.println(Arrays.deepToString(A.getData()));

            // Step 3: Predict next day's weather
            double[] lastObs = weatherData.getRow(weatherData.getRowDimension() - 1);
            double[] prediction = predictNext(A, lastObs);

            System.out.printf("\nPredicted Next Day -> Temp: %.2f, Humidity: %.2f%n",
                    prediction[0], prediction[1]);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}