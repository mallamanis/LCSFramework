package gr.auth.ee.lcs.calibration;

import static org.junit.Assert.*;
import gr.auth.ee.lcs.AbstractLearningClassifierSystem;
import gr.auth.ee.lcs.data.IClassificationStrategy;
import gr.auth.ee.lcs.data.ILCSMetric;
import static org.easymock.EasyMock.*;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;

import org.junit.Before;
import org.junit.Test;

public class InternalValidationTest extends EasyMockSupport {

	InternalValidation ival;
	AbstractLearningClassifierSystem mockLcs;
	IClassificationStrategy mockClassificationStrategy;
	ILCSMetric mockMetric;

	@Before
	public void setUp() throws Exception {
		mockLcs = createMock(AbstractLearningClassifierSystem.class);
		mockClassificationStrategy = createMock(IClassificationStrategy.class);
		mockMetric = createMock(ILCSMetric.class);

		ival = new InternalValidation(mockLcs, mockClassificationStrategy,
				mockMetric);
	}

	@Test
	public void testCalibrate() {

		testCalibration(.1017);
		testCalibration(.499);
		testCalibration(.25);
		testCalibration(.29);
		testCalibration(.001);

	}

	/**
	 * @param peakValue
	 */
	public void testCalibration(final double peakValue) {
		resetAll();
		final int numOfIterations = 15;
		final Capture<Double> threshold = new Capture<Double>();
		mockClassificationStrategy.setThreshold(capture(threshold));
		expectLastCall().anyTimes();
		expect(mockMetric.getMetric(mockLcs)).andAnswer(new IAnswer<Double>() {
			public Double answer() {
				final double thr = threshold.getValue();
				return Math.exp(-Math.abs(thr - peakValue));
			}
		}).anyTimes();

		replayAll();
		ival.calibrate(numOfIterations);

		assertTrue(Math.abs(threshold.getValue() - peakValue) < .001);
		verifyAll();
	}

}
