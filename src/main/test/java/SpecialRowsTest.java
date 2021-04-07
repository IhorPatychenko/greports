import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpecialRowsTest extends AbstractTest {

    @Test
    void specialRowTotalKeyTest() {
        final Object cellValue = reportDataReader.getCellValue("Cars", null, 0);
        assertEquals("Total", cellValue);
    }

    @Test
    void specialRowTotalModelsTest() {
        final Number cellValue = reportDataReader.getCellValue("Cars", null, 1, Number.class);
        assertEquals(4, cellValue.intValue());
    }

    @Test
    void specialRowTotalPriceTest() {
        final Double cellValue = reportDataReader.getCellValue("Cars", null, 5, Double.class);
        assertEquals(471360.0, cellValue, 5);
    }
}
