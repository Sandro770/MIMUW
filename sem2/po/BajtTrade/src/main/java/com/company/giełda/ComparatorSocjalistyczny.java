package com.company.giełda;

import com.company.oferta.Oferta;

import java.util.Comparator;

public class ComparatorSocjalistyczny extends ComparatorOfertNaGieldzie implements Comparator<Oferta> {
    public int compare(Oferta o1, Oferta o2) {
        int cmpRes = ComparatorKapitalistyczny.compareWithoutOfertType(o1
                , o2);

        if (cmpRes != 0)
            return cmpRes < 0 ? 1 : -1;

        return compareTypeAndProduct(o1, o2);
    }
}
