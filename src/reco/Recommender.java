package reco;

import java.io.IOException;

public abstract class Recommender {
	protected String trainFile;
	protected String testFile;
	
	public Recommender(String trainFile, String testFile) {
		this.trainFile = trainFile;
		this.testFile = testFile;
	}
	
	public abstract void train() throws IOException;
	public abstract void test() throws IOException;
}
