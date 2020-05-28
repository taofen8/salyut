/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * Flink Tuple2
 */
package com.trico.salyut.utils;

public class Tuple2<T0, T1>  {

    /** Field 0 of the tuple. */
    public T0 f0;
    /** Field 1 of the tuple. */
    public T1 f1;

    /**
     * Creates a new tuple where all fields are null.
     */
    public Tuple2() {}

    /**
     * Creates a new tuple and assigns the given values to the tuple's fields.
     *
     * @param value0 The value for field 0
     * @param value1 The value for field 1
     */
    public Tuple2(T0 value0, T1 value1) {
        this.f0 = value0;
        this.f1 = value1;
    }


    @SuppressWarnings("unchecked")
    public <T> T getField(int pos) {
        switch(pos) {
            case 0: return (T) this.f0;
            case 1: return (T) this.f1;
            default: throw new IndexOutOfBoundsException(String.valueOf(pos));
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void setField(T value, int pos) {
        switch(pos) {
            case 0:
                this.f0 = (T0) value;
                break;
            case 1:
                this.f1 = (T1) value;
                break;
            default: throw new IndexOutOfBoundsException(String.valueOf(pos));
        }
    }

    /**
     * Sets new values to all fields of the tuple.
     *
     * @param value0 The value for field 0
     * @param value1 The value for field 1
     */
    public void setFields(T0 value0, T1 value1) {
        this.f0 = value0;
        this.f1 = value1;
    }

    /**
     * Returns a shallow copy of the tuple with swapped values.
     *
     * @return shallow copy of the tuple with swapped values
     */
    public Tuple2<T1, T0> swap() {
        return new Tuple2<T1, T0>(f1, f0);
    }


    /**
     * Modified
     **/
    @Override
    public String toString() {
        return "(" + this.f0
                + "," + this.f1
                + ")";
    }

    /**
     * Deep equality for tuples by calling equals() on the tuple members.
     * @param o the object checked for equality
     * @return true if this is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tuple2)) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        Tuple2 tuple = (Tuple2) o;
        if (f0 != null ? !f0.equals(tuple.f0) : tuple.f0 != null) {
            return false;
        }
        if (f1 != null ? !f1.equals(tuple.f1) : tuple.f1 != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = f0 != null ? f0.hashCode() : 0;
        result = 31 * result + (f1 != null ? f1.hashCode() : 0);
        return result;
    }

    /**
     * Shallow tuple copy.
     * @return A new Tuple with the same fields as this.
     */
    @SuppressWarnings("unchecked")
    public Tuple2<T0, T1> copy() {
        return new Tuple2<>(this.f0,
                this.f1);
    }

    /**
     * Creates a new tuple and assigns the given values to the tuple's fields.
     * This is more convenient than using the constructor, because the compiler can
     * infer the generic type arguments implicitly. For example:
     * {@code Tuple3.of(n, x, s)}
     * instead of
     * {@code new Tuple3<Integer, Double, String>(n, x, s)}
     * @param value0 The value for field 0
     * @param value1 The value for field 1
     * @return A new Tuple
     */
    public static <T0, T1> Tuple2<T0, T1> of(T0 value0, T1 value1) {
        return new Tuple2<>(value0,
                value1);
    }
}
