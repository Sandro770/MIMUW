package com.company.giełda;

import com.company.oferta.Oferta;
import com.company.produkt.Produkt;

public abstract class ComparatorOfertNaGieldzie {
    protected static int compareTypeAndProduct(Oferta o1, Oferta o2) {
        int cmpRes = o1.typ.compareTo(o2.typ);
        if (cmpRes != 0)
            return cmpRes;

        Produkt.Typ t1, t2;
        t1 = o1.produkt.dajTyp();
        t2 = o2.produkt.dajTyp();

        return t1.compareTo(t2);
    }
}
