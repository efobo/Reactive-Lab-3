package statistics;

import entities.Manufacturer;
import entities.Product;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AverageRatingCollector implements Collector<Product, Map<Manufacturer, AverageRatingAccumulator>, Map<Manufacturer, Double>> {
    private final List<Manufacturer> manufacturers;
    private final long delay;

    @Override
    public Supplier<Map<Manufacturer, AverageRatingAccumulator>> supplier() {
        return () -> {
            Map<Manufacturer, AverageRatingAccumulator> map = new HashMap<>();
            manufacturers.forEach(manufacturer -> map.put(manufacturer, new AverageRatingAccumulator()));
            return map;
        };
    }

    @Override
    public BiConsumer<Map<Manufacturer, AverageRatingAccumulator>, Product> accumulator() {
        return (map, product) -> {
            Manufacturer manufacturer = product.getManufacturer();
            AverageRatingAccumulator accumulator = map.get(manufacturer);

            if (accumulator != null) { // Убедимся, что производитель существует
                product.getReviewsWithDelay(delay).forEach(review -> accumulator.addRating(review.getRating()));
            }
        };
    }

    @Override
    public BinaryOperator<Map<Manufacturer, AverageRatingAccumulator>> combiner() {
        return (map1, map2) -> {
            map2.forEach((manufacturer, accumulator) ->
                    map1.merge(manufacturer, accumulator, AverageRatingAccumulator::combine));
            return map1;
        };
    }

    @Override
    public Function<Map<Manufacturer, AverageRatingAccumulator>, Map<Manufacturer, Double>> finisher() {
        return map -> map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getAverage()));
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
    }
}
