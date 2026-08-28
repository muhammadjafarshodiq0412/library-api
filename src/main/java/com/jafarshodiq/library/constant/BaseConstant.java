package com.jafarshodiq.library.constant;


public final class BaseConstant {

    private BaseConstant() {}

    public static final class HeaderParameter {

        private HeaderParameter() {}

        public static final String X_TRACKING_REF = "X-Tracking-Ref";
        public static final String MDC_KEY = "tracking-ref";
    }

    public static final class DateTimeParameter {

        private DateTimeParameter() {}

        public static final String RESPONSE_DATE_TIME_FORMAT = "dd MMM yyyy, HH:mm:ss";
    }
}