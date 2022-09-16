package com.company.strategia.coRobi;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;

import java.util.Random;

public class Rozkładowy extends StrategiaCoRobiDanegoDnia {
    @Override
    public boolean czyUczeSie(Robotnik robotnik, Giełda giełda) {
        double prawdopodobienstwoNauki =
                1.0 / ((double)giełda.dajAktualnyDzień() + 3.0);

        double x = new Random().nextDouble(0, 1);

        return x <= prawdopodobienstwoNauki;
    }
}
