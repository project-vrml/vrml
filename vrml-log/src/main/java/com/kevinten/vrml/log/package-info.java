/**
 * This package supply the API to log by the key.
 *
 * <pre>
 * LOG规范 : ①Tags ②基础信息 ③具体日志内容
 *
 * [[ <-- tag --> ]][ <- basic info -> ] [ content ]
 * [[messageId,key]][ClassName.FuncName] log content.
 * </pre>
 * -------------------------------------------------
 * <pre>
 * LOG约定 :
 *
 * 1. 动态参数小于3，并列一行
 * 2. 动态参数小于3，但表达式较长，参数作为单独一行
 * 3. 动态参数大于等于3，参数作为单独一行
 * </pre>
 */
package com.kevinten.vrml.log;