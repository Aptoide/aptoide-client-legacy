/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 06/05/2016.
 */

package com.aptoide.amethyst.analytics;

import com.seatgeek.sixpack.ConversionError;
import com.seatgeek.sixpack.Experiment;
import com.seatgeek.sixpack.ParticipatingExperiment;

import java.util.concurrent.ExecutorService;

public class ABTest<T> {

	private final ExecutorService executorService;
	private final Experiment experiment;
	private final AlternativeParser<T> alternativeParser;
	private ParticipatingExperiment participatingExperiment;

	public ABTest(ExecutorService executorService, Experiment experiment, AlternativeParser<T>
			alternativeParser) {
		this.executorService = executorService;
		this.experiment = experiment;
		this.alternativeParser = alternativeParser;
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
		if (isParticipating()) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					try {
						participatingExperiment.convert();
					} catch (ConversionError error) {
						error.printStackTrace();
					}
				}
			});
		}
	}

	public T alternative() {
		if (isParticipating()) {
			return alternativeParser.parse(participatingExperiment.selectedAlternative.name);
		} else {
			return alternativeParser.parse(experiment.getControlAlternative().name);
		}
	}

	private boolean isParticipating() {
		return participatingExperiment != null;
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

		return getName().equals(abTest.getName());
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}
}