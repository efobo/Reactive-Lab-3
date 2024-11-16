package statistics;

import entities.Manufacturer;
import entities.Product;
import entities.Review;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Calculation {

    // Конвейер с помощью Stream API на базе коллекторов из стандартной библиотеки
    public static Map<Manufacturer, Double> avgRatingWithSeqPipeline(List<Product> products, long delay) {
        return products.stream()
                .collect(Collectors.groupingBy(
                        Product::getManufacturer,
                        Collectors.flatMapping(
                                product -> product.getReviewsWithDelay(delay).stream(),
                                Collectors.averagingDouble(Review::getRating)
                        )
                ));
    }

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

    public static Map<Manufacturer, Double> avgRatingWithOptParPipeline(List<Product> products, long delay) {
        return StreamSupport.stream(new ProductSpliterator(products), true)
                .collect(Collectors.groupingByConcurrent(
                        Product::getManufacturer,
                        Collectors.flatMapping(
                                product -> product.getReviewsWithDelay(delay).parallelStream(),
                                Collectors.averagingDouble(Review::getRating)
                        )
                ));
    }

    // Собственный коллектор
    public static Map<Manufacturer, Double> avgRatingWithSeqCollector(List<Product> products, List<Manufacturer> manufacturers, long delay) {
        return products.stream()
                .collect(new AverageRatingCollector(manufacturers, delay));
    }

    public static Map<Manufacturer, Double> avgRatingWithParCollector(List<Product> products, List<Manufacturer> manufacturers, long delay) {
        return products.parallelStream()
                .collect(new AverageRatingCollector(manufacturers, delay));
    }

    public static Map<Manufacturer, Double> avgRatingWithOptParCollector(List<Product> products, List<Manufacturer> manufacturers, long delay) {
        return StreamSupport.stream(new ProductSpliterator(products), true)
                .collect(new AverageRatingCollector(manufacturers, delay));
    }


    public static void printResult(Map<Manufacturer, Double> result) {
        for (Manufacturer manufacturer : result.keySet()) {
            System.out.printf("For manufacturer \"%s\" average product rating is %f\n", manufacturer.name(), result.get(manufacturer));
        }
    }
}
