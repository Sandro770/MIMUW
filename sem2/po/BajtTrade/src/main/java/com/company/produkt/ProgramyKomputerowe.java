package com.company.produkt;

import com.company.strategia.ścieżkaKariery.Programista;
import com.company.strategia.ścieżkaKariery.ŚcieżkaKariery;

import java.util.Optional;

public class ProgramyKomputerowe extends MajacyPoziomy {
    public ProgramyKomputerowe(Typ typ, int ilosc, Optional<Integer> poziomJakości) {
        super(typ, ilosc, poziomJakości);
    }

    @Override
    public ŚcieżkaKariery dajOdpowiadającąŚcieżkęKariery() {
        return new Programista();
    }
}
