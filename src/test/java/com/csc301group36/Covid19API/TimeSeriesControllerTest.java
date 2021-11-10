package com.csc301group36.Covid19API;

import com.csc301group36.Covid19API.Entities.ReqBody;
import com.csc301group36.Covid19API.Entities.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TimeSeriesControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testOverwrite() throws Exception{
        String url = "http://localhost:" + port + "/api/timeSeries/overwrite/death";
        String data = "Province/State,Country/Region,Lat,Long,1/22/20,1/23/20\n" +
                "Australian Capital Territory,Australia,-35.4735,149.0124,1,0\n" +
                "\"\",Austria,47.5162,14.5501,0,300";
        assertThat(this.restTemplate.postForObject(url, data, Response.class)).hasFieldOrPropertyWithValue("isCompleted", true);
    }

    @Test
    public void testUpdate() throws Exception{
        String url = "http://localhost:" + port + "/api/timeSeries/update/death";
        String data = "Province/State,Country/Region,Lat,Long,1/22/20,1/23/20\n" +
                "Australian Capital Territory,Australia,-35.4735,149.0124,1,0\n" +
                "\"\",Austria,47.5162,14.5501,0,200";
        assertThat(this.restTemplate.postForObject(url, data, Response.class)).hasFieldOrPropertyWithValue("isCompleted", true);
    }

    @Test
    public void testUpdateWrongField() throws Exception{
        String url = "http://localhost:" + port + "/api/timeSeries/update/death";
        String data = "Province/State,Country/Region,Lat,Long,1/22/20,1/23/20,1/24/20\n" +
                "Australian Capital Territory,Australia,-35.4735,149.0124,1,0\n" +
                "\"\",Austria,47.5162,14.5501,0,200,0";
        assertThat(this.restTemplate.postForObject(url, data, Response.class)).hasFieldOrPropertyWithValue("isCompleted", false);
    }

    @Test
    public void testUpdateWrongType() throws Exception{
        String url = "http://localhost:" + port + "/api/timeSeries/update/death";
        String data = "Province/State,Country/Region,Lat,Long,1/22/20,1/23/20\n" +
                "Australian Capital Territory,Australia,-35.4735,149.0124,1,0\n" +
                "\"\",Austria,47.5162,14.5501,A,200";
        assertThat(this.restTemplate.postForObject(url, data, Response.class)).hasFieldOrPropertyWithValue("isCompleted", false);
    }

    @Test
    public void testQuery() throws Exception{
        String url = "http://localhost:" + port + "/api/timeSeries/query/json/death?country=Austria&startDate=1/22/20&endDate=1/23/20";
        List<Map<String, String>> qr = (List<Map<String, String>>)this.restTemplate.getForObject(url, Object.class);
        assertThat(qr.size()).isEqualTo(1);
        assertThat(qr.get(0).get("1/22/20")).isEqualTo("0");
        assertThat(qr.get(0).get("1/23/20")).isEqualTo("200");
    }
}
