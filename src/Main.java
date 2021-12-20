import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args){
        ArrayList<Integer> data = new ArrayList<>(Arrays.asList(15, 45, 79, 45, 153, 167, 200, 49, 64, 100, 140, 140, 140, 140));
        DisplayInterface tableDisplay = new Table(data);
        tableDisplay.displayData();
    }
}
