package com.company.strategia.coRobi;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;

public class Okresowy extends StrategiaCoRobiDanegoDnia {
    private int okresowosc_nauki;

    @Override
    public boolean czyUczeSie(Robotnik robotnik, Giełda giełda) {
        return giełda.dajAktualnyDzień() % okresowosc_nauki == 0;
    }
}
