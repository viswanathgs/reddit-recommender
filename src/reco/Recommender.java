package reco;

import java.io.IOException;

/**
 * 
 * @author viswanathgs
 * 
 * Abstract class Recommender.
 * 
 */
public abstract class Recommender {
	protected String trainFile;
	protected String testFile;
	protected int testLineBegin;
	protected int testLineEnd;
	
	/**
	 * 
	 * @param trainFile
	 * @param testFile
	 * 
	 * testLineBegin and testLineEnd are used for cross validation purposes,
	 * when the trainFile and the testFile are the same. train() is expected
	 * to skip the lines from testLineBegin (inclusive) to testLineEnd (exclusive), 
	 * and test() is expected to read only the lines from testLineBegin (inclusive)
	 * to testLineEnd (exclusive). Both are 0-based, and default to -1, meaning the 
	 * entire file is to be read.
	 */
	public Recommender(String trainFile, String testFile) {
		this.trainFile = trainFile;
		this.testFile = testFile;
		testLineBegin = -1;
		testLineEnd = -1;
	}
	
	public String getTrainFile() {
		return trainFile;
	}
	
	public String getTestFile() {
		return testFile;
	}
	
	public void setTestLineBegin(int lineNumber) {
		testLineBegin = lineNumber;
	}
	
	public void setTestLineEnd(int lineNumber) {
		testLineEnd = lineNumber;
	}
	
	/**
	 * Trains the trainFile, skipping lines from testLineBegin (inclusive) to
	 * testLineEnd (exclusive). If they are -1, the entire file is used.
	 * 
	 * @throws IOException
	 */
	public abstract void train() throws IOException;
	
	/**
	 * Tests testFile from testLineBegin (inclusive) to testLineEnd (exclusive).
	 * If they are -1, the entire file is used.
	 * Returns the prediction accuracy.
	 * 
	 * @return accuracy
	 * @throws IOException
	 */
	public abstract double test() throws IOException;
}
