import models.Car;
import org.greports.exceptions.GreportsRuntimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionsTest extends AbstractTest {

    @Test
    void parseExceptionTest() {
        testNullObjectException(() -> greportsGenerator.parse(null, Car.REPORT_NAME, Car.class));
    }

    @Test
    void configuratorOverridenColumnsExceptionTest() {
        testNullObjectException(() -> configurator.setOverriddenTitles(null));
    }
    @Test
    void configuratorOverrideColumnsExceptionTest() {
        testNullObjectException(() -> configurator.overrideTitle(null, null));
    }

    @Test
    void configuratorRemovedColumnsExceptionTest() {
        testNullObjectException(() -> configurator.setRemovedColumns(null));
    }

    @Test
    void configuratorAutosizedColumnsExceptionTest() {
        testNullObjectException(() -> configurator.setAutosizedColumns(null));
    }

    @Test
    void configuratorSheetNameExceptionTest() {
        testNullObjectException(() -> configurator.setSheetName(null));
    }

    private void testNullObjectException(Executable executable) {
        Exception exception = assertThrows(GreportsRuntimeException.class, executable);
        assertEquals("The object is null", exception.getMessage());
    }
}
