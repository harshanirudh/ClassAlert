package com.harsha;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MainDriver {
private static final String BASE_URL=
"https://cmsweb.cms.sjsu.edu/psp/CSJPRDF/EMPLOYEE/CSJPRD/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL?pslnkid=SJ_CLASS_SRCH_LNK";
public static void main(String args[]) {
	try {
		while(true) {
			WebDriver driver = getWebDriver();
			startChecking(driver);
			Thread.sleep(getTimeToSleep());
		}
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
}
private static long getTimeToSleep() {
	int hour = LocalTime.now().getHour();
	if(hour>=20 || hour<=7)
		return 5*60*1000;
	else
		return 10*60*1000;
}
	public static void startChecking(WebDriver driver)  {
		try {
		
			WebDriverWait wait = new WebDriverWait(driver,30);
		driver.get(BASE_URL);
		String selectTermXpath="//select[@id='CLASS_SRCH_WRK2_STRM$35$']";
		String inputSubjectXpath="//input[@id='SSR_CLSRCH_WRK_SUBJECT$0']";
		String selectCourseXpath="//select[@id='SSR_CLSRCH_WRK_SSR_EXACT_MATCH1$1']";
		String inputCourseXpath="//input[@id='SSR_CLSRCH_WRK_CATALOG_NBR$1']";
		String selectCCXpath="//select[@id='SSR_CLSRCH_WRK_ACAD_CAREER$2']";
		String boxOpenClass="//input[@type='checkbox']";
		String buttonSearch="//input[@id='CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH' and @type='button'] ";
		System.out.println(driver.getTitle());
		
		//Switch to ifram
		driver.switchTo().frame("ptifrmtgtframe");
		//JavascriptExecutor js= (JavascriptExecutor)driver;
		// To select Term
		Select dropTerm= new Select(driver.findElement(By.xpath(selectTermXpath)));
		dropTerm.selectByVisibleText("Spring 2022");
		Thread.sleep(500);
		
		//To input Subject
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(inputSubjectXpath)));
		WebElement subjectInputEle = driver.findElement(By.xpath(inputSubjectXpath));
		subjectInputEle.sendKeys("CMPE");
		subjectInputEle.sendKeys(Keys.TAB);
		
		//To select Course search criteria type
		Select dropCourse=new Select(driver.findElement(By.xpath(selectCourseXpath)));
		dropCourse.selectByVisibleText("greater than or equal to");
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(inputCourseXpath)));
		//To input course number
		WebElement courseEle = driver.findElement(By.xpath(inputCourseXpath));
		courseEle.sendKeys("202");
		courseEle.sendKeys(Keys.TAB);
		Thread.sleep(500);
		
		//To input course career type
		Select dropCC=new Select(driver.findElement(By.xpath(selectCCXpath)));
		dropCC.selectByVisibleText("Graduate");
		
		//To select to show open classes or not
		WebElement openBox = driver.findElement(By.xpath(boxOpenClass));
		if(openBox.isSelected())
			openBox.click();
		
		//To submit form
		WebElement search = driver.findElement(By.xpath(buttonSearch));
		search.sendKeys(Keys.RETURN);
		/************Next page********/
		//To select confirmation to display more than 50 results
		String warning="//span[text()='Your search will return over 50 classes, would you like to continue?']";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(warning)));
		String okPath="//input[@id='#ICSave']";
		driver.findElement(By.xpath(okPath)).click();
		
		//To check if we are in results page
		String resultFound="//td[contains(text(),'found')]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(resultFound)));

		// to get all divs of courses
		String subjectDivs = "//div[contains(@id,'win0divSSR_CLSRSLT_WRK_GROUPBOX2GP')]";
		List<WebElement> subject = driver.findElements(By.xpath(subjectDivs));
		List<String> filterConditionList = Arrays.asList("CMPE 202","CMPE 281","CMPE 283","CMPE 275","CMPE 287");

		//To filter desired courses
		List<WebElement> desiredSubject = subject.stream()
				.filter(sub -> filterConditionList.stream().anyMatch(filter -> sub.getText().contains(filter)))
				.collect(Collectors.toList());
		String rows="./parent::td/parent::tr/following-sibling::tr//tr[contains(@id,'trSSR_CLSRCH_MTG1$')]";
		
		for (WebElement subSection : desiredSubject) {
			List<WebElement> desiredResult=new ArrayList<>();
			List<WebElement> sectionsRow = subSection.findElements(By.xpath(rows));
			System.out.println("Section sizes "+sectionsRow.size());
				for (WebElement row : sectionsRow) {
//					if(!row.findElements(By.xpath(".//img[@alt='Wait List']")).isEmpty()){
					if(!row.findElements(By.xpath(".//img[@alt='Open']")).isEmpty()){
//					if(row.findElement(By.tagName("img")).getAttribute("alt").contains("Wait List")) {
						String courseName = subSection.getText();
						System.out.println("COURSE: "+ courseName);
						String classNumber = row.findElement(By.xpath(".//a[contains(@id,'MTG_CLASS_NBR')]")).getText();
						System.out.println(classNumber);
						if(!(classNumber.contentEquals("23780") || classNumber.contentEquals("29192") || classNumber.contentEquals("24281")))
						SendEmail.send(courseName+" with class number:"+classNumber+" is available now!!");
					}
				}
	
		}
//		driver.quit();
	}
		catch(Exception e) {
			e.printStackTrace();
		}finally {
			driver.quit();
		}
	}
	private static WebDriver getWebDriver() {
		WebDriver driver;
		ApplicationConfig.setBroswerConfig();
		ChromeOptions options= new ChromeOptions();
//		options.addArguments("--headless");
//		options.addArguments("--disable-gpu");
		options.addArguments("window-size=1400,800");
		driver= new ChromeDriver(options);
		return driver;
	}
}
