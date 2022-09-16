package com.company.strategia.coRobi;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;

public class Pracuś extends StrategiaCoRobiDanegoDnia {
    @Override
    public boolean czyUczeSie(Robotnik robotnik, Giełda giełda) {
        return false;
    }
}
