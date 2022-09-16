package com.company.giełda;

import com.company.agent.Agent;
import com.company.agent.Robotnik;
import com.company.oferta.Oferta;
import com.company.oferta.OfertaRobotnika;
import com.company.oferta.OfertaSpekulanta;
import com.company.produkt.FabrykaProduktów;
import com.company.produkt.Produkt;
import com.company.symulacja.InformacjeOGiełdzie;
import com.company.symulacja.InformacjeOgolne;
import com.company.symulacja.InformacjeStartoweOSymulacji;
import com.squareup.moshi.Json;
import kotlin.jvm.Transient;

import java.util.*;

import static com.company.giełda.Giełda.Transakcja.Typ.ROBOTNIK_KUPUJE;
import static com.company.giełda.Giełda.Transakcja.Typ.ROBOTNIK_SRZEDAJE;
import static com.company.oferta.Oferta.Typ.KUPNA;
import static com.company.oferta.Oferta.Typ.SPRZEDAZY;
import static com.company.produkt.Produkt.Typ.DIAMENT;
import static com.company.produkt.Produkt.Typ.JEDZENIE;
import static java.lang.Math.max;
import static java.lang.Math.min;

public abstract class Giełda {
    transient private ArrayList<OfertaRobotnika> ofertyRobotnikow;
    transient private HashMap<Produkt.Typ, ArrayList<OfertaSpekulanta>> ofertySpekulantow;
    transient private int aktualnyDzien = 0;
    transient private InformacjeOgolne info;
    transient private HashMap<Produkt.Typ, HashMap<Integer,
            ArrayList<Transakcja>>> dokonaneTransakcje;

    public Giełda(InformacjeOgolne infoOgolne) {
        inicjalizujZmienne();
        info = infoOgolne;

        ustawCenyZdniaZero(infoOgolne.ceny);
    }

    private void inicjalizujZmienne() {
        this.ofertyRobotnikow = new ArrayList<>();
        this.ofertySpekulantow = new HashMap<>();
        this.dokonaneTransakcje = new HashMap<>();

        for (Produkt.Typ typ: Produkt.Typ.values())
            if (typ != DIAMENT) {
                ofertySpekulantow.put(typ, new ArrayList<>());
                dokonaneTransakcje.put(typ, new HashMap<>());
            }
    }

    private void ustawCenyZdniaZero(Map<Produkt.Typ, Double> ceny) {
        for (Produkt.Typ typ: ceny.keySet())
            zaksiegujTransakcje(typ, ceny.get(typ), 1, ROBOTNIK_SRZEDAJE);
    }

    public InformacjeOGiełdzie dajOpis() {
        int dzien = dajAktualnyDzień();

        Map<Produkt.Typ, Double> ceny_srednie, ceny_max, ceny_min;

        ceny_srednie = new HashMap<>();
        ceny_max = new HashMap<>();
        ceny_min = new HashMap<>();

        for (Produkt.Typ typ: Produkt.Typ.values())
            if (typ != DIAMENT) {
                ceny_srednie.put(typ, dajSredniaCene(typ));
                ceny_max.put(typ, dajNajwyzszaCene(typ, dajAktualnyDzień()));
                ceny_min.put(typ, dajNajnizszaCene(typ, dajAktualnyDzień()));
            }

        return new InformacjeOGiełdzie(dzien, ceny_srednie,
                ceny_max, ceny_min);
    }

    private Double dajNajwyzszaCene(Produkt.Typ typ, int dzien) {
        List<Transakcja> transakcje = dajDokonaneTransakcje(typ, dzien);

        if (transakcje.isEmpty())
            return dajNajwyzszaCene(typ, 0);

        return transakcje.stream().max(Comparator.comparingDouble(x -> x.cena)).get().cena;
    }

    public enum Typ{
        @Json(name = "socjalistyczna") SOCJALISTYCZNA,
        @Json(name = "kapitalistyczna")KAPITALISTYCZNA,
        @Json(name = "zrownowazona")ZROWNOWAZONA
    }
    Typ typ;

    public class Transakcja {
        public final double cena;
        public final int waga;

        public enum Typ{ROBOTNIK_KUPUJE, ROBOTNIK_SRZEDAJE}
        public final Typ typ;
        public Transakcja(double cena, int waga, Typ typ) {
            this.cena = cena;
            this.waga = waga;
            this.typ = typ;
        }
    }

    public void zaksiegujTransakcje(Produkt.Typ typProduktu, double cena, int waga, Transakcja.Typ typTransakcji) {
        int dzien = dajAktualnyDzień();
        dokonaneTransakcje.get(typProduktu).computeIfAbsent(dzien, k -> new ArrayList<>());

        dokonaneTransakcje.get(typProduktu).get(dzien).add(new Transakcja(cena,
                waga, typTransakcji));
    }

    public int dajKareZaBrakUbran() {
        return info.kara_za_brak_ubran;
    }

    protected void sortujSocjalistycznie() {
        ofertyRobotnikow.sort(new ComparatorSocjalistyczny());
    }

    protected void sortujKapitalistycznie() {
        ofertyRobotnikow.sort(new ComparatorKapitalistyczny());
    }

    public void wykonajDzień() {
        posortujRobotnikow();

        for (OfertaRobotnika ofertaRobotnika: ofertyRobotnikow)
            realizujOferte(ofertaRobotnika);

        skupNiesprzedaneOdRobotnikow();
        wyczyscNiezrealizowane();
    }

    private void realizujOferte(OfertaRobotnika ofertaRobotnika) {
        Produkt.Typ typProduktu = ofertaRobotnika.produkt.dajTyp();
        Oferta.Typ typOfertyRobotnika = ofertaRobotnika.typ;

        List<OfertaSpekulanta> interesujaceOferty =
                ofertySpekulantow.get(typProduktu)
                        .stream()
                        .filter(x -> x.typ != typOfertyRobotnika)
                        .sorted().toList();

        for (OfertaSpekulanta ofertaSpekulanta: interesujaceOferty)
            zrealizujOferty(ofertaRobotnika, ofertaSpekulanta);
    }

    public void nowyDzień(int nrDnia) {
        this.aktualnyDzien++;
    }

    private void wyczyscNiezrealizowane() {
        for (Produkt.Typ typ: Produkt.Typ.values())
            if (typ != DIAMENT)
                this.ofertySpekulantow.get(typ).clear();

        this.ofertyRobotnikow.clear();
    }

    private void skupNiesprzedaneOdRobotnikow() {
        for (OfertaRobotnika ofertaRobotnika: ofertyRobotnikow)
            if (ofertaRobotnika.typ == SPRZEDAZY)
                bankKupujacyOferte(ofertaRobotnika);
    }

    private void bankKupujacyOferte(OfertaRobotnika ofertaRobotnika) {
        double cena = dajNajnizszaCeneKupnaZWczoraj(ofertaRobotnika.produkt.dajTyp());
        double ile = ofertaRobotnika.produkt.dajIlosc();

        Produkt produkt = FabrykaProduktów.dajProdukt(DIAMENT, ile * cena);

        ofertaRobotnika.czyja.dodajZasob(produkt);
    }

    private double dajNajnizszaCeneKupnaZWczoraj(Produkt.Typ typ) {
        return dajNajnizszaCene(typ, aktualnyDzien - 1);
    }

    private double dajNajnizszaCene(Produkt.Typ typ, int dzien) {
        List<Transakcja> transakcje = dajDokonaneTransakcje(typ, dzien);

        if (transakcje.isEmpty())
            return dajNajnizszaCene(typ, 0);

        return transakcje.stream().max((x, y) -> Double.compare(y.cena,
                x.cena)).get().cena;
    }

    protected abstract void posortujRobotnikow();


    public double dajSredniaArytmetycznaSrednichCenJedzenia(int okres) {
        double suma = 0;
        dajSredniaCene(JEDZENIE);

        for (int i = 1; i <= okres; i++)
            suma += dajSredniaCene(JEDZENIE, dajAktualnyDzień() - i);

        return suma / (double) okres;
    }

    private double dajSredniaCene(Produkt.Typ typ, int dzien) {
        if (dzien < 0)
            return dajSredniaCene(typ, 0);

        double suma, ile;

        suma = dajWazonaSumeCen(typ, dzien);
        ile = dajSumeSztukProduktow(typ, dzien);

        if (suma == 0) {
            suma = dajCeneZDniaZerowego(typ, dzien);
            ile = 1;
        }

        return suma / ile;
    }

    private double dajCeneZDniaZerowego(Produkt.Typ typ, int dzien) {
        return dokonaneTransakcje.get(typ).get(0).get(0).cena;
    }

    public int dajAktualnyDzień() {
        return aktualnyDzien;
    }

    public Produkt dajNajczesciejPojawiajacySieProdukt(int ostatnieNDni) {
        int poprzedniIle = 0;
        Produkt.Typ poprzedniTyp = JEDZENIE;

        for (Produkt.Typ typ: Produkt.Typ.values()) {
            if (typ == DIAMENT)
                continue;

            int ileRazyPojawilSie = 0;
            for (int i = 1; i <= ostatnieNDni; i++) {
                int dzien = aktualnyDzien - i;

                ileRazyPojawilSie += dokonaneTransakcje.get(typ).get(dzien).size();
            }

            if (ileRazyPojawilSie >= poprzedniIle) {
                poprzedniIle = ileRazyPojawilSie;
                poprzedniTyp = typ;
            }
        }
        return FabrykaProduktów.dajProdukt(poprzedniTyp, 0);
    }

    public double dajSredniaCene(Produkt.Typ typ) {
        return dajSredniaCene(typ, dajAktualnyDzień());
    }

    public double dajMaksymalnaSredniaCeneWPrzeciaguOstatnichDni(Produkt.Typ typProduktu, int ileOstatniDni) {
        double maksymalna = Double.NEGATIVE_INFINITY;

        for (int i = 1; i <= ileOstatniDni; i++) {
            double kandydat = dajSredniaCene(typProduktu,aktualnyDzien - i);

            maksymalna = max(maksymalna, kandydat);
        }

        return maksymalna;
    }

    public double jakiWzrostCen(Produkt.Typ typProduktu, int ileOstatnichDni) {
        double teraz = dajSredniaCene(typProduktu, dajAktualnyDzień());
        double wtedy = dajSredniaCene(typProduktu,
                dajAktualnyDzień() - ileOstatnichDni);

        return teraz - wtedy;
    }

    public void dodajOferte(Oferta oferta) {
        if (oferta instanceof OfertaRobotnika)
            ofertyRobotnikow.add((OfertaRobotnika) oferta);
        else
            ofertySpekulantow.get(oferta.produkt.dajTyp()).add((OfertaSpekulanta) oferta);
    }

    public double dajSredniaCenePrzezOstatnieDni(Produkt.Typ typ,
                                                 int ileOstatnichDni) {
        double suma = 0;
        double ileSztuk = 0;

        for (int i = 1; i <= ileOstatnichDni; i++) {
            int dzien = aktualnyDzien - i;

            double tmp = dajWazonaSumeCen(typ, dzien);
            if (tmp == 0)
                dzien = 0;

            suma += dajWazonaSumeCen(typ, dzien);
            ileSztuk += dajSumeSztukProduktow(typ, dzien);
        }

        return suma / ileSztuk;
    }

    private double dajSumeSztukProduktow(Produkt.Typ typ, int dzien) {
        if (dzien < 0)
            return dajSumeSztukProduktow(typ, 0);

        ArrayList<Transakcja> cenyDanegoDnia = dokonaneTransakcje.get(typ).get(dzien);

        if (cenyDanegoDnia == null)
            return 0;

        return cenyDanegoDnia.stream()
                .mapToDouble(x -> x.waga)
                .sum();
    }

    private double dajWazonaSumeCen(Produkt.Typ typ, int dzien) {
        ArrayList<Transakcja> cenyDanegoDnia = dajDokonaneTransakcje(typ, dzien);

        double suma = 0;

        if (cenyDanegoDnia == null) {
            if (dzien < 0)
                return dajWazonaSumeCen(typ, 0);
            return 0;
        }

        suma = cenyDanegoDnia
                .stream()
                .mapToDouble(x -> x.cena * x.waga)
                .sum();

        return suma;
    }

    private ArrayList<Transakcja> dajDokonaneTransakcje(Produkt.Typ typ, int dzien) {
        if (dzien < 0)
            return dajDokonaneTransakcje(typ, 0);

        if (dokonaneTransakcje.get(typ).get(dzien) == null)
            return new ArrayList<>();

        return dokonaneTransakcje.get(typ).get(dzien);
    }

    public boolean czyFunkcjaZeSrednichCenScisleWypuklaPrzezOstatnie3Dni(Giełda giełda, Produkt.Typ typ) {
        double[] c = new double[3];

        for (int i = 3; i >= 1; i--)
            c[i - 1] = dajSredniaCene(typ, dajAktualnyDzień() - i);

        return c[1] < (c[0] + c[2]) / 2.0;
    }

    public boolean czyFunkcjaZeSrednichCenScisleWkleeslaPrzezOstatnie3Dni(Giełda giełda, Produkt.Typ typ) {
        double[] c = new double[3];

        for (int i = 3; i >= 1; i--)
            c[i - 1] = dajSredniaCene(typ, dajAktualnyDzień() - i);

        return c[1] > (c[0] + c[2]) / 2.0;
    }

    public boolean czyPierwszyDzien() {
        return aktualnyDzien == 1;
    }

    public int dajLiczbeProduktowRobotnikowWystawionaDoSprzedazy(Produkt.Typ typ, int numerTury) {
        return dajDokonaneTransakcje(typ, numerTury)
                .stream()
                .filter(t -> t.typ == ROBOTNIK_SRZEDAJE)
                .mapToInt(t -> t.waga)
                .sum();
    }

    private void zrealizujOferty(OfertaRobotnika ofertaRobotnika, OfertaSpekulanta ofertaSpekulanta) {
        if (ofertaRobotnika.produkt.dajTyp() != ofertaSpekulanta.produkt.dajTyp())
            throw new IllegalArgumentException();

        Robotnik robotnik = (Robotnik) ofertaRobotnika.czyja;
        int liczbaSztukRobotnika = (int) ofertaRobotnika.produkt.dajIlosc();
        int liczbaSztukSpekulanta = (int) ofertaSpekulanta.produkt.dajIlosc();

        int naIleStac;
        double cena = ofertaSpekulanta.dajCene();

        if (ofertaSpekulanta.typ == KUPNA)
            naIleStac = naIleSztukStac(ofertaSpekulanta.czyja, cena);
        else
            naIleStac = naIleSztukStac(ofertaRobotnika.czyja, cena);

        int liczbaSztukSprzedawanych = min(naIleStac, min(liczbaSztukRobotnika,
                liczbaSztukSpekulanta));

        dokonajTransakcji(ofertaRobotnika, ofertaSpekulanta,
                liczbaSztukSprzedawanych, cena);
    }

    private int naIleSztukStac(Agent czyja, double cena) {
        return (int) Math.floor(czyja.ileDiamentow() / cena);
    }

    private void dokonajTransakcji(OfertaRobotnika ofertaRobotnika, OfertaSpekulanta ofertaSpekulanta, int liczbaSztukWymienianych, double cena) {
        double sumarycznaKwotaTranskacji = liczbaSztukWymienianych * cena;
        
        obsluzDiamenty(ofertaRobotnika, sumarycznaKwotaTranskacji);
        obsluzDiamenty(ofertaSpekulanta, sumarycznaKwotaTranskacji);

        Produkt produktWymienany =
                wyodrebnijIZabierzWymienanyProdukt(ofertaRobotnika,
                        ofertaSpekulanta, liczbaSztukWymienianych);

        dajZasobOdpowiedniejOsobie(ofertaRobotnika, ofertaSpekulanta,
                produktWymienany);

        zaksiegujTransakcje(produktWymienany.dajTyp(), cena,
                (int) produktWymienany.dajIlosc(),
                dajTypTransakcji(ofertaRobotnika));
    }

    private Transakcja.Typ dajTypTransakcji(Oferta oferta) {
        if (oferta.czyja instanceof Robotnik)
            return oferta.typ == KUPNA ? ROBOTNIK_KUPUJE : ROBOTNIK_SRZEDAJE;
        else
            return oferta.typ == KUPNA ? ROBOTNIK_SRZEDAJE : ROBOTNIK_KUPUJE;
    }

    private void dajZasobOdpowiedniejOsobie(OfertaRobotnika ofertaRobotnika, OfertaSpekulanta ofertaSpekulanta, Produkt produktWymienany) {
        if (ofertaRobotnika.typ == KUPNA)
            ofertaRobotnika.czyja.dodajZasob(produktWymienany);
        else
            ofertaRobotnika.czyja.dodajZasob(produktWymienany);
    }

    private Produkt wyodrebnijIZabierzWymienanyProdukt(OfertaRobotnika ofertaRobotnika, OfertaSpekulanta ofertaSpekulanta, int liczbaSztukWymienianych) {
        if (ofertaRobotnika.typ == KUPNA)
            return zabierzProdukt(ofertaSpekulanta, liczbaSztukWymienianych);
        else
            return zabierzProdukt(ofertaRobotnika, liczbaSztukWymienianych);
    }

    private Produkt zabierzProdukt(Oferta oferta,
                                   int liczbaSztukWymienianych) {
        Produkt kopiaProduktu = oferta.produkt.skopiuj();

        kopiaProduktu.ustawIlosc(liczbaSztukWymienianych);
        oferta.produkt.ustawIlosc(oferta.produkt.dajIlosc() - liczbaSztukWymienianych);

        return kopiaProduktu;
    }

    private void obsluzDiamenty(Oferta oferta,
                                double sumarycznaKwotaTranskacji) {
        if (oferta.typ == KUPNA) {
            oferta.czyja.pobierzDiamenty(DIAMENT, sumarycznaKwotaTranskacji);
        }
        else {
            Produkt d = FabrykaProduktów.dajProdukt(DIAMENT,
                    sumarycznaKwotaTranskacji);

            oferta.czyja.dodajZasob(d);
        }
    }
}
