package com.company.produkt;

import com.company.strategia.ścieżkaKariery.Inżynier;
import com.company.strategia.ścieżkaKariery.ŚcieżkaKariery;

import java.util.Optional;

public class Narzędzia extends MajacyPoziomy {
    public Narzędzia(Typ typ, int ilosc, Optional<Integer> poziomJakości) {
        super(typ, ilosc, poziomJakości);
    }

    @Override
    public ŚcieżkaKariery dajOdpowiadającąŚcieżkęKariery() {
        return new Inżynier();
    }
}
