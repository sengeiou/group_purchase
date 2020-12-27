/*
 * Copyright Ningbo Qishan Technology Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mds.group.purchase.system;

import org.apache.logging.log4j.core.util.Assert;

import java.util.Date;

/**
 * The type Rest api invoke status.
 *
 * @author pavawi
 */
public class RestApiInvokeStatus {
    private String name;
    private Date startDate;
    private Date latestDate;
    private long times;
    private float averageDuration;
    private int minDuration;
    private int maxDuration;
    private int[] durations;

    /**
     * Instantiates a new Rest api invoke status.
     *
     * @param name the name
     */
    public RestApiInvokeStatus(String name) {
        Assert.isEmpty(name);

        this.name = name;
        this.durations = new int[1000];
        this.minDuration = Integer.MAX_VALUE;
        this.maxDuration = Integer.MIN_VALUE;
        Date now = new Date();
        this.startDate = now;
        this.latestDate = now;

    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Gets latest date.
     *
     * @return the latest date
     */
    public Date getLatestDate() {
        return latestDate;
    }

    /**
     * Gets times.
     *
     * @return the times
     */
    public long getTimes() {
        return times;
    }

    /**
     * Gets min duration.
     *
     * @return the min duration
     */
    public int getMinDuration() {
        return minDuration;
    }

    /**
     * Gets max duration.
     *
     * @return the max duration
     */
    public int getMaxDuration() {
        return maxDuration;
    }

    /**
     * Sets duration.
     *
     * @param duration the duration
     */
    public void setDuration(int duration) {
        this.durations[(int) (this.times % this.durations.length)] = duration;
        this.maxDuration = this.maxDuration > duration ? this.maxDuration : duration;
        this.minDuration = this.minDuration < duration ? this.minDuration : duration;
        this.latestDate = new Date();
        this.times++;
    }

    /**
     * Gets average duration.
     *
     * @return the average duration
     */
    public float getAverageDuration() {
        long length = this.times < this.durations.length ? this.times : this.durations.length;

        int count = 0;
        for (int i = 0; i < length; i++) {
            count += this.durations[i];
        }
        this.averageDuration = (float) count / (float) length;

        return this.averageDuration;
    }
}