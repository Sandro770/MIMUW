package com.company.strategia.coRobi;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;

public class Oszczędny extends StrategiaCoRobiDanegoDnia {
    private int limit_diamentow;

    @Override
    public boolean czyUczeSie(Robotnik robotnik, Giełda giełda) {
        return robotnik.ileDiamentow() > limit_diamentow;
    }
}
