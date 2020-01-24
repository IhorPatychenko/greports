package engine;

import annotations.Configuration;
import exceptions.ReportEngineReflectionException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import positioning.TranslationsParser;
import utils.AnnotationUtils;
import utils.Translator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class ReportLoader2 {

    public enum ReportLoaderErrorTreatment {
        SKIP_ROW_ON_ERROR, SKIP_COLUMN_ON_ERROR, THROW_ERROR
    }

    private String reportName;
    private Workbook currentWorkbook;
    private ReportLoaderResult loaderResult;
    private Translator translator;

    public ReportLoader2(String reportName, String filePath) throws IOException, InvalidFormatException {
        this(reportName, new File(filePath));
    }

    public ReportLoader2(String reportName, File file) throws IOException, InvalidFormatException {
        this(reportName, new FileInputStream(file));
    }

    public ReportLoader2(String reportName, InputStream inputStream) throws IOException, InvalidFormatException {
        this(reportName, WorkbookFactory.create(inputStream));
    }

    private ReportLoader2(String reportName, Workbook workbook) {
        this.reportName = reportName;
        this.currentWorkbook = workbook;
        this.loaderResult = new ReportLoaderResult();
    }

    public <T> ReportLoader2 bindForClass(Class<T> clazz) throws ReportEngineReflectionException, IOException {
        return bindForClass(clazz, ReportLoaderErrorTreatment.THROW_ERROR);
    }

    public <T> ReportLoader2 bindForClass(Class<T> clazz, ReportLoaderErrorTreatment treatment) throws ReportEngineReflectionException, IOException {
        final Configuration configuration = AnnotationUtils.getClassReportConfiguration(clazz, reportName);
        this.translator = new Translator(new TranslationsParser(configuration.translationsDir()).parse(configuration.reportLang()));
        final List<ReportColumn> annotationColumns = AnnotationUtils.loadAnnotations(clazz, reportName, false);
        final List<ReportColumn> unwindedAnnotationColumns = AnnotationUtils.loadAnnotations(clazz, reportName, true);
        final List<ReportBlock> reportBlocks = new ArrayList<>();
        getBlocks(0, reportBlocks, annotationColumns, unwindedAnnotationColumns, false);
        return this;
    }

    private int getBlocks(int index, List<ReportBlock> reportBlocks, List<ReportColumn> columns, List<ReportColumn> unwindedColumns, boolean isRepeatable){
        ReportBlock reportBlock = null;
        for(int i = 0; i < columns.size() && index < unwindedColumns.size(); ){
            if(reportBlock == null) {
                reportBlock = new ReportBlock(index);
                reportBlocks.add(reportBlock);
            }
            if(columns.get(i).getAnnotation().equals(unwindedColumns.get(index).getAnnotation()) &&
                    columns.get(i).getFieldClass().equals(unwindedColumns.get(index).getFieldClass()) &&
                    columns.get(i).getParentClass().equals(unwindedColumns.get(index).getParentClass())) {
                reportBlock.setEndColumn(index)
                        .addReportColumn(columns.get(i))
                        .setRepeatable(isRepeatable);
                index++;
            } else {
                Class<?> clazz;
                if(columns.get(i).getFieldClass().equals(List.class)){
                    final Field field = columns.get(i).getField();
                    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                    clazz = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                } else {
                    clazz = columns.get(i).getFieldClass();
                }
                index = getBlocks(i, reportBlocks,
                        AnnotationUtils.loadAnnotations(clazz, columns.get(i).getReportName(), true),
                        unwindedColumns,
                        columns.get(i).getFieldClass().equals(List.class)
                );
                reportBlock = null;
            }
            i++;
        }
        return index;
    }


    public ReportLoaderResult getLoaderResult() {
        return loaderResult;
    }
}
