import data.ReportData;
import models.BackDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class ReportEngineTest {

    private final ReportEngine<BackDto> reportEngine = new ReportEngine<>("es");

    @Test
    void testParse() throws Exception {
        BackDto dto = new BackDto(1, "Ihor", "Patychenko");
        final ReportData reportData = reportEngine.parse(dto, "Report1").getData();
        assertEquals("Report1", reportData.getName());
    }

    @Test
    void testParseCollection() throws Exception {
        Collection<BackDto> collection = new ArrayList<>(Arrays.asList(
                new BackDto(1, "Ihor", "Patychenko"),
                new BackDto(2, "Ihor", "Patychenko")
        ));
        final ReportData reportData = reportEngine.parse(collection, "Report1").getData();
        assertEquals(2, reportData.getRows().size());
    }

    @Test
    void getData() {
    }

    @Test
    void generateReport() {
    }
}