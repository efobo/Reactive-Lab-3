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
//@Threads(8)
@State(Scope.Benchmark)
@Warmup(iterations = 0)
@Measurement(iterations = 1, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BenchmarkCalculation {
    //@Param({"500", "2000"})
    @Param({"1"})
    private int productCount;

    private List<Manufacturer> manufacturers;
    private List<Product> products;

    @Setup(Level.Trial) // Вызовется в начале всего теста, всего тестов 3 (поскольку 3 разных productCount)
    public void generate() {
        int manufacturerCount = 3;
        int reviewCount = 10;

        System.out.printf("\nGenerating %d manufacturers... ", manufacturerCount);

        ManufacturerGenerator manufacturerGenerator = new ManufacturerGenerator();
        manufacturers = manufacturerGenerator.generateList(manufacturerCount);

        System.out.println("Done.");
        System.out.printf("Generating %d products with %d reviews... ", productCount, reviewCount);

        ProductGenerator productGenerator = new ProductGenerator(manufacturers, reviewCount);
        products = productGenerator.generateList(productCount);

        System.out.println("Done.");
    }

    //@Benchmark
    //@Group("_2_ParallelStreamWithDelay")
    public Map<Manufacturer, Double> parPipelineWithDelay() {
        return Calculation.avgRatingWithParPipeline(products, 1);
    }

    @Benchmark
    //@Group("_2_ParallelStreamWithDelay")
    public Map<Manufacturer, Double> rxWithDelay() {
        Calculation.avgRatingWithRX(products, 1);
        return null;
    }

}