package org.opensha.eq.forecast;

import java.util.Iterator;

import org.opensha.geo.Location;
import org.opensha.mfd.IncrementalMfd;

import com.google.common.base.Predicate;

/**
 * Wrapper class for groups of related {@code SlabSource}s. Class decorates a
 * {@link GridSourceSet}.
 * 
 * @author Peter Powers
 * @see GridSourceSet
 */
public class SlabSourceSet implements SourceSet<PointSource> {

	private GridSourceSet delegate;

	SlabSourceSet(GridSourceSet delegate) {
		this.delegate = delegate;
	}

	@Override public int compareTo(SourceSet<PointSource> other) {
		return delegate.compareTo(other);
	}

	@Override public String name() {
		return delegate.name();
	}

	@Override public Iterator<PointSource> iterator() {
		return delegate.iterator();
	}

	@Override public SourceType type() {
		return SourceType.SLAB;
	}

	@Override public int size() {
		return delegate.size();
	}

	@Override public double weight() {
		return delegate.weight();
	}

	@Override public Iterable<PointSource> locationIterable(Location loc) {
		return delegate.locationIterable(loc);
	}

	@Override public GmmSet groundMotionModels() {
		return delegate.groundMotionModels();
	}

	@Override public Predicate<PointSource> distanceFilter(Location loc, double distance) {
		return delegate.distanceFilter(loc, distance);
	}
	
}