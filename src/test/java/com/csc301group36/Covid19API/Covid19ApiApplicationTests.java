package com.csc301group36.Covid19API;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

		for(CSVRecord record : records) {
			System.out.println(record);
			break;
		}
		for(CSVRecord record : records) {
			System.out.println(record);
		}
//		Collection<String> headers = temp.get(0).toMap().keySet();
//
//
//		CSVFormat format2 = CSVFormat.Builder.create(CSVFormat.DEFAULT)
//				.setHeader(headers.toArray(new String[0]))
//				.setIgnoreHeaderCase(true)
//				.setDelimiter(',')
//				.build();
//
//		StringWriter w = new StringWriter();
//		CSVPrinter p = new CSVPrinter(w, format2);
//		p.printRecords(records);
//		p.flush();
//		p.close();
//
//		System.out.println(w.toString());

	}

}
