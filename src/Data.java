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
    DateTimeFormatter df = DateTimeFormatter.ofPattern("M/d/yyyy"); // date format for reading file
    DateTimeFormatter dfInput = DateTimeFormatter.ofPattern("MM/dd/yyyy"); // date format for user input

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
        boolean found = false;
        while (true){
            BufferedReader csvReader;
            csvReader = new BufferedReader(new FileReader(filePath));
            System.out.println("-------------------");
            System.out.print("Enter name of country or continent: ");
            String location = formatLocationInput(sc.nextLine());
            Main.checkExit(location);
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
            if (found){
                csvReader.close();
                break;
            } else{
                System.out.println("Country not found, please try again.");
            }
        }
    }

    private String formatLocationInput(String location){
        location = location.trim();
        String[] tokens = location.split(" ");
        String formatted = "";
        for (String i : tokens){
            String first = i.substring(0, 1);
            String remain = i.substring(1);
            formatted += first.toUpperCase() + remain.toLowerCase() + " ";
        }
        return formatted.trim();
    }

    private void getDateRange(){

        System.out.println("-------------------");
        System.out.printf("""
                Enter date range options (%s - %s) format MM/DD/YYYY:
                \t1) Choosing 2 dates
                \t2) Choosing number of days/ weeks from a date
                \t3) Choosing number of days/ weeks to a date
                >>>\040""", startDate.format(dfInput), endDate.format(dfInput));


        label:
        while (true){
            String choice = sc.nextLine();
            choice = choice.trim();
            Main.checkExit(choice);
            switch (choice) {
                case "1":
                    System.out.println("-------------------");
                    System.out.print("Start date (MM/DD/YYYY): ");
                    LocalDate startDate = inputDate();

                    System.out.println("-------------------");
                    System.out.print("End date (MM/DD/YYYY): ");
                    LocalDate endDate = inputDate();
                    while (startDate.isAfter(endDate)) {
                        System.out.print("Start date is after end date, re-enter date (MM/DD/YYYY): ");
                        endDate = inputDate();
                    }
                    setDateRange(startDate, endDate);
                    break label;
                case "2":
                    fromDate();
                    break label;
                case "3":
                    toDate();
                    break label;
                default:
                    System.out.print("Invalid choice, re-enter choice: ");
                    break;
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
        System.out.print("Set date (MM/DD/YYYY): ");
        LocalDate setDate = inputDate();
        LocalDate nextDate;
        String opt = getDayOrWeek();

        System.out.println("-------------------");
        System.out.print("Number of days/ weeks from date: ");

        int span = numberInput();
        while(true){
            if (opt.equals("1")){
                nextDate = setDate.plusDays(span);
            }else{
                nextDate = setDate.plusWeeks(span);
            }
            if(validDateRange(nextDate)){
                break;
            }
            System.out.print("Out of range, re-enter number of days/ weeks from date: ");
            span = numberInput();
        }
        setDateRange(setDate, nextDate);
    }

    private void toDate(){
        System.out.println("-------------------");
        System.out.print("Set date (MM/DD/YYYY): ");
        LocalDate nextDate = inputDate();
        LocalDate currentDate;
        String opt = getDayOrWeek();

        System.out.println("-------------------");
        System.out.print("Number of days/ weeks to date: ");

        int span = numberInput();
        while(true){
            if (opt.equals("1")){
                currentDate = nextDate.plusDays(-span);
            }else {
                currentDate = nextDate.plusWeeks(-span);
            }
            if(validDateRange(currentDate)){
                break;
            }
            System.out.print("Out of range, re-enter number of days/ weeks to date: ");
            span = numberInput();
        }

        setDateRange(currentDate, nextDate);

    }

    private String getDayOrWeek(){
        System.out.print("""
                1) Day
                2) Week
                >>>\040""");
        String  opt = sc.nextLine();
        opt = opt.trim();
        Main.checkExit(opt);

        while (!opt.equals("1") && !opt.equals("2")){
            System.out.print("""
                    Invalid choice, re-enter value
                    >>>\040""");
            opt = sc.nextLine();
            opt = opt.trim();
            Main.checkExit(opt);
        }

        return opt;
    }

    private LocalDate inputDate(){

        String userInputString;
        LocalDate userInput = null;

        boolean valid = false;

        while (!valid){
            userInputString = sc.nextLine();
            userInputString = userInputString.trim();
            try {
                userInput = LocalDate.parse(userInputString, df);
            }catch (DateTimeParseException e){
                System.out.print("Invalid date input, re-enter date (MM/DD/YYYY): ");
                continue;
            }
            if(!userInputString.equals(userInput.format(dfInput))){
                System.out.print("Invalid date input or wrong format, re-enter date (MM/DD/YYYY): ");
                continue;
            }
            if(!validDateRange(userInput)){
                System.out.print("Date out of range, re-enter date (MM/DD/YYYY): ");
                continue;
            }
            valid = true;
        }

        return userInput;
    }

    private boolean validDateRange(LocalDate date){
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    private Integer numberInput() {
        Scanner sc = new Scanner(System.in);
        String str = "";
        boolean flag = false;

        while (!flag) {
            str = sc.nextLine();
            str = str.trim();

            try {
                Integer.parseInt(str);
                flag = true;
            } catch (Exception e) {
                System.out.print("Your input is not a number, please try again: ");
            }
            if (Integer.parseInt(str) < 1){
                System.out.print("Number of day or week can not less than 1, please try again: ");
                flag = false;
            }
        }
        return Integer.parseInt(str);
    }
}
