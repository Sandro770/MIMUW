package com.company.giełda;

import com.company.oferta.Oferta;
import com.company.produkt.Produkt;

import java.util.Comparator;

public class ComparatorKapitalistyczny extends ComparatorOfertNaGieldzie implements Comparator<Oferta> {
    public static int compareWithoutOfertType(Oferta o1, Oferta o2) {
        double d1 = o1.czyja.ileDiamentow();
        double d2 = o2.czyja.ileDiamentow();

        int resCmp = Double.compare(d2, d1);

        if (resCmp != 0)
            return resCmp;

        double id1 = o1.czyja.dajId();
        double id2 = o1.czyja.dajId();

        return Double.compare(id2, id1);
    }
    public int compare(Oferta o1, Oferta o2) {
        int cmpRes = compareWithoutOfertType(o1, o2);

        if (cmpRes != 0)
            return cmpRes;

        return compareTypeAndProduct(o1, o2);
    }
}
