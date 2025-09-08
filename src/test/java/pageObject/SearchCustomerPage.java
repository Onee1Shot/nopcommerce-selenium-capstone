package pageObject;

import java.awt.List;

import javax.swing.table.TableRowSorter;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SearchCustomerPage {
	
	WebDriver ldriver;
	
	public  SearchCustomerPage (WebDriver rDriver) {
		
		ldriver = rDriver;
		PageFactory.initElements(rDriver, this);
		
	}
	
	@FindBy(css = "input#SearchEmail")
	WebElement emailAdd;
	
	@FindBy(id ="search-customers")
	WebElement searchBtn;
	
	@FindBy(xpath="//table[@role='grid']")
	WebElement searchResultElement;
	
	
	
	
	
	
	public void enterEmailAdd(String email) {
		
		emailAdd.sendKeys(email);
	}
	
	
	//Action method to perform click action on search button
	public void clickOnSearchButton() {
		searchBtn.click();
	}
	
	
}