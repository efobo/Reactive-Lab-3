package statistics;

import entities.Manufacturer;
import entities.Product;
import entities.Review;
import hu.akarnokd.rxjava3.math.MathObservable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Calculation {
    public static Map<Manufacturer, Double> avgRatingWithParPipeline(List<Product> products, long delay) {
        return products.parallelStream()
                .collect(Collectors.groupingByConcurrent(
                        Product::getManufacturer,
                        Collectors.flatMapping(
                                product -> product.getReviewsWithDelay(delay).parallelStream(),
                                Collectors.averagingDouble(Review::getRating)
                        )
                ));
    }

    public static Map<Manufacturer, Double> avgRatingWithRX(List<Product> products, long delay) {
        return Observable.fromIterable(products)
                .observeOn(Schedulers.computation())
                .groupBy(Product::getManufacturer)
                .flatMap(group -> group
                        .map(product -> Observable.fromIterable(product.getReviewsWithDelay(delay)).map(Review::getRating))
                        .flatMap(MathObservable::averageDouble)
                        .map(average -> Map.entry(group.getKey(), average))
                )
                .toMap(Map.Entry::getKey, Map.Entry::getValue)
                .blockingGet();
    }

    public static void printResult(Map<Manufacturer, Double> result) {
        for (Manufacturer manufacturer : result.keySet()) {
            System.out.printf("For manufacturer \"%s\" average product rating is %f\n", manufacturer.name(), result.get(manufacturer));
        }
    }
}
