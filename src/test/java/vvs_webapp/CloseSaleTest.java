package vvs_webapp;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CloseSaleTest {

    @Test
    public void testCloseSale() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            // Ignore CSS 404 errors
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            // Step 1: Navigate to the page listing sales
            HtmlPage page = webClient.getPage("http://localhost:8080/VVS_webappdemo/UpdateSaleStatusPageControler");

            // Log the page content for debugging
            System.out.println(page.asXml());

            // Find the table with sales data
            HtmlTable salesTable = (HtmlTable) page.getByXPath("//table").get(0);
            HtmlTableRow saleRow = null;
            String saleId = null;

            // Log the number of rows in the table for debugging
            System.out.println("Number of rows in the table: " + salesTable.getRowCount());

            // Find the first sale with status "O"
            List<HtmlTableRow> rows = salesTable.getRows();
            for (HtmlTableRow row : rows) {
                if (row.getCells().size() > 3 && row.getCell(3).asText().equals("O")) {
                    saleRow = row;
                    saleId = saleRow.getCell(0).asText();
                    break;
                }
            }

            Assert.assertNotNull("No open sale found", saleId);

            // Step 2: Close the sale
            HtmlForm closeSaleForm = page.getForms().get(0);
            HtmlTextInput saleIdField = closeSaleForm.getInputByName("id");
            saleIdField.setValueAttribute(saleId);
            HtmlSubmitInput closeSaleButton = closeSaleForm.getInputByValue("Close Sale");
            HtmlPage resultPage = closeSaleButton.click();

            // Verify the result page does not contain error messages
            Assert.assertFalse(resultPage.asText().contains("Error Messages"));

            // Step 3: Navigate back to the page to verify the sale is closed
            HtmlPage updatedPage = webClient.getPage("http://localhost:8080/VVS_webappdemo/UpdateSaleStatusPageControler");
            HtmlTable updatedSalesTable = (HtmlTable) updatedPage.getByXPath("//table").get(0);

            boolean isSaleClosed = false;
            for (HtmlTableRow row : updatedSalesTable.getRows()) {
                if (row.getCells().size() > 3 && row.getCell(0).asText().equals(saleId) && row.getCell(3).asText().equals("C")) {
                    isSaleClosed = true;
                    break;
                }
            }

            Assert.assertTrue("The sale was not closed successfully", isSaleClosed);
        }
    }
}
