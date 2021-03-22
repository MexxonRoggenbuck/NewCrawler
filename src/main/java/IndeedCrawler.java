import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class IndeedCrawler {

    private List<String> projectURLList = new ArrayList<>();
    private List<WebElement> projectUrls;
    private List<String> projectUrlsAsString = new ArrayList<>();
    private WebDriver driver;
    private File file;
    private Date currentDate = new Date();
    private String currentDateAsString = "";
    private Path path;
    private Calendar calendar = Calendar.getInstance();
    private Path pathPreviosWeek = Paths.get("C:\\Users\\Administrator\\mexxon Consulting GmbH & Co. KG\\Technical Consulting -" +
            " projektradar - Crawling Data\\"+ calendar.get(Calendar.YEAR) + "\\" +
            (calendar.get(Calendar.WEEK_OF_YEAR)-1)+". Week " + calendar.get(Calendar.YEAR));

    public void determinePreviosWeeksPath() {
        if(Calendar.WEEK_OF_YEAR>1) {
            pathPreviosWeek = Paths.get("C:\\Users\\Administrator\\mexxon Consulting GmbH & Co. KG\\Technical Consulting -" +
                    " projektradar - Crawling Data\\"+ calendar.get(Calendar.YEAR) + "\\" +
                    (calendar.get(Calendar.WEEK_OF_YEAR)-1)+". Week " + calendar.get(Calendar.YEAR));
        }
        else {
            pathPreviosWeek = Paths.get("C:\\Users\\Administrator\\mexxon Consulting GmbH & Co. KG\\Technical Consulting -" +
                    " projektradar - Crawling Data\\"+ calendar.get(Calendar.YEAR) + "\\" +
                    53+". Week " + (calendar.get(Calendar.YEAR)-1));
        }
    }

    public IndeedCrawler() throws InterruptedException {
        // System.setProperty("webdriver.chrome.driver", "C:\\Users\\Administrator\\Selenium\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\n.roggenbuck\\Selenium\\chromedriver.exe");
        driver = new ChromeDriver();
        createCSVForIndeedProjects();
        getProjectLinks();
        openProjects();
    }

    public void createCSVForIndeedProjects() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        currentDateAsString = dateFormat.format(currentDate);
        Calendar calendar = Calendar.getInstance();
        Path path = Paths.get("C:\\Users\\n.roggenbuck\\Mexxon\\Crawling_data\\"+ calendar.get(Calendar.YEAR) + "\\"
                + calendar.get(Calendar.WEEK_OF_YEAR)+". Week " + calendar.get(Calendar.YEAR));
       /* Path path = Paths.get("C:\\Users\\Administrator\\mexxon Consulting GmbH & Co. KG\\Technical Consulting -" +
                " projektradar - Crawling Data\\"+ calendar.get(Calendar.YEAR) + "\\"
                + calendar.get(Calendar.WEEK_OF_YEAR)+". Week " + calendar.get(Calendar.YEAR));*/
        file = new File(path.toString(), currentDateAsString+"_Indeed_All_Jobs.xlsx");
        try {
            if(file.createNewFile() == true) {
                System.out.println("New file created...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void acceptCookieButtons() {
        try {
            WebElement acceptCoockieButton = driver.findElement(By.xpath("//*[@id=\"onetrust-accept-btn-handler\"]"));
            acceptCoockieButton.click();
        } catch (Exception e) {
            System.out.println("Coockie button already accepted...");
        }
    }

    public void getProjectLinks() throws InterruptedException {
        driver.get("https://de.indeed.com/jobs?q=testing&l=61352");
        Thread.sleep(1000);
        acceptCookieButtons();
        Thread.sleep(2000);
        for(int i = 0; i < 15; i++) {
            projectUrls = driver.findElements(By.xpath("//*[@class=\"title\"]/a"));
            for(WebElement url: projectUrls) {
                projectUrlsAsString.add(url.getAttribute("href"));
            }
            Thread.sleep(4000);
            driver.get("https://de.indeed.com/jobs?q=testing&l=61352&start="+(10*(i+1)));
        }
    }

    public void openProjects() throws InterruptedException {
        int rowCounter = 1;

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sh = wb.createSheet();
        sh.createRow(0);
        sh.getRow(0).createCell(0).setCellValue("Project_Link");
        sh.getRow(0).createCell(1).setCellValue("Project_Title");
        sh.getRow(0).createCell(2).setCellValue("Project_Location");
        sh.getRow(0).createCell(3).setCellValue("Allgemeine_Infos_ueber_Arbeit_und_ggf_Arbeitgeber");
        sh.getRow(0).createCell(4).setCellValue("Company_Name");
        sh.getRow(0).createCell(5).setCellValue("Crawling_Date");
        for(String url: projectUrlsAsString) {
            sh.createRow(rowCounter);
            ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(0)); //switches to new tab
            Thread.sleep(2000);
            driver.get(url);
            Thread.sleep(2000);
            String projectTitle = "n/a", projectLocation = "n/a",
                    jobDescriptionText = "n/a", companyName = "n/a", crawlingDate = "";

            try {
                sh.getRow(rowCounter).createCell(0).setCellValue(url);
                Thread.sleep(125);
            }catch (Exception e) {
                sh.getRow(rowCounter).createCell(0).setCellValue(url);
            }
            try {
                WebElement projectTitleWE = driver.findElement(By.xpath("//h1[@class=\"icl-u-xs-mb--xs icl-u-xs-mt--none " +
                        "jobsearch-JobInfoHeader-title\"]"));
                projectTitle = projectTitleWE.getText();
                sh.getRow(rowCounter).createCell(1).setCellValue(projectTitle);
                Thread.sleep(125);
            }catch (Exception e) {

            }
            // Try if there are only two divs
            try {
                WebElement projectLocationWE =
                        driver.findElement(
                                By.xpath("//*[@id=\"viewJobSSRRoot\"]/div[1]/div[3]/div[2]/div[2]/div/div/div[2]"));
                projectLocation = projectLocationWE.getText();
                sh.getRow(rowCounter).createCell(2).setCellValue(projectLocation);
                Thread.sleep(125);
            }catch (Exception e) {

            }
            // Try an other xPath, if the 'div count' is not enough...
            try {
                if(projectLocation.equals("n/a") || projectLocation.isEmpty()) {
                    WebElement projectLocationWE =
                            driver.findElement(
                                    By.xpath("//*[@id=\"viewJobSSRRoot\"]/div[1]/div[2]/div[2]/div/div[2]/div/div[2]"));
                    projectLocation = projectLocationWE.getText();
                }
                sh.getRow(rowCounter).createCell(2).setCellValue(projectLocation);
                Thread.sleep(125);
            }catch (Exception e) {

            }
            // Try an other xPath, if the 'div count' is not enough...
            try {
                if(projectLocation.equals("n/a") || projectLocation.isEmpty()) {
                    WebElement projectLocationWE =
                            driver.findElement(
                                    By.xpath("//*[@id=\"viewJobSSRRoot\"]/div/div[3]/div[2]/div[2]/div/div/div[2]"));
                    projectLocation = projectLocationWE.getText();
                }
                sh.getRow(rowCounter).createCell(2).setCellValue(projectLocation);
                Thread.sleep(125);
            }catch (Exception e) {

            }
            try {
                if(projectLocation.equals("n/a") || projectLocation.isEmpty()) {
                    WebElement projectLocationWE =
                            driver.findElement(
                                    By.xpath("//*[@id=\"viewJobSSRRoot\"]/div[1]/div[3]/div[2]/div/div[2]/div/div[2]"));
                    projectLocation = projectLocationWE.getText();
                }
                sh.getRow(rowCounter).createCell(2).setCellValue(projectLocation);
                Thread.sleep(125);
            }catch (Exception e) {

            }
            try {
                if(projectLocation.equals("n/a") || projectLocation.isEmpty()) {
                    WebElement projectLocationWE =
                            driver.findElement(
                                    By.xpath("//*[@id=\"viewJobSSRRoot\"]/div[1]/div[3]/div[2]/div/div[2]/div/div[2]"));
                    projectLocation = projectLocationWE.getText();
                }
                sh.getRow(rowCounter).createCell(2).setCellValue(projectLocation);
                Thread.sleep(125);
            }catch (Exception e) {

            }

            try {
                if(projectLocation.equals("n/a") || projectLocation.isEmpty()) {
                    WebElement projectLocationWE =
                            driver.findElement(
                                    By.xpath("//*[@id=\"viewJobSSRRoot\"]/div[1]/div[2]/div/div/div[2]/div/div[2]"));
                    projectLocation = projectLocationWE.getText();
                }
                sh.getRow(rowCounter).createCell(2).setCellValue(projectLocation);
                Thread.sleep(125);
            }catch (Exception e) {

            }
            if(projectLocation.isEmpty()) {
                projectLocation= "n/a";
            }
            try {
                List<WebElement> jobDescrBodyWEs = driver.findElements(By.xpath("//*[@id=\"jobDescriptionText\"]/*"));
                jobDescriptionText = "";
                for(WebElement jobDescriptionWE: jobDescrBodyWEs){
                    jobDescriptionText += jobDescriptionWE.getText();
                }
                sh.getRow(rowCounter).createCell(3).setCellValue(jobDescriptionText);
            }
            catch (Exception e) {

            }

            try{
                WebElement companyNameWE = driver.findElement(By.xpath("//div[@class=\"icl-u-lg-mr--sm icl-u-xs-mr--xs\"]/a"));
                companyName = companyNameWE.getText();
                sh.getRow(rowCounter).createCell(4).setCellValue(companyName);
            }
            catch (Exception e) {

            }
            try{
                WebElement companyNameWE = driver.findElement(By.xpath("//div[@class=\"icl-u-lg-mr--sm icl-u-xs-mr--xs\"]"));
                companyName = companyNameWE.getText();
                sh.getRow(rowCounter).createCell(4).setCellValue(companyName);
            }
            catch(Exception e) {

            }

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            crawlingDate = dateFormat.format(currentDate);
            sh.getRow(rowCounter).createCell(5).setCellValue(crawlingDate);

            try {
                FileOutputStream fos = new FileOutputStream(file);
                wb.write(fos);
                Thread.sleep(125);
            } catch (Exception e) {
                e.printStackTrace();
            }

            rowCounter++;
        }
    }
}
