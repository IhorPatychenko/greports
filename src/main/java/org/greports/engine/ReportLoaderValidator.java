package org.greports.engine;

import org.greports.annotations.CellValidator;
import org.greports.annotations.ColumnValidator;
import org.greports.exceptions.ReportEngineReflectionException;
import org.greports.exceptions.ReportEngineValidationException;
import org.greports.utils.TranslationsParser;
import org.greports.utils.Translator;
import org.greports.validators.AbstractCellValidator;
import org.greports.validators.AbstractColumnValidator;
import org.greports.validators.AbstractValidator;
import org.greports.validators.ValidatorFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ReportLoaderValidator {

    private final Translator translator;

    public ReportLoaderValidator(ReportConfiguration configuration) {
        final Map<String, Object> translations = new TranslationsParser(configuration.getLocale(), configuration.getTranslationsDir(), configuration.getTranslationFileExtension()).getTranslations();
        this.translator = new Translator(translations);
    }

    protected void checkColumnValidations(final List<Object> values, final List<ColumnValidator> columnValidators) {
        for (final ColumnValidator columnValidator : columnValidators) {
            try {
                AbstractValidator validatorInstance = ValidatorFactory.get(columnValidator.validatorClass(), columnValidator.param());
                validateColumn((AbstractColumnValidator) validatorInstance, values, columnValidator.errorMessage());
            } catch (ReflectiveOperationException e) {
                throw new ReportEngineValidationException("Error instantiating a validator @" + columnValidator.validatorClass().getSimpleName(), columnValidator.validatorClass());
            }
        }
    }

    protected void checkCellValidations(final Object value, final List<CellValidator> cellValidators) throws ReportEngineReflectionException {
        for (final CellValidator cellValidator : cellValidators) {
            try {
                AbstractValidator validatorInstance = ValidatorFactory.get(cellValidator.validatorClass(), cellValidator.value());
                validateCell((AbstractCellValidator) validatorInstance, value, cellValidator.errorMessage());
            } catch (ReflectiveOperationException e) {
                throw new ReportEngineReflectionException("Error instantiating a validator @" + cellValidator.validatorClass().getSimpleName(), e, cellValidator.validatorClass());
            }
        }
    }

    private void validateColumn(final AbstractColumnValidator validatorInstance, final List<Object> values, final String errorMessageKey) {
        if (!validatorInstance.isValid(values)) {
            String errorMessage = translator.translate(errorMessageKey, validatorInstance.getValidatorValue());
            final Integer errorRowIndex = validatorInstance.getErrorRowIndex(values);
            throw new ReportEngineValidationException(errorMessage, validatorInstance.getClass(), errorRowIndex, (Serializable) validatorInstance.getErrorValue());
        }
    }

    private void validateCell(final AbstractCellValidator validatorInstance, final Object value, final String errorMessageKey) {
        if (!validatorInstance.isValid(value)) {
            String errorMessage = translator.translate(errorMessageKey, validatorInstance.getValidatorValue());
            throw new ReportEngineValidationException(errorMessage, validatorInstance.getClass());
        }
    }
}
