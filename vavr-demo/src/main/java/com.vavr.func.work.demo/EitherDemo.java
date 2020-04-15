package com.vavr.func.work.demo;

import com.vavr.func.work.error.code.ErrorCodes;
import io.vavr.control.Either;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Either demo.
 */
public class EitherDemo {

    /**
     * CASE: 从Request中生成Entity
     * 1. id :将String转为int
     * 2. sum:将x,y相加
     */
    interface Case1 {

        @Data
        class Entity {
            private int id;
            private int sum;
        }

        @Data
        class Request {
            private String id;
            private Integer x;
            private Integer y;
        }

        /**
         * null!
         * 1. 外部需要防御式代码
         * 2. 可能造成NPE
         * 3. 语义不明确
         * 4. 丢失了错误信息
         */
        private Entity getEntityNull(Request request) {
            Entity entity = new Entity();
            {
                String id = request.getId();
                if (!StringUtils.isNumeric(id)) {
                    return null;
                }
                entity.setId(Integer.parseInt(id));
            }
            {
                Integer x = request.getX();
                Integer y = request.getY();
                if (x == null || y == null) {
                    return null;
                }
                entity.setSum(x + y);
            }
            return entity;
        }

        /**
         * Maybe
         * 1. 丢失了错误信息
         */
        private Optional<Entity> getEntityOptional(Request request) {
            Entity entity = new Entity();
            {
                String id = request.getId();
                if (!StringUtils.isNumeric(id)) {
                    return Optional.empty();
                }
                entity.setId(Integer.parseInt(id));
            }
            {
                Integer x = request.getX();
                Integer y = request.getY();
                if (x == null || y == null) {
                    return Optional.empty();
                }
                entity.setSum(x + y);
            }
            return Optional.of(entity);
        }

        /**
         * Exception
         * 1. 数百时钟开销
         * 2. 异常的滥用
         */
        private Entity getEntityException(Request request) {
            Entity entity = new Entity();
            {
                String id = request.getId();
                if (!StringUtils.isNumeric(id)) {
                    throw new RuntimeException(ErrorCodes.PARAMETER_ERROR.getMessage());
                }
                entity.setId(Integer.parseInt(id));
            }
            {
                Integer x = request.getX();
                Integer y = request.getY();
                if (x == null || y == null) {
                    throw new RuntimeException(ErrorCodes.BUSINESS_ERROR.getMessage());
                }
                entity.setSum(x + y);
            }
            return entity;
        }

        /**
         * Either
         * 1. 如果调用链路过深，需要返回的层级会过深
         */
        private Either<ErrorCodes, Entity> getEntityEither(Request request) {
            Entity entity = new Entity();
            {
                String id = request.getId();
                if (!StringUtils.isNumeric(id)) {
                    return Either.left(ErrorCodes.PARAMETER_ERROR);
                }
                entity.setId(Integer.parseInt(id));
            }
            {
                Integer x = request.getX();
                Integer y = request.getY();
                if (x == null || y == null) {
                    return Either.left(ErrorCodes.BUSINESS_ERROR);
                }
                entity.setSum(x + y);
            }
            return Either.right(entity);
        }
    }
}
