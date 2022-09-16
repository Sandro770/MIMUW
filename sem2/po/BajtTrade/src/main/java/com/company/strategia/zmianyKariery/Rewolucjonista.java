package com.company.strategia.zmianyKariery;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;
import com.company.produkt.Produkt;
import com.company.strategia.ścieżkaKariery.ŚcieżkaKariery;

import java.util.List;

import static com.company.strategia.zmianyKariery.StrategiaZmianyŚcieżkiKariery.Typ.REWOLUCJONISTA;
import static java.lang.Math.max;

public class Rewolucjonista extends StrategiaZmianyŚcieżkiKariery {
    public Rewolucjonista() {
        super(REWOLUCJONISTA);
    }

    @Override
    public ŚcieżkaKariery naCoZmiana(Robotnik robotnik, Giełda giełda) {
        return giełda.dajAktualnyDzień() % 7 == 0 ?
                dajZmienionąScieżkę(robotnik, giełda) :
                robotnik.dajAktualnaSciezkeKariery();
    }

    private ŚcieżkaKariery dajZmienionąScieżkę(Robotnik robotnik, Giełda giełda) {
        int n = max(1, robotnik.dajId() % 17);

        Produkt produkt = giełda.dajNajczesciejPojawiajacySieProdukt(n);

        ŚcieżkaKariery ścieżkaKariery =
                produkt.dajOdpowiadającąŚcieżkęKariery();

        return wybierzTęSamąŚcieżkęKariery(ścieżkaKariery,
                robotnik.dajŚcieżkiKariery());
    }

    public static ŚcieżkaKariery wybierzTęSamąŚcieżkęKariery(
            ŚcieżkaKariery ścieżkaKariery, List<ŚcieżkaKariery> ścieżkiKariery) {
        return ścieżkiKariery.stream().filter(x ->
                x.getClass().equals(ścieżkaKariery.getClass())
        ).findFirst()
                .orElse(null);
    }
}
