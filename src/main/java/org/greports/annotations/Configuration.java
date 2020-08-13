package org.greports.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Configuration {

    /**
     * Defines a name of report which will be used by the org.greports.engine
     * to obtain this configuration.
     *
     * @return {@link String}
     */
    String[] reportName();

    /**
     * Relative route to directory containing translations.
     * This directory needs to be placed in resource folder of the project.
     * Every translations file need to have the follow name: messages.[reportLang].yml
     * Valid examples: i18n/messages.en.yml
     *                 i18n/messages.es.yml
     *                 i18n/messages.de.yml

     * Invalid example: i18n/messages.en.yaml
     *                  i18n/messages.es.yaml
     *                  i18n/messages.de.yaml
     *                  i18n/translations.yml
     *
     * @return {@link String}
     */
    String translationsDir() default "i18n/";

    /**
     * Locale string representation.
     *
     * @return {@link String}
     */
    String locale() default "en_US";

    /**
     * Template path to be used to generate the report.
     * This template path needs to point the template inside the resource folder of the project.
     *
     * @return {@link String}
     */
    String templatePath() default "";

    /**
     * Sheet's name. If value is empty, the org.greports.engine will generate the sheet's name automatically.
     *
     * @return {@link String}
     */
    String sheetName() default "";

    /**
     * The value indicates if the header needs to be created.
     *
     * @return {@link boolean}
     */
    boolean createHeader() default true;

    /**
     * The value indicates if the header needs to be sortable.
     *
     * @return {@link boolean}
     */
    boolean sortableHeader() default false;

    /**
     * Header's row index.
     *
     * @return {@link short}
     */
    short headerRowIndex() default 0;

    /**
     * Indicates on which row the data will start.
     * 0-based.
     *
     * @return short
     */
    short dataStartRowIndex() default 1;

    /**
     * Indicates on which column the data will start.
     * 0-based.
     * @return short
     */
    short dataStartColumnIndex() default 0;

    /**
     * Indicates if the configuration to be parsed need to be injected
     * into existing sheet using {@link org.greports.engine.TemplateDataInjector}
     *
     * @return boolean
     */
    boolean templatedInject() default false;

    /**
     * An array of {@link SpecialRow}
     *
     * @return an array of {@link SpecialRow}
     */
    SpecialRow[] specialRows() default {};

    /**
     * An array of {@link SpecialColumn}
     *
     * @return an array of {@link SpecialColumn}
     */
    SpecialColumn[] specialColumns() default {};
}
