package group.rxcloud.vrml.ratecontrol.sphu.config;

/**
 * 预热模式及配置
 */
public class FlowControlMode {

    /**
     * 自旋等待策略
     */
    public static class SpinWaitConfig {

        /**
         * 线程自旋sleep时间
         */
        private int sleepMills = 1000;

        /**
         * 最大自旋次数，超过后将直接获取令牌
         */
        private int maxSpinCount = 100;

        public int getSleepMills() {
            return sleepMills;
        }

        public void setSleepMills(int sleepMills) {
            this.sleepMills = sleepMills;
        }

        public int getMaxSpinCount() {
            return maxSpinCount;
        }

        public void setMaxSpinCount(int maxSpinCount) {
            this.maxSpinCount = maxSpinCount;
        }
    }

    /**
     * MQ延迟重试策略
     */
    public static class MqRetryConfig {

        /**
         * 重试时间，注意MQ最大重试32次
         */
        private int delayMills;

        /**
         * 最大MQ重试次数，超过后将直接获取令牌
         */
        private int maxMqRetryCount = 30;

        public int getDelayMills() {
            return delayMills;
        }

        public void setDelayMills(int delayMills) {
            this.delayMills = delayMills;
        }

        public int getMaxMqRetryCount() {
            return maxMqRetryCount;
        }

        public void setMaxMqRetryCount(int maxMqRetryCount) {
            this.maxMqRetryCount = maxMqRetryCount;
        }
    }
}
