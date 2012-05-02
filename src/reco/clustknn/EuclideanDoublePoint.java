package reco.clustknn;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.math3.stat.clustering.Clusterable;

public class EuclideanDoublePoint implements Clusterable<EuclideanDoublePoint> {
	private final double[] point;
	
	public double[] getPoint() {
		return point;
	}
	
	public EuclideanDoublePoint(double[] point) {
		this.point = point;
	}
	
	@Override
	public EuclideanDoublePoint centroidOf(Collection<EuclideanDoublePoint> points) {
		assert(points.size() > 0);
		
		double[] centroid = null;
		Iterator<EuclideanDoublePoint> iter = points.iterator();
		int length = 0;
		while (iter.hasNext()) {
			EuclideanDoublePoint iterPoint = iter.next();
			
			if (centroid == null) {
				length = iterPoint.getPoint().length;
				centroid = new double[length];
				for (int i = 0; i < length; i++) {
					centroid[i] = 0.0;
				}
			}
			
			for (int i = 0; i < length; i++) {
				centroid[i] += iterPoint.getPoint()[i];
			}
		}
		
		int totalPoints = points.size();
		for (int i = 0; i < length; i++) {
			centroid[i] /= (double) totalPoints;
		}
		
		return new EuclideanDoublePoint(centroid);
	}

	@Override
	public double distanceFrom(EuclideanDoublePoint other) {
		double[] otherPoint = other.getPoint();
		
		assert(point.length == otherPoint.length);
		
		double distance = 0.0;
		for (int i = 0; i < point.length; i++) {
			distance += Math.pow(point[i] - otherPoint[i], 2.0);
		}
		return Math.sqrt(distance);
	}

}
