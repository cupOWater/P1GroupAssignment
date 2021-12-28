import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.time.*;
import java.time.format.*;
import java.util.ArrayList;  

public class Data {
    ArrayList<LocalDate> range = new ArrayList<>();
    ArrayList<LocalDate> date = new ArrayList<>();
    ArrayList<Integer> newCase = new ArrayList<>();
    ArrayList<Integer> newDeath = new ArrayList<>();
    ArrayList<Integer> peopleVaccinated = new ArrayList<>();
    DateTimeFormatter df = DateTimeFormatter.ofPattern("M/d/yyyy");
    LocalDate startDate;
    LocalDate endDate;
    Scanner sc = new Scanner(System.in);

    public Data(String filePath) throws IOException {
        readData(filePath);
        this.startDate = date.get(0);
        this.endDate = date.get(date.size() - 1);
        getDateRange();
    }

    private void readData(String filePath) throws IOException {
        BufferedReader csvReader;

        System.out.println("-------------------");
        System.out.println("Enter name of country or continent: ");
        String location = sc.nextLine();
        Main.checkExit(location);

        csvReader = new BufferedReader(new FileReader(filePath));
        boolean found = false;
        int currentVacc = 0;
        String row = csvReader.readLine();


        while(row != null){
            String[] rawDat = row.split(",");

            if (rawDat[2].equals(location)){
                found = true;
                date.add(LocalDate.parse(rawDat[3], df));

                try {
                    newCase.add(Integer.parseInt(rawDat[4]));
                }
                catch (NumberFormatException e){
                    newCase.add(0);
                }

                try {
                    newDeath.add(Integer.parseInt(rawDat[5]));
                }
                catch (NumberFormatException e){
                    newDeath.add(0);
                }

                if(!rawDat[6].equals("")){
                    currentVacc = Integer.parseInt(rawDat[6]);
                }
                peopleVaccinated.add(currentVacc);
            }
            row = csvReader.readLine();
        }

        if (!found){
            System.out.println("Country/ Continent not found");
            readData(filePath);
        }
        csvReader.close();
    }

    private void getDateRange(){

        System.out.println("-------------------");
        System.out.printf("""
                Enter date range options (%s - %s) format MM/DD/YYYY:
                \t1. Choosing 2 dates
                \t2. Choosing number of days/ weeks from a date
                \t3. Choosing number of days/ weeks to a date
                >>>\040""", startDate.format(df), endDate.format(df));
        String choice = sc.nextLine();
        Main.checkExit(choice);

        switch (choice) {
            case "1" ->
                    {
                        System.out.println("-------------------");
                        System.out.print("Start date: ");
                        LocalDate startDate = inputDate();

                        System.out.println("-------------------");
                        System.out.print("End date: ");
                        LocalDate endDate = inputDate();
                        if (startDate.isAfter(endDate)){
                            System.out.println("Start date is after end date");
                            getDateRange();
                        }
                        setDateRange(startDate, endDate);
                    }
            case "2" -> fromDate();
            case "3" -> toDate();
            default -> {
                System.out.println("Invalid choice");
                getDateRange();
            }
        }

    }

    private void setDateRange(LocalDate start, LocalDate end){
        while (!start.equals(end.plusDays(1))){
            range.add(start);
            start = start.plusDays(1);
        }
    }

    private void fromDate(){
        System.out.println("-------------------");
        System.out.print("Set date:");
        LocalDate setDate = inputDate();
        LocalDate nextDate = null;
        System.out.print("""
                1. Day
                2. Week
                >>>\040""");
        String  opt = sc.nextLine();
        Main.checkExit(opt);

        System.out.println("-------------------");
        System.out.print("Number of days/ weeks from date: ");
        int span = sc.nextInt();

        if (opt.equals("1")){
            nextDate = setDate.plusDays(span);
        }else if (opt.equals("2")){
            nextDate = setDate.plusWeeks(span);
        }else{
            System.out.println("Invalid choice");
            getDateRange();
        }

        if(nextDate != null && validDateRange(nextDate)){
            setDateRange(setDate, nextDate);
        }
    }

    private void toDate(){
        System.out.println("-------------------");
        System.out.print("Set date: ");
        LocalDate nextDate = inputDate();
        LocalDate currentDate = null;
        System.out.print("""
                1. Day
                2. Week
                >>>\040""");
        String  opt = sc.nextLine();
        Main.checkExit(opt);

        System.out.println("-------------------");
        System.out.print("Number of days/ weeks to date: ");
        int span = sc.nextInt();

        if (opt.equals("1")){
            currentDate = nextDate.plusDays(-span);
        }else if (opt.equals("2")){
            currentDate = nextDate.plusWeeks(-span);
        }else{
            System.out.println("Invalid choice");
            getDateRange();
        }

        if(currentDate != null && validDateRange(currentDate)){
            setDateRange(currentDate, nextDate);
        }
    }

    private LocalDate inputDate(){
        LocalDate userInput = LocalDate.parse(sc.nextLine(), df);
        if(!validDateRange(userInput)){
            System.out.print("Date out of range, re-enter date: ");
            inputDate();
        }
        return userInput;
    }

    private boolean validDateRange(LocalDate date){
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}
