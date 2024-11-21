package statistics;

import entities.Manufacturer;
import entities.Product;
import entities.Review;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.Map;

public class AverageRatingSubscriber implements Subscriber<Product> {
    private final Map<Manufacturer, StatisticsAccumulator> statistics;
    private final int batchSize;
    private Subscription subscription;
    private int processed = 0;

    public AverageRatingSubscriber(Map<Manufacturer, StatisticsAccumulator> statistics, int batchSize) {
        this.statistics = statistics;
        this.batchSize = batchSize;
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
            subscription.request(batchSize);
        }
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void onComplete() {
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
}