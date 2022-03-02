package selenium.test;

import static org.junit.Assert.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

//TestCase Requirement
//Steps : 
//1. Open Amazon
//2. Search for required product : Here searching for Product Name : Brut
//3. Select the Set of Product : Here need to select set of 3 Product
//4. Look for required rating of that product like item with 3.9 rating to be selected
//5. On satisfying above condition : Select single product and add that product to cart.
//6. Verify if the product is added to cart successfully

public class VerifySeleniumTC {
	static WebDriver driver;
	String WebSiteURL = "https://www.amazon.in/";
	String productName = "Brut";
	String SearchInputBox = "//input[@id='twotabsearchtextbox']";
	String SearchButton = "//input[@id='nav-search-submit-button']";
	String HomePageContainerObj = "//div[@id='nav-xshop-container']";
	boolean presenceOfProduct = false;
	String ProductSetCount[] = { "Pack Of 3", "Pack of 2" };
	String UserRating = "3.8";
	String PurchaseWindowTitle = "Buy " + productName;
	String AddToCartBtn = "//input[@id='add-to-cart-button']";
	String CartValueXpath = "//span[@id='nav-cart-count']";
	String ProductSetof3Xpath = "//span[contains(text(),'" + ProductSetCount[0] + "')]";
	String Setof3ProudctRatingXpath = ProductSetof3Xpath+ "//ancestor::h2/parent::div/following-sibling::div/div/span[contains(@aria-label,'out of 5 stars')]";

	@BeforeClass
	public static void DriverSetup() {
		try {
			System.setProperty("webdriver.chrome.driver", "D:\\SeleniumWebDriver\\chromedriver.exe");
		} catch (Exception e) {
			System.out.println("Couldn't able to set the system property");
			e.printStackTrace();
		}
	}

	@Test

	public void VerifyRequiredProductAddToCart() {

		try {
			driver = new ChromeDriver();
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.get(WebSiteURL);
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(HomePageContainerObj)));

			driver.findElement(By.xpath(SearchInputBox)).sendKeys(productName);
			driver.findElement(By.xpath(SearchButton)).click();

			Thread.sleep(10);

			List<WebElement> Items = driver.findElements(By.xpath(ProductSetof3Xpath));
			List<WebElement> ProductRatings = driver.findElements(By.xpath(Setof3ProudctRatingXpath));

			// Checking for required product using method call
			boolean presenceOfProduct = CheckingForRequiredProduct(Items, ProductRatings, UserRating);

			if (presenceOfProduct) {
				Set<String> w = driver.getWindowHandles();

				Iterator<String> it = w.iterator();
				while (it.hasNext()) {

					String value = it.next().toString();
					String windowTitle = driver.switchTo().window(value).getTitle();
					if (windowTitle.contains(PurchaseWindowTitle)) {
						driver.switchTo().window(value);
						wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AddToCartBtn)));
						driver.findElement(By.xpath(AddToCartBtn)).click();
						Assert.assertEquals("1", driver.findElement(By.xpath(CartValueXpath)).getText());
					}
					
					
					 
				}
			}

			else {
				System.out.println("Required product with given rating was not found");
				Assert.assertFalse(true);
			}

		} catch (Exception e) {
			// Assert.assertFalse(true);
			e.printStackTrace();
		}
	}

	// Method to check presence of Required Product with required rating
	public boolean CheckingForRequiredProduct(List<WebElement> Items, List<WebElement> ProductRatings,
			String UserRating) {

		try {
			Iterator<WebElement> itemIt = Items.iterator();
			Iterator<WebElement> RatedItemIt = ProductRatings.iterator();

			while (itemIt.hasNext() && (RatedItemIt.hasNext())) {

				WebElement RatedProduct = RatedItemIt.next();
				WebElement ThisItem = itemIt.next();

				String ThisProductRated = RatedProduct.getAttribute("aria-label");

				if (ThisProductRated.contains(UserRating)) {
					System.out.println("Required rated Product found with rating " + ThisProductRated);
					ThisItem.click();
					presenceOfProduct = true;
					break;

				} else {
					System.out.println("Checking Next Product for the required rating");
				}

			}
			return presenceOfProduct;

		} catch (Exception e) {
			System.out.println("Couldn't check for relevant product");
			e.printStackTrace();
			return presenceOfProduct;

		}
	}

	@AfterClass
	public static void afterClassMethod() {

		driver.close();
		driver.quit();

	}

}
