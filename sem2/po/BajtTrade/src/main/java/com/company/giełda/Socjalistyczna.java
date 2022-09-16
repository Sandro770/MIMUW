package com.company.giełda;

import com.company.strategia.Strategia;
import com.company.symulacja.InformacjeOgolne;

public class Socjalistyczna extends Giełda {

    public Socjalistyczna(InformacjeOgolne infoOgolne) {
        super(infoOgolne);
    }

    @Override
    protected void posortujRobotnikow() {
        sortujSocjalistycznie();
    }
}
