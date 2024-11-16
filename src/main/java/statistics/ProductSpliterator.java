package statistics;

import entities.Product;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ProductSpliterator implements Spliterator<Product> {
    private final List<Product> products;
    private int current = 0;

    @Override
    public boolean tryAdvance(Consumer<? super Product> action) {
        if (current < products.size()) {
            action.accept(products.get(current++));
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<Product> trySplit() {
        int currentSize = products.size() - current;
        if (currentSize < 10) { // Минимальный размер для разделения
            return null;
        }
        int splitPos = current + currentSize / 2;
        List<Product> splitList = products.subList(current, splitPos);
        current = splitPos;
        return new ProductSpliterator(splitList);
    }

    @Override
    public long estimateSize() {
        return products.size() - current;
    }

    @Override
    public int characteristics() {
        return ORDERED | SIZED | SUBSIZED | NONNULL | IMMUTABLE;
    }
}

