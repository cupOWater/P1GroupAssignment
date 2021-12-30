import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Summary {
    private Group group;
    private Metric metric;
    private ResultType resultType;
    private boolean isAccumulated;

    ArrayList<Integer> groupsResult = new ArrayList<>();
    // This ArrayList to store the calculated data of each group
    ArrayList<String> groups = new ArrayList<>();
    // This ArrayList to store pairs of start date and end date of each group

    static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public static Summary summaryProcessor(Data data){
        // This method to combine all the steps of summary process
        Summary newSummary = new Summary();
        newSummary.setGroup();
        newSummary.getGroup().groupDetail(newSummary, data);
        newSummary.setMetric();
        newSummary.setResultType();

        // This for loop to calculate and assign appropriate data to groups ArrayList
        // Index of each group in groups ArrayList and their data in groupsResult ArrayList is the same
        for(int i = 0; i < newSummary.groups.size(); i++){
            String[] tokens = newSummary.groups.get(i).split(",");
            ArrayList<Integer> dataArray = newSummary.getMetric().getData(data);
            LocalDate groupStart = LocalDate.parse(tokens[0], dateFormat);
            LocalDate groupEnd = LocalDate.parse(tokens[1], dateFormat);
            int groupResult = newSummary.getResultType().CalculateGroup(groupStart, groupEnd, dataArray, data, newSummary);
            newSummary.groupsResult.add(i, groupResult);
        }
        return newSummary;
    }

    public boolean getIsAccumulated() {
        return isAccumulated;
    }

    public Group getGroup() {
        return group;
    }

    private void setGroup() {
        // This method to ask and get input from user to assign a type of group to Summary instance

        String selection;
        Scanner sc = new Scanner(System.in);
        System.out.println("-------------------");
        System.out.println("""
                Please specify grouping types:
                  1) No grouping: each day will be a group.
                  2) Number of groups you want to divide.
                  3) Number of days you want each group to have.""");
        System.out.print(">>> ");
        selection = sc.nextLine();
        selection = selection.trim();
        Main.checkExit(selection);

        while (true) {
            if (intArrayCheck(selection, new int[]{1, 2, 3})) {
                if (selection.equals("1"))
                    this.group = new NoGroup();
                else if (selection.equals("2"))
                    this.group = new NumberOfGroups();
                else
                    this.group = new NumberOfDays();
                break;
            } else {
                System.out.println("-------------------");
                System.out.println("""
                        Please specify grouping types:
                          1) No grouping: each day will be a group.
                          2) Number of groups you want to divide.
                          3) Number of days you want each group to have.""");
                System.out.print(">>> ");
                selection = sc.nextLine();
                selection = selection.trim();
                Main.checkExit(selection);
            }
        }
    }


    public Metric getMetric() {
        return metric;
    }

    private void setMetric() {
        // This method to ask and get input from user to assign a type of metric to Summary instance
        String selection;
        Scanner sc = new Scanner(System.in);
        System.out.println("-------------------");
        System.out.println("""
                Please specify a metric:
                  1) Positive cases.
                  2) Deaths
                  3) People Vaccinated""");
        System.out.print(">>> ");
        selection = sc.nextLine();
        selection = selection.trim();
        Main.checkExit(selection);

        while (true) {
            if (intArrayCheck(selection, new int[]{1, 2, 3})) {
                if (selection.equals("1")) {
                    this.metric = new PositiveCases();
                    this.isAccumulated = false;
                }
                else if (selection.equals("2")) {
                    this.metric = new Deaths();
                    this.isAccumulated = false;
                }
                else {
                    this.metric = new Vaccinated();
                    this.isAccumulated = true;
                }
                    break;
            } else {
                System.out.println("-------------------");
                System.out.println("""
                        Please specify a metric:
                          1) Positive cases.
                          2) Deaths
                          3) People Vaccinated""");
                System.out.print(">>> ");
                selection = sc.nextLine();
                selection = selection.trim();
                Main.checkExit(selection);
            }
        }
    }


    public ResultType getResultType() {
        return resultType;
    }

    private void setResultType() {
        // This method to ask and get input from user to assign a type of resultType to Summary instance
        String selection;
        Scanner sc = new Scanner(System.in);
        System.out.println("-------------------");
        System.out.println("""
                Please specify a metric:
                  1) New total.
                  2) Up to""");
        System.out.print(">>> ");
        selection = sc.nextLine();
        selection = selection.trim();
        Main.checkExit(selection);

        while (true) {
            if (intArrayCheck(selection, new int[]{1, 2})) {
                if (selection.equals("1"))
                    this.resultType = new NewTotal();
                else
                    this.resultType = new UpTo();
                break;
            } else {
                System.out.println("-------------------");
                System.out.println("""
                Please specify a metric:
                  1) New total.
                  2) Up to""");
                System.out.print(">>> ");
                selection = sc.nextLine();
                selection = selection.trim();
                Main.checkExit(selection);
            }
        }
    }

    static boolean intInputCheck(String number){
        // This method to check if the input string can be parsed into int
        try {
            Integer.parseInt(number);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean intArrayCheck(String number, int[] wanted){
        // This method to check if the user's input can be parsed into int and
        // if it is existed in a list of what we want
        if (!intInputCheck(number)) {
            System.out.println("Please input a number");
            return false;
        }
        for(int numbers: wanted){
            if (Integer.parseInt(number) == numbers)
                return true;
        }
        if(wanted.length == 2)
            System.out.printf("Please input number %s or %s only \n", wanted[0], wanted[1]);
        else
            System.out.printf("Please input number from %s to %s only \n", wanted[0], wanted[wanted.length-1]);
        return false;
    }
}


abstract class Group {
    int groupsNum;

    abstract void groupDetail(Summary summary,Data data);
    // This method to get the detail information of each group type

    void groupDivide(int groupsNum, Summary summary, Data data) {
        // This method to divide days into groups for the NumberOfGroups and NumberOfDays group types
        // And then add the pair of start date and end date of each group to the groups ArrayList of Summary instance

        LocalDate groupStart;
        LocalDate groupEnd;

        if (data.range.size() % groupsNum == 0) {
            // Case that number of day can be divided equally into groups
            // Assign continuously group start date and end date
            groupStart = data.range.get(0);
            groupEnd = data.range.get((data.range.size() / groupsNum) - 1);
            for (int i = 0; i < groupsNum; i++) {
                summary.groups.add(groupStart.format(Summary.dateFormat) + "," + groupEnd.format(Summary.dateFormat));
                groupStart = groupEnd.plusDays(1);
                groupEnd = groupStart.plusDays((data.range.size() / groupsNum) - 1);
            }
        } else {
            // Case that number of day can not be divided equally
            // Add one more day into first groups until out of the remainder days

            groupStart = data.range.get(0);
            groupEnd = data.range.get((data.range.size() / groupsNum) - 1);


            // Add one more day into first groups until out of the remainder days
            for (int i = 0; i < (data.range.size() % groupsNum); i++) {
                groupEnd = groupEnd.plusDays(1);
                summary.groups.add(groupStart.format(Summary.dateFormat) + "," + groupEnd.format(Summary.dateFormat));
                groupStart = groupEnd.plusDays(1);
                groupEnd = groupStart.plusDays((data.range.size() / groupsNum) - 1);
            }
            // Continue the normal division when out of the remainder days
            for (int i = 0; i < groupsNum - (data.range.size() % groupsNum); i++) {
                summary.groups.add(groupStart.format(Summary.dateFormat) + "," + groupEnd.format(Summary.dateFormat));
                groupStart = groupEnd.plusDays(1);
                groupEnd = groupStart.plusDays((data.range.size() / groupsNum) - 1);
            }
        }
    }
}

class NoGroup extends Group {
    // This class to identify the NoGroup type of the Group class

    @Override
    void groupDetail(Summary summary,Data data) {
        // NoGroup group type does not have any further information
        // Then just divide the same number of days into that number of groups
        groupDivide(data.range.size(), summary, data);
    }
}

class NumberOfGroups extends Group {
    // This class to identify the NumberOfGroups type of the Group class

    String groupNumberInput(Data data) {
        // This method to check the user's input number of groups to catch exceptions
        Scanner sc = new Scanner(System.in);
        String groupsNum = sc.nextLine();
        groupsNum = groupsNum.trim();
        Main.checkExit(groupsNum);

        // This while loop to check exceptions and ask user to input correctly
        while (true) {
            if (!Summary.intInputCheck(groupsNum)) {
                System.out.println("-------------------");
                System.out.println("Your input is not a number. Please try again:");
            } else if (Integer.parseInt(groupsNum) < 1) {
                System.out.println("-------------------");
                System.out.println("Number of groups cannot be less than 1. Please try again:");
            } else if (Integer.parseInt(groupsNum) > data.range.size()) {
                System.out.println("-------------------");
                System.out.printf("Number of groups cannot be bigger than the days in time range %s. " +
                        "Please try again:", data.range.size());
            } else return groupsNum;

            System.out.print(">>> ");
            groupsNum = groupNumberInput(data);
        }
    }

    @Override
    void groupDetail(Summary summary,Data data) {
        System.out.println("-------------------");
        System.out.println("Enter a number of groups: ");
        System.out.print(">>> ");

        groupsNum = Integer.parseInt(groupNumberInput(data));

        groupDivide(groupsNum, summary, data);
    }
}

class NumberOfDays extends Group {
    // This class to identify the NumberOfDays type of the Group class

    String dayNumberInput(Data data) {
        // This method to check the user's input number of days to catch exceptions
        Scanner sc = new Scanner(System.in);
        String days = sc.nextLine();
        days = days.trim();
        Main.checkExit(days);

        // This while loop to check exceptions and ask user to input number of days correctly
        while (true){
            if (!Summary.intInputCheck(days)) {
                System.out.println("-------------------");
                System.out.println("Your input is not a number. Please try again:");
            } else if (Integer.parseInt(days) < 1) {
                System.out.println("-------------------");
                System.out.println("Number of days can not be less than 1. Please try again:");
            } else if (Integer.parseInt(days) > data.range.size()) {
                System.out.println("-------------------");
                System.out.printf("Number of days cannot be bigger than the days in time range %s. " +
                        "Please try again: \n", data.range.size());
            } else if (data.range.size() % Integer.parseInt(days) != 0) {
                System.out.println("-------------------");
                System.out.printf("""
                        Number of days can not be divided equally into groups.
                        number of days in your chosen time range is: %s.""", data.range.size());
                System.out.print("\nSome suggestion number of days for you: ");
                for (int i = 1; i <= data.range.size(); i++){
                    if(data.range.size() % i == 0)
                        System.out.print(i + "; ");
                }
                System.out.println("Please try again: ");
            } else return days;

            System.out.print(">>> ");
            days = dayNumberInput(data);
        }
    }

    @Override
    void groupDetail(Summary summary,Data data){
        System.out.println("-------------------");
        System.out.println("Enter a number of days you want for each group: ");
        System.out.print(">>> ");

        int daysPerGroup = Integer.parseInt(dayNumberInput(data));
        groupsNum = data.range.size() / daysPerGroup;

        groupDivide(groupsNum, summary, data);
    }
}


abstract class Metric{
    abstract ArrayList<Integer> getData(Data data);
    // This method to get the suitable ArrayList of data from the Data class depending on the type of metric
}

class PositiveCases extends Metric{
    // This class to identify the PositiveCases type of the Metric class

    @Override
    ArrayList<Integer> getData(Data data){
        return data.newCase;
    }
}

class Vaccinated extends Metric{
    // This class to identify the Vaccinated type of the Metric class

    @Override
    ArrayList<Integer> getData(Data data){
        return data.peopleVaccinated;
    }
}

class Deaths extends Metric{
    // This class to identify the Deaths type of the Metric class

    @Override
    ArrayList<Integer> getData(Data data){
        return data.newDeath;
    }
}


abstract class ResultType{
    abstract int CalculateGroup(LocalDate groupStartDate, LocalDate groupEndDate, ArrayList<Integer> array, Data data,
                                Summary sum);
    // This method to calculate the data of each group
}

class NewTotal extends ResultType {
    // This class to identify the NewTotal type of ResultType class

    @Override
    int CalculateGroup(LocalDate groupStartDate, LocalDate groupEndDate, ArrayList<Integer> array, Data data,
                       Summary sum) {
        // This method will calculate the data in the way of new total of a group

        if (!sum.getIsAccumulated()) {
            int result = 0;
            for (int i = 0; i < data.date.size(); i++) {
                if ((data.date.get(i).isEqual(groupStartDate) || data.date.get(i).isAfter(groupStartDate)) &&
                        (data.date.get(i).isEqual(groupEndDate) || data.date.get(i).isBefore(groupEndDate))) {
                    result += array.get(i);
                }
            }
            return result;
        }
        else{
            int start = 0, end = 0;
            for (int i = 0; i < data.date.size(); i++) {
                if (data.date.get(i).isEqual(groupStartDate.minusDays(1)))
                    start = array.get(i);
                else if (data.date.get(i).isEqual(groupEndDate))
                    end = array.get(i);
            }
            return end - start;
        }
    }
}

class UpTo extends ResultType {
    // This class to identify the UpTo type of ResultType class

    @Override
    int CalculateGroup(LocalDate groupStartDate, LocalDate groupEndDate, ArrayList<Integer> array, Data data,
                       Summary sum) {
        // This method will calculate the data in the way of up to the end date of a group

        int result = 0;
        if (!sum.getIsAccumulated()) {
            for (int i = 0; i < data.date.size(); i++) {
                if (data.date.get(i).isEqual(groupEndDate) || data.date.get(i).isBefore(groupEndDate)) {
                    result += array.get(i);
                }
            }
        } else {
            for (int i = 0; i < data.date.size(); i++) {
                if (data.date.get(i).isEqual(groupEndDate))
                    result = array.get(i);
            }
        }
        return result;
    }
}
