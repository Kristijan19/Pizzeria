package com.anas.pizzeria.model;

import com.anas.pizzeria.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PizzaMenuCatalog {

    public static final String DOC_ID = "current";

    public static final class Entry {
        public final String id;
        public final int nameResId;
        public final int imageResId;
        public final double defaultPrice;

        public Entry(String id, int nameResId, int imageResId, double defaultPrice) {
            this.id = id;
            this.nameResId = nameResId;
            this.imageResId = imageResId;
            this.defaultPrice = defaultPrice;
        }
    }

    private static final Entry[] ENTRIES = {
            new Entry("margherita", R.string.pizza_margherita, R.drawable.pizza_item, 700),
            new Entry("pepperoni", R.string.pizza_pepperoni, R.drawable.pizza_item, 1100),
            new Entry("hawaiian", R.string.pizza_hawaiian, R.drawable.pizza_item, 1200),
            new Entry("bbq_chicken", R.string.pizza_bbq_chicken, R.drawable.pizza_item, 1300),
            new Entry("supreme", R.string.pizza_supreme, R.drawable.pizza_item, 1400),
            new Entry("veggie", R.string.pizza_veggie, R.drawable.pizza_item, 1200),
            new Entry("meat_lovers", R.string.pizza_meat_lovers, R.drawable.pizza_item, 1100),
            new Entry("four_cheese", R.string.pizza_four_cheese, R.drawable.pizza_item, 1000),
            new Entry("buffalo", R.string.pizza_buffalo, R.drawable.pizza_item, 1300),
            new Entry("mushroom", R.string.pizza_mushroom, R.drawable.pizza_item, 1200),
            new Entry("mediterranean", R.string.pizza_mediterranean, R.drawable.pizza_item, 1100),
            new Entry("fajita", R.string.pizza_fajita, R.drawable.pizza_item, 900),
            new Entry("olive", R.string.pizza_olive, R.drawable.pizza_item, 700),
            new Entry("tandoori", R.string.pizza_tandoori, R.drawable.pizza_item, 1300),
            new Entry("extravaganza", R.string.pizza_extravaganza, R.drawable.pizza_item, 1800),
            new Entry("hot_spicy", R.string.pizza_hot_spicy, R.drawable.pizza_item, 800),
    };

    private PizzaMenuCatalog() {}

    public static List<Entry> getEntries() {
        List<Entry> list = new ArrayList<>();
        for (Entry e : ENTRIES) list.add(e);
        return list;
    }

    public static Map<String, Double> getDefaultPrices() {
        Map<String, Double> map = new HashMap<>();
        for (Entry e : ENTRIES) {
            map.put(e.id, e.defaultPrice);
        }
        return map;
    }
}
