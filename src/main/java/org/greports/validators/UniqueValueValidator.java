package org.greports.validators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UniqueValueValidator extends AbstractColumnValidator {

    public UniqueValueValidator(final String params) {
        super(params);
    }

    @Override
    public boolean isValid(final List<Object> list) {
        Set<Object> set = new HashSet<>(list);
        return set.size() == list.size();
    }

    @Override
    public int getErrorRowIndex(final List<Object> list) {
        final List<Object> setList = new ArrayList<>(new HashSet<>(list));
        for (int i = 0; i < setList.size(); i++) {
            if(!setList.get(i).equals(list.get(i))){
                return i;
            }
        }
        return list.size() - 1;
    }

    @Override
    public Object getErrorValue() {
        return null;
    }
}
