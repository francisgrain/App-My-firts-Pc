package com.example.demo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SessionAttributes("carrello")

@Controller
public class MyController {
	
    private final ComputerJDBCTemp computerJDBCTemp;
    private final pcOrdJDBCTemp pcOrdJDBCTemp;
    
    @Autowired
    private EmailService emailService;

    @Autowired
    public MyController(ComputerJDBCTemp computerJDBCTemp, pcOrdJDBCTemp pcOrdJDBCTemp) {
        this.computerJDBCTemp = computerJDBCTemp;
        this.pcOrdJDBCTemp = pcOrdJDBCTemp;
        //this.emailService = emailService;
    }

    @GetMapping("/")
    public String getComputer() {
        return "magazzino";
    }

    @GetMapping("/getComputer")
    public String getAll(Model model) {
        ArrayList<computer> lista = computerJDBCTemp.ritornaComputer();
        model.addAttribute("lista", lista);
        return "listaComputer";
    }

    @PostMapping("/addComputer")
    public String addComputer(@RequestParam("marca") String marca, @RequestParam("tipologia") String tipologia,
                              @RequestParam("modello") String modello, @RequestParam("descrizione") String descrizione, @RequestParam("qnt") String qnt,
                              @RequestParam("url") String url, @RequestParam("prezzo") String prezzo, Model model) {
        String[] ImgUrl = url.split("\"");
        String urlImage = ImgUrl[3];
        
        computer pc1 = new computer();
        pc1.setId(0);
        pc1.setMarca(marca);
        pc1.setTipologia(tipologia);
        pc1.setModello(modello);
        pc1.setDescrizione(descrizione);
        pc1.setQnt(Integer.parseInt(qnt));
        pc1.setUrl(urlImage);
        pc1.setPrezzo(Double.parseDouble(prezzo));

        model.addAttribute("computer", pc1);
        computerJDBCTemp.insertComputer(marca, tipologia, modello, descrizione, Integer.parseInt(qnt), urlImage, Double.parseDouble(prezzo));
        return "insComputer";
    }

    @GetMapping("/listaMagazzino")
    public String getStore(Model model) {
        ArrayList<computer> lista = computerJDBCTemp.ritornaComputer();
        model.addAttribute("lista", lista);
        return "listaMagazzino";
    }

    //sezione per modificare gli articoli in magazzino
    @GetMapping("/change")
    public String change(Model model) {
        ArrayList<computer> lista = computerJDBCTemp.ritornaComputer();
        model.addAttribute("lista", lista);
        return "change";
    }

    //modifica delle quantità in magazzino e dei prezzi degli articoli
    @PostMapping("/changeP")
    public String changeP(@RequestParam("computer") String[] ordini,
                          @RequestParam("pezzi") String[] pezzi,
                          @RequestParam("prezzo") String[] prezzi,
                          Model model) {
        ArrayList<Integer> pcIds = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();
        ArrayList<Double> newPrices = new ArrayList<>();

        for (String s : ordini) {
            if (!s.isEmpty()) {
                pcIds.add(Integer.parseInt(s));
            }
        }

        for (String s : pezzi) {
            if (!s.isEmpty()) {
                quantities.add(Integer.parseInt(s));
            }
        }

        for (String s : prezzi) {
            if (!s.isEmpty()) {
                newPrices.add(Double.parseDouble(s));
            }
        }

        for (int i = 0; i < pcIds.size(); i++) {
            if (i < quantities.size()) {
                computerJDBCTemp.updatePezzi(quantities.get(i), pcIds.get(i));
            }
            if (i < newPrices.size()) {
                computerJDBCTemp.updatePrezzo(newPrices.get(i), pcIds.get(i));
            }
            
            
        }

        model.addAttribute("message", "Modifica avvenuta con successo");
        return "successPage";
    }
    
    //eliminazione degli elementi dal magazzino
    @PostMapping("/changeD")
    public String changeD(@RequestParam("computer") String[] ordini,
                          @RequestParam("pezzi") String[] pezzi,
                          Model model) {
        ArrayList<Integer> pcIds = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();

        for (String s : ordini) {
            if (!s.isEmpty()) {
                pcIds.add(Integer.parseInt(s));
            }
        }

        for (String s : pezzi) {
            if (!s.isEmpty()) {
                quantities.add(Integer.parseInt(s));
            }
        }
       
        for (int i = 0; i < pcIds.size(); i++) {
            if (i < quantities.size()) {
                computerJDBCTemp.deleteComputer(quantities.get(i), pcIds.get(i));
            }
        }

        model.addAttribute("message", "Eliminazione avvenuta con successo");
        return "successPage";
    }
    
    //store lato utente
    @GetMapping("/store")
    public String getStore1(Model model) {
        ArrayList<computer> lista = computerJDBCTemp.ritornaComputer();
        model.addAttribute("lista", lista);
        return "store";
    }
    
    //metodo per acquistare
 // Metodo per inizializzare il carrello nella sessione se non esiste
    @ModelAttribute("carrello")
    public List<pcOrd> createCarrello() {
        return new ArrayList<>();
    }

    @PostMapping("/buyPc")
    public String buyPc(@RequestParam("ordini") int ordine,
                        @RequestParam("quantities") int quantita, 
                        @ModelAttribute("carrello") List<pcOrd> carrello, 
                        Model model) {
        ArrayList<computer> lista = computerJDBCTemp.ritornaComputer();
        double prezzoTotale = 0;

        // Trova il computer selezionato dalla lista
        for (computer comp : lista) {
            if (comp.getId() == ordine) {
                boolean found = false;

                // Controlla se il prodotto è già nel carrello
                for (pcOrd pc : carrello) {
                    if (pc.getModello().equals(comp.getModello())) {
                        pc.setQnt(pc.getQnt() + quantita); // Aggiungi quantità se esiste già
                        found = true;
                        break;
                    }
                }

                // Se il prodotto non è già nel carrello, aggiungilo
                if (!found) {
                    pcOrd newPc = new pcOrd();
                    newPc.setModello(comp.getModello());
                    newPc.setQnt(quantita);
                    carrello.add(newPc);
                }
            }
        }

        // Calcola il prezzo totale
        for (pcOrd pc : carrello) {
            for (computer comp : lista) {
                if (comp.getModello().equals(pc.getModello())) {
                    prezzoTotale += comp.getPrezzo() * pc.getQnt();
                }
            }
        }

        model.addAttribute("lista", carrello);
        model.addAttribute("prezzo", prezzoTotale);

        return "carrello";
    }



    @PostMapping("/confPc")
    public ResponseEntity<String> confermaPc(
        @RequestParam("modello") String[] ordini,
        @RequestParam("qnt") String[] pezzi,
        @RequestParam("prezzo") String prezzo,
        @RequestParam("email") String email,
        @ModelAttribute("carrello") List<pcOrd> carrello,
        Model model) {

        ArrayList<pcOrd> pc = new ArrayList<>();
        ArrayList<Integer> qnt = new ArrayList<>();
        ArrayList<computer> lista = computerJDBCTemp.ritornaComputer();

        for (String s : pezzi) {
            if (!s.isEmpty()) {
                int x = Integer.parseInt(s);
                qnt.add(x);
            }
        }

        Set<String> modelliAggiunti = new HashSet<>();

        for (int j = 0; j < ordini.length; j++) {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getModello().equals(ordini[j])) {
                    if (!modelliAggiunti.contains(ordini[j])) {
                        modelliAggiunti.add(ordini[j]);
                        pcOrd computer = new pcOrd();
                        computer.setModello(lista.get(i).getModello());
                        computer.setQnt(qnt.get(j));
                        computer.setPrezzo(lista.get(i).getPrezzo());
                        pc.add(computer);

                        // Riduzione della quantità in magazzino al momento della conferma
                        int nuovaQuantita = lista.get(i).getQnt() - qnt.get(j);
                        computerJDBCTemp.updateCarrello(nuovaQuantita, lista.get(i).getId());
                }
            }
        }

        double prezzoTotale = 0;
        for (pcOrd comp : pc) {
        	//System.out.println(comp.getModello());
        	//System.out.println(comp.getQnt());
        	pcOrdJDBCTemp.updatePezzi(comp.getQnt(), comp.getModello());
            prezzoTotale += comp.getPrezzo() * comp.getQnt();

        }
        
        String to = email;
        String subject = "Ordine da My First Pc confermato";
        StringBuilder text = new StringBuilder("Hai acquistato: \n");

        for (pcOrd comp : pc) {
        	
            text.append(comp.getModello()).append("\n");
            text.append("Pezzi :").append(comp.getQnt()).append("\n");
        }

        text.append("Il prezzo totale da pagare è ").append(prezzoTotale).append(" Euro");
        emailService.sendSimpleEmail(to, subject, text.toString());

        // Pulisci il carrello dopo la conferma dell'ordine
        carrello.clear();

        }         return ResponseEntity.ok("Email inviata con successo, grazie per l'acquisto, alla prossima");

    }
    
//metodo per la creazione della pagina statistiche di vendita
    @GetMapping("/showPcOrders")
    public String showPcOrders(Model model) {
        ArrayList<pcOrd> pcOrders = new ArrayList<>();
        pcOrders = pcOrdJDBCTemp.ritornaOrdPc();
        
        // Convertiamo la lista in una stringa JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String pcOrdersJson = "";
    	try {
			pcOrdersJson = objectMapper.writeValueAsString(pcOrders);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       

        // Passiamo la stringa JSON al modello
        model.addAttribute("pcOrdersJson", pcOrdersJson);
        
        model.addAttribute("pcOrders", pcOrders);
        return "pcOrders";
    }
	
	 
	 //metodo per invio mail
	@GetMapping("/formEmail")
	public String formEmail(){
		
		return "formEmail";
	}
	
	
   @PostMapping("sendEmail")
   public ResponseEntity<String> sendEmail(@RequestParam("to") String to, @RequestParam("subject") String subject, @RequestParam("text") String text) {
       emailService.sendSimpleEmail(to, subject, text);
       return ResponseEntity.ok("Email sent successfully");
   }
}
