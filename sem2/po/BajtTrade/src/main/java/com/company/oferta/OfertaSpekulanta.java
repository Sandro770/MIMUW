package com.company.oferta;

import com.company.agent.Agent;
import com.company.produkt.Produkt;

import java.util.Comparator;
import java.util.Optional;

import static com.company.oferta.Oferta.Typ.KUPNA;
import static com.company.oferta.Oferta.Typ.SPRZEDAZY;

public class OfertaSpekulanta extends Oferta implements Comparable {
    final double cena;

    public OfertaSpekulanta(Typ typ, Agent czyja, Produkt produkt, double cena) {
        super(typ, czyja, produkt);
        this.cena = cena;
    }

    private static int comparePoziom(OfertaSpekulanta o1, OfertaSpekulanta o2) {
        Optional<Integer> p1 = o1.produkt.dajPoziom();
        Optional<Integer> p2 = o2.produkt.dajPoziom();

        if (p1.isEmpty())
            return 0;

        return p2.get().compareTo(p1.get());
    }

    @Override
    public int compareTo(Object o) {
        if (!o.getClass().equals(this.getClass()))
            throw new IllegalArgumentException();

        OfertaSpekulanta o2 = (OfertaSpekulanta) o;

        if (this.typ != o2.typ)
            throw new IllegalStateException();

        if (this.typ == SPRZEDAZY) {
            int cmpRes = comparePoziom(this, o2);

            if (cmpRes != 0)
                return cmpRes;

            return Double.compare(this.cena, o2.cena);
        }
        else {
            return Double.compare(o2.cena, this.cena);
        }
    }

    public double dajCene() {
        return this.cena;
    }
}
