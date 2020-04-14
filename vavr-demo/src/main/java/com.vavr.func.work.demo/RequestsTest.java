package com.vavr.func.work.demo;

/**
 * Requests.
 */
public class RequestsTest {


//    protected IntlFlightListSearchResponseType invokeSoaRequestTest(IntlFlightListSearchRequestType intlFlightListSearchRequestType) throws Exception {
//        // 1. 请求日志
//        log.info("request[{}]", intlFlightListSearchRequestType.toString());
//
//        // 2. 第一次请求，并忽略结果
//        intlFlightListSearchRequestType.setMode(FIRST_REQUEST_MODE);
//        try {
//            ibuFlightServiceOnlineApiClient.intlFlightListSearch(intlFlightListSearchRequestType);
//
//            // 3. 若请求成功，延时1S
//            Thread.sleep(FIRST_CALL_WAIT_TIME);
//        } catch (InterruptedException e) {
//            throw new EventDependentServiceException(ErrorCodes.SYSTEM_PROCESS_ERROR, e);
//        } catch (Exception e) {
//            // ignore
//        }
//
//        // 4. 第二次请求
//        intlFlightListSearchRequestType.setMode(FORMAL_REQUEST_MODE);
//        try {
//            IntlFlightListSearchResponseType responseType = ibuFlightServiceOnlineApiClient.intlFlightListSearch(intlFlightListSearchRequestType);
//
//            // 5. 检查响应体
//            if (responseType == null) {
//                throw new EventDependentServiceException(ErrorCodes.DEPENDENT_FLIGHT_ONLINE_INTL_SERVICE_ERROR);
//            }
//            if (responseType.getResponseStatus() == null || !responseType.getResponseStatus().getAck().equals(AckCodeType.Success)) {
//                throw new EventDependentServiceException(ErrorCodes.DEPENDENT_FLIGHT_ONLINE_INTL_SERVICE_ERROR);
//            }
//            if (responseType.getResponseHead() == null) {
//                throw new EventDependentServiceException(ErrorCodes.DEPENDENT_FLIGHT_ONLINE_INTL_SERVICE_ERROR);
//            }
//
//            // 6. 检查响应码
//            if (!"0".equals(responseType.getResponseHead().getErrorCode())) {
//                throw new EventDependentServiceException(ErrorCodes.DEPENDENT_FLIGHT_ONLINE_INTL_SERVICE_CODE_ERROR);
//            }
//
//            // 7. 响应日志
//            log.info("response[{}]", responseType.toString());
//            return responseType;
//        } catch (Exception e) {
//            throw new EventDependentServiceException(ErrorCodes.DEPENDENT_FLIGHT_ONLINE_INTL_SERVICE_ERROR);
//        }
//    }
}
