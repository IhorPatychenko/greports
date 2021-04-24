package models;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.greports.annotations.Column;
import org.greports.annotations.Configuration;
import org.greports.annotations.Report;
import org.greports.annotations.SpecialRow;
import org.greports.annotations.SpecialRowCell;
import org.greports.engine.ValueType;
import org.greports.interfaces.collectedvalues.CollectedValues;
import styles.DefaultStyles;

import java.formats.Formats;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

@Report(reportConfigurations = {
    @Configuration(reportName = Car.REPORT_NAME, sheetName = "Cars", specialRows = {
        @SpecialRow(rowIndex = Integer.MAX_VALUE, cells = {
            @SpecialRowCell(targetId = "brand", value = "Total"),
            @SpecialRowCell(targetId = "model", valueType = ValueType.COLLECTED_VALUE, value = "model", columnWidth = 4),
            @SpecialRowCell(targetId = "price", valueType = ValueType.COLLECTED_VALUE, value = "price", format = Formats.FLOAT_2_DEC, comment = "translation.price.total")
        })
    })
})
public class Car implements CollectedValues<Object, Object>, DefaultStyles {

    public static final String REPORT_NAME = "Car";

    @Column(reportName = REPORT_NAME, position = 1, title = "Brand", format = Formats.TEXT, autoSizeColumn = true, id = "brand")
    private String brand;
    @Column(reportName = REPORT_NAME, position = 2, title = "Model", format = Formats.TEXT, autoSizeColumn = true, id = "model")
    private String model;
    @Column(reportName = REPORT_NAME, position = 3, title = "Year", format = Formats.INTEGER, autoSizeColumn = true, id = "year")
    private int year;
    @Column(reportName = REPORT_NAME, position = 4, title = "Wheels", format = Formats.INTEGER, autoSizeColumn = true, id = "wheels")
    private short wheels;
    @Column(reportName = REPORT_NAME, position = 5, title = "Sold time", format = Formats.DATE, autoSizeColumn = true, id = "sold_time")
    private Date soldTime;
    @Column(reportName = REPORT_NAME, position = 6, title = "Price", format = Formats.FLOAT_2_DEC, autoSizeColumn = true, id = "price")
    private float price;

    private Car() {}

    public Car(String brand, String model, int year, short wheels, Date soldTime, float price) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.wheels = wheels;
        this.soldTime = soldTime;
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public Car setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public String getModel() {
        return model;
    }

    public Car setModel(String model) {
        this.model = model;
        return this;
    }

    public int getYear() {
        return year;
    }

    public Car setYear(int year) {
        this.year = year;
        return this;
    }

    public short getWheels() {
        return wheels;
    }

    public Car setWheels(short wheels) {
        this.wheels = wheels;
        return this;
    }

    public Date getSoldTime() {
        return soldTime;
    }

    public Car setSoldTime(Date soldTime) {
        this.soldTime = soldTime;
        return this;
    }

    public float getPrice() {
        return price;
    }

    public Car setPrice(float price) {
        this.price = price;
        return this;
    }

    @Override
    public Map<Pair<String, String>, BooleanSupplier> isCollectedValue() {
        Map<Pair<String, String>, BooleanSupplier> map = new HashMap<>();
        map.put(Pair.of(REPORT_NAME, "model"), () -> true);
        map.put(Pair.of(REPORT_NAME, "price"), () -> true);
        return map;
    }

    @Override
    public Map<Pair<String, String>, Object> getCollectedValue() {
        Map<Pair<String, String>, Object> map = new HashMap<>();
        map.put(Pair.of(REPORT_NAME, "model"), this.model);
        map.put(Pair.of(REPORT_NAME, "price"), this.price);
        return map;
    }

    @Override
    public Map<Pair<String, String>, Object> getCollectedValuesResult(List<Object> collectedValues) {
        Map<Pair<String, String>, Object> map = new HashMap<>();
        map.put(Pair.of(REPORT_NAME, "model"), collectedValues.size());
        map.put(Pair.of(REPORT_NAME, "price"), collectedValues.stream().mapToDouble(price -> NumberUtils.toDouble(price.toString())).sum());
        return map;
    }
}
