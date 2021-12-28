import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Data test = new Data("covid-data.csv");
        Summary sum = new Summary();
        DisplayInterface display;

        sum = Summary.summaryProcessor(test, sum);
        display = DisplayInterface.setDisplayType(sum);
        display.displayData();

    }
}
