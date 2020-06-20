package it.cosenzproject.tools.util;

public class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String STRING = "String";
    public static final String PARAM_NAME = "resultSet";

    public static final String CONVERT_SUFFIX = "RSConverter";
    public static final String RESULT_SET_FORMAT = "result.set%s(resultSet.get%s(\"$L\"))";
    public static final String RESULT_SET_STRING_FORMAT = "result.set%s(resultSet.get%s(\"$L\") != null ? resultSet.get%s(\"$L\").trim() : null)";
    public static final String RESULT_SET_CUSTOMER_CONVERTER_FORMAT = "result.set%s(%s(%s))";
}
