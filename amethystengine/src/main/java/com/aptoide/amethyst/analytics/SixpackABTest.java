///*
// * Copyright (c) 2016.
// * Modified by marcelo.benites@aptoide.com on 06/05/2016.
// */
//
//package com.aptoide.amethyst.analytics;
//
//import com.seatgeek.sixpack.ConversionError;
//import com.seatgeek.sixpack.Experiment;
//import com.seatgeek.sixpack.ParticipatingExperiment;
//import com.seatgeek.sixpack.PrefetchedExperiment;
//
//import java.util.concurrent.ExecutorService;
//
//public class SixpackABTest<T> implements ABTest<T> {
//
//	private final ExecutorService executorService;
//	private final Experiment experiment;
//	private final AlternativeParser<T> alternativeParser;
//	private ParticipatingExperiment participatingExperiment;
//	private PrefetchedExperiment prefetchedExperiment;
//
//	public SixpackABTest(ExecutorService executorService, Experiment experiment, AlternativeParser<T>
//			alternativeParser) {
//		this.executorService = executorService;
//		this.experiment = experiment;
//		this.alternativeParser = alternativeParser;
//	}
//
//	@Override
//	public String getName() {
//		return experiment.name;
//	}
//
//	@Override
//	public void participate() {
//		if (!isParticipating()) {
//			executorService.submit(new Runnable() {
//				@Override
//				public void run() {
//					participatingExperiment = experiment.participate();
//				}
//			});
//		}
//	}
//
//	@Override
//	public void convert() {
//		if (isParticipating()) {
//			executorService.submit(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						participatingExperiment.convert();
//					} catch (ConversionError error) {
//						error.printStackTrace();
//					}
//				}
//			});
//		}
//	}
//
//	@Override
//	public T alternative() {
//		if (experiment.hasForcedChoice()) {
//			return alternativeParser.parse(experiment.forcedChoice.name);
//		}
//		if (isParticipating()) {
//			return alternativeParser.parse(participatingExperiment.selectedAlternative.name);
//		} else if (isPrefetched()) {
//			return alternativeParser.parse(prefetchedExperiment.selectedAlternative.name);
//		}
//		return alternativeParser.parse(experiment.getControlAlternative().name);
//	}
//
//	private boolean isParticipating() {
//		return participatingExperiment != null;
//	}
//
//	private boolean isPrefetched() {
//		return prefetchedExperiment != null;
//	}
//
//	@Override
//	public void prefetch() {
//		if (!isPrefetched()) {
//			executorService.submit(new Runnable() {
//				@Override
//				public void run() {
//					prefetchedExperiment = experiment.prefetch();
//				}
//			});
//		}
//	}
//
//	@Override
//	public boolean equals(Object o) {
//		if (this == o) return true;
//		if (o == null || getClass() != o.getClass()) return false;
//
//		ABTest<?> abTest = (ABTest<?>) o;
//
//		return getName().equals(abTest.getName());
//	}
//
//	@Override
//	public int hashCode() {
//		return getName().hashCode();
//	}
//}