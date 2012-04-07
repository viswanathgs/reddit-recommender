package reco.lsa_svd;

import java.io.IOException;

import utils.CrossValidator;

public class TestClass {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String inputFile = "datasets/affinities-100sr-shuffled.dat";
		CrossValidator crossValidator = new CrossValidator(inputFile);
		crossValidator.validate();
	}
}
