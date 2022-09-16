package com.company.produkt;

import com.company.strategia.ścieżkaKariery.Rzemieślnik;
import com.company.strategia.ścieżkaKariery.ŚcieżkaKariery;

import java.util.Optional;

public class Ubrania extends MajacyPoziomy {
    private int ileUzytych = 0;
    public Ubrania(Typ typ, int ilosc, Optional<Integer> poziomJakości) {
        super(typ, ilosc, poziomJakości);
    }

    @Override
    public ŚcieżkaKariery dajOdpowiadającąŚcieżkęKariery() {
        return new Rzemieślnik();
    }

    /**
     * Zakładam, że zużywam ubrania tak, żeby zużycie dwóch ubrań w obiekcie
     * różniło się maksymalnie o 1
     */
    public void uzyj(int ileUzywam) {
        ileUzytych += ileUzywam;

        int p = dajPoziom().get();
        int srednia = ileUzytych / (p * p);

        if (srednia >= p * p - 1) {
            int ileNiezuzytych = (int) (dajIlosc() * p * p - ileUzytych);
            int ileZuzytych = (int) (dajIlosc() - ileNiezuzytych);

            ustawIlosc(dajIlosc() - ileZuzytych);
            ileUzytych -= ileZuzytych * p * p;
        }
    }

    public int ileMoznaUzyc() {
        return (int) dajIlosc();
    }
}
