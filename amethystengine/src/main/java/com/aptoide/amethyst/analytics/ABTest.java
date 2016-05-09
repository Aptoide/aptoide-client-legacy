/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 06/05/2016.
 */

package com.aptoide.amethyst.analytics;

import com.seatgeek.sixpack.Experiment;
import com.seatgeek.sixpack.ParticipatingExperiment;

import java.util.concurrent.ExecutorService;

public class ABTest<T> {

	private final ExecutorService executorService;
	private final Experiment experiment;
	private final AlternativeConverter<T> alternativeConverter;
	private ParticipatingExperiment participatingExperiment;

	public ABTest(ExecutorService executorService, Experiment experiment, AlternativeConverter<T> alternativeConverter) {
		this.executorService = executorService;
		this.experiment = experiment;
		this.alternativeConverter = alternativeConverter;
	}

	public String getName() {
		return experiment.name;
	}

	public void participate() {
		if (participatingExperiment == null) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					participatingExperiment = experiment.participate();
				}
			});
		}
	}

	public void convert() {
		if (participatingExperiment != null) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					participatingExperiment.convert();
				}
			});
		} else {
			throw new IllegalStateException("Must participate first.");
		}
	}

	public T alternative() {
		if (participatingExperiment != null) {
			return alternativeConverter.convert(participatingExperiment.selectedAlternative.name);
		} else {
			throw new IllegalStateException("Must participate first.");
		}
	}

	public void prefetch() {
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				experiment.prefetch();
			}
		});
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ABTest<?> abTest = (ABTest<?>) o;

		return experiment.name.equals(abTest.experiment.name);
	}

	@Override
	public int hashCode() {
		return experiment.name.hashCode();
	}
}