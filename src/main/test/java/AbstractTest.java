import models.Car;
import models.Person;
import org.apache.logging.log4j.Level;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.greports.engine.Configurator;
import org.greports.engine.DataReader;
import org.greports.engine.GreportsGenerator;
import org.greports.engine.GreportsLoader;
import org.greports.exceptions.GreportsReflectionException;
import org.greports.exceptions.GreportsRuntimeException;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AbstractTest {

    protected static final String OUTPUT_TEST_DIR_ENV_KEY = "GREPORTS_TEST_DIR";
    protected static final String OUTPUT_TEST_DIR = System.getenv(OUTPUT_TEST_DIR_ENV_KEY);
    protected static final String OUTPUT_FILE_NAME = "Cars.xlsx";
    protected static String FILE_PATH;

    static {

        if(OUTPUT_TEST_DIR == null) {
            throw new GreportsRuntimeException(String.format("You need to define the '%s' environment variable in order to run tests.", OUTPUT_TEST_DIR_ENV_KEY), AbstractTest.class);
        }

        if(String.valueOf(OUTPUT_TEST_DIR.charAt(OUTPUT_TEST_DIR.length() - 1)).equals(File.separator)) {
            FILE_PATH = OUTPUT_TEST_DIR + OUTPUT_FILE_NAME;
        } else {
            FILE_PATH = OUTPUT_TEST_DIR + File.separator + OUTPUT_FILE_NAME;
        }
    }

    protected static final Date currentDate = new Date();

    protected static List<Car> loadedCars;
    protected static GreportsLoader greportsLoader;
    protected static DataReader dataReader;
    protected static GreportsGenerator greportsGenerator;
    protected static Configurator configurator;

    @BeforeAll
    public static void createCars() {

        List<Car> cars = Arrays.asList(
                new Car("Mercedes-Benz", "S600", 2019, (short) 4, currentDate, 79900.0f, new Person("Grace", "MacDonald")),
                new Car("BMW", "320di", 2020, (short) 4, currentDate, 55900.0f, new Person("Caroline", "Clarkson")),
                new Car("Audi", "A1 Sportline", 2020, (short) 4, currentDate, 20560.0f, new Person("Julia", "Poole")),
                new Car("Lamborghini", "Aventador", 2020, (short) 4, currentDate, 315000.0f, new Person("Sonia", "Peake"))
        );

        greportsGenerator = new GreportsGenerator(true, Level.ALL);
        configurator = greportsGenerator.getConfigurator(Car.class, Car.REPORT_NAME);

        try {
            greportsGenerator.parse(cars, Car.REPORT_NAME, Car.class)
                    .getResult()
                    .writeToPath(FILE_PATH);

            loadCars();
        } catch(GreportsReflectionException | IOException e) {
            throw new GreportsRuntimeException("Error creating cars", e, GreportsGeneratorAndLoaderTest.class);
        }
    }

    protected static void loadCars() {
        try {
            greportsLoader = new GreportsLoader(FILE_PATH, Car.REPORT_NAME);
            dataReader = greportsLoader.getReader();
            loadedCars = greportsLoader.bindForClass(Car.class).getLoaderResult().getResult(Car.class);
        } catch(InvalidFormatException | IOException | GreportsReflectionException e) {
            throw new GreportsRuntimeException("Error loading cars", e, GreportsGeneratorAndLoaderTest.class);
        }
    }

}
