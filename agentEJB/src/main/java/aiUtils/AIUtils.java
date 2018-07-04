package aiUtils;

import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class AIUtils {

	public static Instances loadDataset(String path) {
		Instances dataset = null;
		try {
			dataset = DataSource.read(path);
			dataset.setClassIndex(dataset.numAttributes() - 1);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return dataset;
	}

	public static LinearRegression createLR(Instances dataset) {
		LinearRegression lr = new LinearRegression();
		try {
			lr.buildClassifier(dataset);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return lr;
	}
}
