import benchmark.BenchmarkCalculation;
import entities.Manufacturer;
import entities.Product;
import generators.ManufacturerGenerator;
import generators.ProductGenerator;
import hu.akarnokd.rxjava3.math.MathObservable;
import io.reactivex.rxjava3.core.Observable;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import statistics.Calculation;

import java.util.List;

public class Main {
    public static void main(String[] args) throws RunnerException {
        MathObservable.averageDouble(Observable.just(1, 2, 3)).subscribe(System.out::println).dispose();

        List<Manufacturer> manufacturers;
        List<Product> products;

        int productCount = 20;
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
        Calculation.printResult(Calculation.avgRatingWithRX(products, 0));

        /*
        Options opt = new OptionsBuilder()
                .include(BenchmarkCalculation.class.getSimpleName())
                .build();
        new Runner(opt).run();*/
    }
}