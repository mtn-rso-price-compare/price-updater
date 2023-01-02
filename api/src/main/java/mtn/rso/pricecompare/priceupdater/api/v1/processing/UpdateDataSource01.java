package mtn.rso.pricecompare.priceupdater.api.v1.processing;

import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;

public class UpdateDataSource01 {

    final static Set<String> itemNames = Set.of(
            "1001 Cvet čaj šipek s hibiskusom 60g", "ABC sirni namaz klasik 100g", "Agragold beli kristalni sladkor 1kg",
            "Alpsko mleko 1.5% 1L", "Bakina Tajna džem figa 375g", "Banane 1kg",
            "Chio čips sir 150g", "Cvetača 1 kos", "Delamaris skuša z zelenjavo Izola Brand 3x125g",
            "Ferrero Rocher bonbonjera 100g", "Granatno jabolko 1kg", "Jar detergent Citrus 650ml",
            "Jogobella sadni jogurt 150g", "Leibniz masleni keksi 200g", "Lindor bonboni lešnik 200g",
            "Lipton zeleni čaj 32.5g", "Maestro mleti cimet 70g", "Meggle maslo 250g",
            "Milka čokolada celi lešniki 100g", "Natureta delikatesne kumarice 530g", "Nescafe cappuccino z vanilijo 148g",
            "Nutella kremni namaz 400g", "Oreo keksi 176g", "Pril detergent limona in melisa 750ml",
            "Rocher temna čokolada 90g", "Thomy majoneza 611g", "Tuc slani krekerji 100g",
            "Zvijezda alkoholni kis 1L", "Čokolino 1.8kg", "Žito pšenična moka T400 1kg"
    );

    final static Map<String, Double> pricesTus = Map.ofEntries(
            entry("1001 Cvet čaj šipek s hibiskusom 60g", 1.34), entry("ABC sirni namaz klasik 100g", 0.68),
            entry("Alpsko mleko 1.5% 1L", 1.01), entry("Bakina Tajna džem figa 375g", 3.02),
            entry("Banane 1kg", 1.38), entry("Chio čips sir 150g", 1.89),
            entry("Cvetača 1 kos", 2.05), entry("Ferrero Rocher bonbonjera 100g", 2.56),
            entry("Granatno jabolko 1kg", 2.16), entry("Jar detergent Citrus 650ml", 3.1),
            entry("Jogobella sadni jogurt 150g", 0.46), entry("Lindor bonboni lešnik 200g", 4.66),
            entry("Lipton zeleni čaj 32.5g", 1.61), entry("Maestro mleti cimet 70g", 1.08),
            entry("Milka čokolada celi lešniki 100g", 0.85), entry("Nescafe cappuccino z vanilijo 148g", 2.21),
            entry("Nutella kremni namaz 400g", 3.03), entry("Oreo keksi 176g", 1.43),
            entry("Pril detergent limona in melisa 750ml", 2.14), entry("Rocher temna čokolada 90g", 1.72),
            entry("Tuc slani krekerji 100g", 0.76), entry("Čokolino 1.8kg", 12.62)
    );

    final static Map<String, Double> pricesMercator = Map.ofEntries(
            entry("1001 Cvet čaj šipek s hibiskusom 60g", 1.52), entry("ABC sirni namaz klasik 100g", 1.05),
            entry("Agragold beli kristalni sladkor 1kg", 1.74), entry("Alpsko mleko 1.5% 1L", 1.79),
            entry("Bakina Tajna džem figa 375g", 3.46), entry("Banane 1kg", 1.57),
            entry("Chio čips sir 150g", 2.03), entry("Cvetača 1 kos", 2.44),
            entry("Delamaris skuša z zelenjavo Izola Brand 3x125g", 5.54), entry("Granatno jabolko 1kg", 2.64),
            entry("Jogobella sadni jogurt 150g", 0.57), entry("Leibniz masleni keksi 200g", 1.8),
            entry("Lindor bonboni lešnik 200g", 4.09), entry("Maestro mleti cimet 70g", 1.23),
            entry("Meggle maslo 250g", 4.65), entry("Milka čokolada celi lešniki 100g", 1.24),
            entry("Natureta delikatesne kumarice 530g", 2.27), entry("Nescafe cappuccino z vanilijo 148g", 3.93),
            entry("Nutella kremni namaz 400g", 3.52), entry("Oreo keksi 176g", 1.4),
            entry("Pril detergent limona in melisa 750ml", 3.28), entry("Thomy majoneza 611g", 4.02),
            entry("Tuc slani krekerji 100g", 0.93), entry("Zvijezda alkoholni kis 1L", 1.41),
            entry("Čokolino 1.8kg", 15.17), entry("Žito pšenična moka T400 1kg", 1.49)
    );

    final static Map<String, Double> pricesSpar = Map.ofEntries(
            entry("1001 Cvet čaj šipek s hibiskusom 60g", 1.67), entry("ABC sirni namaz klasik 100g", 1.31),
            entry("Agragold beli kristalni sladkor 1kg", 1.57), entry("Alpsko mleko 1.5% 1L", 1.3),
            entry("Bakina Tajna džem figa 375g", 3.7), entry("Banane 1kg", 1.5),
            entry("Chio čips sir 150g", 2.48), entry("Cvetača 1 kos", 2.24),
            entry("Delamaris skuša z zelenjavo Izola Brand 3x125g", 4.71), entry("Ferrero Rocher bonbonjera 100g", 2.6),
            entry("Granatno jabolko 1kg", 2.04), entry("Jar detergent Citrus 650ml", 3.86),
            entry("Leibniz masleni keksi 200g", 2.19), entry("Lindor bonboni lešnik 200g", 5.47),
            entry("Maestro mleti cimet 70g", 1.35), entry("Meggle maslo 250g", 3.83),
            entry("Milka čokolada celi lešniki 100g", 1.27), entry("Natureta delikatesne kumarice 530g", 2.13),
            entry("Nutella kremni namaz 400g", 2.31), entry("Oreo keksi 176g", 1.85),
            entry("Pril detergent limona in melisa 750ml", 3.19), entry("Rocher temna čokolada 90g", 2.03),
            entry("Thomy majoneza 611g", 3.34), entry("Tuc slani krekerji 100g", 0.93),
            entry("Zvijezda alkoholni kis 1L", 1.19), entry("Čokolino 1.8kg", 18.09),
            entry("Žito pšenična moka T400 1kg", 1.84)
    );

}
