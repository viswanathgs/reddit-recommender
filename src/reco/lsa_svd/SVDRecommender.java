package reco.lsa_svd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import reco.Recommender;

/**
 * 
 * @author viswanathgs
 *
 * Extends the abstract class Recommender. Prediction using SVD.
 */
public class SVDRecommender extends Recommender {
	Map<String, Integer> accountIDs;
	Map<String, Integer> subredditIDs;
	RealMatrix termDocumentMatrix; // terms - sub-reddits, documents - accounts (users)
	int accountCount; // number of columns
	int subredditCount; // number of rows
	RealMatrix U; // left singular values
	RealMatrix V; // right singular values
	double threshold;
	
	/**
	 * 
	 * @param trainFile (account_id, subreddit_id, affinity)
	 * @param testFile (account_id, subreddit_id, affinity)
	 * @throws IOException
	 */
	public SVDRecommender(String trainFile, String testFile) {
		super(trainFile, testFile);
	}
	
	/**
	 * 
	 * Term document matrix is created with subreddits as terms and
	 * accounts (users) as documents. SVD is applied and U and V matrices
	 * are computed.
	 */
	@Override
	public void train() throws IOException {
		// Populate accountIDs and subredditIDs
		BufferedReader fin = new BufferedReader(new FileReader(trainFile));
		accountIDs = new HashMap<String, Integer>();
		subredditIDs = new HashMap<String, Integer>();
		accountCount = 0;
		subredditCount = 0;
		String line;
		int lineNumber = -1;
		while ((line = fin.readLine()) != null) {
			lineNumber++;
			if (lineNumber >= testLineBegin && lineNumber < testLineEnd) {
				continue;
			}
			
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
		
		// Fill in the term document matrix. Defaults to zeroVoteAffinity value.
		termDocumentMatrix = new Array2DRowRealMatrix(subredditCount, accountCount);
		for (int i = 0; i < subredditCount; ++i) {
			for (int j = 0; j < accountCount; ++j) {
				termDocumentMatrix.setEntry(i, j, utils.AffinityCalculator.zeroVoteAffinity);
			}
		}
		
		fin = new BufferedReader(new FileReader(trainFile));
		lineNumber = -1;
		while ((line = fin.readLine()) != null) {
			lineNumber++;
			if (lineNumber >= testLineBegin && lineNumber < testLineEnd) {
				continue;
			}
			
			StringTokenizer lineTokenizer = new StringTokenizer(line);
			String account = lineTokenizer.nextToken();
			String subreddit = lineTokenizer.nextToken();
			Double affinity = Double.parseDouble(lineTokenizer.nextToken());
			
			termDocumentMatrix.setEntry(subredditIDs.get(subreddit), accountIDs.get(account), affinity);
		}
		fin.close();
		
		// Compute SVD
		SingularValueDecomposition svd = new SingularValueDecomposition(termDocumentMatrix);
		U = svd.getU();
		V = svd.getV();
		
		System.out.println("U: " + U.getRowDimension() + ", " + U.getColumnDimension());
		System.out.println("V: " + V.getRowDimension() + ", " + V.getColumnDimension());
	}
	
	// TODO Confusion matrix
	@Override
	public double test() throws IOException {
		BufferedReader fin = new BufferedReader(new FileReader(testFile));
		String line;
		//TODO
		threshold = 1.03;
		int match = 0;
		int total = 0;
		int lineNumber = -1;
		while ((line = fin.readLine()) != null) {
			lineNumber++;
			if (testLineBegin != -1) {
				if (lineNumber < testLineBegin) {
					continue;
				} else if (lineNumber >= testLineEnd) {
					break;
				}
			}
			
			StringTokenizer lineTokenizer = new StringTokenizer(line);
			String account = lineTokenizer.nextToken();
			String subreddit = lineTokenizer.nextToken();
			Double affinity = Double.parseDouble(lineTokenizer.nextToken());
			
			if (!subredditIDs.containsKey(subreddit) || !accountIDs.containsKey(account)) {
				continue;
			}
			double distance = euclideanDistance(U.getRow(subredditIDs.get(subreddit)), V.getRow(accountIDs.get(account)));
			
//			System.out.println(line + "\tdist = " + distance);
			if ((distance < threshold) && (affinity > 0.8)) {
				match++;
			} else if ((distance >= threshold) && (affinity <= 0.8)) {
				match++;
			}
			total++;
		}
	
		double accuracy = (double)match / (double)total;
		System.out.println("Accuracy = " + accuracy);
		return accuracy;
	}
	
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	public double getThreshold() {
		return threshold;
	}
	
	double euclideanDistance(double[] x, double[] y) {
		if (x.length != y.length) {
			throw new IllegalArgumentException("Sizes do not match");
		}
		
		double distance = 0.0;
		for (int i = 0; i < x.length; ++i) {
			distance += Math.pow(x[i] - y[i], 2.0);
		}
		return Math.sqrt(distance);
	}
}
