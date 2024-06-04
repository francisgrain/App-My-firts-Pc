package com.example.demo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class ComputerJDBCTemp {
	private JdbcTemplate jdbcTemplateObject;

	@Autowired
	public void setJdbcTemplateObject(JdbcTemplate jdbcTemplateObject) {
		this.jdbcTemplateObject = jdbcTemplateObject;
	}

	//aggiunge un nuovo articolo al magazzino
	public int insertComputer(String marca, String tipologia, String modello, String descrizione, int qnt, String url, double prezzo) {
		String query = "INSERT INTO magazzino (marca, tipologia, modello, descrizione, qnt, url, prezzo) VALUES (?, ?, ?, ?, ?, ?, ?)";
		return jdbcTemplateObject.update(query, marca, tipologia, modello, descrizione, qnt, url, prezzo);
	}

	public ArrayList<computer> ritornaComputer() {
		String query = "SELECT * FROM magazzino";
		return jdbcTemplateObject.query(query, new ResultSetExtractor<ArrayList<computer>>() {
			@Override
			public ArrayList<computer> extractData(ResultSet rs) throws SQLException {
				ArrayList<computer> listaComputer = new ArrayList<>();
				while (rs.next()) {
					computer computer = new computer();
					computer.setId(rs.getInt("id"));
					computer.setMarca(rs.getString("marca"));
					computer.setTipologia(rs.getString("tipologia"));
					computer.setModello(rs.getString("modello"));
					computer.setDescrizione(rs.getString("descrizione"));
					computer.setQnt(rs.getInt("qnt"));
					computer.setUrl(rs.getString("url"));
					computer.setPrezzo(rs.getDouble("prezzo"));

					listaComputer.add(computer);
				}
				return listaComputer;
			}
		});
	}
	
	//aggiorna quantità in magazzino
    public int updatePezzi(int pezzi, int id) {
        String query = "UPDATE magazzino SET qnt = qnt + ? WHERE id = ?";
        return jdbcTemplateObject.update(query, pezzi, id);
}
    //aggiorna prezzi in magazzino
    public int updatePrezzo(double prezzo, int id) {
        String query = "UPDATE magazzino SET prezzo = ? WHERE id = ?";
        return jdbcTemplateObject.update(query, prezzo, id);
}
    //metodo per cancellare un articolo
    public int deleteComputer(int pezzi, int id) {
        // Controlla prima la quantità attuale del computer
        String queryCheck = "SELECT qnt FROM magazzino WHERE id = ?";
        Integer currentQuantity = jdbcTemplateObject.queryForObject(queryCheck, new Object[]{id}, Integer.class);
        
        if (currentQuantity != null && currentQuantity > pezzi) {
            // Se la quantità attuale è maggiore della quantità da eliminare, riduci la quantità
            String queryUpdate = "UPDATE magazzino SET qnt = qnt - ? WHERE id = ?";
            return jdbcTemplateObject.update(queryUpdate, pezzi, id);
        } else if (currentQuantity != null && currentQuantity == pezzi) {
            // Se la quantità attuale è uguale alla quantità da eliminare, elimina il record
            String queryDelete = "DELETE FROM magazzino WHERE id = ?";
            return jdbcTemplateObject.update(queryDelete, id);
        } else {
            // Gestisci il caso in cui la quantità da eliminare è maggiore della quantità attuale (potrebbe essere un errore)
            return 0;
        }
    }
    	// toglie pezzi dal magazzino dopo l'acquisto
    	public int updateCarrello(int pezzi, int id) {
        String query = "UPDATE magazzino SET qnt = qnt - ? WHERE id = ?";
        return jdbcTemplateObject.update(query, pezzi, id);
}
    
}