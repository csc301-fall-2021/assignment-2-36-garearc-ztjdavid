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
public class DailyReportsControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testUpload() throws Exception{
        String url = "http://localhost:" + port + "/api/DailyReports/upload/1999-01-01";
        String data = "FIPS,Admin2,Province_State,Country_Region,Last_Update,Lat,Long_,Confirmed,Deaths,Recovered,Active,Combined_Key,Incidence_Rate,Case-Fatality_Ratio\n" +
                "45001,Abbeville,South Carolina,US,2020-06-06 02:33:00,34.22333378,-82.46170658,47,0,0,47,\"Abbeville, South Carolina, US\",191.625555510254,1.0\n" +
                "22001,Acadia,Louisiana,US,2020-06-06 02:33:00,30.2950649,-92.41419698,467,26,0,441,\"Acadia, Louisiana, US\",752.6795068095737,5.56745182012848";
        assertThat(this.restTemplate.postForObject(url, data, Response.class)).hasFieldOrPropertyWithValue("isCompleted", true);
    }

    @Test
    public void testWrongUpload() throws Exception{
        String url = "http://localhost:" + port + "/api/DailyReports/upload/1999-01-01";
        String data = "FIPS,Admin2,Province_State,Country_Region,Last_Update,Lat,Long_,Confirmed,Deaths,Recovered,Active,Combined_Key,Incidence_Rate,Case-Fatality_Ratio\n" +
                "45001,Abbeville,South Carolina,US,2020-06-06 02:33:00,34.22333378,-82.46170658,47,A,0,47,\"Abbeville, South Carolina, US\",191.625555510254,1.0";
        assertThat(this.restTemplate.postForObject(url, data, Response.class)).hasFieldOrPropertyWithValue("isCompleted", false);
    }

    @Test
    public void testQuery() throws Exception{
        String url = "http://localhost:" + port + "/api/DailyReports/query/json/death";
        ReqBody rb = new ReqBody();
        rb.setCountry("US");
        rb.setStartDate("1999-01-01");
        rb.setEndDate("1999-01-01");
        List<Map<String, String>> qr = (List<Map<String, String>>)this.restTemplate.postForObject(url, rb, Object.class);
        assertThat(qr.size()).isEqualTo(2);
        assertThat(qr.get(0).get("Deaths")).isEqualTo("0");
        assertThat(qr.get(1).get("Deaths")).isEqualTo("26");
        assertThat(qr.get(0).containsKey("Confirmed")).isFalse();
    }
}
