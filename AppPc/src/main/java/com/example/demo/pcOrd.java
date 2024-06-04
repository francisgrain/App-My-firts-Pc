package com.example.demo;

public class pcOrd {
	int id;
    private String modello;
    private int qnt;
    private double prezzo;  // Assicurati che questa proprietà esista

 // Nel modello pcOrd
    public String getFormattedPrezzoUnitario() {
        // Formatta il prezzo come stringa con due decimali
        return String.format("%.2f", prezzo);
    }

    public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	
    
    // Getters and setters per tutte le proprietà
    public String getModello() {
        return modello;
    }

    public void setModello(String modello) {
        this.modello = modello;
    }

    public int getQnt() {
        return qnt;
    }

    public void setQnt(int qnt) {
        this.qnt = qnt;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }
}
