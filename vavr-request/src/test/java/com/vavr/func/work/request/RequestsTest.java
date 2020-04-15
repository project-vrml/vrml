package com.vavr.func.work.request;

import com.vavr.func.work.request.config.RequestConfiguration;
import com.vavr.func.work.test.Tests;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * {@link Requests} API test.
 */
public class RequestsTest implements Tests {

    /**
     * Test request check
     */
    @Test
    public void request() {
        Requests<Object> requests = Requests
                .of(() -> {
                    Object scenarioApiResponse = new Object();
                    return scenarioApiResponse;
                })
                .check(scenarioApiResponse -> {
                    if (scenarioApiResponse == null) {
                        throw new IllegalArgumentException("service error");
                    }
                })
                .record(scenarioApiResponse -> new RequestConfiguration.RequestReportValue.ReportBuilder("hotelChurn0Api")
                        .recordValue(String.valueOf(scenarioApiResponse.getClass().getCanonicalName()))
                        .useConfig()
                        .build());

        Assert.assertNotNull(requests);
        Assert.assertTrue(requests.isFailure());
    }

    private static final int MAX_FOR_TEST = 100;

    /**
     * Test record trigger alert when count achieve target
     */
    @Test
    public void record() {
        for (int i = 0; i < MAX_FOR_TEST; i++) {
            if (i == MAX_FOR_TEST - 1) {
                // debug breakpoint
                i = MAX_FOR_TEST - 1;
            }
            int finalI = i;
            Requests
                    .of(() -> {
                        Object scenarioApiResponse = new Object();
                        return scenarioApiResponse;
                    })
                    .record(scenarioApiResponse -> new RequestConfiguration.RequestReportValue.ReportBuilder("default")
                            // 1 or 2
                            .recordValue(String.valueOf((finalI % 2)))
                            .strategy(new RequestConfiguration.RequestReportConfig()
                                    // max(s) is enough
                                    .reportExpiredSeconds(MAX_FOR_TEST)
                                    // max size is enough
                                    .reportPoolMaxSize(MAX_FOR_TEST)
                                    // ÷10 will trigger
                                    .reportTriggerCount(MAX_FOR_TEST / 10)
                                    // expose 1
                                    .noRecordKeys(Collections.singletonList("1"))
                                    // name
                                    .requestReportName("default"))
                            .build());
        }
    }
}