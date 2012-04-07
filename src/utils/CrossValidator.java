package utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import reco.Recommender;
import reco.lsa_svd.SVDRecommender;

/**
 * 
 * @author viswanathgs
 *
 * Class for cross validating the input file over the train and test
 * methods of Recommender.
 */

public class CrossValidator {
	String inputFile;
	int totalLines;
	int folds; // Cross validation parameter folds
	
	/**
	 * 
	 * @param inputFile
	 * @throws IOException
	 * 
	 * 10-fold validation by default.
	 */
	public CrossValidator(String inputFile) throws IOException {
		this.inputFile = inputFile;
		totalLines = countLines(inputFile);
		setFolds(10);
	}
	
	public void validate(int folds) throws IOException {
		setFolds(folds);
		validate();
	}
	
	/**
	 * 
	 * Divides the inputFile into 'folds' sets.
	 * When one set is used for testing, the remaining (folds - 1) sets are
	 * used for training. Repeats the same technique 'folds' rounds, with each 
	 * set being sent for training during each round. The average accuracy from
	 * all the rounds is calculated.
	 * 
	 * @throws IOException 
	 */
	public void validate() throws IOException {
		Recommender recommender = new SVDRecommender(inputFile, inputFile);
		
		int foldSize = (int) Math.ceil((double) totalLines / (double) folds);
		double accuracy = 0.0;
		for (int round = 0; round < folds; ++round) {
			System.out.println("\nRound " + (round + 1)+ "/" + folds + ": ");
			
			recommender.setTestLineBegin(round * foldSize);
			recommender.setTestLineEnd(Math.min((round + 1) * foldSize, totalLines));
			recommender.train();
			accuracy += recommender.test();
		}
		
		accuracy /= (double) folds;
		System.out.println("Folds = " + folds + " Accuracy = " + accuracy);
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * 
	 * Returns the total number of lines in a file.
	 */
	public int countLines(String file) throws IOException {
		LineNumberReader  lnr = new LineNumberReader(new FileReader(new File(file)));
		lnr.skip(Long.MAX_VALUE);
		return lnr.getLineNumber();
	}
	
	public void setFolds(int folds) {
		this.folds = folds;
	}
	
	public int getFolds() {
		return folds;
	}
}

