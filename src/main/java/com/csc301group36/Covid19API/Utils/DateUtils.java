package com.csc301group36.Covid19API.Utils;

import com.csc301group36.Covid19API.Entities.DBType;
import com.csc301group36.Covid19API.Exceptions.InternalError;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DateUtils {
    public Date stringToDate(String date, DBType dbType) throws InternalError {
        try{
            if(dbType == DBType.DailyReports){
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                return formatter.parse(date);
            }
            else{
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
                return formatter.parse(date);
            }
        }catch (ParseException e){
            throw new InternalError("We found a date time with invalid format: " + date);
        }
    }

    public boolean isInBetween(Date start, Date end, Date target){
        return target.before(end) && target.after(start);
    }

    public boolean isValidDate(String d, DBType dbType){
        try{
            stringToDate(d, dbType);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
