import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args){
        ArrayList<Integer> data = new ArrayList<>(Arrays.asList(4, 45, 79, 45, 153, 167, 200, 49, 64, 100, 140, 140, 140, 140));
        ArrayList<Integer> data2 = new ArrayList<>(Arrays.asList(46, 45, 7, 45, 153, 167, 200, 49, 64, 100, 140, 140, 140, 140));

        DisplayInterface display = DisplayInterface.setDisplayType(data);
        display.displayData();
        display = DisplayInterface.setDisplayType(data2);
        display.displayData();

    }
}
