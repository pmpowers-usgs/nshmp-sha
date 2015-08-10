package org.opensha2.calc;

import static org.opensha2.eq.model.SourceType.*;
import static org.opensha2.calc.AsyncCalc.toClusterCurves;
import static org.opensha2.calc.AsyncCalc.toClusterGroundMotions;
import static org.opensha2.calc.AsyncCalc.toClusterInputs;
import static org.opensha2.calc.AsyncCalc.toGroundMotions;
import static org.opensha2.calc.AsyncCalc.toHazardCurveSet;
import static org.opensha2.calc.AsyncCalc.toHazardCurves;
import static org.opensha2.calc.AsyncCalc.toHazardResult;
import static org.opensha2.calc.AsyncCalc.toInputs;
import static org.opensha2.calc.AsyncCalc.toSystemInputs;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.opensha2.data.ArrayXY_Sequence;
import org.opensha2.eq.model.ClusterSourceSet;
import org.opensha2.eq.model.HazardModel;
import org.opensha2.eq.model.Source;
import org.opensha2.eq.model.SourceSet;
import org.opensha2.eq.model.SourceType;
import org.opensha2.eq.model.SystemSourceSet;
import org.opensha2.gmm.Imt;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Static probabilistic seismic hazard analysis calculators.
 * 
 * @author Peter Powers
 */
public class Calcs {

	
	// TODO if config specifies using grid tables, we need to reroute grid calcs
	// where to build/store lookup table/object for each GmmSet
	
	/**
	 * Compute a hazard curve using the supplied {@link Executor}.
	 * 
	 * @param model to use
	 * @param config
	 * @param site of interest
	 * @param executor optional Executor to use in calculation
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static HazardResult hazardCurve(
			HazardModel model,
			CalcConfig config,
			Site site,
			Executor executor)
			throws InterruptedException, ExecutionException {

		AsyncList<HazardCurveSet> curveSetCollector = AsyncList.createWithCapacity(model.size());
		Map<Imt, ArrayXY_Sequence> modelCurves = config.logModelCurves();

		for (SourceSet<? extends Source> sourceSet : model) {

			if (sourceSet.type() == CLUSTER) {

				ClusterSourceSet clusterSourceSet = (ClusterSourceSet) sourceSet;

				AsyncList<ClusterInputs> inputs = toClusterInputs(clusterSourceSet, site, executor);
				if (inputs.isEmpty()) continue; // all sources out of range

				AsyncList<ClusterGroundMotions> groundMotions = toClusterGroundMotions(inputs,
					clusterSourceSet, config.imts(), executor);

				AsyncList<ClusterCurves> clusterCurves = toClusterCurves(groundMotions,
					modelCurves, config.exceedanceModel, config.truncationLevel, executor);

				ListenableFuture<HazardCurveSet> curveSet = toHazardCurveSet(clusterCurves,
					clusterSourceSet, modelCurves, executor);

				curveSetCollector.add(curveSet);
				
			} else if (sourceSet.type() == SYSTEM) {

				SystemSourceSet systemSourceSet = (SystemSourceSet) sourceSet;
				
				ListenableFuture<SystemInputs> inputs = toSystemInputs(systemSourceSet, site, executor);
				
				
				
			} else {

				AsyncList<HazardInputs> inputs = toInputs(sourceSet, site, executor);
				if (inputs.isEmpty()) continue; // all sources out of range

				AsyncList<HazardGroundMotions> groundMotions = toGroundMotions(inputs, sourceSet,
					config.imts(), executor);

				AsyncList<HazardCurves> hazardCurves = toHazardCurves(groundMotions, modelCurves,
					config.exceedanceModel, config.truncationLevel, executor);

				ListenableFuture<HazardCurveSet> curveSet = toHazardCurveSet(hazardCurves,
					sourceSet, modelCurves, executor);

				curveSetCollector.add(curveSet);

			}
		}

		ListenableFuture<HazardResult> futureResult = toHazardResult(curveSetCollector,
			modelCurves, site, executor);
		
		return futureResult.get();

	}

}