package org.codeforamerica.shiba.application;

import org.codeforamerica.shiba.County;
import org.codeforamerica.shiba.MonitoringService;
import org.codeforamerica.shiba.application.parsers.ApplicationDataParser;
import org.codeforamerica.shiba.pages.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codeforamerica.shiba.County.Hennepin;
import static org.mockito.Mockito.*;

class ApplicationFactoryTest {

    Clock clock = mock(Clock.class);

    @SuppressWarnings("unchecked")
    ApplicationDataParser<County> countyParser = mock(ApplicationDataParser.class);
    MonitoringService monitoringService = mock(MonitoringService.class);
    ApplicationFactory applicationFactory = new ApplicationFactory(clock, countyParser, monitoringService);
    ApplicationData applicationData = new ApplicationData();
    ZoneOffset zoneOffset = ZoneOffset.UTC;
    PagesData pagesData;

    @BeforeEach
    void setUp() {
        pagesData = new PagesData();
        PageData homeAddress = new PageData();
        homeAddress.put("zipCode", InputData.builder().value(List.of("something")).build());
        pagesData.put("homeAddress", homeAddress);
        applicationData.setPagesData(pagesData);
        Subworkflows subworkflows = new Subworkflows();
        subworkflows.addIteration("someGroup", new PagesData(Map.of("somePage", new PageData(Map.of("someInput", InputData.builder().value(List.of("someValue")).build())))));
        applicationData.setSubworkflows(subworkflows);
        applicationData.setFlow(FlowType.FULL);
        applicationData.setStartTimeOnce(Instant.EPOCH);

        when(clock.instant()).thenReturn(Instant.now());
        when(clock.getZone()).thenReturn(zoneOffset);
    }

    @Test
    void shouldObtainACopyOfTheApplicationData() {
        Application application = applicationFactory.newApplication("", applicationData);

        assertThat(application.getApplicationData()).isNotSameAs(applicationData);
        assertThat(application.getApplicationData()).isEqualTo(applicationData);
    }

    @Test
    void shouldProvideCompletedAtTimestamp() {
        Instant instant = Instant.ofEpochSecond(125423L);
        when(clock.instant()).thenReturn(instant);

        Application application = applicationFactory.newApplication("", applicationData);

        assertThat(application.getCompletedAt()).isEqualTo(ZonedDateTime.ofInstant(instant, zoneOffset));
    }

    @Test
    void shouldProvideTimeToComplete() {
        Instant now = Instant.now();
        when(clock.instant()).thenReturn(now);
        applicationData.setStartTime(now.minusSeconds(142));

        Application application = applicationFactory.newApplication("", applicationData);

        assertThat(application.getTimeToComplete()).isEqualTo(Duration.ofSeconds(142));
    }

    @Test
    void shouldProvideApplicationFlow() {
        FlowType flow = FlowType.FULL;
        applicationData.setFlow(flow);

        Application application = applicationFactory.newApplication("", applicationData);

        assertThat(application.getFlow()).isEqualTo(flow);
    }

    @Test
    void shouldParseCounty() {
        when(countyParser.parse(applicationData)).thenReturn(Hennepin);

        Application application = applicationFactory.newApplication("", applicationData);

        assertThat(application.getCounty()).isEqualTo(Hennepin);
    }

    @Test
    void shouldSendApplicationIdToMonitoringService() {
        applicationFactory.newApplication("appId", applicationData);

        verify(monitoringService).setApplicationId("appId");
    }
}