package com.company.produkt;

import com.company.strategia.ścieżkaKariery.Górnik;
import com.company.strategia.ścieżkaKariery.ŚcieżkaKariery;

public class Diamenty extends Produkt {
    public Diamenty(Typ typ, double ilosc) {
        super(typ, ilosc);
    }

    @Override
    public ŚcieżkaKariery dajOdpowiadającąŚcieżkęKariery() {
        return new Górnik();
    }
}
