package com.ayushjainttn.bootcampproject.ecommerce.utils;

public class GlobalExpressions {
    private GlobalExpressions(){
        throw new IllegalStateException("Global Expressions Utility Class");
    }
    public static final String RGX_PASSWORD = "(?=^.{8,15}$)(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&amp;*()_+}{&quot;:;'?/&gt;.&lt;,])(?!.*\\s).*$";
    public static final String RGX_MOBILE_NUMBER = "^[6789]\\d{9}$";
    public static final String RGX_GST_NUMBER = "\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}[A-Z\\d]{1}[Z]{1}[A-Z\\d]{1}";
    public static final String RGX_COMPANY_NAME = "(^[A-Za-z0-9/., -]*$)";
    public static final String RGX_EMAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    public static final String RGX_ALPHABETS = "(?i)^((?!\\d)(?!\\W)(?!null)(?!_).)*$";
    public static final String RGX_ADDRESS_LABEL = "(?i)^(?:company|office|others|shop|work|home)$";
    public static final String RGX_ADDRESS_CITY = "(?i)^((?!\\d)(?!\\W)(?!null)(?!_).|\\s)*$";
    public static final String RGX_ADDRESS_STATE = "(?i)^((?!\\d)(?!\\W)(?!null)(?!_).|\\s)*$";
    public static final String RGX_ADDRESS_COUNTRY = "(?i)^((?!\\d)(?!\\W)(?!null)(?!_).|\\s)*$";
    public static final String RGX_ADDRESS_LINE = "(^[A-Za-z0-9/., -]*$)";
    public static final String RGX_PRODUCT_NAME = "(?i)^((?!\\d)(?!\\W)(?!null)(?!_).|\\s|\\d)*$";
    public static final String RGX_PRODUCT_DESCRIPTION = "(^[A-Za-z0-9/., -&]*$)";
    public static final String RGX_CATEGORY_NAME = "(?i)^((?!\\d)(?!\\W)(?!null)(?!_).|\\s|\\d|&)*$";
    public static final String RGX_ADDRESS_POSTAL_CODE = "(^[0-9]*$)";
    public static final String RGX_CATEGORY_METADATA_FIELD_VALUE = "(^[A-Za-z0-9/., -&]*$)";

    //3 MONTHS CREDENTIALS VALIDITY
    public static final Long SECONDS_CREDENTIALS_EXPIRE = 3*30*24*60*60L;

    //24 HOURS ACCOUNT UNLOCK
    public static final Long SECONDS_ACCOUNT_UNLOCK = 24*60*60L;

    //15 MINS PASSWORD RESET TOKEN VALIDITY
    public static final Long SECONDS_PASSWORD_RESET_TOKEN = 15*60L;

    //3 HOURS ACCOUNT ACTIVATION TOKEN VALIDITY
    public static final Long SECONDS_ACCOUNT_ACTIVATION_TOKEN = 3*60*60L;
}
