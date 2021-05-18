import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ReportGeneratorAndLoaderTest extends AbstractTest {

    @Test
    void brandTest() {
        assertEquals("Mercedes-Benz", loadedCars.get(0).getBrand());
        assertEquals("BMW", loadedCars.get(1).getBrand());
        assertEquals("Audi", loadedCars.get(2).getBrand());
        assertEquals("Lamborghini", loadedCars.get(3).getBrand());
    }

    @Test
    void modelTest() {
        assertEquals("S600", loadedCars.get(0).getModel());
        assertEquals("320di", loadedCars.get(1).getModel());
        assertEquals("A1 Sportline", loadedCars.get(2).getModel());
        assertEquals("Aventador", loadedCars.get(3).getModel());
    }

    @Test
    void yearTest() {
        assertEquals(2019, loadedCars.get(0).getYear());
        assertEquals(2020, loadedCars.get(1).getYear());
        assertEquals(2020, loadedCars.get(2).getYear());
        assertEquals(2020, loadedCars.get(3).getYear());
    }

    @Test
    void wheelsTest() {
        assertEquals(4, loadedCars.get(0).getWheels());
        assertEquals(4, loadedCars.get(1).getWheels());
        assertEquals(4, loadedCars.get(2).getWheels());
        assertEquals(4, loadedCars.get(3).getWheels());
    }

    @Test
    void priceTest() {
        assertEquals(79900.0f, loadedCars.get(0).getPrice(), 0);
        assertEquals(55900.0f, loadedCars.get(1).getPrice(), 0);
        assertEquals(20560.0f, loadedCars.get(2).getPrice(), 0);
        assertEquals(315000.0f, loadedCars.get(3).getPrice(), 0);
    }
}
