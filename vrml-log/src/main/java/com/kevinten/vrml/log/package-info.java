/**
 * This package supply the API to log by the key.
 *
 * <pre>
 * LOG : 1.Tags 2.basic info 3.content
 *
 * [[ <-- tag --> ]][ <- basic info -> ] [ content ]
 * [[messageId,key]][ClassName.FuncName] log content.
 * </pre>
 * <pre>
 * LOG contract :
 *
 * 1. dynamic params < 3, in 1 line.
 * 2. dynamic params < 3, but expression is too lang, use another line.
 * 3. dynamic params >=3, use another line.
 * </pre>
 */
package com.kevinten.vrml.log;