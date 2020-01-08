package validators;

import exceptions.ReportEngineValidationException;

import java.util.Objects;

import static exceptions.ReportEngineRuntimeExceptionCode.VALIDATION_ERROR;

public class NotNullValidator extends AbstractValidator {

    @Override
    public void validate(Object value) throws ReportEngineValidationException {
        if(Objects.isNull(value)){
            throw new ReportEngineValidationException(getErrorMessage(), VALIDATION_ERROR);
        }
    }

    @Override
    public String getErrorMessage() {
        return "";
    }

    @Override
    public String getErrorKey() {
        return "ReportsEngine.validators.notNull";
    }
}
