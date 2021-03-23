import models.Car;
import org.apache.log4j.Level;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.greports.engine.ReportConfigurator;
import org.greports.engine.ReportDataReader;
import org.greports.engine.ReportGenerator;
import org.greports.engine.ReportLoader;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.exceptions.ReportEngineRuntimeException;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AbstractTest {

    protected static final String FILE_PATH = "C:\\Users\\IhorPatychenko\\Documents\\cars.xlsx";
    protected static final String FILE_PATH2 = "C:\\Users\\IhorPatychenko\\Documents\\cars2.xlsx";

    protected static final Date currentDate = new Date();

    protected static List<Car> loadedCars;
    protected static ReportLoader reportLoader;
    protected static ReportDataReader reportDataReader;
    protected static ReportGenerator reportGenerator;
    protected static ReportConfigurator configurator;

    @BeforeAll
    public static void createCars() {
        List<Car> cars = new ArrayList<>();
        cars.add(new Car("Mercedes-Benz", "S600", 2019, (short) 4, currentDate, 79900.0f));
        cars.add(new Car("BMW", "320di", 2020, (short) 4, currentDate, 55900.0f));
        cars.add(new Car("Audi", "A1 Sportline", 2020, (short) 4, currentDate, 20560.0f));
        cars.add(new Car("Lamborghini", "Aventador", 2020, (short) 4, currentDate, 315000.0f));

        reportGenerator = new ReportGenerator(true, Level.ALL);
        configurator = reportGenerator.getConfigurator(Car.class, Car.REPORT_NAME);

        try {
            reportGenerator.parse(cars, Car.REPORT_NAME, Car.class)
                    .getResult()
                    .writeToPath(FILE_PATH);

            loadCars();
        } catch(ReportEngineReflectionException | IOException e) {
            throw new ReportEngineRuntimeException("Error creating cars", e, ReportGeneratorAndLoaderTest.class);
        }
    }

    protected static void loadCars() {
        try {
            reportLoader = new ReportLoader(FILE_PATH, Car.REPORT_NAME);
            reportDataReader = reportLoader.getReader();
            loadedCars = reportLoader.bindForClass(Car.class).getLoaderResult().getResult(Car.class);
        } catch(InvalidFormatException | IOException | ReportEngineReflectionException e) {
            throw new ReportEngineRuntimeException("Error loading cars", e, ReportGeneratorAndLoaderTest.class);
        }
    }

}
