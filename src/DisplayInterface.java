import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public abstract class DisplayInterface {
    Summary sumDat;
    final int groupNo;

    public DisplayInterface(Summary sumDat){
        this.sumDat = sumDat;
        this.groupNo = sumDat.groups.size();
    }

    static public DisplayInterface setDisplayType(Summary sumDat){
        Scanner displayScanner = new Scanner(System.in);
        System.out.println("-------------------");
        System.out.println("""
                Please select a display option:
                \t1) Tabular Display
                \t2) Chart Display""");
        System.out.print(">>> ");
        String choice = displayScanner.nextLine();
        choice = choice.trim();
        Main.checkExit(choice);

        // Will ask indefinitely until appropriate input is entered
        while (true){
            if (choice.equals("1")){
                return new Table(sumDat);
            } else if (choice.equals("2")) {
                return new Chart(sumDat);
            }
            System.out.println("Invalid option, please choose again:");
            System.out.print(">>> ");
            choice = displayScanner.nextLine();
            choice = choice.trim();
            Main.checkExit(choice);
        }
    }

    abstract void displayData();
}

class Table extends  DisplayInterface{
    private final int cellWidth = 30;

    protected Table(Summary sumDat) {
        super(sumDat);
    }

    @Override
    public void displayData() {
        displayHeader();
        for (int i = 0; i < groupNo; i++){
            System.out.printf("|%-" + cellWidth + "s|%-" + cellWidth + "s|\n", " " + processDateRange(sumDat.groups.get(i)), " " + sumDat.groupsResult.get(i));
        }
    }

    private void displayHeader(){
        System.out.printf("|%-" + cellWidth + "s|%-" + cellWidth + "s|\n", " Range", " Value");
        System.out.println("-" + "-".repeat(cellWidth) + "-" + "-".repeat(cellWidth) + "-");
    }

    private String processDateRange(String dates){
        /* This method is used to process the date range string from Summary class
        *  It is formatted "date1,date2" */

        // Split the string into an array of strings includes start date and end date
        String[] date = dates.split(",");

        // If dates are same, return 1 date. Else return appropriate string.
        if(date[0].equals(date[1])){
            return date[0];
        }
        else{
            return String.format("%s to %s", date[0], date[1]);
        }

    }
}

class Chart extends  DisplayInterface{
    // The number of rows and columns for chart
    private final int rows = 24;
    private final int cols = 80;

    protected Chart(Summary sumDat) {
        super(sumDat);
    }

    @Override
    public void displayData() {
        /* Displaying the data in chart form.
         * Row 24 = x-axis. Row 23 = 0. Row 0 = max value. Column 0 = y-axis */

        // Get an array of each group's result from Summary class
        ArrayList<Integer[]> valuePos = getValuePos(sumDat.groupsResult);
        for (int row = 0; row < rows; row++){
            // First column is always pipe |
            System.out.print("|");

            // skip empty rows except last row
            boolean emptyRow = true;
            for(Integer[] position : valuePos){
                if (row == position[0]) {
                    emptyRow = false;
                    break;
                }
            }
            if (emptyRow && row != rows - 1){
                System.out.println();
                continue;
            }

            for (int col = 1; col < cols; col++){
                // If last row, output all underscores _.
                if (row == rows - 1){
                    System.out.print("_");
                    continue;
                }

                boolean found = false;
                for (Integer[] position : valuePos){
                    // Check index of row and col with the coordinate of value point.
                    if (row == position[0] && col == position [1]){
                        System.out.print("*");
                        found = true;
                    }
                }
                // If no value point is at coordinate, print space.
                if (!found) {
                    System.out.print(" ");
                }
            }

            System.out.println();
        }
    }

    private ArrayList<Integer[]> getValuePos(ArrayList<Integer> valList){
        /*Get the coordinate of value points as index of row and column.
        * The maximum value is retrieved and made the upper bound of chart.
        * Position of other values are calculated with: ((current value * row width) / max value) */

        // Data interval divides width of chart with size of array to ensure even distribution.
        int dataInterval = Math.round((cols - 1f) / (groupNo));
        int currentCol = Math.round(dataInterval / 2f);
        int maxValue = Collections.max(valList);
        // This array will store the value of x and y in the chart
        ArrayList<Integer[]> posVal = new ArrayList<>();

        for (Integer val : valList){
            // Since loop index start at 0 and the last row is used, minus 2 to row to get starting y-point of chart.
            // Chart is drawn from top down, so row values are inverted.
            posVal.add(new Integer[]{(rows - 2) - Math.round((val * (rows - 2f)) / maxValue), currentCol});
            currentCol += dataInterval;
        }
        return posVal;
    }

}