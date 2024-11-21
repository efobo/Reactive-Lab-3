package statistics;

import entities.Manufacturer;
import entities.Product;
import entities.Review;
import generators.ManufacturerGenerator;
import generators.ProductGenerator;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;


public class ReactiveStatistics {

    public static Map<Manufacturer, Double> calculateStatisticsAsync(List<Product> products, int batchSize) throws InterruptedException {
        Flowable<Product> productFlowable = Flowable.create(emitter -> {
            for (Product product : products) {
                emitter.onNext(product);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);

        Map<Manufacturer, StatisticsAccumulator> statistics = new ConcurrentHashMap<>();
        CountDownLatch latch = new CountDownLatch(1);

        productFlowable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new RegulatedStatisticsSubscriber(statistics, batchSize, latch));

        latch.await();
        return finalizeStatistics(statistics);
    }

    private static Map<Manufacturer, Double> finalizeStatistics(Map<Manufacturer, StatisticsAccumulator> statistics) {
        return statistics.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getAverageRating()
                ));
    }

    static class RegulatedStatisticsSubscriber implements Subscriber<Product> {
        private final Map<Manufacturer, StatisticsAccumulator> statistics;
        private final int batchSize;
        private final CountDownLatch latch;
        private Subscription subscription;
        private int processed = 0;

        public RegulatedStatisticsSubscriber(Map<Manufacturer, StatisticsAccumulator> statistics, int batchSize, CountDownLatch latch) {
            this.statistics = statistics;
            this.batchSize = batchSize;
            this.latch = latch;
        }

        @Override
        public void onSubscribe(Subscription s) {
            this.subscription = s;
            s.request(batchSize);
        }

        @Override
        public void onNext(Product product) {
            Manufacturer manufacturer = product.getManufacturer();
            statistics.computeIfAbsent(manufacturer, k -> new StatisticsAccumulator())
                    .addReviews(product.getReviews());

            processed++;
            if (processed % batchSize == 0) {
                System.out.printf("Processed %d products so far.\n", processed);
                subscription.request(batchSize);
            }
        }

        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
            latch.countDown();
        }

        @Override
        public void onComplete() {
            System.out.println("Processing complete.");
            latch.countDown();
        }
    }

    static class StatisticsAccumulator {
        private int totalRating = 0;
        private int count = 0;

        public void addReviews(List<Review> reviews) {
            for (Review review : reviews) {
                totalRating += review.getRating();
                count++;
            }
        }

        public double getAverageRating() {
            return count == 0 ? 0.0 : (double) totalRating / count;
        }
    }
//    public static Map<Manufacturer, Double> calculateStatisticsAsync(long limit, int batchSize) throws InterruptedException {
//        Map<Manufacturer, Double> statistics = new ConcurrentHashMap<>();
//        CountDownLatch latch = new CountDownLatch(1);
//
//        AtomicLong remainingLimit = new AtomicLong(limit);
//
//        Flowable<Product> flowable = Flowable.generate(emitter -> {
//            ManufacturerGenerator manufacturerGenerator = new ManufacturerGenerator();
//            List<Manufacturer> manufacturers = manufacturerGenerator.generateList(3);
//            ProductGenerator productGenerator = new ProductGenerator(manufacturers, 10);
//            Product product = productGenerator.generate();
//            emitter.onNext(product);
//
//            if (remainingLimit.decrementAndGet() <= 0) {
//                emitter.onComplete();
//            }
//        });
//
//        flowable
//                .observeOn(Schedulers.computation())
//                .buffer(batchSize)
//                .subscribe(new Subscriber<List<Product>>() {
//                    private Subscription subscription;
//
//                    @Override
//                    public void onSubscribe(Subscription s) {
//                        this.subscription = s;
//                        subscription.request(batchSize);
//                    }
//
//                    @Override
//                    public void onNext(List<Product> batch) {
//                        batch.forEach(product -> {
//                            Manufacturer manufacturer = product.getManufacturer();
//                            List<Review> reviews = product.getReviews(); // Здесь без задержки
//                            double avgRating = reviews.stream()
//                                    .mapToInt(Review::getRating)
//                                    .average()
//                                    .orElse(0.0);
//
//                            statistics.merge(manufacturer, avgRating, (oldValue, newValue) ->
//                                    (oldValue + newValue) / 2);
//                        });
//
//                        subscription.request(batchSize);
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//                        t.printStackTrace();
//                        latch.countDown();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        latch.countDown();
//                    }
//                });
//
//        latch.await();
//        return statistics;
//    }

}



