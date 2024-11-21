import benchmark.BenchmarkCalculation;
import entities.Manufacturer;
import entities.Product;
import generators.ManufacturerGenerator;
import generators.ProductGenerator;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import statistics.Calculation;

import java.util.List;

public class Main {
    public static void main(String[] args) throws RunnerException {
        /*
        List<Manufacturer> manufacturers;
        List<Product> products;

        int productCount = 2000;
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

        System.out.println("ParStream:\n");
        Calculation.printResult(Calculation.avgRatingWithParPipeline(products, 0));

        System.out.println("Observable:\n");
        Calculation.printResult(Calculation.avgRatingWithRxObservable(products, 0));

        System.out.println("Flowable:\n");
        Calculation.printResult(Calculation.avgRatingWithRxFlowable(products, 100));
        */

        Options opt = new OptionsBuilder()
                .include(BenchmarkCalculation.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}