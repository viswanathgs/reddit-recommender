package reco.clustknn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;

public class BisectingKMeansClusterer {
	int k;
	Collection<EuclideanDoublePoint> points;
	
	public BisectingKMeansClusterer(Collection<EuclideanDoublePoint> points, int k) {
		this.points = points;
		this.k = k;
	}
	
	List<Cluster<EuclideanDoublePoint>> cluster() {
		// Direct k-means
//		Random random = new Random();
//		KMeansPlusPlusClusterer<EuclideanDoublePoint> kMeansClusterer = new KMeansPlusPlusClusterer<EuclideanDoublePoint>(random);
//		return kMeansClusterer.cluster(points, k, 50);	
		
		List<Cluster<EuclideanDoublePoint>> clusters = new ArrayList<Cluster<EuclideanDoublePoint>>();
		
		// Initialize the cluster of all points
		Cluster<EuclideanDoublePoint> initialCluster = new Cluster<EuclideanDoublePoint>(getCenterPoint(points));
		Iterator<EuclideanDoublePoint> iter = points.iterator();
		while (iter.hasNext()) {
			initialCluster.addPoint(iter.next());
		}
		clusters.add(initialCluster);
		
		while (clusters.size() < k) {
			// Pick the largest cluster
			int maxCluster = 0;
			for (int i = 1; i < clusters.size(); i++) {
				if (clusters.get(i).getPoints().size() > clusters.get(maxCluster).getPoints().size()) {
					maxCluster = i;
				}
			}
			
			// Bisecting step
			Random random = new Random();
			KMeansPlusPlusClusterer<EuclideanDoublePoint> kMeansClusterer = new KMeansPlusPlusClusterer<EuclideanDoublePoint>(random);
			// TODO iter
			List<Cluster<EuclideanDoublePoint>> bisectedClusters = kMeansClusterer.cluster(clusters.get(maxCluster).getPoints(), 2, 100);
			clusters.remove(maxCluster);
			clusters.addAll(bisectedClusters);
		}
		
		return clusters;
		
	}
	
	EuclideanDoublePoint getCenterPoint(Collection<EuclideanDoublePoint> points) {
		EuclideanDoublePoint centerPoint = null;
		double minDistance = 0.0;
		
		Iterator<EuclideanDoublePoint> iter = points.iterator();
		while (iter.hasNext()) {
			EuclideanDoublePoint currentPoint = iter.next();
			Iterator<EuclideanDoublePoint> iter1 = points.iterator();
			double distance = 0.0;
			
			while (iter1.hasNext()) {
				distance += currentPoint.distanceFrom(iter1.next());
			}
			
			if (centerPoint == null || distance < minDistance) {
				centerPoint = currentPoint;
				minDistance = distance;
			}
		}
		
		return centerPoint;
	}
}
