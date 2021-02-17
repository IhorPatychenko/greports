import org.junit.Assert;
import org.junit.Test;


public class ReportGeneratorAndLoaderTest extends AbstractTest {

    @Test
    public void brandTest() {
        Assert.assertEquals("Mercedes-Benz", loadedCars.get(0).getBrand());
        Assert.assertEquals("BMW", loadedCars.get(1).getBrand());
        Assert.assertEquals("Audi", loadedCars.get(2).getBrand());
        Assert.assertEquals("Lamborghini", loadedCars.get(3).getBrand());
    }

    @Test
    public void modelTest() {
        Assert.assertEquals("S600", loadedCars.get(0).getModel());
        Assert.assertEquals("320di", loadedCars.get(1).getModel());
        Assert.assertEquals("A1 Sportline", loadedCars.get(2).getModel());
        Assert.assertEquals("Aventador", loadedCars.get(3).getModel());
    }

    @Test
    public void yearTest() {
        Assert.assertEquals(2019, loadedCars.get(0).getYear());
        Assert.assertEquals(2020, loadedCars.get(1).getYear());
        Assert.assertEquals(2020, loadedCars.get(2).getYear());
        Assert.assertEquals(2020, loadedCars.get(3).getYear());
    }

    @Test
    public void wheelsTest() {
        Assert.assertEquals(4, loadedCars.get(0).getWheels());
        Assert.assertEquals(4, loadedCars.get(1).getWheels());
        Assert.assertEquals(4, loadedCars.get(2).getWheels());
        Assert.assertEquals(4, loadedCars.get(3).getWheels());
    }

    @Test
    public void priceTest() {
        Assert.assertEquals(79900.0f, loadedCars.get(0).getPrice(), 0);
        Assert.assertEquals(55900.0f, loadedCars.get(1).getPrice(), 0);
        Assert.assertEquals(20560.0f, loadedCars.get(2).getPrice(), 0);
        Assert.assertEquals(315000.0f, loadedCars.get(3).getPrice(), 0);
    }
}
