package com.niutucode.practice;

/***
 * 雪花算法
 *
 * @author niutucode
 */
public class Snowflake {
    /**
     * 开始时间戳
     */
    private static final long START_TIMESTAMP = 1736820033851L;
    /**
     * 机器位数
     */
    private static final long MACHINE_BIT = 10L;
    /**
     * 序列号位数
     */
    private static final long SEQUENCE_BIT = 12L;
    /**
     * 机器最大值 1023
     */
    private static final long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    /**
     * 序列号最大值 4095
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
    /**
     * 机器标识向左移动的位数
     */
    private static final long MACHINE_LEFT = SEQUENCE_BIT;
    /**
     * 时间戳向左移动的位数
     */
    private static final long TIMESTAMP_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    /**
     * 机器ID
     */
    private long machineId;
    /**
     * 序列号
     */
    private long sequence = -1L;
    /**
     * 上一次时间戳
     */
    private long lastTimeStamp = 0L;

    /**
     * 构造器
     *
     * @param machineId 机器ID
     */
    public Snowflake(long machineId) {
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("机器ID不能大于" + MAX_MACHINE_NUM + "或者小于0");
        }
        this.machineId = machineId;
    }

    /**
     * 产生下一个时间戳
     *
     * @param lastTimeStamp 上一次生成的时间戳
     * @return 下一个时间戳
     */
    private long nextTimestamp(long lastTimeStamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimeStamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * 获取分布式ID
     *
     * @return 分布式ID
     */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimeStamp) {
            throw new RuntimeException("时钟回拨异常");
        }
        if (timestamp == lastTimeStamp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数已经达到最大
            if (sequence == 0) {
                timestamp = nextTimestamp(lastTimeStamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimeStamp = timestamp;
        return (timestamp - START_TIMESTAMP) << TIMESTAMP_LEFT | machineId << MACHINE_LEFT | sequence;
    }
}
