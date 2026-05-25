package com.artillexstudios.axcrates.utils;

import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RandomUtils {
    public static CrateReward randomValue(@NotNull HashMap<CrateReward, Double> map) {
        List<Pair<CrateReward, Double>> list = new ArrayList<>();
        map.forEach((key, value) -> list.add(new Pair<>(key, value)));

        EnumeratedDistribution<CrateReward> e = new EnumeratedDistribution<>(list);

        return e.sample();
    }
}
