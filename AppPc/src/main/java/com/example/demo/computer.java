package com.example.demo;

import java.text.DecimalFormat;

public class computer {

	private int id;
	private String marca;
	private String tipologia;
	private String modello;
	private String descrizione;
	private int qnt;
	private String url;
	private double prezzo;

	public String getFormattedPrezzo() {
		DecimalFormat df = new DecimalFormat("#,##0.00");
		return df.format(prezzo) + " Euro";
	}
	
	public String getFormattedQnt() {
		DecimalFormat dq = new DecimalFormat("#,##0");
		return dq.format(qnt);
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public String getTipologia() {
		return tipologia;
	}
	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}
	public String getModello() {
		return modello;
	}
	public void setModello(String modello) {
		this.modello = modello;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public int getQnt() {
		return qnt;
	}
	public void setQnt(int qnt) {
		this.qnt = qnt;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public double getPrezzo() {
		return prezzo;
	}
	public void setPrezzo(double prezzo) {
		this.prezzo = prezzo;
	}
	@Override
	public String toString() {
		return "computer [id=" + id + ", marca=" + marca + ", tipologia=" + tipologia + ", modello=" + modello
				+ ", descrizione=" + descrizione + ", qnt=" + qnt + ", url=" + url + ", prezzo=" + prezzo + "]";
	}
}
