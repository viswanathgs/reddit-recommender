package reco.clustknn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class ClustKNNValidator {
	Map<String, Integer> accountIDs;
	Map<String, Integer> subredditIDs;
	int accountCount; 
	int subredditCount;
	RealMatrix matrix;
	int folds = 10;
	
	public ClustKNNValidator(String inFile) throws IOException {
		BufferedReader fin = new BufferedReader(new FileReader(inFile));
		accountIDs = new HashMap<String, Integer>();
		subredditIDs = new HashMap<String, Integer>();
		accountCount = 0;
		subredditCount = 0;
		String line;
		
		while ((line = fin.readLine()) != null) {
			StringTokenizer lineTokenizer = new StringTokenizer(line);
			String account = lineTokenizer.nextToken();
			String subreddit = lineTokenizer.nextToken();
			
			if (!accountIDs.containsKey(account)) {
				accountIDs.put(account, accountCount);
				accountCount++;
			}
			if (!subredditIDs.containsKey(subreddit)) {
				subredditIDs.put(subreddit, subredditCount);
				subredditCount++;
			}
		}
		fin.close();
		
		// TODO
		System.out.println("accountCount = " + accountCount + " subredditCount = " + subredditCount);
		
		matrix = new Array2DRowRealMatrix(accountCount, subredditCount);
		for (int i = 0; i < accountCount; ++i) {
			for (int j = 0; j < subredditCount; ++j) {
				// TODO modify zeroVoteAffinity
				matrix.setEntry(i, j, 0.0);
			}
		}
		
		fin = new BufferedReader(new FileReader(inFile));
		while ((line = fin.readLine()) != null) {
			StringTokenizer lineTokenizer = new StringTokenizer(line);
			String account = lineTokenizer.nextToken();
			String subreddit = lineTokenizer.nextToken();
			Double affinity = Double.parseDouble(lineTokenizer.nextToken());
			
			matrix.setEntry(accountIDs.get(account), subredditIDs.get(subreddit), affinity);
		}
		fin.close();
	}
	
	void validate() throws IOException {
		int totalLines = matrix.getRowDimension();
		int foldSize = (int) Math.ceil((double) totalLines / (double) folds);
		double accuracy = 0.0;
		
		for (int round = 0; round < folds; ++round) {
			System.out.println("\nRound " + (round + 1)+ "/" + folds + ": ");
			accuracy += trainAndTest(round * foldSize, Math.min((round + 1) * foldSize, totalLines));
		}
		
		accuracy /= (double) folds;
		System.out.println("Folds = " + folds + " Accuracy = " + accuracy);
	}
	
	double trainAndTest(int testLineBegin, int testLineEnd) throws IOException {
		Collection<EuclideanDoublePoint> trainPoints = new ArrayList<EuclideanDoublePoint>();
		Collection<EuclideanDoublePoint> testPoints = new ArrayList<EuclideanDoublePoint>();
		
		for (int line = 0; line < matrix.getRowDimension(); ++line) {
			if (line >= testLineBegin && line < testLineEnd) {
				testPoints.add(new EuclideanDoublePoint(matrix.getRow(line)));
			} else {
				trainPoints.add(new EuclideanDoublePoint(matrix.getRow(line)));
			}
		}
		
		// TODO k, l, threshold
		ClustKNNRecommender recommender = new ClustKNNRecommender(50);
		recommender.train(trainPoints);
		return recommender.test(testPoints, 50, 0.18);
	}
}
