package data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataGenerator {
    public static String getCorrectYear() {
        LocalDate date = LocalDate.now().plusYears(2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy");
        return date.format(formatter);
    }

    public static String getWrongYear() {
        LocalDate date = LocalDate.now().minusYears(2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy");
        return date.format(formatter);
    }

    public static String getRandomCvc() {
        String[] cvcOptions = {"123", "999", "985", "015", "888", "656", "001", "234", "601", "111"};
        int chooseCvc =(int) (Math.random()*cvcOptions.length);
        return cvcOptions[chooseCvc];
    }
}
