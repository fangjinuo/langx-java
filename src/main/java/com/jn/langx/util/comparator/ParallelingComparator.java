package com.jn.langx.util.comparator;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings({"unchecked"})
public class ParallelingComparator implements Comparator {
    private final List<Comparator> delegates = new ArrayList<Comparator>();

    @Override
    public int compare(Object o1, Object o2) {
        Preconditions.checkTrue(isValid());
        int leftMoveUnit = 32 / delegates.size();
        int deltaMax = new Double(Math.pow(2, leftMoveUnit + 1)).intValue() - 1;
        int result = 0;

        Boolean isNegative = null;
        for (int i = 0; i < delegates.size(); i++) {
            Comparator comparator = delegates.get(i);
            int delta = comparator.compare(o1, o2);
            if (isNegative == null) {
                if (delta != 0) {
                    isNegative = delta < 0;
                }
            }
            int leftMove = (delegates.size() - 1 - i) * leftMoveUnit;

            if (i > 0 && isNegative != null && ((delta > 0 && isNegative) || (delta < 0 && !isNegative))) {
                result = result + ((deltaMax - 1 - (Math.abs(delta) % deltaMax)) << leftMove);
            } else {
                result = result + ((Math.abs(delta) % deltaMax) << leftMove);
            }
        }
        if (isNegative != null && isNegative) {
            return 0 - result;
        }
        return result;
    }

    public void addComparator(Comparator comparator) {
        Preconditions.checkNotNull(comparator);
        delegates.add(comparator);
    }

    public boolean isValid(){
        return Emptys.isNotEmpty(delegates);
    }
}