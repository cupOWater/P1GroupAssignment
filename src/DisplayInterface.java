import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public abstract class DisplayInterface {
    protected ArrayList<Integer> sumDat;
    protected final int groupNo;

    public DisplayInterface(ArrayList<Integer> sumDat){
        this.sumDat = sumDat;
        this.groupNo = sumDat.size();
    }

    abstract void displayData();
}

class Table extends  DisplayInterface{
    private final int cellWidth = 30;

    public Table(ArrayList<Integer> sumDat) {
        super(sumDat);
    }

    @Override
    public void displayData() {
        displayHeader();
        for (int i = 0; i < groupNo; i++){
            System.out.printf("|%-" + cellWidth + "s|%-" + cellWidth + "s|\n", "", " " + sumDat.get(i));
        }
    }

    private void displayHeader(){
        System.out.printf("|%-" + cellWidth + "s|%-" + cellWidth + "s|\n", " Range", " Value");
        System.out.println("-" + "-".repeat(cellWidth) + "-" + "-".repeat(cellWidth) + "-");
    }
}

class Chart extends  DisplayInterface{
    private final int rows = 24;
    private final int cols = 80;

    public Chart(ArrayList<Integer> sumDat) {
        super(sumDat);
    }

    @Override
    public void displayData() {
        // Displaying the data in chart form. Row 23 = 0// row 0 = max value

        ArrayList<Integer[]> valuePos = getValuePos(sumDat);
        for (int row = 0; row < rows; row++){
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
                if (row == rows - 1){
                    System.out.print("_");
                    continue;
                }
                boolean found = false;
                for (Integer[] position : valuePos){
                    if (row == position[0] && col == position [1]){
                        System.out.print("*");
                        found = true;
                    }
                }
                if (!found) {
                    System.out.print(" ");
                }
            }

            System.out.println();
        }
    }

    private ArrayList<Integer[]> getValuePos(ArrayList<Integer> valList){
        // get the index for row and column to display as chart
        System.out.println((cols - 1f) / (groupNo));
        int dataInterval = Math.round((cols - 1f) / (groupNo));
        int currentCol = Math.round(dataInterval / 2f);
        int maxValue = Collections.max(valList);
        ArrayList<Integer[]> posVal = new ArrayList<>();

        for (Integer val : valList){
            // Since loop index start at 0 and the last row is used, minus 2 to row to get starting y-point of chart
            posVal.add(new Integer[]{(rows - 2) - Math.round((val * (rows - 2f)) / maxValue), currentCol});
            currentCol += dataInterval;
        }
        return posVal;
    }

}