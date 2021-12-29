import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Summary sum = new Summary();

        System.out.println("""
                                _Covid data processing and analytics tool_
                                -------------------------
                                Enter "exit" anytime if you want to exit the program.""");
        System.out.println();

        while (true) {

            Scanner sc = new Scanner(System.in);

            Data data = new Data("covid-data.csv");
            sum = Summary.summaryProcessor(data, sum);
            DisplayInterface display = DisplayInterface.setDisplayType(sum);
            display.displayData();

            String isContinue;

            // Asking for continue at the end
            while (true) {
                System.out.println("-------------------------");
                System.out.println("Do you want to continue(Y/N)? ");
                System.out.print(">>> ");
                isContinue = sc.nextLine();
                checkExit(isContinue);
                isContinue = isContinue.toLowerCase();

                if (isContinue.equals("y") || isContinue.equals("n"))
                    break;

                System.out.print("Input Y/N only, please try again");

            }
            if (isContinue.equals("n")) {
                System.out.print("Thank you for using, see you again!");
                break;
            }
        }
    }


    public static void checkExit(String text) {
        // This method to check whenever user enter "exit" in the program and then exit the program
        text = text.trim();
        text = text.toLowerCase();

        if (text.equals("exit")) {
            System.out.println("-------------------------");
            System.out.println("Thank you for using.");
            System.exit(0);
        }
    }
}