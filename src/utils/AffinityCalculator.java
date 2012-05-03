package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class AffinityCalculator {
	public double zeroVoteAffinity;

	public AffinityCalculator() {
		// TODO zeroVoteAffinity - change this to reflect the metric.
		// zeroVoteAffinity = 0.5;
		// -1.0 to 1.0 scale
		zeroVoteAffinity = 0.0;
	}
	
	/**
	 * @param inFile 
	 * 	Format - account_id	subreddit_id	direction.
	 * 	Should be sorted based on account_id first and then subreddit_id.
	 * @throws IOException 
	 * 
	 * This function calculates the affinity of an account to a subreddit.
	 * The result is written to outFile in the format
	 * account_id	subreddit_id	affinity. 
	 */
	public void calculateAffinities(String inFile, String outFile) throws IOException {
		BufferedReader fin = new BufferedReader(new FileReader(inFile));
		BufferedWriter fout = new BufferedWriter(new FileWriter(outFile));
		String line;
		String currentAccount = "";
		String currentSubreddit = "";
		int upvotes = 0;
		int totalvotes = 0;
		
		while ((line = fin.readLine()) != null) {
			StringTokenizer lineTokenizer = new StringTokenizer(line);
			String account = lineTokenizer.nextToken();
			String subreddit = lineTokenizer.nextToken();
			String direction = lineTokenizer.nextToken();
			
			if (currentAccount.equals("")) {
				currentAccount = account;
				currentSubreddit = subreddit;
			}
			if (!currentAccount.equals(account) || !currentSubreddit.equals(subreddit)) {
				fout.write(currentAccount + "\t" + currentSubreddit + "\t" + affinityMetric(upvotes, totalvotes) + "\n");
				fout.flush();
				currentAccount = account;
				currentSubreddit = subreddit;
				totalvotes = 0;
				upvotes = 0;
			}
			
			totalvotes++;
			if (direction.equals("1")) {
				upvotes++;
			}
		}
		fout.write(currentAccount + "\t" + currentSubreddit + "\t" + affinityMetric(upvotes, totalvotes));
		
		fin.close();
		fout.close();
	}

	/**
	 * 
	 * @param upvotes
	 * @param totalvotes
	 * @return calculated affinity based on a specific metric.
	 * 
	 * The metric used is upvotes/totalvotes. 0.5 indicates no votes,
	 * > 0.5 denotes positive affinity, and negative affinity otherwise. 
	 * 
	 * TODO 
	 * 	Experiment different affinity metrics. Try -1.0 to 1.0 scale.
	 * 
	 */
	private double affinityMetric(int upvotes, int totalvotes) {
		if (totalvotes == 0) {
			return zeroVoteAffinity;
		} else {
			// return (double)upvotes / (double)totalvotes;
			// -1.0 to 1.0 scale
			return ((double)upvotes / (double)totalvotes) * 2.0 - 1.0;
		}
	}
}
