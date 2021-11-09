package com.csc301group36.Covid19API;

import com.csc301group36.Covid19API.Utils.DBManager;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

@SpringBootTest
public class DBManagerTest {
    @Autowired
    private DBManager dbManager;

    @Test
    public void checkInitDirectories(){
        File dirTimeSeries = new File("timeSeries");
        File dirDailyReports = new File("dailyReports");
        Assertions.assertThat(dirDailyReports.exists()).as("Check dailyReports folder.").isTrue();
        Assertions.assertThat(dirTimeSeries.exists()).as("Check timeSeries folder.").isTrue();
    }

    @Test
    public void writeToFile() throws Exception{
        File testFile = new File("test.csv");
        testFile.createNewFile();
        dbManager.writeContentToFile("test\n", testFile.getPath());
        BufferedReader reader = new BufferedReader(new FileReader(testFile));
        Assertions.assertThat(reader.readLine()).as("Test writing to a csv file.").isEqualTo("test\n");
        reader.close();
        testFile.delete();
    }
}
