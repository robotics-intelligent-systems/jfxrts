import java.util.Random;

/**
 * Simple Java Neural Network for Weather Prediction
 * Feedforward network with 1 hidden layer, trained via backpropagation.
 */
public class SimpleWeatherNN {

    // Network architecture
    private final int inputSize;     // e.g., temperature, humidity, wind speed
    private final int hiddenSize;    // hidden neurons
    private final int outputSize;    // e.g., rain probability
    private final double learningRate;

    // Weights and biases
    private double[][] weightsInputHidden;
    private double[][] weightsHiddenOutput;
    private double[] biasHidden;
    private double[] biasOutput;

    // Constructor
    public SimpleWeatherNN(int inputSize, int hiddenSize, int outputSize, double learningRate) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        this.learningRate = learningRate;
        initWeights();
    }

    // Initialize weights randomly
    private void initWeights() {
        Random rand = new Random();
        weightsInputHidden = new double[inputSize][hiddenSize];
        weightsHiddenOutput = new double[hiddenSize][outputSize];
        biasHidden = new double[hiddenSize];
        biasOutput = new double[outputSize];

        for (int i = 0; i < inputSize; i++)
            for (int j = 0; j < hiddenSize; j++)
                weightsInputHidden[i][j] = rand.nextDouble() - 0.5;

        for (int i = 0; i < hiddenSize; i++)
            for (int j = 0; j < outputSize; j++)
                weightsHiddenOutput[i][j] = rand.nextDouble() - 0.5;

        for (int i = 0; i < hiddenSize; i++)
            biasHidden[i] = rand.nextDouble() - 0.5;

        for (int i = 0; i < outputSize; i++)
            biasOutput[i] = rand.nextDouble() - 0.5;
    }

    // Sigmoid activation
    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    // Derivative of sigmoid
    private double sigmoidDerivative(double x) {
        return x * (1 - x);
    }

    // Forward pass
    private double[] forward(double[] inputs, double[] hiddenOut) {
        // Hidden layer
        for (int j = 0; j < hiddenSize; j++) {
            double sum = biasHidden[j];
            for (int i = 0; i < inputSize; i++)
                sum += inputs[i] * weightsInputHidden[i][j];
            hiddenOut[j] = sigmoid(sum);
        }

        // Output layer
        double[] outputs = new double[outputSize];
        for (int k = 0; k < outputSize; k++) {
            double sum = biasOutput[k];
            for (int j = 0; j < hiddenSize; j++)
                sum += hiddenOut[j] * weightsHiddenOutput[j][k];
            outputs[k] = sigmoid(sum);
        }
        return outputs;
    }

    // Train with one sample
    public void train(double[] inputs, double[] targets) {
        double[] hiddenOut = new double[hiddenSize];
        double[] outputs = forward(inputs, hiddenOut);

        // Output layer error
        double[] outputErrors = new double[outputSize];
        for (int k = 0; k < outputSize; k++)
            outputErrors[k] = targets[k] - outputs[k];

        // Hidden layer error
        double[] hiddenErrors = new double[hiddenSize];
        for (int j = 0; j < hiddenSize; j++) {
            double error = 0;
            for (int k = 0; k < outputSize; k++)
                error += outputErrors[k] * weightsHiddenOutput[j][k];
            hiddenErrors[j] = error;
        }

        // Update weights Hidden->Output
        for (int j = 0; j < hiddenSize; j++) {
            for (int k = 0; k < outputSize; k++) {
                double delta = learningRate * outputErrors[k] * sigmoidDerivative(outputs[k]) * hiddenOut[j];
                weightsHiddenOutput[j][k] += delta;
            }
        }

        // Update biases Output
        for (int k = 0; k < outputSize; k++)
            biasOutput[k] += learningRate * outputErrors[k] * sigmoidDerivative(outputs[k]);

        // Update weights Input->Hidden
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                double delta = learningRate * hiddenErrors[j] * sigmoidDerivative(hiddenOut[j]) * inputs[i];
                weightsInputHidden[i][j] += delta;
            }
        }

        // Update biases Hidden
        for (int j = 0; j < hiddenSize; j++)
            biasHidden[j] += learningRate * hiddenErrors[j] * sigmoidDerivative(hiddenOut[j]);
    }

    // Predict
    public double[] predict(double[] inputs) {
        return forward(inputs, new double[hiddenSize]);
    }

    // Example usage
    public static void main(String[] args) {
        // Create network: 3 inputs, 4 hidden neurons, 1 output
        SimpleWeatherNN nn = new SimpleWeatherNN(3, 4, 1, 0.1);

        // Example training data: [temp, humidity, wind] -> [rain probability]
        double[][] trainingInputs = {
            {30, 70, 10}, // warm, humid, low wind
            {25, 80, 5},
            {15, 60, 20},
            {10, 90, 5},
            {35, 40, 15}
        };
        double[][] trainingOutputs = {
            {1}, // likely rain
            {1},
            {0},
            {1},
            {0}
        };

        // Normalize inputs (simple scaling)
        for (int i = 0; i < trainingInputs.length; i++) {
            trainingInputs[i][0] /= 40.0; // temp scale
            trainingInputs[i][1] /= 100.0; // humidity scale
            trainingInputs[i][2] /= 50.0; // wind scale
        }

        // Train for multiple epochs
        for (int epoch = 0; epoch < 5000; epoch++) {
            for (int i = 0; i < trainingInputs.length; i++) {
                nn.train(trainingInputs[i], trainingOutputs[i]);
            }
        }

        // Test prediction
        double[] testInput = {28 / 40.0, 75 / 100.0, 8 / 50.0};
        double[] prediction = nn.predict(testInput);
        System.out.printf("Predicted rain probability: %.2f%%\n", prediction[0] * 100);
    }
}
