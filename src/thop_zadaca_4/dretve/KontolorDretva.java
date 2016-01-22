/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thop_zadaca_4.dretve;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import thop_zadaca_4.GeneriranjeSvihVrijednosti;
import thop_zadaca_4.aplikacija.ParkingApplication;
import thop_zadaca_4.podaci.Automobil;
import thop_zadaca_4.podaci.PodaciOAutomobilima;

/**
 *
 * @author Tomislav
 */
public class KontolorDretva extends Thread {

    private GeneriranjeSvihVrijednosti gsv;
    private List<Integer> argumenti;

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        while (true) {
            //System.err.println("K");
            try {
                float rand1 = gsv.vrijemeRazmakaKontrolora();
                int random1 = (int) (rand1 * 1000);
                sleep(random1);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                break;
            }
            dolazakKontrolora();
        }
    }

    private void dolazakKontrolora() {
        if (baremJedanAutoParkiran()) {
            for (Automobil auto : ParkingApplication.auti) {

                if (auto.isNaParkiralistu()) {
                    boolean provjera = provjeraParkiranja(auto.getVrijemeParkiranja(), auto.getNaKolikoSeParkira(), auto.getAutomobilID());
                    if (provjera) {
                        Timestamp vrijeme = new Timestamp(System.currentTimeMillis());
                        PodaciOAutomobilima poa = new PodaciOAutomobilima(auto, auto.getZona().getBrojZone(), 0,vrijeme, "Parkiranje važeće", "K");
                        //poa.datumIVrijeme(System.currentTimeMillis());
                        poa.ispisZapisaDnevnika();
                        ParkingApplication.dnevnik.add(poa);
                        
                        ParkingApplication.auti.remove(auto);
                        ParkingApplication.auti.add(auto);
                        return;
                        //return;
                    } else {
                        
                        //formuli ((brojZona + 1 - i) * cijenaJedinice * kaznaParkiranja), a pauk odvozi automobil na deponij.
                        float cijenaKazne = (float)((float)argumenti.get(1) + 1 - auto.getZona().getBrojZone())*(float)argumenti.get(7)*(float)argumenti.get(9);
                        //System.err.println("CIJENA KAZNE: " + cijenaKazne);
                        Timestamp vrijeme = new Timestamp(System.currentTimeMillis());
                        PodaciOAutomobilima poa = new PodaciOAutomobilima(auto, auto.getZona().getBrojZone(), cijenaKazne,vrijeme, "Pauk odvozi auto", "K");
                        ParkingApplication.zone.get(auto.getZona().getBrojZone()-1).dodajKaznu(cijenaKazne);
                        ParkingApplication.zone.get(auto.getZona().getBrojZone()-1).ukloniAutoIzZone();
                        ParkingApplication.zone.get(auto.getZona().getBrojZone()-1).autoIdeNaDeponij();
                        //poa.datumIVrijeme(System.currentTimeMillis());
                        poa.ispisZapisaDnevnika();
                        ParkingApplication.dnevnik.add(poa);
                        auto.setNaParkiralistu(false);
                        ParkingApplication.auti.remove(auto);
                        return;
                    }
                }
            }
        }

    }

    private boolean provjeraParkiranja(Timestamp kadaJeParkiran, int naKolikoJeParkiran, int id) {
        //System.err.println("AutoID " + id + "\tKada je parkiran: " + kadaJeParkiran + "\tNa koliko: " + naKolikoJeParkiran);
        
        
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(kadaJeParkiran.getTime());
        cal.add(Calendar.SECOND, naKolikoJeParkiran);
        Timestamp doKadaJeParkiran = new Timestamp(cal.getTimeInMillis());
        Timestamp trenutnoVrijeme = new Timestamp(System.currentTimeMillis());
        
        //System.err.println("K\t AutoID: " + id + "\tTrenutno vrijeme: " + trenutnoVrijeme + "\tParkiran do: " + doKadaJeParkiran + "\tDodano: " + naKolikoJeParkiran + " sec" +"\tStaro vrijeme: " + kadaJeParkiran);
        if(trenutnoVrijeme.after(doKadaJeParkiran))
        {
            return false;
        }
        return true;
    }

    private boolean baremJedanAutoParkiran() {
        for (Automobil auto : ParkingApplication.auti) {
            if (auto.isNaParkiralistu() == true) {
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    public void setGsv(GeneriranjeSvihVrijednosti gsv) {
        this.gsv = gsv;
    }

    public void setArgumenti(List<Integer> argumenti) {
        this.argumenti = argumenti;
    }
}
