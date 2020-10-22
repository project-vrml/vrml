[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-error

A set of error code definition specifications for [vrml](https://github.com/kevinten10/vrml) library

## Usage

### Maven

```xml
<dependency>
  <groupId>com.kevinten</groupId>
  <artifactId>vrml-error</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Error Code Design

#### Design goals

* Support fine-grained error location
* Clear classification of errors
* Compatible with commonalities between different systems

#### How to do it?

* Support fine-grained error location =>  Semantically unique
* Clear classification of errors =>  Five categories + sub-categories
* Compatible with commonalities between different systems =>  General API support 

5 digit code : [Type code][System code][Subcategory code][CODE][CODE]
1 : parameter error.
2 : business error
3 : repository service error
4 : dependent service error
5 : system error

### Error Code Demo

```java
/**
 * Application error info context:
 * [20000] SUCCESS
 * [1Dxxx] {@code parameter} error. Define your parameter check exception
 * [2Dxxx] {@code business} error. Define your business logic exception
 * [3Dxxx] {@code repository service}. Define repository operation exception
 * [4Dxxx] {@code dependent service}. Define dependency service exception
 * [5Dxxx] {@code system} error. Define application system exception
 */
public enum ErrorCodes implements ErrorCodeContext {

    /**
     * The successful error code.
     */
    SUCCESS("20000", "Success"),

    /*-------------------------------------------Parameter error as below---------------------------------------**/
    /**
     * Invalid basic parameter error, the code starts with 0.
     */
    PARAMETER_ERROR(
            GENERATOR.createParameterErrorCode("000"), "Invalid parameter error!"),

    /*-------------------------------------------Business error as below---------------------------------------**/
    /**
     * Basic business error, the code starts with 0.
     */
    BUSINESS_ERROR(
            GENERATOR.createBusinessProcessErrorCode("001"), "Business error!"),

    /*-------------------------------------------Repository error as below---------------------------------------**/
    /**
     * Basic repository error, the code starts with 0.
     */
    REPOSITORY_ERROR(
            GENERATOR.createRepositoryErrorCode("000"), "Repository error!"),

    /*-------------------------------------------Dependent service error as below---------------------------------------**/
    /**
     * Basic dependent service error, the code starts with 0.
     */
    DEPENDENT_SERVICE_ERROR(
            GENERATOR.createDependentServiceErrorCode("000"), "Failed to call the dependent service!"),

    /*-------------------------------------------System error as below---------------------------------------**/
    /**
     * Basic system error, the code starts with 0.
     */
    SYSTEM_ERROR(
            GENERATOR.createSystemErrorCode("000"), "System error!"),
    ;

    // -- Encapsulation

    private final String code;
    private final String message;

    ErrorCodes(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    static {
        // remove duplicate codes
        ErrorCodeManager.assertErrorCodesNoDuplicate(ErrorCodes.values());
    }

    /**
     * Show error codes.
     */
    public static void showErrorCodes() {
        MANAGER.showErrorCodes(ErrorCodes.values());
    }
}
```

## Become a Developer

Developer repository can be found [here](https://github.com/kevinten10/vrml/tree/develop/vrml-request).

### Maven

```xml
<dependency>
  <groupId>com.kevinten</groupId>
  <artifactId>vrml-error</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.