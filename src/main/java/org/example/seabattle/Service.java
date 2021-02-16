package org.example.seabattle;

import java.util.LinkedList;
import java.util.List;

public class Service {
    List<Sector> backupList = new LinkedList<>();

    void printout(List<Sector> sectorList, String header) {
        System.out.println("list title: " + header);
        for (Sector sector : sectorList) {
            int x = sector.getCoordinateX();
            int y = sector.getCoordinateY();
            char a = (char) (y + 65);
            int p = sector.getPlayer();
            String n = sector.getTakenBy();
            System.out.print(" [" + a  + (x + 1) + ",p=" + p +"," + n + "] ");
        }
        System.out.println("\nlist contains " + sectorList.size() + " items.\n");
    }

}
