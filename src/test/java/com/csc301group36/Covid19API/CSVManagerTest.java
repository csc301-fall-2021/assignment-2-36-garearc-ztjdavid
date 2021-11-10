package com.csc301group36.Covid19API;

import com.csc301group36.Covid19API.Utils.CSVManager;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CSVManagerTest {
    @Autowired
    private CSVManager csvManager;

    @Test
    public void testGetRecords()throws Exception{
        String csv = "A,B,C\n1,2,3\n4,5,6";
        List<CSVRecord> records = csvManager.getRecords(csv);
        assertThat(records.size()).isEqualTo(2);
        assertThat(records.get(0).get("A")).isEqualTo("1");
        assertThat(records.get(0).get("B")).isEqualTo("2");
        assertThat(records.get(0).get("C")).isEqualTo("3");
    }

    @Test
    public void testGetHeaders()throws Exception{
        String csv = "A,B,C\n1,2,3\n4,5,6";
        List<CSVRecord> records = csvManager.getRecords(csv);
        Collection<String> headers = csvManager.getHeaders(records);
        assertThat(headers.size()).isEqualTo(3);
        assertThat(headers).containsAll(Arrays.asList("A","B","C"));
    }
}
