package com.cyberfend.cyfsecurity;

import java.util.ArrayList;
import java.util.Scanner;
import android.util.Pair;

public class SensorDataBuilder {

    public native synchronized void initializeKeyN();
    public native synchronized String buildN(ArrayList<Pair<String, String>> arrayList);
    public native synchronized void decryptN();
    public native synchronized void encryptKeyN();

    static {
        try {
            System.load("/data/local/tmp/reverse/libakamaibmp.so");
            System.out.println("library loaded");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void main(String[] args) {

        SensorDataBuilder l = new SensorDataBuilder();
	ArrayList<Pair<String,String>> list = new ArrayList<>();

	list.add(new Pair<>("device", "android"));
	list.add(new Pair<>("version", "14"));
	list.add(new Pair<>("model", "pixel"));
	list.add(new Pair<>("os", "linux"));
	list.add(new Pair<>("sensor", "gyro"));

        Scanner sc = new Scanner(System.in);


            l.initializeKeyN();
	    while (true) {
            String input = sc.nextLine();
        if (input.equals("a")) {
            System.out.println("calling initializeKeyN()");
	    System.out.println("Calling buildN");
	    String m = l.buildN(list);
	    System.out.println(m);
        }
}
    }
}
