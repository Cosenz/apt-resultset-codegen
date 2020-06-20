package it.cosenzproject.tools.generator;

import static it.cosenzproject.tools.util.Constants.RESULT_SET_FORMAT;
import static it.cosenzproject.tools.util.Constants.RESULT_SET_STRING_FORMAT;
import static it.cosenzproject.tools.util.Constants.RESULT_SET_CUSTOMER_CONVERTER_FORMAT;
import static it.cosenzproject.tools.util.Constants.STRING;
import static it.cosenzproject.tools.util.Constants.PARAM_NAME;

import java.util.Locale;

/**
 * The purpose of this class is to build the statment to pass to JavaPoet Method
 *
 * @author Andrea Cosentino
 */
public class StatementBuilder {

    private String propertyName;
    private String resultSetType;
    private String converter;

    public StatementBuilder setPropertyName(String propertyName) {
        if(propertyName == null)
            this.propertyName ="";
        else
            this.propertyName = propertyName.substring(0, 1)
                                    .toUpperCase(Locale.ITALY)
                                   .concat(propertyName.substring(1));
        return this;
    }

    public StatementBuilder setResultSetType(String resultSetType) {
        this.resultSetType = resultSetType;
        return this;
    }

    public StatementBuilder setConverter(String converter) {
        this.converter = converter;
        return this;
    }

    /**
     * Build the correct statement
     *
     * @return String
     */
    public String build() {
        String statement = String.format(RESULT_SET_FORMAT, propertyName, resultSetType);
        if(converter != null && !converter.isEmpty()) {
            statement = String.format(RESULT_SET_CUSTOMER_CONVERTER_FORMAT,
                    propertyName,
                    converter, PARAM_NAME);
        }
        else if(STRING.equals(resultSetType)) {
            statement = String.format(RESULT_SET_STRING_FORMAT, propertyName, resultSetType, resultSetType);
        }
        return statement;
    }
}
