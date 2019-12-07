package styles;

import utils.Tuple;

import java.util.*;
import java.util.stream.Collectors;

public class ReportStylesBuilder<T extends ReportStyle> {

    public enum StylePriority {
        PRIORITY1, PRIORITY2, PRIORITY3, PRIORITY4
    }

    private Class<T> forClass;
    private StylePriority priority;
    private Collection<ReportStyleBuilder<T>> styleBuilders = new ArrayList<>();

    public ReportStylesBuilder(Class<T> forClass, StylePriority priority){
        this.forClass = forClass;
        this.priority = priority;
    }

    public ReportStyleBuilder<T> newStyle(Tuple tuple) {
        final ReportStyleBuilder<T> reportStyleBuilder = new ReportStyleBuilder<>(forClass, this, tuple);
        styleBuilders.add(reportStyleBuilder);
        return reportStyleBuilder;
    }

    public Collection<T> build(){
        return styleBuilders.stream()
                .map(ReportStyleBuilder::build)
                .collect(Collectors.toList());
    }

    public StylePriority getPriority(){
        return priority;
    }

}
