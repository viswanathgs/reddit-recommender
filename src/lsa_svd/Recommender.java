package lsa_svd;

import java.io.IOException;

public class Recommender {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String trainFile = "datasets/train.dat";
		String testFile = "datasets/test.dat";
		SVDRecommender svdRecommender = new SVDRecommender(trainFile, testFile);
	}

}
