package nbcc.assignment2.services;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public class RequestAttributeService {

    private final HttpServletRequest request;

    public RequestAttributeService(HttpServletRequest request) {
        this.request = request;
    }

    protected String getString(String paramName) {
        return request.getParameter(paramName);
    }

    protected boolean getBoolean(String paramName) {
        return getBoolean(paramName, false);
    }

    protected Boolean getBoolean(String paramName, Boolean defaultValue) {

        var param = request.getParameter(paramName);

        if (param != null && !param.isBlank()) {
            try {
                return Boolean.parseBoolean(param);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    protected int getInteger(String paramName) {
        return getInteger(paramName, 0);
    }

    protected Integer getInteger(String paramName, Integer defaultValue) {

        var param = request.getParameter(paramName);

        if (param != null && !param.isBlank()) {
            try {
                return Integer.parseInt(param);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    protected long getLong(String paramName) {
        return getLong(paramName, 0L);
    }

    protected Long getLong(String paramName, Long defaultValue) {

        var param = request.getParameter(paramName);

        if (param != null && !param.isBlank()) {
            try {
                return Long.parseLong(param);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    protected double getDouble(String paramName) {
        return getDouble(paramName, 0.0);
    }

    protected Double getDouble(String paramName, Double defaultValue) {

        var param = request.getParameter(paramName);

        if (param != null && !param.isBlank()) {
            try {
                return Double.parseDouble(param);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    protected BigDecimal getBigDecimal(String paramName) {

        return getBigDecimal(paramName, BigDecimal.ZERO);
    }

    protected BigDecimal getBigDecimal(String paramName, BigDecimal defaultValue) {

        var param = request.getParameter(paramName);

        if (param != null && !param.isBlank()) {
            try {
                return new BigDecimal(param);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }
}
