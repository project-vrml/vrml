package io.vrml.data.ability;

import io.vavr.Lazy;

import java.util.function.Supplier;

/**
 * Allow the obj to hold the data for processing.
 */
public interface Holdable {

    /**
     * The Holder factory.
     */
    interface HolderFactory {

        /**
         * Supply hold data
         *
         * @param <DTO>      the holder data type
         * @param lazySupply lazy value supplier
         * @return the holder
         */
        static <DTO> Holder<DTO> supplyHoldData(Supplier<DTO> lazySupply) {
            return new Holder<>(lazySupply);
        }

        /**
         * Supply hold data
         *
         * @param <DTO>  the holder data type
         * @param supply value
         * @return the holder
         */
        static <DTO> Holder<DTO> supplyHoldData(DTO supply) {
            return new Holder<>(supply);
        }
    }

    // -- Holder

    /**
     * Hold the data obj
     *
     * @param <DTO> data transfer obj
     */
    final class Holder<DTO> {

        private final boolean init;
        /**
         * Lazy load value supplier
         */
        private final Lazy<DTO> dataObj;

        /**
         * Instantiates a new Holder.
         *
         * @param lazySupply the lazy supply
         */
        public Holder(Supplier<DTO> lazySupply) {
            this.dataObj = Lazy.of(lazySupply);
            this.init = true;
        }

        /**
         * Instantiates a new Holder.
         *
         * @param supply the supply
         */
        public Holder(DTO supply) {
            this.dataObj = Lazy.of(() -> supply);
            this.init = true;
        }

        /**
         * The only way to get data
         *
         * @return the data
         */
        public DTO getData() {
            if (!init) {
                return null;
            }
            return dataObj.get();
        }

        @Override
        public String toString() {
            if (init) {
                if (this.getData() == null) {
                    return "not set value";
                } else {
                    return this.getData().toString();
                }
            } else {
                return "uninitialized";
            }
        }
    }
}
