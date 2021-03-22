import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StepstoneCrawler {

    private List<String> projectURLList = new ArrayList<>();
    private List<WebElement> projectUrls;
    private List<String> projectUrlsAsString = new ArrayList<>();
    private WebDriver driver;
    private File file;
    private Date currentDate = new Date();
    private String currentDateAsString = "";
    private Path path;
    private Calendar calendar = Calendar.getInstance();

    public StepstoneCrawler() throws InterruptedException {
        // System.setProperty("webdriver.chrome.driver", "C:\\Users\\Administrator\\Selenium\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\n.roggenbuck\\Selenium\\chromedriver.exe");
        driver = new ChromeDriver();
        createCSVForStepstoneJobs();
        getJobLinks();
        Thread.sleep(3500);
        acceptCookieButton();
        openJobsAndSaveItsInformation();
    }

    public void createCSVForStepstoneJobs() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        currentDateAsString = dateFormat.format(currentDate);
        Calendar calendar = Calendar.getInstance();
        Path path = Paths.get("C:\\Users\\n.roggenbuck\\Mexxon\\Crawling_data");
       /* Path path = Paths.get("C:\\Users\\Administrator\\mexxon Consulting GmbH & Co. KG\\Technical Consulting -" +
                " projektradar - Crawling Data\\"+ calendar.get(Calendar.YEAR) + "\\"
                + calendar.get(Calendar.WEEK_OF_YEAR)+". Week " + calendar.get(Calendar.YEAR));*/
        file = new File(path.toString(), currentDateAsString+"_Stepstone_All_Jobs.xlsx");
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

    public void getJobLinks() {
        driver.get("https://www.stepstone.de/5/ergebnisliste.html?stf=freeText&ns=1&qs=%5B%5D&companyID=0&cityID=0" +
                "&sourceOfTheSearchField=resultlistpage%3Ageneral&searchOrigin=Resultlist_top-search&ke=" +
                "Testing&ws=61352&ra=30");
        projectUrls = driver.findElements(By.xpath("//article/div[3]/div[1]/a"));
        int index = 0;
        for(WebElement webElement: projectUrls) {
            projectUrlsAsString.add(webElement.getAttribute("href"));
            System.out.println(projectUrlsAsString.get(index));
            index++;
        }
    }

    public void acceptCookieButton() {
        WebElement cookieButton = driver.findElement(By.xpath("//*[@id=\"ccmgt_explicit_accept\"]"));
        cookieButton.click();
        System.out.println("Cookies were accepted");
    }

    public void openJobsAndSaveItsInformation() throws InterruptedException {
        int rowCounter = 1;

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sh = wb.createSheet();
        sh.createRow(0);
        sh.getRow(0).createCell(0).setCellValue("Job_Link");
        sh.getRow(0).createCell(1).setCellValue("Job_Title");
        sh.getRow(0).createCell(2).setCellValue("Job_Location");
        sh.getRow(0).createCell(3).setCellValue("Career_level");
        sh.getRow(0).createCell(4).setCellValue("Company_Name");
        sh.getRow(0).createCell(5).setCellValue("Crawling_Date");
        sh.getRow(0).createCell(6).setCellValue("Tasks");
        sh.getRow(0).createCell(7).setCellValue("Profile");
        sh.getRow(0).createCell(8).setCellValue("Company_offers");
        sh.getRow(0).createCell(9).setCellValue("All_job_information");

        for(String url: projectUrlsAsString) {
            driver.get(url);
            sh.createRow(rowCounter);

            // Job url
            sh.getRow(rowCounter).createCell(0).setCellValue(url);
            // Job title
            WebElement jobTitle = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[1]/" +
                    "div/section/div/div/div/div[1]/div[2]/div[2]/h1"));
            sh.getRow(rowCounter).createCell(1).setCellValue(jobTitle.getText());
            // Job location
            WebElement jobLocation = null;
            try {
                jobLocation = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div/div[1]/div[1]/" +
                        "div[1]/div/section/div/div/div/div[1]/div[2]/div[3]/ul/li[1]/text()"));

            }
            catch (Exception e) {

            }
            try {
                jobLocation = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div[2]/div/div[1]/" +
                        "div[1]/div[1]/div/section/div/div/div/div[1]/div[2]/div[3]/ul/li[1]/text()"));

            }
            catch (Exception e) {

            }
            try {
                jobLocation = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div/div[1]/div[1]/" +
                        "div[1]/div/section/div/div/div/div[1]/div[2]/div[3]/ul/li[1]/a"));

            }
            catch (Exception e) {

            }
            if(jobLocation != null) {
                sh.getRow(rowCounter).createCell(2).setCellValue(jobLocation.getText());
            }
            else {
                sh.getRow(rowCounter).createCell(2).setCellValue("n/a");
            }
            Thread.sleep(1000);
            rowCounter++;
        }
    }





    
}
