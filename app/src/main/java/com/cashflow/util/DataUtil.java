package com.cashflow.util;

import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_MONTHLY;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_WEEKLY;
import static com.cashflow.helper.Constants.STATEMENT_VIEW_MODE_YEARLY;

import com.cashflow.helper.Constants;

public class DataUtil {
    public static String getViewModeBadge(int viewMode) {
        switch (viewMode) {
            case STATEMENT_VIEW_MODE_WEEKLY:
                return Constants.STATEMENT_BADGE_WEEKLY;
            case STATEMENT_VIEW_MODE_MONTHLY:
                return Constants.STATEMENT_BADGE_MONTHLY;
            case STATEMENT_VIEW_MODE_YEARLY:
                return Constants.STATEMENT_BADGE_YEARLY;
            default:
                return Constants.STATEMENT_BADGE_INDIVIDUAL;
        }
    }
}
