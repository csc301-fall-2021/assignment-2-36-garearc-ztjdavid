package com.csc301group36.Covid19API;

import com.csc301group36.Covid19API.Entities.QueryResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.*;

@SpringBootTest
class Covid19ApiApplicationTests {

	@Test
	void contextLoads() throws Exception {
		File f = new File("csv/test.csv");
		Reader r = new FileReader(f);
		CSVFormat format = CSVFormat.Builder.create(CSVFormat.DEFAULT)
				.setHeader()
				.setDelimiter(',')
				.build();

		Iterable<CSVRecord> records = format.parse(r);
		List<CSVRecord> recordList = new ArrayList<>();
		records.forEach(recordList::add);


		List<Map<String, String>> queryResult = new ArrayList<>();
		for(CSVRecord record : recordList){
			queryResult.add(record.toMap());
		}
		System.out.println(queryResult);
	}

}
