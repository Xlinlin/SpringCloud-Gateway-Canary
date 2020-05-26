package com.xiao.springcloud.loadbalancer.eureka.chooser;

import com.xiao.springcloud.loadbalancer.eureka.util.BalancerThreadLocalRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link com.alibaba.nacos.client.naming.utils.Chooser}
 *
 * @ClassName: BalancerChooser
 * @Description: 路由选择器
 * @author: xiaolinlin
 * @date: 2020/5/26 21:31
 **/
public class BalancerChooser<K, T> {

    private K uniqueKey;

    private volatile BalancerRef<T> ref;

    public T random() {
        List<T> items = ref.items;
        if (items.size() == 0) {
            return null;
        }
        if (items.size() == 1) {
            return items.get(0);
        }
        return items.get(BalancerThreadLocalRandom.current().nextInt(items.size()));
    }

    public T randomWithWeight() {
        BalancerRef<T> ref = this.ref;
        double random = BalancerThreadLocalRandom.current().nextDouble(0, 1);
        int index = Arrays.binarySearch(ref.weights, random);
        if (index < 0) {
            index = -index - 1;
        } else {
            return ref.items.get(index);
        }

        if (index >= 0 && index < ref.weights.length) {
            if (random < ref.weights[index]) {
                return ref.items.get(index);
            }
        }

        /* This should never happen, but it ensures we will return a correct
         * object in case there is some floating point inequality problem
         * wrt the cumulative probabilities. */
        return ref.items.get(ref.items.size() - 1);
    }

    public BalancerChooser(K uniqueKey) {
        this(uniqueKey, new ArrayList<BalancerPair<T>>());
    }

    public BalancerChooser(K uniqueKey, List<BalancerPair<T>> pairs) {
        BalancerRef<T> ref = new BalancerRef<T>(pairs);
        ref.refresh();
        this.uniqueKey = uniqueKey;
        this.ref = ref;
    }

    public K getUniqueKey() {
        return uniqueKey;
    }

    public BalancerRef<T> getRef() {
        return ref;
    }

    public void refresh(List<BalancerPair<T>> itemsWithWeight) {
        BalancerRef<T> newRef = new BalancerRef<T>(itemsWithWeight);
        newRef.refresh();
        newRef.poller = this.ref.poller.refresh(newRef.items);
        this.ref = newRef;
    }

    public class BalancerRef<T> {

        private List<BalancerPair<T>> itemsWithWeight;
        private List<T> items = new ArrayList<T>();
        private BalancerPoller<T> poller = new BalancerGenericPoller<>(items);
        private double[] weights;

        @SuppressWarnings("unchecked")
        public BalancerRef(List<BalancerPair<T>> itemsWithWeight) {
            this.itemsWithWeight = itemsWithWeight;
        }

        public void refresh() {
            Double originWeightSum = (double) 0;

            for (BalancerPair<T> item : itemsWithWeight) {

                double weight = item.weight();
                //ignore item which weight is zero.see test_randomWithWeight_weight0 in ChooserTest
                if (weight <= 0) {
                    continue;
                }

                items.add(item.item());
                if (Double.isInfinite(weight)) {
                    weight = 10000.0D;
                }
                if (Double.isNaN(weight)) {
                    weight = 1.0D;
                }
                originWeightSum += weight;
            }

            double[] exactWeights = new double[items.size()];
            int index = 0;
            for (BalancerPair<T> item : itemsWithWeight) {
                double singleWeight = item.weight();
                //ignore item which weight is zero.see test_randomWithWeight_weight0 in ChooserTest
                if (singleWeight <= 0) {
                    continue;
                }
                exactWeights[index++] = singleWeight / originWeightSum;
            }

            weights = new double[items.size()];
            double randomRange = 0D;
            for (int i = 0; i < index; i++) {
                weights[i] = randomRange + exactWeights[i];
                randomRange += exactWeights[i];
            }

            double doublePrecisionDelta = 0.0001;

            if (index == 0 || (Math.abs(weights[index - 1] - 1) < doublePrecisionDelta)) {
                return;
            }
            throw new IllegalStateException("Cumulative Weight caculate wrong , the sum of probabilities does not equals 1.");
        }

        @Override
        public int hashCode() {
            return itemsWithWeight.hashCode();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null) {
                return false;
            }
            if (getClass() != other.getClass()) {
                return false;
            }
            if (!(other.getClass().getGenericInterfaces()[0].equals(this.getClass().getGenericInterfaces()[0]))) {
                return false;
            }
            BalancerRef<T> otherRef = (BalancerRef<T>) other;
            if (itemsWithWeight == null) {
                if (otherRef.itemsWithWeight != null) {
                    return false;
                }
            } else {
                if (otherRef.itemsWithWeight == null) {
                    return false;
                } else {
                    return this.itemsWithWeight.equals(otherRef.itemsWithWeight);
                }
            }
            return true;
        }
    }

    @Override
    public int hashCode() {
        return uniqueKey.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (getClass() != other.getClass()) {
            return false;
        }

        BalancerChooser otherChooser = (BalancerChooser) other;
        if (this.uniqueKey == null) {
            if (otherChooser.getUniqueKey() != null) {
                return false;
            }
        } else {
            if (otherChooser.getUniqueKey() == null) {
                return false;
            } else if (!this.uniqueKey.equals(otherChooser.getUniqueKey())) {
                return false;
            }

        }

        if (this.ref == null) {
            if (otherChooser.getRef() != null) {
                return false;
            }
        } else {
            if (otherChooser.getRef() == null) {
                return false;
            } else if (!this.ref.equals(otherChooser.getRef())) {
                return false;
            }

        }

        return true;
    }
}
