package com.company.strategia.coRobi;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;

public class Student extends StrategiaCoRobiDanegoDnia {
    private int zapas, okres;

    @Override
    public boolean czyUczeSie(Robotnik robotnik, Giełda giełda) {
        return robotnik.ileDiamentow() >= 100 * zapas *
                giełda.dajSredniaArytmetycznaSrednichCenJedzenia(okres);
    }
}
