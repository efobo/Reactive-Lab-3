package statistics;

import entities.Manufacturer;
import entities.Product;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Subscription;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class AverageRatingSubscriber implements FlowableSubscriber<Product> {
    private final Map<Manufacturer, AverageRatingAccumulator> innerMap = new ConcurrentHashMap<>();
    private final Map<Manufacturer, Double> result;
    private final int batchSize;

    private int processed = 0;
    private Subscription subscription;

    @Override
    public void onSubscribe(@NonNull Subscription s) {
        subscription = s;
        subscription.request(batchSize);
    }

    @Override
    public void onNext(Product product) {
        innerMap.computeIfAbsent(product.getManufacturer(), k -> new AverageRatingAccumulator())
                .addReviews(product.getReviews());
        processed++;
        if (processed % batchSize == 0) {
            subscription.request(batchSize);
        }
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }

    @Override
    public void onComplete() {
        innerMap.forEach((k, v) -> result.put(k, v.getAverage()));
    }
}