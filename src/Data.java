import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.time.*;
import java.time.format.*;
import java.util.ArrayList;

public class Data {
    // Set of variables required for Summary class
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

    // Constructor required a file path to create a Data object
    public Data(String filePath) throws IOException {
        readData(filePath);
        this.startDate = date.get(0);
        this.endDate = date.get(date.size() - 1);
        fillMissingDate(); // fill in the missing dates after getting all the dates from .csv
        getDateRange();
    }

    private void readData(String filePath) throws IOException {
        boolean found = false;
        // Loop until exit check or until a country/ continent is found
        while (true){
            BufferedReader csvReader;
            csvReader = new BufferedReader(new FileReader(filePath));
            System.out.println("-------------------");
            System.out.print("Enter name of country or continent: ");
            String location = formatLocationInput(sc.nextLine());
            Main.checkExit(location);
            int currentVacc = 0; // Vaccination value is accumulated, so the initial value should be 0

            String row = csvReader.readLine();
            // While not end of file
            // Read data from .csv file and add the appropriate column data to the right variable

            while(row != null){
                String[] rawDat = row.split(",");

                if (rawDat[2].equals(location)){
                    found = true;
                    date.add(LocalDate.parse(rawDat[3], df));

                    // Math.max here to handle negative values, since negative is smaller than 0, return 0
                    try {
                        newCase.add(Math.max(Integer.parseInt(rawDat[4]), 0));
                    }
                    catch (NumberFormatException e){
                        newCase.add(0);
                    }

                    try {
                        newDeath.add(Math.max(Integer.parseInt(rawDat[5]), 0));
                    }
                    catch (NumberFormatException e){
                        newDeath.add(0);
                    }

                    // If a new vaccinated value is encountered, it will be the new currentVacc
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
        // This function is for formatting user's location input
        // It returns same string with first character of each letter capitalized and the rest lowercase
        location = location.trim(); // remove any extra white spaces
        String[] tokens = location.split(" ");
        StringBuilder formatted = new StringBuilder();
        for (String i : tokens){
            String first = i.substring(0, 1);
            String remain = i.substring(1);
            formatted.append(first.toUpperCase()).append(remain.toLowerCase()).append(" ");
        }
        return formatted.toString().trim();
    }

    private void fillMissingDate(){
        // To fill in some date gaps in the .csv file

        ArrayList<LocalDate> fullDates = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.equals(endDate.plusDays(1))){
            fullDates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        for (LocalDate fullDate : fullDates){
            int index = fullDates.indexOf(fullDate);
            if(!date.get(index).equals(fullDate)){
                date.add(index, fullDate);
                newCase.add(index, 0);
                newDeath.add(index, 0);
                peopleVaccinated.add(index, peopleVaccinated.get(index - 1)); // get vaccinated value from the day previous
            }
        }

    }

    private void getDateRange(){
        // Function to get the values for the Arraylist range variable

        System.out.println("-------------------");
        System.out.printf("""
                Enter date range options (%s - %s) format MM/DD/YYYY:
                \t1) Choosing 2 dates
                \t2) Choosing number of days/ weeks from a date
                \t3) Choosing number of days/ weeks to a date
                >>>\040""", startDate.format(dfInput), endDate.format(dfInput));


        label: //To break out of while loop if switch case is appropriate
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
                        endDate = inputDate(); // Let user re-enter new end date if it is before start date
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
        // Function to add the dates between 2 dates into variable range
        while (!start.equals(end.plusDays(1))){
            range.add(start);
            start = start.plusDays(1);
        }
    }

    private void fromDate(){
        // Function to get the dates from an initial set date
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
                nextDate = setDate.plusDays(span); //Get the end date after set amount of days
            }else{
                nextDate = setDate.plusWeeks(span);//Get the end date after set amount of weeks
            }
            if(validDateRange(nextDate)){
                break; //If the date range is valid, break loop
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
                currentDate = nextDate.plusDays(-span); //Get the start date after set amount of days to set date
            }else {
                currentDate = nextDate.plusWeeks(-span); //Get the start date after set amount of weeks to set date
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
        // Prompt user to get the option of set days or weeks
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
            Main.checkExit(userInputString);
            try { // Check if input is an appropriate date
                userInput = LocalDate.parse(userInputString, df);
            }catch (DateTimeParseException e){
                System.out.print("Invalid date input, re-enter date (MM/DD/YYYY): ");
                continue;
            }
            if(!userInputString.equals(userInput.format(dfInput))){ // Check if the date inputted is in the appropriate format
                System.out.print("Invalid date input or wrong format, re-enter date (MM/DD/YYYY): ");
                continue;
            }
            if(!validDateRange(userInput)){ // Check if the date is within the date range chosen by location
                System.out.print("Date out of range, re-enter date (MM/DD/YYYY): ");
                continue;
            }
            valid = true;
        }

        return userInput;
    }

    private boolean validDateRange(LocalDate date){
        // Function to check if a date is between the date range of a certain location
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    private Integer numberInput() {
        // Check user input for an integer
        Scanner sc = new Scanner(System.in);
        String str = "";
        boolean flag = false;

        while (!flag) {
            str = sc.nextLine();
            str = str.trim();
            Main.checkExit(str);

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
