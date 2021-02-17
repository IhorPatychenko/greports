import org.junit.Assert;
import org.junit.Test;

public class SpecialRowsTest extends AbstractTest {

    @Test
    public void specialRowTotalKeyTest() {
        final Object cellValue = reportDataReader.getCellValue("Cars", null, 0);
        Assert.assertEquals("Total", cellValue);
    }

    @Test
    public void specialRowTotalModelsTest() {
        final Number cellValue = reportDataReader.getCellValue("Cars", null, 1, Number.class);
        Assert.assertEquals(4, cellValue.intValue());
    }

    @Test
    public void specialRowTotalPriceTest() {
        final Double cellValue = reportDataReader.getCellValue("Cars", null, 5, Double.class);
        Assert.assertEquals(471360.0, cellValue, 5);
    }
}
