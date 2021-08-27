package org.javaloong.kongmink.petclinic.visits.ds.impl.web;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.javaloong.kongmink.petclinic.visits.ds.impl.service.VisitServiceImpl;
import org.javaloong.kongmink.petclinic.visits.model.Visit;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

@DBUnit(mergeDataSets = true)
@DataSet(value = {"visitData.xml"})
public class VisitResourceIT extends WebResourceTestSupport {

    @ClassRule
    public static JaxrsServerProvider<VisitResource> server = JaxrsServerProvider
            .jaxrsServer(VisitResource.class, () -> {
                VisitServiceImpl visitService = new VisitServiceImpl();
                visitService.setJpaTemplate(jpaTemplateSpy());
                VisitResource resource = new VisitResource();
                resource.setVisitService(visitService);
                return resource;
            })
            .withProvider(jacksonJsonProvider())
            .withProvider(validationExceptionMapper());

    @Test
    @DataSet(transactional = true)
    public void addVisit_ShouldReturnValidationErrors() {
        Visit visit = new Visit();
        visit.setDate(new Date());

        Response response = target(server.baseUrl())
                .path("/visits")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(visit));

        assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @DataSet(transactional = true)
    @ExpectedDataSet("createVisitDataExpected.xml")
    public void addVisit_ShouldAddVisitAndReturnHttpStatusCreated() throws Exception {
        Visit visit = new Visit();
        visit.setDate(parseDate("2020-01-01"));
        visit.setDescription("hello");
        visit.setPetId(1);

        Response response = target(server.baseUrl())
                .path("/visits")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(visit));

        assertThat(response.getStatus()).isEqualTo(Status.CREATED.getStatusCode());
    }

    @Test
    public void getVisits_VisitsFound_ShouldReturnFoundVisits() {
        Response response = target(server.baseUrl())
                .path("/visits")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
        Collection<Visit> visits = response.readEntity(new GenericType<Collection<Visit>>() {});
        assertThat(visits).hasSize(3);
    }

    private Date parseDate(String str) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.parse(str);
    }
}
