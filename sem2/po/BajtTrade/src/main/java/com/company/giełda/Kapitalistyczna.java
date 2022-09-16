package com.company.giełda;

import com.company.symulacja.InformacjeOgolne;

public class Kapitalistyczna extends Giełda {

    public Kapitalistyczna(InformacjeOgolne infoOgolne) {
        super(infoOgolne);
    }

    @Override
    protected void posortujRobotnikow() {
        sortujKapitalistycznie();
    }
}
