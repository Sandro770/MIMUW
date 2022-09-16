package com.company.produkt;

import com.company.strategia.ścieżkaKariery.Rolnik;
import com.company.strategia.ścieżkaKariery.ŚcieżkaKariery;

public class Jedzenie extends Produkt {
    public Jedzenie(Typ typ, int ilosc) {
        super(typ, ilosc);
    }


    @Override
    public ŚcieżkaKariery dajOdpowiadającąŚcieżkęKariery() {
        return new Rolnik();
    }
}
