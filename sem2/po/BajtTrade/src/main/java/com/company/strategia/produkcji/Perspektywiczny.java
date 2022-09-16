package com.company.strategia.produkcji;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;
import com.company.produkt.Produkt;

public class Perspektywiczny extends StrategiaProdukcji {
    private final int historia_perspektywy;

    public Perspektywiczny(int historia_pespektywy) {
        this.historia_perspektywy = historia_pespektywy;
    }

    @Override
    public double jakaWagaDlaDanegoProduktu(Produkt.Typ typProduktu, Giełda giełda, Robotnik robotnik) {
        return giełda.jakiWzrostCen(typProduktu, historia_perspektywy);
    }
}
