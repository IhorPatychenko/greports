package validators;

import org.greports.validators.AbstractCellValidator;

public class NotNullValidator extends AbstractCellValidator {

    protected NotNullValidator(String value) {
        super(value);
    }

    @Override
    public boolean isValid(Object object) {
        return object != null;
    }
}
