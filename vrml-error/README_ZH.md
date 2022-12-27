[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-error

一组错误代码定义规范

## Usage

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-error</artifactId>
  <version>2.0.0</version>
</dependency>
```

### Error Code Design

#### Design goals

* 支持细粒度的错误定位
* 清晰的类别划分
* 不同系统间的共性

#### How to do it?

* 支持细粒度的定位  =>  语义唯一
* 清晰的类别划分     =>  五大类+子分类
* 不同系统间的共性  =>  通用的API支持


    6位码 : [应用名称*3]-[类型码][模块码][子分类码][CODE][CODE][CODE]
    1 : parameter error.
    2 : business error
    3 : repository service error
    4 : dependent service error
    5 : system error

### Error Code Demo

```java
/**
 * Application error info context:
 * [SEC-200000] SUCCESS
 * [SEC-10Xxxx] {@code parameter} error. Define your parameter check exception
 * [SEC-20Xxxx] {@code business} error. Define your business logic exception
 * [SEC-30Xxxx] {@code repository service}. Define repository operation exception
 * [SEC-40Xxxx] {@code dependent service}. Define dependency service exception
 * [SEC-50Xxxx] {@code system} error. Define application system exception
 */
public enum ErrorCodes implements ErrorCodeContext {

    /**
     * The successful error code.
     */
    SUCCESS("200000", "Success"),

    /*-------------------------------------------Parameter error as below---------------------------------------**/
    /**
     * Invalid basic parameter error, the code starts with 0.
     */
    PARAMETER_ERROR(
            GENERATOR.createParameterErrorCode("0000"), "Invalid parameter error!"),

    /*-------------------------------------------Business error as below---------------------------------------**/
    /**
     * Basic business error, the code starts with 0.
     */
    BUSINESS_ERROR(
            GENERATOR.createBusinessProcessErrorCode("0001"), "Business error!"),

    /*-------------------------------------------Repository error as below---------------------------------------**/
    /**
     * Basic repository error, the code starts with 0.
     */
    REPOSITORY_ERROR(
            GENERATOR.createRepositoryErrorCode("0000"), "Repository error!"),

    /*-------------------------------------------Dependent service error as below---------------------------------------**/
    /**
     * Basic dependent service error, the code starts with 0.
     */
    DEPENDENT_SERVICE_ERROR(
            GENERATOR.createDependentServiceErrorCode("0000"), "Failed to call the dependent service!"),

    /*-------------------------------------------System error as below---------------------------------------**/
    /**
     * Basic system error, the code starts with 0.
     */
    SYSTEM_ERROR(
            GENERATOR.createSystemErrorCode("0000"), "System error!"),
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
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-error</artifactId>
  <version>2.0.0</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.