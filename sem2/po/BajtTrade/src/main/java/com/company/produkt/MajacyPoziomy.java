package com.company.produkt;

import java.util.Optional;

public abstract class MajacyPoziomy extends Produkt{
    protected MajacyPoziomy(Typ typ, double ile, Optional<Integer> poziom) {
        super(typ, ile);

        this.poziom = poziom;
    }
}
