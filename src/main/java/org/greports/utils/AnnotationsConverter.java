package org.greports.utils;

import org.greports.annotations.Cell;
import org.greports.annotations.CellGetter;
import org.greports.annotations.CellValidator;
import org.greports.annotations.Column;
import org.greports.annotations.ColumnGetter;
import org.greports.annotations.ColumnSetter;
import org.greports.annotations.ColumnValidator;
import org.greports.annotations.Converter;
import org.greports.annotations.Subreport;
import org.greports.annotations.SubreportGetter;
import org.greports.annotations.SubreportSetter;
import org.greports.converters.AbstractValueConverter;
import org.greports.converters.NotImplementedConverter;
import org.greports.engine.ValueType;

import java.lang.annotation.Annotation;

/**
 * Annotation converter class.
 * This one is for internal use of greports engine.
 */
public class AnnotationsConverter {

    private AnnotationsConverter(){}

    private static final Converter notImplementedConveter =  new Converter() {

        @Override
        public Class<? extends Annotation> annotationType() {
            return Converter.class;
        }

        @Override
        public Class<? extends AbstractValueConverter> converterClass() {
            return NotImplementedConverter.class;
        }

        @Override
        public String[] params() {
            return new String[0];
        }
    };

    public static Subreport convert(final SubreportGetter subreportGetter) {
        return new Subreport() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Subreport.class;
            }

            @Override
            public String[] reportName() {
                return subreportGetter.reportName();
            }

            @Override
            public float position() {
                return subreportGetter.position();
            }

            @Override
            public String id() {
                return subreportGetter.id();
            }
        };
    }

    public static Subreport convert(final SubreportSetter subreportGetter) {
        return new Subreport() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Subreport.class;
            }

            @Override
            public String[] reportName() {
                return subreportGetter.reportName();
            }

            @Override
            public float position() {
                return subreportGetter.position();
            }

            @Override
            public String id() {
                return subreportGetter.id();
            }
        };
    }

    public static Column convert(final ColumnGetter columnGetter) {
        return new Column() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Column.class;
            }

            @Override
            public String[] reportName() {
                return columnGetter.reportName();
            }

            @Override
            public float position() {
                return columnGetter.position();
            }

            @Override
            public CellValidator[] cellValidators() {
                return new CellValidator[0];
            }

            @Override
            public ColumnValidator[] columnValidators() {
                return new ColumnValidator[0];
            }

            @Override
            public Converter getterConverter() {
                return notImplementedConveter;
            }

            @Override
            public Converter setterConverters() {
                return columnGetter.typeConverter();
            }

            @Override
            public String title() {
                return columnGetter.title();
            }

            @Override
            public String format() {
                return columnGetter.format();
            }

            @Override
            public ValueType valueType() {
                return columnGetter.valueType();
            }

            @Override
            public String id() {
                return columnGetter.id();
            }

            @Override
            public boolean autoSizeColumn() {
                return columnGetter.autoSizeColumn();
            }

            @Override
            public int columnWidth() {
                return columnGetter.columnWidth();
            }
        };
    }


    public static Cell convert(final CellGetter cellGetter) {
        return new Cell() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Cell.class;
            }

            @Override
            public String[] reportName() {
                return cellGetter.reportName();
            }

            @Override
            public int row() {
                return cellGetter.row();
            }

            @Override
            public int column() {
                return cellGetter.column();
            }

            @Override
            public CellValidator[] cellValidators() {
                return cellGetter.cellValidators();
            }

            @Override
            public Converter getterConverter() {
                return cellGetter.getterConverter();
            }

            @Override
            public Converter setterConverter() {
                return notImplementedConveter;
            }

            @Override
            public String format() {
                return cellGetter.format();
            }

            @Override
            public ValueType valueType() {
                return cellGetter.valueType();
            }

            @Override
            public String id() {
                return cellGetter.id();
            }

            @Override
            public boolean autoSizeColumn() {
                return cellGetter.autoSizeColumn();
            }

            @Override
            public int columnWidth() {
                return cellGetter.columnWidth();
            }
        };
    }

    public static Column convert(final ColumnSetter columnSetter) {
        return new Column() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Column.class;
            }

            @Override
            public String[] reportName() {
                return columnSetter.reportName();
            }

            @Override
            public float position() {
                return columnSetter.position();
            }

            @Override
            public CellValidator[] cellValidators() {
                return columnSetter.cellValidators();
            }

            @Override
            public ColumnValidator[] columnValidators() {
                return columnSetter.columnValidators();
            }

            @Override
            public Converter getterConverter() {
                return notImplementedConveter;
            }

            @Override
            public Converter setterConverters() {
                return columnSetter.typeConverters();
            }

            @Override
            public String title() {
                return columnSetter.title();
            }

            @Override
            public String format() {
                return columnSetter.format();
            }

            @Override
            public ValueType valueType() {
                return columnSetter.valueType();
            }

            @Override
            public String id() {
                return columnSetter.id();
            }

            @Override
            public boolean autoSizeColumn() {
                return columnSetter.autoSizeColumn();
            }

            @Override
            public int columnWidth() {
                return columnSetter.columnWidth();
            }
        };
    }
}
