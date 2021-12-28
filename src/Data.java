import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.time.*;
import java.time.format.*;
import java.util.ArrayList;  

public class Data {
    ArrayList<LocalDate> range = new ArrayList<LocalDate>();
    ArrayList<LocalDate> date = new ArrayList<LocalDate>();
    ArrayList<Integer> newCase = new ArrayList<Integer>();
    ArrayList<Integer> newDeath = new ArrayList<Integer>();
    ArrayList<Integer> peopleVacinated = new ArrayList<Integer>();

    public static Data selectNewData(String filePath) throws IOException {
        Data newData = new Data();
        Scanner scanner = new Scanner(System.in);

        readData(newData, filePath);

        return newData;
    }

    static void readData(Data data, String filePath) throws IOException {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("M/d/yyyy");
        Scanner locInput = new Scanner(System.in);
        BufferedReader csvReader;

        System.out.println("Enter name of country or continent: ");
        String location = locInput.nextLine();

        csvReader = new BufferedReader(new FileReader(filePath));
        boolean found = false;
        int currentVacc = 0;
        String row = csvReader.readLine();


        while(row != null){
            String[] rawDat = row.split(",");

            if (rawDat[2].equals(location)){
                found = true;
                data.date.add(LocalDate.parse(rawDat[3], df));

                try {
                    data.newCase.add(Integer.parseInt(rawDat[4]));
                }
                catch (NumberFormatException e){
                    data.newCase.add(0);
                }

                try {
                    data.newDeath.add(Integer.parseInt(rawDat[5]));
                }
                catch (NumberFormatException e){
                    data.newDeath.add(0);
                }

                if(!rawDat[6].equals("")){
                    currentVacc = Integer.parseInt(rawDat[6]);
                }
                data.peopleVacinated.add(currentVacc);
            }
            row = csvReader.readLine();
        }

        if (!found){
            System.out.println("Country/ Continent not found");
        }
        csvReader.close();
    }

}

//abstract class DataReader {
//    // Data object that needed to be written into
//    Data data;
//
//    static String filePath = "covid-data.csv";
//    static DateTimeFormatter df = DateTimeFormatter.ofPattern("M/d/yyyy");
//
//    Scanner locInput = new Scanner(System.in);
//    BufferedReader csvReader;
//
//    abstract void readData(Data data) throws IOException;
//
//}
//
//class ContinentReader extends DataReader{
//
//    @Override
//    void readData(Data data) throws IOException {
//        System.out.println("Select a continent:");
//        String continent = locInput.nextLine();
//        csvReader = new BufferedReader(new FileReader(filePath));
//
//        while (csvReader.readLine() != null){
//            String[] rawDat = csvReader.readLine().split(",");
//            if (rawDat[1].equals(continent)){
//
//            }
//        }
//        csvReader.close();
//    }
//}
//
//class CountryReader extends DataReader{
//
//    @Override
//    void readData(Data data) throws IOException {
//        System.out.println("Select a country:");
//        String country = locInput.nextLine();
//        csvReader = new BufferedReader(new FileReader(filePath));
//        boolean found = false;
//        int currentVacc = 0;
//
//        String row = csvReader.readLine();
//        while(row != null){
//            String[] rawDat = row.split(",");
//
//            if (rawDat[2].equals(country)){
//                found = true;
//                data.date.add(LocalDate.parse(rawDat[3], df));
//
//                try {
//                    data.newCase.add(Integer.parseInt(rawDat[4]));
//                }
//                catch (NumberFormatException e){
//                    data.newCase.add(0);
//                }
//
//                try {
//                    data.newDeath.add(Integer.parseInt(rawDat[5]));
//                }
//                catch (NumberFormatException e){
//                    data.newDeath.add(0);
//                }
//
//                if(!rawDat[6].equals("")){
//                    currentVacc = Integer.parseInt(rawDat[6]);
//                }
//                data.peopleVacinated.add(currentVacc);
//            }
//            row = csvReader.readLine();
//        }
//
//        if (!found){
//            System.out.println("Country not found");
//        }
//        csvReader.close();
//    }
//}