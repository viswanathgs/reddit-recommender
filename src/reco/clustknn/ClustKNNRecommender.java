package reco.clustknn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.stat.clustering.Cluster;

public class ClustKNNRecommender {
	Collection<EuclideanDoublePoint> surrogateUsers;
	int k;

	public ClustKNNRecommender(int k) {
		this.k = k;
	}

	public void train(Collection<EuclideanDoublePoint> points) throws IOException {
		surrogateUsers = new ArrayList<EuclideanDoublePoint>();
		
		BisectingKMeansClusterer clusterer = new BisectingKMeansClusterer(points, k);
		List<Cluster<EuclideanDoublePoint>> clusters = clusterer.cluster();
		Iterator<Cluster<EuclideanDoublePoint>> iter = clusters.iterator();
		while (iter.hasNext()) {
			surrogateUsers.add(iter.next().getCenter());
		}
	}

	public double test(Collection<EuclideanDoublePoint> points, int l, double threshold) {
		int match = 0;
		int total = 0;
		int tp = 0;
		int tn = 0;
		int fp = 0;
		int fn = 0;
		
		java.util.Iterator<EuclideanDoublePoint> iter = points.iterator();
		while (iter.hasNext()) {
			EuclideanDoublePoint point = iter.next();
			int length = point.getPoint().length;
			Collection<SimilarityPair> sortedSurrogateSimilarities = computeSortedSurrogateSimilarities(point); 
			
			for (int i = 0; i < length; i++) {
				// Skip test cases with affinity 0.0 (no occurrence in dataset)
				if (point.getPoint()[i] == 0.0) {
					continue;
				}
					
				double score = computePredictionScore(sortedSurrogateSimilarities, point, i, l);
				//System.out.println("score = " + score + " pointi = " + point.getPoint()[i]);
				
				if (score > threshold) {
					if (point.getPoint()[i] > 0.5) {
						match++;
						tp++;
					} else {
						fp++;
					}
				} else {
					if (point.getPoint()[i] <= 0.5) {
						match++;
						tn++;
					} else {
						fn++;
					}
				}
				total++;
			}
		}
		
		double accuracy = (double)match / (double)total;
		double precision = (double)tp / (double)(tp + fp);
		double recall = (double)tp / (double)(tp + fn);
		System.out.println("Accuracy = " + accuracy + " Precision = " + precision + " Recall = " + recall);
		System.out.println("tp = " + tp + " tn = " + tn + " fp = " + fp + " fn = " + fn);
		return accuracy;
	}
	
	double computePredictionScore(Collection<SimilarityPair> surrogateSimilarities, EuclideanDoublePoint point, int item, int l) {
		double score = 0.0;
		int count = 0;
		double totalWeight = 0.0;
		Iterator<SimilarityPair> iter = surrogateSimilarities.iterator();
		while (iter.hasNext()) {
			count++;
			if (count > l) {
				break;
			}
			SimilarityPair similarityPair = iter.next();
			// TODO add weight
			score += (similarityPair.getSurrogatePoint().getPoint()[item] - similarityPair.getMean()) * similarityPair.getSimilarity();
			totalWeight += similarityPair.getSimilarity();
		}
		// TODO weight
		return SimilarityPair.calculateMean(point) + score / (double) totalWeight;
	}
	
	/**
	 * Returns a list of similarity pairs of surrogate users and the given point,
	 * sorted based on the similarity (Pearson correlation) between two (from 
	 * highest to lowest).
	 * 
	 */
	Collection<SimilarityPair> computeSortedSurrogateSimilarities(EuclideanDoublePoint point) {
		List<SimilarityPair> surrogateSimilarities = new ArrayList<SimilarityPair>();
		
		Iterator<EuclideanDoublePoint> iter = surrogateUsers.iterator();
		while (iter.hasNext()) {
			EuclideanDoublePoint surrogatePoint = iter.next();
			surrogateSimilarities.add(new SimilarityPair(surrogatePoint, point));
		}
		Collections.sort(surrogateSimilarities);
		
		return surrogateSimilarities;
	}
}
