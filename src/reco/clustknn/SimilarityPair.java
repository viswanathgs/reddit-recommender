package reco.clustknn;

public class SimilarityPair implements Comparable<SimilarityPair> {
	EuclideanDoublePoint surrogatePoint;
	double similarity;
	double mean;
	
	public SimilarityPair(EuclideanDoublePoint surrogatePoint, EuclideanDoublePoint point) {
		this.surrogatePoint = surrogatePoint;
		similarity = pearsonCorrelation(surrogatePoint, point);
		mean = calculateMean(surrogatePoint);
	}
	
	public EuclideanDoublePoint getSurrogatePoint() {
		return surrogatePoint;
	}
	
	public double getSimilarity() {
		return similarity;
	}
	
	public double getMean() {
		return mean;
	}
	
	@Override
	public int compareTo(SimilarityPair other) {
		if (similarity > other.similarity) {
			return -1;
		} else if (similarity < other.similarity) {
			return 1;
		}
		return 0;
	}
	
	double pearsonCorrelation(EuclideanDoublePoint x, EuclideanDoublePoint y) {
		double[] xPoint = x.getPoint();
		double[] yPoint = y.getPoint();
		
		assert(xPoint.length == yPoint.length);
		
		double xMean = calculateMean(x);
		double yMean = calculateMean(y);
		
		double covariance = 0.0;
		double xDeviation = 0.0;
		double yDeviation = 0.0;
		for (int i = 0; i < xPoint.length; i++) {
			covariance += (xPoint[i] - xMean) * (yPoint[i] - yMean);
			xDeviation += Math.pow(xPoint[i] - xMean, 2.0);
			yDeviation += Math.pow(yPoint[i] - yMean, 2.0);
		}
		xDeviation = Math.sqrt(xDeviation);
		yDeviation = Math.sqrt(yDeviation);
		
		return covariance / (xDeviation * yDeviation);
	}

	static double calculateMean(EuclideanDoublePoint point) {
		double sum = 0.0;
		for (int i = 0; i < point.getPoint().length; i++) {
			sum += point.getPoint()[i];
		}
		return sum / (double) point.getPoint().length;
	}
	
}
