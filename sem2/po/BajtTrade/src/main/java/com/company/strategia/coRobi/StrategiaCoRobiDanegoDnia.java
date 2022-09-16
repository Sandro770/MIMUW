package com.company.strategia.coRobi;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;
import com.company.strategia.Strategia;

public abstract class StrategiaCoRobiDanegoDnia extends Strategia {

    public abstract boolean czyUczeSie(Robotnik robotnik, Giełda giełda);
}
