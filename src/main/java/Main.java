import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        try {
            createFolderForCurrentWeek();
            // IndeedCrawler indeedCrawler = new IndeedCrawler();
            StepstoneCrawler stepstoneCrawler = new StepstoneCrawler();
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createFolderForCurrentWeek() {
        Calendar calendar = Calendar.getInstance();
        try {
/*            Path path = Paths.get("C:\\Users\\Administrator\\mexxon Consulting GmbH & Co. KG\\Technical Consulting -" +
                    " projektradar - Crawling Data\\"+ calendar.get(Calendar.YEAR) + "\\"
                    + calendar.get(Calendar.WEEK_OF_YEAR)+". Week " + calendar.get(Calendar.YEAR));*/
            Path path = Paths.get("C:\\Users\\n.roggenbuck\\Mexxon\\Crawling_data"+ calendar.get(Calendar.YEAR) + "\\"
                    + calendar.get(Calendar.WEEK_OF_YEAR)+". Week " + calendar.get(Calendar.YEAR));
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
