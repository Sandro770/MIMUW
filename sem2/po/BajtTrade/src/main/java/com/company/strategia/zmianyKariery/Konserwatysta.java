package com.company.strategia.zmianyKariery;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;
import com.company.strategia.ścieżkaKariery.ŚcieżkaKariery;

import static com.company.strategia.zmianyKariery.StrategiaZmianyŚcieżkiKariery.Typ.KONSERWATYSTA;

public class Konserwatysta extends StrategiaZmianyŚcieżkiKariery {
    public Konserwatysta() {
        super(KONSERWATYSTA);
    }

    @Override
    public ŚcieżkaKariery naCoZmiana(Robotnik robotnik, Giełda giełda) {
        return robotnik.dajAktualnaSciezkeKariery();
    }
}
