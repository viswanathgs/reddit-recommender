package reco.clustknn;

import java.io.IOException;

import utils.AffinityCalculator;
import utils.CrossValidator;

public class TestClass {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String inputFile = "datasets/affinities-100sr-shuffled-scale.dat";
		
//		CrossValidator crossValidator = new CrossValidator(inputFile);
//		crossValidator.setFolds(5);
//		crossValidator.validate();
		
//		AffinityCalculator affinityCalculator = new AffinityCalculator();
//		affinityCalculator.calculateAffinities("datasets/publicvotes-sorted.dat", "datasets/affinities-1e7-scale.dat");
		
		ClustKNNValidator clustKNNValidator = new ClustKNNValidator(inputFile);
		clustKNNValidator.validate();
	}
}
