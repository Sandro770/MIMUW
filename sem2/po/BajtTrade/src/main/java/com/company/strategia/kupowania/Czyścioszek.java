package com.company.strategia.kupowania;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;

import static com.company.produkt.Produkt.Typ.UBRANIA;

public class Czyścioszek extends StrategiaKupowaniaIStosowaniaProgramów {

    protected static void zadbajOUbrania(Robotnik robotnik, Giełda giełda) {
        int ileZuzyje = robotnik.ileZuzyjeUbran();
        kupTyleZebyWystarczylo(UBRANIA, 100 + ileZuzyje, robotnik, giełda);
    }

    @Override
    protected void wystawOfertęKupnaBezJedzenia(Robotnik robotnik, Giełda giełda) {
        Czyścioszek.zadbajOUbrania(robotnik, giełda);
    }

}
