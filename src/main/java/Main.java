import benchmark.BenchmarkCalculation;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Main {
    public static void main(String[] args) throws RunnerException {
        /*
        List<Manufacturer> manufacturers;
        List<Product> products;

        int productCount = 200;
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

        System.out.println("RX:\n");
        Calculation.printResult(Calculation.avgRatingWithRxObservable(products, 0));
        */

        Options opt = new OptionsBuilder()
                .include(BenchmarkCalculation.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}