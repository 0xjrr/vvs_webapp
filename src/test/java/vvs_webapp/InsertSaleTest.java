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

public class InsertSaleTest {

    @Test
    public void testInsertNewSale() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            // Step 1: Get a valid customer VAT
            HtmlPage customersPage = webClient.getPage("http://localhost:8080/VVS_webappdemo/GetAllCustomersPageController");
            HtmlTable customersTable = customersPage.getHtmlElementById("clients");
            List<HtmlTableRow> rows = customersTable.getRows();
            String vat = rows.get(1).getCell(2).asText(); // Assuming the first row contains valid data

            // Step 2: Navigate to the add sale page
            HtmlPage addSalePage = webClient.getPage("http://localhost:8080/VVS_webappdemo/addSale.html");

            // Step 3: Fill out the form and submit the new sale
            HtmlForm form = addSalePage.getForms().get(0);
            HtmlTextInput vatField = form.getInputByName("customerVat");
            vatField.setValueAttribute(vat);
            HtmlSubmitInput submitButton = form.getInputByValue("Add Sale");
            HtmlPage resultPage = submitButton.click();
            Assert.assertFalse(resultPage.asText().contains("Error Messages"));

            // Step 4: Verify the new sale is listed as open for the customer
            HtmlPage salesInfoPage = webClient.getPage("http://localhost:8080/VVS_webappdemo/GetSalePageController?customerVat=" + vat);
            List<HtmlTable> tables = salesInfoPage.getByXPath("//table");
            HtmlTable salesTable = null;
            for (HtmlTable table : tables) {
                HtmlTableRow headerRow = table.getRow(0);
                if (headerRow.getCell(0).asText().equals("Id") && headerRow.getCell(1).asText().equals("Date") &&
                    headerRow.getCell(2).asText().equals("Total") && headerRow.getCell(3).asText().equals("Status") &&
                    headerRow.getCell(4).asText().equals("Customer Vat Number")) {
                    salesTable = table;
                    break;
                }
            }
            Assert.assertNotNull(salesTable);

            boolean saleFound = false;
            for (HtmlTableRow row : salesTable.getRows()) {
                if (row.getCell(4).asText().equals(vat) && row.getCell(3).asText().equals("O")) { // Assuming "O" means open sale
                    saleFound = true;
                    break;
                }
            }
            Assert.assertTrue(saleFound);
        }
    }
}
