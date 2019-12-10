import models.BackDto;
import models.BackDto2;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

class ReportDataParserTest {

    private final ReportDataParser reportDataParser = new ReportDataParser("es");

    @Test
    void testParse() throws Exception {
//        BackDto dto = new BackDto(1, "Ihor", "Patychenko");
//        final ReportData reportData = reportDataParser.parse(dto, "Report1").getData();
//        assertEquals("Report1", reportData.getName());
    }

    @Test
    void testParseCollection() throws Exception {
//        Collection<BackDto> collection = new ArrayList<>(Arrays.asList(
//                new BackDto(1, "Juan", "Vasquez"),
//                new BackDto(2, "Ihor", "Patychenko")
//        ));
//        final ReportData reportData = reportDataParser.parse(collection, "Report1").getData();
//        assertEquals(2, reportData.getRowsCount());
    }

    @Test
    void testGetData() throws Exception {
//        Collection<BackDto> collection = new ArrayList<>(Arrays.asList(
//                new BackDto(1, "Juan", "Vasquez"),
//                new BackDto(2, "Ihor", "Patychenko")
//        ));
//        final ReportData reportData = reportDataParser.parse(collection, "Report1").getData();
//        assertEquals("Ihor", reportData.getRow(1).getColumns().get(1).getValue());
//        assertEquals("Patychenko", reportData.getRow(1).getColumns().get(2).getValue());
    }

    @Test
    void testTranslations() throws Exception {
//        final BackDto backDto = new BackDto(2, "Ihor", "Patychenko");
//        final ReportData reportData = reportDataParser.parse(backDto, "Report1").getData();
//        assertEquals("Nombre", reportData.getHeader().getCell(1).getTitle());
//        assertEquals("Apellidos", reportData.getHeader().getCell(2).getTitle());
    }

    @Test
    void testDataType() throws Exception {
//        final BackDto backDto = new BackDto(2, "Ihor", "Patychenko");
//        final ReportData reportData = reportDataParser.parse(backDto, "Report1").getData();
//        assertEquals(reportData.getRow(0).getColumn(3).getDataClass(), Date.class);
    }

    @Test
    void testSaveFile() throws Exception {
        int i = 0;
        Collection<BackDto> collection = new ArrayList<>(Arrays.asList(
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito"),
            new BackDto(++i, "Pepito", "Fulanito")
        ));
        Collection<BackDto2> collection2 = new ArrayList<>(Arrays.asList(
                new BackDto2(44, "Jose Manuel", "Garcia"),
                new BackDto2(44, "Jose Manuel", "Garcia"),
                new BackDto2(44, "Jose Manuel", "Garcia"),
                new BackDto2(44, "Jose Manuel", "Garcia"),
                new BackDto2(44, "Jose Manuel", "Garcia")
        ));
        final ReportGenerator reportGenerator = new ReportGenerator("es");
        final ReportGeneratorResult result = reportGenerator
                .parse(collection, "Report1")
                .parse(collection2, "Report1")
                .getResult();
        result.writeToFileOutputStream("C:\\Users\\Ihor Patychenko\\Documents\\test.xlsx");
        //result.writeToFileOutputStream("/Users/ihorpatychenko/test.xlsx");
    }

}