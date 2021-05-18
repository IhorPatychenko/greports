package org.greports.engine;

import org.greports.annotations.CellValidator;
import org.greports.annotations.Column;
import org.greports.annotations.ColumnValidator;
import org.greports.annotations.Converter;
import org.greports.annotations.SpecialColumn;
import org.greports.annotations.Subreport;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReportBlock {

    private final Class<?> blockClass;
    private final String reportName;
    private Annotation annotation;
    private final List<ReportBlock> blocks = new ArrayList<>();
    private final List<Object> values = new ArrayList<>();
    private final ReportBlock parentBlock;
    private Method parentMethod;
    private int startColumn;
    private boolean multiple;
    private int repeatCount;

    public ReportBlock(final Class<?> blockClass, String reportName, final ReportBlock parentBlock) {
        this.blockClass = blockClass;
        this.reportName = reportName;
        this.parentBlock = parentBlock;
        this.multiple = false;
        this.repeatCount = 1;
    }

    public ReportBlock(final Class<?> blockClass, String reportName, final ReportBlock parentBlock, final Annotation annotation, final Method parentMethod, boolean multiple) {
        this(blockClass, reportName, parentBlock);
        this.annotation = annotation;
        this.parentMethod = parentMethod;
        this.multiple = multiple;
    }

    public Class<?> getBlockClass() {
        return blockClass;
    }

    public String getReportName() {
        return reportName;
    }

    public Method getParentMethod() {
        return parentMethod;
    }

    public boolean isColumn() {
        return annotation instanceof Column;
    }

    public boolean isSpecialColumn() {
        return annotation instanceof SpecialColumn;
    }

    public boolean isSubreport() {
        return annotation instanceof Subreport;
    }

    public List<ReportBlock> getBlocks() {
        return blocks;
    }

    public List<CellValidator> getCellValidators() {
        return Arrays.asList(getAsColumn().cellValidators());
    }

    public List<ColumnValidator> getColumnValidators() {
        return Arrays.asList(getAsColumn().columnValidators());
    }

    public List<Converter> getSetterConverters() {
        return Collections.singletonList(getAsColumn().setterConverter());
    }

    public List<Converter> getGetterConverters() {
        return Collections.singletonList(getAsColumn().getterConverter());
    }

    public void addValue(Object value) {
        this.values.add(value);
    }

    public List<Object> getValues() {
        return values;
    }

    public ReportBlock getParentBlock() {
        return parentBlock;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(final int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public Float getPosition() {
        if (isColumn()) return getAsColumn().position();
        else if (isSpecialColumn()) return getAsSpecialColumn().position();
        else return getAsSubreport().position();
    }

    public Column getAsColumn() {
        return (Column) annotation;
    }

    public SpecialColumn getAsSpecialColumn() {
        return (SpecialColumn) annotation;
    }

    public Subreport getAsSubreport() {
        return (Subreport) annotation;
    }

    public void addBlock(final ReportBlock block) {
        this.blocks.add(block);
    }

    public ReportBlock orderBlocks() {
        this.blocks.sort(Comparator.comparing(ReportBlock::getPosition));
        return this;
    }

    private int getTotalColumnsCount() {
        int total = 0;
        for (final ReportBlock block : this.blocks) {
            total += block.isSubreport() ? block.getTotalColumnsCount() : 1;
        }
        return total;
    }

    public void setBlockIndexes(int start) {
        doSetBlockIndexes(start);
    }

    private int doSetBlockIndexes(int start) {
        this.startColumn = start;
        int endColumn;
        if (parentBlock == null && isSpecialColumn()) {
            endColumn = start;
        } else if (isSubreport() || parentBlock == null) {
            endColumn = start + getTotalColumnsCount() - 1;
            for (int i = 0, startCount = this.startColumn; i < this.blocks.size(); i++) {
                startCount += this.blocks.get(i).doSetBlockIndexes(startCount);
            }
        } else {
            endColumn = start;
        }
        return endColumn - this.startColumn + 1;
    }
}
