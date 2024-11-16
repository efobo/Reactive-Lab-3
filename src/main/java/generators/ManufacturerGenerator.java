package generators;

import entities.Manufacturer;

public class ManufacturerGenerator extends AbstractGenerator<Manufacturer> {
    @Override
    protected Manufacturer generate() {
        return new Manufacturer(
                id++,
                faker.company().name(),
                faker.timeAndDate().birthday(0, 100)
        );
    }
}
