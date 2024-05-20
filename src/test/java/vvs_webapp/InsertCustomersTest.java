package vvs_webapp;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Random;

public class InsertCustomersTest {

    @Test
    public void testInsertNewCustomers() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            Random random = new Random();

            HtmlPage addCustomerPage = webClient.getPage("http://localhost:8080/VVS_webappdemo/addCustomer.html");

            // Step 1: Insert the first customer
            String vat1 = String.valueOf(random.nextInt(900000000) + 100000000);
            String phone1 = String.valueOf(random.nextInt(900000000) + 100000000);
            boolean success = addCustomer(addCustomerPage, vat1, "John Doe", phone1, webClient);
            while (!success) {
                vat1 = String.valueOf(random.nextInt(900000000) + 100000000);
                phone1 = String.valueOf(random.nextInt(900000000) + 100000000);
                success = addCustomer(addCustomerPage, vat1, "John Doe", phone1, webClient);
            }

            // Step 2: Insert the second customer
            String vat2 = String.valueOf(random.nextInt(900000000) + 100000000);
            String phone2 = String.valueOf(random.nextInt(900000000) + 100000000);
            success = addCustomer(addCustomerPage, vat2, "Jane Smith", phone2, webClient);
            while (!success) {
                vat2 = String.valueOf(random.nextInt(900000000) + 100000000);
                phone2 = String.valueOf(random.nextInt(900000000) + 100000000);
                success = addCustomer(addCustomerPage, vat2, "Jane Smith", phone2, webClient);
            }

            // Step 3: Verify both customers are listed
            HtmlPage customersPage = webClient.getPage("http://localhost:8080/VVS_webappdemo/GetAllCustomersPageController");
            HtmlTable customersTable = customersPage.getHtmlElementById("clients");
            List<HtmlTableRow> rows = customersTable.getRows();
            boolean foundJohn = false;
            boolean foundJane = false;
            for (HtmlTableRow row : rows) {
                if (row.asText().contains("John Doe") && row.asText().contains(phone1) && row.asText().contains(vat1)) {
                    foundJohn = true;
                }
                if (row.asText().contains("Jane Smith") && row.asText().contains(phone2) && row.asText().contains(vat2)) {
                    foundJane = true;
                }
            }
            Assert.assertTrue(foundJohn);
            Assert.assertTrue(foundJane);
        }
    }

    private boolean addCustomer(HtmlPage page, String vat, String designation, String phone, WebClient webClient) throws Exception {
        HtmlForm form = page.getForms().get(0);
        HtmlTextInput vatField = form.getInputByName("vat");
        HtmlTextInput designationField = form.getInputByName("designation");
        HtmlTextInput phoneField = form.getInputByName("phone");
        vatField.setValueAttribute(vat);
        designationField.setValueAttribute(designation);
        phoneField.setValueAttribute(phone);
        HtmlSubmitInput submitButton = form.getInputByValue("Get Customer");
        HtmlPage resultPage = submitButton.click();
        return !resultPage.asText().contains("Error Messages");
    }
}
