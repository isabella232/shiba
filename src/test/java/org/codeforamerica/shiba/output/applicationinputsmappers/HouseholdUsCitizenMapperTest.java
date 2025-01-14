package org.codeforamerica.shiba.output.applicationinputsmappers;

import org.codeforamerica.shiba.application.Application;
import org.codeforamerica.shiba.output.ApplicationInput;
import org.codeforamerica.shiba.output.ApplicationInputType;
import org.codeforamerica.shiba.pages.data.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HouseholdUsCitizenMapperTest {
    HouseholdUsCitizenMapper mapper = new HouseholdUsCitizenMapper();

    @Test
    void shouldParseTrueOrFalseForHouseholdMemberUsCitizenship() {
        ApplicationData applicationData = new ApplicationData();
        PagesData pagesData = new PagesData();
        PagesData householdMember = new PagesData();
        applicationData.getSubworkflows().addIteration("household", householdMember);
        UUID householdMemberID = applicationData.getSubworkflows().get("household").get(0).getId();


        PageData whoIsUsCitizenPage = new PageData();
        whoIsUsCitizenPage.put("whoIsNonCitizen", InputData.builder()
                .value(List.of("personAFirstName personALastName applicant", "personBFirstName personBLastName " + householdMemberID))
                .build());
        pagesData.put("whoIsNonCitizen", whoIsUsCitizenPage);
        applicationData.setPagesData(pagesData);

        List<ApplicationInput> result = mapper.map(Application.builder()
                .applicationData(applicationData)
                .build(), null, null);

        assertThat(result).isEqualTo(List.of(
                new ApplicationInput(
                        "usCitizen",
                        "isUsCitizen",
                        List.of("false"),
                        ApplicationInputType.SINGLE_VALUE,
                        null
                ),
                new ApplicationInput(
                        "usCitizen",
                        "isUsCitizen",
                        List.of("false"),
                        ApplicationInputType.SINGLE_VALUE,
                        0
                )
        ));
    }
}