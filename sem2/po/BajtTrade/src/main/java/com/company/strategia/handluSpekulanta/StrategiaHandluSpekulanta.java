package com.company.strategia.handluSpekulanta;

import com.company.agent.Spekulant;
import com.company.giełda.Giełda;
import com.company.produkt.Produkt;
import com.company.strategia.Strategia;

public abstract class StrategiaHandluSpekulanta extends Strategia {
    public abstract void wejdźZOfertami(Giełda giełda, Produkt.Typ typ, Spekulant spekulant);
}
