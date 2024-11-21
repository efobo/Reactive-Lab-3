package benchmark;

import entities.Manufacturer;
import entities.Product;
import generators.ManufacturerGenerator;
import generators.ProductGenerator;
import org.openjdk.jmh.annotations.*;
import statistics.Calculation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Fork(1)
@Threads(8)
@State(Scope.Benchmark)
@Warmup(iterations = 0)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BenchmarkCalculation {
    @Param({"500", "2000", "100000"})
    private int productCount;
    private List<Product> products;

    @Setup(Level.Trial) // Вызовется в начале всего теста, всего тестов 3 (поскольку 3 разных productCount)
    public void generate() {
        int manufacturerCount = 3;
        int reviewCount = 10;

        System.out.printf("\nGenerating %d manufacturers... ", manufacturerCount);

        ManufacturerGenerator manufacturerGenerator = new ManufacturerGenerator();
        List<Manufacturer> manufacturers = manufacturerGenerator.generateList(manufacturerCount);

        System.out.println("Done.");
        System.out.printf("Generating %d products with %d reviews... ", productCount, reviewCount);

        ProductGenerator productGenerator = new ProductGenerator(manufacturers, reviewCount);
        products = productGenerator.generateList(productCount);

        System.out.println("Done.");
    }

    @Benchmark
    public Map<Manufacturer, Double> parPipeline() {
        if (productCount == 100000) {
            return null;
        }
        return Calculation.avgRatingWithParPipeline(products, 1);
    }

    @Benchmark
    public Map<Manufacturer, Double> rxObservable() {
        if (productCount == 100000) {
            return null;
        }
        return Calculation.avgRatingWithRxObservable(products, 1);
    }

    @Benchmark
    public Map<Manufacturer, Double> rxFlowable() {
        return Calculation.avgRatingWithRxFlowable(products, 100);
    }
}