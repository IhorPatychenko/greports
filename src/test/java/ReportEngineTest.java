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
                new BackDto(1, "Juan", "Velasquez"),
                new BackDto(2, "Ihor", "Patychenko")
        ));
        final ReportData reportData = reportEngine.parse(collection, "Report1").getData();
        assertEquals(2, reportData.getRowsCount());
    }

    @Test
    void getData() throws Exception {
        Collection<BackDto> collection = new ArrayList<>(Arrays.asList(
                new BackDto(1, "Juan", "Velasquez"),
                new BackDto(2, "Ihor", "Patychenko")
        ));
        final ReportData reportData = reportEngine.parse(collection, "Report1").getData();
        assertEquals("Ihor", reportData.getRow(1).getColumns().get(1).getValue());
        assertEquals("Patychenko", reportData.getRow(1).getColumns().get(2).getValue());
    }

    @Test
    void testTranslations() throws Exception {
        final BackDto backDto = new BackDto(2, "Ihor", "Patychenko");
        final ReportData reportData = reportEngine.parse(backDto, "Report1").getData();
        assertEquals("Name", reportData.getHeader().getCell(1).getTitle());
    }
}