package vvs_webapp;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Random;

public class CustomerSaleDeliveryTest {

    private String generateRandomVat() {
        Random random = new Random();
        return String.valueOf(100000000 + random.nextInt(900000000));
    }

    @Test
    public void testCustomerSaleDelivery() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            // Ignore CSS 404 errors
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            String customerVat = null;
            HtmlPage resultPage = null;

            // Step 1: Create a new customer with a unique VAT number
            System.out.println("Step 1: Creating new customer");
            while (true) {
                String vat = generateRandomVat();
                HtmlPage addCustomerPage = webClient.getPage("http://localhost:8080/VVS_webappdemo/addCustomer.html");
                HtmlForm addCustomerForm = addCustomerPage.getForms().get(0);
                HtmlTextInput vatField = addCustomerForm.getInputByName("vat");
                HtmlTextInput designationField = addCustomerForm.getInputByName("designation");
                HtmlTextInput phoneField = addCustomerForm.getInputByName("phone");

                vatField.setValueAttribute(vat);
                designationField.setValueAttribute("Test Customer");
                phoneField.setValueAttribute("987654321");

                HtmlSubmitInput addCustomerButton = addCustomerForm.getInputByValue("Get Customer");
                resultPage = addCustomerButton.click();

                if (!resultPage.asText().contains("Error Messages")) {
                    customerVat = vat;
                    break;
                }
            }

            Assert.assertNotNull("Failed to create a new customer after multiple attempts", customerVat);

            // Step 2: Create a new sale for the customer
            System.out.println("Step 2: Creating new sale");
            HtmlPage addSalePage = webClient.getPage("http://localhost:8080/VVS_webappdemo/addSale.html");
            HtmlForm addSaleForm = addSalePage.getForms().get(0);
            HtmlTextInput customerVatField = addSaleForm.getInputByName("customerVat");
            customerVatField.setValueAttribute(customerVat);

            HtmlSubmitInput addSaleButton = addSaleForm.getInputByValue("Add Sale");
            HtmlPage saleResultPage = addSaleButton.click();

            // Verify the sale was added successfully
            Assert.assertFalse(saleResultPage.asText().contains("Error Messages"));

            // Find the sale ID from the sales table
            HtmlTable salesTable = (HtmlTable) saleResultPage.getByXPath("//table[@class='w3-table w3-bordered']").get(0);
            HtmlTableRow saleRow = salesTable.getRows().get(1); // Assuming the first row is the header
            String saleId = saleRow.getCell(0).asText();

            // Step 3: Insert a delivery for the sale
            System.out.println("Step 3: Adding delivery");
            HtmlPage addDeliveryPage = webClient.getPage("http://localhost:8080/VVS_webappdemo/AddSaleDeliveryPageController?vat=" + customerVat);
            HtmlForm addDeliveryForm = addDeliveryPage.getForms().get(0);
            HtmlTextInput saleIdField = addDeliveryForm.getInputByName("sale_id");
            HtmlTextInput addressIdField = addDeliveryForm.getInputByName("addr_id");

            saleIdField.setValueAttribute(saleId);
            addressIdField.setValueAttribute("1"); // Assuming address ID 1

            HtmlSubmitInput addDeliveryButton = addDeliveryForm.getInputByValue("Insert");
            HtmlPage deliveryResultPage = addDeliveryButton.click();

            // Verify the delivery was added successfully
            Assert.assertFalse(deliveryResultPage.asText().contains("Error Messages"));

         // Step 4: Verify the sale delivery
            System.out.println("Step 4: Verifying delivery");
            System.out.println("Step 4: VAT: " + customerVat);
            // Log the page content for debugging
            System.out.println(deliveryResultPage.asXml());
            HtmlTable deliveryTable = (HtmlTable) deliveryResultPage.getByXPath("//table[@class='w3-table w3-bordered']").get(0);
            boolean deliveryFound = false;
            for (HtmlTableRow row : deliveryTable.getRows()) {
                List<HtmlTableCell> cells = row.getCells();
                if (cells.size() > 1 && cells.get(1).asText().equals(saleId)) {
                    deliveryFound = true;
                    break;
                }
            }

            Assert.assertTrue("The delivery was not found in the delivery table", deliveryFound);
        }
    }
}
