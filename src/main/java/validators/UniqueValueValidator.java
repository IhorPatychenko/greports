package validators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UniqueValueValidator extends AbstractColumnValidator {

    protected UniqueValueValidator(final String validatorValue) {
        super(validatorValue);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isValid(final Object object) {
        List<Object> list = (List<Object>) object;
        Set<Object> set = new HashSet<>(list);
        return set.size() == list.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public int getErrorRowIndex(final Object object) {
        final List<Object> list = (List<Object>) object;
        final List<Object> setList = new ArrayList<>(new HashSet<>(list));
        for (int i = 0; i < setList.size(); i++) {
            if(!setList.get(i).equals(list.get(i))){
                return i;
            }
        }
        return list.size() - 1;
    }
}
