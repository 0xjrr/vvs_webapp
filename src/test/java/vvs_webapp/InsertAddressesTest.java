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

public class InsertAddressesTest {

    @Test
    public void testInsertNewAddresses() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            // Step 1: Get the VAT numbers from the customers' page
            final HtmlPage customersPage = webClient.getPage("http://localhost:8080/VVS_webappdemo/GetAllCustomersPageController");
            final HtmlTable customersTable = customersPage.getHtmlElementById("clients");
            List<HtmlTableRow> rows = customersTable.getRows();
            String vat = null;
            if (rows.size() > 1) { // Assuming there's at least one customer
                vat = rows.get(1).getCell(2).asText(); // Get the VAT number from the first customer's row
            }

            // Step 2: Insert two new addresses for the customer
            if (vat != null) {
                final HtmlPage addAddressPage = webClient.getPage("http://localhost:8080/VVS_webappdemo/addAddressToCustomer.html");
                final HtmlForm form = addAddressPage.getForms().get(0); // Get the form

                // Fill out the form for the first address
                final HtmlTextInput vatField = form.getInputByName("vat");
                final HtmlTextInput addressField = form.getInputByName("address");
                final HtmlTextInput doorField = form.getInputByName("door");
                final HtmlTextInput postalCodeField = form.getInputByName("postalCode");
                final HtmlTextInput localityField = form.getInputByName("locality");
                vatField.setValueAttribute(vat);
                addressField.setValueAttribute("123 Main St");
                doorField.setValueAttribute("1");
                postalCodeField.setValueAttribute("12345");
                localityField.setValueAttribute("City A");
                final HtmlSubmitInput submitButton = form.getInputByValue("Insert");
                HtmlPage resultPage = submitButton.click();

                // Verify the result for the first address by checking the redirected page
                Assert.assertTrue(resultPage.asText().contains("123 Main St"));

                // Fill out the form for the second address
                addressField.setValueAttribute("456 Elm St");
                doorField.setValueAttribute("2");
                postalCodeField.setValueAttribute("67890");
                localityField.setValueAttribute("City B");
                resultPage = submitButton.click();

                // Verify the result for the second address by checking the redirected page
                Assert.assertTrue(resultPage.asText().contains("456 Elm St"));

                // Step 3: Verify that the table of addresses includes the new addresses
                final HtmlPage customerPage = webClient.getPage("http://localhost:8080/VVS_webappdemo/GetCustomerPageController?vat=" + vat);
                String pageText = customerPage.asText();
                Assert.assertTrue(pageText.contains("123 Main St"));
                Assert.assertTrue(pageText.contains("456 Elm St"));
            }
        }
    }
}
