package mtn.rso.pricecompare.priceupdater.api.v1.processing;

import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;

public class UpdateDataSource02 {

    final static Set<String> itemNames = Set.of(
            "1001 Cvet čaj meta 30g", "1001 Cvet čaj šipek s hibiskusom 60g", "Agragold beli kristalni sladkor 1kg",
            "Alpsko mleko 1.5% 1L", "Ave čevapčiči Leskovački 480g", "Banane 1kg",
            "Barcaffe kava Classic mleta 200g", "Chio čips paprika 150g", "Chio čips sir 150g",
            "Coca Cola gazirana pijača 0.5L", "Cvetača 1 kos", "Delamaris skuša z zelenjavo Izola Brand 3x125g",
            "Granatno jabolko 1kg", "Jar detergent Citrus 650ml", "Jogobella sadni jogurt 150g",
            "Leibniz masleni keksi 200g", "Limone 1kg", "Lindor bonboni lešnik 200g",
            "Meggle maslo 250g", "Merci bonbonjera rdeča 250g", "Milka čokolada celi lešniki 100g",
            "Natureta delikatesne kumarice 530g", "Nescafe cappuccino z vanilijo 148g", "Nutella kremni namaz 400g",
            "Rocher temna čokolada 90g", "Thomy majoneza 611g", "Zvijezda alkoholni kis 1L",
            "Zvijezda sončnično olje 1L", "Čokolino 1.8kg", "Žito pšenična moka T400 1kg"
    );

    final static Map<String, Double> pricesTus = Map.ofEntries(
            entry("1001 Cvet čaj meta 30g", 1.25), entry("1001 Cvet čaj šipek s hibiskusom 60g", 1.56),
            entry("Alpsko mleko 1.5% 1L", 1.04), entry("Ave čevapčiči Leskovački 480g", 5.08),
            entry("Banane 1kg", 1.38), entry("Barcaffe kava Classic mleta 200g", 2.38),
            entry("Chio čips paprika 150g", 1.95), entry("Chio čips sir 150g", 1.82),
            entry("Cvetača 1 kos", 1.89), entry("Granatno jabolko 1kg", 2.15),
            entry("Jar detergent Citrus 650ml", 3.1), entry("Jogobella sadni jogurt 150g", 0.42),
            entry("Lindor bonboni lešnik 200g", 4.51), entry("Merci bonbonjera rdeča 250g", 3.79),
            entry("Milka čokolada celi lešniki 100g", 0.89), entry("Nescafe cappuccino z vanilijo 148g", 2.25),
            entry("Nutella kremni namaz 400g", 2.89), entry("Rocher temna čokolada 90g", 1.73),
            entry("Čokolino 1.8kg", 14.53)
    );

    final static Map<String, Double> pricesMercator = Map.ofEntries(
            entry("1001 Cvet čaj meta 30g", 1.59), entry("1001 Cvet čaj šipek s hibiskusom 60g", 1.85),
            entry("Agragold beli kristalni sladkor 1kg", 1.65), entry("Alpsko mleko 1.5% 1L", 1.77),
            entry("Ave čevapčiči Leskovački 480g", 5.33), entry("Banane 1kg", 1.84),
            entry("Barcaffe kava Classic mleta 200g", 2.57), entry("Chio čips paprika 150g", 1.74),
            entry("Chio čips sir 150g", 2.0), entry("Coca Cola gazirana pijača 0.5L", 0.9),
            entry("Cvetača 1 kos", 2.34), entry("Delamaris skuša z zelenjavo Izola Brand 3x125g", 5.51),
            entry("Granatno jabolko 1kg", 2.69), entry("Jogobella sadni jogurt 150g", 0.6),
            entry("Leibniz masleni keksi 200g", 2.08), entry("Limone 1kg", 2.11),
            entry("Lindor bonboni lešnik 200g", 3.5), entry("Meggle maslo 250g", 4.3),
            entry("Merci bonbonjera rdeča 250g", 5.14), entry("Milka čokolada celi lešniki 100g", 1.21),
            entry("Natureta delikatesne kumarice 530g", 2.11), entry("Nescafe cappuccino z vanilijo 148g", 3.26),
            entry("Nutella kremni namaz 400g", 3.13), entry("Thomy majoneza 611g", 3.78),
            entry("Zvijezda alkoholni kis 1L", 1.52), entry("Zvijezda sončnično olje 1L", 2.4),
            entry("Čokolino 1.8kg", 13.43), entry("Žito pšenična moka T400 1kg", 1.32)
    );

    final static Map<String, Double> pricesSpar = Map.ofEntries(
            entry("1001 Cvet čaj meta 30g", 1.57), entry("1001 Cvet čaj šipek s hibiskusom 60g", 1.55),
            entry("Agragold beli kristalni sladkor 1kg", 1.69), entry("Alpsko mleko 1.5% 1L", 1.52),
            entry("Ave čevapčiči Leskovački 480g", 7.23), entry("Banane 1kg", 1.51),
            entry("Barcaffe kava Classic mleta 200g", 3.13), entry("Chio čips paprika 150g", 2.61),
            entry("Chio čips sir 150g", 2.3), entry("Coca Cola gazirana pijača 0.5L", 0.77),
            entry("Cvetača 1 kos", 2.1), entry("Delamaris skuša z zelenjavo Izola Brand 3x125g", 5.12),
            entry("Granatno jabolko 1kg", 1.89), entry("Jar detergent Citrus 650ml", 4.46),
            entry("Leibniz masleni keksi 200g", 2.02), entry("Limone 1kg", 1.93),
            entry("Lindor bonboni lešnik 200g", 5.18), entry("Meggle maslo 250g", 3.96),
            entry("Merci bonbonjera rdeča 250g", 5.1), entry("Milka čokolada celi lešniki 100g", 1.17),
            entry("Natureta delikatesne kumarice 530g", 2.11), entry("Nutella kremni namaz 400g", 2.68),
            entry("Rocher temna čokolada 90g", 2.13), entry("Thomy majoneza 611g", 4.04),
            entry("Zvijezda alkoholni kis 1L", 1.33), entry("Zvijezda sončnično olje 1L", 3.94),
            entry("Čokolino 1.8kg", 18.72), entry("Žito pšenična moka T400 1kg", 1.73)
    );

}
