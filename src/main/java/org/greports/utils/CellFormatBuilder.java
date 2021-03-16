package org.greports.utils;

public class CellFormatBuilder {

    public enum Unit {
        /**
         * Represents a full year number. Example: 1992, 2011, etc
         */
        FULL_YEAR("yyyy"),
        /**
         * Represents a full numerical month number: Example: 01, 08, 12
         */
        FULL_MONTH("MM"),
        /**
         * Represents a full numerical day number: Example: 03, 15, 27
         */
        FULL_DAY("dd");

        private final String value;

        Unit(String value) {
            this.value = value;
        }
    }

    public enum Delimiter {
        SLASH("/"), DOT("."), DASH("-");

        private final String value;

        Delimiter(String value) {
            this.value = value;
        }
    }

    private StringBuilder sb;

    public CellFormatBuilder append(String value) {
        sb.append(value);
        return this;
    }

    public CellFormatBuilder append(Unit unit) {
        sb.append(unit.value);
        return this;
    }

    public CellFormatBuilder append(Delimiter delimiter) {
        sb.append(delimiter.value);
        return this;
    }

    public String build() {
        final String value = sb.toString();
        sb = new StringBuilder();
        return value;
    }

}
