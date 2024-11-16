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
    @Param({"5000", "25000", "50000"})
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

    // ПОСЛЕДОВАТЕЛЬНЫЕ СТРИМЫ

    @Benchmark
    @Group("_1_SequentialStreamNoDelay")
    public Map<Manufacturer, Double> seqCollectorNoDelay() {
        return Calculation.avgRatingWithSeqCollector(products, manufacturers, 0);
    }

    @Benchmark()
    @Group("_1_SequentialStreamNoDelay")
    public Map<Manufacturer, Double> seqPipelineNoDelay() {
        return Calculation.avgRatingWithSeqPipeline(products, 0);
    }

    @Benchmark
    @Group("_1_SequentialStreamWithDelay")
    public Map<Manufacturer, Double> seqCollectorWithDelay() {
        return Calculation.avgRatingWithSeqCollector(products, manufacturers, 1);
    }

    @Benchmark
    @Group("_1_SequentialStreamWithDelay")
    public Map<Manufacturer, Double> seqPipelineWithDelay() {
        return Calculation.avgRatingWithSeqPipeline(products, 1);
    }

    // ПАРАЛЛЕЛЬНЫЕ СТРИМЫ

    @Benchmark
    @Group("_2_ParallelStreamNoDelay")
    public Map<Manufacturer, Double> parCollectorNoDelay() {
        return Calculation.avgRatingWithParCollector(products, manufacturers, 0);
    }

    @Benchmark
    @Group("_2_ParallelStreamNoDelay")
    public Map<Manufacturer, Double> parPipelineNoDelay() {
        return Calculation.avgRatingWithParPipeline(products, 0);
    }

    @Benchmark
    @Group("_2_ParallelStreamWithDelay")
    public Map<Manufacturer, Double> parCollectorWithDelay() {
        return Calculation.avgRatingWithParCollector(products, manufacturers, 1);
    }

    @Benchmark
    @Group("_2_ParallelStreamWithDelay")
    public Map<Manufacturer, Double> parPipelineWithDelay() {
        return Calculation.avgRatingWithParPipeline(products, 1);
    }

    // ОПТИМИЗИРОВАННЫЕ ПАРАЛЛЕЛЬНЫЕ СТРИМЫ (СО СПЛИТЕРАТОРОМ)

    @Benchmark
    @Group("_3_OptimizedParallelStreamNoDelay")
    public Map<Manufacturer, Double> optParCollectorNoDelay() {
        return Calculation.avgRatingWithOptParCollector(products, manufacturers, 0);
    }

    @Benchmark
    @Group("_3_OptimizedParallelStreamNoDelay")
    public Map<Manufacturer, Double> optParPipelineNoDelay() {
        return Calculation.avgRatingWithParPipeline(products, 0);
    }

    @Benchmark
    @Group("_3_OptimizedParallelStreamWithDelay")
    public Map<Manufacturer, Double> optParCollectorWithDelay() {
        return Calculation.avgRatingWithOptParCollector(products, manufacturers, 1);
    }

    @Benchmark
    @Group("_3_OptimizedParallelStreamWithDelay")
    public Map<Manufacturer, Double> optParPipelineWithDelay() {
        return Calculation.avgRatingWithOptParPipeline(products, 1);
    }
}