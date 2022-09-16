package com.company.giełda;

import com.company.symulacja.InformacjeOgolne;

public class Zrównoważona extends Giełda {
    public Zrównoważona(InformacjeOgolne infoOgolne) {
        super(infoOgolne);
    }

    @Override
    protected void posortujRobotnikow() {
        if (dajAktualnyDzień() % 2 == 1)
            sortujSocjalistycznie();
        else
            sortujKapitalistycznie();
    }
}
