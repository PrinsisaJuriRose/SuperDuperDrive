package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.File;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

	@LocalServerPort
	private int port;

	private WebDriver driver;
	private EncryptionService encryptionService;

	@BeforeAll
	static void beforeAll() {
		//WebDriverManager.chromedriver().setup();
		WebDriverManager.firefoxdriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		//this.driver = new ChromeDriver();
		this.driver = new FirefoxDriver();
		this.encryptionService = new EncryptionService();
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
		}
	}

	@Test
	public void getLoginPage() {
		driver.get("http://localhost:" + this.port + "/login");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	/**
	 * PLEASE DO NOT DELETE THIS method.
	 * Helper method for Udacity-supplied sanity checks.
	 **/
	private void doMockSignUp(String firstName, String lastName, String userName, String password){
		// Create a dummy account for logging in later.

		// Visit the sign-up page.
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		driver.get("http://localhost:" + this.port + "/signup");
		webDriverWait.until(ExpectedConditions.titleContains("Sign Up"));
		
		// Fill out credentials
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputFirstName")));
		WebElement inputFirstName = driver.findElement(By.id("inputFirstName"));
		inputFirstName.click();
		inputFirstName.sendKeys(firstName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputLastName")));
		WebElement inputLastName = driver.findElement(By.id("inputLastName"));
		inputLastName.click();
		inputLastName.sendKeys(lastName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
		WebElement inputUsername = driver.findElement(By.id("inputUsername"));
		inputUsername.click();
		inputUsername.sendKeys(userName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
		WebElement inputPassword = driver.findElement(By.id("inputPassword"));
		inputPassword.click();
		inputPassword.sendKeys(password);

		// Attempt to sign up.
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonSignUp")));
		WebElement buttonSignUp = driver.findElement(By.id("buttonSignUp"));
		buttonSignUp.click();

		/* Check that the sign up was successful. 
		// You may have to modify the element "success-msg" and the sign-up 
		// success message below depening on the rest of your code.
		*/
		Assertions.assertTrue(driver.findElement(By.id("success-msg")).getText().contains("You successfully signed up!"));
	}

	
	
	/**
	 * PLEASE DO NOT DELETE THIS method.
	 * Helper method for Udacity-supplied sanity checks.
	 **/
	private void doLogIn(String userName, String password)
	{
		// Log in to our dummy account.
		driver.get("http://localhost:" + this.port + "/login");
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
		WebElement loginUserName = driver.findElement(By.id("inputUsername"));
		loginUserName.click();
		loginUserName.sendKeys(userName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
		WebElement loginPassword = driver.findElement(By.id("inputPassword"));
		loginPassword.click();
		loginPassword.sendKeys(password);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
		WebElement loginButton = driver.findElement(By.id("login-button"));
		loginButton.click();

		webDriverWait.until(ExpectedConditions.titleContains("Home"));

	}

	private void doCreateNote(String noteTitle, String noteDescription)
	{
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));
		driver.findElement(By.id("nav-notes-tab")).click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("button-new-note")));
		driver.findElement(By.id("button-new-note")).click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));
		driver.findElement(By.id("note-title")).sendKeys(noteTitle);
		driver.findElement(By.id("note-description")).sendKeys(noteDescription);
		driver.findElement(By.id("button-note-save")).click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp-save-success")));
		WebElement successSpan = driver.findElement(By.id("sp-save-success"));
		WebElement homeLink = successSpan.findElement(By.linkText("here"));
		homeLink.click();
	}

	private boolean checkNoteExist(String noteTitle, String noteDescription)
	{
		boolean noteFound = false;
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		WebElement userTable = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userTable")));

		// Locate the tbody of the table.
		WebElement tbody = userTable.findElement(By.tagName("tbody"));

		// Locate the rows of the tbody (note entries).
		List<WebElement> rows = tbody.findElements(By.tagName("tr"));

		// Loop through each row and check the title and description.
		for (WebElement row : rows) {
			// Locate the title in the <th> tag (Title column).
			WebElement titleElement = row.findElement(By.tagName("th"));

			// Locate the description in the second <td> tag (Description column).
			List<WebElement> tdElements = row.findElements(By.tagName("td"));
			if (tdElements.size() > 1) {  // Ensure there are multiple <td> elements
				WebElement descriptionElement = tdElements.get(1);  // The second <td> is the description

				// Extract the text content from both title and description elements.
				String actualTitle = titleElement.getText();
				String actualDescription = descriptionElement.getText();

				// Check if the actual title and description match the expected values.
				if (actualTitle.equals(noteTitle) && actualDescription.equals(noteDescription)) {
					noteFound = true;
					break;  // Stop the loop if the expected note is found.
				}
			}
		}
		return  noteFound;
	}

	private void doUpdateNote(String oldTitle, String oldDescription, String newTitle, String newDescription)
	{
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userTable")));
		// Locate the user table and tbody.
		WebElement userTable = driver.findElement(By.id("userTable"));
		WebElement tbody = userTable.findElement(By.tagName("tbody"));

		// Find the rows of the tbody (note entries).
		List<WebElement> rows = tbody.findElements(By.tagName("tr"));

		// Loop through the rows to find the note to edit.
		for (WebElement row : rows) {
			WebElement titleElement = row.findElement(By.tagName("th"));
			WebElement descriptionElement = row.findElements(By.tagName("td")).get(1);

			// Check if this is the note we want to edit.
			if (titleElement.getText().equals(oldTitle) && descriptionElement.getText().equals(oldDescription)) {

				// Click the edit button to open the modal.
				WebElement editButton = row.findElement(By.cssSelector("button.btn-success"));
				editButton.click();

				// Wait for the modal to be visible (if applicable).
				webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));

				// Update the title and description in the modal.
				WebElement titleInput = driver.findElement(By.id("note-title"));
				WebElement descriptionInput = driver.findElement(By.id("note-description"));

				titleInput.clear();
				titleInput.sendKeys(newTitle);
				descriptionInput.clear();
				descriptionInput.sendKeys(newDescription);

				// Save changes.
				driver.findElement(By.id("button-note-save")).click();

				webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp-save-success")));
				WebElement successSpan = driver.findElement(By.id("sp-save-success"));
				WebElement homeLink = successSpan.findElement(By.linkText("here"));
				homeLink.click();
				break;
			}
		}
	}

	private void doDeleteNote(String noteTitle, String noteDescription)
	{
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userTable")));
		// Locate the user table and tbody.
		WebElement userTable = driver.findElement(By.id("userTable"));
		WebElement tbody = userTable.findElement(By.tagName("tbody"));

		// Find the rows of the tbody (note entries).
		List<WebElement> rows = tbody.findElements(By.tagName("tr"));

		// Loop through the rows to find the note to edit.
		for (WebElement row : rows) {
			WebElement titleElement = row.findElement(By.tagName("th"));
			WebElement descriptionElement = row.findElements(By.tagName("td")).get(1);

			// Check if this is the note we want to edit.
			if (titleElement.getText().equals(noteTitle) && descriptionElement.getText().equals(noteDescription)) {

				// Click the delete link to open the modal.
				WebElement deleteLink = row.findElement(By.linkText("Delete"));
				deleteLink.click();

				// Handle the confirmation alert (if applicable).
				driver.switchTo().alert().accept();

				webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp-save-success")));
				WebElement successSpan = driver.findElement(By.id("sp-save-success"));
				WebElement homeLink = successSpan.findElement(By.linkText("here"));
				homeLink.click();
				break;
			}
		}
	}

	private void doCreateCredential(String url, String userName, String password)
	{
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));
		driver.findElement(By.id("nav-credentials-tab")).click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("button-new-cred")));
		driver.findElement(By.id("button-new-cred")).click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url")));
		driver.findElement(By.id("credential-url")).sendKeys(url);
		driver.findElement(By.id("credential-username")).sendKeys(userName);
		driver.findElement(By.id("credential-password")).sendKeys(password);

		driver.findElement(By.id("button-cred-save")).click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp-save-success")));
		WebElement successSpan = driver.findElement(By.id("sp-save-success"));
		WebElement homeLink = successSpan.findElement(By.linkText("here"));
		homeLink.click();
	}

	private MutablePair<Boolean, String> checkCredentialExist(String url, String userName, String password)
	{
		boolean credentialFound = false;
		MutablePair<Boolean, String> result = MutablePair.of(credentialFound, password);
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		WebElement credentialTable = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialTable")));

		// Locate the tbody of the table.
		WebElement tbody = credentialTable.findElement(By.tagName("tbody"));

		// Locate the rows of the tbody (note entries).
		List<WebElement> rows = tbody.findElements(By.tagName("tr"));

		// Loop through each row and check the title and description.
		for (WebElement row : rows) {
			// Locate the URL in the <th> tag (URL column).
			WebElement URLElement = row.findElement(By.tagName("th"));

			// Locate the username in the second <td> tag (Username column) and the password in the third.
			List<WebElement> tdElements = row.findElements(By.tagName("td"));
			if (tdElements.size() > 1) {  // Ensure there are multiple <td> elements
				WebElement userNameElement = tdElements.get(1);  // The second <td> is the username
				WebElement passwordElement = tdElements.get(2);  // The third <td> is the password

				// Extract the text content from url, username, password elements.
				String actualURL = URLElement.getText();
				String actualUserName = userNameElement.getText();
				String actualPassword = passwordElement.getText(); //encrypted password

				// Check if the actual title and description match the expected values.
				if (actualURL.equals(url) && actualUserName.equals(userName)) {
					credentialFound = true;
					result.setRight(actualPassword);
					break;  // Stop the loop if the expected note is found.
				}
			}
		}

		result.setLeft(credentialFound);
		return result;
	}

	private String doUpdateCredential(String[] oldCredential, String[] newCredential)
	{
		String oldDisplayedPassword = "";

		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialTable")));

		// Locate the credential table and tbody.
		WebElement userTable = driver.findElement(By.id("credentialTable"));
		WebElement tbody = userTable.findElement(By.tagName("tbody"));

		// Find the rows of the tbody (credential entries).
		List<WebElement> rows = tbody.findElements(By.tagName("tr"));

		// Loop through the rows to find the credential to view/edit.
		for (WebElement row : rows) {
			WebElement URLElement = row.findElement(By.tagName("th"));
			WebElement userNameElement = row.findElements(By.tagName("td")).get(1);

			// Check if this is the note we want to edit.
			if (URLElement.getText().equals(oldCredential[0]) && userNameElement.getText().equals(oldCredential[1])) {

				// Click the edit button to open the modal.
				WebElement editButton = row.findElement(By.cssSelector("button.btn-success"));
				editButton.click();

				// Wait for the modal to be visible (if applicable).
				webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url")));

				// Update the credential in the modal.
				WebElement URLInput = driver.findElement(By.id("credential-url"));
				WebElement userNameInput = driver.findElement(By.id("credential-username"));
				WebElement passwordInput = driver.findElement(By.id("credential-password"));


				if(URLInput.getAttribute("value").equals(oldCredential[0]) && userNameInput.getAttribute("value").equals(oldCredential[1])) {
					oldDisplayedPassword = passwordInput.getAttribute("value");
					URLInput.clear();
					URLInput.sendKeys(newCredential[0]);
					userNameInput.clear();
					userNameInput.sendKeys(newCredential[1]);
					passwordInput.clear();
					passwordInput.sendKeys(newCredential[2]);

					// Save changes.
					driver.findElement(By.id("button-cred-save")).click();

					webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp-save-success")));
					WebElement successSpan = driver.findElement(By.id("sp-save-success"));
					WebElement homeLink = successSpan.findElement(By.linkText("here"));
					homeLink.click();
					break;
				}
			}
		}
		return oldDisplayedPassword;
	}

	private void doDeleteCredential(String[] credential)
	{
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialTable")));

		// Locate the credential table and tbody.
		WebElement userTable = driver.findElement(By.id("credentialTable"));
		WebElement tbody = userTable.findElement(By.tagName("tbody"));

		// Find the rows of the tbody (credential entries).
		List<WebElement> rows = tbody.findElements(By.tagName("tr"));

		// Loop through the rows to find the credential to edit.
		for (WebElement row : rows) {
			WebElement URLElement = row.findElement(By.tagName("th"));
			WebElement userNameElement = row.findElements(By.tagName("td")).get(1);

			// Check if this is the note we want to edit.
			if (URLElement.getText().equals(credential[0]) && userNameElement.getText().equals(credential[1])) {

				// Click the delete link to open the modal.
				WebElement deleteLink = row.findElement(By.linkText("Delete"));
				deleteLink.click();

				// Handle the confirmation alert (if applicable).
				driver.switchTo().alert().accept();

				webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sp-save-success")));
				WebElement successSpan = driver.findElement(By.id("sp-save-success"));
				WebElement homeLink = successSpan.findElement(By.linkText("here"));
				homeLink.click();
				break;
			}
		}
	}

	/**
	 * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the 
	 * rest of your code. 
	 * This test is provided by Udacity to perform some basic sanity testing of 
	 * your code to ensure that it meets certain rubric criteria. 
	 * 
	 * If this test is failing, please ensure that you are handling redirecting users 
	 * back to the login page after a succesful sign up.
	 * Read more about the requirement in the rubric: 
	 * https://review.udacity.com/#!/rubrics/2724/view 
	 */
	@Test
	public void testRedirection() {
		// Create a test account
		doMockSignUp("Redirection","Test","RT","123");

		// Check if we have been redirected to the log in page.
		Assertions.assertEquals("http://localhost:" + this.port + "/login", driver.getCurrentUrl());
	}

	/**
	 * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the 
	 * rest of your code. 
	 * This test is provided by Udacity to perform some basic sanity testing of 
	 * your code to ensure that it meets certain rubric criteria. 
	 * 
	 * If this test is failing, please ensure that you are handling bad URLs 
	 * gracefully, for example with a custom error page.
	 * 
	 * Read more about custom error pages at: 
	 * https://attacomsian.com/blog/spring-boot-custom-error-page#displaying-custom-error-page
	 */
	@Test
	public void testBadUrl() {
		// Create a test account
		doMockSignUp("URL","Test","UT","123");
		doLogIn("UT", "123");
		
		// Try to access a random made-up URL.
		driver.get("http://localhost:" + this.port + "/some-random-page");
		Assertions.assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
	}


	/**
	 * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the 
	 * rest of your code. 
	 * This test is provided by Udacity to perform some basic sanity testing of 
	 * your code to ensure that it meets certain rubric criteria. 
	 * 
	 * If this test is failing, please ensure that you are handling uploading large files (>1MB),
	 * gracefully in your code. 
	 * 
	 * Read more about file size limits here: 
	 * https://spring.io/guides/gs/uploading-files/ under the "Tuning File Upload Limits" section.
	 */
	@Test
	public void testLargeUpload() {
		// Create a test account
		doMockSignUp("Large File","Test","LFT","123");
		doLogIn("LFT", "123");

		// Try to upload an arbitrary large file
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		String fileName = "upload5m.zip";

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fileUpload")));
		WebElement fileSelectButton = driver.findElement(By.id("fileUpload"));
		fileSelectButton.sendKeys(new File(fileName).getAbsolutePath());

		WebElement uploadButton = driver.findElement(By.id("uploadButton"));
		uploadButton.click();
		try {
			webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("success")));
		} catch (org.openqa.selenium.TimeoutException e) {
			System.out.println("Large File upload failed");
		}
		Assertions.assertFalse(driver.getPageSource().contains("HTTP Status 403 – Forbidden"));

	}

	/*Verifies that an unauthorized user can only access the login and signup pages.*/
	@Test
	public void testUnauthorizedAccess()
	{
		driver.get("http://localhost:" + this.port + "/login");
		Assertions.assertEquals("Login", driver.getTitle());

		driver.get("http://localhost:" + this.port + "/signup");
		Assertions.assertEquals("Sign Up", driver.getTitle());

		driver.get("http://localhost:" + this.port + "/home");
		Assertions.assertEquals("Login", driver.getTitle());

		driver.get("http://localhost:" + this.port + "/result");
		Assertions.assertEquals("Login", driver.getTitle());
	}
	/* Signs up a new user, logs in, verifies that the home page is accessible,
	 * logs out, and verifies that the home page is no longer accessible.*/
	@Test
	public void testNewUser()
	{
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		doMockSignUp("NewUser","Test","NU","123");
		doLogIn("NU","123");
		Assertions.assertEquals("Home", driver.getTitle());

		WebElement logoutButton = driver.findElement(By.id("submit-logout"));
		logoutButton.click();

		webDriverWait.until(ExpectedConditions.titleContains("Login"));
		Assertions.assertEquals("Login", driver.getTitle());

		driver.get("http://localhost:" + this.port + "/home");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	/* Creates a note, and verifies it is displayed*/
	@Test
	public void testCreateNote()
	{
		String noteTitle = "Create Note Title";
		String noteDescription = "Create Note Description";

		doMockSignUp("CreateNoteUser","Test","CNU","123");
		doLogIn("CNU","123");

		doCreateNote(noteTitle, noteDescription);

		// Verify that the note was found.
		Assertions.assertTrue(checkNoteExist(noteTitle, noteDescription), "Note Failed to be Created");
	}

	/* A test that edits an existing note and verifies that the changes are displayed*/
	@Test
	public void testEditNote()
	{
		String oldNoteTitle = "Create Note Title";
		String oldNoteDescription = "Create Note Description";
		String newNoteTitle = "New Note Title";
		String newNoteDescription = "New Creation of Note Description";

		doMockSignUp("EditNoteUser","Test","ENU","123");
		doLogIn("ENU","123");

		doCreateNote(oldNoteTitle, oldNoteDescription);
		// Verify that the note was found.
		Assertions.assertTrue(checkNoteExist(oldNoteTitle, oldNoteDescription), "Note Failed to be Created");

		doUpdateNote(oldNoteTitle, oldNoteDescription, newNoteTitle, newNoteDescription);
		// Verify that the note was found.
		Assertions.assertTrue(checkNoteExist(newNoteTitle, newNoteDescription), "Note Failed to be Updated");
		// Verify that the old note was not found.
		Assertions.assertFalse(checkNoteExist(oldNoteTitle, oldNoteDescription), "Note Failed to be Updated");
	}

	/* A test that deletes a note and verifies that the note is no longer displayed.*/
	@Test
	public void testDeleteNote()
	{
		String noteTitle = "Create Note Title";
		String noteDescription = "Create Note Description";
		doMockSignUp("DeleteNoteUser","Test","DNU","123");
		doLogIn("DNU","123");

		doCreateNote(noteTitle, noteDescription);
		Assertions.assertTrue(checkNoteExist(noteTitle, noteDescription), "Note Failed to be Created");

		doDeleteNote(noteTitle, noteDescription);
		Assertions.assertFalse(checkNoteExist(noteTitle, noteDescription), "Note Failed to be Deleted");

	}

	/* A test that creates a set of credentials, verifies that they are displayed,
	 * and verifies that the displayed password is encrypted.*/
	@Test
	public void testCreateCredential()
	{
		doMockSignUp("CreateCredentialUser","Test","CCU","123");
		doLogIn("CCU","123");
		String[] url ={"www.google.com", "www.facebook.com", "www.youtube.com"};
		String[] userName = {"GoogleNedaa", "FacbookNedaa", "YoutubeNedaa"};
		String[] password = {"1234", "QwEr", "Yt123"};
		MutablePair<Boolean, String> result = MutablePair.of(false, null);

		for(int i = 0; i < url.length; i++)
		{
			doCreateCredential(url[i], userName[i], password[i]);
			result = checkCredentialExist(url[i], userName[i], password[i]);
			// Assert that the credentials are found
			Assertions.assertTrue(result.getLeft(), "Failed to saved the Credentials.");

			// Verify that the password is encrypted and not equal to the plain text password
			Assertions.assertNotEquals(result.getRight(), password[i], "The displayed password should be encrypted, but it matches the plain text password.");
		}
	}

	/* A test that views an existing set of credentials, verifies that the viewable password is unencrypted,
	 * edits the credentials, and verifies that the changes are displayed*/
	@Test
	public void testEditCredential()
	{
		doMockSignUp("EditCredentialUser","Test","ECU","123");
		doLogIn("ECU","123");
		String[] oldCredential ={"www.google.com", "GoogleNedaa", "1234"};
		String[] newCredential = {"www.facebook.com", "FacebookNedaa", "QwEr"};

		doCreateCredential(oldCredential[0], oldCredential[1], oldCredential[2]);
		// Assert that the credentials are found
		Assertions.assertTrue(checkCredentialExist(oldCredential[0], oldCredential[1], oldCredential[2]).getLeft(), "Failed to saved the Credentials.");

		String oldDisplayedPassword = doUpdateCredential(oldCredential, newCredential);
		Assertions.assertEquals(oldCredential[2], oldDisplayedPassword,"The old password is not displayed as plaintext or not equal");
		Assertions.assertFalse(checkCredentialExist(oldCredential[0], oldCredential[1], oldCredential[2]).getLeft(), "Failed to update the Credentials.");
		Assertions.assertTrue(checkCredentialExist(newCredential[0], newCredential[1], newCredential[2]).getLeft(), "Failed to update the Credentials.");

	}

	/* A test that deletes an existing set of credentials and
	 * verifies that the credentials are no longer displayed.*/
	@Test
	public void testDeleteCredential()
	{
		doMockSignUp("DeleteCredentialUser","Test","DCU","123");
		doLogIn("DCU","123");
		String[] credential ={"www.google.com", "GoogleNedaa", "1234"};

		doCreateCredential(credential[0], credential[1], credential[2]);
		// Assert that the credentials are found
		Assertions.assertTrue(checkCredentialExist(credential[0], credential[1], credential[2]).getLeft(), "Failed to saved the Credentials.");

		doDeleteCredential(credential);
		Assertions.assertFalse(checkCredentialExist(credential[0], credential[1], credential[2]).getLeft(), "Failed to delete the Credential.");

	}
}

